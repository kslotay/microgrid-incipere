package sgs.model.objectModels;

import java.util.GregorianCalendar;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.Consumer;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;
/**
 * Initializes the variableSet. 
 * Initializes the variable powerDemand with values from the Object. 
 * Handles the update of the object variables (rounded to 4 digits) and provides set- and get-methods for that variables.  
 * @author bbreilin, Poechacker
 *
 */
public abstract class AbstractConsumerModel extends AbstractModel{
	protected Consumer consumer;
	protected SingleVariable powerDemand;
	
	public AbstractConsumerModel(Consumer consumer){
		this.consumer = consumer;
		this.variableSet = new VariableSet();
			
		this.powerDemand = new SingleVariable( EnumPV.powerDemand, this.consumer.getPowerDemand() );
		this.powerDemand.properties.set(true, false);
		this.variableSet.add(powerDemand);
		
		this.initVariableSet(); // implemented in sub-class
	}
	
	/**
	 * The method calls updateVariables() and forwards the variable values for to the Consumer Object
	 */
	@Override
	public void updateModel(GregorianCalendar currentTime, Weather weather,	int resolution) {
		
		this.updateVariables(currentTime, weather, resolution);
		
		this.consumer.variableSet.set( EnumPV.powerDemand, this.getPowerDemand().roundValue() );
	}
		
	protected NumericValue getPowerDemand(){
		return powerDemand.getValueNumeric();
	}
	
	protected void setPowerDemand(NumericValue demand){
		this.powerDemand.setValue(demand);
	}
}
