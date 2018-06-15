package sgs.model.objectModels;

import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.SolarPowerPlant;
import sgs.model.variables.EnumUnit;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
import solarCalculations.SolarCalculations;
import solarCalculations.klaus.AzimuthZenithAngle;
import solarCalculations.klaus.PSA;
import testing.Out;
/**
 * 
 * @author bbreilin
 *
 */
public class SolarPeakPowerModel extends AbstractSolarPowerPlantModel{

	private SingleVariable longitude;
	private SingleVariable latitude;
	private SingleVariable height;
	private SingleVariable efficiency;
	private SingleVariable squareMeters;
	
	public SolarPeakPowerModel(SolarPowerPlant powerPlant){
		super(powerPlant);
		modelName = "SolarPeakPowerModel";
		description = "The produced power is specified by the ratedPower of the PV system and the efficiency factor." +
				"Reduction is considered by 1) time of day and 2) day of the year by use of clear sky model and " +
				"3) by using random cloudiness of the weather thread ";
		icon = new ImageIcon("Data2/SolarPowerPlant_ICON.png");
//		this.initVariableSet();
	}
	
	@Override
	public void updateVariables(GregorianCalendar currentTime, Weather weather, int resolution){
		squareMeters.setValue(ratedPower.getValueDouble() / (SolarCalculations.Wpeak_Intensity*efficiency.getValueDouble()));
		double sunFactor = 1-weather.getCloudFactor();
    	double intensity;
    	AzimuthZenithAngle sun = PSA.calculateSolarPosition(currentTime, latitude.getValueDouble(), longitude.getValueDouble());
    	intensity = SolarCalculations.getSolarIntensity(sun.getZenithAngle(), height.getValueDouble());
    	Out.pl("intensity="+ intensity + ", pos="+latitude.getValueDouble()+"/"+longitude.getValueDouble()+", height="+height.getValueDouble()+", squareMeters="+squareMeters.getValueDouble()+", "+sun+" ,ratio="+intensity/SolarCalculations.Wpeak_Intensity);
        this.setPowerProduction( new NumericValue(squareMeters.getValueDouble() * intensity*efficiency.getValueDouble() * sunFactor) );
        
//        powerPlant.setPowerProduction( this.powerProduction.getValueNumeric().roundValue(4) );
//        powerPlant.setPeakPower(this.ratedPower.getValueNumeric().roundValue(4));
	}
	
	@Override
	protected void initVariableSet(){
		this.ratedPower.properties.set(true, true);
		this.powerProduction.properties.set(false, false);
		this.longitude = this.initVariable("longitude", new NumericValue(14.4), EnumUnit.degree, true, true);
		this.latitude = this.initVariable("latitude", new NumericValue(46.6), EnumUnit.degree, true, true);
		this.height = this.initVariable("height", new NumericValue(0), EnumUnit.kilometers, true, true);
		this.efficiency = this.initVariable("efficiency", new NumericValue(0.2), EnumUnit.percentDiv100, true, true);
		NumericValue initSqMeters = new NumericValue( ratedPower.getValueDouble() / (SolarCalculations.Wpeak_Intensity*efficiency.getValueDouble()));
		this.squareMeters = this.initVariable("squareMeters", initSqMeters, EnumUnit.squareMeters, true, false);
	}
}
