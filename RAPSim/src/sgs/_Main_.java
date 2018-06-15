package sgs;

import javax.swing.ImageIcon;
import jGridMap.view.PrimaryView;
import sgs.controller.PrimaryController;
import sgs.controller.ReflectionStuff;
import sgs.model.SgsGridModel;
import sgs.model.gridData.GridDataTree;
import sgs.model.gridObjects.SmartGridObject;

/**
 * Main class for initializing and starting the SGS.
 * 
 * a) take care of initializing static data, e.g. for recognition of all classes implementing SmartGridObject
 * b) initialize the model (contains JGM)
 * c) initialize the the view with the model
 * d) initialize controller
 * e) final actions like making the GUI visible
 * 
 * @author Kristofer Schweiger, Sabrina Huber
 */
public class _Main_ {

	/** Number of Cells for width/height of the GridMap, e.g. 40/25. JGridMap counts from 0 to 39 (40 Cells), and from 0 to 25 (for 26 Cells) **/
	private static int nrOfCellsWidth=40, nrOfCellsHeight=25;
	/** Width and height in pixels **/
	private static int gridWidth=nrOfCellsWidth*20, gridHeight=nrOfCellsHeight*20;
	
	
	/**
	 * Initialize & Start
	 * @param args - unused
	 */
	public static void main(String[] args) {
		
		// --- initialization for static data ---
		//
		GridDataTree.INIT_TREE(ReflectionStuff.getClassesForPackage(SmartGridObject.class));
		 
		// --- Model ---
		//
		/**gives Model 
		 * @see SgsGridModel.java
		 * {@code SgsGridModel gridModel = new SgsGridModel(nrOfCellsWidth, nrOfCellsHeight, gridWidth, gridHeight);}
		 * gridModel => data about Width, Height and content of Cells */
		SgsGridModel gridModel = new SgsGridModel(nrOfCellsWidth, nrOfCellsHeight, gridWidth, gridHeight);
		SmartGridObject.gridModel = gridModel;	// static model, so it doesn't need to be set for every object extra.
		
		// --- View ---
		//
		/**{@code PrimaryView primaryView = new PrimaryView("SGS - Smart Grid Simulator", gridModel);}
		 * View of JGridMap and rest of the GUI, set Name to "SGS - Smart Grid Simulator" and size of given gridModel */
		PrimaryView primaryView = new PrimaryView("RAPSim - Renewable Alternative Powersystems Simulation", gridModel);
		
	
		ImageIcon RAPSimIcon = new ImageIcon("./Data2/RAPSim_ICON.png");
		primaryView.setIconImage( (RAPSimIcon).getImage() ); 

		
		SgsGridModel.mainView = primaryView;
	
		// --- Controllers ---
		//
		new PrimaryController(gridModel, primaryView);
		
		/**{@code primaryView.pack(); primaryView.setVisible(true);}
		 * start up, set Visible true, and causes the window to be sized to fit the preferred size and layouts*/
		primaryView.pack();
		primaryView.setVisible(true);
		primaryView.setLocationRelativeTo(null);

	}
	
}
