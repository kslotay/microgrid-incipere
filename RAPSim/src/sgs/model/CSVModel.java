package sgs.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.PowerLine;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.simulation.Bus;
import sgs.model.simulation.Path;
import sgs.model.variables.EnumPV;

import com.csvreader.CsvWriter;

/**
 * 
 * @author Huber Sabrina
 *Model to create the csv file
 *from the list "data" it collects all necessary infos to know from which objects, which parameter it needs, and write them into the csv file
 */
public class CSVModel {

	public static File csv;
	public File newCSVFile = new File("");
	public EnumPV[] enums = EnumPV.values();
	private final SgsGridModel gridModel;
	private static List<String> data = new ArrayList<String>();
	private String filename ;
	private static boolean havePermission = false; 
	public boolean setNewFileName = true;
	

	/**
	 * the user needs to activate the option to save the specified data into a csvfile, with activating a checkbox in the simulationStart window. 
	 * if its checked true, we have the permission to save the specified data. 
	 * we need that, so that the timeThread stop doing anything, bevor its starts. 
	 * @param value
	 */
	public void setPermission(boolean value){
		havePermission = value;
	}
	
	
	/**
	 * constructor
	 * @param gridModel
	 */
	public CSVModel(SgsGridModel gridModel) {
		super();
		this.gridModel = gridModel;
	}


	/**
		 * 
		 * @param splitColumn - data from one column 
		 * @return dataString - return the data for that column
		 */
		private String createData(String[] splitColumn){
			
			String dataString = "";

			String type, collectionNumber, sgoString, parameter; 
				
			//Always , so there's no need for a loop or something else. ng
			type = splitColumn [0];
			collectionNumber = splitColumn[1];
			sgoString = splitColumn [2];
			parameter = splitColumn [3];
			
			
			if(type.equals("BUS")){
				
				LinkedList<Bus> busInGrid = gridModel.buses;
					
				for (Bus bus : busInGrid){
//					bus.refreshValues();
					if(bus.toString().equals(collectionNumber)){
						for(SmartGridObject sgo : bus.busGridObjects){
							if(sgo.toString().equals(sgoString)){
								dataString = getDataParameter(parameter, sgo, null);
 
								return dataString;
							}
						}
					}				
				}
				
			}else if(type.equals("PATH")){ 	
				
				LinkedList<Path> pathInGrid = gridModel.paths;
//				
				for(Path path: pathInGrid){
					path.refreshValues();
					if(path.toString().equals(collectionNumber)){
						for(SmartGridObject sgo : path.pathObjects){
							if(sgo.toString().equals(sgoString)){
								dataString = getDataParameter(parameter, sgo, null);
							}
						}
					}
				}
				
				return dataString; 
			}

			return dataString; 
		}


