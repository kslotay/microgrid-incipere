package sgs.controller.simulation;

import java.util.LinkedList;

import sgs.controller.GridController;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.simulation.Path;
import sgs.model.variables.EnumPV;
import testing.Out;
import testing.Out_;

public class _TestingAlgorithm extends AbstractDistributionAlgorithm {

	@SuppressWarnings("unused")
	private final GridController gridController;
	private final SgsGridModel gridModel;

	private long calcCount=0;
	
	/**
	 * Constructor for reflection. Must (only) use parameter GridController.
	 */
	public _TestingAlgorithm(GridController gridController){
		this.gridController = gridController;
		this.gridModel = gridController.getModel();
		
		Out.pl("> Algorithm constructed...");
	}
	
	@Override
	public void initializeAlgorithm() {
		
		Out.pl("> initializeAlgorithm() ...");
		
		LinkedList<Path> paths = gridModel.paths;
		Out_.pl(paths);
	}
	
	@Override
	public void gridChanged() {
		this.initializeAlgorithm();
	}
	
	@Override
	protected void setPropertiesForVariables() {
    	this.selectClass( SmartGridObject.class );
    	this.setProperties(true, false, EnumPV.powerDemand, EnumPV.maxPowerTransport);
		this.setProperties(true, true, EnumPV.powerProductionOptimal);		// Uses
		this.setProperties(true, false, EnumPV.powerProduction);			// Uses
    	
    	Out.pl("> Variables were set...");
	}
	
	@Override
	protected void setPropertiesForBusAndPath() {
		super.setDefaultPropertiesForBusAndPath(gridModel);
	}
	
	
	@Override
	public void calculationStep() {
		System.out.println("Calc "+ calcCount++ +" -----------------------------------------------------");
		//The Calculation:
		
		LinkedList<Path> paths = gridModel.paths;
		Out.pl("Paths: "); Out_.p(paths); Out_.pl();
		for(SmartGridObject sgo : gridModel.gridObjectList){
			Out.pl(sgo+" is in Bus "+sgo.getBusNumber());
		}
	}

	@Override
	protected void unloadAlgorithm() {
		Out.pl("> Unload Algorithm");
	}

	@Override
	public String getDescription() {
		return "An algorithm for testing. Test, test, test...";
	}

	@Override
	public void simulationStarted() {
		// nothing
	}

	@Override
	public void simulationStopped() {
		// nothing
	}

}
