/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.internal.protocol;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.openhab.binding.nibeheatpump.internal.NibeHeatPumpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connector for serial port communication.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class NibeHeatPumpSerialConnector extends NibeHeatPumpConnector {

	private static final Logger logger = LoggerFactory
			.getLogger(NibeHeatPumpSerialConnector.class);

	private List<NibeHeatPumpEventListener> _listeners = new ArrayList<NibeHeatPumpEventListener>();

	private InputStream in = null;
	private OutputStream out = null;
	private SerialPort serialPort = null;
	private Thread readerThread = null;
	private String portName = null;

	private List<byte[]> readQueue = new ArrayList<byte[]>();
	private List<byte[]> writeQueue = new ArrayList<byte[]>();
	
	// state machine states
	private enum StateMachine {
		WAIT_START, 
		WAIT_DATA, 
		OK_MESSAGE_RECEIVED, 
		WRITE_TOKEN_RECEIVED,
		READ_TOKEN_RECEIVED,
		CRC_FAILURE,
	};

	public NibeHeatPumpSerialConnector(String portName) {

		logger.debug("Nibe heatpump Serial Port message listener started");
		this.portName = portName;
	}

	public synchronized void addEventListener(NibeHeatPumpEventListener listener) {
		if (!_listeners.contains(listener)) {
			_listeners.add(listener);
		}
	}

	public synchronized void removeEventListener(
			NibeHeatPumpEventListener listener) {
		_listeners.remove(listener);
	}

	@Override
	public void connect() throws NibeHeatPumpException {

		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);

			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					2000);

			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(38400, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			serialPort.enableReceiveThreshold(1);
			serialPort.disableReceiveTimeout();

			in = serialPort.getInputStream();
			out = serialPort.getOutputStream();

			out.flush();
			if (in.markSupported()) {
				in.reset();
			}
		} catch (NoSuchPortException | PortInUseException
				| UnsupportedCommOperationException | IOException e) {
			throw new NibeHeatPumpException(e);
		}

		readQueue.clear();
		writeQueue.clear();
		
		readerThread = new SerialReader(in);
		readerThread.start();
	}

	@Override
	public void disconnect() throws NibeHeatPumpException {

		logger.debug("Disconnecting");

		if (readerThread != null) {
			logger.debug("Interrupt serial listener");
			readerThread.interrupt();
		}

		if (out != null) {
			logger.debug("Close serial out stream");
			IOUtils.closeQuietly(out);
		}
		if (in != null) {
			logger.debug("Close serial in stream");
			IOUtils.closeQuietly(in);
		}

		if (serialPort != null) {
			logger.debug("Close serial port");
			serialPort.close();
		}

		readerThread = null;
		serialPort = null;
		out = null;
		in = null;

		logger.debug("Closed");
	}

	@Override
	public void sendDatagram(byte[] data) throws NibeHeatPumpException {
		
		//TODO: limit queues?
		if (NibeHeatPumpProtocol.isModbus40WriteRequestPdu(data)) {
			writeQueue.add(data);
		} else if (NibeHeatPumpProtocol.isModbus40ReadRequestPdu(data)) {
			readQueue.add(data);
		} else {
			logger.debug("Ignore unknown PDU");
		}
	}

	public class SerialReader extends Thread implements SerialPortEventListener {
		boolean interrupted = false;
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		@Override
		public void interrupt() {
			interrupted = true;
			super.interrupt();
			IOUtils.closeQuietly(in);
		}

		public void run() {
			logger.debug("Data listener started");

			// RXTX serial port library causes high CPU load
			// Start event listener, which will just sleep and slow down event
			// loop
			try {
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
			} catch (TooManyListenersException e) {
			}

			try {
				final int dataBufferMaxLen = 100;
				byte[] dataBuffer = new byte[dataBufferMaxLen];
				int index = 0;
				StateMachine state = StateMachine.WAIT_START;
				int b;

				while (interrupted != true) {

					switch (state) {

					case WAIT_START:
						if ((b = in.read()) > -1) {
							logger.trace("Received byte: {}", b);
							if (b == NibeHeatPumpProtocol.FRAME_START_CHAR) {
								logger.trace("Frame start found");
								dataBuffer[0] = NibeHeatPumpProtocol.FRAME_START_CHAR;
								index = 1;
								state = StateMachine.WAIT_DATA;
							}
						}
						break;

					case WAIT_DATA:
						if ((b = in.read()) > -1) {
							if (index >= dataBufferMaxLen) {
								logger.trace("Too long message received");
								state = StateMachine.WAIT_START;
							} else {
								dataBuffer[index++] = (byte) b;

								int msglen = checkNibeMessage(dataBuffer, index);

								switch (msglen) {
								case 0: // Ok, but not ready
									break;
								case -1: // Invalid message
									state = StateMachine.WAIT_START;
									break;
								case -2: // Checksum error
									state = StateMachine.CRC_FAILURE;
									break;
								default:
									state = StateMachine.OK_MESSAGE_RECEIVED;
									break;
								}
							}
						}
						break;
						
					case CRC_FAILURE:
						logger.trace("CRC failure");
						try {
							sendNakToNibe();
						} catch (IOException e) {
							sendErrorToListeners(e.getMessage());
						}
						state = StateMachine.WAIT_START;
						break;
						
					case OK_MESSAGE_RECEIVED:
						logger.trace("OK message received");
						if (NibeHeatPumpProtocol
								.isModbus40ReadTokenPdu(dataBuffer)) {
							state = StateMachine.READ_TOKEN_RECEIVED;
						}
						if (NibeHeatPumpProtocol
								.isModbus40WriteTokenPdu(dataBuffer)) {
							state = StateMachine.WRITE_TOKEN_RECEIVED;
						} else {
							try {
								sendAckToNibe();
							} catch (IOException e) {
								sendErrorToListeners(e.getMessage());
							}
							sendMsgToListeners(Arrays.copyOfRange(dataBuffer,
									0, index));
							state = StateMachine.WAIT_START;
						}
						break;
						
					case READ_TOKEN_RECEIVED:
						logger.trace("Read token received");
						try {
							if (!readQueue.isEmpty()) {
								byte[] data = readQueue.remove(0);
								sendDataToNibe(data);
							} else {
								sendAckToNibe();
							}
						} catch (IOException e) {
							sendErrorToListeners(e.getMessage());
						}
						state = StateMachine.WAIT_START;
						break;
					
					case WRITE_TOKEN_RECEIVED:
						logger.trace("Write token received");
						try {
							if (!writeQueue.isEmpty()) {
								byte[] data = writeQueue.remove(0);
								sendDataToNibe(data);
							} else {
								sendAckToNibe();
							}
						} catch (IOException e) {
							sendErrorToListeners(e.getMessage());
						}
						state = StateMachine.WAIT_START;
						break;
					}
				}
			} catch (InterruptedIOException e) {
				Thread.currentThread().interrupt();
				logger.error("Interrupted via InterruptedIOException");
			} catch (IOException e) {
				logger.error("Reading from serial port failed", e);
				sendErrorToListeners(e.getMessage());
			}

			serialPort.removeEventListener();
			logger.debug("Data listener stopped");
		}

		@Override
		public void serialEvent(SerialPortEvent arg0) {
			try {
				/*
				 * See more details from
				 * https://github.com/NeuronRobotics/nrjavaserial/issues/22
				 */
				logger.trace("RXTX library CPU load workaround, sleep forever");
				sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
			}
		}

	}

	/*
	 * Return: >0 if valid message received (return message length). 0 if OK but
	 * message not ready. -1 if invalid message. -2 if checksum fails
	 */
	private int checkNibeMessage(byte data[], int len) {
		if (len >= 1) {
			if (data[0] != NibeHeatPumpProtocol.FRAME_START_CHAR) {
				return -1;
			}

			if (len >= 2) {
				if (!(data[1] == 0x00)) {
					return -1;
				}
			}

			if (len >= 6) {
				int datalen = data[4];

				if (len < datalen + 6) {
					return 0;
				}

				// calculate XOR checksum
				byte calc_checksum = 0;
				for (int i = 2; i < (datalen + 5); i++) {
					calc_checksum ^= data[i];
				}

				byte msg_checksum = data[datalen + 5];
				logger.debug("Calculated checksum={}", calc_checksum);
				logger.debug("Message checksum={}", msg_checksum);

				if (calc_checksum != msg_checksum) {

					// if checksum is 0x5C (start character), heat pump seems to
					// send 0xC5 checksum
					if (calc_checksum != 0x5C && msg_checksum != 0xC5) {
						return -2;
					}
				}

				return datalen + 6;
			}
		}

		return 0;
	}

	private void sendNakToNibe() throws IOException {
		logger.debug("Send Nak");
		out.write(0x15);
		out.flush();
	}

	private void sendAckToNibe() throws IOException {
		logger.debug("Send Ack");
		out.write(0x06);
		out.flush();
	}

	private void sendDataToNibe(byte[] data) throws IOException {
		logger.trace("Sending data (len={}): {}",
				data.length, DatatypeConverter.printHexBinary(data));
		out.write(data);
		out.flush();
	}

	private void sendMsgToListeners(byte[] msg) {
		try {
			Iterator<NibeHeatPumpEventListener> iterator = _listeners
					.iterator();

			while (iterator.hasNext()) {
				((NibeHeatPumpEventListener) iterator.next())
						.packetReceived(msg);
			}

		} catch (Exception e) {
			logger.error("Event listener invoking error", e);
		}
	}

	private void sendErrorToListeners(String error) {
		try {
			Iterator<NibeHeatPumpEventListener> iterator = _listeners
					.iterator();

			while (iterator.hasNext()) {
				((NibeHeatPumpEventListener) iterator.next())
						.errorOccured(error);
			}

		} catch (Exception e) {
			logger.error("Event listener invoking error", e);
		}
	}
}
