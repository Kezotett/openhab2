/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.discovery;

import static org.openhab.binding.samsungtv.SamsungTvBindingConstants.*;
import static org.openhab.binding.samsungtv.config.SamsungTvConfiguration.UDN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.RemoteDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SamsungTvDiscoveryParticipant} is responsible processing the
 * results of searches for UPnP devices
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvDiscoveryParticipant implements UpnpDiscoveryParticipant {

	private Logger logger = LoggerFactory
			.getLogger(SamsungTvDiscoveryParticipant.class);

	@Override
	public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
		return Collections.singleton(SAMSUNG_TV_AGENT_THING_TYPE);
	}

	@Override
	public DiscoveryResult createResult(RemoteDevice device) {
		ThingUID uid = getThingUID(device);
		if (uid != null) {
			Map<String, Object> properties = new HashMap<>(3);
			String label = "Samsung TV";
			try {
				label = device.getDetails().getModelDetails().getModelName();
			} catch (Exception e) {
				// ignore and use default label
			}
			properties.put(UDN, device.getIdentity().getUdn()
					.getIdentifierString());

			DiscoveryResult result = DiscoveryResultBuilder.create(uid)
					.withProperties(properties).withLabel(label).build();

			logger.debug(
					"Created a DiscoveryResult for device '{}' with UDN '{}'",
					device.getDetails().getFriendlyName(), device.getIdentity()
							.getUdn().getIdentifierString());
			return result;
		} else {
			return null;
		}
	}

	@Override
	public ThingUID getThingUID(RemoteDevice device) {
		if (device != null) {
			if (device.getDetails().getManufacturerDetails().getManufacturer() != null
					&& device.getDetails().getFriendlyName() != null) {
				if (device.getDetails().getManufacturerDetails()
						.getManufacturer().toUpperCase()
						.contains("SAMSUNG ELECTRONICS")) {

					String modelName = device.getDetails().getModelDetails()
							.getModelName();

					// UDN shouldn't contain '-' characters.
					String udn = device.getIdentity().getUdn()
							.getIdentifierString().replace("-", "_");

					if (device.getType().getType().equals("MediaRenderer")) {

						logger.debug(
								"Discovered a Samsung TV model '{}' MediaRenderer thing with UDN '{}'",
								modelName, udn);

						return new ThingUID(
								SAMSUNG_TV_MEDIARENDERER_THING_TYPE, udn);

					} else if (device.getType().getType()
							.equals("RemoteControlReceiver")) {

						logger.debug(
								"Discovered a Samsung TV model '{}' RemoteController thing with UDN '{}'",
								modelName, udn);

						return new ThingUID(
								SAMSUNG_TV_REMOTE_CONTROLLER_THING_TYPE, udn);

					} else if (device.getType().getType()
							.equals("MainTVServer2")) {

						logger.debug(
								"Discovered a Samsung TV model '{}' MainTVServer2 thing with UDN '{}'",
								modelName, udn);

						return new ThingUID(SAMSUNG_TV_AGENT_THING_TYPE, udn);
					}
				}
			}
		}
		return null;
	}
}
