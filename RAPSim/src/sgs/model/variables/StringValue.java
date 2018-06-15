package sgs.model.variables;

import org.apache.commons.math3.complex.Complex;

/**
 * Holds a String value.
 * 
 * @author Kristofer Schweiger
 */
public class StringValue extends AbstractValue {

	private String valueStr;
	
	public StringValue(String value){
		this.valueStr = value;
	}
	
	@Override
	public void setValue(String value) {
		this.valueStr = value;
	}
	@Override
	public void setValue(double d) {
		valueStr = ""+d;
	}

	@Override
	public void setValue(Complex complex) {
		valueStr = ""+complex.getReal()+","+complex.getImaginary();
	}
	
	@Override
	public void setValue(NumericValue numericValue) {
		valueStr = ""+numericValue.getReal()+","+numericValue.getImaginary();
	}
//	@Override
//	public void setValue(Object value) {
//		valueStr = value.toString();
//	}

	@Override
	public String getValue() {
		return valueStr;
	}

	@Override
	public NumericValue getValueNumeric() {
		return null;
	}
	@Override
	/** @return Strings have a double value of zero. **/
	public double getValueDouble() {
		return 0.0;
	}
	@Override
	/** @return Strings have a complex value of zero. **/
	public Complex getValueComplex() {
		return new Complex(0.0);
	}


	/**
	 * TEST
	 */
	public static void main(String[] args){
		
		StringValue test1 = new StringValue("bla");
		StringValue test2 = new StringValue("blubb");
		
		test2.setValue(1234);
		test1 = new StringValue("333");
		
		System.out.println(test1);
		System.out.println(test2);
	}

	
	@Override
	public AbstractValue copy() {
		StringValue nVal = new StringValue(valueStr);
		return nVal;
	}

}
