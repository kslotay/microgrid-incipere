package sgs.model.gridObjects;

import java.util.GregorianCalendar;

import sgs.controller.simulation.Weather;


/**
 * An Abstract Class for all Objects, that Transport Energy
 * @author tobi, Kristofer Schweiger
 */
public abstract class PowerTransport extends SmartGridObject {

	/**
	 * Abstract constructor
	 * @param currentPowerConsumption
	 */
	public PowerTransport(){
		super();
	}

    // ---------------------------------------------------------
    // --- Override --- ----------------------------------------

//	@Override
//	public LinkedHashMap<String, String> getDataAsMap() {
//		LinkedHashMap<String, String> map = new LinkedHashMap<>();
//		//map.put("Maximal Energy", String.format("%.2f", maxTransportEnergy));
//		//map.put("Energy Decrement (%)", String.format("%.2f", energyTransportDecrement));
//		map.put("Current Power Flow", String.format("%.2f", currentPowerConsumption));
//		map.put("Resistance R", String.format("%.2f", powerLineResistance.getReal()));
//		map.put("Reactance X", String.format("%.2f", powerLineResistance.getImaginary()));
//		map.put("LineCharge C", String.format("%.2f", powerLineCharge));
//		return map;
//	}

	@Override
	public void setProductionToWeatherAndTime(GregorianCalendar currentTime, Weather weather) {
		//no change at power transport
	}
}