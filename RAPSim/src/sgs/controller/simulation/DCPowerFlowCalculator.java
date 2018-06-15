package sgs.controller.simulation;

import java.util.LinkedList;

import Jama.Matrix;
import sgs.controller.GridController;
import sgs.controller.simulation.ACPowerFlowCalculator.busTypes;
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
 * DC-Flow-Algorithm
 * @author Benjamin Breiling
 * @see TheoreticalStuff/DCPowerFlowEquations.pdf
 */

public class DCPowerFlowCalculator extends AbstractDistributionAlgorithm{

	@SuppressWarnings("unused")
	private final GridController gridController;
	private final SgsGridModel gridModel;
	
	private double[][] B;
	private double[]   realBusPower;
	private busTypes[] typesOfBuses;
	
	public DCPowerFlowCalculator(GridController gridController) {
		this.gridController = gridController;
		this.gridModel = gridController.getModel();
	}
	
	@Override
	public void initializeAlgorithm() {
		closeLog();		// using file directly. (?)
		
		// TODO track changes in paths and execute function only in case of changes
		
		typesOfBuses = new busTypes[ gridModel.buses.size() ];	
		setBusTypes();  
	}
	
//	public static String data;
//	
//	public static String getData(){
//		
//		data = "Hallo2";
//		
//		return data;
//	}
	

