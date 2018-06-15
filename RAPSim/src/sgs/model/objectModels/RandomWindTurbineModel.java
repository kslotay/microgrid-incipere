package sgs.model.objectModels;

import jGridMap.util.NESRandom;

import java.util.GregorianCalendar;
import java.util.Random;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.WindTurbinePowerPlant;
import sgs.model.variables.EnumUnit;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
/**
 * 
 * @author bbreilin
 *
 */
public class RandomWindTurbineModel extends AbstractWindPowerPlantModel{
	
    private static final double PowerProduction_Sun 			= 0.5;  // Factor for production
    private static final double PowerProduction_Cloudy 			= 0.8;	// Factor for production
    private static final double PowerProduction_Rain 			= 1;
    private static final Random RAND = new NESRandom();
	
	private SingleVariable randomFactor;

	public RandomWindTurbineModel(WindTurbinePowerPlant powerPlant) {
		super(powerPlant);
		modelName = "RandomWindTurbineModel";
		description = "This is a random model of a wind turbine powerplant is scaled by two factors: P= P_rated * F_u * F_w.  " +
				" F_w is weather dependent and set to 0.5 / 0.8 / 1 in case the weather is specified as sunny / cloudy / rainy, respectively. " +
				" The user specified random factor F_u multiplies with a equal distributed value.";
		icon = new ImageIcon("Data2/WindTurbinePowerPlant_ICON.png");
//		this.initVariableSet();
	}

	@Override
	public void updateVariables(GregorianCalendar currentTime, Weather weather, int resolution) {
		
		while(this.randomFactor.getValueDouble()>1.0){
			this.randomFactor.setValue(this.randomFactor.getValueDouble()/10.0);
		}
		double factor;
    	{
        	double weatherFactor;
            switch (weather.state) {
                case clear: {
                	weatherFactor = PowerProduction_Sun;
                }
                break;
                case cloudy: {
                	weatherFactor = PowerProduction_Cloudy;
                }
                break;
                case rainy: {
                	weatherFactor = PowerProduction_Rain;
                }
                break;
                default: {
                	throw new RuntimeException("Fatal Error: Unknown WeatherID ?!?");
                }
            }
            double rndFactor = 1.0 - RAND.nextDouble()*this.randomFactor.getValueDouble();		// 0..1 // 1 - rnd*portion defined in PowerProduction_Rnd
            
            factor = rndFactor * weatherFactor;
    	}
      	this.setPowerProduction( new NumericValue(factor,0.0).multiply(ratedPower.getValueNumeric()  )  ); 
  	
		
//		this.powerPlant.setPowerProduction( this.powerProduction.getValueNumeric().roundValue(4) );
//		this.powerPlant.setPeakPower( this.ratedPower.getValueNumeric().roundValue(4) );
	}

	@Override
	protected void initVariableSet() {
		this.ratedPower.properties.set(true, true);
		this.randomFactor = this.initVariable("randomProductionFactor", new NumericValue(0.2), EnumUnit.none, true, true);
	}

}
