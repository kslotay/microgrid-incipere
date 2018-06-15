package sgs.model.variables;

import org.apache.commons.math3.complex.Complex;

import sgs.model.ProgramConstants;

/**
 * New value representing Double as well as Complex.
 * 
 * @author Kristofer Schweiger
 */
public class NumericValue extends AbstractValue{
	
	/** real and imaginary part of value **/
	private double real, imaginary;
	
	/** True if using only real value **/
	public boolean isReal;
	
	
	public NumericValue(){
		setValue(0,0);
	}
	public NumericValue(double real){
		setValue(real,0);
	}
	public NumericValue(double real, double imaginary){
		setValue(real,imaginary);
	}
	public NumericValue(Complex c){
		setValue(c);
	}
	public NumericValue(NumericValue n){
		setValue(n);
	}
	/**
	 * Constructor with String value. Stores object value, not String.
	 * Watch out for parsing exceptions.
	 * Accepted formats:
	 * 	100,200 	-> 100 + 200i
	 *  100 200 	-> 100 + 200i
	 *  100\t200 	-> 100 + 200i
	 *  100,,,200 	-> 100 + 200i
	 *  Combinations with whitespaces at start, end and middle
	 *  ,200 		-> 0 + 200i
	 *  \t 200 		-> 200 + 0i
	 *  100			-> 100 + 0i
	 * 
	 * @param value
	 */
	public NumericValue(String complexValue){
		setValue(complexValue);
	}
	
	@Override
	public void setValue(double real) {
		setValue(real,0);
	}
	
	@Override
	public void setValue(Complex c){
		setValue(c.getReal(), c.getImaginary());
	}
	
	@Override
	public void setValue(NumericValue v){
		setValue(v.real, v.imaginary);
	}
	
	@Override
	public void setValue(String complex) {
		
		String[] tmp;
		complex = complex.trim();
		
		if( complex.contains(",") ){
			tmp = numberSplit(complex, ','); //complex.split(",");
		}
		else if( complex.contains("\t") ){
			tmp = numberSplit(complex, '\t');
		}
		else if( complex.contains(" ") ){
			tmp = numberSplit(complex, ' ');
		}
		else{
			tmp = new String[] {complex};	// allow single real value
			//throw new NumberFormatException("Unknown seperation format for complex number.");
		}
		
		if(tmp.length>2){
			throw new NumberFormatException("Unaccaptable number of values in String: "+tmp.length);
		}
		
		try{
			double r = Double.parseDouble(tmp[0]);
			double i = 0;
			if(tmp.length>1)
				i = Double.parseDouble(tmp[1]);
			
			setValue(r,i);
		}
		catch(RuntimeException e){
			throw new NumberFormatException("Unknown number format for complex number: "+e.getMessage());
		}
	}
	
	public void setValue(double real, double imaginary){
		this.real = real;
		this.imaginary = imaginary;
		this.isReal = imaginary==0;
	}
	
	public void setReal(double real){
		this.real = real;
	}
	
	public void setImaginary(double imaginary){
		this.imaginary = imaginary;
	}
	/**
	 * Get up to two numbers separated by sep.
	 * 
	 * @param s   - trimmed string with numbers
	 * @param sep - separator
	 * @return String array of size 0, 1 or 2 with (hopefully correct) numbers. 
	 * 	Number format is not tested.
	 * 	Whitespace characters are removed.
	 */
	private static String[] numberSplit(String s, char sep){
		
		StringBuffer a, b;
		{
			int bSize = s.length()/2;
			a = new StringBuffer(bSize); 
			b = new StringBuffer(bSize);
		}
		StringBuffer current = a;
		
		for(int i=0; i<s.length(); i++){
			char c = s.charAt(i);
			
			if(c == sep){
				current = b;
			}
			else if(Character.isWhitespace(c)){
				// do nothing
			}
			else{
				current.append(c);
			}
		}
		
		if(b.length()>0){
			if(a.length()==0)
				a.append('0');
			return new String[] {a.toString(), b.toString()};
		}
		else if(a.length()>0)
			return new String[] {a.toString()};
		else
			return new String[] {};
	}
	
	/** @return Real part of value **/
	public double r(){
		return real;
	}
	public double getReal(){
		return real;
	}
	/** @return imaginary part of value **/
	public double i(){
		return imaginary;
	}
	public double getImaginary(){
		return imaginary;
	}
	
	/** @return sqrt(a^2+b^2) **/
	public double abs(){
		if(isReal)
			return real;
		else
			return Math.sqrt(real*real + imaginary*imaginary);
	}
	/** @return true if it has real value only **/
	public boolean isReal(){
		return isReal;
	}
	/** @return true if real part and imaginary part are NaN **/
	public boolean isNaN() {
		Double re = this.r();
		Double im = this.i();
		 if (  re.isNaN() && im.isNaN() ) {
			 return true;
		 }
		return false;
	}
	
//	@Override
//	public void setValue(Object value) {
//		setValue( (Complex)value );
//	}

