package sgs.model.objectModels;

import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.WindTurbinePowerPlant;
import sgs.model.variables.EnumUnit;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;

public class UsualWindTurbineModel extends AbstractWindPowerPlantModel{
	
// 	is initialized in super class
//  private SingleVariable ratedPower;  
//  private SingleVariable powerProduction; 
	private SingleVariable cutInSpeed;  
    private SingleVariable ratedSpeed;	
    private SingleVariable cutOutSpeed;
    private double c1;

	public UsualWindTurbineModel(WindTurbinePowerPlant powerPlant) {
		super(powerPlant);
		modelName = "UsualWindTurbineModel";
		description = "The delivered power is related to the windspeed from the weather thread. Below cutIn and above cutOut production is 0, " +
				"between rated and cutOut it is the rated power and increases exponentially between cutIn and rated. " +
				"The exponential part is fitted with one parameter 'c1' so that exp[c1*(ratedSpeed-inSpeed)] = ratedPower.";
		icon = new ImageIcon("Data2/WindTurbineUsualModel_ICON.png");
	}
	
	@Override
	protected void initVariableSet() {  // called in constructor of super class
		this.ratedPower.properties.set(true, true);
		this.powerProduction.properties.set(false, false);

		this.cutInSpeed = this.initVariable("cut-in speed", new NumericValue(1.0), EnumUnit.meterPerSecond, true, true);	
		this.ratedSpeed = this.initVariable("rated speed", new NumericValue(3.0), EnumUnit.meterPerSecond, true, true);
		this.cutOutSpeed = this.initVariable("cut-out speed", new NumericValue(10.0), EnumUnit.meterPerSecond, true, true);
	}
	
	@Override
	public void updateVariables(GregorianCalendar currentTime, Weather weather, int resolution) {
		
		double windSpeed = weather.getWindSpeed();
		
		if (windSpeed<cutInSpeed.getValueDouble() || windSpeed>cutOutSpeed.getValueDouble()){
			this.setPowerProduction( new NumericValue(0.0) ); 
		} else if ( windSpeed>ratedSpeed.getValueDouble() && windSpeed<cutOutSpeed.getValueDouble() ){
			this.setPowerProduction( this.getRatedPower() );
		} else if (windSpeed>cutInSpeed.getValueDouble() && windSpeed<ratedPower.getValueDouble()){
			c1 = Math.log(ratedPower.getValueDouble()) / ( ratedSpeed.getValueDouble()-cutInSpeed.getValueDouble() );
			NumericValue pValue = new NumericValue(Math.exp( c1*(windSpeed-cutInSpeed.getValueDouble()) ) );
			this.setPowerProduction( pValue );
		}
	}
}
