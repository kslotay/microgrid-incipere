package sgs.controller.simulation;


import sgs.model.gridObjects.SmartGridObject;

import java.util.Random;

import org.apache.commons.math3.distribution.WeibullDistribution;


public class Weather {
	
	protected static final Random RAND = SmartGridObject.RAND;
	
	
	private double cloudFactor;
	private double temperature;
	private double windSpeed ; 
	
	public enum WEATHER {clear, cloudy, rainy}; 
	public WEATHER state;
	// 
	private WeibullDistribution windSpeedDistribution;  //= new WeibullDistribution(double 3, double 2.1);
	private double cloudChangeMean;
	
	
    public Weather(){
    	cloudFactor = 0.5;
    	temperature = 20;
    	windSpeed = 3; 
    	
    	this.state = WEATHER.cloudy;
    	// 
    	cloudChangeMean = 0.5;
    	windSpeedDistribution = new WeibullDistribution( 3, 2.1);

    }
	
	
    public void setRandSeed(long seed){
    	// TODO make seed setable via the GUI
		RAND.setSeed( seed );
    }
    
    /**
	 * @return the cloudFactor
	 */
    public double getCloudFactor(){
//    	System.out.println("Cloudfactor "+cloudFactor);
    	double roundedCF = this.cloudFactor;
		roundedCF = Math.round(roundedCF*1000.0)/1000.0;
    	return roundedCF;
    }	
  
    /**
	 * @return the temperature
	 */
	public double getTemperature() {
		double roundedTe;
		roundedTe = Math.round(this.temperature*1000.0) /1000.0;
		return roundedTe;
	}


	/**
	 * @return the windSpeed
	 */
	public double getWindSpeed() {
		double roundedWS;
		roundedWS = Math.round(this.windSpeed * 1000.0) /1000.0 ;
		return roundedWS;
	}


	
	public void newWindSpeed(){
		this.windSpeed = windSpeedDistribution.sample();
		System.out.println("Windspeed "+windSpeed);
		
	}
	

	/**
	 * set the cloudFactor to a new value
	 */
	public void newCloudFactor(){
    	// a toy model for generating cloudfactor 
    	double hourlyMaximaleChange = 0.5;
		double increaseChangeProbPerExtremHour = 0.05;
		
		double increment = hourlyMaximaleChange * (cloudChangeMean - RAND.nextDouble() );
		if ( cloudFactor+increment > 1 ){
			this.cloudFactor = 1;
			cloudChangeMean = cloudChangeMean-increaseChangeProbPerExtremHour;
		}else if (cloudFactor+increment < 0){
			this.cloudFactor = 0;
			cloudChangeMean = cloudChangeMean+increaseChangeProbPerExtremHour;
		}else {
			this.cloudFactor = cloudFactor+increment;
			cloudChangeMean = 0.5;
		}
    	
		if (cloudFactor>0.7) {
			this.state = WEATHER.rainy;
		}else if(cloudFactor>0.3){
			this.state = WEATHER.cloudy;
		} else {
			this.state = WEATHER.clear;
		}
		
		System.out.println("Cloudfactor "+cloudFactor);
		
    }
    
}
