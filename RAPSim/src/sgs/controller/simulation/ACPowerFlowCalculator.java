package sgs.controller.simulation;

import java.util.ArrayList;
import java.util.LinkedList;

import main.StatKeeper;
import sgs.controller.GridController;
import sgs.controller.simulation.calculation.GaussSeidelPFSolver;
import sgs.model.ProgramConstants;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.Consumer;
import sgs.model.gridObjects.GridPower;
import sgs.model.gridObjects.PowerLine;
import sgs.model.gridObjects.PowerPlant;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.simulation.Bus;
import sgs.model.simulation.Path;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;

/**
 * Distributes and calculates the whole power in the system.
 * 
 * @author Lukas Felician Krasel, Manfred Pöchacker, Kristofer Schweiger
 *
 */
public class ACPowerFlowCalculator extends AbstractDistributionAlgorithm {

	@SuppressWarnings("unused")
	private final GridController gridController;
	private final SgsGridModel gridModel;
	
	/** option to define generator bus:
	 * CONTAINS_GENERATOR (default)
	 * ISNETTO_PRODUCER
	 * **/ 
	public enum generatorBusOptions{CONTAINS_GENERATOR,ISNETTO_PRODUCER};
//	public generatorBusOptions genBusOption = generatorBusOptions.ISNETTO_PRODUCER; 
	public generatorBusOptions genBusOption = generatorBusOptions.CONTAINS_GENERATOR; 


//	public static boolean TEST_OVERLAYS = false;
//	IterativeDCVoltageCalculator iDCVoltageCalculator;
	private final GaussSeidelPFSolver acGSSolver;
	@SuppressWarnings("unused")
	private final StatKeeper statKeeper;

	private busTypes[] typesOfBuses = null;
	// matrices & vectors
	private NumericValue[][] Y; // admittance matrix
	
	/** Swing bus defined specifically for this algorithm **/
	public int swingBusNumber;
	//	Vector U; // voltage
//	Vector P; // power
	private static NumericValue DEFAULT_VALUE = new NumericValue(1.0, 0);

	
	public ACPowerFlowCalculator(GridController gridController) {
		this.gridController = gridController;
		this.gridModel = gridController.getModel();
		
//		if(TEST_OVERLAYS){
//			this.gridModel.overlayMode = OverlayMode.BY_ALGORITHM;
//		} else {
//			this.gridModel.overlayMode = OverlayMode.AUTOMATIC;
//		}
		
		statKeeper = new StatKeeper();
		acGSSolver = new GaussSeidelPFSolver();
	}

	
	//is there a need for public? - Not if only used internally, but it's not... [Krisi].
	public enum busTypes{REFERENCE_BUS,LOAD_BUS,GENERATOR_BUS};
	
	
	@Override
	public void initializeAlgorithm() {
		
		// TODO track changes in paths and execute function only in case of changes
		Y = createAdmittanceMatrix(gridModel.paths);
		
		if ( typesOfBuses == null ){
			typesOfBuses = new busTypes[ gridModel.buses.size() ];	
			setBusTypes();  // REFERENCE_BUS by default to 0-power bus or lowest number GENERATOR bus
		} else {
			updateBusTypes(); // calls setBusTypes but keeps the settings of the previous simulation
		}
	}
	
	
	@Override
    protected void setPropertiesForVariables() {
    	
    	this.selectClass( SmartGridObject.class );
    	this.setProperties(true, false, EnumPV.voltageAngle, EnumPV.currentVoltage);
    	
    	this.selectClass( PowerPlant.class );
		this.setProperties(true, false, EnumPV.powerProduction);		// Uses
		this.setProperties(true, true, EnumPV.nominalVoltage);		// Uses	
//		this.setProperties(true, true, EnumPV.voltageAngle);// Uses
    	
		this.selectClass( GridPower.class);
		this.setProperties(true, true, EnumPV.currentVoltage, EnumPV.voltageAngle, EnumPV.nominalVoltage);
		this.setProperties(true, false, EnumPV.powerProduction, EnumPV.powerConsumption);
		
    	this.selectClass( Consumer.class );
    	this.setProperties(true, false, EnumPV.powerConsumption);
    	this.setProperties(false, true, EnumPV.powerDemand);  // is used in Algorithm but no need to be visible as well
    	
    	this.selectClass( PowerLine.class );
    	this.setProperties(true, true, EnumPV.resistance, EnumPV.powerLineCharge);
    	this.setProperties(true, false, EnumPV.currentPowerLoss, EnumPV.currentPowerTransport);
//    	this.setProperties(false, false, EnumPV.currentVoltage, EnumPV.voltageAngle);
    	
//    	this.selectClass( Connector.class );   // current Algorithm can not handle maxValues
//    	this.setProperties(true, false, EnumPV.maxPowerTransport);
    }
	
	
	@Override
	protected void setPropertiesForBusAndPath() {
		super.setDefaultPropertiesForBusAndPath(gridModel);
		
		//gridModel.pathVariableCollection.add(EnumPV.powerLineCharge);
	}
	
