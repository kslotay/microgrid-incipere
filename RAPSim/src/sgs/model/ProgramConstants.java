package sgs.model;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Interface holding a few constants for the Controller.
 * Use e.g. with static import.
 * Variables in interface are automatically final.
 * 
 * @author Kristofer Schweiger, Manfred Pöchacker
 */
public interface ProgramConstants {
	
	/** path to image files and other application data files **/
	public static String dataPath  = "Data2/";
	
	/** default file in dataPath where project is stored **/
	public static File defaultSimulationFile = new File(dataPath+"data.zip");
	
	/** file in dataPath where the program configuration is stored **/
	public static File parameterFile = new File(dataPath+"parameters.zip");
	
	/** the date format used **/
	public static DateFormat df = new SimpleDateFormat("yyyy.MM.dd - HH:mm");
	
	/** the number of digits to round in property windows**/
	public static int shownDigits = 5;
	
	/** specifies the displayed range of the voltage angle color overlay in degree**/
	public static int varCOVoltageAngle = 5;
	
	/** specifies the displayed range of the nominal voltage color overlay in factors of nominal value**/
	public static double varCONominalVoltage = 0.1;
}
