package sgs.model.gridObjects;

import sgs.model.gridData.GridDataEnum;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;

/**
 * Is used as a connector on the grid.
 * @author tobi, Kristofer Schweiger
 */
public class Connector extends PowerTransport {

	private static final double Default_MaxPowerTransport       = Double.POSITIVE_INFINITY;
	private static final double Default_TransportPowerDecrement = 0.0d; // 0 Percent

	public Connector() {
		super();
    	variableSet.set(EnumPV.maxPowerTransport, Default_MaxPowerTransport);
    	variableSet.set(EnumPV.transportPowerDecrement, Default_TransportPowerDecrement);
    	setResistance(new NumericValue(0));
	}
	
    // ---------------------------------------------------------
    // --- Override --- ----------------------------------------

	@Override
	public GridDataEnum getEnum() {
		return GridDataEnum.SPLINE;
	}

}