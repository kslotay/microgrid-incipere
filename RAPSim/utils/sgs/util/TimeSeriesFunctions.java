package sgs.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * 
 * @author bbreilin
 *
 */
public class TimeSeriesFunctions {
		/**
		 * calculates the average-powerconsumption over an given timethread-interval
		 * @param data
		 * @param arrayCol
		 * @param startTime
		 * @param fileRes
		 * @param currentTime
		 * @param timeThreadRes
		 * @param timeLeft
		 * @return ret: type: Object[] with
		 * 							double: average power consumption, 
		 * 							int: time left from timethread interval, 
		 * 							GregorianCalendar: currentTime after calculation 
		 */
		public static Object[] getSyncValue(ArrayList<String> data, int arrayCol, int startTime,int fileRes, GregorianCalendar currentTime, int timeThreadRes,int timeLeft){
			int minutesOfDay =currentTime.get(Calendar.HOUR_OF_DAY)*60 + currentTime.get(Calendar.MINUTE);
			int startIndex = (minutesOfDay - startTime)/fileRes;
			if(startIndex < 0) startIndex = startIndex + 1440/fileRes;
			Object[] ret = new Object[3];	
			GregorianCalendar currentTimeCopy = (GregorianCalendar) currentTime.clone();
			double avg = 0;
			int index = startIndex;
			while(timeLeft > 0 && index < 1440/fileRes){
				if(timeThreadRes % fileRes == 0){
					String[] dataset = data.get(index).split(";");
					avg = avg + Double.parseDouble(dataset[arrayCol])*fileRes;
					index++;
					timeLeft = timeLeft - fileRes;
					currentTimeCopy.add(Calendar.MINUTE, fileRes);
				}
				else{
					String[] dataset = data.get(index).split(";");
					minutesOfDay =currentTimeCopy.get(Calendar.HOUR_OF_DAY)*60 + currentTimeCopy.get(Calendar.MINUTE);
					int restMin = fileRes-(minutesOfDay%fileRes);
					if(restMin != fileRes){
						System.out.println("restmin: "+restMin);
						avg = avg + Double.parseDouble(dataset[arrayCol])*restMin;
						System.out.println("added: "+ Double.parseDouble(dataset[arrayCol])*restMin);
						timeLeft = timeLeft - restMin;
						currentTimeCopy.add(Calendar.MINUTE, restMin);
					}
					else if(timeLeft < fileRes){
						System.out.println("timeleft " + timeLeft);
						avg = avg + Double.parseDouble(dataset[arrayCol])*timeLeft;
						System.out.println("added " + Double.parseDouble(dataset[arrayCol])*timeLeft);
						currentTimeCopy.add(Calendar.MINUTE, timeLeft);
						timeLeft = 0;
					}
					else{
						avg = avg + Double.parseDouble(dataset[arrayCol])*fileRes;
						System.out.println("added " + Double.parseDouble(dataset[arrayCol])*fileRes);
						currentTimeCopy.add(Calendar.MINUTE, fileRes);
						timeLeft = timeLeft-fileRes;
					}
					index++;
				}
			}
			ret[0] = avg/timeThreadRes;
			ret[1] = timeLeft;
			ret[2] = currentTimeCopy;
			return ret;
		}
}
