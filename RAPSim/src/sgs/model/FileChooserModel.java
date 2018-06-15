package sgs.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.csvreader.CsvWriter;

/**
 * 
 * @author hs
 *Model to create the csv file
 */
public class FileChooserModel {

	public static File csv = new File("./simulation.csv");
	
	/**
	 * create the header in the csv-file
	 * @param head
	 * @throws IOException e
	 */
	public static void createHead(int algo){
		
		boolean alreadyExists = FileChooserModel.csv.exists();
		String head = "";
		
		if (!alreadyExists)	{			
			if(algo == 1){
				head = "date time weather" + "\t" + "bus" + "\t" + "netto production\n";
			} else if(algo == 2){
				head = "Date Time Weather\tBus\tNumber\tVoltage\tVoltage Angle\tReal Power\tReactive Power\n";
			}else if(algo == 3) {
				head = "Date Time Weather\tBus1\tBus2\tpower\n";
			}
						
			try {
				CsvWriter header = new CsvWriter(new FileWriter(csv, true), '\"');					
				header.write(head);
				header.endRecord();
				header.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
	}
	
	/**
	 * write all other data, without a header
	 * @param head	Header
	 * @param data	every data that algorithem gives
	 * @throws IOException e
	 * 
	 */
	public static void createCSV( String data){
		
		try {
				CsvWriter csvOutput = new CsvWriter(new FileWriter(csv, true), '\"');
				csvOutput.write(data);
				csvOutput.endRecord();
				csvOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * delete the CSV-File
	 */
	public static void deleteCSV(){
		csv.delete();
	}
	
	
	/**
	 * 
	 * @return csv type
	 */
	public static File getCSV(){
		return csv;
	}
}