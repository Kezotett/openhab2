/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.internal.protocol;

/**
 * This interface defines interface to receive data from heat pump.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public interface NibeHeatPumpEventListener {

	/**
	 * Procedure for receive raw data from heat pump.
	 * 
	 * @param data
	 *            Received raw data.
	 */
	void packetReceived(byte[] data);

	/**
	 * Procedure for receiving error information.
	 * 
	 * @param error
	 *            Error occured.
	 */
	void errorOccured(String error);

}
