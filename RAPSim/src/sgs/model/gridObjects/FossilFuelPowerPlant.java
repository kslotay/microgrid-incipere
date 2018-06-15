package sgs.model.gridObjects;

import sgs.model.gridData.GridDataEnum;

/**
 * A SmartGridObject.
 * @author tobi, Kristofer Schweiger
 */
public final class FossilFuelPowerPlant extends PowerPlant {

//    private static final double Default_PowerProduction 	= 2.0; // It only changes if there's Armageddon

    /**
     * Constructor
     */
    public FossilFuelPowerPlant() {
    	super();
    	this.objectName = "FossilFuelPowerPlant";
//    	this.model = new FossilFuelModel(this);
//    	setPowerProduction(Default_PowerProduction);		//variableSet.set(EnumPV.powerProduction, Default_PowerProduction);
//    	setPeakPower(new NumericValue(FossilFuelPowerPlant.Default_PowerProduction));
    }
    
    // ---------------------------------------------------------
    // --- Override --- ----------------------------------------

//    @Override
//    public void setProductionToWeatherAndTime(GregorianCalendar currentTime, Weather weather) {
//    	// not influenced
//    	super.setProductionToWeatherAndTime(currentTime, weather);
//    }

	@Override
	public GridDataEnum getEnum() {
		return GridDataEnum.FOSSIL_FUEL_POWER_PLANT;
	}

}