package testing.t2;

import testing.Out;


public class ReflectionTests1Object {
	
	@SuppressWarnings("unused")
	private int intPrivate = 123;
	protected int intProtected = 234;
	public int intPublic = 345;
	
	static int intStatic = 500;
	final int intFinal = 600;
	final static int intFinalStatic = 700;
	
	long l = 123456;
	double d = 123.456;
	float f = 234.567f;
	String s = "my string!";
	Object o = new Object();
	ReflectionTests1Object to = null;
	
	static int serial = 0;
	
	public ReflectionTests1Object(){
		serial++;
		Out.pl("public ReflectionTests1Object()");
	}
	
	
}
