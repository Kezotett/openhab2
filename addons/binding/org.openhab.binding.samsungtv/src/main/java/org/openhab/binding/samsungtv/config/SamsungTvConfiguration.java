/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.config;

/**
 * Configuration class for {@link SamsungTvBinding} device.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvConfiguration {
	public static final String UDN = "udn";
	public static final String NETWORK_ADDRESS = "networkAddress";
	public static final String PORT = "port";
	
	public String udn;
	public String networkAddress;
	public int port;
	
}
