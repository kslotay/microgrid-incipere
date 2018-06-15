package sgs.controller.simulation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import sgs.controller.interfaces.GridChangeListener;
import sgs.model.SgsGridModel;
import sgs.model.gridData.GridDataNode;
import sgs.model.gridData.GridDataTree;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.variables.EnumPV;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;
import sgs.model.variables.collector.VariableCollection;
import sgs.model.variables.collector.VariableCollectorSum;


/**
 *
 * @author Tobi, Kristofer Schweiger
 */
public abstract class AbstractDistributionAlgorithm implements GridChangeListener {

	/** GridDataNode temporary used for setting algorithm specific variables **/
	private GridDataNode selectedNode = null;
    /** Description of how the Algorithm works (to ease selection process in future) **/
    protected static String description;
    /** Log stream for logging data if activated. Can be null **/
	protected OutputStream log = null;

    /**
     * Constructor: initialize variables (visibility, properties) used in this algorithm.
     * Note: Sub classes must have a Constructor with the parameter GridController for generating automated instances.
     */
    public AbstractDistributionAlgorithm(){
    	//initializeVariables();
    	//Out.pl("AbstractDistributionAlgorithm: constructor call for "+this.getClass().getSimpleName());
    }
    
    /**
     * Calculate grid for one simulation step.
     * > Buses and Paths are locked before.
     * > Grid/View-Repaint is automatically called after the function.
     */
    public abstract void calculationStep();
    
    /**
	 * Initialize Algorithm specific settings and data.
	 */
	public void initializeAlgorithm(){
		initializeAlgorithm();
	}

	/**
     * Full initialization. 
     * Should be called if algorithm is used for the first time or the algorithm was changed.
     */
    public void initializeFull(){
    	initializeVariables();
    	initializeAlgorithm();
    }
    
    /**
     * Initialize the used Variables
     */
    private void initializeVariables() {
    	resetPropertiesForVariables();
    	setPropertiesForVariables();
    	setPropertiesForBusAndPath();
    }
    
    /**
	 * Set the algorithm specific variables with help of the super class methods.
	 * @see {@link #selectClass(Class)}, {@link #setProperties(boolean,boolean,EnumPV[])}, {@link #initializeVariables()}
	 */
	protected abstract void setPropertiesForVariables();

	/**
	 * Step 0: reset existing variable properties
	 * @see {@link #setPropertiesForVariables()}, {@link #initializeVariables()}
	 */
	private void resetPropertiesForVariables(){
		Set<Entry<Class<? extends SmartGridObject>, GridDataNode>> entries2 = GridDataTree.classToNodeMap.entrySet();
		for(Entry<Class<? extends SmartGridObject>, GridDataNode> entry : entries2){
			GridDataNode node = entry.getValue();
			VariableSet varSet = node.nodeVariableSet;
			if(varSet!=null)
				for(SingleVariable var : varSet){
					var.properties.reset();
				}
		}
	}

	/**
	 * Step 1: Select class for enabling variables
	 * @param c
	 * @see {@link #setPropertiesForVariables()}
	 */
	protected void selectClass(Class<? extends SmartGridObject> selectedClass) {
		this.selectedNode = GridDataTree.classToNodeMap.get(selectedClass);
	}

	/**
	 * Step 2: Enable variables with their properties.
	 * Sets properties for selected class and its sub classes.
	 * @param visible	- the variable is visible to the user
	 * @param editable	- the variable is editable for the user
	 * @param variables - variables where specified properties should be set
	 * @see {@link #setPropertiesForVariables()}
	 */
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

	/**
	 * Set SgsGridModel.resistanceAttributes, busVariableCollection, pathVariableCollection if used.
	 */
	protected abstract void setPropertiesForBusAndPath();

	protected static void setDefaultPropertiesForBusAndPath(SgsGridModel gridModel) {
		
		// --- resistanceAttributes ---
		//
		gridModel.resistanceAttributes.clear();
		gridModel.resistanceAttributes.add(EnumPV.resistance);
		gridModel.resistanceAttributes.add(EnumPV.transportPowerDecrement);
		gridModel.resistanceAttributes.add(EnumPV.powerLineCharge);
		
		// --- busVariableCollection ---
		//
		gridModel.busVariableCollection = new VariableCollection(
				new VariableCollectorSum(EnumPV.powerConsumption),
				new VariableCollectorSum(EnumPV.powerProduction),
				new VariableCollectorSum(EnumPV.powerDemand)
				);
		
		// --- pathVariableCollection  ---
		//
		gridModel.pathVariableCollection = new VariableCollection(
				new VariableCollectorSum(EnumPV.maxPowerTransport),
				new VariableCollectorSum(EnumPV.resistance),
				new VariableCollectorSum(EnumPV.transportPowerDecrement),
				new VariableCollectorSum(EnumPV.powerLineCharge)
				);
	}

	/**
	 * Free external resources used by this algorithm.
	 */
	protected abstract void unloadAlgorithm();

	/**
     * Is called if the Grid is modified.
     */
    @Override
	public abstract void gridChanged();
    
    
    
    
    
//    /**
//     * Get all the Transactions that have taken place
//     * @return 
//     */
//    public abstract LinkedHashMap<SmartGridObject, double[]> getTransactions();

    /**
     * @return the name of the algorithm
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return description of the specific algorithm used for the "Info" button 
     */
    public abstract String getDescription();
    
//    /**
//     * Called by time thread.
//     * Write interesting contents of the simulation to a file.
//     */
//    public abstract void writeToFile();
    
	/**
	 * Called directly after the simulation was started.
	 * Use this to make a LOG file if one is used.
	 */
	public abstract void simulationStarted();

	/**
	 * Called directly after the simulation was stopped.
	 * Use this to flush/close a log file if one was used.
	 */
	public abstract void simulationStopped();

//	/**
//	 * Try to make a new log-Stream
//	 */
//    protected void makeLog(String filename){
//    	try {
//    		log = new BufferedOutputStream( new FileOutputStream(filename) );
//    	} catch (FileNotFoundException e) {
//    		log = null;
//    		e.printStackTrace();
//    	}
//    }
	
	/**
	 * Close log-Stream, if it exists.
	 */
	protected void closeLog(){
		if(log!=null){
			try {
				log.flush();
				log.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				log=null;
			}
		}
	}
	
	/**
	 * Log information if log-Stream exists.
	 * @param s
	 */
	protected void log(String s){
		if(log!=null){
			try {
				//log.write("date" + "\t" + "time" + "weather" + "Bus" + "Netto production" +);
				log.write(s.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * @author hs
	 * all classes who inherit this one, should have this method to create a String, to make the CSV-File
	 */
	public String generateList(){
		
		String header = "";
		
		return header;
	}
	
	
}
