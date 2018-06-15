package sgs.controller;

import static sgs.controller.GridMapListener.MouseKey.LEFT;
import static sgs.controller.GridMapListener.MouseKey.NONE;
import static sgs.controller.GridMapListener.MouseKey.RIGHT;
import static sgs.model.gridData.GridDataEnum.DELETE_GEAR;
import static sgs.model.gridData.GridDataEnum.EMPTY;
import static sgs.model.gridData.GridDataEnum.PLINE;
import static sgs.model.gridData.GridDataEnum.SPLINE;
import jGridMap.JGridMap.JGridMapKeyEvent;
import jGridMap.JGridMap.JGridMapMouseEvent;
import jGridMap.JGridMap.JGridMapEventListener;

import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import sgs.controller.interfaces.GridChangeListener;
import sgs.controller.simulation.NetworkAnalyser;
import sgs.model.SgsGridModel;
import sgs.model.gridData.GridDataEnum;
import sgs.model.gridObjects.PowerLine;
import sgs.model.gridObjects.SmartGridObject;
import sgs.view.PropertiesDialog;
import testing.Out;

/**
 * Handles all Grid Events (user interaction) directly.
 * Works over the GridController.
 *
 * @author Tobi, Schweiger, Huber
 */
public class GridMapListener implements JGridMapEventListener {

	/** NetworkAnalyser used **/
	public final NetworkAnalyser networkAnalyser;
	
	private boolean ctrlActivated = false; //default
    private boolean dragged = false;
    enum MouseKey {LEFT,RIGHT,NONE};
    MouseKey actualKey = NONE;

    
	private final GridController gridController;
	private final SgsGridModel gridModel;

	private final JFrame view;
    private GridChangeListener gridChangeListenerAlgorithm = null;
    
    /**
     * Constructor:
     * @param gridController
     * @param gridModel
     * @param view
     */
    public GridMapListener(GridController gridController, SgsGridModel gridModel, JFrame view){
		this.gridController = gridController;
		this.gridModel = gridModel;
		this.view = view;
		
		this.networkAnalyser = new NetworkAnalyser(gridController);
    }

    
    @Override
    /**
     * @param e type JGRidMapEvent
     */
    public void handleJGridMapMouseEvent(JGridMapMouseEvent e) {
    	
    	actualKey = NONE;

    	
        switch (e.getType()) {
            case drag: //drag of the whole grid 
                leftclick(e);
            	drag(e);
                break;
            case leftPress: //drag and drop a object
//            	
//            	int x = e.getCurrent().x;
//	    		int y = e.getCurrent().y;
//	    		
//	    		gridController.colorOverlayController.addMark(x,y);
            	
                break;
            case leftDoublePress:
            	//no functions ... yet
                break;
            case rightDoublePress:
            	//no functions ... yet

            	break;
            case rightPress:
            	//no functions ... yet

                break;
            case leftRelease:
            	
            	leftclick(e);
            	leftrelease(e);

                break;
            case rightDoubleRelease:
            	rightDoubleRelease(e);
            	break;
            case rightRelease:
            	rightclick(e);
                rightrelease(e);
                break;
		default:
			break;
        }
    }
    
    
    
    /**
     * @param algorithm
     * @return last GridChangeListener
     */
	public GridChangeListener setGridChangeListenerAlgorithm(GridChangeListener algorithm){
		GridChangeListener tmp = this.gridChangeListenerAlgorithm;
		this.gridChangeListenerAlgorithm = algorithm;
		return tmp;
	}



    /**
     * Stuff to do if the grid was changed.
     */
    private void gridChanged(){
    	networkAnalyser.createBusesAndFindPaths();
    	gridController.colorOverlayController.repaintData();
    	
    	if(gridChangeListenerAlgorithm != null)
    		gridChangeListenerAlgorithm.gridChanged();
    }

