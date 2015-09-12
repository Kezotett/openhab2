/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.internal.protocol;

import org.openhab.binding.nibeheatpump.internal.NibeHeatPumpException;

/**
 * Base class for Nibe heat pump communication.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public abstract class NibeHeatPumpConnector {

	/**
	 * Procedure for connect to heat pump.
	 * 
	 * @throws NibeHeatPumpException
	 */
	public abstract void connect() throws NibeHeatPumpException;

	/**
	 * Procedure for disconnect from heat pump.
	 * 
	 * @throws NibeHeatPumpException
	 */
	public abstract void disconnect() throws NibeHeatPumpException;

	/**
	 * Procedure for register event listener.
	 * 
	 * @param listener
	 *            Event listener instance to handle events.
	 */
	public abstract void addEventListener(NibeHeatPumpEventListener listener);

	/**
	 * Procedure for remove event listener.
	 * 
	 * @param listener
	 *            Event listener instance to remove.
	 */
	public abstract void removeEventListener(NibeHeatPumpEventListener listener);

	/**
	 * Procedure for sending datagram to heat pump.
	 * 
	 * @throws NibeHeatPumpException
	 */
	public abstract void sendDatagram(byte[] data) throws NibeHeatPumpException;

}
