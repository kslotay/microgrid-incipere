package solarCalculations.klaus;

import java.util.GregorianCalendar;


public class UsageOf_SPA_PSA {
  public static void main(String[] args) {
	  
	  
    final GregorianCalendar dateTime = new GregorianCalendar();
    final double latitude = 46;
    final double longitude = 14;

    AzimuthZenithAngle position = PSA.calculateSolarPosition(dateTime,
                                                             latitude,
                                                             longitude);
    System.out.println("PSA: " + position);


    position = SPA.calculateSolarPosition(dateTime,
                                          latitude,
                                          longitude,
                                          190, // elevation
                                          67, // delta T
                                          1010, // avg. air pressure
                                          11); // avg. air temperature
    System.out.println("SPA: " + position);
  }
}