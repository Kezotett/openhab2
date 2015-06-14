/**
 * Copyright (c) 2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.internal.service;

import static org.openhab.binding.samsungtv.SamsungTvBindingConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOParticipant;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOService;
import org.openhab.binding.samsungtv.internal.service.api.SamsungTvService;
import org.openhab.binding.samsungtv.internal.service.api.ValueReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MediaRendererService} is responsible for handling MediaRenderer
 * commands.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class MediaRendererService implements UpnpIOParticipant,
		SamsungTvService {

	private final String SERVICE_NAME = "RenderingControl";
	private final List<String> supportedCommands = Arrays.asList(VOLUME, MUTE, BRIGHTNESS, CONTRAST, SHARPNESS, COLOR_TEMPERATURE);

	private Logger logger = LoggerFactory.getLogger(MediaRendererService.class);

	private UpnpIOService service;

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> pollingJob;

	private String udn;
	private int pollingInterval;

	private Map<String, String> stateMap = Collections
			.synchronizedMap(new HashMap<String, String>());

	private List<ValueReceiver> listeners = new ArrayList<ValueReceiver>();

	public MediaRendererService(UpnpIOService upnpIOService, String udn,
			int pollingInterval) {
		logger.debug("Create a Samsung TV MediaRenderer service");

		if (upnpIOService != null) {
			service = upnpIOService;
		} else {
			logger.debug("upnpIOService not set.");
		}

		this.udn = udn;
		this.pollingInterval = pollingInterval;

		scheduler = Executors.newScheduledThreadPool(1);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public List<String> getSupportedChannelNames() {
		return supportedCommands;
	}

	@Override
	public void addEventListener(ValueReceiver listener) {
		listeners.add(listener);
	}

	@Override
	public void removeEventListener(ValueReceiver listener) {
		listeners.remove(listener);
	}

	@Override
	public void start() {
		if (pollingJob == null || pollingJob.isCancelled()) {
			logger.debug("Start refresh task, interval={}", pollingInterval);
			pollingJob = scheduler.scheduleAtFixedRate(pollingRunnable, 0,
					pollingInterval, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void stop() {
		if (pollingJob != null && !pollingJob.isCancelled()) {
			pollingJob.cancel(true);
			pollingJob = null;
		}
	}

	private Runnable pollingRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRegistered()) {

				try {
					updateResourceState(SERVICE_NAME, "GetVolume",
							SamsungTvUtils.buildHashMap("InstanceID", "0",
									"Channel", "Master"));
					updateResourceState(SERVICE_NAME, "GetMute",
							SamsungTvUtils.buildHashMap("InstanceID", "0",
									"Channel", "Master"));
					updateResourceState(SERVICE_NAME, "GetBrightness",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));
					updateResourceState(SERVICE_NAME, "GetContrast",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));
					updateResourceState(SERVICE_NAME, "GetSharpness",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));
					updateResourceState(SERVICE_NAME, "GetColorTemperature",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));

				} catch (Exception e) {
					logger.debug("Exception during poll : {}", e);
				}
			}
		}
	};

	@Override
	public void handleCommand(String channel, Command command) {
		logger.debug("Received channel: {}, command: {}", channel, command);
		
		switch(channel) {
		case VOLUME:
			setVolume(command);
			break;
		case MUTE:
			setMute(command);
			break;
		case BRIGHTNESS:
			setBrightness(command);
			break;
		case CONTRAST:
			setContrast(command);
			break;
		case SHARPNESS:
			setSharpness(command);
			break;
		case COLOR_TEMPERATURE:
			setColorTemperature(command);
			break;
		default:
			logger.warn(
					"Samsung TV doesn't support transmitting for channel '{}'",
					channel);
		}
	}

	private boolean isRegistered() {
		return service.isRegistered(this);
	}

	@Override
	public String getUDN() {
		return udn;
	}

	@Override
	public void onValueReceived(String variable, String value, String service) {

		String oldValue = stateMap.get(variable);
		if (value.equals(oldValue)) {
			logger.trace(
					"Variable '{}' value haven't been changed, ignore update",
					variable);
			return;
		}

		stateMap.put(variable, value);

		for (ValueReceiver listener : listeners) {
			switch (variable) {
			case "CurrentVolume":
				listener.valueReceived(VOLUME,
						(value != null) ? new PercentType(value)
								: UnDefType.UNDEF);
				break;

			case "CurrentMute":
				State newState = UnDefType.UNDEF;
				if (value != null) {
					newState = value.equals("true") ? OnOffType.ON
							: OnOffType.OFF;
				}
				listener.valueReceived(MUTE, newState);
				break;

			case "CurrentBrightness":
				listener.valueReceived(BRIGHTNESS,
						(value != null) ? new PercentType(value)
								: UnDefType.UNDEF);
				break;

			case "CurrentContrast":
				listener.valueReceived(CONTRAST,
						(value != null) ? new PercentType(value)
								: UnDefType.UNDEF);
				break;

			case "CurrentSharpness":
				listener.valueReceived(SHARPNESS,
						(value != null) ? new PercentType(value)
								: UnDefType.UNDEF);
				break;

			case "CurrentColorTemperature":
				listener.valueReceived(COLOR_TEMPERATURE,
						(value != null) ? new DecimalType(value)
								: UnDefType.UNDEF);
				break;
			}
		}
	}

	protected Map<String, String> updateResourceState(String serviceId,
			String actionId, Map<String, String> inputs) {

		Map<String, String> result = service.invokeAction(this, serviceId,
				actionId, inputs);

		for (String variable : result.keySet()) {
			onValueReceived(variable, result.get(variable), serviceId);
		}

		return result;
	}

	private void setVolume(Command command) {
		int newValue;

		try {
			newValue = DataConverters.convertCommandToIntValue(command, 0, 100,
					Integer.valueOf(stateMap.get("CurrentVolume")));
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Command '" + command
					+ "' not supported");
		}

		updateResourceState(SERVICE_NAME, "SetVolume",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master", "DesiredVolume", Integer.toString(newValue)));

		updateResourceState(SERVICE_NAME, "GetVolume",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master"));
	}

	private void setMute(Command command) {
		boolean newValue;

		try {
			newValue = DataConverters.convertCommandToBooleanValue(command);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Command '" + command
					+ "' not supported");
		}

		updateResourceState(SERVICE_NAME, "SetMute",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master", "DesiredMute", Boolean.toString(newValue)));

		updateResourceState(SERVICE_NAME, "GetMute",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master"));

	}

	private void setBrightness(Command command) {
		int newValue;

		try {
			newValue = DataConverters.convertCommandToIntValue(command, 0, 100,
					Integer.valueOf(stateMap.get("CurrentBrightness")));
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Command '" + command
					+ "' not supported");
		}

		updateResourceState(SERVICE_NAME, "SetBrightness",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredBrightness", Integer.toString(newValue)));

		updateResourceState(SERVICE_NAME, "GetBrightness",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

	private void setContrast(Command command) {
		int newValue;

		try {
			newValue = DataConverters.convertCommandToIntValue(command, 0, 100,
					Integer.valueOf(stateMap.get("CurrentContrast")));
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Command '" + command
					+ "' not supported");
		}

		updateResourceState(SERVICE_NAME, "SetContrast",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredContrast", Integer.toString(newValue)));

		updateResourceState(SERVICE_NAME, "GetContrast",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

	private void setSharpness(Command command) {
		int newValue;

		try {
			newValue = DataConverters.convertCommandToIntValue(command, 0, 100,
					Integer.valueOf(stateMap.get("CurrentSharpness")));
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Command '" + command
					+ "' not supported");
		}

		updateResourceState(SERVICE_NAME, "SetSharpness",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredSharpness", Integer.toString(newValue)));

		updateResourceState(SERVICE_NAME, "GetSharpness",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

	private void setColorTemperature(Command command) {
		int newValue;

		try {
			newValue = DataConverters.convertCommandToIntValue(command, 0, 4,
					Integer.valueOf(stateMap.get("CurrentColorTemperature")));
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Command '" + command
					+ "' not supported");
		}

		updateResourceState(SERVICE_NAME, "SetColorTemperature",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredColorTemperature", Integer.toString(newValue)));

		updateResourceState(SERVICE_NAME, "GetColorTemperature",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

	@Override
	public void onStatusChanged(boolean status) {
		logger.debug("onStatusChanged");
	}
}