	@Override
	public String getValue() {
		
		if(isReal){
			return ""+real;
		}
		else{
			return ""+real+","+imaginary;
		}
	}

	@Override
	public NumericValue getValueNumeric() {
		return this;
	}
	@Override
	/** @return the absolute value from the complex object **/
	public double getValueDouble() {
		if(isReal)
			return real;
		else
			return Math.sqrt(real*real + imaginary*imaginary);
	}
	
	@Override
	public Complex getValueComplex() {
		return new Complex(real,imaginary);
	}
	
	/**
	 * Add another NumericValue to this value.
	 * @param val
	 */
	public NumericValue add(NumericValue val){
		this.real += val.real;
		this.imaginary += val.imaginary;
		
		if ( this.getImaginary() != 0.0 ) 
			this.isReal = false;
		return this;
	}
	/**
	 * Subtract another NumericValue from this value.
	 * @param val
	 * @return this
	 */
	public NumericValue subtract(NumericValue val){
		this.real -= val.real;
		this.imaginary -= val.imaginary;
		
		if ( this.getImaginary() != 0.0 ) 
			this.isReal = false;
		return this;
	}
	/**
	 * Multiply by another NumericValue.
	 * @param val
	 */
	public NumericValue multiply(NumericValue val){
		double a=this.real, b=this.imaginary;
		double c=val.real, d=val.imaginary;
		
		this.real = a*c-b*d;
		this.imaginary = a*d+b*c;
		
		if ( this.getImaginary() != 0.0 ) 
			this.isReal = false;
		return this;
	}
	/**
	 * Divide by another NumericValue.
	 * @param val
	 */
	public NumericValue divide(NumericValue val){
		double a=this.real, b=this.imaginary;
		double c=val.real, d=val.imaginary;
		
		double divisor = c*c + d*d;
		
//		this.real = (a*c+b*d) / divisor;
//		this.imaginary = (b*c-a*d) / divisor;
		
		double re = (a*c+b*d) / divisor;
		double im = (b*c-a*d) / divisor;
		
		this.setValue(re, im);
		
		if ( this.getImaginary() != 0.0 ) 
			this.isReal = false;
		return this;
	}
	/**
	 * Sets the imaginary part minus imaginary part.
	 */
	public NumericValue conjugate() {
		//this.setValue( this.r(), -1*this.i() );
		this.imaginary = -imaginary;
		return this;
	}

	
	@Override
	public NumericValue copy() {
		return new NumericValue(real,imaginary);
	}
	
	@Override
	public String toString(){
		if(isReal){
			return ""+real;
//			return ""+real+",0i";  // TODO resort the file-output 
		}
		else{
			return ""+real+","+imaginary;
		}
	}
	
	
	/**
	 * Rounds real and imaginary part to the number of digits.
	 */
	public NumericValue roundValue(int roundPower){
		double roundFactor = Math.pow(10, roundPower);
		this.real = Math.round(this.real*roundFactor)/roundFactor;
		this.imaginary = Math.round(this.imaginary*roundFactor)/roundFactor;
		return this;
	}
	
	
	/**
	 * Rounds real and imaginary part to the number of digits specified in class sgs.model.ProgramConstants .
	 */
	public NumericValue roundValue(){
		int digits = ProgramConstants.shownDigits;
		this.roundValue(digits);
		return this;
	}
	
	
	
	/**
	 * Testing
	 */
	public static void main2(String[] args){
		
		NumericValue t1 = new NumericValue("100");
		NumericValue t2 = new NumericValue("10");
		NumericValue x;
		
		x = t1.copy(); x.add(t2);
		System.out.println( x );
		//
		x = t1.copy(); x.subtract(t2);
		System.out.println( x );
		//
		x = t1.copy(); x.multiply(t2);
		System.out.println( x );
		//
		x = t1.copy(); x.divide(t2);
		System.out.println( x );
		
//		NumericValue t1 = new NumericValue("100.5, 200");
//		NumericValue t2 = new NumericValue("100 \t 100");
//		
//		t2.setValue("200 200");
//		t2.setValue("100 ");
//		t2.setValue("100,");
//		t2.setValue("100\t");
//		t2.setValue("100\t,,,200");
//		
//		t2.setValue(" 300 \t, ");
//		t2.setValue("999");
//		t2.setValue(", 100");
//		
//		System.out.println(t1);
//		System.out.println(t2);
//		
//		System.out.println("Absolute-Sum: " + (t1.getValueDouble()+t2.getValueDouble()) );
	}
	

	

	
}
