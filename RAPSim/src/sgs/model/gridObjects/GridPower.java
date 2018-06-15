package sgs.model.gridObjects;

import java.util.GregorianCalendar;

import sgs.controller.simulation.ACPowerFlowCalculator;
import sgs.controller.simulation.Weather;
import sgs.model.gridData.GridDataEnum;
import sgs.model.variables.EnumPV;

/**
 * The GridPower object represents the connection to the distribution grid. 
 * It balances the total power of the connected buses and feeds in missing or leads away surplus power. 
 * It is used as the voltage angle reference for the AC-Powerflow algorithm 
 * @author Kristofer Schweiger
 * @param powerProductionOptimal is set to 0 by default, i.e. the grid is balanced. The automatic ColorOverlay uses this. 
 * @see ACPowerFlowCalculator.java
 */
public final class GridPower extends ProSumer {

    //private static final double Default_PowerProduction = Double.POSITIVE_INFINITY; // It only changes if there's Armageddon

    /**
     * Constructor
     */
    public GridPower() {
    	super();
    	variableSet.set(EnumPV.powerProduction, 0.0);
    	variableSet.set(EnumPV.powerProductionOptimal, 0.0);
    }
    
    // ---------------------------------------------------------
    // --- Override --- ----------------------------------------

    @Override
    public void setProductionToWeatherAndTime(GregorianCalendar currentTime, Weather weather) {
    	// not influenced
    	super.setProductionToWeatherAndTime(currentTime, weather);
    }

	@Override
	public GridDataEnum getEnum() {
		return GridDataEnum.GRID_POWER;
	}

}