    /**
     * @param x
     * @param y
     * @return true if placement is ok (only) regarding it's neighbors.
     */
    private boolean isPlacementOk(int x, int y) {
    	
    	int id = gridModel.gridData[y][x];
        if ( PLINE.includesID(id) ) {
            if (!isPowerLinePlaceMentOk(x, y)) {
                return false;
            }
        }
        LinkedList<SmartGridObject> neighbours = gridController.getDirectNeighborsFrom(null, gridModel.gridObjects[y][x]); //TODO beim verschieben gibts an den übergebenen Koordinaten kein sgo somit wird null übergeben
        for (SmartGridObject o : neighbours) {
            if (o instanceof PowerLine) {
                return isPowerLinePlaceMentOk(o.getX(), o.getY());
            }
        }
        return true;
    }

    
    /**
     * 
     * @param x
     * @param y
     * @return
     */
    private boolean isPowerLinePlaceMentOk(int x, int y) {
        LinkedList<SmartGridObject> neighbours = gridController.getDirectNeighborsFrom(null, gridModel.gridObjects[y][x]);
        boolean isOk;
        
        if (neighbours.size() < 2  ) {
        	isOk = true;
        }
        else if (neighbours.size() == 2) {
            
        	SmartGridObject o1 = neighbours.get(0);
        	SmartGridObject o2 = neighbours.get(1);
        	int xDiff = Math.abs( o1.getX()-o2.getX() );
        	int yDiff = Math.abs( o1.getY()-o2.getY() );
        	
        	if(xDiff==2 || yDiff==2){		// the two objects are on opposite sides if their distance equals 2:
        		isOk = true;
        	}
        	else{
        		isOk = false;
        	}
        } 
        else {		// (neighbours.size() > 2)
        	isOk = false;
        }
        
        if(!isOk){
          JOptionPane.showMessageDialog(null,
          "Powerline can only be placed between two Objects,\nthat are on opposite sides of each other!",
          "Placement Error",
          JOptionPane.ERROR_MESSAGE);
        }
        return isOk;
        
    }

    
    
    /**
	 * 
	 * @param e
	 */
	private void leftclick(JGridMapMouseEvent e) {
		Out.pl("leftclick");
		actualKey = LEFT;
		
		
	}

	/**
	     * 
	     * @param e
	     */
	    private void leftrelease(JGridMapMouseEvent e) {
	
	    	// --- releaseXY == originXY, kein drag---
	    	//
	    	if (e.getCurrent().equals( e.getOrigin()) ) {		
	
	    		int x = e.getCurrent().x;
	    		int y = e.getCurrent().y;
	    		
	    		GridDataEnum pointer = gridModel.currentPointer;
	    		//System.out.format("Mouseclick (%d, %d), "+pointer+"\n", x, y);
	
	    		// First "gear"
	    		if( pointer==DELETE_GEAR ) {
//	    			deleteGridObject(x,y); //TODO
	
	    			if (gridModel.gridData[y][x] != EMPTY.getID()) {
	
	    				gridController.deleteGridObject(x, y);
	    				gridController.calculatePowerLines(x, y, 2);
	    				gridChanged();
	    			}
	    		}
	    		// Other "gears" (useless)
	    		else if(pointer==null || pointer.ordinal()<PLINE.ordinal()) {
	    			
	    					if (ctrlActivated){ 

	    						gridController.colorOverlayController.markOne(x,y,true);
	    					} else {

		    					gridController.colorOverlayController.markOne(x,y,false);
		    					System.out.println("orgX " + x +" / " +  y );
	    						
	    						int [] temp = new int[2];
	    						
	    						temp [0] = x;
	    						temp [1] = y; 
	    					
	    						gridController.tempRememberMarkObject = temp;
	    						
	    					}	    		
	    		
	    		}
	    		// Object placement:
	    		else {
	    			if (gridModel.gridData[y][x] == EMPTY.getID()) {
	//    				if(gridController.getCopyModus() == false){
		    					gridController.addGridObject(pointer, x, y);
		
		    				if (isPlacementOk(x, y)) {
		    					
		    					if ( PLINE==pointer || SPLINE==pointer )
		    						gridController.calculatePowerLines(x, y, 1);
		    					else
		    						gridController.calculatePowerLines(x, y, 2);
		
		    					gridChanged();
		    				}
		    				else{
		    					gridController.deleteGridObject(x, y);
//		    					gridController.undoManager.addEdit(new UndoRedoManager(gridController, gridModel.gridObjects[y][x]));
		    				}
	    					
	    				
	    			}
	    		}
	
	    	} 
	    	// --- releaseXY != originXY ---
	    	//
	    	else {							
	    		if (dragged) {
	    			int targetX = e.getCurrent().x;
	    			int targetY = e.getCurrent().y;
	    			int orgX = e.getOrigin().x;
	    			int orgY = e.getOrigin().y;
	    			
	    			if ( gridModel.gridData[targetY][targetX] != EMPTY.getID() 
	    					|| gridModel.gridData[orgY][orgX] == EMPTY.getID() )
	    			{
	    				// do not drag in this cases.
	    			} 
	    			else {	// try drag:
	    				
	    				SmartGridObject sso = gridModel.gridObjects[orgY][orgX];
	    				
	    				gridController.deleteGridObject(orgX, orgY);
	    				gridController.addGridObject(sso, targetX, targetY);
	    				
	    				if(isPlacementOk(targetX, targetY)){
	        				gridController.calculatePowerLines(targetX, targetY, 1);
	        				gridController.calculatePowerLines(targetX, targetY, 2);
	        				gridController.calculatePowerLines(orgX, orgY, 1);
	        				gridController.calculatePowerLines(orgX, orgY, 2);
	        				gridChanged();
	    				}
	    				else{
	    					
	    					gridController.deleteGridObject(targetX, targetY);
	    					gridController.addGridObject(sso, orgX, orgY);
//	    					gridController.undoManager.addEdit(new UndoRedoManager(gridController, sso));
	    				}
	    				
	    			}
	    		}
	    	}
	    	
	    	dragged = false;		// reset values
	    	actualKey = NONE;		// -
	    }

	    
	    