	/**
	 * create the header in the csv-file
	 * @param newHeader 
	 * @param head
	 * @throws IOException e
	 */
	private void createHead(StringBuilder newHeader) { 	
	
		if(setNewFileName == false){
			csv.delete();
		} else {
			if(csv.getAbsolutePath().endsWith(".csv")){
//				String [] splitColumn = csv.toString().split(".csv");
				
				
				
//				for(int i = 0; i<splitColumn.length; i++){
//					System.out.println(splitColumn[i] );
//				}
				
			}
		}
	
		String head = "Date&Time" + ";" + "Cloudfactor" + ";" + "Windspeed" + ";";
		head = head + newHeader;
		
		
		try{
			CsvWriter header = new CsvWriter(new FileWriter(csv,true), '\"');
			header.write(head);
			header.endRecord();
			header.close();	
		} catch (IOException e){
			e.printStackTrace();
		}
	
	}
	
	
	/**
	 * write all other data, without a header
	 * @param head	Header
	 * @param data	every data that algorithem gives
	 * @throws IOException e
	 * 
	 */
	public void createCSVLine(String currentTime, Weather currentWeather){		
		
		if(havePermission == true){ //we are allowed to write into a csv
			
			String dataLine = "";
			currentTime = cleanString(currentTime);
				
			dataLine = currentTime + ";" + currentWeather.getCloudFactor() + ";" + currentWeather.getWindSpeed(); 
			
			for(int i = 0; i<data.size(); i++){

				String column = data.get(i);
				String [] splitColumn = column.split(";");
				
					dataLine = dataLine + ";" + createData(splitColumn);
			}	
			
			try {
					CsvWriter csvOutput = new CsvWriter(new FileWriter(csv, true), '\"');
					csvOutput.write(dataLine);
					csvOutput.endRecord();
					csvOutput.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}


	/**
	 * csv do not allow special caracters in the body. so every string that write into it, has to be cleaned.
	 * @param stringToClean
	 * @return stringToClean  replaceAll("[:.]", ".").replaceAll("[- ()]", " ")
	 * 	 */
	private String cleanString(String stringToClean){
		stringToClean = stringToClean.replaceAll("[:.]", ".").replaceAll("[- ()]", " ");
		
		return stringToClean; 
	}
	
	
	/**
	 * 
	 * @param p - the parameter 
	 * @param sgo - smartgridobject
	 * @param plo - powerlineobject
	 * @return dataParameter
	 */
	private String getDataParameter(String p, SmartGridObject sgsObj, PowerLine plObj){
			
			String dataParameter = "";
			// TODO find better solution if possible 
			switch (EnumPV.valueOf(p)){
				case powerDemand:
					dataParameter = sgsObj.getPowerDemand().toString();
					break;
				case powerConsumption:
					dataParameter = sgsObj.getCurrentPowerConsumption().toString();
					break;
				case powerProductionOptimal:
					dataParameter = sgsObj.getPeakPower().toString();
					break;
				case powerProduction:
					dataParameter = sgsObj.getPowerProduction().toString();
					break;
//				case powerProductionNeeded:
//					dataParameter = sgsObj.getParameterOfSGO(); 	
//					System.out.println(sgsObj.getParameterOfSGO());
//					dataParameter =  sgsObj.variableSet.get(EnumPV.powerProductionNeeded).getValueNumeric().toString(); 
//					break;
				case currentVoltage:
					dataParameter = sgsObj.getCurrentVoltage().toString();
					break;
				case voltageAngle:
					dataParameter = sgsObj.variableSet.get(EnumPV.voltageAngle).getValueNumeric().toString();
					break;
				case maxPowerTransport:
					dataParameter = sgsObj.getMaxPowerTransport() + "";
					break;
				case transportPowerDecrement:
					dataParameter = sgsObj.getTransportPowerDecrement() + "";
					break;
				case resistance:
					dataParameter = sgsObj.getResistance().toString();
					break;
				case powerLineCharge:
					dataParameter = sgsObj.getLineCharge() + "";
					break;
				case currentCurrent:
					dataParameter = plObj.getCurrentCurrent() + "";
					break;
				case currentPowerLoss:
					dataParameter = plObj.getCurrentPowerLoss() + "";
					break;
				case currentPowerTransport:
					dataParameter = plObj.getCurrentPowerTransport() + "";
					break;
				default:
					break;
		}
			
			return dataParameter;
			
		}


	/**
	 * start csv Model and duty 
	 * @param choosenData - to save the list from GiveDataCollections
	 * @param header 
	 */
	public void run(List<String> choosenData, boolean permissionToSave, StringBuilder header) {
		data = choosenData; //save List from GiveDataCollections
		createHead(header);
		havePermission = permissionToSave; 
		
	}

 

	/**
	 * receive file from the gui ResultsCSVView.java
	 * @param file - selected path + filename + .csv file format
	 */
	public void setCSVFile(File file) {
		csv = file; 
		filename = csv.getName(); 
	}
	
	
	/**
	 * 
	 * @return the name of the csvfile
	 */
	public String getFileName(){
		return filename; 
	}
	
	
}