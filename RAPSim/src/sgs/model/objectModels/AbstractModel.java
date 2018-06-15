package sgs.model.objectModels;

import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.variables.EnumUnit;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;
/**
 * The AM describes all parameters for Model administration in RAPSim.
 * The attributes are: modelName, description, icon and variableSet.
 * For all four are get-methods implemented
 * further methods are initVarible()
 * 
 * @author bbreilin, mpoechacker
 * 
 */
public abstract class AbstractModel {
	
	protected String modelName;
	protected String description;
	protected ImageIcon icon;
	protected VariableSet variableSet;
	
	/**
	 * Should be implemented in the AbstractObjectnameModel and must contain the updateVariables() method
	 */
	public abstract void updateModel(GregorianCalendar currentTime, Weather weather, int resolution);
	
	/**
	 * Should be implemented in the model to calculate new variable values
	 */
	protected abstract void updateVariables(GregorianCalendar currentTime, Weather weather, int resolution);
	
	/**
	 * Implemented in the model and called in the constructor of an AbstractObjectnameModel super-class
	 */
	protected abstract void initVariableSet();
	
	public VariableSet getVariables(){
		return this.variableSet;
	}
		
	public String getModelName() {
		return modelName;
	}
	public String getDescription() {
		return description;
	}
	public ImageIcon getIcon() {
		return icon;
	}
	
	/**
	 * Initialize a single variable and specify whether it is visible and editable in the property window for the model. 
	 * @param name
	 * @param initValue
	 * @param unit
	 * @param visible
	 * @param editable
	 * @return
	 */
	protected SingleVariable initVariable(String name, NumericValue initValue, EnumUnit unit, boolean visible, boolean editable){
		SingleVariable var = new SingleVariable(name, initValue, unit);
		var.properties.set(visible, editable);
		this.variableSet.add(var);
		return var;
	}
	/**
	 * Initialize a single variable for the model which is visible and editable.  
	 * @param name
	 * @param initValue
	 * @param unit
	 * @return
	 */
	protected SingleVariable initVariable(String name, NumericValue initValue, EnumUnit unit){
		SingleVariable var = new SingleVariable(name, initValue, unit);
		var.properties.set(true, true);
		this.variableSet.add(var);
		return var;
	}
}
