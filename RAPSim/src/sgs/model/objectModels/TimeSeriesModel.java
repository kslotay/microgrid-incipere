package sgs.model.objectModels;
/**
 * 
 * @author bbreilin
 *
 */
public interface TimeSeriesModel {
	public static int PERIOD_DAILY = 0;
	public static int PERIOD_WEEKLY = 1;
	public static int PERIOD_MONTHLY = 2;
	public static int PERIOD_ANNUALY = 3;
	
	
	public void loadDataFromFile();
	
}
