/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nibeheatpump.internal.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.nibeheatpump.internal.models.VariableInformation.NibeDataType;
import org.openhab.binding.nibeheatpump.internal.models.VariableInformation.Type;

/**
 * Class for 
 * 
 * @author Pauli Anttila - Initial contribution
 */
public class F750 {
	
	@SuppressWarnings("serial")
	private static final Map<Integer, VariableInformation> VARIABLE_INFO_F750 = 
	Collections.unmodifiableMap(new HashMap<Integer, VariableInformation>() {{
		put(40004, new VariableInformation(10,	"BT1 Outdoor temp",								NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40005, new VariableInformation(10,	"EB23-BT2 Supply temp S4",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40006, new VariableInformation(10,	"EB22-BT2 Supply temp S3",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40007, new VariableInformation(10,	"EB21-BT2 Supply temp S2",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40008, new VariableInformation(10,	"BT2 Supply temp S1",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40012, new VariableInformation(10,	"EB100-EP14-BT3 Return temp",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40013, new VariableInformation(10,	"BT7 Hot Water top",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40014, new VariableInformation(10,	"BT6 Hot Water load",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40017, new VariableInformation(10,	"EB100-EP14-BT12 Cond. out",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40018, new VariableInformation(10,	"EB100-EP14-BT14 Hot gas temp",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40019, new VariableInformation(10,	"EB100-EP14-BT15 Liquid line",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40020, new VariableInformation(10,	"EB100-BT16 Evaporator temp",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40022, new VariableInformation(10,	"EB100-EP14-BT17 Suction",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40025, new VariableInformation(10,	"EB100-BT20 Exhaust air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40026, new VariableInformation(10,	"EB100-BT21 Vented air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40030, new VariableInformation(10,	"EB23-BT50 Room Temp S4",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40031, new VariableInformation(10,	"EB22-BT50 Room Temp S3",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40032, new VariableInformation(10,	"EB21-BT50 Room Temp S2",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40033, new VariableInformation(10,	"BT50 Room Temp S1",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40045, new VariableInformation(10,	"EQ1-BT64 PCS4 Supply Temp",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40047, new VariableInformation(10,	"EB100-BT61 Supply Radiator Temp",				NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40048, new VariableInformation(10,	"EB100-BT62 Return Radiator Temp",				NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40050, new VariableInformation(10,	"EB100-BS1 Air flow",							NibeDataType.S16,	Type.Settings));	// Unit: 
		put(40051, new VariableInformation(100,	"EB100-BS1 Air flow unfiltered",				NibeDataType.S16,	Type.Settings));	// Unit: 
		put(40054, new VariableInformation(1,	"EB100-FD1 Temperature limiter",				NibeDataType.S16,	Type.Settings));	// Unit: 
		put(40067, new VariableInformation(10,	"BT1 Average",									NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40071, new VariableInformation(10,	"BT25 external supply temp",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40072, new VariableInformation(10,	"BF1 Flow",										NibeDataType.S16,	Type.Settings));	// Unit: l/m
		put(40074, new VariableInformation(1,	"EB100-FR1 Anode Status",						NibeDataType.S16,	Type.Settings));	// Unit: 
		put(40077, new VariableInformation(10,	"BT6 external water heater load temp.",			NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40078, new VariableInformation(10,	"BT7 external water heater top temp.",			NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40079, new VariableInformation(10,	"EB100-BE3 Current Phase 3",					NibeDataType.S32,	Type.Settings));	// Unit: A
		put(40081, new VariableInformation(10,	"EB100-BE2 Current Phase 2",					NibeDataType.S32,	Type.Settings));	// Unit: A
		put(40083, new VariableInformation(10,	"EB100-BE1 Current Phase 1",					NibeDataType.S32,	Type.Settings));	// Unit: A
		put(40107, new VariableInformation(10,	"EB100-BT20 Exhaust air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40108, new VariableInformation(10,	"EB100-BT20 Exhaust air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40109, new VariableInformation(10,	"EB100-BT20 Exhaust air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40110, new VariableInformation(10,	"EB100-BT21 Vented air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40111, new VariableInformation(10,	"EB100-BT21 Vented air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40112, new VariableInformation(10,	"EB100-BT21 Vented air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40127, new VariableInformation(10,	"EB23-BT3 Return temp S4",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40128, new VariableInformation(10,	"EB22-BT3 Return temp S3",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40129, new VariableInformation(10,	"EB21-BT3 Return temp S2",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40141, new VariableInformation(10,	"AZ2-BT22 Supply air temp. SAM",				NibeDataType.S16,	Type.Settings));	// Unit: ºC
		put(40142, new VariableInformation(10,	"AZ2-BT23 Outdoor temp. SAM",					NibeDataType.S16,	Type.Settings));	// Unit: ºC
		put(40143, new VariableInformation(10,	"AZ2-BT68 Flow temp. SAM",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40144, new VariableInformation(10,	"AZ2-BT69 Return temp. SAM",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40157, new VariableInformation(10,	"EP30-BT53 Solar Panel Temp",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(40158, new VariableInformation(10,	"EP30-BT54 Solar Load Temp",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(43001, new VariableInformation(1,	"Software version",								NibeDataType.U16,	Type.Settings));	// Unit: 
		put(43005, new VariableInformation(10,	"Degree Minutes",								NibeDataType.S16,	Type.Settings));	// Unit: 
		put(43006, new VariableInformation(10,	"Calculated Supply Temperature S4",				NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(43007, new VariableInformation(10,	"Calculated Supply Temperature S3",				NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(43008, new VariableInformation(10,	"Calculated Supply Temperature S2",				NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(43009, new VariableInformation(10,	"Calculated Supply Temperature S1",				NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(43013, new VariableInformation(1,	"Freeze Protection Status",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43061, new VariableInformation(1,	"t. after start timer",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43062, new VariableInformation(1,	"t. after mode change",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43064, new VariableInformation(10,	"HMF dT set.",									NibeDataType.S16,	Type.Settings));	// Unit: 
		put(43065, new VariableInformation(10,	"HMF dT act.",									NibeDataType.S16,	Type.Settings));	// Unit: 
		put(43081, new VariableInformation(10,	"Tot. op.time add.",							NibeDataType.S32,	Type.Settings));	// Unit: h
		put(43084, new VariableInformation(100,	"Int. el.add. Power",							NibeDataType.S16,	Type.Settings));	// Unit: kW
		put(43086, new VariableInformation(1,	"Prio",											NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43091, new VariableInformation(1,	"Int. el.add. State",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43108, new VariableInformation(1,	"Fan speed current",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(43122, new VariableInformation(1,	"Compr. current min.freq.",						NibeDataType.S16,	Type.Settings));	// Unit: Hz
		put(43123, new VariableInformation(1,	"Compr. current max.freq.",						NibeDataType.S16,	Type.Settings));	// Unit: Hz
		put(43124, new VariableInformation(10,	"Airflow ref.",									NibeDataType.S16,	Type.Settings));	// Unit: 
		put(43132, new VariableInformation(1,	"Inverter com. timer",							NibeDataType.U16,	Type.Settings));	// Unit: sec
		put(43133, new VariableInformation(1,	"Inverter drive status",						NibeDataType.U16,	Type.Settings));	// Unit: 
		put(43136, new VariableInformation(10,	"Compr. current freq.",							NibeDataType.U16,	Type.Settings));	// Unit: Hz
		put(43137, new VariableInformation(1,	"Inverter alarm code",							NibeDataType.U16,	Type.Settings));	// Unit: 
		put(43138, new VariableInformation(1,	"Inverter fault code",							NibeDataType.U16,	Type.Settings));	// Unit: 
		put(43140, new VariableInformation(10,	"compr. temp.",									NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(43141, new VariableInformation(1,	"compr. in power",								NibeDataType.U16,	Type.Settings));	// Unit: W
		put(43144, new VariableInformation(100,	"Compr. energy total",							NibeDataType.U32,	Type.Settings));	// Unit: kWh
		put(43147, new VariableInformation(1,	"Compr. in current",							NibeDataType.S16,	Type.Settings));	// Unit: A
		put(43181, new VariableInformation(1,	"Chargepump speed",								NibeDataType.S16,	Type.Settings));	// Unit: 
		put(43182, new VariableInformation(1,	"Compr. freq. setpoint",						NibeDataType.U16,	Type.Settings));	// Unit: Hz
		put(43239, new VariableInformation(10,	"Tot. HW op.time add.",							NibeDataType.S32,	Type.Settings));	// Unit: h
		put(43305, new VariableInformation(100,	"Compr. energy HW",								NibeDataType.U32,	Type.Settings));	// Unit: kWh
		put(43375, new VariableInformation(1,	"compr. in power mean",							NibeDataType.S16,	Type.Settings));	// Unit: W
		put(43382, new VariableInformation(1,	"Inverter mem error code",						NibeDataType.U16,	Type.Settings));	// Unit: 
		put(43416, new VariableInformation(1,	"Compressor starts EB100-EP14",					NibeDataType.S32,	Type.Settings));	// Unit: 
		put(43420, new VariableInformation(1,	"Tot. op.time compr. EB100-EP14",				NibeDataType.S32,	Type.Settings));	// Unit: h
		put(43424, new VariableInformation(1,	"Tot. HW op.time compr. EB100-EP14",			NibeDataType.S32,	Type.Settings));	// Unit: h
		put(43427, new VariableInformation(1,	"Compressor State EP14",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43435, new VariableInformation(1,	"Compressor status EP14",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43437, new VariableInformation(1,	"HM-pump Status EP14",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43514, new VariableInformation(1,	"PCA-Base Relays EP14",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43516, new VariableInformation(1,	"PCA-Power Relays EP14",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(43542, new VariableInformation(10,	"Calculated supply air temp.",					NibeDataType.S16,	Type.Settings));	// Unit: ºC
		put(44258, new VariableInformation(1,	"External supply air accessory relays",			NibeDataType.U8,	Type.Settings));	// Unit: 
		put(44267, new VariableInformation(10,	"Calc. Cooling Supply Temperature S4",			NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(44268, new VariableInformation(10,	"Calc. Cooling Supply Temperature S3",			NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(44269, new VariableInformation(10,	"Calc. Cooling Supply Temperature S2",			NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(44270, new VariableInformation(10,	"Calc. Cooling Supply Temperature S1",			NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(44317, new VariableInformation(1,	"SCA accessory relays",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(44331, new VariableInformation(1,	"Software release",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(45001, new VariableInformation(1,	"Alarm number",									NibeDataType.S16,	Type.Settings));	// Unit: 
		put(47062, new VariableInformation(10,	"HW charge offset",								NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47291, new VariableInformation(1,	"Floor drying timer",							NibeDataType.U16,	Type.Settings));	// Unit: hrs
		put(47004, new VariableInformation(1,	"Heat curve S4",								NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47005, new VariableInformation(1,	"Heat curve S3",								NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47006, new VariableInformation(1,	"Heat curve S2",								NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47007, new VariableInformation(1,	"Heat curve S1",								NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47008, new VariableInformation(1,	"Offset S4",									NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47009, new VariableInformation(1,	"Offset S3",									NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47010, new VariableInformation(1,	"Offset S2",									NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47011, new VariableInformation(1,	"Offset S1",									NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47012, new VariableInformation(10,	"Min Supply System 4",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47013, new VariableInformation(10,	"Min Supply System 3",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47014, new VariableInformation(10,	"Min Supply System 2",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47015, new VariableInformation(10,	"Min Supply System 1",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47016, new VariableInformation(10,	"Max Supply System 4",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47017, new VariableInformation(10,	"Max Supply System 3",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47018, new VariableInformation(10,	"Max Supply System 2",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47019, new VariableInformation(10,	"Max Supply System 1",							NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47020, new VariableInformation(1,	"Own Curve P7",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47021, new VariableInformation(1,	"Own Curve P6",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47022, new VariableInformation(1,	"Own Curve P5",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47023, new VariableInformation(1,	"Own Curve P4",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47024, new VariableInformation(1,	"Own Curve P3",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47025, new VariableInformation(1,	"Own Curve P2",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47026, new VariableInformation(1,	"Own Curve P1",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47027, new VariableInformation(1,	"Point offset outdoor temp.",					NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47028, new VariableInformation(1,	"Point offset",									NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47029, new VariableInformation(1,	"External adjustment S4",						NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47030, new VariableInformation(1,	"External adjustment S3",						NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47031, new VariableInformation(1,	"External adjustment S2",						NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47032, new VariableInformation(1,	"External adjustment S1",						NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47033, new VariableInformation(10,	"External adjustment with room sensor S4",		NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47034, new VariableInformation(10,	"External adjustment with room sensor S3",		NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47035, new VariableInformation(10,	"External adjustment with room sensor S2",		NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47036, new VariableInformation(10,	"External adjustment with room sensor S1",		NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47041, new VariableInformation(1,	"Hot water mode",								NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47043, new VariableInformation(10,	"Start temperature HW Luxury",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47044, new VariableInformation(10,	"Start temperature HW Normal",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47045, new VariableInformation(10,	"Start temperature HW Economy",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47046, new VariableInformation(10,	"Stop temperature Periodic HW",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47047, new VariableInformation(10,	"Stop temperature HW Luxury",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47048, new VariableInformation(10,	"Stop temperature HW Normal",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47049, new VariableInformation(10,	"Stop temperature HW Economy",					NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47050, new VariableInformation(1,	"Periodic HW",									NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47051, new VariableInformation(1,	"Periodic HW Interval",							NibeDataType.S8,	Type.Settings));	// Unit: days
		put(47054, new VariableInformation(1,	"Run time HWC",									NibeDataType.S8,	Type.Settings));	// Unit: min
		put(47055, new VariableInformation(1,	"Still time HWC",								NibeDataType.S8,	Type.Settings));	// Unit: min
		put(47092, new VariableInformation(1,	"Manual compfreq HW",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47093, new VariableInformation(1,	"Manual compfreq speed HW",						NibeDataType.U16,	Type.Settings));	// Unit: Hz
		put(47094, new VariableInformation(1,	"Sec per compfreq step",						NibeDataType.U8,	Type.Settings));	// Unit: s
		put(47095, new VariableInformation(1,	"Max compfreq step",							NibeDataType.U8,	Type.Settings));	// Unit: Hz
		put(47096, new VariableInformation(1,	"Manual compfreq Heating",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47097, new VariableInformation(1,	"Min speed after start",						NibeDataType.U8,	Type.Settings));	// Unit: Min
		put(47098, new VariableInformation(1,	"Min speed after HW",							NibeDataType.U8,	Type.Settings));	// Unit: Min
		put(47099, new VariableInformation(1,	"GMz",											NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47100, new VariableInformation(10,	"Max diff VBF-BerVBF",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47101, new VariableInformation(1,	"Comp freq reg P",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47102, new VariableInformation(1,	"Comp freq max delta F",						NibeDataType.S8,	Type.Settings));	// Unit: Hz
		put(47103, new VariableInformation(1,	"Min comp freq",								NibeDataType.S16,	Type.Settings));	// Unit: Hz
		put(47104, new VariableInformation(1,	"Max comp freq",								NibeDataType.S16,	Type.Settings));	// Unit: Hz
		put(47105, new VariableInformation(1,	"Comp freq heating",							NibeDataType.S16,	Type.Settings));	// Unit: Hz
		put(47131, new VariableInformation(1,	"Language",										NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47134, new VariableInformation(1,	"Period HW",									NibeDataType.U8,	Type.Settings));	// Unit: min
		put(47135, new VariableInformation(1,	"Period Heat",									NibeDataType.U8,	Type.Settings));	// Unit: min
		put(47136, new VariableInformation(1,	"Period Pool",									NibeDataType.U8,	Type.Settings));	// Unit: min
		put(47138, new VariableInformation(1,	"Operational mode heat medium pump",			NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47206, new VariableInformation(1,	"DM start heating",								NibeDataType.S16,	Type.Settings));	// Unit: 
		put(47207, new VariableInformation(1,	"DM start cooling",								NibeDataType.S16,	Type.Settings));	// Unit: 
		put(47208, new VariableInformation(1,	"DM start add.",								NibeDataType.S16,	Type.Settings));	// Unit: 
		put(47209, new VariableInformation(1,	"DM between add. steps",						NibeDataType.S16,	Type.Settings));	// Unit: 
		put(47210, new VariableInformation(1,	"DM start add. with shunt",						NibeDataType.S16,	Type.Settings));	// Unit: 
		put(47212, new VariableInformation(100,	"Max int add. power",							NibeDataType.S16,	Type.Settings));	// Unit: kW
		put(47214, new VariableInformation(1,	"Fuse",											NibeDataType.U8,	Type.Settings));	// Unit: A
		put(47261, new VariableInformation(1,	"Exhaust Fan speed 4",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47262, new VariableInformation(1,	"Exhaust Fan speed 3",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47263, new VariableInformation(1,	"Exhaust Fan speed 2",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47264, new VariableInformation(1,	"Exhaust Fan speed 1",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47265, new VariableInformation(1,	"Exhaust Fan speed normal",						NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47266, new VariableInformation(1,	"Supply Fan speed 4",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47267, new VariableInformation(1,	"Supply Fan speed 3",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47268, new VariableInformation(1,	"Supply Fan speed 2",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47269, new VariableInformation(1,	"Supply Fan speed 1",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47270, new VariableInformation(1,	"Supply Fan speed normal",						NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47271, new VariableInformation(1,	"Fan return time 4",							NibeDataType.U8,	Type.Settings));	// Unit: h
		put(47272, new VariableInformation(1,	"Fan return time 3",							NibeDataType.U8,	Type.Settings));	// Unit: h
		put(47273, new VariableInformation(1,	"Fan return time 2",							NibeDataType.U8,	Type.Settings));	// Unit: h
		put(47274, new VariableInformation(1,	"Fan return time 1",							NibeDataType.U8,	Type.Settings));	// Unit: h
		put(47275, new VariableInformation(1,	"Filter Reminder period",						NibeDataType.U8,	Type.Settings));	// Unit: Months
		put(47276, new VariableInformation(1,	"Floor drying",									NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47277, new VariableInformation(1,	"Floor drying period 7",						NibeDataType.U8,	Type.Settings));	// Unit: days
		put(47278, new VariableInformation(1,	"Floor drying period 6",						NibeDataType.U8,	Type.Settings));	// Unit: days
		put(47279, new VariableInformation(1,	"Floor drying period 5",						NibeDataType.U8,	Type.Settings));	// Unit: days
		put(47280, new VariableInformation(1,	"Floor drying period 4",						NibeDataType.U8,	Type.Settings));	// Unit: days
		put(47281, new VariableInformation(1,	"Floor drying period 3",						NibeDataType.U8,	Type.Settings));	// Unit: days
		put(47282, new VariableInformation(1,	"Floor drying period 2",						NibeDataType.U8,	Type.Settings));	// Unit: days
		put(47283, new VariableInformation(1,	"Floor drying period 1",						NibeDataType.U8,	Type.Settings));	// Unit: days
		put(47284, new VariableInformation(1,	"Floor drying temp. 7",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47285, new VariableInformation(1,	"Floor drying temp. 6",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47286, new VariableInformation(1,	"Floor drying temp. 5",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47287, new VariableInformation(1,	"Floor drying temp. 4",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47288, new VariableInformation(1,	"Floor drying temp. 3",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47289, new VariableInformation(1,	"Floor drying temp. 2",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47290, new VariableInformation(1,	"Floor drying temp. 1",							NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47294, new VariableInformation(1,	"Use airflow defrost",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47295, new VariableInformation(1,	"Airflow reduction trig",						NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47296, new VariableInformation(1,	"Airflow defrost done",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47297, new VariableInformation(1,	"Initiate inverter",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47298, new VariableInformation(1,	"Force inverter init",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47299, new VariableInformation(1,	"Min time defrost",								NibeDataType.U8,	Type.Settings));	// Unit: min
		put(47300, new VariableInformation(10,	"DOT",											NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47301, new VariableInformation(10,	"delta T at DOT",								NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47302, new VariableInformation(1,	"Climate system 2 accessory",					NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47303, new VariableInformation(1,	"Climate system 3 accessory",					NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47304, new VariableInformation(1,	"Climate system 4 accessory",					NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47305, new VariableInformation(10,	"Climate system 4 mixing valve amp.",			NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47306, new VariableInformation(10,	"Climate system 3 mixing valve amp.",			NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47307, new VariableInformation(10,	"Climate system 2 mixing valve amp.",			NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47308, new VariableInformation(10,	"Climate system 4 shunt wait",					NibeDataType.S16,	Type.Settings));	// Unit: secs
		put(47309, new VariableInformation(10,	"Climate system 3 shunt wait",					NibeDataType.S16,	Type.Settings));	// Unit: secs
		put(47310, new VariableInformation(10,	"Climate system 2 shunt wait",					NibeDataType.S16,	Type.Settings));	// Unit: secs
		put(47317, new VariableInformation(1,	"Shunt controlled add. accessory",				NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47318, new VariableInformation(1,	"Shunt controlled add. min. temp.",				NibeDataType.S8,	Type.Settings));	// Unit: °C
		put(47319, new VariableInformation(1,	"Shunt controlled add. min. runtime",			NibeDataType.U8,	Type.Settings));	// Unit: hrs
		put(47320, new VariableInformation(10,	"Shunt controlled add. mixing valve amp.",		NibeDataType.S8,	Type.Settings));	// Unit: 
		put(47321, new VariableInformation(1,	"Shunt controlled add. mixing valve wait",		NibeDataType.S16,	Type.Settings));	// Unit: secs
		put(47352, new VariableInformation(1,	"SMS40 accessory",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47370, new VariableInformation(1,	"Allow Additive Heating",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47371, new VariableInformation(1,	"Allow Heating",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47372, new VariableInformation(1,	"Allow Cooling",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47378, new VariableInformation(10,	"Max diff. comp.",								NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47379, new VariableInformation(10,	"Max diff. add.",								NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47384, new VariableInformation(1,	"Date format",									NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47385, new VariableInformation(1,	"Time format",									NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47387, new VariableInformation(1,	"HW production",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47388, new VariableInformation(1,	"Alarm lower room temp.",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47389, new VariableInformation(1,	"Alarm lower HW temp.",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47391, new VariableInformation(1,	"Use room sensor S4",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47392, new VariableInformation(1,	"Use room sensor S3",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47393, new VariableInformation(1,	"Use room sensor S2",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47394, new VariableInformation(1,	"Use room sensor S1",							NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47395, new VariableInformation(10,	"Room sensor setpoint S4",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47396, new VariableInformation(10,	"Room sensor setpoint S3",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47397, new VariableInformation(10,	"Room sensor setpoint S2",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47398, new VariableInformation(10,	"Room sensor setpoint S1",						NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(47399, new VariableInformation(10,	"Room sensor factor S4",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47400, new VariableInformation(10,	"Room sensor factor S3",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47401, new VariableInformation(10,	"Room sensor factor S2",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47402, new VariableInformation(10,	"Room sensor factor S1",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47415, new VariableInformation(1,	"Speed circ.pump Pool",							NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47417, new VariableInformation(1,	"Speed circ.pump Cooling",						NibeDataType.U8,	Type.Settings));	// Unit: %
		put(47442, new VariableInformation(1,	"preset flow clim. sys.",						NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47473, new VariableInformation(1,	"Max time defrost",								NibeDataType.U8,	Type.Settings));	// Unit: min
		put(47537, new VariableInformation(1,	"Night cooling",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47538, new VariableInformation(1,	"Start room temp. night cooling",				NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47539, new VariableInformation(1,	"Night Cooling Min. diff.",						NibeDataType.U8,	Type.Settings));	// Unit: °C
		put(47555, new VariableInformation(1,	"DEW accessory",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(47570, new VariableInformation(1,	"Operational mode",								NibeDataType.U8,	Type.Settings));	// Unit: 
		put(48134, new VariableInformation(1,	"Operational mode charge pump",					NibeDataType.U8,	Type.Settings));	// Unit: 
		put(48158, new VariableInformation(10,	"SAM supply air curve: outdoor temp T3",		NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(48159, new VariableInformation(10,	"SAM supply air curve: outdoor temp T2",		NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(48160, new VariableInformation(10,	"SAM supply air curve: outdoor temp T1",		NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(48161, new VariableInformation(10,	"SAM supply air curve: supply air temp at T3",	NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(48162, new VariableInformation(10,	"SAM supply air curve: supply air temp at T2",	NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(48163, new VariableInformation(10,	"SAM supply air curve: supply air temp at T1",	NibeDataType.S16,	Type.Settings));	// Unit: °C
		put(48201, new VariableInformation(1,	"SCA accessory",								NibeDataType.U8,	Type.Settings));	// Unit: 
	}});

	public static VariableInformation getVariableInfo(int key) {
		return VARIABLE_INFO_F750.get(key);
	}
}
