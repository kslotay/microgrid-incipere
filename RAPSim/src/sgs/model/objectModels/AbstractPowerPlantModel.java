package sgs.model.objectModels;

import java.util.GregorianCalendar;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.PowerPlant;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;
/**
 * Initializes the variableSet. 
 * Initializes the variables powerProductionOptimal and powerProduction with values from the Object. 
 * Handles the update of the object variables (rounded to 4 digits) and provides set- and get-methods for the two variables.  
 * @author bbreilin, Poechacker
 *
 */
public abstract class AbstractPowerPlantModel extends AbstractModel{

	protected SingleVariable ratedPower;
	protected SingleVariable powerProduction;
	protected PowerPlant powerPlant;
	
	public AbstractPowerPlantModel(PowerPlant powerPlant){
		this.powerPlant = powerPlant;
		this.variableSet = new VariableSet();
		
		// set default model variables for PowerPlant objects
		this.ratedPower = new SingleVariable( EnumPV.powerProductionOptimal,  this.powerPlant.getPeakPower());
		this.ratedPower.properties.set(true, false);
		this.variableSet.add(ratedPower);
				
		this.powerProduction = new SingleVariable( EnumPV.powerProduction, this.powerPlant.getPowerProduction());
		this.powerProduction.properties.set(true, false);
		this.variableSet.add(powerProduction);
		
		// call the function of the sub-class
		this.initVariableSet();
	}
	
	/**
	 * The method calls updateVariables() and sets the variable values for to the PowerPlant Object
	 */
	@Override
	public void updateModel(GregorianCalendar currentTime, Weather weather,	int resolution) {
		this.updateVariables( currentTime, weather, resolution);
		this.powerPlant.setPowerProduction( this.powerProduction.getValueNumeric().roundValue() );	 
		this.powerPlant.setPeakPower(  this.ratedPower.getValueNumeric().roundValue() );
	}
	
	protected NumericValue getRatedPower(){
		return ratedPower.getValueNumeric();
	}
	
	protected void setRatedPower(NumericValue peakPower){
		this.ratedPower.setValue(peakPower);
	}
	
	protected NumericValue getPowerProduction(){
		return powerProduction.getValueNumeric();
	}
	
	protected void setPowerProduction(NumericValue powerProduction){
		this.powerProduction.setValue(powerProduction);
	}
	
}
