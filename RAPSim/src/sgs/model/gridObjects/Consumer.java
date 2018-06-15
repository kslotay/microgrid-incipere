package sgs.model.gridObjects;

import sgs.model.objectModels.AbstractConsumerModel;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
/**
 * 
 * @author bbreilin
 *
 */
public abstract class Consumer extends ProSumer {
	
	public Consumer(){
		super();
		this.variableSet.get(EnumPV.currentVoltage).setValue(new NumericValue(1.0));
		this.variableSet.get(EnumPV.voltageAngle).setValue(new NumericValue(0.0));
	}
	
	@Override
	public AbstractConsumerModel getModel(){
		return (AbstractConsumerModel) this.model;
	}
	
	public void setModel(AbstractConsumerModel model){
		this.model = model;
	}

}
