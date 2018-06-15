package sgs.model.gridObjects;

import jGridMap.model.GridObject;

import java.awt.Window;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import sgs.controller.simulation.Weather;
import sgs.model.SgsGridModel;
import sgs.model.gridData.GridDataEnum;
import sgs.model.gridData.GridDataNode;
import sgs.model.gridData.OverlayList;
import sgs.model.objectModels.AbstractModel;
import sgs.model.other.WindowOwner;
import sgs.model.simulation.Bus;
import sgs.model.variables.EnumPV;
import sgs.model.variables.NumericValue;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;
import sgs.model.variables.collector.VariableOwner;

/**
 * Simulation/Application specific object representing the 
 * most important simulation dependent values and functions.
 * 
 * Variables: Adds all default variables with default values.
 * Values and visibility may be changed by sub classes.
 * Visibility may be changed by Algorithms. (TODO)
 * 
 * @author Kristofer Schweiger
 */
public abstract class SmartGridObject extends GridObject implements WindowOwner, VariableOwner {

	public VariableSet variableSet;  // TODO make it protected?
	protected GridDataNode selectedNode;
	protected AbstractModel model;
	protected String objectName;
	
	
	/** Simulation relevant parameter **/
    private int busNumber = -1;			// -1 = no bus
    
    public OverlayList overlays = new OverlayList();
    //public double overlayValue = 0;		// 0..1, 0 = red
	
	/** Holds windows owned inly by this specific object **/
	public LinkedList<Window> ownedWindows = new LinkedList<Window>();	// will be closed if object is erased
	/** true if this object can be dragged in the grid **/
	protected boolean draggable = true;
	/** Model is static, so it doesn't need to be set for every object extra **/
	public static SgsGridModel gridModel;
	
	/**
	 * New Constructor for variable set.
	 * SmartGridObject adds all default values 
	 */
	public SmartGridObject(){
		this.selectedNode  = sgs.model.gridData.GridDataTree.classToNodeMap.get( this.getClass() ); 
    	variableSet = selectedNode.nodeVariableSet.copyKeepProperties();
    }
	
    /**
     * @return the busNumber
     */
    public int getBusNumber() {
        return busNumber;
    }

    /**
     * @param busNumber the clusterNumber to set
     */
    public void setBusNumber(int busNumber) {
        this.busNumber = busNumber;
    }
    
    /**
     * @return the Bus corresponding to the busNumber. May result in an Error if data is not up to date.
     */
    public Bus getBus() {
    	if(busNumber < 0)
    		return null;
        return gridModel.buses.get(busNumber);
    }
    
//    TODO: find bether place for this function 
	protected NumericValue getBusVoltage(){
    	if(busNumber < 0)
    		return null;
		return gridModel.buses.get(busNumber).getBusVoltage();
	}
	
	// --- Variables --- -------------------------------------------------
	// -------------------------------------------------------------------
    /**
     * 
     * @return the current model
     */
	
	public AbstractModel getModel() {
		return model;
	}

	public void setModel(AbstractModel model){
		this.model = model;
	}
    /**
     * @return current power consumption of this object
     */
    public NumericValue getCurrentPowerConsumption() {
		//return currentPowerConsumption;
		return variableSet.get(EnumPV.powerConsumption).getValueNumeric();
	}

	/**
     * @param numeric - the new charge value for this element
     */
    public void setCurrentPowerConsumption(NumericValue numeric) {
    	//this.currentPowerConsumption = charge;
		variableSet.get(EnumPV.powerConsumption).setValue(numeric);
	}
    
    public void setCurrentPowerConsumption(double value) {
    	setCurrentPowerConsumption( new NumericValue(value,0) );
    }
    
//    /**
//     * Resets current power consumption
//     */
//    public void resetCurrentPowerConsumption() {
//    	//this.currentPowerConsumption = powerProduction;
//    	NumericValue powerProduction = variableSet.get(EnumPV.powerProduction).getValueNumeric();
//    	variableSet.get(EnumPV.powerConsumption).setValue(powerProduction);
//	}
    
