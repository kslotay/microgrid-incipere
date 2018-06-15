package sgs.model.gridObjects;

import sgs.model.gridData.GridDataEnum;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;

/**
 * A SmartGridObject.
 * @author tobi, Kristofer Schweiger
 */
public class PowerLine extends PowerTransport {

	private static final double Default_MaxPowerTransport 		= 10000.0d;
	private static final double Default_TransportPowerDecrement	= 0.01d; // 1 Percent

	/**
	 * Constructor
	 */
	public PowerLine() {
		super();
		super.draggable = false;
		setMaxPowerTransport(Default_MaxPowerTransport);
		setTransportPowerDecrement(Default_TransportPowerDecrement);
		setResistance(new NumericValue(1));
		setPowerLineCharge(new NumericValue(0));
	}
	

	/**
	 * @param maxEnergy
	 */
	public void setMaxPowerTransport(double maxEnergy) {
		//this.maxPowerTransport = maxEnergy;
		super.variableSet.get(EnumPV.maxPowerTransport).setValue(maxEnergy);
	}

	/**
	 * @param energyDecrement
	 */
	public void setTransportPowerDecrement(double transportPowerDecrement) {
		//this.transportPowerDecrement = energyDecrement;
		super.variableSet.get(EnumPV.transportPowerDecrement).setValue(transportPowerDecrement);
	}
	
	public void setCurrentCurrent(NumericValue current){
		super.variableSet.get(EnumPV.currentCurrent).setValue(current);
	}
	
	/**
	 * 
	 * @return variableSet.get(EnumPV.currentCurrent)
	 */
	public SingleVariable getCurrentCurrent(){
		return variableSet.get(EnumPV.currentCurrent);
	}
	
	public void setCurrentPowerLoss(NumericValue currentPowerLoss){
		super.variableSet.get(EnumPV.currentPowerLoss).setValue(currentPowerLoss);
	}
	
	/**
	 * 
	 * @return variableSet.get(EnumPV.currentPowerLoss)
	 */
	public SingleVariable getCurrentPowerLoss(){
		return variableSet.get(EnumPV.currentPowerLoss);
	}
	
	public void setCurrentPowerTransport(NumericValue currentPowerTransport){
		super.variableSet.get(EnumPV.currentPowerTransport).setValue(currentPowerTransport);
	}
	
	/**
	 * 
	 * @return variableSet.get(EnumPV.currentPowerTransport)
	 */
	public SingleVariable getCurrentPowerTransport(){
		return variableSet.get(EnumPV.currentPowerTransport);
	}
	
    // ---------------------------------------------------------
    // --- Override --- ----------------------------------------
	
//	@Override
//	public void setData(ArrayList<String> data) {
//		//maxTransportEnergy = Double.parseDouble(data.get(0));
//		//energyTransportDecrement = Double.parseDouble(data.get(1));
//		
//		powerLineResistance = new Complex(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(2)));
//		powerLineCharge = Double.parseDouble(data.get(3));
//	}

	@Override
	public GridDataEnum getEnum() {
		return GridDataEnum.PLINE;
	}

}