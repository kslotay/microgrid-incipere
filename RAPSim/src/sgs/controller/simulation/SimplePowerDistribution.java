package sgs.controller.simulation;

import java.util.LinkedList;
import sgs.controller.GridController;
import sgs.model.ProgramConstants;
import sgs.model.SgsGridModel;
import sgs.model.SgsGridModel.OverlayMode;
import sgs.model.gridObjects.Consumer;
import sgs.model.gridObjects.GridPower;
import sgs.model.gridObjects.PowerLine;
import sgs.model.gridObjects.PowerPlant;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.simulation.Bus;
import sgs.model.variables.EnumPV;
import sgs.model.variables.collector.VariableCollection;
import sgs.model.variables.collector.VariableCollectorSum;
import testing.Out;



public class SimplePowerDistribution extends AbstractDistributionAlgorithm {

	@SuppressWarnings("unused")
	private final GridController gridController;
	private final SgsGridModel gridModel;
	private final TimeThread timeThread;
	private long calcCount=0;
	
	
	public static boolean TEST_OVERLAYS = false;
	
	
//	public static String getData(){
//		
//		String data = ""; 
//		FileChooserModel.createCSV(data);
//		return data;
//	}
	
	
	/**
	 * Constructor for reflection. Must (only) use parameter GridController.
	 * @author Kristofer Schweiger
	 * 
	 */
	public SimplePowerDistribution(GridController gridController){
		this.gridController = gridController;
		this.gridModel = gridController.getModel();
		this.timeThread = gridModel.timeThread;
		Out.pl("> Algorithm constructed...");
		
		if(TEST_OVERLAYS)
			gridModel.overlayMode = OverlayMode.BY_ALGORITHM;		//TODO: testing
		else
			gridModel.overlayMode = OverlayMode.AUTOMATIC;
	}
	
	
	
	
	
	@Override
		public void calculationStep() {
			System.out.println("Calc "+ calcCount++ +" -----------------------------------------------------");
			LinkedList<Bus> buses = gridModel.buses;
			
//			int head = 1; //for spdc
//			
//			FileChooserModel.createHead(head);
			
			
//			String header = generateList();
//			CSVModel.createHead(1, header);
//			
			String timeWeather = ProgramConstants.df.format(timeThread.currentTime.getTime()) +" ("+timeThread.currentWeather+")";
			log(timeWeather +"\t");

			for(Bus bus : buses){		// for every Bus, equal to network in this case: 
				bus.refreshValues();		// get newest values for buses and paths
				
				log(bus.getNettoPowerProduction().getReal() + " \t "); //Input for Log.file
				System.out.println(timeWeather +": "+bus+ " netto production = "+bus.getNettoPowerProduction().getReal()); //Output for console
				
				
				double availableBusPower = bus.getValue(EnumPV.powerProduction).getReal();
				double totalBusDemand =bus.getValue(EnumPV.powerDemand).getReal();
				
				
				System.out.println("Available BusPower: " + availableBusPower); //Output for console
				System.out.println("Total BusDemand: " + totalBusDemand);	//Output for console
				
				double consumptionTmp =0d;
				double productionTmp =0d;
				
				boolean gridPowerAvailable = false;
				
				//search for GridPower-Object and set boolean-Variable
				for(SmartGridObject sgo: bus.busGridObjects){
					if(sgo instanceof GridPower){
						gridPowerAvailable = true;
						break;
					}
				}
				
				
				for(SmartGridObject sgo : bus.busGridObjects){		// for every Bus Object:
					//sgo.variableSet.get(EnumPV.powerDemand).getValueDouble();
					
//					if(TEST_OVERLAYS)
//						sgo.overlays.setOverlaySingle( Overlay.randomOverlay(-2, null, null) );		//TODO: testing
//					
					if(sgo.variableSet.isUsed(EnumPV.powerDemand)){
						
						double demandN = sgo.getPowerDemand().getReal();
						consumptionTmp = consumptionTmp + demandN;
						
						if(availableBusPower < consumptionTmp && !gridPowerAvailable){// if gridPower is used PowerDemand equals PowerConsumption
							demandN = demandN - (consumptionTmp-availableBusPower);
							if(demandN < 0)
								demandN = 0;
						}
						sgo.setCurrentPowerConsumption(demandN);	
						
					} else if(sgo.variableSet.isUsed(EnumPV.powerProduction)){
						
						//if the currect object is type of Gridpower then decide if it's Producer or Consumer and set Values
						
						if(sgo instanceof GridPower){
							if(availableBusPower < totalBusDemand){
								sgo.setPowerProduction(totalBusDemand - availableBusPower);
								sgo.setCurrentPowerConsumption(0);
							}
							else if(availableBusPower > totalBusDemand){
								sgo.setCurrentPowerConsumption(availableBusPower - totalBusDemand);
								sgo.setPowerProduction(0);
							}
							else{
								sgo.setCurrentPowerConsumption(0);	
								sgo.setPowerProduction(0);								
							}
						}
						
						//else deal object as normal PowerPlant and set Power Production
						
						else{
							double productionN = Math.round(sgo.getPowerProduction().getReal()*10000)/10000.0;
							productionTmp += productionN;
														
							if(totalBusDemand < productionTmp && !gridPowerAvailable){ // if GridPower is used then powerProduction is not reduced
								productionN = productionN - ( productionTmp-totalBusDemand );
								if (productionN < 0)
									productionN = 0;
							}
							sgo.variableSet.get(EnumPV.powerProduction).setValue(productionN);	//current production, but not the production written by the object itself.
//							sgo.variableSet.get(EnumPV.powerProductionNeeded).setValue(productionN);	//current production, but not the production written by the object itself.
						}
						
					}					
						
				} //_for busGridObjects
				
			} //_for Buses
			
			log("\r\n");  
		}

