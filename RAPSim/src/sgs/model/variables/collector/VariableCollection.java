package sgs.model.variables.collector;

import java.util.ArrayList;

import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.VariableSet;

public class VariableCollection {
	
	/** all relevant collectors, may be added later **/
	public ArrayList<VariableCollector> collectors;
	
	/**
	 * Constructor with 
	 * @param collectors
	 */
	public VariableCollection(VariableCollector... collectors){
		
		this.collectors = new ArrayList<VariableCollector>(collectors.length);
		for(VariableCollector c : collectors)
			this.collectors.add(c);
	}
	
	/**
	 * @param variableName
	 * @return value for variable or null
	 */
	public NumericValue getValue(EnumPV variableName){
		for(VariableCollector c : collectors)
			if(c.variableName == variableName)
				return c.getValue();
		return null;
	}
	
	/**
	 * @return VariableSet build from this collection
	 */
	public VariableSet getVariableSet(){
		VariableSet set = new VariableSet();
		for(VariableCollector c : collectors)
			set.add(c.getSingleVariable());
		return set;
	}
	
	/**
	 * Reset all contained values.
	 */
	public void resetValues() {
		for(VariableCollector c : collectors)
			c.restValues();
	}
	
	/**
	 * @param owner
	 * @return true if at least one value was collected
	 */
	public boolean collectFrom(VariableOwner owner) {
		
		boolean collected = false;
		
		for(VariableCollector c : collectors)
			collected = c.collectFrom(owner) || collected;
		
		return collected;
	}
	
	/**
	 * @return a deep copy of this collector.
	 */
	public VariableCollection copy(){
		VariableCollection tmp = new VariableCollection();
		for(VariableCollector c : collectors)
			tmp.collectors.add(c.copy());
		
		return tmp;
	}
	
	@Override
	public String toString(){
//		StringBuffer b = new StringBuffer();
//		for(int i=0; i<this.collectors.size(); i++){
//			VariableCollector vc = collectors.get(i);
//			b.append(vc.getVariableName() + "_" + vc.)
//		}
		return collectors.toString();
	}
}
