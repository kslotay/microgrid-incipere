package sgs.model.gridObjects;

import sgs.model.gridData.GridDataEnum;
import sgs.model.objectModels.RandomWindTurbineModel;
import sgs.model.variables.EnumPV;

/**
 * A SmartGridObject.
 * @author tobi, Kristofer Schweiger
 */
public class WindTurbinePowerPlant extends PowerPlant {
    
    /**
     * Constructor
     */
    public WindTurbinePowerPlant() {
    	super();
    	this.setProperties(true, false, EnumPV.powerProductionOptimal);
    	this.objectName = "WindTurbinePowerPlant";
    	this.model = new RandomWindTurbineModel(this);
    }
    
  
	@Override
	public GridDataEnum getEnum() {
		return GridDataEnum.WIND_TURBINE_POWER_PLANT;		
	}

}