	/**
		 * give( readable, editable, variablename) 
		 * variablename is out of EnumPV
		 */
		@Override
		protected void setPropertiesForVariables() {
			this.selectClass( PowerPlant.class );
			this.setProperties(true, false, EnumPV.powerProductionOptimal);
			this.setProperties(true, false, EnumPV.powerProduction);
//			this.setProperties(true, false, EnumPV.powerProductionNeeded);// Calculates
			this.selectClass(GridPower.class);		// Uses
			this.setProperties(true, false, EnumPV.powerProduction);
			this.setProperties(true, false, EnumPV.powerConsumption);
	    	this.selectClass( Consumer.class );		// Uses
	    	this.setProperties(true, false, EnumPV.powerDemand);
	    	this.setProperties(true, false, EnumPV.powerConsumption);			// Calculates
	    	this.selectClass( PowerLine.class );
	    	this.setProperties(false, false, EnumPV.resistance, EnumPV.powerLineCharge);	
	    	
	    	Out.pl("> Variables were set...");
		}

	@Override
	protected void setPropertiesForBusAndPath() {
		// --- resistanceAttributes ---
		//
		gridModel.resistanceAttributes.clear();		// no resistance attributes
		
		// --- busVariableCollection ---
		//
		gridModel.busVariableCollection = new VariableCollection(
				new VariableCollectorSum(EnumPV.powerConsumption),		// result
				new VariableCollectorSum(EnumPV.powerProduction),		// Input 1
				new VariableCollectorSum(EnumPV.powerDemand)			// Input 2
				);
		
		// --- pathVariableCollection  ---
		//
		gridModel.pathVariableCollection = new VariableCollection(
				// none
				);
	}

	//	@Override
	//	public void writeToFile() {
	//		Out.pl("> Should be writing to file...");
	//	}
	
		@Override
			public void initializeAlgorithm() {
				Out.pl("> initializeAlgorithm()");
				
		//		if(SgsGridModel.saveSimResultsToFile){
		//			try {
		//				log = new BufferedOutputStream( new FileOutputStream("./"+SimplePowerDistribution.class.getSimpleName()+"_data.txt"));
		//			} catch (FileNotFoundException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		else{
		//			if(log!=null)
		//				try {
		//					log.close();
		//				} catch (IOException e) {
		//					
		//					e.printStackTrace();
		//				}
		//			
		//			log=null;
		//		}
				
			}

	@Override
	protected void unloadAlgorithm() {
		closeLog();		// if somehow not closed/flushed before.
	}

	//	@Override
	//	public void writeToFile() {
	//		Out.pl("> Should be writing to file...");
	//	}
	
		@Override
	public void gridChanged() {
		this.initializeAlgorithm();
	}

		@Override
		public void simulationStarted() {
			if(gridModel.saveSimResultsToFile){
//				makeLog(gridModel.programParameters.getSimResultFile());
				String l = "day time weather\t";
				for(int i = 0; i < gridModel.buses.size();i++){
					l = l + "NettoPowerProduction " + i + "\t";
				}
				l = l + "\n";
				log(l);
			}
		}

	@Override
	public void simulationStopped() {
		closeLog();		// log ends at simulation end.
	}

	
	
	

//	@Override
//	public void writeToFile() {
//		Out.pl("> Should be writing to file...");
//	}

	@Override
	public String getDescription() {
		return "A simple distribution algorithm. \n " + 
				"Each object in the scenario states its desired consumption or production, respectivelly. \n " + 
				"The algorithm sums up all the available power production and allocates it to the consumers without physical order. (The Last ones in the SGSObjects-List come off badly.) \n " + 
				"The under provided consumers move to red in the color overlay, as well do generators working below capacity.  \n " + 
				"Losses in the powerlines are not considered at all. Dynamics come from production changes due to climate data.";
	}

	
	/**
	 * String generate List for SimplePowerDistribution Algorithmus,
	 * check if that what it can give on information, is also wanted.
	 * @return header	Generate a String header, for the Header in the CSV-File and work as a list with only values of true params
	 */
//	public String generateList(){
//		String header = "";
//	 
//		
//		if(CSVController.getDateTime()){
//			header = header + "Date Time";
//		}
//		if(CSVController.getWeather()){
//			header = header + "\tWeather";
//		}
//		for(int i = 0; i < gridModel.buses.size();i++){
//			if(CSVController.getBus1()){
//				header = header + "\tBus";
//			}
//			if(CSVController.getNPP()){
//				header = header + "\tNettoPowerProduction " + i;
//			}
//			
//		}
//		
//		return header;
//		
//	}
	
}
