package sgs.model.gridObjects;

import sgs.model.objectModels.AbstractPowerPlantModel;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;


/**
 * A SmartGridObject.
 * @author tobi, Kristofer Schweiger, bbreilin
 */
public abstract class PowerPlant extends ProSumer {
	/**
	 * Abstract constructor
	 */
	public PowerPlant() {
		super();
		this.variableSet.set(EnumPV.currentVoltage, new NumericValue(1.0));
		this.variableSet.set(EnumPV.voltageAngle, new NumericValue(0.0));
	}
	@Override
	public AbstractPowerPlantModel getModel() {
		return (AbstractPowerPlantModel) model;
	}
	public void setModel(AbstractPowerPlantModel model) {
		this.model = model;
	}
	
	

	// ---------------------------------------------------------
	// --- Override --- ----------------------------------------

	//    @Override
	//    public LinkedHashMap<String, String> getDataAsMap() {
	//        LinkedHashMap<String, String> map = new LinkedHashMap<>();
	//        map.put("current Voltage", String.format("%.2f", getBusVoltage()));
	//        map.put("Power Production", String.format("%.2f", powerProduction));
	//        map.put("Unnecessary Production", String.format("%.2f", currentPowerConsumption));
	//        for (int i = 0; i < getOffers().size(); i++) {
	//            map.put("Offer " + i + " For", "" + String.format("%.2f", getOffers().get(i).getPrice()));
	//            map.put("Offer " + i + " Sell", "" + String.format("%.2f", getOffers().get(i).getAmount()));
	//        }
	//        return map;
	//    }
	//
	//    @Override
	//    public void setData(ArrayList<String> data) {
	//        powerProduction = Double.parseDouble(data.get(1));
	//        for (int i=2; i<data.size()-1; i+=2) {
	//            getOffers().get((i - 2) / 2).setPrice(Double.parseDouble(data.get(i)));
	//            getOffers().get((i - 2) / 2).setAmount(Double.parseDouble(data.get(i + 1)));
	//        }
	//    }

}