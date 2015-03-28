/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.handler;

import static org.openhab.binding.samsungtv.config.SamsungTvConfiguration.*;
import static org.openhab.binding.samsungtv.SamsungTvBindingConstants.*;

import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryServiceRegistry;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOParticipant;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOService;
import org.openhab.binding.samsungtv.config.SamsungTvConfiguration;
import org.openhab.binding.samsungtv.protocol.SamsungTvException;
import org.openhab.binding.samsungtv.protocol.KeyCode;
import org.openhab.binding.samsungtv.protocol.SamsungTvRemoteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SamsungTvRemoteControllerHandler} is responsible for handling
 * commands, which are sent to one of the channels.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvRemoteControllerHandler extends BaseThingHandler
		implements UpnpIOParticipant, DiscoveryListener {

	private Logger logger = LoggerFactory
			.getLogger(SamsungTvRemoteControllerHandler.class);

	private UpnpIOService service;
	private DiscoveryServiceRegistry discoveryServiceRegistry;
	private SamsungTvConfiguration configuration = null;
	
	public SamsungTvRemoteControllerHandler(Thing thing,
			UpnpIOService upnpIOService,
			DiscoveryServiceRegistry discoveryServiceRegistry) {
		super(thing);

		logger.debug(
				"Create a Samsung TV Remote Controller Handler for thing '{}'",
				getThing().getUID());

		if (upnpIOService != null) {
			service = upnpIOService;
		} else {
			logger.debug("upnpIOService not set.");
		}
		
		if (discoveryServiceRegistry != null) {
			this.discoveryServiceRegistry = discoveryServiceRegistry;
			this.discoveryServiceRegistry.addDiscoveryListener(this);
		}
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {
		logger.debug("Received channel: {}, command: {}", channelUID, command);

		KeyCode key = null;

		switch (channelUID.getId()) {
		case KEY_CODE:
			if (command instanceof StringType) {

				try {
					key = KeyCode.valueOf(command.toString());
				} catch (Exception e) {

					try {
						key = KeyCode.valueOf("KEY_" + command.toString());
					} catch (Exception e2) {
						// do nothing, error message is logged later
					}
				}
			}
			break;

		default:
			if (command instanceof OnOffType) {

				if (command.equals(OnOffType.ON)) {
					try {
						key = KeyCode.valueOf(channelUID.getId());
					} catch (Exception e) {
						// do nothing, error message is logged later
					}
				}
			}
		}

		if (key != null) {
			sendKeyCode(key);
		} else {
			logger.error("Command '{}' not supported for channel '{}'",
					command, channelUID.getId());
		}
	}

	@Override
	public void initialize() {
		configuration = getConfigAs(SamsungTvConfiguration.class);

		if (configuration.udn != null) {
			logger.debug("Initializing Samsung TV handler for UDN '{}'",
					configuration.udn);
		} else if (configuration.networkAddress != null) {
			logger.debug("Initializing Samsung TV handler for address '{}'",
					configuration.networkAddress);
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

		if (getThing().getStatus() == ThingStatus.ONLINE) {
			logger.debug("Setting status for thing '{}' to OFFLINE", getThing()
					.getUID());
			getThing().setStatus(ThingStatus.OFFLINE);
		}
	}

	/**
	 * Sends a command to Samsung TV device.
	 * 
	 * @param key
	 *            Button code to send
	 */
	private void sendKeyCode(final KeyCode key) {

		String host = null;
		
		if (configuration.udn != null && !configuration.udn.isEmpty()) {
			host = service.getDescriptorURL(this).getHost();
		} else {
			host = configuration.networkAddress;
		}

		if (host != null) {

			SamsungTvRemoteController remoteController = new SamsungTvRemoteController(
					host, configuration.port, "openHAB2", "openHAB2");

			if (remoteController != null) {
				logger.debug("Try to send command: {}", key);

				try {
					remoteController.sendKey(key);

				} catch (SamsungTvException e) {
					logger.error("Could not send command to device on {}: {}",
							host + ":" + configuration.port, e);
				}
			}
		} else {
			logger.error("TV network address not defined");
		}
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
	}
	
	@Override
	public void thingDiscovered(DiscoveryService source, DiscoveryResult result) {
		if (getThing().getConfiguration().get(UDN)
				.equals(result.getProperties().get(UDN))) {
			logger.debug("Setting status for thing '{}' to ONLINE", getThing()
					.getUID());
			getThing().setStatus(ThingStatus.ONLINE);
		}
	}

	@Override
	public void thingRemoved(DiscoveryService source, ThingUID thingUID) {
		logger.debug("Setting status for thing '{}' to OFFLINE", getThing()
				.getUID());
		getThing().setStatus(ThingStatus.OFFLINE);
	}
}
