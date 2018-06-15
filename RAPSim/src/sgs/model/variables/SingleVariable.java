package sgs.model.variables;

import org.apache.commons.math3.complex.Complex;

/**
 * Holds a single attribute, intended to use in the attribute set.
 * 
 * @author Kristofer Schweiger
 */
public class SingleVariable implements Comparable<SingleVariable>{
	
	private final String name;
	private AbstractValue value;
	private EnumUnit unit;



	public VariableProperties properties;
	
	/**
	 * Constructor: only for use in Models - not in Objects
	 * 
	 * @param name
	 * @param value
	 * @param unit
	 * @link ModelVariable
	 */
	public SingleVariable(String name, AbstractValue value, EnumUnit unit){
		this.name = name;
		this.value = value;
		this.unit = unit;
		this.properties = new VariableProperties();
		//renameLaterEnum = new TreeSet<EnumRenameLater>();
	}
	
	/**
	 * Constructor: for EnumPV types
	 * 
	 * @param name
	 * @param value
	 * @param unit
	 */
	public SingleVariable(EnumPV name, AbstractValue value){
		this(name.name(), value, name.getUnit());
		
	}
	
	/**
	 * Constructor: internal use
	 * @param name
	 */
	protected SingleVariable(String name){
		this.name = name;
	}
	
	/** @return the variable name **/
	public String name(){
		return name;
	}
	/** @return the value object, containing the actual variable value **/
	public AbstractValue value(){
		return value;
	}
	/** @return the unit, describing the variable and its value **/
	public EnumUnit unit(){
		return unit;
	}
	
	
	/** @return the value of the variable as String. **/
	public Double getValueString(){
		return value.getValueDouble();
	}
	/** @return the double value of the variable, zero for non numeric values. **/
	public Double getValueDouble(){
		return value.getValueDouble();
	}
	/** @return the Complex value of the variable, zero for non numeric values. **/
	public Complex getValueComplex(){
		return value.getValueComplex();
	}
	/** @return the numeric value, may be null for non numeric values. **/
	public NumericValue getValueNumeric(){
		return value.getValueNumeric().copy();
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setValue(String val){
		this.value.setValue(val);
	}
	/**
	 * Set double value. May not be compatible with every subclass of AbstractValue.
	 * @param d
	 */
	public void setValue(double d){
		this.value.setValue(d);
	}
	/**
	 * Set Complex value. May not be compatible with every subclass of AbstractValue.
	 * @param d
	 * 
	 */
	public void setValue(Complex c){
		value.setValue(c);
	}
	/**
	 * Set Numeric value. May not be compatible with every subclass of AbstractValue.
	 * @param v
	 */
	public void setValue(NumericValue v){
		value.setValue(v);
	}
	
	/**
	 * @return true if Variable isVisible OR isEditable (definition)
	 */
    public boolean variableIsUsed(){
    	return properties.isVisible() || properties.isEditable();
    }

	@Override
	/** Implementation for TreeSet **/
	public int compareTo(SingleVariable attr2) {
		int ret = name.compareToIgnoreCase(attr2.name);
		return ret;
	}
	
	/**
	 * @return a deep Copy, except the properties.
	 */
	public SingleVariable copyKeepProperties() {
		AbstractValue nVal = this.value.copy();
		SingleVariable nVar = new SingleVariable(name, nVal, unit);
		
		nVar.properties = this.properties;
		return nVar;
	}
	
	@Override
	public String toString(){
		return "{"+name+", "+value.toString()+" "+unit+"}";
	}
}