    /**
     * @return power production of this object
     */
    public NumericValue getPowerProduction() {
		//return powerProduction;
    	return variableSet.get(EnumPV.powerProduction).getValueNumeric();
	}
    
    /**
	 * Producer method
	 * @param powerProduction
	 */
	public void setPowerProduction(double powerProduction){
		variableSet.get(EnumPV.powerProduction).setValue(powerProduction);
	}

	/**
	 * Producer method
	 * @param powerProduction
	 */
	public void setPowerProduction(NumericValue powerProduction){
		variableSet.get(EnumPV.powerProduction).setValue(powerProduction);
	}

	/**
     * Producer method
     * @param ratedPower
     */
	public void setPeakPower(NumericValue ratedPower){
		variableSet.get(EnumPV.powerProductionOptimal).setValue(ratedPower);
	}
	/**
	 * Producer method
	 * @param powerProduction
	 */
	public void setPeakPower(double ratedPower){
		variableSet.get(EnumPV.powerProductionOptimal).setValue(ratedPower);
	}

	/**
	 * Producer method
	 * @return
	 */
	public NumericValue getPeakPower(){
		return variableSet.get(EnumPV.powerProductionOptimal).getValueNumeric();
	}
	/**
     * @return power usage of this object
     */
    public NumericValue getPowerDemand() {
		//return powerDemand;
    	return variableSet.get(EnumPV.powerDemand).getValueNumeric();
	}
    
    /**
     * @return maximum energy this object can "transport" (?)
     */
    public double getMaxPowerTransport(){
    	//return maxPowerTransport;
    	return variableSet.get(EnumPV.maxPowerTransport).getValueDouble();
    }
    
    /**
     * @return Energy decrement factor 
     * e.g. 0.01 = 1 percent energy decrement
     */
	public double getTransportPowerDecrement() {
		return variableSet.get(EnumPV.transportPowerDecrement).getValueDouble();
	}
	
	public void setResistance(NumericValue resistance){
		variableSet.get(EnumPV.resistance).setValue(resistance);
	}

	public NumericValue getResistance(){
		return variableSet.get(EnumPV.resistance).getValueNumeric();
	}
	
	public NumericValue getCurrentVoltage(){
		// double u = variableSet.get(EnumPV.currentVoltage).getValueDouble();
		 //double angDeg = variableSet.get(EnumPV.voltageAngle).getValueDouble();
		 //return new NumericValue( u*Math.cos( deg2rad(angDeg)), u*Math.sin( deg2rad(angDeg)) );
		return variableSet.get(EnumPV.currentVoltage).getValueNumeric();
	}
	
	public void setCurrentVoltage(double currentVoltage){
		variableSet.get(EnumPV.currentVoltage).setValue(currentVoltage);
	}
	
	/**
	 * Set current voltage and calculated angle.
	 * @param currentVoltage
	 */
	public void setCurrentVoltage(NumericValue currentVoltage){
		double ang = rad2deg( Math.asin(  currentVoltage.getImaginary()/currentVoltage.abs() ) );
		variableSet.get(EnumPV.voltageAngle).setValue( roundC(ang)  );
		
		variableSet.set(EnumPV.currentVoltage, currentVoltage);
	}
	
	// TODO remove the following 3 methods
	private Double roundC(double value) {
		int accuracy = 1000;
		double tmp =  Math.round(accuracy*value);	
		tmp = tmp/accuracy;
		return tmp;
	}
	private double rad2deg(double rad){
		double deg = rad*180/Math.PI ;
		return deg;
	}
	@SuppressWarnings("unused")
	private double deg2rad (double deg){
		double rad = deg/180*Math.PI ;
		return rad;
	}
	
	
	public void setPowerLineCharge(NumericValue powerLineCharge){
		variableSet.get(EnumPV.powerLineCharge).setValue(powerLineCharge);
	}

	public double getLineCharge(){
		return variableSet.get(EnumPV.powerLineCharge).getValueDouble();
	}
	
