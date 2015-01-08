/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.Command;

/**
 * The {@link SamsunTvDataConverters} is responsible for converting
 * openhab commands Samsung TV specific values.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsunTvDataConverters {

	public static int convertCommandToIntValue(ChannelUID channelUID,
			Command command, int min, int max, int currentValue) {
		if (command instanceof IncreaseDecreaseType
				|| command instanceof DecimalType
				|| command instanceof PercentType) {

			int value;
			if (command instanceof IncreaseDecreaseType
					&& command == IncreaseDecreaseType.INCREASE) {
				value = Math.min(max, currentValue + 1);
			} else if (command instanceof IncreaseDecreaseType
					&& command == IncreaseDecreaseType.DECREASE) {
				value = Math.max(min, currentValue - 1);
			} else if (command instanceof DecimalType) {
				value = ((DecimalType) command).intValue();
			} else {
				throw new NumberFormatException("Command '" + command
						+ "' not supported for channel '" + channelUID + "'");
			}

			return value;

		} else {
			throw new NumberFormatException("Command '" + command
					+ "' not supported for channel '" + channelUID + "'");
		}
	}

	public static boolean convertCommandToBooleanValue(ChannelUID channelUID,
			Command command) {

		if (command instanceof OnOffType || command instanceof OpenClosedType
				|| command instanceof UpDownType) {

			boolean newValue;

			if (command.equals(OnOffType.ON) || command.equals(UpDownType.UP)
					|| command.equals(OpenClosedType.OPEN)) {
				newValue = true;
			} else if (command.equals(OnOffType.OFF)
					|| command.equals(UpDownType.DOWN)
					|| command.equals(OpenClosedType.CLOSED)) {
				newValue = false;
			} else {
				throw new NumberFormatException("Command '" + command
						+ "' not supported for channel '" + channelUID + "'");
			}

			return newValue;

		} else {
			throw new NumberFormatException("Command '" + command
					+ "' not supported for channel '" + channelUID + "'");
		}
	}

}
