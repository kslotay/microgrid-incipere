package sgs.model.variables;


/**
 * Enum for predefined variables.
 * Holds the variable name and their ISO unit.
 * 
 * @author Kristofer Schweiger
 */
public enum EnumPV {
	
	powerDemand(EnumUnit.watt, 0.0),				// power component wants to consume 
	powerConsumption(EnumUnit.watt, 0.0),			// power currently consumed by component
	
	powerProductionOptimal(EnumUnit.watt, 0.0),		// power normally/optimally produced by component
	powerProduction(EnumUnit.watt, 0.0),			// power currently produced by component
//	powerProductionNeeded(EnumUnit.watt, 0.0),	    // part of currently produced power needed by consumers
	
	nominalVoltage(EnumUnit.volt, 1.0),				// nominalVoltage of ProSumer
	currentVoltage(EnumUnit.volt, 1.0),				// current voltage at component
	voltageAngle(EnumUnit.degree, 0.0),				// angle of voltage, also contained in currentVoltage
	
	// PowerLine
	maxPowerTransport(EnumUnit.watt, 0.0),			// maximum power instance can transport
	transportPowerDecrement(EnumUnit.none, 0.0), 	// percentile decrement ~ serial resistance
	resistance(EnumUnit.ohm, 0.0),					// serial resistance, e.g. Power Line
	powerLineCharge(EnumUnit.ohm, 0.0),				// parallel resistance, e.g. Power Line
	currentCurrent(EnumUnit.ampere, 0.0),           // current over the powerline,
	currentPowerLoss(EnumUnit.watt, 0.0), 			// current power lost on the powerline
	currentPowerTransport(EnumUnit.watt, 0.0);
	
	
	private EnumUnit unit;
	private Object defaultValue;
	
	/**
	 * Enum Constructor
	 * @param unit - Unit from Enum , can be watt, ohm, degree, kilometers ... {@link EnumUnit.java}
	 * @param defaultValue
	 */
	private EnumPV(EnumUnit unit, Object defaultValue){
		this.unit = unit;
		this.defaultValue = defaultValue;
		
	}
	
	/**
	 * @return the unit defined for this variable
	 */
	public EnumUnit getUnit(){
		return unit;
	}
	/**
	 * @return the default value defined for this variable
	 */
	public Object getDefaultValue(){
		return defaultValue;
	}
	
}
