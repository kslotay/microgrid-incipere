package sgs.model.objectModels;

import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.gridObjects.CustomConsumer;
/**
 * 
 * @author bbreilin
 *
 */
public class ConstantDemandModel extends AbstractCustomConsumerModel {

	public ConstantDemandModel(CustomConsumer consumer) {
		super(consumer);
		this.icon = new ImageIcon("Data2/House_ICON.png");
		this.modelName = "ConstantDemandModel";
		this.description = "Consumer with constant power demand.";
//		this.initVariableSet();
	}

	@Override
	public void updateVariables(GregorianCalendar currentTime, Weather weather, int resolution) {
		
//		this.consumer.setCurrentPowerConsumption( this.powerDemand.getValueNumeric()  );
	}

	@Override
	protected void initVariableSet() {
		this.powerDemand.properties.set(true, true);
	}

}
