package sgs.model.objectModels;

import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.FossilFuelPowerPlant;
import sgs.model.variables.EnumUnit;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;


/**
 * 
 * @author mpoechac
 *
 */

public class FossilFuelModel extends AbstractFossilFuelPowerPlantModel{
	
//	private SingleVariable tankSize;
	private final double energyDensity = 10.0 ; //  kWh per liter, value for gasoline/diesel
	private SingleVariable tankLevel;
	private SingleVariable tankSize;

	
	public FossilFuelModel( FossilFuelPowerPlant powerPlant ){
		super(powerPlant);
//		this.initVariableSet();
		icon = new ImageIcon("Data2/FossilFuelPowerPlant_ICON.png");
		modelName = "FossilFuels";
		description = "The tanklevel is calculated according the consumed energy engine runs only full power. The Energydensity fits for Diesel i.e. 36 MJ/l = 10kWh/l. Refilling needs to be done by hand.";
		
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateVariables(GregorianCalendar currentTime,
			Weather weather, int resolution) {

//		double energyPerTimeStep =  powerPlant.getPowerProduction().getValueDouble() /1000 * resolution/60;
		double energyPerTimeStep =  powerPlant.getPeakPower().getValueDouble() /1000 * resolution/60;
		double energyInTank = energyDensity * this.tankLevel.getValueDouble() * this.tankSize.getValueDouble(); // in  kWh
				
		// set Production to the Level needed from the Grid
//		powerPlant.setPowerProduction( powerPlant.getVariableSet().get(EnumPV.powerProductionNeeded).getValueNumeric() );
		
//		TODO improve model for last time step
		if (energyInTank > 0.0) {
//			this.powerProduction = powerPlant.getPeakPower();
			energyInTank = energyInTank - 100*energyPerTimeStep;
			tankLevel.setValue( energyInTank/energyDensity / this.tankSize.getValueDouble()  );
		} else {
			this.setPowerProduction( new NumericValue(0.0) );
		}
		
//		this.powerPlant.variableSet.get(EnumPV.powerProduction).getValueNumeric().setReal(this.powerProduction.getReal());
//		this.setPeakPower( this.peakPower.getValueNumeric().roundValue(4) );
		
		this.setPowerProduction( this.ratedPower.getValueNumeric() );
//		write the values to the object
//		powerPlant.setPowerProduction( this.powerProduction.getValueNumeric() );
//		powerPlant.setPeakPower( this.ratedPower.getValueNumeric() );
	}

	@Override
	protected void initVariableSet() {
		
		this.setRatedPower(new NumericValue( 500.0 ));
		powerPlant.setPowerProduction( this.ratedPower.getValueDouble()  );
		
		this.tankSize = new SingleVariable("tankSize", new NumericValue(10), EnumUnit.liter );
		this.tankSize.properties.set(true,true);
		this.variableSet.add( tankSize );
		
		this.tankLevel = new SingleVariable("tankLevel", new NumericValue(100), EnumUnit.percent );
		this.tankLevel.properties.set(true,true);
		this.variableSet.add( tankLevel );
//		this.initVariable("energyDensity", new NumericValue(10), EnumUnit.liter ,true ,false);
	
	}

}
