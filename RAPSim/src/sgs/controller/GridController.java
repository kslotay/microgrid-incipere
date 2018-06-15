package sgs.controller;

import static sgs.model.gridData.GridDataEnum.EMPTY;
import static sgs.model.gridData.GridDataEnum.PLINE;
import static sgs.model.gridData.GridDataEnum.SPLINE;
import jGridMap.JGridMap;
import jGridMap.view.interfaces.PrimaryViewI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.undo.*;

import sgs.controller.undoableEdit.UndoRedoManager;
import sgs.model.SgsGridModel;
import sgs.model.gridData.GridDataEnum;
import sgs.model.gridData.GridDataEnum.Type;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.gridObjects.Connector;
import sgs.model.objectModels.AbstractModel;
import sgs.model.variables.EnumPV;
import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;
import testing.Out;

/**
 * Provides most methods for editing the GridModel. 
 * 
 * Initializes the GridMapListener.
 * Loads images for the JGM (loadImagesNew)
 * Adds and deletes GridObjects.
 * Provides logics for neighbor detection and power-line drawing.
 * 
 * @author Kristofer Schweiger, Sabrina Huber
 */
public class GridController {
	
	public final GridMapListener gridMapListener;
    public final SgsGridModel gridModel;			// used by time thread (easier access)
    public final ColorOverlayController colorOverlayController; 
//    private static boolean copyModusBoolean = false;
//    private boolean copyStatus; //status tells us if we have allready choosen an object to copy
	private SmartGridObject copyObject;
//	private LinkedList copiedObjectList = new <SmartGridObject>LinkedList(); //um mehrer Objekte zu kopieren
	public int[] tempRememberMarkObject;
	public UndoManager undoManager = new UndoManager(); 
	


	/**
     * Constructor:
	 * - Creates GridMapListener.
	 * - Loads Object Images.
     * @param gridModel
     * @param view
     */
	public GridController(SgsGridModel gridModel, PrimaryViewI view){
		this.gridModel = gridModel;
		initGridData();
		this.gridMapListener = new GridMapListener(this, gridModel, (JFrame)view);
		gridModel.jGridMap.addEventListener(gridMapListener);
		loadImagesNew(gridModel.jGridMap);
		
		this.colorOverlayController = new ColorOverlayController( this, view);
		
		
	}
	
	
	public void actionfor_pasteCopiedObject(){	
		
		int x = tempRememberMarkObject[0];
		int y = tempRememberMarkObject[1];
		
		
		SmartGridObject original = getSavedSmartGridObject();
		SmartGridObject copy = addGridObject(SmartGridObject.factory(original.getEnum()),x,y);
//		calculatePowerLines(x,y,1);
//		calculatePowerLines(x,y,2);
		
		VariableSet originalVar = original.getVariableSet();
		
		for(SingleVariable var : originalVar){
			String enu = var.name();
			copy.variableSet.get(EnumPV.valueOf(enu)).setValue(var.getValueDouble());
		}

		colorOverlayController.repaint();
		colorOverlayController.repaint();
		
	}

