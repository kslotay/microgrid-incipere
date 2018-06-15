package sgs.model.gridObjects;

import sgs.model.gridData.GridDataEnum;
import sgs.model.objectModels.SolarPeakPowerModel;
import sgs.model.variables.EnumPV;
/**
 * 
 * @author bbreilin
 *
 */
public class SolarPowerPlant extends PowerPlant {
	
  /**
     * Constructor
     */
	public SolarPowerPlant() {
    	super();
    	this.setProperties(true, false, EnumPV.powerProductionOptimal);
    	this.objectName = "SolarPowerPlant";
    	this.model = new SolarPeakPowerModel(this);
    }
    
	@Override
	public GridDataEnum getEnum() {
		return GridDataEnum.SOLAR_POWER_PLANT;
	}

}
