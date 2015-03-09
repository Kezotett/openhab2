/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.samsungtv.internal;

import static org.openhab.binding.samsungtv.SamsungTvBindingConstants.*;

import java.util.Collection;

import org.openhab.binding.samsungtv.handler.SamsungTvAgentHandler;
import org.openhab.binding.samsungtv.handler.SamsungTvMediaRendererHandler;
import org.openhab.binding.samsungtv.handler.SamsungTvRemoteControllerHandler;
import org.eclipse.smarthome.config.discovery.DiscoveryServiceRegistry;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.io.transport.upnp.UpnpIOService;

import com.google.common.collect.Lists;

/**
 * The {@link SamsungTvHandlerFactory} is responsible for creating things and
 * thing handlers.
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class SamsungTvHandlerFactory extends BaseThingHandlerFactory {

	private final static Collection<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Lists
			.newArrayList(SAMSUNG_TV_MEDIARENDERER_THING_TYPE,
					SAMSUNG_TV_REMOTE_CONTROLLER_THING_TYPE,
					SAMSUNG_TV_AGENT_THING_TYPE);

	private UpnpIOService upnpIOService;
	private DiscoveryServiceRegistry discoveryServiceRegistry;
	
	@Override
	public boolean supportsThingType(ThingTypeUID thingTypeUID) {
		return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
	}

	@Override
	protected ThingHandler createHandler(Thing thing) {

		ThingTypeUID thingTypeUID = thing.getThingTypeUID();

		if (thingTypeUID.equals(SAMSUNG_TV_MEDIARENDERER_THING_TYPE)) {
			return new SamsungTvMediaRendererHandler(thing, upnpIOService, discoveryServiceRegistry);
		} else if (thingTypeUID.equals(SAMSUNG_TV_REMOTE_CONTROLLER_THING_TYPE)) {
			return new SamsungTvRemoteControllerHandler(thing, upnpIOService, discoveryServiceRegistry);
		} else if (thingTypeUID.equals(SAMSUNG_TV_AGENT_THING_TYPE)) {
			return new SamsungTvAgentHandler(thing, upnpIOService, discoveryServiceRegistry);
		}

		return null;
	}

	protected void setUpnpIOService(UpnpIOService upnpIOService) {
		this.upnpIOService = upnpIOService;
	}

	protected void unsetUpnpIOService(UpnpIOService upnpIOService) {
		this.upnpIOService = null;
	}
	
    protected void setDiscoveryServiceRegistry(DiscoveryServiceRegistry discoveryServiceRegistry) {
        this.discoveryServiceRegistry = discoveryServiceRegistry;
    }
    
    protected void unsetDiscoveryServiceRegistry(DiscoveryServiceRegistry discoveryServiceRegistry) {
    	this.discoveryServiceRegistry = null;
    }
}
