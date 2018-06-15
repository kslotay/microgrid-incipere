package sgs.controller.simulation;

import java.util.LinkedList;

import sgs.controller.GridController;
import sgs.model.ProgramConstants;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.Consumer;
import sgs.model.gridObjects.GridPower;
import sgs.model.gridObjects.PowerLine;
import sgs.model.gridObjects.PowerPlant;
import sgs.model.simulation.Bus;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.collector.VariableCollection;
import sgs.model.variables.collector.VariableCollectorSum;
import testing.Out;



public class ActiveReactivePowerBalance extends AbstractDistributionAlgorithm {

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
	 * @author Manfred Pöchacker 
	 * 
	 */
	public ActiveReactivePowerBalance(GridController gridController){
		this.gridController = gridController;
		this.gridModel = gridController.getModel();
		this.timeThread = gridModel.timeThread;
		Out.pl("> Algorithm constructed...");
		
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
				
				bus.balanceBusPowerto(new NumericValue(0d,0d) );
				
//				bus.refreshValues();		// get newest values for buses and paths
//				
////				log(bus.getNettoPowerProduction().getReal() + " \t "); //Input for Log.file
//				
//				System.out.println(timeWeather +": "+bus+ " netto production = "+bus.getNettoPowerProduction().getReal()); //Output for console
//
//				NumericValue nettoBusPower = bus.getNettoPowerProduction(); // powerProduction minus powerDemand
////				NumericValue totalBusDemand =bus.get;
//				
//				double realBusPower =nettoBusPower.getReal();
//				double imagBusPower =nettoBusPower.getImaginary();
//				
//				if(realBusPower==0 && imagBusPower==0){
//					System.out.println("Bus "+bus+ " in balance");
//				} 
//				else {
//				
//				NumericValue availableBusProduction = bus.getValue(EnumPV.powerProduction);
//				NumericValue totalBusDemand =bus.getValue(EnumPV.powerDemand);
//				
//				boolean gridPowerAvailable =  bus.containsInstanceOf( new GridPower() );
//				
//				
////				System.out.println("Total BusPower: " + nettoBusPower); //Output for console
////				System.out.println("Total BusDemand: " + totalBusDemand);	//Output for console
//				
//				NumericValue consumptionTmp = new NumericValue( 0d,0d);
//				NumericValue productionTmp = new NumericValue( 0d,0d);
//				
//				NumericValue demandSGO  = new NumericValue( 0d,0d);
//				NumericValue prodSGO = new NumericValue( 0d,0d);
//				
//				
//				for(SmartGridObject sgo : bus.busGridObjects){		// for every Bus Object:
//					//sgo.variableSet.get(EnumPV.powerDemand).getValueDouble();
//										
//					if(sgo.variableSet.isUsed(EnumPV.powerDemand)){
//						
//						demandSGO = sgo.getPowerDemand();
//						consumptionTmp = consumptionTmp.add( demandSGO ); 
//						
//						if ( availableBusProduction.getReal() < consumptionTmp.getReal() && !gridPowerAvailable ){
//							demandSGO.subtract( consumptionTmp.copy().subtract(availableBusProduction)	 );
//							if ( demandSGO.getReal()<0 ){
//								demandSGO = new NumericValue(0d,0d);
//							} else {
//							// calculate reduced reactive power
//								double demandReAct = sgo.getPowerDemand().getImaginary() * demandSGO.getReal()/sgo.getPowerDemand().getReal();
//								demandSGO.setImaginary(demandReAct);
//							}
//						}
//						sgo.setCurrentPowerConsumption(demandSGO.roundValue());
//						
//					} 
//					else if(sgo.variableSet.isUsed(EnumPV.powerProduction)){
//						
//						if(sgo instanceof GridPower ){					
//							if ( realBusPower<=0d ) {
//								sgo.setPowerProduction( new NumericValue( -realBusPower,-imagBusPower) ) ;
//								sgo.setCurrentPowerConsumption( new NumericValue(0d,0d));
//								}  
//							else if (realBusPower>0d) {
//								sgo.setCurrentPowerConsumption( nettoBusPower );
//								sgo.setPowerProduction( new NumericValue( 0d,0d) ) ;
//								} 
//						}
//						else {
//						prodSGO = sgo.getPowerProduction().roundValue();
//						productionTmp = productionTmp.add( prodSGO );						
//						if ( totalBusDemand.getReal()<productionTmp.getReal() && !gridPowerAvailable ) {	
//							
//							prodSGO = prodSGO.subtract( productionTmp.copy().subtract(totalBusDemand) ); 							
//							if (prodSGO.getReal()<0){
//								prodSGO = new NumericValue(0d,0d);
//								} 
//							}
//						if (!gridPowerAvailable){
//							// calculate reactive power to set								
//							double prodReAct = totalBusDemand.getImaginary() * prodSGO.getReal() / totalBusDemand.getReal() ;
//							prodSGO.setImaginary(prodReAct);
//							}
//						sgo.setPowerProduction(prodSGO);
//						}
//					}
//					 			
//					
//				}
//			}
			}
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
				
	
				
			}

	@Override
	protected void unloadAlgorithm() {
//		closeLog();		// if somehow not closed/flushed before.
	}

	
	
		@Override
	public void gridChanged() {
		this.initializeAlgorithm();
	}

		@Override
		public void simulationStarted() {
	
		}

	@Override
	public void simulationStopped() {
//		closeLog();		// log ends at simulation end.
	}

	
	
	

//	@Override
//	public void writeToFile() {
//		Out.pl("> Should be writing to file...");
//	}

	@Override
	public String getDescription() {
		return "Balance the equations for active and reactive power. \n"  + 
				"In case of available Grid Connection it takes/gives everything what is needed. \n" + 
				"Otherwise consumption or production are reduced to balance total amount of active power. \n " +
				"The order of objects in reduction is not physical (it is according their apearence in the bus objects list). \n " +
				"Reactive power is ´forced´ to generators according their share of active load. This Algorithm works as SimpleDistribution plus consideration of reactive power." ;
	}

	

}
