/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.handler;

import static org.openhab.binding.samsungtv.SamsungTvBindingConstants.*;
import static org.openhab.binding.samsungtv.config.SamsungTvConfiguration.UDN;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOParticipant;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOService;
import org.openhab.binding.samsungtv.internal.SamsungTvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SamsungTvMediaRendererHandler} is responsible for handling
 * commands, which are sent to one of the channels.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvMediaRendererHandler extends BaseThingHandler implements
		UpnpIOParticipant {

	private Logger logger = LoggerFactory
			.getLogger(SamsungTvMediaRendererHandler.class);

	private UpnpIOService service;
	private ScheduledFuture<?> pollingJob;

	/**
	 * The default refresh interval when not specified in channel configuration.
	 */
	private static final int DEFAULT_REFRESH_INTERVAL = 10;

	private Map<String, String> stateMap = Collections
			.synchronizedMap(new HashMap<String, String>());

	public SamsungTvMediaRendererHandler(Thing thing,
			UpnpIOService upnpIOService) {
		super(thing);

		logger.debug(
				"Create a Samsung TV Media Renderer Handler for thing '{}'",
				getThing().getUID());

		if (upnpIOService != null) {
			service = upnpIOService;
		} else {
			logger.debug("upnpIOService not set.");
		}
	}

	private Runnable pollingRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRegistered()) {
				try {
					updateResourceState("RenderingControl", "GetVolume",
							SamsungTvUtils.buildHashMap("InstanceID", "0",
									"Channel", "Master"));
					updateResourceState("RenderingControl", "GetMute",
							SamsungTvUtils.buildHashMap("InstanceID", "0",
									"Channel", "Master"));
					updateResourceState("RenderingControl", "GetBrightness",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));
					updateResourceState("RenderingControl", "GetContrast",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));
					updateResourceState("RenderingControl", "GetSharpness",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));
					updateResourceState("RenderingControl",
							"GetColorTemperature",
							SamsungTvUtils.buildHashMap("InstanceID", "0"));

				} catch (Exception e) {
					logger.debug("Exception during poll : {}", e);
				}
			}
		}
	};

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		logger.debug("Received channel: {}, command: {}", channelUID, command);

		URL url = service.getDescriptorURL(this);

		if (url != null) {
			logger.debug("Samsung TV url found as '{}' (host='{}')", url,
					url.getHost());

			if (channelUID.getId().equals(VOLUME)) {
				setVolume(channelUID, command);
			} else if (channelUID.getId().equals(MUTE)) {
				setMute(channelUID, command);
			} else if (channelUID.getId().equals(BRIGHTNESS)) {
				setBrightness(channelUID, command);
			} else if (channelUID.getId().equals(CONTRAST)) {
				setContrast(channelUID, command);
			} else if (channelUID.getId().equals(SHARPNESS)) {
				setSharpness(channelUID, command);
			} else if (channelUID.getId().equals(COLOR_TEMPERATURE)) {
				setColorTemperature(channelUID, command);
			} else {
				logger.error("Unsupported command '{}'", command);
			}
		}
	}

	private synchronized void onUpdate() {
		if (pollingJob == null || pollingJob.isCancelled()) {
			Configuration config = getThing().getConfiguration();
			// use default if not specified
			int refreshInterval = DEFAULT_REFRESH_INTERVAL;
			Object refreshConfig = config.get("refresh");
			if (refreshConfig != null) {
				refreshInterval = Integer.parseInt((String) refreshConfig);
			}
			logger.debug("Start refresh task, interval={}", refreshInterval);
			pollingJob = scheduler.scheduleAtFixedRate(pollingRunnable, 0,
					refreshInterval, TimeUnit.SECONDS);
		}
	}

	@Override
	public void initialize() {
		Configuration configuration = getConfig();

		if (configuration.get(UDN) != null) {
			logger.debug("Initializing Samsung TV handler for UDN '{}'",
					configuration.get(UDN));
			onUpdate();
		} else {
			logger.debug("Cannot initalize Samsung TV handler. UDN not set.");
		}

		if (getThing().getStatus() == ThingStatus.OFFLINE) {
			logger.debug("Setting status for thing '{}' to ONLINE", getThing()
					.getUID());
			getThing().setStatus(ThingStatus.ONLINE);
		}
	}

	@Override
	public void dispose() {

		if (pollingJob != null && !pollingJob.isCancelled()) {
			pollingJob.cancel(true);
			pollingJob = null;
		}

		if (getThing().getStatus() == ThingStatus.ONLINE) {
			logger.debug("Setting status for thing '{}' to OFFLINE", getThing()
					.getUID());
			getThing().setStatus(ThingStatus.OFFLINE);
		}
	}

	private boolean isRegistered() {
		return service.isRegistered(this);
	}

	@Override
	public String getUDN() {
		return (String) this.getThing().getConfiguration().get(UDN);
	}

	@Override
	public void onValueReceived(String variable, String value, String service) {
		logger.debug("Received pair '{}':'{}' (service '{}') for thing '{}'",
				new Object[] { variable, value, service,
						this.getThing().getUID() });

		this.stateMap.put(variable, value);

		switch (variable) {
		case "CurrentVolume":
			updateState(new ChannelUID(getThing().getUID(), VOLUME),
					(value != null) ? new PercentType(value) : UnDefType.UNDEF);
			break;

		case "CurrentMute":
			State newState = UnDefType.UNDEF;
			if (value != null) {
				newState = value.equals("true") ? OnOffType.ON : OnOffType.OFF;
			}
			updateState(new ChannelUID(getThing().getUID(), MUTE), newState);
			break;

		case "CurrentBrightness":
			updateState(new ChannelUID(getThing().getUID(), BRIGHTNESS),
					(value != null) ? new PercentType(value) : UnDefType.UNDEF);
			break;

		case "CurrentContrast":
			updateState(new ChannelUID(getThing().getUID(), CONTRAST),
					(value != null) ? new PercentType(value) : UnDefType.UNDEF);
			break;

		case "CurrentSharpness":
			updateState(new ChannelUID(getThing().getUID(), SHARPNESS),
					(value != null) ? new PercentType(value) : UnDefType.UNDEF);
			break;

		case "CurrentColorTemperature":
			updateState(new ChannelUID(getThing().getUID(), COLOR_TEMPERATURE),
					(value != null) ? new DecimalType(value) : UnDefType.UNDEF);
			break;
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

	private void setVolume(ChannelUID channelUID, Command command) {
		int newValue = SamsunTvDataConverters.convertCommandToIntValue(
				channelUID, command, 0, 100,
				Integer.valueOf(stateMap.get("CurrentVolume")));

		updateResourceState("RenderingControl", "SetVolume",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master", "DesiredVolume", Integer.toString(newValue)));

		updateResourceState("RenderingControl", "GetVolume",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master"));
	}

	private void setMute(ChannelUID channelUID, Command command) {
		boolean newValue = SamsunTvDataConverters.convertCommandToBooleanValue(
				channelUID, command);

		updateResourceState("RenderingControl", "SetMute",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master", "DesiredMute", Boolean.toString(newValue)));

		updateResourceState("RenderingControl", "GetMute",
				SamsungTvUtils.buildHashMap("InstanceID", "0", "Channel",
						"Master"));

	}

	private void setBrightness(ChannelUID channelUID, Command command) {
		int newValue = SamsunTvDataConverters.convertCommandToIntValue(
				channelUID, command, 0, 100,
				Integer.valueOf(stateMap.get("CurrentBrightness")));

		updateResourceState("RenderingControl", "SetBrightness",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredBrightness", Integer.toString(newValue)));

		updateResourceState("RenderingControl", "GetBrightness",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

	private void setContrast(ChannelUID channelUID, Command command) {
		int newValue = SamsunTvDataConverters.convertCommandToIntValue(
				channelUID, command, 0, 100,
				Integer.valueOf(stateMap.get("CurrentContrast")));

		updateResourceState("RenderingControl", "SetContrast",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredContrast", Integer.toString(newValue)));

		updateResourceState("RenderingControl", "GetContrast",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

	private void setSharpness(ChannelUID channelUID, Command command) {
		int newValue = SamsunTvDataConverters.convertCommandToIntValue(
				channelUID, command, 0, 100,
				Integer.valueOf(stateMap.get("CurrentSharpness")));

		updateResourceState("RenderingControl", "SetSharpness",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredSharpness", Integer.toString(newValue)));

		updateResourceState("RenderingControl", "GetSharpness",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

	private void setColorTemperature(ChannelUID channelUID, Command command) {
		int newValue = SamsunTvDataConverters.convertCommandToIntValue(
				channelUID, command, 0, 4,
				Integer.valueOf(stateMap.get("CurrentColorTemperature")));

		updateResourceState("RenderingControl", "SetColorTemperature",
				SamsungTvUtils.buildHashMap("InstanceID", "0",
						"DesiredColorTemperature", Integer.toString(newValue)));

		updateResourceState("RenderingControl", "GetColorTemperature",
				SamsungTvUtils.buildHashMap("InstanceID", "0"));
	}

}
