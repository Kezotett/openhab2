/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.internal.models;

/**
 * Class for 
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class VariableInformation {

	public enum NibeDataType {
		U8, U16, U32, S8, S16, S32;
	}

	public enum Type {
		Sensor, Status, Settings;
	}

	public int factor;
	public String variable;
	public NibeDataType dataType;
	public Type type;

	public VariableInformation() {
	}

	public VariableInformation(int factor, String variable,
			NibeDataType dataType, Type type) {
		this.factor = factor;
		this.variable = variable;
		this.dataType = dataType;
		this.type = type;
	}
	
	public static VariableInformation getVariableInfo(PumpModel model, int key) {
		switch(model) {
		case F1145:
			return F1X45.getVariableInfo(key);
		case F1245:
			return F1X45.getVariableInfo(key);
		case F750:
			return F750.getVariableInfo(key);
		default:
			return null;
		}
	}
}