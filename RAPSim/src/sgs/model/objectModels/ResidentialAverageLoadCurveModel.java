package sgs.model.objectModels;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.CustomConsumer;
import sgs.model.variables.EnumUnit;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
import sgs.util.TimeSeriesFunctions;
/**
 * 
 * @author bbreilin, mpoechacker
 *
 */
public class ResidentialAverageLoadCurveModel extends
		AbstractCustomConsumerModel implements TimeSeriesModel {
	private static final int SEASON_WINTER = 0;
	private static final int SEASON_SUMMER = 1;
	private static final int SEASON_INTERIM = 2;
	
	private static final int SATURDAY = 0;
	private static final int SUNDAY = 1;
	private static final int WORKDAY = 2;
	
	private FileReader csvFile; 
	private BufferedReader dataReader;
	private static ArrayList<String> data;
	private SingleVariable annualEnergyConsumption;
	private final int fileRes = 15;
	
	public ResidentialAverageLoadCurveModel(CustomConsumer consumer) {
		super(consumer);
		this.modelName = "ResidentialAverageLoadCurveModel";
		this.icon = new ImageIcon("Data2/House_ICON.png");
		this.description = "The averaged annual residential load curve H0, from literature C. Fünfgeld, BTU Cottbus - Lehrstuhl Energiewirtschaft/ Oktober 1999, is scaled to the annual energy consumption of this house. " +
						"Winter: 1.11.-20.3. / Übergang: 21.3.-14.5. / Sommer: 15.5.-14.9. / Übergang: 15.9.-31.10. " +
						"The orgininal file is in 15' intervals. ";
		this.loadDataFromFile();
//		this.initVariableSet();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initVariableSet() {
		this.annualEnergyConsumption = this.initVariable("annualEnergyConsumption", new NumericValue(1000), EnumUnit.kiloWattHour, true, true);
//		TODO set correlation between Annual Consumption and Nominal Load 
	}
	

	@Override
	public void updateVariables(GregorianCalendar currentTime, Weather weather, int resolution) {
		this.getValueToTime(currentTime, resolution);
//		this.consumer.setCurrentPowerConsumption( this.powerDemand.getValueNumeric().roundValue(4)  );
	}


	@Override
	public void loadDataFromFile() {
		try {
			csvFile = new FileReader("src/sgs/model/objectModels/ResidentialAverageLoadCurve.csv");
			dataReader = new BufferedReader(csvFile);
			data = new ArrayList<String>();
			String line = "";
			while((line = dataReader.readLine()) != null){
				data.add(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void getValueToTime(GregorianCalendar currentTime, int resolution) {
		int monthOfYear = currentTime.get(Calendar.MONTH);
		int dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);
		int seasonOffset;
		int dayOffset;
		
		// determine the season_offset		
		seasonOffset = this.getSeasonOffset(monthOfYear, dayOfMonth);
		
		// determine the day_offset	
		dayOffset = this.getDayOffset(dayOfWeek);
		
		//set powerDemand
		GregorianCalendar currentTimeCopy = (GregorianCalendar) currentTime.clone();
		
		double avg = 0;
		int timeLeft = resolution;
		int arrayCol = 1+3*seasonOffset+dayOffset;
		while(timeLeft > 0){
			Object[] result = TimeSeriesFunctions.getSyncValue(data, arrayCol, 0, this.fileRes, currentTimeCopy, resolution, timeLeft);
			avg = avg + (double)result[0];
			timeLeft = (int) result[1];
			currentTimeCopy = (GregorianCalendar) result[2];
			seasonOffset = this.getSeasonOffset(currentTimeCopy.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH));
			dayOffset = this.getDayOffset(currentTimeCopy.get(Calendar.DAY_OF_WEEK));
		}
		powerDemand.setValue(avg*this.annualEnergyConsumption.getValueDouble()/1000);
	}

	
	private int getSeasonOffset(int monthOfYear, int dayOfMonth){
		if(monthOfYear >= Calendar.NOVEMBER || monthOfYear <= Calendar.MARCH){
			if(monthOfYear == Calendar.MARCH && dayOfMonth > 20) return ResidentialAverageLoadCurveModel.SEASON_INTERIM;
			else return ResidentialAverageLoadCurveModel.SEASON_WINTER;
		}
		else if(monthOfYear >= Calendar.MAY || monthOfYear <= Calendar.SEPTEMBER){
			if((monthOfYear == Calendar.MAY && dayOfMonth < 15) || (monthOfYear == Calendar.SEPTEMBER && dayOfMonth > 14)) 
				return ResidentialAverageLoadCurveModel.SEASON_INTERIM;
			else return ResidentialAverageLoadCurveModel.SEASON_SUMMER;
		}
		else return ResidentialAverageLoadCurveModel.SEASON_INTERIM;
	}
	
	private int getDayOffset(int dayOfWeek){
		if(dayOfWeek == Calendar.SATURDAY) return ResidentialAverageLoadCurveModel.SATURDAY;
		else if(dayOfWeek == Calendar.SUNDAY) return ResidentialAverageLoadCurveModel.SUNDAY;
		else return ResidentialAverageLoadCurveModel.WORKDAY;
	}

}
