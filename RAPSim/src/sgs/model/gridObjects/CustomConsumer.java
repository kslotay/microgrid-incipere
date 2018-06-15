package sgs.model.gridObjects;

import sgs.model.gridData.GridDataEnum;
import sgs.model.objectModels.ConstantDemandModel;
import sgs.model.variables.EnumPV;
/**
 * 
 * @author bbreilin
 *
 */
public class CustomConsumer extends Consumer {
	public CustomConsumer(){
		this.setProperties(true, false, EnumPV.powerDemand);
		this.objectName = "CustomConsumer";
		this.model = new ConstantDemandModel(this);
	}
	@Override
	public GridDataEnum getEnum() {
		// TODO Auto-generated method stub
		return GridDataEnum.CUSTOM_CONSUMER;
	}

}
