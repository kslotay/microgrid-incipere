package sgs.controller.simulation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TimeZone;

import sgs.controller.ButtonController;
import sgs.controller.GridController;
import sgs.controller.MenuController;
import sgs.controller.fileManagement.csvConstruction.CSVController;
import sgs.model.ProgramConstants;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.SmartGridObject;
import sgs.view.SpeedChanger;
import testing.Out;

/**
 * Time in a running simulation. Has time-relevant functions and parameters.
 * Uses GridController and ButtonController for interaction.
 * 
 * @author tobi, Schweiger
 */
public class TimeThread extends Thread {

	
	/** The used algorithm, abstract class **/
	private AbstractDistributionAlgorithm algorithm;
	/** The grid controller for interaction/control **/
	private final GridController gridController;
	/** The button controller for interaction/control **/
	private final ButtonController buttonController;
	/** the model **/
	private final SgsGridModel gridModel;
	
	/** true while TimeThread should run **/
    private boolean doRun = false;
  


	/** true while TimeThread is running **/
	private static boolean isRunning;
    /** true -> run simulation slowly for user **/
    public boolean realTime = true;
    /** simulation start **/
    public GregorianCalendar startTime;
    /** simulation current time **/
	public GregorianCalendar currentTime;
	/** simulation planned end **/
    public GregorianCalendar endTime;
    /** simulated time between two simulation steps in minutes, e.g. 1 hour = 60 **/
    public int simulationResolutionMinutes = 60;
    /** Realtime between two simulationsteps **/
    public int interSimStepMiliSec = 1000;

  
    public Weather currentWeather = new Weather() ;
	
    public final CSVController csvController ;
	
	
	/** weather changes at least once every X minutes, but may be the same weather. **/
	private final int weatherChangeLimit = 60;
	public SpeedChanger sliderDialog;
	

    
	/**
	 * Initialize and start time thread.
	 * Sets reference in GridModel
	 * 
	 * @param gridController
	 * @param buttonController
	 * @param csvController 
	 */
	public TimeThread(GridController gridController, ButtonController buttonController, CSVController csvController) {
		
		this.gridController = gridController;
		this.gridModel = gridController.gridModel;
		gridModel.timeThread = this;
		this.buttonController = buttonController;
		
		this.csvController = new CSVController(gridModel);
//		this.csvController =  csvController;
		
		this.changeAlgorithm( new SimplePowerDistribution(gridController) );	// initialize algorithm
		
		currentTime = new GregorianCalendar();
		currentTime.setTimeZone(TimeZone.getTimeZone("CET"));
		currentTime.setTime(new Date(System.currentTimeMillis()));
		buttonController.updateDateAndTime(currentWeather, currentTime);
	
		this.start();
	}
	
	/**
	 * Start or stop time thread.
	 * 
	 * @param start
	 */
	public synchronized void startOrStop(boolean start){
		doRun = start;
		MenuController.openSlider.setEnabled(doRun); //reveal the openSlider in the Menubar
		
		if(start){
			
//			TODO: make Seed setable by GUI 
			currentWeather.setRandSeed( 1000 );
			
			algorithm.simulationStarted();
			isRunning = true;
		}
		else {
			algorithm.simulationStopped();
			try{
				buttonController.resetStartStopSimButton();
			}
			catch(Exception e){ throw new RuntimeException("Do not forget to set the button controller - "+e.getMessage()); }

			if (realTime) {
				if(this.sliderDialog != null)
					this.sliderDialog.setVisible(false);
				
//				gridController.closeAllObjectParameterWindows();
			}
		}
		this.notifyAll();
	}
	/**
	 * @return true if algorithm is currently still doing something or should start to do something.
	 */
	public static boolean isRunning(){
		return isRunning;		// == isRunning || doRun
	}
	
    /**
     * @return the currently used algorithm.
     */
    public AbstractDistributionAlgorithm getAlgorithm(){
    	return algorithm;
    }
    /**
     * Change the used algorithm.
     * - does not stop running simulations 
     * - initializes the algorithm.
     * @param newAlgorithm
     */
    public synchronized void changeAlgorithm(AbstractDistributionAlgorithm newAlgorithm){
    	
    	if(this.algorithm != null){
        	Out.pl("Algorithm change: stopping " + this.algorithm);
        	//startOrStop(false);		// should simulation be stopped at change?
        	gridController.closeAllObjectParameterWindows();
        	algorithm.simulationStopped();
        	algorithm.unloadAlgorithm();
    	}
    	
    	newAlgorithm.initializeFull();			// init variables and stuff
    	algorithm = newAlgorithm;				// set algorithm
    	gridController.gridMapListener.setGridChangeListenerAlgorithm(newAlgorithm);	// gets message at change
    	gridController.gridMapListener.networkAnalyser.createBusesAndFindPaths();		// calculate buses and paths depending on algorithm
    	if(doRun)
    		newAlgorithm.simulationStarted();	// start simulation if running (fast change)
    	
    	this.notifyAll();
    	Out.pl("Algorithm changed: " + newAlgorithm);
    }

