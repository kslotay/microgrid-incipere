package sgs.model.objectModels;

import java.util.GregorianCalendar;

import sgs.controller.simulation.Weather;

/**
 * 
 * @author bbreilin
 *
 */
public abstract class AbstractPowerLineModel extends AbstractModel{
	
	
	@Override
	public void updateModel(GregorianCalendar currentTime, Weather weather,
			int resolution) {
		// TODO Auto-generated method stub
		this.updateVariables(currentTime, weather, resolution);
	}

}
