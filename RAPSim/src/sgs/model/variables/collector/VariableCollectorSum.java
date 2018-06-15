package sgs.model.variables.collector;

import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;

public class VariableCollectorSum extends VariableCollector {

	private NumericValue value;
	private int nrOfValues;
	
	/**
	 * Constructor for sum collector.
	 * @param variableName
	 */
	public VariableCollectorSum(EnumPV variableName) {
		super(variableName);
		value = new NumericValue();
		nrOfValues=0;
	}

	@Override
	public NumericValue getValue() {
		return value;
	}
	
	@Override
	public int getNumberOfValues() {
		return nrOfValues;
	}
	
	@Override
	public void restValues() {
		value.setValue(0, 0);
		nrOfValues=0;
	}

	@Override
	public boolean collectFrom(VariableOwner owner) {
		VariableSet set = owner.getVariableSet();
		if(set != null){
			SingleVariable sv = set.get(variableName);
			NumericValue value2 = sv.getValueNumeric();
			value.add(value2);	// sum up value
			return true;
		}
		return false;
	}

	@Override
	public VariableCollectorSum copy() {
		VariableCollectorSum tmp = new VariableCollectorSum(variableName);
		tmp.value = value.copy();
		tmp.nrOfValues = nrOfValues;
		return tmp;
	}

}
