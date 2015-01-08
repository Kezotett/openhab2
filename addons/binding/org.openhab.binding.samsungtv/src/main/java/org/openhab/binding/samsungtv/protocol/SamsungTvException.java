/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.protocol;

/**
 * Exception for Samsung TV communication
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvException extends Exception {

	private static final long serialVersionUID = -5292218577704635666L;
	
	public SamsungTvException() {
		super();
	}

	public SamsungTvException(String message) {
		super(message);
	}

	public SamsungTvException(String message, Throwable cause) {
		super(message, cause);
	}

	public SamsungTvException(Throwable cause) {
		super(cause);
	}

}