	@Override
	/**
	 * solves the DC-Power-Flow-Equations
	 * input: (1) Susceptance-Matrix 
	 * 		  (2) Netto real power production on every bus
	 * 
	 * output:(3) real power flow on the power lines
	 * 		  (4) phase angle on the buses
	 * 
	 * @see TheoreticalStuff/DCPowerFlowEquations.pdf
	 */
	public void calculationStep() {
		// TODO Auto-generated method stub
		
//		int head = 3; //for dcpf 	
//		FileChooserModel.createHead(head);
		
		
//		String header = generateList();
//		CSVModel.createHead(3, header);
		
		
		
		
		System.out.println("Calc--------------------------------------------------------");
		typesOfBuses = new busTypes[ gridModel.buses.size() ];	
		setBusTypes();  
		B = createSusceptanceMatrix(gridModel.paths);
		this.realBusPower = getRealBusPower();
		int numberOfReferenceBuses = 0;
		for(int i=0; i<typesOfBuses.length;i++){
			if(typesOfBuses[i].equals(busTypes.REFERENCE_BUS) ){
				numberOfReferenceBuses++;
			}
		}
		int[] matrixRows = new int[gridModel.buses.size() - numberOfReferenceBuses];
		int[] referenceBusNumbers = new int[numberOfReferenceBuses];
		int index = 0;
		int index2 = 0;
		for(int i=0; i<typesOfBuses.length;i++){
			if(!typesOfBuses[i].equals(busTypes.REFERENCE_BUS) ){
				matrixRows[index] = i;
				index++;
			}
			else{
				referenceBusNumbers[index2] = i;
				index2++;
			}
		}
		Matrix Bm = Matrix.constructWithCopy(this.B);
		//Bm.print(3, 3);
		Matrix Bsub = Bm.getMatrix(matrixRows,matrixRows);
		Matrix P = new Matrix(realBusPower,realBusPower.length);
		Matrix Psub = P.getMatrix(matrixRows, 0, 0);
		//System.out.println("Bsub:");
		//Bsub.print(3, 3);
		//System.out.println("Psub:");
		//Psub.print(3, 3);
		Matrix angleMatrix;
		try{
			
		
			angleMatrix = Bsub.solve(Psub);
			//angleMatrix.print(3, 3);

			double [][] D = new double[gridModel.paths.size()][gridModel.paths.size()];
			for(int i = 0; i<gridModel.paths.size();i++){
				for(int j=0;j <gridModel.paths.size();j++){
					D[i][j] = 0;
					if(i == j) D[i][j] = 1.0/gridModel.paths.get(i).pathObjects.get(0).getResistance().getImaginary();
				}
			}
			double [][] A = new double[gridModel.paths.size()][gridModel.buses.size()];
			for(int i=0;i<gridModel.paths.size();i++){
				for(int j=0;j<gridModel.buses.size();j++){
					A[i][j] = 0.0;
					if(gridModel.paths.get(i).getBus1().getBusNumber() == j) A[i][j] = -1;
					else if(gridModel.paths.get(i).getBus2().getBusNumber() == j) A[i][j] = 1;
				}	
			}
			Matrix matD = new Matrix(D);
			Matrix matA = (new Matrix(A)).getMatrix(0, gridModel.paths.size()-1, matrixRows);
		
			Matrix Pb = (matD.times(matA)).times(angleMatrix);
			//Pb.print(3, 3);
			double[][] angle = angleMatrix.getArray();
			double[] pref = new double[referenceBusNumbers.length];

			for(int i=0; i<pref.length;i++){
				double sum = 0.0;
				int angleIndex = 0;
				int refBusIndex = 0;
				for(int j=0;j<B.length;j++){
					if(referenceBusNumbers[refBusIndex] != j){				
						sum = sum + B[referenceBusNumbers[i]][j] * angle[angleIndex][0];
						angleIndex++;
					}
					else{
						if(refBusIndex < referenceBusNumbers.length-1)refBusIndex++;
					}
				}
				pref[i] = sum;
			}
			double[][] pb = Pb.getArray();
			String timeWeather = ProgramConstants.df.format(gridModel.timeThread.currentTime.getTime()) +" ("+gridModel.timeThread.currentWeather+")";
			System.out.println("PowerFlow:");
			for(int i=0;i<pb.length; i++){ 
				System.out.println("Bus"+gridModel.paths.get(i).getBus1().getBusNumber()+"<->"+"Bus"+gridModel.paths.get(i).getBus2().getBusNumber()+": "
						+ Math.round(pb[i][0]*100000)/100000.0 + "W");
			}
			// log values
			String l;
			l = timeWeather + "\t";
			for(int i=0;i<pb.length; i++){
				
				l = l + Math.round(pb[i][0]*100000)/100000.0 + "\t";
			}
			l = l + "\n";
			log(l);
			
			
			int index3 = 0;
			int index4 = 0;
			System.out.println("PhaseAngle:");
			for(int i = 0; i < gridModel.buses.size();i++){
				if(i == referenceBusNumbers[index3]){
					if(index3 < referenceBusNumbers.length-1){
						System.out.println("Bus" + i + ": " + 0);
						index3++;
					}
				}
				else{
					System.out.println("Bus" + i + ": " + angle[index4][0]);
					index4++;
				}
			}
			setPathPower(pb,referenceBusNumbers, pref, angle);
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	
	/**
	 * Sets: (1) the power on every path and reference bus 
	 * 		 (2) the phase angle on every bus
	 * @param angle: Array with the phase angle on every bus
	 * @param pb: Array with bus power for every bus except reference buses
	 * @param referenceBusNumbers: Array with bus numbers of every reference bus
	 * @param pref: array with bus power of every reference bus
	 */
	private void setPathPower(double[][] pb, int[] referenceBusNumbers, double[] pref, double[][] angle) {
		
		// set power transport on every path
		int index = 0;
		for(Path p: gridModel.paths){
			((PowerLine)p.pathObjects.get(0)).setCurrentPowerTransport(new NumericValue(Math.round(pb[index][0]*100000)/100000.0));
			index++;
		}
		// set netto power production and phase angle to every reference bus 
		int index2 = 0;
		for(int i: referenceBusNumbers){
			for(SmartGridObject sgo: gridModel.buses.get(i).busGridObjects){
				if(sgo instanceof GridPower){
					((GridPower)sgo).setPowerProduction(pref[index2]);
					((GridPower)sgo).variableSet.get(EnumPV.voltageAngle).setValue(0.0);
				}
			}
			index2++;	
		}
		// set phase angle on every bus except reference buses
		int index3 = 0;
		int index4 = 0;
		for(int i = 0; i < gridModel.buses.size();i++){
			if(i == referenceBusNumbers[index3]){
				if(index3 < referenceBusNumbers.length-1) index3++;
			}
			else{
				for(SmartGridObject sgo: gridModel.buses.get(i).busGridObjects){
					sgo.variableSet.get(EnumPV.voltageAngle).setValue(angle[index4][0]);
				}
				index4++;
			}
		}
	}

	@Override
	protected void setPropertiesForVariables() {
    	this.selectClass( SmartGridObject.class );
    	this.setProperties(true, false, EnumPV.voltageAngle);
    	this.selectClass( PowerPlant.class );		// Uses
		this.setProperties(true, false, EnumPV.powerProduction);			// Uses 	
    	this.selectClass( Consumer.class );
    	this.setProperties(true, false, EnumPV.powerConsumption);
    	this.selectClass( PowerLine.class );
    	this.setProperties(true, true, EnumPV.resistance, EnumPV.powerLineCharge);
    	this.setProperties(true, false, EnumPV.currentPowerTransport);
		
	}

	@Override
	protected void setPropertiesForBusAndPath() {
		super.setDefaultPropertiesForBusAndPath(gridModel);
		
	}

	@Override
	protected void unloadAlgorithm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gridChanged() {
		this.initializeAlgorithm();
		
	}

	@Override
	public String getDescription() {
		return "This Algorithm approximates the power flow between two buses by making several assumptions\n" +
			   "The Algorithm needs:\n" +
			   "	(1) the susceptance-matrix and \n" +
			   "	(2) the real-power on every bus (except reference buses) \n" +
			   "as input parameter and calculates:\n" +
			   "	(1) the real power flow between two buses\n" +
			   "It\'s important that the susceptance matrix is regular\n";
	}

	@Override
	public void simulationStarted() {
//		if(gridModel.saveSimResultsToFile){
////			this.makeLog(gridModel.programParameters.getSimResultFile());
//			log("Date - Time (Weather)\t");
//			for(int i=0;i<gridModel.paths.size(); i++){
//				log("Bus"+gridModel.paths.get(i).getBus1().getNumber()+"<->"+"Bus"+gridModel.paths.get(i).getBus2().getNumber()+" \t ");
//			}
//			log("\n");
//			
//		}
	}

	@Override
	public void simulationStopped() {
		// TODO Auto-generated method stub
		
	}
	private void setBusTypes() {
		
		//	set LOAD and GENERATOR buses
		//	TODO set two SwingBuses in case of two separated grids ?
		
		  // is included in genBusIndex
		
		NumericValue tmpPQ;
		
		for ( int cnt=0; cnt < typesOfBuses.length; cnt++) {	// 
			
			Bus tmpB =  gridModel.buses.get(cnt);
			
			if (tmpB.containsInstanceOf( new GridPower() ) ) {
				typesOfBuses[cnt]= busTypes.REFERENCE_BUS ;
			}
			
			tmpPQ = gridModel.buses.get(cnt).getNettoPowerProduction();
			boolean isGen = gridModel.buses.get(cnt).containsPowerPlant() ;
			
			// set to GEN- or LOAD bus in case of not being a SWING bus
			if ( isGen ) {  
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
	 * forms the susceptance matrix (susceptance: imaginary part of admittance)
	 * @param paths: powerlines 
	 * @return susceptance matrix as double[][]
	 */
	private double[][] createSusceptanceMatrix(LinkedList<Path> paths) {
		double[][] matrix = new double[gridModel.buses.size()][gridModel.buses.size()];
		
		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix.length;j++){
				matrix[i][j] = 0.0;
			}
		}
		
		for(Path p: paths){
			matrix[p.getBus1().getBusNumber()][p.getBus2().getBusNumber()] = -1.0/(p.pathObjects.get(0).getResistance().getImaginary());
			matrix[p.getBus2().getBusNumber()][p.getBus1().getBusNumber()] = -1.0/(p.pathObjects.get(0).getResistance().getImaginary());
		}
		
		for(int i=0;i<matrix.length;i++){
			double sum = 0.0;
			for(int j=0;j<matrix.length;j++){
				if(j != i)
					sum = sum - matrix[i][j];
			}
			matrix[i][i] = sum;
		}
		return matrix;
	}
	/**
	 * 
	 * @return Array with bus power for every bus
	 */
	private double[] getRealBusPower() {
		double[] power = new double[gridModel.buses.size()];
		for(Bus b: gridModel.buses){
			power[b.getBusNumber()] = b.getNettoPowerProduction().getReal();
		}
		return power;
	}
	
	

}
