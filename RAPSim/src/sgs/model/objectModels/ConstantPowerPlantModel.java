package sgs.model.objectModels;

import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.CustomPowerPlant;

/**
 * 
 * @author bbreilin
 *
 */

public class ConstantPowerPlantModel extends AbstractCustomPowerPlantModel{
	
	public ConstantPowerPlantModel( CustomPowerPlant powerPlant) {
		super(powerPlant);
// public class ConstantPowerPlantModel extends AbstractFossilFuelPowerPlantModel{
// public ConstantPowerPlantModel(FossilFuelPowerPlant powerPlant) {		super(powerPlant);
//		initVariableSet();
		icon = new ImageIcon("Data2/GeothermalPowerPlant_ICON.png");
		modelName = "ConstantPowerPlantModel";
		description = "Constant generation on peak power. ";
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateVariables(GregorianCalendar currentTime,
			Weather weather, int resolution) {
		this.setPowerProduction(this.getRatedPower()); // 
		
//		powerPlant.setPowerProduction(this.powerProduction.getValueNumeric());
//		powerPlant.setPeakPower(this.ratedPower.getValueNumeric());

	}

	@Override
	protected void initVariableSet() {
		this.ratedPower.properties.set(true, true);
		this.powerProduction.properties.set(false, false);
	}

}
