package sgs.controller.simulation.calculation;

import java.util.LinkedList;

import javax.swing.JOptionPane;

import sgs.controller.simulation.ACPowerFlowCalculator;
import sgs.model.variables.NumericValue;

/**
 * This class is used to solve the AC-power flow problem according the Gauss-Seidel method.
 * It needs vectors for power P, voltage V, busTypes and the admittance matrix Y.
 * 
 * @author Lukas Felician Krasel, Manfred Pöchacker
 * @param maximumIterations gives number of steps when solver is terminated in case convergenceAcuracy can not be reached 
 * @param convergenceAcuracy is the accuracy requirenmet that iteration can be stopped
 * 
 */

public class GaussSeidelPFSolver {

	//private final double DEFAULT_U_GEN = 1.05;
	//private final double DEFAULT_U_LOAD = 1;

//	GlobalEnergyDistributionAlgorithm gEDA;

	private NumericValue Y[][]; // admittance
	private NumericValue P[]; // power
	private NumericValue U[]; // voltage

	private ACPowerFlowCalculator.busTypes[] busTypes;

	private int noOfBuses;

	private int swingIndex = -1;
	private boolean errorMessageSend=false;

	
	private static NumericValue DEFAULT_VALUE = new NumericValue(1.0, 0);
	private final double confergenceAcuracy = 1e-6;
	private final int maximumIterations = 100;
	private double scaleFactor;
	
	public static final double d2r = Math.PI / 180; // degree into radiant
	public static final double w = 100 * Math.PI;

	public GaussSeidelPFSolver() {
		
//public IterativeACVoltageCalculator(GlobalEnergyDistributionAlgorithm gEDA) {
//		this.gEDA = gEDA;
		Y = null;
		P = null;
		U = null;

		busTypes = null;
	}

