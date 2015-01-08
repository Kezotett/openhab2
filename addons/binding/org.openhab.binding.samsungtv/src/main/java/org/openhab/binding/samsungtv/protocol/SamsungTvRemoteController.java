/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SamsungTvRemoteController} is responsible for sending key codes to
 * the Samsung TV.
 * 
 * @see <a
 *      href="http://sc0ty.pl/2012/02/samsung-tv-network-remote-control-protocol/">http://sc0ty.pl/2012/02/samsung-tv-network-remote-control-protocol/</a>
 * 
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvRemoteController {

	private static Logger logger = LoggerFactory
			.getLogger(SamsungTvRemoteController.class);

	// Access granted response
	private static final char[] ACCESS_GRANTED_RESP = new char[] { 0x64, 0x00,
			0x01, 0x00 };

	// User rejected your network remote controller response
	private static final char[] ACCESS_DENIED_RESP = new char[] { 0x64, 0x00,
			0x00, 0x00 };

	// waiting for user to grant or deny access response
	private static final char[] WAITING_USER_GRANT_RESP = new char[] { 0x0A,
			0x00, 0x02, 0x00, 0x00, 0x00 };

	// timeout or cancelled by user response
	private static final char[] ACCESS_TIMEOUT_RESP = new char[] { 0x65, 0x00 };

	private static final String APP_STRING = "iphone.iapp.samsung";

	private static final int TIMEOUT = 5000;

	private String host;
	private int port;
	private String appName;
	private String uniqueId;

	private Socket socket;
	private InputStreamReader reader;
	private BufferedWriter writer;

	public SamsungTvRemoteController(String host, int port, String appName,
			String uniqueId) {
		this.host = host;
		this.port = port;
		this.appName = appName != null ? appName : "";
		this.uniqueId = uniqueId != null ? uniqueId : "";
	}

	public void openConnection() throws SamsungTvException {
		logger.debug("Open connection to host '{}:{}'", host, port);

		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(host, port), TIMEOUT);
		} catch (Exception e) {
			throw new SamsungTvException("Connection failed", e);
		}

		logger.debug("Connection successfully opened...quering access");

		try {
			/*
			 * 
			 * offset	value and description
			 * ------	---------------------
			 * 0x00		0x00 - datagram type?
			 * 0x01		0x0013 - string length (little endian)
			 * 0x03		"iphone.iapp.samsung" - string content
			 * 0x16		0x0038 - payload size (little endian)
			 * 0x18		payload
			 * 
			 * Payload starts with 2 bytes: 0x64 and 0x00, 
			 * then comes 3 strings encoded with base64 algorithm. 
			 * Every string is preceded by 2-bytes field containing encoded string length. 
			 * 
			 * These three strings are as follow:
			 * 
			 * remote control device IP,
			 * unique ID – value to distinguish controllers,
			 * name – it will be displayed as controller name.
			 */

			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));

			writer.append((char) 0x00);
			writeString(writer, APP_STRING);
			writeString(writer, createRegistrationPayload(socket
					.getLocalAddress().getHostAddress()));
			writer.flush();

			try {

				/*
				 * offset	value and description
				 * ------	---------------------
				 * 0x00		don't know, it it always 0x00 or 0x02
				 * 0x01		0x000c - string length (little endian)
				 * 0x03		"iapp.samsung" - string content
				 * 0x0f		0x0006 - payload size (little endian)
				 * 0x11		payload
				*/
				
				InputStream in = socket.getInputStream();
				reader = new InputStreamReader(in);

				reader.skip(1);
				readString(reader);
				char[] result = readCharArray(reader);

				if (Arrays.equals(result, ACCESS_GRANTED_RESP)) {
					logger.debug("Access granted");
					
				} else if (Arrays.equals(result, ACCESS_DENIED_RESP)) {
					throw new SamsungTvException("Access denied");
					
				} else if (Arrays.equals(result, ACCESS_TIMEOUT_RESP)) {
					throw new SamsungTvException("Registration timed out");
					
				} else if (Arrays.equals(result, WAITING_USER_GRANT_RESP)) {
					throw new SamsungTvException("Waiting for user to grant access");
					
				} else {
					throw new SamsungTvException("Unknown response received for access query");
				}

				int i;
				while ((i = in.available()) > 0) {
					in.skip(i);
				}

			} catch (IOException e) {
				throw new SamsungTvException(e);
			}
		} catch (IOException e) {
			throw new SamsungTvException(e);
		}
	}

	public void closeConnection() throws SamsungTvException {
		try {
			socket.close();
		} catch (IOException e) {
			throw new SamsungTvException(e);
		}
	}

	public void sendKey(KeyCode key) throws SamsungTvException {

		if (!isConnected()) {
			openConnection();
		}

		try {
			sendKeyData(key);
		} catch (SamsungTvException e) {
			logger.debug("Couldn't send command", e);
			logger.debug("Retry one time...");

			closeConnection();
			openConnection();
			
			sendKeyData(key);
		}

		logger.debug("Command successfully sent");
	}

	private boolean isConnected() {
		if (socket == null || socket.isClosed() || !socket.isConnected()) {
			return false;
		} else {
			return true;
		}
	}

	private String createRegistrationPayload(String ip) throws IOException {
		/*
		 * Payload starts with 2 bytes: 0x64 and 0x00, 
		 * then comes 3 strings encoded with base64 algorithm. 
		 * Every string is preceded by 2-bytes field containing encoded string length. 
		 * 
		 * These three strings are as follow:
		 * 
		 * remote control device IP,
		 * unique ID – value to distinguish controllers,
		 * name – it will be displayed as controller name.
		 */

		StringWriter w = new StringWriter();
		w.append((char) 0x64);
		w.append((char) 0x00);
		writeBase64String(w, ip);
		writeBase64String(w, uniqueId);
		writeBase64String(w, appName);
		w.flush();
		return w.toString();
	}

	private void writeString(Writer writer, String str) throws IOException {
		int len = str.length();
		byte low = (byte) (len & 0xFF);
		byte high = (byte) ((len >> 8) & 0xFF);

		writer.append((char) (low));
		writer.append((char) (high));
		writer.append(str);
	}

	private void writeBase64String(Writer writer, String str)
			throws IOException {
		String tmp = new String(Base64.encodeBase64(str.getBytes()));
		writeString(writer, tmp);
	}

	private String readString(Reader reader) throws IOException {
		char[] buf = readCharArray(reader);
		return new String(buf);
	}

	private char[] readCharArray(Reader reader) throws IOException {
		byte low = (byte) reader.read();
		byte high = (byte) reader.read();
		int len = (high << 8) + low;

		char[] buffer = new char[len];
		reader.read(buffer);
		return buffer;
	}

	private void sendKeyData(KeyCode key) throws SamsungTvException {
		logger.debug("Sending key code " + key.getValue());
		
		/*
		 * offset	value and description
		 * ------	---------------------
		 * 0x00		always 0x00
		 * 0x01		0x0013 - string length (little endian)
		 * 0x03		"iphone.iapp.samsung" - string content 
		 * 0x16		0x0011 - payload size (little endian)
		 * 0x18		payload
		 */
		try {
			writer.append((char) 0x00);
			writeString(writer, APP_STRING);
			writeString(writer, createKeyDataPayload(key));
			writer.flush();

			/*
			 * Read response. Response is pretty useless, because TV seems to
			 * send same response in both ok and error situation.
			 */
			reader.skip(1);
			readString(reader);
			readCharArray(reader);
		} catch (IOException e) {
			throw new SamsungTvException(e);
		}
	}

	private String createKeyDataPayload(KeyCode key) throws IOException {
		/* 
		 * Payload:
		 * 
		 * offset	value and description
		 * ------	---------------------
		 * 0x18		three 0x00 bytes
		 * 0x1b		0x000c - key code size (little endian)
		 * 0x1d		key code encoded as base64 string
		 */

		StringWriter writer = new StringWriter();
		writer.append((char) 0x00);
		writer.append((char) 0x00);
		writer.append((char) 0x00);
		writeBase64String(writer, key.getValue());
		writer.flush();
		return writer.toString();
	}
}
