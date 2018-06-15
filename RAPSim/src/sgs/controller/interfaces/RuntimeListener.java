package sgs.controller.interfaces;

import java.util.GregorianCalendar;

import sgs.controller.simulation.Weather;

/**
 * watch over the interface
 *
 */
public interface RuntimeListener {
	
	/**
	 * call, tells that current Weather and Time has Changed
	 * @param currentWeather
	 * @param currentTime
	 */
	public void updateDateAndTime(Weather currentWeather, GregorianCalendar currentTime);
	
	
	
}
