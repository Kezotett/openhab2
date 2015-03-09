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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryServiceRegistry;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOParticipant;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOService;
import org.openhab.binding.samsungtv.internal.SamsungTvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The {@link SamsungTvAgentHandler} is responsible for handling
 * commands, which are sent to one of the channels.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvAgentHandler extends BaseThingHandler implements
		UpnpIOParticipant, DiscoveryListener {

	private Logger logger = LoggerFactory
			.getLogger(SamsungTvAgentHandler.class);

	private UpnpIOService service;
	private DiscoveryServiceRegistry discoveryServiceRegistry;
	private ScheduledFuture<?> pollingJob;

	/**
	 * The default refresh interval when not specified in channel configuration.
	 */
	private static final int DEFAULT_REFRESH_INTERVAL = 10;

	public SamsungTvAgentHandler(Thing thing, UpnpIOService upnpIOService,
			DiscoveryServiceRegistry discoveryServiceRegistry) {
		super(thing);

		logger.debug("Create a Samsung TV Agent Handler for thing '{}'",
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

	private Runnable pollingRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRegistered()) {
				try {

					updateResourceState("MainTVAgent2",
							"GetCurrentMainTVChannel", null);

					updateResourceState("MainTVAgent2",
							"GetCurrentExternalSource", null);

					updateResourceState("MainTVAgent2",
							"GetCurrentContentRecognition", null);

					updateResourceState("MainTVAgent2", "GetCurrentBrowserURL",
							null);
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

			if (channelUID.getId().equals(SOURCENAME)) {
				setSourceName(channelUID, command);
			} else if (channelUID.getId().equals(BROWSER_URL)) {
				setBrowserUrl(channelUID, command);
			} else if (channelUID.getId().equals(STOP_BROWSER)) {
				stopBrowser(channelUID, command);
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

			URL url = service.getDescriptorURL(this);
			logger.debug("url={}", url);
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

		switch (variable) {
		case "ProgramTitle":
			updateState(new ChannelUID(getThing().getUID(), PROGRAM_TITLE),
					(value != null) ? new StringType(value) : UnDefType.UNDEF);
			break;

		case "ChannelName":
			updateState(new ChannelUID(getThing().getUID(), CHANNEL_NAME),
					(value != null) ? new StringType(value) : UnDefType.UNDEF);
			break;

		case "CurrentExternalSource":
			updateState(new ChannelUID(getThing().getUID(), SOURCENAME),
					(value != null) ? new StringType(value) : UnDefType.UNDEF);
			break;

		case "CurrentChannel":
			updateState(new ChannelUID(getThing().getUID(), CHANNEL),
					(value != null) ? new StringType(value) : UnDefType.UNDEF);
			break;

		case "ID":
			updateState(new ChannelUID(getThing().getUID(), SOURCEID),
					(value != null) ? new DecimalType(value) : UnDefType.UNDEF);
			break;

		case "BrowserURL":
			updateState(new ChannelUID(getThing().getUID(), BROWSER_URL),
					(value != null) ? new StringType(value) : UnDefType.UNDEF);
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

	private void setSourceName(ChannelUID channelUID, Command command) {

		Map<String, String> result = updateResourceState("MainTVAgent2",
				"GetSourceList", null);

		String source = command.toString();
		String id = null;

		if (result.get("Result").equals("OK")) {
			String xml = result.get("SourceList");

			Map<String, String> list = parseSourceList(xml);
			if (list != null) {
				id = list.get(source);
			}
		} else {
			logger.error("Source list query failed, result='{}'",
					result.get("Result"));
		}

		if (source != null && id != null) {
			result = updateResourceState("MainTVAgent2", "SetMainTVSource",
					SamsungTvUtils.buildHashMap("Source", source, "ID", id,
							"UiID", "0"));

			if (result.get("Result").equals("OK")) {
				logger.debug("Command succesfully executed");
			} else {
				logger.error("Command execution failed, result='{}'",
						result.get("Result"));
			}
		} else {
			logger.error("Source id for '{}' couldn't be found",
					command.toString());
		}
	}

	private void setBrowserUrl(ChannelUID channelUID, Command command) {

		Map<String, String> result = updateResourceState("MainTVAgent2",
				"RunBrowser",
				SamsungTvUtils.buildHashMap("BrowserURL", command.toString()));

		if (result.get("Result").equals("OK")) {
			logger.debug("Command succesfully executed");
		} else {
			logger.error("Command execution failed, result='{}'",
					result.get("Result"));
		}
	}

	private void stopBrowser(ChannelUID channelUID, Command command) {

		Map<String, String> result = updateResourceState("MainTVAgent2",
				"StopBrowser", null);

		if (result.get("Result").equals("OK")) {
			logger.debug("Command succesfully executed");
		} else {
			logger.error("Command execution failed, result='{}'",
					result.get("Result"));
		}
	}

	private Map<String, String> parseSourceList(String xml) {
		Map<String, String> list = new HashMap<String, String>();

		if (xml != null) {
			Document dom = SamsungTvUtils.loadXMLFromString(xml);

			if (dom != null) {

				NodeList nl = dom.getDocumentElement().getElementsByTagName(
						"Source");

				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {

						String sourceType = null;
						String id = null;

						Element el = (Element) nl.item(i);
						NodeList l = el.getElementsByTagName("SourceType");
						if (l != null && l.getLength() > 0) {
							sourceType = l.item(0).getFirstChild()
									.getNodeValue();
						}
						l = el.getElementsByTagName("ID");
						if (l != null && l.getLength() > 0) {
							id = l.item(0).getFirstChild().getNodeValue();
						}

						if (sourceType != null && id != null) {
							list.put(sourceType, id);
						}
					}
				}
			}

		}

		return list;
	}

	@Override
	public void thingDiscovered(DiscoveryService source, DiscoveryResult result) {
		if (getThing().getConfiguration().get(UDN)
				.equals(result.getProperties().get(UDN))) {
			logger.debug("Setting status for thing '{}' to ONLINE", getThing()
					.getUID());
			getThing().setStatus(ThingStatus.ONLINE);
			onUpdate();
		}
	}

	@Override
	public void thingRemoved(DiscoveryService source, ThingUID thingUID) {
		logger.debug("Setting status for thing '{}' to OFFLINE", getThing()
				.getUID());
		getThing().setStatus(ThingStatus.OFFLINE);
	}
}
