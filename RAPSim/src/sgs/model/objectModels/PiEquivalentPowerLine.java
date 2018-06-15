/**
 * 
 */
package sgs.model.objectModels;

import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import sgs.controller.simulation.Weather;
import sgs.model.variables.EnumUnit;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;

/**
 * @author Poechacker
 *
 */
public class PiEquivalentPowerLine extends AbstractPowerLineModel {

//	private SingleVariable frequenzy;
	private SingleVariable lineImpetanz;
	private SingleVariable lineCharge;
	
	
	/**
	 * 
	 */
	public PiEquivalentPowerLine() {
		super();
		this.modelName = "PiEquivalentCirquit";
		this.icon = new ImageIcon("Data2/PiPowerLine_ICON.png");
		this.description = "The powerline is represented by its line impedanz and the line charge to ground. ";
		this.initVariableSet();
	}

	
	@Override
	public void updateVariables(GregorianCalendar currentTime, Weather weather,
			int resolution) {
		// TODO Auto-generated method stub
	}	
	
	
	
	@Override
	protected void initVariableSet() {
//		this.frequenzy = this.initVariable("nominalFrequency", new NumericValue(50), EnumUnit.hertz, true, true);
		this.lineImpetanz = this.initVariable("lineImpetance", new NumericValue(0.1,0.2), EnumUnit.ohm, true, true);
		this.lineCharge = this.initVariable("lineCharge", new NumericValue(0), EnumUnit.ohm, true, true);
				
	}

}
