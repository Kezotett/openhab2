/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.internal.protocol;

import java.util.Hashtable;

import org.apache.commons.lang.ArrayUtils;
import org.openhab.binding.nibeheatpump.internal.NibeHeatPumpException;
import org.openhab.binding.nibeheatpump.internal.models.PumpModel;
import org.openhab.binding.nibeheatpump.internal.models.VariableInformation;

/**
 * Class for parse data packets from Nibe heat pumps
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class NibeHeatPumpProtocol {

	static final byte FRAME_START_CHAR = (byte) 0x5C;
	
	static final byte OFFSET_ADR = 2;
	static final byte OFFSET_CMD = 3;
	static final byte OFFSET_LEN = 4;
	static final byte OFFSET_DATA = 5;

	static final byte CMD_RMU_DATA_READ_OUT = (byte) 0x62;
	static final byte CMD_MODBUS_DATA_READ_OUT = (byte) 0x68;
	static final byte CMD_MODBUS_READ_REQ = (byte) 0x69;
	static final byte CMD_MODBUS_READ_RESP = (byte) 0x6A;
	static final byte CMD_MODBUS_WRITE_REQ = (byte) 0x6B;
	static final byte CMD_MODBUS_WRITE_RESP = (byte) 0x6C;

	static final byte ADR_SMS40 = (byte) 0x16;
	static final byte ADR_RMU40 = (byte) 0x19;
	static final byte ADR_MODBUS40 = (byte) 0x20;

	public static Hashtable<Integer, Integer> ParseMessage(byte[] data)
			throws NibeHeatPumpException {

		if (isModbus40DataReadOut(data)) {

			final int datalen = data[OFFSET_LEN];
			int msglen = 5 + datalen;

			byte checksum = 0;

			// calculate XOR checksum
			for (int i = 2; i < msglen; i++)
				checksum ^= data[i];

			final byte msgChecksum = data[msglen];

			// if checksum is 0x5C (start character), heat pump seems to send
			// 0xC5 checksum

			if (checksum == msgChecksum
					|| (checksum == FRAME_START_CHAR && msgChecksum == 0xC5)) {

				if (datalen > 0x50) {
					// if data contains 0x5C (start character),
					// data seems to contains double 0x5C characters

					// let's remove doubles
					for (int i = 1; i < msglen; i++) {
						if (data[i] == FRAME_START_CHAR) {
							data = ArrayUtils.remove(data, i);
							msglen--;
						}
					}
				}

				// parse data to hash table

				Hashtable<Integer, Integer> values = new Hashtable<Integer, Integer>();

				try {
					for (int i = OFFSET_DATA; i < (msglen - 1); i += 4) {

						int id = ((data[i + 1] & 0xFF) << 8 | (data[i + 0] & 0xFF));
						int value = (short) ((data[i + 3] & 0xFF) << 8 | (data[i + 2] & 0xFF));

						if (id != 0xFFFF)
							values.put(id, value);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					throw new NibeHeatPumpException(
							"Error occured during data parsing", e);
				}

				return values;

			} else {
				throw new NibeHeatPumpException("Checksum does not match");
			}

		} else if (isModbus40ReadResponse(data)) {

			final int datalen = data[OFFSET_LEN];
			int msglen = 5 + datalen;

			byte checksum = 0;

			// calculate XOR checksum
			for (int i = 2; i < msglen; i++)
				checksum ^= data[i];

			final byte msgChecksum = data[msglen];

			// if checksum is 0x5C (start character), heat pump seems to send
			// 0xC5 checksum

			if (checksum == msgChecksum
					|| (checksum == FRAME_START_CHAR && msgChecksum == 0xC5)) {

				if (datalen > 0x06) {
					// if data contains 0x5C (start character),
					// data seems to contains double 0x5C characters

					// let's remove doubles
					for (int i = 1; i < msglen; i++) {
						if (data[i] == FRAME_START_CHAR) {
							data = ArrayUtils.remove(data, i);
							msglen--;
						}
					}
				}

				// parse data to hash table

				Hashtable<Integer, Integer> values = new Hashtable<Integer, Integer>();

				int id = ((data[OFFSET_DATA + 1] & 0xFF) << 8 | (data[OFFSET_DATA + 0] & 0xFF));
				int value = (int) ((data[OFFSET_DATA + 5] & 0xFF) << 24
						| (data[OFFSET_DATA + 4] & 0xFF) << 16
						| (data[OFFSET_DATA + 3] & 0xFF) << 8 | (data[OFFSET_DATA + 2] & 0xFF));

				values.put(id, value);
				return values;

			} else {
				throw new NibeHeatPumpException("Checksum does not match");
			}

		} else if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_RMU40
				&& data[OFFSET_CMD] == CMD_RMU_DATA_READ_OUT
				&& data[OFFSET_LEN] >= (byte) 0x18) {

			// RMU40 data packet, INCOMPLETE!

			int datalen = data[OFFSET_LEN];
			int msglen = 5 + datalen;

			byte checksum = 0;

			// calculate XOR checksum
			for (int i = 2; i < msglen; i++)
				checksum ^= data[i];

			byte msgChecksum = data[msglen];

			// if checksum is 0x5C (start character), heat pump seems to send
			// 0xC5 checksum

			if (checksum == msgChecksum
					|| (checksum == FRAME_START_CHAR && msgChecksum == 0xC5)) {

				if (datalen > 0x18) {
					// if data contains 0x5C (start character),
					// data seems to contains double 0x5C characters

					// let's remove doubles
					for (int i = 1; i < msglen; i++) {
						if (data[i] == FRAME_START_CHAR) {
							data = ArrayUtils.remove(data, i);
							msglen--;
						}
					}
				}

				// parse data to hash table

				Hashtable<Integer, Integer> values = new Hashtable<Integer, Integer>();

				try {
					int value = 0;

					// parse outdoor temperature
					value = (short) ((data[6] & 0xFF) << 8 | (data[5] & 0xFF));
					values.put(40004, value);

					// parse hot water top
					value = (short) ((data[8] & 0xFF) << 8 | (data[7] & 0xFF));
					values.put(40013, value);

					// parse point offset
					value = (short) ((data[10] & 0xFF) << 8 | (data[9] & 0xFF));
					values.put(47011, value);

				} catch (ArrayIndexOutOfBoundsException e) {
					throw new NibeHeatPumpException(
							"Error occured during data parsing", e);
				}

				return values;

			} else {
				throw new NibeHeatPumpException("Checksum does not match");
			}

		} else {
			return null;
		}
	}

	public static boolean isModbus40DataReadOut(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_MODBUS40) {

			if (data[OFFSET_CMD] == CMD_MODBUS_DATA_READ_OUT
					&& data[OFFSET_LEN] >= (byte) 0x50) {
				return true;
			}
		}

		return false;
	}

	public static boolean isModbus40ReadResponse(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_MODBUS40) {

			if (data[OFFSET_CMD] == CMD_MODBUS_READ_RESP
					&& data[OFFSET_LEN] >= (byte) 0x06) {
				return true;
			}
		}

		return false;
	}

	public static boolean isRmu40DataReadOut(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_RMU40) {

			if (data[OFFSET_CMD] == CMD_RMU_DATA_READ_OUT
					&& data[OFFSET_LEN] >= (byte) 0x18) {
				return true;
			}
		}

		return false;
	}

	public static boolean isModbus40WriteResponsePdu(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_MODBUS40
				&& data[OFFSET_CMD] == CMD_MODBUS_WRITE_RESP) {
			return true;
		}

		return false;
	}

	public static boolean isModbus40WriteTokenPdu(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_MODBUS40
				&& data[OFFSET_CMD] == CMD_MODBUS_WRITE_REQ
				&& data[OFFSET_LEN] == 0x00) {
			return true;
		}

		return false;
	}

	public static boolean isModbus40ReadTokenPdu(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_MODBUS40
				&& data[OFFSET_CMD] == CMD_MODBUS_READ_REQ
				&& data[OFFSET_LEN] == 0x00) {
			return true;
		}

		return false;
	}

	public static boolean isModbus40WriteRequestPdu(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_MODBUS40
				&& data[OFFSET_CMD] == CMD_MODBUS_WRITE_REQ) {
			return true;
		}

		return false;
	}

	public static boolean isModbus40ReadRequestPdu(byte[] data) {

		if (data[0] == FRAME_START_CHAR && data[1] == (byte) 0x00
				&& data[OFFSET_ADR] == ADR_MODBUS40
				&& data[OFFSET_CMD] == CMD_MODBUS_READ_REQ) {
			return true;
		}

		return false;
	}

	public static boolean modbus40WriteSuccess(byte[] data) {
		if (isModbus40WriteResponsePdu(data)) {
			if (data[OFFSET_DATA] == 1) {
				return true;
			}
		}

		return false;
	}

	public static byte[] createModbus40ReadPdu(int coildAddress) {

		byte[] data = new byte[6];
		data[0] = (byte) 0xC0;
		data[1] = CMD_MODBUS_READ_REQ;
		data[2] = (byte) 0x02; // data len
		data[3] = (byte) (coildAddress & 0xFF);
		data[4] = (byte) ((coildAddress >> 8) & 0xFF);

		// calculate XOR checksum
		for (int i = 0; i < 5; i++)
			data[5] ^= data[i];

		return data;
	}

	public static byte[] createModbus40WritePdu(PumpModel pumpModel,
			int coildAddress, int value) {

		VariableInformation variableInfo = VariableInformation.getVariableInfo(
				pumpModel, coildAddress);

		byte[] data = new byte[10];

		data[0] = (byte) 0xC0;
		data[1] = CMD_MODBUS_WRITE_REQ;
		data[2] = (byte) 0x06; // data len
		data[3] = (byte) (coildAddress & 0xFF);
		data[4] = (byte) ((coildAddress >> 8) & 0xFF);

		switch (variableInfo.dataType) {

		case U8:
		case S8:
			data[5] = (byte) (value & 0xFF);
			data[6] = 0;
			data[7] = 0;
			data[8] = 0;
			break;

		case U16:
		case S16:
			data[5] = (byte) (value & 0xFF);
			data[6] = (byte) ((value >> 8) & 0xFF);
			data[7] = 0;
			data[8] = 0;
			break;

		case U32:
		case S32:
			data[5] = (byte) (value & 0xFF);
			data[6] = (byte) ((value >> 8) & 0xFF);
			data[7] = (byte) ((value >> 16) & 0xFF);
			data[8] = (byte) ((value >> 24) & 0xFF);
			break;
		}

		// calculate XOR checksum
		for (int i = 0; i < 9; i++) {
			data[9] ^= data[i];
		}

		return data;
	}
}
