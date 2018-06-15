package sgs.controller.undoableEdit;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import sgs.controller.GridController;
import sgs.model.gridObjects.SmartGridObject;


public class UndoRedoManager implements UndoableEdit{

//	private final GridMapListener gml;
	private final GridController gridController; 
	private SmartGridObject sgo;
	private boolean canRedo=false, canUndo=true;
	private boolean objectDeleted = false; // to know if redo/undo should add or delete an object, we save if it was deleted or added 
	
	/**
	 * Constructor for undoing an delete action in the GridMap
	 * @param gc
	 * @param sgs
	 */
	public UndoRedoManager(GridController gc, SmartGridObject sgs, boolean objectStatus) {
//		this.gml = gc;
		
		this.gridController = gc;
		this.sgo = sgs;
		
		this.objectDeleted = objectStatus; 
		//this.view = view;
		
	}

	@Override
	public boolean addEdit(UndoableEdit arg0) {
		
		return false;
	}

	@Override
	public boolean canRedo() {
		return canRedo;
	}

	@Override
	public boolean canUndo() {
		return canUndo;
	}

	@Override
	public void die() {
		canRedo = false;
		canUndo = false;
		sgo = null; // Ok :(
	}

	@Override
	public String getPresentationName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getRedoPresentationName() {
		return getPresentationName();
	}

	@Override
	public String getUndoPresentationName() {
		return getPresentationName();
	}

	@Override
	public boolean isSignificant() {
		return true;
	}

	@Override
	public void redo() throws CannotRedoException {
//TODO jetzt soll es ja nicht immer löschen oder hinzufügen
//		
		if(objectDeleted) {
			gridController.deleteGridObject(sgo.getX(), sgo.getY());
//			replaceEdit(new UndoRedoManager(gridController,sgo,false));
//			addEdit(new UndoRedoManager(gridController, sgo, false));
		}
		else {
			gridController.addGridObject(sgo, sgo.getX(), sgo.getY());
//			addEdit(new UndoRedoManager(gridController, sgo, false));
//			replaceEdit(new UndoRedoManager(gridController,sgo,true));
		}
		
		
//		gridController.deleteGridObject(sgo.getX(), sgo.getY());
//		undo();
	
		
	}
	@Override
	public boolean replaceEdit(UndoableEdit arg0) {
		return false;
	}

	@Override
	public void undo() throws CannotUndoException { 	
		if(objectDeleted){
			gridController.addGridObject(sgo, sgo.getX(), sgo.getY());
//			addEdit(new UndoRedoManager(gridController, sgo, true));
//			replaceEdit(new UndoRedoManager(gridController,sgo,true));
			
		}
		else {
			gridController.deleteGridObject(sgo.getX(), sgo.getY());
//			replaceEdit(new UndoRedoManager(gridController,sgo,false));
//			addEdit(new UndoRedoManager(gridController, sgo, false));
		}
		
//		gridController.addGridObject(sgo, sgo.getX(), sgo.getY());

	}

	
	

}