	@Override
	/**
	 * @throws IOException e if not possible FileChooserController.createCSVController
	 */
	public void run() {

		long t0,t1;
		t0 = System.currentTimeMillis();	// start time (real)
			
		while (true) {
			
			if (doRun) {	 // --- Run: ---
				isRunning = true;

				
				if (currentTime.getTime().getTime() < endTime.getTime().getTime()) {	 // --- Run (time): ---
					//t0 = System.currentTimeMillis();	// start time (real)
					simStep();							// --- simulation step ---
					
					
					synchronized (this) {
						if (realTime) {
							
							t1 = System.currentTimeMillis();	// finish time (real)
							
							long sleepMS;
							if(interSimStepMiliSec >= 0)
								sleepMS = getSleepTimeMS(interSimStepMiliSec,t0,t1);	// time to sleep (real)
							else
								sleepMS = Long.MAX_VALUE;
							
							//System.out.println("--- --- timethread --- --- --- --- --- --- ");
							System.out.println("--- Timethread milliseconds to sleep: " + sleepMS + " --- ");
							//System.out.println(" = "+interSimStepMiliSec+"*1000 - ("+t1+" - "+t0+")");
							
							while (sleepMS > 0) {
								try{
									wait(sleepMS);	// use sleep time
									break;			// exit loop @ end of time OR notify
								} catch(InterruptedException ex){		// re-measure time at interrupt:
									t1 = System.currentTimeMillis();
									sleepMS = getSleepTimeMS(interSimStepMiliSec,t0,t1);
									System.out.println("--- ...milliseconds to sleep: " + sleepMS + " --- ");
								};
							}
							
							t0 = System.currentTimeMillis();	// start time (real) // ~ t1+sleep
						}
					}

					currentTime.add(Calendar.MINUTE, simulationResolutionMinutes);
					buttonController.updateDateAndTime(currentWeather, currentTime);
				
				} else {	 // --- End of simulation time: ---
//					synchronized (this) {
//						System.out.println("simulation stops");
//						if(SgsGridModel.saveSimResultsToFile){
//							algorithm.writeToFile();
//						}
						startOrStop(false);
//					}
				}
				
			} else {		// --- Not running: ---
				isRunning = false;
				
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException ex) { ex.printStackTrace(); }
			}
			
		}//_while_true_
	}
	
	/**
	 * getter for boolean doRun
	 * @return doRun value
	 */
	 public boolean isDoRun() {
		return doRun;
	}

	 /**
	  * Setter for doRun Boolean
	  * @param doRun
	  */
	public void setDoRun(boolean doRun) {
		this.doRun = doRun;
	}
	
	
	
	/**
	 * 
	 * @param interSimStepMiliSec - time to sleep in seconds
	 * @param t0 - start, time before algorithm execution
	 * @param t1 - stop, time before sleep
	 * @return time left to sleep
	 */
	private static long getSleepTimeMS(long interSimStepSec, long t0, long t1){
		long sleepMS = ( (interSimStepSec - (t1 - t0)) );
		return sleepMS;
	}
	
	/**
	 * A single simulation step, locking Buses and Paths.
	 * Also sets production according to weather and time.
	 * Repaints View/Grid.
	 */
	private void simStep() {
		 	
		changeWeather();
		updateGridObjects(currentTime, currentWeather);
		
		gridModel.lockBusesPaths();
		algorithm.calculationStep();
//		TODO aufruf
		csvController.csvModel.createCSVLine(ProgramConstants.df.format(currentTime.getTime()).toString(), currentWeather);
//		csvController.makeCSVLine(currentTime, currentWeather);
		gridModel.unlockBusesPaths();
		
		//TODO ich brauche eine Verbindung zum ColorOverlayController
//		gridController.repaintData(); //repaint ColorOverlay
		//connection to the ColorOverlayController an his repaint() method 
		gridController.colorOverlayController.repaintData(); //repaint colorOverlay
		
	}
	
	/** Change 'currentWeather' randomly depending on 'weatherChangeLimit' **/
	private void changeWeather(){
		
		int minutesPassed = simulationResolutionMinutes;
	//	int randMinutes = RAND.nextInt(weatherChangeLimit);
		
		//if(minutesPassed >= randMinutes){
		//	WEATHER[] values = WEATHER.values();
		//	int index = RAND.nextInt(values.length);
	    //	currentWeather = values[index];
		//}			
		
		if (minutesPassed >= weatherChangeLimit){
			currentWeather.newCloudFactor();
			currentWeather.newWindSpeed();
		}
		
	}
	
	
    /**
     * Set production of all SmartSimObjects to weather and time.
     */
    private void updateGridObjects(GregorianCalendar currentTime, Weather weather) {

    	LinkedList<SmartGridObject> gridObjectList = gridModel.gridObjectList;

    	for (SmartGridObject temp : gridObjectList) {
//    		if(temp.getEnum().getMappedModel() != null){  // TODO: check if this 4 lines ca be moved to SGObject class or subclasses
//    			temp.getModel().updateModel(currentTime, weather, simulationResolutionMinutes);
//    		}
//    		else{
//    			temp.setProductionToWeatherAndTime(currentTime, weather);
//    		}
    		temp.updateObject(currentTime, weather, simulationResolutionMinutes);
    	}
    }
	
}