	/**
	 * Add an grid object with it's corresponding enum.
	 * Initializes the object with the choosen model
	 * @param enu - enum for an SmartSimObject
	 * @param x - new coordinate
	 * @param y - new coordinate
	 */
	public void addGridObject(GridDataEnum enu, int x, int y) {
		
		SmartGridObject sso = SmartGridObject.factory(enu);
		if(enu.getMappedModel() != null){
			try {
				AbstractModel model = (AbstractModel)gridModel.modelConstructor.newInstance(enu.getMappedClass().cast(sso));
				sso.setModel(model);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		addGridObject(sso,x,y);
		
	}

	/**
	 * Add an grid object directly.
	 * @param enu - enum for an SmartSimObject
	 * @param x - new coordinate
	 * @param y - new coordinate
	 */
	public SmartGridObject addGridObject(SmartGridObject sso, int x, int y) {
		sso.setX(x);
		sso.setY(y);
		
		gridModel.gridObjects[y][x] = sso;
		gridModel.gridObjectList.add(sso);
		gridModel.gridData[y][x] = sso.getEnum().getID( Type.MAP_LRUD );
		
		undoManager.replaceEdit(new UndoRedoManager(this,sso,false));
		calculatePowerLines(x,y,1);
		calculatePowerLines(x,y,2);
	
		return sso;
	}

	
	
	/**
	 * 
	 * @param notVisited - visited objects will be removed. Use all GridObjects if toVisit==null
	 * @param x - coordinate of the object
	 * @param y - ...
	 * @param neighbours - list where to add neighbors
	 */
	private void addNeighborsFrom(LinkedList<SmartGridObject> notVisited, int x, int y, LinkedList<SmartGridObject> neighbours) {
	    
		//int[][] gridData = gridModel.gridData;
		SmartGridObject[][] gridObjects = gridModel.gridObjects;
		
		if ( gridObjects[y][x] != null ) {					// if there is an Object
			if(notVisited!=null){
	    		if (notVisited.remove(gridObjects[y][x])) {	// if contained in search list
	    			neighbours.add(gridObjects[y][x]);			// add to neighbors
	    		}
			}
			else{
				neighbours.add(gridObjects[y][x]);			// add to neighbors
			}
	    }
	}



	// ----------------------------------------------------------------------------
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param checkNeighbours
	 */
	public void calculatePowerLines(int x, int y, int checkNeighbours) {
		
		int[][] gridData      = gridModel.gridData;
		SmartGridObject[][] gridObjects = gridModel.gridObjects;
		int id = gridData[y][x];
		
	    if ( (id<PLINE.getID() || id>SPLINE.getMaxID()) && checkNeighbours != 2) {
	        return;
	    }
	    if (gridObjects[y][x] instanceof Connector) {
	        calculateSupraLines(x, y, checkNeighbours);
	        return;
	    }
	
	    if (checkNeighbours == 1) {
	        //System.out.format("Adjusting powerlines for (%d, %d) - ", X, Y);
	    } else if (checkNeighbours == 0) {
	        //System.out.format("\n		Adjusting neighbour (%d, %d) - ", X, Y);
	    } else // 2 = Check neighbours after deleting a field or creating a house / power plant
	    {
	        //System.out.format("Checking neighbours after deletion / powerplant / house (%d, %d)", X, Y);
	    }
	
	    if (checkNeighbours != 2) {
	        // Up old
	    	// e.g. if (getCellStatus(x, y - 1) && !getCellStatus(x, y + 1) && !getCellStatus(x - 1, y) && !getCellStatus(x + 1, y)) {
	    	
	    	//UP or DOWN
	        if (getCellStatus(x, y - 1) || getCellStatus(x, y + 1)) {
	            gridData[y][x] = PLINE.getID(Type.MAP_UD);
	            //System.out.print("Up Down");
	        }
	        // Right or LEFT
	        if (getCellStatus(x + 1, y) || getCellStatus(x - 1, y) ) {
	            gridData[y][x] = PLINE.getID(Type.MAP_LR);
	            //System.out.print("Right Left");
	        }
	    }
	
	    if (checkNeighbours == 1 || checkNeighbours == 2) {
	        if (x > 0) {
	            calculatePowerLines(x - 1, y, 0);
	        }
	        if (y > 0) {
	            calculatePowerLines(x, y - 1, 0);
	        }
	        if (x < (gridModel.getNrOfCellsWidth() - 1)) {
	            calculatePowerLines(x + 1, y, 0);
	        }
	        if (y < (gridModel.getNrOfCellsHeight() - 1)) {
	            calculatePowerLines(x, y + 1, 0);
	        }
	    }
	}

	// ----------------------------------------------------------------------------
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param CheckNeighbours
	 */
	public void calculateSupraLines(int x, int y, int CheckNeighbours) {
		
		int[][] gridData = gridModel.gridData;
		int CELLAMOUNT_WIDTH  = gridModel.getNrOfCellsWidth();
		int CELLAMOUNT_HEIGHT = gridModel.getNrOfCellsHeight();
		
	    if (CheckNeighbours == 1) {
	        //System.out.format("Adjusting supralines for (%d, %d) - ", X, Y);
	    } else if (CheckNeighbours == 0) {
	        //System.out.format("\n		Adjusting neighbour (%d, %d) - ", X, Y);
	    } else // 2 = Check neighbours after deleting a field or creating a house / power plant
	    {
	        //System.out.format("Checking neighbours after deletion / powerplant / house (%d, %d)", X, Y);
	    }
	
	    if (CheckNeighbours != 2) {
	        // Up
	    	if (getCellStatus(x, y - 1) && !getCellStatus(x, y + 1) && !getCellStatus(x - 1, y) && !getCellStatus(x + 1, y)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_UD);
	            //System.out.print("Up");
	        }
	        // Down
	    	else if (getCellStatus(x, y + 1) && !getCellStatus(x, y - 1) && !getCellStatus(x - 1, y) && !getCellStatus(x + 1, y)) {
	        	gridData[y][x] = SPLINE.getID(Type.MAP_UD);
	            //System.out.print("Down");
	        }
	        // Right
	    	else if (getCellStatus(x + 1, y) && !getCellStatus(x, y + 1) && !getCellStatus(x - 1, y) && !getCellStatus(x, y - 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LR);
	            //System.out.print("Right");
	        }
	        // Left
	    	else if (getCellStatus(x - 1, y) && !getCellStatus(x, y + 1) && !getCellStatus(x, y - 1) && !getCellStatus(x + 1, y)) {
	        	gridData[y][x] = SPLINE.getID(Type.MAP_LR);
	            //System.out.print("Left");
	        }
	
	        // UpDown
	    	else if (getCellStatus(x, y + 1) && getCellStatus(x, y - 1) && !getCellStatus(x + 1, y) && !getCellStatus(x - 1, y)) {
	        	gridData[y][x] = SPLINE.getID(Type.MAP_UD);
	            //System.out.print("UpDown");
	        }
	        // LeftRight
	    	else if (getCellStatus(x + 1, y) && getCellStatus(x - 1, y) && !getCellStatus(x, y + 1) && !getCellStatus(x, y - 1)) {
	        	gridData[y][x] = SPLINE.getID(Type.MAP_LR);
	            //System.out.print("LeftRight");
	        }
	
	        // UpRight
	    	else if (getCellStatus(x, y - 1) && getCellStatus(x + 1, y) && !getCellStatus(x - 1, y) && !getCellStatus(x, y + 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_RU);
	            //System.out.print("UpRight");
	        }
	        // UpLeft
	    	else if (getCellStatus(x, y - 1) && getCellStatus(x - 1, y) && !getCellStatus(x + 1, y) && !getCellStatus(x, y + 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LU);
	            //System.out.print("UpLeft");
	        }
	        // LeftUp
	        // if (GetCellStatus(X - 1, Y) && GetCellStatus(X, Y - 1) && !GetCellStatus(X + 1, Y) && !GetCellStatus(X, Y + 1)) gridData[X][Y] = 34;
	        // DownRight
	    	else if (getCellStatus(x, y + 1) && getCellStatus(x + 1, y) && !getCellStatus(x - 1, y) && !getCellStatus(x, y - 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_RD);
	            //System.out.print("DownRight");
	        }
	        // DownLeft
	    	else if (getCellStatus(x, y + 1) && getCellStatus(x - 1, y) && !getCellStatus(x + 1, y) && !getCellStatus(x, y - 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LD);
	            //System.out.print("DownLeft");
	        }
	
	        // UpRightDown
	        else if (getCellStatus(x, y + 1) && getCellStatus(x + 1, y) && getCellStatus(x, y - 1) && !getCellStatus(x - 1, y)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_RUD);
	            //System.out.print("UpRightDown");
	        }
	        // UpLeftDown
	        else if (getCellStatus(x, y + 1) && getCellStatus(x - 1, y) && getCellStatus(x, y - 1) && !getCellStatus(x + 1, y)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LUD);
	            //System.out.print("UpLeftDown");
	        }
	        // LeftUpRight
	        else if (getCellStatus(x - 1, y) && getCellStatus(x, y - 1) && getCellStatus(x + 1, y) && !getCellStatus(x, y + 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LRU);
	            //System.out.print("LeftUpRight");
	        }
	        // LeftDownRight
	        else if (getCellStatus(x - 1, y) && getCellStatus(x, y + 1) && getCellStatus(x + 1, y) && !getCellStatus(x, y - 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LRD);
	            //System.out.print("LeftDownRight");
	        }
	
	        // All
	        else if (getCellStatus(x - 1, y) && getCellStatus(x, y + 1) && getCellStatus(x + 1, y) && getCellStatus(x, y - 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LRUD);
	            //System.out.print("All");
	        }
	
	        // None
	        else if (!getCellStatus(x - 1, y) && !getCellStatus(x, y + 1) && !getCellStatus(x + 1, y) && !getCellStatus(x, y - 1)) {
	            gridData[y][x] = SPLINE.getID(Type.MAP_LRUD);
	            //System.out.print("None");
	        }
	    }
	
