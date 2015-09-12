/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link NibeHeatPumpBinding} class defines common constants, which are 
 * used across the whole binding.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class NibeHeatPumpBindingConstants {

    public static final String BINDING_ID = "nibeheatpump";
    
    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_F1245 = new ThingTypeUID(BINDING_ID, "f1245");

    /**
	 * Presents all supported Bridge types by RFXCOM binding. 
	 */
	public final static Set<ThingTypeUID> SUPPORTED_BRIDGE_THING_TYPES_UIDS = ImmutableSet
			.of(THING_TYPE_F1245);
}
