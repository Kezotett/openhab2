/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link SamsungTvBinding} class defines common constants, which are used
 * across the whole binding.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvBindingConstants {

	public static final String BINDING_ID = "samsungtv";

	// List of all Thing Type UIDs
	public final static ThingTypeUID SAMSUNG_TV_MEDIARENDERER_THING_TYPE = new ThingTypeUID(
			BINDING_ID, "mediarenderer");
	public final static ThingTypeUID SAMSUNG_TV_REMOTE_CONTROLLER_THING_TYPE = new ThingTypeUID(
			BINDING_ID, "remotecontroller");
	public final static ThingTypeUID SAMSUNG_TV_AGENT_THING_TYPE = new ThingTypeUID(
			BINDING_ID, "tvagent");

	// List of all remote controller thing channel ids
	public final static String KEY_CODE = "keycode";

	// List of all media renderer thing channel ids
	public final static String VOLUME = "volume";
	public final static String MUTE = "mute";
	public final static String BRIGHTNESS = "brightness";
	public final static String CONTRAST = "contrast";
	public final static String SHARPNESS = "sharpness";
	public final static String COLOR_TEMPERATURE = "colortemperature";

	// List of all tv agent thing channel ids
	public final static String SOURCENAME = "sourcename";
	public final static String SOURCEID = "sourceid";
	public final static String CHANNEL = "channel";
	public final static String PROGRAM_TITLE = "programtitle";
	public final static String CHANNEL_NAME = "channelname";
	public final static String BROWSER_URL = "url";
	public final static String STOP_BROWSER = "stopbrowser";
}