	/**
	 * @param e
	 */
	private void drag(JGridMapMouseEvent e) {
		
	    if (!dragged && actualKey==LEFT) {	// only drag with left key
	    	
	        int x = e.getOrigin().x;
	        int y = e.getOrigin().y;
	        SmartGridObject sso = gridModel.gridObjects[y][x];
	        //Out.pl("Prepare drag: "+x+"/"+y+" - "+sso.getClass().getSimpleName() +", "+sso.isDraggable());
	        
	        if(sso!=null && sso.isDraggable()) {
	        	Out.pl("handle drag: "+x+"/"+y+" - "+sso.getClass().getSimpleName() +", "+sso.isDraggable());
	            dragged = true;
	             
	        }
	    }
	}

	
	
	/**
	 * 
	 * @param e
	 */
	private void rightclick(JGridMapMouseEvent e) {
		Out.pl("rightclick");
		actualKey = RIGHT;
	}

	
	
	
	/**
	 * 
	 * @param e
	 */
	private void rightrelease(JGridMapMouseEvent e) {
	
		//transfered to rightDoubleRelease()
		//TODO im Controller Popupmenu zugreifen. der controller ruft die view auf und kümmert sich um die arbeit
		if(e.getCurrent().equals(e.getOrigin())){
			int x = e.getCurrent().x;
			int y = e.getCurrent().y;
			if (gridModel.gridData[y][x] != EMPTY.getID()) {
//			new PropertiesDialog(gridModel, x, y);
				new GridPopUpMenuController(gridModel, x, y, view);
				
		}
	}
	dragged = false;	// reset, no drag with right.
	actualKey = NONE;
		
	}

	
	
	private void rightDoubleRelease(JGridMapMouseEvent e) {
		
    	if(e.getCurrent().equals(e.getOrigin())){
    		int x = e.getCurrent().x;
            int y = e.getCurrent().y;

            if (gridModel.gridData[y][x] != EMPTY.getID()) {
            	new PropertiesDialog(gridModel, x, y);
            } else {
                rightrelease(e);
            }
        }
        
        dragged = false;
        actualKey = NONE;
	}


	@Override
	public void handleJGridMapKeyBoardEvent(JGridMapKeyEvent event) {
		
		switch ((event.getKeyEventType())) {
			case markAll: 
				gridController.colorOverlayController.markAll();
				view.repaint();
				break;
			case markMore_ON:
				ctrlActivated = true;
				view.repaint();
				break;
			case markMore_OFF:
				ctrlActivated = false;
				break;
			case copy:
				try{
    				gridController.actionfor_copyGridObject();
    				view.repaint();
    			} catch (Exception e){
    				 System.err.println("copy of object not possible. method returns " + e.getMessage());
    			}
				break;				
			case paste: 
				try {
    				gridController.actionfor_pasteCopiedObject();
    				view.repaint();
    			} catch(NullPointerException e){
    				System.err.println("Missing: object to copy is " + e.getMessage());
    			}
				break;				
			case undo:			
				try{
					gridController.actionfor_undo();
					view.repaint();
				} catch(Exception e){
					System.err.println("Could not undo " + e.getMessage());
				}

				break;
			case cutoff:
				try{
					gridController.actionfor_cutOffObject();
					view.repaint();
				} catch(NullPointerException e){
					System.err.println("Missing object to cut of " + e.getMessage());
				}
				break;
			case redo:
				try{
					gridController.actionfor_redo();
					view.repaint();
				} catch (NullPointerException e){
					System.err.println("Theres no object for redo function. Method returns " + e.getMessage());
				}
				
				break;
				
			case save:
//				fileManager.saveGridXML(ProgramConstants.defaultSimulationFile);
				break;
				
			case newGrid:
				gridController.clearGrid();
				view.repaint();
				break;
				
			case delete:
				try{				
					gridController.actionfor_deleteObject();
					view.repaint();
				} catch (Exception e){
					System.err.println("delete cant be done" + e.getMessage());
				}
				break;
				
       
			default:
				break;
    }
		
	}


	

	

	
}
