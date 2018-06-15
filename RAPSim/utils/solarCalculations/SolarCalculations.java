package solarCalculations;

import testing.Out;
import testing.Out_;

public class SolarCalculations {

	/** kW/m^2 above the atmosphere **/
	public static final double Intensity_0 = 1.353*1000;
	/** defined solar intensity per m² for Watt Peak calculation (1000.0W/m², 25°C, AM=1,5) **/
	public static final double Wpeak_Intensity = 1000.0;	// http://de.wikipedia.org/wiki/Watt_Peak
	/** defined Air Mass for Watt Peak calculation **/
	public static final double WPeak_AirMass = 1.5;		// http://de.wikipedia.org/wiki/Watt_Peak
	/** atmosphere, km **/
	public static final double y_atm = 9;
	/** earth radius, km **/
	public static final double R_E = 6371;
	
	private static final double PI = Math.PI;
	private static final double RAD = (PI / 180.0);
	
	/**
	 * A1 - Air Mass - Very simple algorithm
	 * @param z - zenith angle, degree from Azimuth. z=-90..90
	 * @return Air Mass: 1=Optimal, K>1: Suboptimal
	 */
	public static double getAirMass_Simple(double z){
		assert z>=-90 && z<=90;
		z = (z)*RAD;
		
		double am = 1/(Math.cos(z));	// z... radiant from sun zenith
		return am;
	}
	
	/**
	 * A2 - Air Mass - Kasten and Young (1989)
	 * 
	 * @param z - zenith angle, degree from Azimuth. z=0..90
	 * @return Air Mass: 1=Optimal, K>1: Suboptimal
	 */
	public static double getAirMass_KastenYoung(double z){
		assert z>=0 && z<=90;
		double zDeg = z;	// z degree from zenith
		z = zDeg*RAD;			// z radiant from zenith
		
		double tmp = 0.50572*  Math.pow( (96.07995-zDeg), -1.6364 );
		double am = 1/( Math.cos(z) + tmp );
		if(am<1)
			return 1.0;			// for consistency with definition
		return am;
	}
	
	/**
	 * A3 - Air Mass - Simple spherical shell
	 * 
	 * @param z - zenith angle, degree from Azimuth. z=AnyValue
	 * @return Air Mass: 1=Optimal, K>1: Suboptimal
	 */
	public static double getAirMass_A3(double z){
		z = (z)*RAD;			// z radiant from zenith
		double r = 6371/9;		// Earth radius divide by effective atmosphere
		
		double rCosZ = r*Math.cos(z);
		double am = Math.sqrt( rCosZ*rCosZ + 2*r + 1 ) - rCosZ;
		return am;
	}
	
	/**
	 * A4 - Air Mass - depending on height
	 * @param z - zenith angle, degree from Azimuth. z=AnyValue
	 * @param height - km above see level
	 * @return
	 */
	public static double getAirMass_A4(double z, double height){
		z = (z)*RAD;				// z in radiant from zenith
		
		final double r = 707.888;	// ratio = R_E / y_atm
		double c = height/y_atm;
		
		double cosZ = Math.cos(z);		// cos(z)
		double cos2Z = cosZ*cosZ;		// cos(z)^2
		double rPc = r+c;				// r plus c
		
		double amTmp = (rPc*rPc)*cos2Z+(2*r+1+c)*(1-c);
		double am = Math.sqrt(amTmp) - rPc*cosZ;
		
		return am;
	}
	
	/**
	 * A simple intensity calculation.
	 * Does not directly handle night, but it is accomplished with the internal properties of the formula.
	 * 
	 * @param am
	 * @return intensity: W/m^2
	 */
	public static double getSolarIntensity(double am){
		final double strayAndDirectLight = 1.1;	// a constant for stray light approximation; 10% of direct light
		final double k1 = 0.678;		// another constant, see documentation or literature
		
		double intensityTemp = Math.pow(0.7, Math.pow(am,k1));
		double intensity = strayAndDirectLight * Intensity_0 * intensityTemp;
		
		return intensity;
	}
	
	/**
	 * A more complex intensity calculation.
	 * Uses getAirMass_A3(z) internally.
	 * Does not directly handle night, but it is accomplished with the internal properties of the formula.
	 * 
	 * @param z - zenith angle, degree from Azimuth
	 * @param height - km above see level
	 * @return intensity: W/m^2
	 */
	public static double getSolarIntensity(double z, double height){
		final double strayAndDirectLight = 1.1;	// a constant for stray light approximation; 10% of direct light
		final double ik1 = 0.678;		// another constant, see documentation or literature
		final double hk1 = 7.1;			// another constant in context with the height
		
		double am = getAirMass_A3(z);	// based on sea level air mass
		Out.pl("airMass="+am);
		
		double intensityTemp = Math.pow(0.7, Math.pow(am,ik1));
		double heightTmp = height/hk1;
		
		double intensity = strayAndDirectLight * Intensity_0 * ((1-heightTmp)*intensityTemp + heightTmp);
		
		return intensity;
	}
	
	
//	/**
//	 * Prepare z for Java calculation.
//	 * @param z - degree from Azimuth
//	 * @return radiant from sun zenith
//	 */
//	private static double zInterpretation(double z){
//		return (90.0-z)*RAD;
//	}
	
    private static double round(double number, int positions)
    {
        double temp = Math.pow(10.0, positions);
        number *= temp;
        number = Math.floor(number+0.5);
        number /= temp;
        //Out_.pl("round: "+b4+" to "+number);
        return (number);
    }
    
    
	/**
	 * TODO: TEST for solar calculations
	 * @param args
	 */
	public static void main(final String[] args) {
		
		for(double z=-180; z<=+180; z+=30){
			Out_.pl( "z="+z+", "+"cos(z)="+Math.cos(z*RAD));
//			Out_.pl( "A1-Simple: "+getAirMass_Simple(z) );
//			Out_.pl( "A2-Kasten: "+getAirMass_KastenYoung(z) );
			Out_.pl( "A3       : "+ round(getAirMass_A3(z),8) );
			Out_.pl( "A4-height: "+ round(getAirMass_A4(z,0),8) );
			Out_.pl();
		}
//		double z=1;
//		Out_.pl( z+": "+"cos(z)="+Math.cos(z*RAD));
//		Out_.pl( "A1-Simple: "+getAirMass_Simple(z) );
//		Out_.pl( "A2-Kasten: "+getAirMass_KastenYoung(z) );
//		Out_.pl( "A3       : "+ round(getAirMass_A3(z),8) );
//		Out_.pl( "A4-height: "+ round(getAirMass_A4(z,0),8) );
//		Out_.pl();
	}
}
