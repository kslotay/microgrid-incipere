package sgs.controller;

import jGridMap.view.PrimaryView;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.undo.UndoManager;

import sgs.controller.fileManagement.FileManager;
import sgs.controller.fileManagement.csvConstruction.CSVController;
import sgs.controller.simulation.TimeThread;
import sgs.model.ProgramConstants;
import sgs.model.SgsGridModel;
import sgs.view.FirstStartWindow;

/**
 * Has full access to the View and the Model. (MVC)
 * 
 * Initializes the ColorLegendPanel
 * 
 * Starts and organizes other controllers:
 * 	GridController - grid interaction
 * 	ButtonController - buttons, user interaction
 * 	TimeThread - simulation
 * 	MenuController - menu, user interaction
 * 
 * Provides more general function: exitProgramm()
 * 
 * @author Kristofer Schweiger
 *
 */
public class PrimaryController {
	
	/** the file manager for loading/saving data; static for providing a general and static exit method **/
	private static FileManager fileManager;
	/** the view; static for providing a general and static exit method **/
	private static PrimaryView primaryView;
	/** the model; final because it should never change **/
	private final SgsGridModel gridModel;
	
	
	
	/**
	 * Constructor:
	 * Use the constructor only once, else there can be problems with static components.
	 * 
	 * @param gridModel
	 * @param primaryView
	 */
	public PrimaryController(SgsGridModel gridModel, PrimaryView primaryView){
		
		assert PrimaryController.fileManager==null;	// should not be set/used before; check because of "static"
		assert PrimaryController.primaryView==null;	// -
		
		// Access view class directly (no interfaces) instead of casting
		this.gridModel = gridModel;
		PrimaryController.primaryView = primaryView;

		CSVController csvController = new CSVController(gridModel);
		
		
		// --- Start other controllers ---
		//
		GridController gridController = new GridController(gridModel, primaryView);
		// Button controller:
		ButtonController buttonController = new ButtonController(primaryView, gridController);
//		//ColorOverlayController
//		ColorOverlayController colorOverlayController = new ColorOverlayController(gridController, primaryView);
//		
		
		// Time thread:
		TimeThread timeThread = new TimeThread(gridController, buttonController, csvController);
		buttonController.setTimeThread(timeThread);
		
		//Menu controller:
		new MenuController(primaryView, primaryView, gridController, timeThread, primaryView);
		
		// --- specify window close operation ---
		//
		primaryView.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		primaryView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				PrimaryController.exitProgramm();
			}
		});
		
		// --- File management ---
		//
		fileManager = new FileManager(gridController);
		fileManager.load(ProgramConstants.defaultSimulationFile, ProgramConstants.parameterFile);
		
		if(gridModel.programParameters.isFirstStart()){
			FirstStartWindow.start(PrimaryController.primaryView, gridModel.programParameters);
		}
		
		// --- Undo Manager ---
				//
		UndoManager undoManager = new UndoManager();
		undoManager.setLimit(10); 	//max 10 moves verzeichnen
		gridController.setUndoManager(undoManager);
			
	}
	
    /**
     * Static function for exit.
     * 
     */
    public static void exitProgramm() {
    	
        if(fileManager.gridModel.programParameters.parametersChanged)
        	fileManager.saveProgramParameters(ProgramConstants.parameterFile);
    	
        Object[] options = {
        	"Save & Exit",
            "Exit without Save",
            "Cancel"
        };
        int confirmed = JOptionPane.showOptionDialog(primaryView,
                "Are you sure you want to exit?", "User Confirmation",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        
        if (confirmed == JOptionPane.YES_OPTION) {
        	fileManager.saveGridXML(ProgramConstants.defaultSimulationFile);
            PrimaryView.exit(primaryView);
            
        } else if (confirmed == JOptionPane.NO_OPTION) {
            PrimaryView.exit(primaryView);
            
        } else if (confirmed == JOptionPane.CANCEL_OPTION || confirmed == JOptionPane.CLOSED_OPTION) {
        	// do nothing
        }
    }
    
	public SgsGridModel getGridModel() {
		return gridModel;
	}
    
    
}
