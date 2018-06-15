package sgs.model.variables;

/**
 * Unit enum to be used with VariableSet.
 * 
 * TODO: Add all needed units.
 * TODO: Unit calculation, handling of multiplication etc. ?
 * 			-> Special unit with String-like representation? 
 * 
 * @author Kristofer Schweiger, bbreilin
 */
public enum EnumUnit {
	
	watt("W"),
	volt("V"),
	ampere("A"),
	ohm("OHM"),
	degree("°"),
	squareMeters("m²"),
	kilometers("km"),
	percent("%"),
	percentDiv100("% /100"),
	none(""),
	meterPerSecond("m/s"),
	kiloWattHour("kWh"),
	hertz("Hz"), 
	liter("l"),
	TODO("???"); 
	
	String shortName;
	EnumUnit(String shortName){
		this.shortName = shortName;
	}
	
	@Override
	public String toString(){
		return shortName;
	}
	
}