	/**
	 * Solves the AC-Power-Flow Problem by using the iterative Gauss-Seidel-Method
	 * Bus-Voltage is set to 1.0 initial
	 * For every load bus, the voltage is calculated
	 * For every generator bus, the Voltage angle and Reactive Power are calculated
	 * For every reference bus, real and reactive power are calculated
	 * @throws Exception
	 */
	public void solve() throws Exception {
		// this method is written like this because of a model written in
		// mathlab
		noOfBuses = P.length;
		int checkInput = 0;
		
		// check requirements for the algorithm 
		
		for (int i = 0; i < noOfBuses; i++) {
			if ( busTypes[i] == ACPowerFlowCalculator.busTypes.REFERENCE_BUS) {
				swingIndex = i;
				if ( U[i].isNaN() || U[i].equals(new NumericValue(0,0)) ) checkInput=1;  // Swing bus has NAN or ZERO voltage				
			} 
			else if (busTypes[i] == ACPowerFlowCalculator.busTypes.LOAD_BUS) {
				U[i] = DEFAULT_VALUE;
				if( P[i].equals(new NumericValue(0,0)) ) checkInput=2;				// A load bus has ZERO Load 
			}
			else if (busTypes[i] == ACPowerFlowCalculator.busTypes.GENERATOR_BUS) {
				if ( /* P[i].getReal()==0 ||*/  U[i].abs()==0 ) checkInput=3;			// A gen bus has ZERO real power or ZERO voltage
			}	
		}
		
		if (swingIndex < 0) checkInput = 4;
			// their is NO swing bus defined at all
		if ( P.length != U.length || P.length != Y.length ) checkInput = 5;		// size problem  
		
		
		if ( !errorMessageSend && checkInput != 0 ){
			JOptionPane.showMessageDialog(null, "Input missing, Failure TYPE " + checkInput,
					"missing SWING BUS " , JOptionPane.ERROR_MESSAGE);
			
			errorMessageSend = true;
//			return;
		}
		
		// 	Algorithm does not converge for high numbers -> rescaling of power values
		this.scaleFactor = Math.pow( 10, calcScaleExponent() );
		
		
		// initialize arrays

		double[][] g = new double[noOfBuses][noOfBuses]; // real(Y)
		double[][] b = new double[noOfBuses][noOfBuses]; // imaginary(Y)
		double[] p = new double[noOfBuses]; // real(P)
		double[] q = new double[noOfBuses]; // imaginary(P)
			
		NumericValue[] s = new NumericValue[noOfBuses]; // P
		NumericValue[] v = new NumericValue[noOfBuses]; // U
		
		for (int i = 0; i < noOfBuses; i++) {
			for (int j = 0; j < noOfBuses; j++) {
				g[i][j] = Y[i][j].getReal();
				b[i][j] = Y[i][j].getImaginary();
			}
			p[i] = P[i].getReal()/scaleFactor;
			q[i] = P[i].getImaginary()/scaleFactor;
			v[i] = U[i];
		}

		
		double del = 1;
		int indx = 0;
		
		NumericValue tmp1;
		NumericValue tmp2;
		NumericValue sm;
		
		double delp[] = new double[noOfBuses];
		double delq[] = new double[noOfBuses];
		LinkedList<Double> delpq = new LinkedList<Double>();
		
		
		// iterations
		 while (del > confergenceAcuracy && indx < maximumIterations ) { //iteration loop
			 
			// LOAD
			for (int i = 0; i < noOfBuses; i++) {
				if (busTypes[i] != ACPowerFlowCalculator.busTypes.LOAD_BUS)
					continue;

				tmp1 = new NumericValue(p[i],-q[i]);
				v[i].conjugate();
				tmp1.divide(v[i]);
				tmp2 = new NumericValue(0d,0d);

				for (int k = 0; k < noOfBuses; k++) {
					if (i == k)
						continue;
					NumericValue tmp3 = v[k].copy();
					tmp3.multiply( Y[i][k] );
					tmp2.add(tmp3);
				}

				NumericValue vt = tmp1;
				vt.subtract(tmp2);
				vt.divide(Y[i][i]);
				v[i] = new NumericValue(vt.copy());
			}

			// GENERATOR
			for (int j = 0; j < noOfBuses; j++) {
				if (busTypes[j] != ACPowerFlowCalculator.busTypes.GENERATOR_BUS)
					continue;

				tmp1 = new NumericValue(0d, 0d);

				for (int i = 0; i < noOfBuses; i++) {
					NumericValue tmp3 = v[i].copy();
					tmp3.multiply( Y[j][i]);
					tmp1.add(tmp3);
				}
				NumericValue tmp4 = v[j].copy();
				tmp4.conjugate();
				tmp4.multiply(tmp1);
				double qj_imaginary = -tmp4.getImaginary();
				
				tmp1 = new NumericValue( p[j], -qj_imaginary );
				tmp4 = v[j].copy();
				tmp4.conjugate();
				tmp1.divide(tmp4);
				
				tmp2 = new NumericValue(0d, 0d);
				for (int k = 0; k < noOfBuses; k++) {
					if (busTypes[k] == ACPowerFlowCalculator.busTypes.GENERATOR_BUS)
						continue;
					NumericValue tmp3 = v[k].copy();
					tmp3.multiply( Y[j][k]);
					tmp2.add(tmp3);
				}

				NumericValue vt = tmp1;
				vt.subtract(tmp2);
				vt.divide(Y[j][j]);
				double sc = v[j].abs() / vt.abs();
				vt.multiply(new NumericValue(sc,0));
				v[j] = new NumericValue(vt.copy());
			}

			// P-Q (S)
			for (int i = 0; i < noOfBuses; i++) {

				sm = new NumericValue(0d, 0d);

				for (int k = 0; k < noOfBuses; k++) {
					NumericValue tmp3 = v[k].copy();
					tmp3.multiply( Y[i][k]);
					sm.add(tmp3);
				}	
				tmp1 = v[i].copy();
				sm.conjugate();
				tmp1.multiply(sm);
				s[i] = new NumericValue(tmp1);
			}

			// mismatch

			for (int i = 0; i < noOfBuses; i++) {
				s[i].conjugate();
				delp[i] = p[i] - s[i].getReal();
				delq[i] = q[i] + s[i].getImaginary();
			}

			delpq.clear();
			
			for (int i = 0; i < noOfBuses; i++) {
				if (busTypes[i] != ACPowerFlowCalculator.busTypes.REFERENCE_BUS) {
					delpq.add(delp[i]);
				}
				if (busTypes[i] == ACPowerFlowCalculator.busTypes.LOAD_BUS) {
					delpq.add(delq[i]);
				}
			}
			
			del = Math.abs(delpq.get(0)) ;
			for (int i = 1; i < delpq.size(); i++) {
				if ( Math.abs(delpq.get(i)) > del ) {
					del= Math.abs(delpq.get(i));
				}
			}
			
//			System.out.println( indx + " Delta = "+ del );
			
			indx++;
			/*for (int i = 0; i < noOfBuses; i++) {
				System.out.println(i + ": " + v[i].abs()+ " / " + v[i]);
			}*/
			
			
		}	// end iteration loop
		 

		System.out.println("iterations:      " + indx);
		System.out.println("U:------------------");
		for (int i = 0; i < noOfBuses; i++) {
			System.out.println(i + ": " + v[i].abs()+ " / " + v[i]);
		}
		System.out.println("S:------------------");
		for (int i = 0; i < noOfBuses; i++) {
			s[i].multiply(new NumericValue(scaleFactor,0));
			System.out.println(i + ": " + s[i]);
		}
		
//		writeback
		writeBackPandU( s ,v);
		
	}
	
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * 
	 * @return cnt
	 */
	private double calcScaleExponent() {
		double f = 1;
		for (int i=0; i<P.length; i++){
			if ( f < Math.abs(P[i].getReal()) ) f = Math.abs(P[i].getReal());
			if ( f < Math.abs(P[i].getImaginary()) ) f = Math.abs(P[i].getImaginary());
		}
		int cnt = 0;
		while ( f > 1 ){
			f = f/10;
			cnt++;
		}
	
		return cnt;
	}

	
	/**
	 * 
	 * @param p
	 * @param u
	 */
	private void writeBackPandU(NumericValue[] p, NumericValue[] u) {
		double tmp;
		for (int cnt=0; cnt < noOfBuses; cnt++ ){
			
			if ( busTypes[cnt].equals( ACPowerFlowCalculator.busTypes.REFERENCE_BUS ) ){
				P[cnt].setValue( roundCtoNumVal( p[cnt])  );
				
			} else if ( busTypes[cnt].equals( ACPowerFlowCalculator.busTypes.GENERATOR_BUS ) ){
				tmp = p[cnt].getImaginary()+P[cnt].getImaginary(); 
				P[cnt].add(  roundCtoNumVal(new NumericValue( 0.0 , tmp))  );
				
				u[cnt].multiply( new NumericValue(U[cnt].abs()/u[cnt].abs(),0) );
				U[cnt].setValue(u[cnt]);
								
			} else if ( busTypes[cnt].equals( ACPowerFlowCalculator.busTypes.LOAD_BUS ) ){
				U[cnt] = roundCtoNumVal( u[cnt] );
			}
		}		
	}

	
	
	/**
	 * 
	 * @param value
	 * @return NumericValue(tmp)
	 */
	private NumericValue roundCtoNumVal(NumericValue value) {
//		double accuracy = 10000; 	
//		return new NumericValue (   Math.round( accuracy*value.getReal() ) /accuracy,  Math.round( accuracy*value.getImaginary() )/accuracy  );
		return value.roundValue();
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	public void setAdmittanceMatrix(NumericValue[][] y) {
		this.Y = y;
	}

	public NumericValue[][] getAdmittanceMatrix() {
		return Y;
	}

	public void setPowerVector(NumericValue[] pq) {
		P = pq;
		
	}

	public NumericValue[] getPowerVector() {
		return P;
	}

	public void setVoltageVector(NumericValue[] u2) {
		U = u2;
		
	}

	public NumericValue[] getVoltageVector() {
		return U;
	}

	public void setBusTypes( ACPowerFlowCalculator.busTypes[] bT) {
//		for (int k=0; k < bT.length; k++ ) 	busTypes[k] = bT[k];
		busTypes =bT;
	}

	/**
	 * 
	 * @return busTypes
	 */
	public ACPowerFlowCalculator.busTypes[] getBusTypes() {
		return busTypes;
	}

}
