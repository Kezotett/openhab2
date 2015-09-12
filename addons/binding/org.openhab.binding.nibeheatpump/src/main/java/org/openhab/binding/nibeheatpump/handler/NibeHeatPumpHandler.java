/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.nibeheatpump.internal.NibeHeatPumpException;
import org.openhab.binding.nibeheatpump.internal.config.NibeHeatPumpConfiguration;
import org.openhab.binding.nibeheatpump.internal.models.PumpModel;
import org.openhab.binding.nibeheatpump.internal.models.VariableInformation;
import org.openhab.binding.nibeheatpump.internal.models.VariableInformation.NibeDataType;
import org.openhab.binding.nibeheatpump.internal.protocol.NibeHeatPumpConnector;
import org.openhab.binding.nibeheatpump.internal.protocol.NibeHeatPumpEventListener;
import org.openhab.binding.nibeheatpump.internal.protocol.NibeHeatPumpProtocol;
import org.openhab.binding.nibeheatpump.internal.protocol.NibeHeatPumpSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NibeHeatPumpHandler} is responsible for handling commands, which
 * are sent to one of the channels.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class NibeHeatPumpHandler extends BaseThingHandler implements
		NibeHeatPumpEventListener {

	private Logger logger = LoggerFactory.getLogger(NibeHeatPumpHandler.class);

	private PumpModel pumpModel = PumpModel.F1245;
	private NibeHeatPumpConfiguration config;

	private NibeHeatPumpConnector connector = null;

	private Exchanger< byte[]> writeDataExchanger = new Exchanger< byte[]>();
	private Exchanger< byte[]> readDataExchanger = new Exchanger< byte[]>();
	
	private byte[] responseMessage = null;
	private Object notifierObject = new Object();

	private int timeout = 10000;

	private ScheduledFuture<?> connectorTask;
	ScheduledFuture<?> pollingJob;
	List<Integer> itemsToPoll = new ArrayList<Integer>();

	private Map<Integer, Double> stateMap = Collections
			.synchronizedMap(new HashMap<Integer, Double>());

	public NibeHeatPumpHandler(Thing thing) {
		super(thing);
	}

	public synchronized byte[] getResponseMessage() {
		return responseMessage;
	}

	public synchronized void setResponseMessage(byte[] responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		logger.debug("Received channel: {}, command: {}", channelUID, command);

		int coilAddress = Integer.parseInt(channelUID.getId());

		byte[] data = NibeHeatPumpProtocol.createModbus40ReadPdu(coilAddress);
		logger.debug("Sending data message (len={}): {}", data.length,
				DatatypeConverter.printHexBinary(data));

		VariableInformation variableInfo = VariableInformation.getVariableInfo(
				pumpModel, coilAddress);

		if (variableInfo.type == VariableInformation.Type.Settings) {

			if (connector != null) {
				int value = Integer.parseInt(command.toString())
						* variableInfo.factor;
				byte[] byteData = NibeHeatPumpProtocol.createModbus40WritePdu(
						pumpModel, coilAddress, value);

				try {
					sendWriteMessageToNibe(byteData);
					updateStatus(ThingStatus.ONLINE);
					
				} catch (NibeHeatPumpException e) {
					logger.error("Message sending to heat pump failed.", e);
					updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
				}
			} else {
				logger.warn("No connection to heat pump");
				updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_MISSING_ERROR);
			}

		} else {
			logger.error(
					"Command to channel '{}' rejected, because item is read only parameter",
					channelUID);
		}
	}

	@Override
	public void channelLinked(ChannelUID channelUID) {
		logger.debug("channelLinked: {}", channelUID);

		// Add channel to polling loop
		synchronized (itemsToPoll) {
			itemsToPoll.add(Integer.parseInt(channelUID.getId()));
		}
	}

	@Override
	public void channelUnlinked(ChannelUID channelUID) {
		logger.debug("channelUnlinked: {}", channelUID);

		// remove channel from polling loop
		synchronized (itemsToPoll) {
			itemsToPoll.remove(Integer.parseInt(channelUID.getId()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		config = getConfigAs(NibeHeatPumpConfiguration.class);

		logger.debug(
				"Initialized Nibe Heat Pump device handler for {}, udpPort={}",
				getThing().getUID(), config.udpPort);

		try {
			pumpModel = PumpModel.getPumpModel(thing.getThingTypeUID().getId()
					.toString());
		} catch (IllegalArgumentException e) {
			logger.warn("Illegal pump model '{}', using default model '{}'",
					thing.getThingTypeUID().toString(), pumpModel);
		}

		connector = new NibeHeatPumpSimulator();
		connector.addEventListener(this);
		
		clearCache();
		
		if (connectorTask == null || connectorTask.isCancelled()) {
			connectorTask = scheduler.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					logger.debug("Checking Nibe Heat pump connection, thing status = {}", thing.getStatus());
					if (thing.getStatus() != ThingStatus.ONLINE) {
						connect();
					}
				}
			}, 0, 60, TimeUnit.SECONDS);
		}
	}

	private void connect() {
		logger.debug("Connecting to heat pump");
		try {
			connector.connect();
			updateStatus(ThingStatus.ONLINE);
			
			if (pollingJob == null || pollingJob.isCancelled()) {
				logger.debug("Start refresh task, interval={}",
						config.refreshInterval);
				pollingJob = scheduler.scheduleAtFixedRate(pollingRunnable, 0,
						config.refreshInterval, TimeUnit.MILLISECONDS);
			}
		} catch (NibeHeatPumpException e) {
			logger.error("Error occured when connecting to heat pump", e);
			updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
		}
	}
	
	@Override
	public void dispose() {
		logger.debug("Thing {} disposed.", getThing().getUID());

		if (connector != null) {
			connector.removeEventListener(this);
			try {
				connector.disconnect();
			} catch (NibeHeatPumpException e) {
				logger.error("Error occured when disconnecting form heat pump",
						e);
			}
		}

		if (connectorTask != null && !connectorTask.isCancelled()) {
			connectorTask.cancel(true);
			connectorTask = null;
		}
		
		if (pollingJob != null && !pollingJob.isCancelled()) {
			pollingJob.cancel(true);
			pollingJob = null;
		}
	}

	private Runnable pollingRunnable = new Runnable() {

		@Override
		public void run() {
			List<Integer> items = null;
			synchronized (itemsToPoll) {
				items = new ArrayList<Integer>(itemsToPoll);
			}

			for (int item : items) {
				if (connector != null) {
					byte[] data = NibeHeatPumpProtocol
							.createModbus40ReadPdu(item);

					try {
						//synchronized (notifierObject) {
							logger.debug("Sending read request (len={}): {}",
									data.length, DatatypeConverter.printHexBinary(data));
							// just send query to heat pump, response is received
							// and handled by the listener
							connector.sendDatagram(data);
							/*
							byte[] resp = readDataExchanger.exchange(null, timeout, TimeUnit.MILLISECONDS);
								logger.debug("Received read response (len={}): {}",
										data.length, DatatypeConverter.printHexBinary(resp));
								
							*/
					} catch (NibeHeatPumpException e) {
						logger.error("Message sending to heat pump failed.", e);
					}
				/*
				catch (TimeoutException e) {
					logger.debug("Write ack receiving timeout");
				} catch (InterruptedException e) {
					logger.debug("Write ack receiving interupted");
				}
				*/
				} else {
					logger.warn("No connection to heat pump");
				}
			}
		}
	};

	private State convertNibeValueToState(NibeDataType dataType, double value) {
		State state = UnDefType.UNDEF;

		switch (dataType) {
		case U8:
		case U16:
		case U32:
			state = new DecimalType((long) value);
			break;
		case S8:
		case S16:
		case S32:
			BigDecimal bd = new BigDecimal(value).setScale(2,
					RoundingMode.HALF_EVEN);
			state = new DecimalType(bd);
			break;
		}

		return state;
	}

	private void clearCache() {
		stateMap.clear();
	}

	private synchronized boolean sendWriteMessageToNibe(byte[] data) throws NibeHeatPumpException {
		//synchronized (notifierObject) {
			logger.debug("Sending write request (len={}): {}",
					data.length, DatatypeConverter.printHexBinary(data));
			
			//setResponseMessage(null);
			connector.sendDatagram(data);
/*
			try {
				//notifierObject.wait(timeout);

				//byte[] resp = getResponseMessage();
				byte[] resp = writeDataExchanger.exchange(null, timeout, TimeUnit.MILLISECONDS);

				if (resp != null) {
					if (NibeHeatPumpProtocol.modbus40WriteSuccess(resp)) {
						logger.debug("Command succesfully transmitted");
						return true;
					} else {
						logger.error("Command transmit failed");
					}
				} else {
					logger.error(
							"No acknowledge received from heat pump, timeout {}ms ",
							timeout);
				}
			} catch (TimeoutException e) {
				logger.debug("Write ack receiving timeout");
			} catch (InterruptedException e) {
				logger.debug("Write ack receiving interupted");
			}
		//}
*/		
		return false;
	}
	
	@Override
	public void packetReceived(byte[] data) {
		try {
			logger.debug("Received data (len={}): {}", data.length,
					DatatypeConverter.printHexBinary(data));

			updateStatus(ThingStatus.ONLINE);

			if (NibeHeatPumpProtocol.isModbus40WriteResponsePdu(data)) {
				logger.debug("Write response received");
				/*
				setResponseMessage(data);
				synchronized (notifierObject) {
					notifierObject.notify();
				}
				*/
				/*
				try {
					writeDataExchanger.exchange(data, 0, TimeUnit.SECONDS);
				} catch (InterruptedException | TimeoutException e) {
					logger.debug("writeDataExchanger exception: {}", e);
				}
				*/
				
			} else if (NibeHeatPumpProtocol.isModbus40ReadResponse(data)) {
				logger.debug("Read response received");
				//TODO
				//setResponseMessage(data);
				//synchronized (notifierObject) {
				//	notifierObject.notify();
				//}
				/*
				try {
					readDataExchanger.exchange(data, 0, TimeUnit.SECONDS);
				} catch (InterruptedException | TimeoutException e) {
					logger.debug("readDataExchanger exception: {}", e);
				}
				*/

			} else if (NibeHeatPumpProtocol.isModbus40DataReadOut(data)) {
				logger.debug("Data readout received");
				
				Hashtable<Integer, Integer> regValues = NibeHeatPumpProtocol
						.ParseMessage(data);

				if (regValues != null) {

					Enumeration<Integer> keys = regValues.keys();

					while (keys.hasMoreElements()) {

						int key = keys.nextElement();
						double value = regValues.get(key);

						VariableInformation variableInfo = VariableInformation
								.getVariableInfo(pumpModel, key);

						if (variableInfo == null) {
							logger.debug("Unknown variable {}", key);
						} else {
							value = value / variableInfo.factor;
							org.eclipse.smarthome.core.types.State state = convertNibeValueToState(
									variableInfo.dataType, value);

							logger.debug("{}={}", key + ":"
									+ variableInfo.variable, value);

							Double oldValue = stateMap.get(key);

							if (oldValue != null && value == oldValue) {
								logger.trace("Value haven't been changed, ignore update");
								continue;
							}
							stateMap.put(key, value);
							updateState(new ChannelUID(getThing().getUID(),
									String.valueOf(key)), state);
						}
					}
				}
			}
		} catch (NibeHeatPumpException e) {
			logger.error("Error occured when received data from heat pump", e);
		}
	}

	@Override
	public void errorOccured(String error) {
		logger.debug("Error '{}' occured, re-establish the connection", error);
		updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);

		dispose();
		initialize();
	}

}
