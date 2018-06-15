package sgs.model.variables.collector;

import sgs.model.variables.VariableSet;

public interface VariableOwner {
	
	/**
	 * @return the VariableSet owned by this object; or null (no owned variable set)
	 */
	public VariableSet getVariableSet();
	
//	/**
//	 * @return a list of other, contained VariableOwners; or null (no other variable owners)
//	 */
//	public LinkedList<? extends VariableOwner> getSubOwners();
	
	
}
