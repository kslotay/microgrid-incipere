package sgs.controller;

import java.util.LinkedList;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import sgs.model.SgsGridModel;
import sgs.model.gridObjects.SmartGridObject;

/***
 * 
 * @author hs
 * Controller for all functions , that has been placed in the menubar edit
 *tempo implemented it here,
 * Edit Function of Property window will stay here.
 * maybe Undo, Redo, Copy and Paste will be moved to JGridMap later
 */

public class EditFunctionController {

	final UndoManager m = new UndoManager();
    
      
    /**
    * 
    * @param gridObjects
    * @param gridObjectList
    * @param gObj
    * @param gridModel
    */
	public EditFunctionController(SmartGridObject[][] gridObjects,
			LinkedList<SmartGridObject> gridObjectList, SmartGridObject gObj,
			SgsGridModel gridModel) {
		super();
		 
	}

  
	/**
	 * 
	 * @param gobj
	 */
    public void UndoManager(SmartGridObject gobj){
    	  	
    	AddEdit(gobj);
    }
    
    /**
	 * add the SmartGridObjects to the Manager, so that he knows what to undo 
	 * @param gobj
	 */
    private void AddEdit(SmartGridObject gobj){
    	m.addEdit((UndoableEdit)gobj);
    }

    
    
	/**
     * @author hs
     * 
     */
    public void EditUndoFunction() throws CannotUndoException{
		// TODO Auto-generated method stub
    	 
    		
	}
    
    
    
    
    public void EditRedoFunction() throws CannotRedoException{
		// TODO Auto-generated method stub
		
//    	if(){
//    		stack.pop();
//    	}
    	
	}
    
    
    
    
    
	
}
