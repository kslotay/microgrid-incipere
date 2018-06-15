package sgs.model;

import jGridMap.model.GridModel;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.JFrame;

import sgs.controller.simulation.TimeThread;
import sgs.model.gridData.GridDataEnum;
import sgs.model.gridData.Overlay;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.simulation.Bus;
import sgs.model.simulation.Path;
import sgs.model.variables.EnumPV;
import sgs.model.variables.collector.VariableCollection;

/**
 * The model for the SGS which is extending JGridMaps GridModel class, i.e. all displayed data shown in the Grid.  
 * It contains the list with all the GridObjects.
 * It contains a list for the Collections, witch are Buses and Paths.
 * It contains the settings for ColorOverlays.
 * 
 * Most methods for for editing the GridModel are provided by the GridController. 
 * 
 * @author Kristofer Schweiger
 */
public class SgsGridModel extends GridModel {
	
	/**
	 * Overwrite default configurations:
	 * Grid Objects + up to Enum-length (e.g. 3) Overlays
	 */
    static{
    	GridModel.DEFAULT_GRID_DEPTH = 1+Overlay.OverlayLevelEnum.values().length;
    }
    
    /** Frame for adjusting window positions **/
    public static JFrame mainView;
    /** slider dialog (change simulation speed etc.) for reference **/
//    public static SpeedChanger sliderDialog;
    
    /** Drawing data for JGridMap, int-values refer to images in the JGM **/
    public int[][][] gridData3D;
    /** Actual objects in Grid at corresponding position **/
    public SmartGridObject[][] gridObjects;
    /** List of all GridObjects (redundant to gridObjects) **/
    public LinkedList<SmartGridObject> gridObjectList;

    /** true if overlays should be drawn **/
    public boolean overlayUse = false;
    /** Overlay can be (a) automatically defined by GridController or (b) manually by the Algorithm. **/
    public OverlayMode overlayMode = OverlayMode.AUTOMATIC;		// Overlay can be automatically defined by GridController or manually by the Algorithm.
    /** Modes for drawing Overlay. See implementation in GridController **/
    public enum OverlayMode {
    	/** Overlays will be drawn by GridController function, based on Production and Consumption. **/
    	AUTOMATIC,
    	/** Overlays will be manually defined in the Algorithm for every object. **/
    	BY_ALGORITHM;
    };
    
    /** What tool is currently activated. When clicked creates a house etc. according to the buttons which were pressed before **/
    public GridDataEnum currentPointer;
    public Constructor<?> modelConstructor;
    /** true if results should be saved **/
    public boolean saveSimResultsToFile = false;
    /** 
     * File for simulation results, when {@code saveSimResultsToFile = true;}
     * manual by the user in the simulation
     * => ProgramParametersSaved
     */
    //public String simResultFile = "./simulation.txt";
    
    /** 
     * A bus is a list of GridObjects which are directly connected, 
     * meaning they have the same voltage and no resistance in between.
     */
    public LinkedList<Bus> buses = new LinkedList<>();
    /**
     * Paths are connections between buses. 
     * They have for example an electrical resistance.
     */
    public LinkedList<Path> paths = new LinkedList<>();
    /** 
     * True if a thread is currently working with the buses and paths. 
     * @See lockBusesPaths(), unlockBusesPaths()
     */
    private boolean lockedBusesPaths = false;
    
	/** Defines where to split Buses (visible, >0) for NetworkAnalyzer**/
	public TreeSet<EnumPV> resistanceAttributes = new TreeSet<EnumPV>();
	/** VariableCollection for bus **/
	public VariableCollection busVariableCollection = null;
	/** VariableCollection for path **/
	public VariableCollection pathVariableCollection = null;
	/**  currently used instance of TimeThread, set by TimeThread, may be null. **/
	public TimeThread timeThread;
	/** saved parameters for adjusting the program to the specific user **/
    public ProgramParametersSaved programParameters = null;
	
    
	/**
	 * Constructor: full
	 * @param nrOfCellsWidth  - amount of cells, width
	 * @param nrOfCellsHeight - amount of cells , height
	 * @param gridWidth  - initial width in pixels
	 * @param gridHeight - initial height in pixels
	 */
	public SgsGridModel(int nrOfCellsWidth, int nrOfCellsHeight, int gridWidth, int gridHeight){
		
		super(nrOfCellsWidth, nrOfCellsHeight, gridWidth, gridHeight);
		initGrid(nrOfCellsWidth, nrOfCellsHeight);
		
		

	}
	
	/**
	 * Initialize grid
	 * @param nrOfCellsWidth
	 * @param nrOfCellsHeight
	 */
	private void initGrid(int nrOfCellsWidth, int nrOfCellsHeight){
		
		gridObjects = new SmartGridObject[nrOfCellsHeight][nrOfCellsWidth];	// Objects in Grid
		
		gridData3D = new int[GridModel.DEFAULT_GRID_DEPTH][nrOfCellsHeight][nrOfCellsWidth];	//gridData3D = new int[2][nrOfCellsWidth][nrOfCellsHeight];
		gridData3D[0] = gridData;									// object data
		for(int z=1; z<gridData3D.length; z++)
			gridData3D[z] = new int[nrOfCellsHeight][nrOfCellsWidth];	// overlay data in multiple dimensions
		
		gridObjectList = new LinkedList<>();	
		overlayUse = false;
	}
	
    /** @return amount of cells, width **/ 
    public int getNrOfCellsWidth(){
    	return jGridMap.getGridWidth();
    }
    /** @return amount of cells, height **/ 
    public int getNrOfCellsHeight(){
    	return jGridMap.getGridHeight();
    }

    /**
     * Locks buses and paths lists for read and write. {@code lockedBusesPaths = true;}
     */
	public void lockBusesPaths() {
		
		synchronized (buses) {
			while(lockedBusesPaths){
				try {
					wait();
				} catch (InterruptedException e) {}
			}
			lockedBusesPaths = true;
		}
	}

	/**
	 * Unlocks buses and paths lists after read and write. {@code lockedBusesPaths = false;}
	 */
	public void unlockBusesPaths() {
		
		synchronized (buses) {
			lockedBusesPaths = false;
		}
	}
	
}
