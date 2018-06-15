package sgs.model.simulation;

import java.util.LinkedList;

import sgs.model.gridObjects.SmartGridObject;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.VariableSet;
import sgs.model.variables.collector.VariableCollection;
import sgs.model.variables.collector.VariableOwner;

/**
 * A path connects buses separated by resistances.
 * 
 * @author Schweiger
 */
public class Path implements VariableOwner {

	private Bus bus1;
	private Bus bus2;
	public LinkedList<SmartGridObject> pathObjects;
	public VariableCollection variableCollection;
	private VariableSet tmpVariableSet;
	
	public LinkedList<SmartGridObject> notVisitedTmp = null;

	/**
	 * Constructor: Full, TODO: VariableCollection
	 * 
	 * @param busA - not null
	 * @param busB - not null
	 * @param pathObjects
	 * @param variableCollection
	 */
	public Path(Bus busA, Bus busB, LinkedList<SmartGridObject> pathObjects, VariableCollection variableCollection) {
		this.setBuses(busA, busB);
		this.pathObjects = pathObjects;
		this.variableCollection = variableCollection;
	}
	
	/**
	 * Constructor: Empty
	 */
	public Path(VariableCollection variableCollection){
		this.variableCollection = variableCollection;
		this.pathObjects = new LinkedList<SmartGridObject>();
	}
	
	/**
	 * Stores sorted buses. Must not be null.
	 * @param busA
	 * @param busB
	 */
	public void setBuses(Bus busA, Bus busB){
		
		if(busA==null){
			this.bus1 = busB;
		}
		else if(busB==null){
			this.bus1 = busA;
		}
		else if(busA.getBusNumber() <= busB.getBusNumber()){	// store sorted.
			this.bus1 = busA;
			this.bus2 = busB;
		}
		else{
			this.bus1 = busB;
			this.bus2 = busA;
		}
	}
	
	public int getNrOfBuses(){
		if(bus1==null)
			return 0;
		else if(bus2==null)
			return 1;
		else
			return 2;
	}
	
	/**
	 * Add up to two buses, further buses ignored
	 * @param busX
	 * @return true if added
	 */
	public boolean addBus(Bus busX){
		int nr = getNrOfBuses();
		if(nr==2){			// all buses set
			return false;
		}
		else {				// 1 or zero buses set
			setBuses(bus1, busX);
			return true;
		}
	}
	
	/**
	 * @return the firstBus
	 */
	public Bus getBus1() {
		return bus1;
	}

	/**
	 * @return the secondBus
	 */
	public Bus getBus2() {
		return bus2;
	}

	@Override
	public VariableSet getVariableSet() {		// ------------ new default for this type of object
		return tmpVariableSet;
	}
	
	/**
	 * @param variableName
	 * @return value for variableName, or null if name is not found.
	 */
	public NumericValue getValue(EnumPV variableName){		// ------------ new default for this type of object
		return variableCollection.getValue(variableName);
	}
	
	/**
	 * Collects current simulation values with variableCollector
	 */
	public void refreshValues(){		// ------------ new default for this type of object
		variableCollection.resetValues();
		for(SmartGridObject sgo : pathObjects)
			variableCollection.collectFrom(sgo);
		
		tmpVariableSet = variableCollection.getVariableSet();
	}
	
	/**
	 * @return a copy of this path. Depth: Lists/Collections are copied.
	 */
	public Path copy(){
		Path tmp = new Path(bus1,bus2, new LinkedList<SmartGridObject>(pathObjects), variableCollection.copy());
		
		if(notVisitedTmp!=null)
			tmp.notVisitedTmp = new LinkedList<SmartGridObject>(notVisitedTmp);
	
		return tmp;
	}

	@Override
	public String toString(){
		return getBus1() +"<->"+getBus2() +" "+ pathObjects.toString();
	}

}