	@Override
	public void gridChanged() {
		this.initializeAlgorithm();
	}

	public String data;
	
	/**
	 * execution of a calculation step (= feeding the solver, write results back) 
	 */
	@Override
	public void calculationStep() {
		
//		boolean oldOn=true;
//		 oldOn=false;
				
		System.out.println("Calc--------------------------------------------------------");

//		for (SmartGridObject o : gridModel.gridObjectList) {
//			o.resetCurrentPowerConsumption();
//		}

//		calculatePathResistance();
//		initialize();    // calc Y / set bustypes
		
//		if ( typesOfBuses.length != gridModel.buses.size()  ){
			
			Y = createAdmittanceMatrix( gridModel.paths );
			updateBusTypes();

//		}
		
//		testConnection();   // TODO create method to check if network is connected
		
//		Bus.busTypes[] typesOfBuses = new Bus.busTypes[ gridModel.buses.size() ];		
		
//		typesOfBuses  = defineBusTypes();	
		
		acGSSolver.setBusTypes( typesOfBuses );		
		acGSSolver.setAdmittanceMatrix( Y );
		acGSSolver.setPowerVector( getPQ() );
		acGSSolver.setVoltageVector( getU() );
		
		try {
			acGSSolver.solve();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setPowerToBuses( acGSSolver.getPowerVector() );
		setVoltageToBus( acGSSolver.getVoltageVector() );
		setPowerToPaths(acGSSolver.getScaleFactor());
		
		// --- write log ---
//		String timeWeather = ProgramConstants.df.format(gridModel.timeThread.currentTime.getTime()) +" ("+gridModel.timeThread.currentWeather+")";
		String timeWeather = ProgramConstants.df.format(gridModel.timeThread.currentTime.getTime()) +" \t "+gridModel.timeThread.currentWeather.getCloudFactor() +" ";
		String l = timeWeather + "\t";
		
		
//		for(Bus b: gridModel.buses){
//			
//			l = l + b.getBusVoltage().abs() + " \t";
//			l = l + Math.acos(b.getBusVoltage().getReal()/b.getBusVoltage().abs()) + " \t";
//			l = l + b.getNettoPowerProduction().getReal() + " \t";
//			l = l + b.getNettoPowerProduction().getImaginary() + " \t";
			
			
			/**
			 * @author hs
			 * make string for csv and sent it to model
			 */
//			
//			String header = generateList();
//			CSVModel.createHead(2, header);
			
			
			
			/**
			 * @author hs
			 * make string for csv and sent it to model
			 */
			//FileChooserModel.createHead(head);		
//			String timeWeather2 = timeWeather.replaceAll("[:.]", ".").replaceAll("[- ()]", " ");
//			data = timeWeather2 + "\t" + b.getNumber() + "\t" + b.getBusVoltage().abs() + "\t" + Math.acos(b.getBusVoltage().getReal()/b.getBusVoltage().abs()) + "\t" + b.getNettoPowerProduction().getReal() + "\t"+ b.getNettoPowerProduction().getImaginary();
			
//		}
//		l = l + "\n";
//		log(l);
		
		
		//FileChooserModel.createCSV(data);

		
		//
		// TODO: statKeeper doesn't contain useful values
		//statKeeper.saveResults(gridModel.simResultFile);
		//statKeeper.clear();
	}
		/**
		 * This method returns an default vector with the size x
		 * 
		 * @param x
		 * @return matrix
		 */
		public static NumericValue[] createDefaultVector(int x) {
			NumericValue[] matrix = new NumericValue[x];
			for (int i = 0; i < x; i++) {
				matrix[i] = DEFAULT_VALUE;
			}
			return matrix;
		}


	
		@Override
		protected void unloadAlgorithm() {
//			this.selectClass( GridPower.class);
//			this.setProperties(true, true, EnumPV.currentVoltage);
		}


	@Override
	public String getDescription() {
		return "This algorithm solves the AC Powerflow problem for the grid. \n" +
				"It uses a list of Buses and Paths given by the Network Analyzer. Each Bus is characterized by four parameters: \n" +
				"  i) active power P and \n" +
				"  ii) reactive power Q, \n" +
				"  iii) voltage magnitude and \n" +
				"  iv) voltage angle (the four could be summarized in two complex variables). \n" +
				"One bus is definded as REFERENCE_BUS (Volt_ang = 0, Volt_Mag = 1 or an other given fixed value), the other buses are either classified as LOAD_BUS (P and Q are given) or as GENERATOR_BUS (P and Volt_Mag are given). " +
				"For each bus the two missing parameters are determined by iteratively solving the set of differential equations S = Y * U^2 (for instance with Gauss-Seidel methode). " +
	
				"This equation is in n dimensions (n=number of buses) where S is the appearant power (S=P+jQ), U is the complex voltage vector and Y is the addmitance matrix (Y_ij contains the addmittance bettween bus i and bus j which is the inverse of the resistance)." +
				"The admittance matrix considers each powerline by its pi-equivalent circuit (the resistance between two buses and a powerLineCharge to ground)." +
				"The REFERENCE_BUS is automatically setted to the bus with the grid connection. Buses with positive NettoPower are GENERATOR_BUS";
	
	}


	@Override
	/**
	 * 
	 */
	public void simulationStarted() {
		if(gridModel.saveSimResultsToFile){
//			this.makeLog(gridModel.programParameters.getSimResultFile());
			String l = "day - time \t cloudiness";
			for(int i = 0;i < gridModel.buses.size();i++) 
				l = l + "\tVoltage" + i + "\tVoltage Angle" + i + "\tReal Power" + i + "\tReactive Power" + i;
			l = l + "\n";
			log(l);
		}
	}


	@Override
	public void simulationStopped() {
		
	}


	/** 
	 * @return true if this bus was defined as the swing bus (only a single swing bus can exist)
	 */
	public boolean isSwingBus(Bus b){
		return b.getBusNumber()==swingBusNumber;
	}


	/**
    * @param returns true if k and j are connected - needs the Y-matrix !! 
    */  

	@SuppressWarnings("unused")
	private boolean busesAreConnected(int k, int j) {

		LinkedList<Integer> conList = getConnectedBuses(k);
		
		return conList.contains(j); 
	}


	/**
		 * find all connected buses by use of the addmitance matrix Y ! 
		 * @param s the number of a bus
		 * @return LinkedList<Int> with all bus-numbers connected to s
		 */
		private LinkedList<Integer> getConnectedBuses(int s){
			
			LinkedList<Integer> conList = new LinkedList<Integer>() ;
			conList.add(s);
			
			boolean isNew = true;
			int tmp1 = 0 ;
			
			while (isNew){
				isNew = false;
				int tmp = conList.size();
				for (int k=tmp1; k<tmp; k++){
					for (int j=0; j<Y.length; j++){
						
						boolean b1 = !Y[conList.get(k)][j].equals(new NumericValue(0,0)); 	// check if there is an entry != zero 
						boolean b2 = !conList.contains(j);				// check if bus is not already in the list
						if ( b1 && b2 ){
							conList.add(j);
							isNew = true;
						}
					}
				}
				tmp1 =tmp;
			}
			return conList;
		}


	//	%%%%%%%%%%%%%%
		
	/** 
	 * creates the addmitance matrix Y from the path-list
	 * TODO can not handle meshed networks only trees!!!
	 * @param admittanceList including
	 * @return two-dimenisonal array  
	 */
	private NumericValue[][] createAdmittanceMatrix(LinkedList<Path> admittanceList) {

			int numberOfBuses = gridModel.buses.size();
			int numberOfPaths = admittanceList.size();
						
			NumericValue[][] zz = new NumericValue[numberOfBuses][numberOfBuses]; // the resistance value matrix
			NumericValue[][] ych = new NumericValue[numberOfBuses][numberOfBuses]; // the line charging matrix
			NumericValue[][] yb = new NumericValue[numberOfBuses][numberOfBuses]; // the Y matrix

			// creating ych
			for (int i = 0; i < numberOfBuses; i++) {
				for (int j = 0; j < numberOfBuses; j++) {
					double v = 0.0;
					int fromBus;
					int toBus;
					// getting lineCharging values from the list
					for (int k = 0; k < numberOfPaths; k++) {
						fromBus = admittanceList.get(k).getBus1().getBusNumber();
						toBus = admittanceList.get(k).getBus2().getBusNumber();
						if ( ( fromBus == i && toBus == j) || fromBus == j && toBus == i) {
							Path path = admittanceList.get(k);
							v = path.getValue( EnumPV.powerLineCharge ).abs();
						}
					}
					ych[i][j] = new NumericValue(0, v);
				}	
			}
			// creating zz
			for (int i = 0; i < numberOfBuses; i++) {
				for (int j = 0; j < numberOfBuses; j++) {
					if (i == j) {
						zz[i][j] = new NumericValue(0d, 0d);
						continue;
					}
					zz[i][j] = getResistanceOfPath(i, j, admittanceList);
				}
			}
			// creating yb
			for (int i = 0; i < numberOfBuses; i++) {
				for (int j = 0; j < numberOfBuses; j++) {
//					if ( zz[i][j].equals( new NumericValue(0d,0d) ) ) {
					if ( zz[i][j].r()==0 && zz[i][j].i()==0 ){   // ( new NumericValue(0d,0d) ) ) {
						yb[i][j] = new NumericValue(0d, 0d);
					} else {
						yb[i][j] = new NumericValue(-1.0, 0.0);
						yb[i][j].divide( zz[i][j] );
					}
				}
			}
			// diagonal of yb
			for (int i = 0; i < numberOfBuses; i++) {
				NumericValue csum = new NumericValue(0, 0);
				NumericValue ysum = new NumericValue(0, 0);
				for (int j = 0; j < numberOfBuses; j++) {
					ysum.add(yb[i][j]);
					csum.add(ych[i][j]);
				}
				yb[i][i] = csum.subtract(ysum);
			}
			return yb;
		}

	
	/**  TODO check/modifiy for parallel paths? 
	 * estimates the impedance (=complex resistance) between bus i and j
	 * @param i, j, admittanceList
	 * @return NumericValue
	 */
	private NumericValue getResistanceOfPath(int i, int j, LinkedList<Path> admittanceList) {
		
		NumericValue d = new NumericValue(0, 0);
		for (Path p : admittanceList){
			int fromBus = p.getBus1().getBusNumber();
			int toBus = p.getBus2().getBusNumber();
			
			if ( fromBus == i && toBus == j || fromBus == j && toBus == i ){
				d.setValue(p.pathObjects.get(0).getResistance());
				//TODO Make resistance readable from path-collection
				//d.setValue( p.getValue(EnumPV.resistance) ) ;
				break;
			}
		}
		return d;
	}
	/**
	 * calls setBusVoltage() for each bus 
	 * @param voltageVector
	 * @see {@link #Bus.setBusVoltage() }
	 */
	private void setVoltageToBus(NumericValue[] voltageVector) {
		int counter = 0;
		for (Bus o : gridModel.buses){
//			voltageVector[counter].setValue( Math.round(voltageVector[counter].getReal()*100000)/100000.0, Math.round(voltageVector[counter].getImaginary()*100000)/100000.0);
			o.setBusVoltage( voltageVector[counter] );							
			
			counter++;
		}
	}

	/**
	 * 
	 * @param powerVector
	 */
	private void setPowerToBuses(NumericValue[] powerVector) {
		int counter = 0;
		NumericValue temp = new NumericValue(0,0);
		
		for (Bus tmpBus : gridModel.buses){
					
			//temp = ;
			temp =  powerVector[counter];
			temp = tmpBus.balanceBusPowerto(temp);
			System.out.println("power difference on "+tmpBus.toString()+" is "+ temp.toString() );
//			temp.subtract( tmpBus.getNettoPowerProduction() );
//			if (temp.getValueDouble() != 0d ){
//				addPowerToBusObjects(tmpBus, temp );
//			}					
			counter++;
		}
	}
	
	
//	 /**
//     * @param addPow: should be positive; 
//     */  
//    private void addPowerToBusObjects(Bus bus, NumericValue addPow){
//    	
////    	if (addPow.equals(new Complex(0,0))){
////    		return;
////    	}
//    		//TODO make work for more power plants within a bus
//       	
//    	for (SmartGridObject o : bus.getGridObjects() ){
//    		if ( o instanceof PowerPlant ){
//    			if ( addPow.getReal()==0d ){  // in case
//    				
//    				addPow.add( o.getPowerProduction() ); // total power in the bus
//    				
//    				
//    				( (PowerPlant) o).setPowerProduction( new NumericValue( ((PowerPlant)o).getPowerProduction().getReal() , addPow.getImaginary()  ) );
//    			}
////    			addPow.add( o.getPowerProduction() ); 
////    			( (PowerPlant) o).setPowerProduction(new NumericValue(((PowerPlant)o).getPowerProduction().r(),addPow.i()));
////    			( (PowerPlant) o).setPowerProduction( new NumericValue(((PowerPlant)o).getPowerProduction()  ) );
//    			
//    			// o.etPowerProduction().add(addPow) ;
//    			//( (PowerPlant) o).setCurrentPowerConsumption( o.getPowerProduction() );
//    			bus.refreshValues();
////    			return;
//    		} 
//    		else if(o instanceof GridPower){
//    			addPow.add( o.getPowerProduction() ).roundValue();
//    			if ( addPow.getReal()>=0d ) {
//					o.setPowerProduction(  addPow ) ;
//					o.setCurrentPowerConsumption( new NumericValue(0d,0d));
//					}  
//				else if (addPow.getReal()<0d) {
//					o.setCurrentPowerConsumption( new NumericValue(-addPow.getReal(),-addPow.getImaginary() ) );
//					o.setPowerProduction( new NumericValue( 0d,0d) ) ;
//					} 
////    			((GridPower)o).setPowerProduction(addPow);
//    			bus.refreshValues();
//    		}
//    		else if (o instanceof Consumer ) {  //&& busType==typesOfBuses.LOAD_BUS) {
//	
//    			o.setCurrentPowerConsumption( o.getPowerDemand() );
//    			bus.refreshValues();
////    			return;
//    			
//    		}
//    	}
//    		
////    	System.out.println(" No power plant in Bus! No change of power possible.");
//    	
//    }
	
/**
 * Calculates the powerflow and powerloss on every powerline
 * @param scaleFactor: scaleFactor used by the Gauss-Seidel-Solver 
 * @autor Benjamin Breiling 
 */
    private void setPowerToPaths(double scaleFactor){
    	for(Path p: gridModel.paths){
    		NumericValue admittance = new NumericValue(1.0,0.0).divide(p.pathObjects.get(0).getResistance());
    		NumericValue voltageDifference = p.getBus1().getBusVoltage().copy().subtract( p.getBus2().getBusVoltage() );
    		voltageDifference.roundValue(5);
    		NumericValue current = admittance.multiply(voltageDifference);
    		current.roundValue(5);
    		NumericValue powerLoss = voltageDifference.copy().multiply(current.copy().conjugate());
    		powerLoss.roundValue(5);
    		NumericValue powerTransport = p.getBus1().getBusVoltage().copy().multiply(current.copy().conjugate());
    		powerTransport.roundValue(5);
    		((PowerLine)p.pathObjects.get(0)).setCurrentVoltage(voltageDifference);
    		((PowerLine)p.pathObjects.get(0)).setCurrentCurrent(current);
    		((PowerLine)p.pathObjects.get(0)).setCurrentPowerLoss(powerLoss);
    		((PowerLine)p.pathObjects.get(0)).setCurrentPowerTransport(powerTransport);
    	}
    }
/**
 * copies the REFERENCE_BUS entries into a new blank "typesOfBuses"-vector and calls "setBusTypes()"
 * @see {@link #setBusTypes()}
 */
    private void updateBusTypes() {
	
//		busTypes[] tmpBT = typesOfBuses;	
		typesOfBuses = new busTypes[ gridModel.buses.size() ];
//		
//		for (int cnt=0; cnt < tmpBT.length; cnt++ ){
//			if (tmpBT[cnt] == busTypes.REFERENCE_BUS ){ 
//				typesOfBuses[cnt] = tmpBT[cnt];
//		}
//		}
		setBusTypes();
	}

/**
 * set one REFERENCE_BUS for each grid-island
 */
	@SuppressWarnings("unused")
	private void setBusTypes() {
		
		//	set LOAD and GENERATOR buses
		//	TODO set two SwingBuses in case of two separated grids ?
		
		ArrayList<Integer> genBusIndex = new ArrayList<Integer>();		// all bus that have a generator
		ArrayList<Integer> swingBusIndex = new ArrayList<Integer>();    // is included in genBusIndex
		
		NumericValue tmpPQ;
		
		for ( int cnt=0; cnt < typesOfBuses.length; cnt++) {	// check all buses 
			
			Bus tmpB =  gridModel.buses.get(cnt);
			tmpB.refreshValues();
			
			// TODO: not reference bus assigned in case there is no GridPower
			if (tmpB.containsInstanceOf( new GridPower() ) ) {
				typesOfBuses[cnt]= busTypes.REFERENCE_BUS ;
				swingBusNumber = cnt;
			}
			
			tmpPQ = gridModel.buses.get(cnt).getNettoPowerProduction();
			
			boolean isGen;
			if (genBusOption == generatorBusOptions.ISNETTO_PRODUCER){
				isGen = gridModel.buses.get(cnt).isNettoProducer(); }
			else {
				isGen = gridModel.buses.get(cnt).containsPowerPlant(); 
			}
			;
//			
			// TODO problem in case of grid modifications
			// set bus to REFERENCE_BUS if it was one or if zero PQ and is a Generator
//			if ( typesOfBuses[cnt] == ( busTypes.REFERENCE_BUS ) ) {
//				swingBusIndex.add(cnt); 
//				
//			} else if ( tmpPQ.equals(new NumericValue(0,0)) && isGen ){
//				typesOfBuses[cnt] = busTypes.REFERENCE_BUS;
//				swingBusIndex.add(cnt); 				
//			}
			
			// set to GEN- or LOAD bus in case of not being a SWING bus
			if ( isGen ) {  
				genBusIndex.add(cnt);
				if ( typesOfBuses[cnt] != busTypes.REFERENCE_BUS) {
					typesOfBuses[cnt]= busTypes.GENERATOR_BUS ;
				}
			} else if ( tmpPQ.getReal() <= 0.0 && typesOfBuses[cnt] != busTypes.REFERENCE_BUS) {
				typesOfBuses[cnt]= busTypes.LOAD_BUS;
//			} else if ( tmpPQ.getReal() >= 0  &&  typesOfBuses[cnt] != Bus.busTypes.SWING_BUS) {
			}  
		}			
	}
	

	/**
	 * collects voltage values for each bus, refresh the bus values
	 * @return array with NumericValue
	 */

	private NumericValue[] getU() {
		
		NumericValue[] uVector = new NumericValue[ gridModel.buses.size() ];
		int counter = 0; 
	
		for ( Bus b : gridModel.buses ){
			
			b.refreshValues(); 
			
			busTypes busType = typesOfBuses[counter];			
			switch (busType){
				case LOAD_BUS: 
					uVector[counter] = new NumericValue( b.getBusVoltage().abs(), 0d );
					break;
				
				case GENERATOR_BUS: 
					uVector[counter] = new NumericValue( b.getBusVoltage().abs(), 0d );
					break;
					
				case REFERENCE_BUS: 
					uVector[counter] =  new NumericValue( b.getBusVoltage().abs(), 0d );
					System.out.println(uVector[counter]);
					break;
			}
			
			counter++;
		}
		return uVector;
	}
		
	
	/**
	 * collects power values for each bus, refresh the bus values
	 * @return array with NumericValues
	 */
	private NumericValue[] getPQ() {
		
		NumericValue[] pVector = new NumericValue[ gridModel.buses.size() ];
		int counter = 0; 
	
		for ( Bus b : gridModel.buses ){
			
			b.refreshValues();
			
			busTypes busType = typesOfBuses[counter];			
			switch (busType){		// TODO: Use functions with if and else.
				case REFERENCE_BUS: 
					pVector[counter] = new NumericValue(0d,0d);
					break;
				case GENERATOR_BUS: 
					pVector[counter] = new NumericValue( b.getNettoPowerProduction().getReal(), 0d );
					break;
				case LOAD_BUS: 
					pVector[counter] =  b.getNettoPowerProduction();
					break;
			}
			counter++;
		}
		
		return pVector;
	}


	/**@author hs
	 * @deprecated function to create a csv file has now its own class
	 * generate a String for the header in the CSV file
	 * @return header String with only true values
	 */
//	public String generateList(){
//		
//		String header = "";
//		
//		if(CSVController.getDateTime())
//			header = header + "Date Time";
//		if(CSVController.getWeather())
//			header = header + "\tWeather";
//		for(int i = 0;i < gridModel.buses.size();i++) {		
//			if(CSVController.getBus1())	
//				header = header + "\tBus" + i;
//			if(CSVController.getNumber())
//				header = header + "\tNumber";
//			if(CSVController.getBusVoltage())
//				header = header + "\tBus Voltage";
//			if(CSVController.getVoltage_Angle())
//				header = header + "\tVoltage Angle";
//			if(CSVController.getNPP_Real())
//				header = header + "\tNPP Real Power";
//			if(CSVController.getNPP_Imaginary())
//				header = header + "\tNPP Imaginary Reactive Power";
//			}
//		
//		return header;
//	}

	

}