	    if (CheckNeighbours == 1 || CheckNeighbours == 2) {
	        if (x > 0) {
	            calculatePowerLines(x - 1, y, 0);
	        }
	        if (y > 0) {
	            calculatePowerLines(x, y - 1, 0);
	        }
	        if (x < (CELLAMOUNT_WIDTH - 1)) {
	            calculatePowerLines(x + 1, y, 0);
	        }
	        if (y < (CELLAMOUNT_HEIGHT - 1)) {
	            calculatePowerLines(x, y + 1, 0);
	        }
	    }
	}

	/**
	 */
	public void clearGrid() {
		
		int[][] gridData = gridModel.gridData;
		SmartGridObject[][] gridObjects = gridModel.gridObjects;
		LinkedList<SmartGridObject> gridObjectList = gridModel.gridObjectList;
		
	    for (int y = 0; y < gridModel.getNrOfCellsHeight(); y++) {
	        for (int x = 0; x < gridModel.getNrOfCellsWidth(); x++) {
	        	
	        	if(gridObjects[y][x]!=null)
	        		gridObjects[y][x].closeAllWindows();
	        	
	            gridData[y][x] = EMPTY.getID();
	            gridObjects[y][x] = null;
	        }
	    }
	    gridObjectList.clear();
	    gridModel.buses.clear();
	    colorOverlayController.repaintData();
	}

	/**
	 * Closes all opened parameter windows.
	 * 
	 */
	public void closeAllObjectParameterWindows(){
		for(SmartGridObject sgo : gridModel.gridObjectList){
			sgo.closeAllWindows();
		}
	}
	
	
	
	
	private void setSavedSmartGridObject(SmartGridObject sso){
	    copyObject = sso;
	}
	    
	private SmartGridObject getSavedSmartGridObject() {
	    	
		return copyObject;
	}

	public void actionfor_copyGridObject(){
		
//TODO check for ov = 4, erstelle array zum sichern #obj.on grid.length <- max soviele
		int x = tempRememberMarkObject[0];
		int y = tempRememberMarkObject[1]; 
//		
//		LinkedList<SmartGridObject> gridObjectList = gridModel.gridObjectList;		
		
		SmartGridObject sso = gridModel.gridObjects[y][x];
		if(sso != null)
			setSavedSmartGridObject(sso);
		
	}

	/**
	 * gridController action for deleting an object from the grid
	 * this method only deletes an object wich has a mark on it. 
	 */
	public void actionfor_deleteObject(){
		int[][][] gridData3D = gridModel.gridData3D;
		
		for (int y = 0; y < gridData3D[4].length; y++) {
		 	for (int x = 0; x < gridData3D[4][y].length; x++) {
		 		if(gridData3D[4][y][x] != 0) deleteGridObject(x,y);
		 		
		 	}
		 }

	}
	

	/**
	 * gridController action for cut off an object from the grid.
	 *
	 */
	public void actionfor_cutOffObject() {
		
		actionfor_copyGridObject(); //save a copy
		actionfor_deleteObject(); //delete it on the position
		
		
	}

	/**
	 * Delete an instance in the JGM at the coordinates.
	 * In 2D Object Array, 2D ID Array and 1D List.
	 * @param x
	 * @param y
	 */
	public void deleteGridObject(int x, int y) {//TODO keyboardlistener delete 
		
		SmartGridObject[][] gridObjects = gridModel.gridObjects;
		LinkedList<SmartGridObject> gridObjectList = gridModel.gridObjectList;
		int gridID = EMPTY.getID();
		
		if(gridObjects[y][x] == null){		// nothing to do if there is no object
			return;							// -
		}
		
		
		
		if(gridModel.gridData[y][x] != gridID){
			SmartGridObject sgo = gridModel.gridObjects[y][x];
			undoManager.addEdit(new UndoRedoManager(this, sgo, true));
			
		}
		
		gridObjects[y][x].closeAllWindows();		// close related windows
		gridObjects[y][x] = null;					// remove from 2D Object Array
		gridModel.gridData[y][x] = gridID;	// remove from 2D ID Array
	    
		
	    Iterator<SmartGridObject> iterator = gridObjectList.iterator();
	    while (iterator.hasNext()) {				// remove from 1D List
	    	SmartGridObject temp = iterator.next();
	        if (temp.getX()==x && temp.getY()==y) {
	            iterator.remove();
	            return;
	        }
	    }
	   
	    calculatePowerLines(x,y,1);
		calculatePowerLines(x,y,2);
	    
	    
	}



	/**
	 * @param toVisit - only these objects will be used. Null = use all grid objects
	 * @param sgo - start point
	 * @return all neighbors from object and from the neighbors of neighbors.
	 */
	public LinkedList<SmartGridObject> getAllNeighborsFrom(LinkedList<SmartGridObject> toVisit, SmartGridObject sgo) {
		
		LinkedList<SmartGridObject> allNeighbors = new LinkedList<>();		// new list, will be filled with neighbors
		LinkedList<SmartGridObject> toCheck = new LinkedList<>();
		toCheck.add(sgo);
		
		if(toVisit==null)
			toVisit = new LinkedList<>(gridModel.gridObjectList);
		
		while(!toCheck.isEmpty()){
			SmartGridObject current = toCheck.remove();
			LinkedList<SmartGridObject> newNeighbors = getDirectNeighborsFrom(toVisit, current);
			toCheck.addAll( newNeighbors );
			allNeighbors.addAll( newNeighbors );
		}
		return allNeighbors;
	}


	// ----------------------------------------------------------------------------
	
	/**
	 * Doesn't just read the array but checks if it's out of the array. 
	 * Furthermore, this returns true if it's not empty.
	 * @param x
	 * @param y
	 * @return true if coordinate is not empty, false in all other cases.
	 */
	public boolean getCellStatus(int x, int y) // 
	{
		int[][] gridData = gridModel.gridData;
		
	    if (x < 0 || y < 0 || x > (gridModel.getNrOfCellsWidth() - 1) || y > (gridModel.getNrOfCellsHeight() - 1)) {
	        return false;
	    }
	    else if ((gridData[y][x] != EMPTY.getID())) {
	        return true;
	    }
	    return false;
	}


	/**
	 * Based on all SmartGridObjects in Model. 
	 * @param toVisit - only these objects will be used. Found objects will be removed. Null = use all grid objects
	 * @return List of neighbors which were in the list
	 */
	public LinkedList<SmartGridObject> getDirectNeighborsFrom(LinkedList<SmartGridObject> toVisit, SmartGridObject sgo) {
	    
		int x = sgo.getX();
		int y = sgo.getY();
		LinkedList<SmartGridObject> neighbors = new LinkedList<>();		// new list, will be filled with neighbors
		
	    if (x + 1 < gridModel.getNrOfCellsWidth()) {
	        addNeighborsFrom(toVisit, x + 1, y, neighbors);
	    }
	    if (y + 1 < gridModel.getNrOfCellsHeight()) {
	        addNeighborsFrom(toVisit, x, y + 1, neighbors);
	    }
	    if (x - 1 >= 0) {
	        addNeighborsFrom(toVisit, x - 1, y, neighbors);
	    }
	    if (y - 1 >= 0) {
	        addNeighborsFrom(toVisit, x, y - 1, neighbors);
	    }
	
	    return neighbors;
	}

	/**
	 * @param notVisited - only these objects will be "visited", may also be startObjects. 
	 * 					null... use all grid objects EXCLUDING startObjects.
	 * @param startObjects - objects to start from (normally a cluster of objects), not changed
	 * @return List with direct neighbors of objects in sgos.
	 */
	public LinkedList<SmartGridObject> getDirectNeighborsFrom(LinkedList<SmartGridObject> notVisited, LinkedList<SmartGridObject> startObjects) {
		
		LinkedList<SmartGridObject> allNeighbors = new LinkedList<>();		// new list, will be filled with neighbors
		startObjects = new LinkedList<>(startObjects);
		if(notVisited==null){
			notVisited = new LinkedList<>(gridModel.gridObjectList);
			notVisited.removeAll(startObjects);
		}
		
		while(!startObjects.isEmpty()){
			SmartGridObject current = startObjects.remove();
			LinkedList<SmartGridObject> newNeighbors = getDirectNeighborsFrom(notVisited, current);
			allNeighbors.addAll( newNeighbors );
		}
		return allNeighbors; 
	}

	public SgsGridModel getModel() {
		return gridModel;
	}


	
	// ----------------------------------------------------------------------------
	
	/**
	 * Set every position to "EMPTY"
	 */
	private void initGridData(){
		
		int[][] gridData = gridModel.gridData;			// init grid data!!
		for(int i=0; i<gridData.length; i++){
			for(int j=0; j<gridData[i].length; j++){
				gridData[i][j] = EMPTY.getID();
			}
		}
	}

	/**
	 * New JGM image loading function
	 * @param jgm
	 */
	private static void loadImagesNew(JGridMap jgm){
		
		GridDataEnum[] gridDataEnums = GridDataEnum.values();
		for(GridDataEnum gridDataEnum : gridDataEnums){
			loadEnumImages(jgm, gridDataEnum);
		}
	}
	
	/**
	 * Load data for a single 
	 * 
	 * @param jgm
	 * @param gridDataEnum
	 */
	private static void loadEnumImages(JGridMap jgm, GridDataEnum gridDataEnum){
		
		if(gridDataEnum == GridDataEnum.EMPTY)		// do nothing for the 'empty' enum
			return;									// -
		
		ArrayList<GridDataEnum.Type> existingTypes = gridDataEnum.getExistingTypes();
		Out.pl("Enum '"+gridDataEnum+"' has "+existingTypes.size()+" images. ");
		assert 
		 ( existingTypes.contains(GridDataEnum.Type.ICON) ) ||
		 ( existingTypes.contains(GridDataEnum.Type.ICON) &&  existingTypes.contains(GridDataEnum.Type.MAP_LRUD) );
		
		existingTypes.remove(GridDataEnum.Type.ICON);	// do not add the icon to the JGM.
		for(GridDataEnum.Type type : existingTypes){
			
			int id = gridDataEnum.getID(type);
			String img = gridDataEnum.getImage(type);
			
			jgm.addImageToScale(id, img, true);
		}
	}
	
	
    /**
     * Closes all opened parameter windows.
     * 
     */
    public void refreshAllObjectParameterWindows(){
    	for(SmartGridObject sgo : gridModel.gridObjectList){
    		if ( sgo.ownedWindows.isEmpty() ) {
//    			break;
    		} else {
//    			Object testO=sgo.ownedWindows.get(0);
    			sgo.ownedWindows.get(0).revalidate();
    			sgo.ownedWindows.get(0).repaint();
    		}
    		
    	}
    }
    

    /**
     * @author Sabrina Huber
     * received from the PrimaryController an UndoManger, Set the correct undo manager
     * @param undoManager
     */
	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}



	public void actionfor_undo() {
	
		try{
			if(undoManager != null){
				undoManager.undo();
				
			}
		} catch(Exception e){
			System.err.println("Could not undo " + e.getMessage());
		}
	}



	public void actionfor_redo() {
		
		try{
			if(undoManager != null){
				undoManager.redo();
			}
		} catch(Exception e){
			System.err.println("No Objects exists to redo. Method returns " + e.getMessage());
		}
	}



	
}
