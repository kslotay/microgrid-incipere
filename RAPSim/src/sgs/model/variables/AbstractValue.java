package sgs.model.variables;

import org.apache.commons.math3.complex.Complex;

/**
 * Abstract class for values in OneAttribute.
 * 
 * @author Kristofer Schweiger
 */
public abstract class AbstractValue {
	
	/** @param value - a String representation of the specific value class. Should be parsable. **/
	public abstract void setValue(String value);
	/** @param d - a double value. May not be fully compatible with every value class. **/
	public abstract void setValue(double d);
	/** @param complex - a Complex value. May not be fully compatible with every value class. **/
	public abstract void setValue(Complex complex);
	/** @param numericValue - {real,imaginary}. May not be fully compatible with every value class. **/
	public abstract void setValue(NumericValue numericValue);
//	/** @param value - a object the specific value class. Should be of correct type. **/
//	public abstract void setValue(Object value);
	
	/** @return a string representation of the value **/
	public abstract String getValue();
	/** @return a double representation of the value, zero for non numeric values. **/
	public abstract double getValueDouble();
	/** @return a complex representation of the value, zero for non numeric values **/
	public abstract Complex getValueComplex();
//	/** @return a value object, handle with care **/
//	public abstract Object getObjectValue();
	/** @return a NumericValue object or null **/
	public abstract NumericValue getValueNumeric();
	
	@Override
	/** @return A string representation of the value, similar to "String getValue()" **/
	public String toString() {
		return this.getClass().getSimpleName()+":"+getValue();
	}
	
	/**
	 * @return a Copy up to primitive variables from this Value.
	 */
	public abstract AbstractValue copy();
}
