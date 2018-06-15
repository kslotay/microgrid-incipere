package sgs.model.variables.collector;

import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;

public abstract class VariableCollector {
	
	/** The variable to collect **/
	public final EnumPV variableName;
	
	/**
	 * Basic abstract constructor with the variable name.
	 * @param variableName
	 */
	public VariableCollector(EnumPV variableName){
		this.variableName = variableName;
	}
	
	/**
	 * @return SingleVariable from this collector
	 */
	public SingleVariable getSingleVariable(){
		SingleVariable sv = new SingleVariable(getVariableName(), getValue());
		return sv;
	}
	
	/**
	 * @return the variable name which should be collected.
	 */
	public EnumPV getVariableName(){
		return variableName;
	}
	
	/**
	 * @return the collected numeric value 
	 */
	public abstract NumericValue getValue();
	
	/**
	 * @return the number of collected values
	 */
	public abstract int getNumberOfValues();
	
	/**
	 * resets values to zero.
	 */
	public abstract void restValues();
	
	/**
	 * Adds interesting variable values from the variable owner
	 * @param owner1
	 * @return true if a value was collected
	 */
	public abstract boolean collectFrom(VariableOwner owner);
	
	/**
	 * @return a copy of this Collector
	 */
	public abstract VariableCollector copy();
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName()+"("+this.variableName+")";
	}
}
