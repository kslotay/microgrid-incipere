package sgs.model.variables.collector;

import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;

public class VariableCollectorAvg extends VariableCollector {

	private VariableCollectorSum sum;
	private NumericValue value;
	private boolean isNew;
	
	/**
	 * Constructor for average collector.
	 * @param variableName
	 */
	public VariableCollectorAvg(EnumPV variableName) {
		super(variableName);
		sum = new VariableCollectorSum(variableName);
		value = new NumericValue(Double.NaN);				// is initially and at size zero not a number.
		isNew = false;
	}
	
	@Override
	public NumericValue getValue() {

		int nrOfValues = sum.getNumberOfValues();
		
		if(nrOfValues>0 && isNew){
			double real = sum.getValue().r();
			double imag = sum.getValue().i();
			value.setValue( real/nrOfValues, imag/nrOfValues );
			isNew = false;
		}
		
		return value;
	}
	
	@Override
	public int getNumberOfValues() {
		return sum.getNumberOfValues();
	}

	@Override
	public void restValues() {
		sum.restValues();
		value.setValue(Double.NaN);
		isNew = false;
	}

	@Override
	public boolean collectFrom(VariableOwner owner) {
		boolean b = sum.collectFrom(owner);
		isNew = isNew | b;
		return b;
	}

	@Override
	public VariableCollectorAvg copy() {
		VariableCollectorAvg tmp = new VariableCollectorAvg(variableName);
		tmp.sum = sum.copy();
		tmp.value = value.copy();
		tmp.isNew = isNew;
		return tmp;
	}
}