	protected void setProperties(boolean visible, boolean editable, EnumPV... variables){
		
		LinkedList<GridDataNode> nodes = new LinkedList<GridDataNode>();
		nodes.add(selectedNode);
		
		while(!nodes.isEmpty()){
			GridDataNode node = nodes.removeFirst();
			
			if(node.nodeVariableSet != null){		// If class has VariableSet/Enum/!isAbstract
				
		    	for(EnumPV enu : variables){		// set properties for relevant variables
		    		SingleVariable sv = node.nodeVariableSet.get(enu);
		    		sv.properties.set(visible, editable);
		    	}
			}
			
			nodes.addAll(node.subNodes);	// add sub nodes for setting properties
		}
	}

    // ---------------------------------------------------------
    // --- Abstract Methods --- --------------------------------
    
    public void updateObject(GregorianCalendar currentTime, Weather weather, int simulationResolutionMinutes){

		if(this.getEnum().getMappedModel() != null){  
			this.getModel().updateModel(currentTime, weather, simulationResolutionMinutes);
		}
		else{
			this.setProductionToWeatherAndTime(currentTime, weather);
		}
    }
	
	
    /**
     * Adjusts energy production to specific weather and time.
     * Depends on EnumPV.powerProductionOptimal
     * 
     * @param weather 
     */
    public abstract void setProductionToWeatherAndTime(GregorianCalendar currentTime, Weather weather);

    /**
     * @return VariableSet of this object
     * @deprecated use the getVisibles() method of the VariableSet.class
     */
    @Deprecated
	public VariableSet getData(){
    	
    	VariableSet visible = new VariableSet();
    	for(SingleVariable v : variableSet){
    		if(v.properties.isVisible())
    			visible.add(v);
    	}
    	return visible;
    }
//    public void setData(VariableSet variableSet){
//    	//this.variableSet = variableSet;
//    	//not necessary because of value objects.
//    }
    
    // ---------------------------------------------------------
    // --- WindowOwner Methods --- --------------------------------
    
	@Override
	public boolean addWindow(Window w){
		return ownedWindows.add(w);
	}
	
	@Override
	public boolean removeWindow(Window w){
		w.dispose();
		return ownedWindows.remove(w);
	}
	
	@Override
	public int closeAllWindows(){
		
		int size = ownedWindows.size();
		for(Window w : ownedWindows){
			removeWindow(w);
		}
		return size;
	}
	
    // ---------------------------------------------------------
    // --- Drag Methods --- ------------------------------------
	
	public boolean isDraggable(){
		return draggable;
	}
	
	public void setDraggable(boolean draggable){
		this.draggable = draggable;
	}
	
    // ---------------------------------------------------------
    // --- VariableOwner --- -----------------------------------
	
	@Override
	public VariableSet getVariableSet() {
		return variableSet;
	}

	// ---------------------------------------------------------
    // --- ID Methods --- --------------------------------------
	
	/**
	 * @param enu
	 * @return new grid data object for GridDataEnum. 
	 *         Mind to set coordinates if needed!
	 */
	public static SmartGridObject factory(GridDataEnum enu){
		
		Class<? extends SmartGridObject> c = enu.getMappedClass();
		SmartGridObject sgo = null;
		
		if(c == null){
			throw new RuntimeException("Could not get class of "+enu);
		}
		try {
			sgo = c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			System.err.println("Could not get instance of "+ c);
			//System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return sgo;
	}
	
	/**
	 * @return corresponding enum for this object.
	 */
	public abstract GridDataEnum getEnum();
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName()+"@"+x+"/"+y;		//+" \\"+gridModel.gridData[x][y]+"/";
	}

	public String getObjectName() {
		// TODO Auto-generated method stub
		return this.objectName;
	}

	  /**
     * To avoid initializing VariableSet and SingleVariable in the CSV Class GiveDataObject and GiveDataParam, 
     * only return visible parameters in a string for the TreeNodes
     * @return sgoParameter
     */
    public String getParameterOfSGO(){
    	
    	String sgoParameter = "";
    	
    	VariableSet parameter = variableSet.getVisibles(); //Receive the trueParameters for the object
    	
    	for(SingleVariable v : parameter){
    		sgoParameter = sgoParameter + v.name() + "/";
    		
    	}
    	
    	return sgoParameter;
    }
}