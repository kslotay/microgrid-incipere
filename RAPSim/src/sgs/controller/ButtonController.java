package sgs.controller;


import jGridMap.view.interfaces.PrimaryViewI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import sgs.controller.simulation.TimeThread;
import sgs.controller.simulation.Weather;
import sgs.model.SgsGridModel;
import sgs.model.gridData.GridDataEnum;
import sgs.view.BottomPanel;
import sgs.view.SimOptionsDialog;
import sgs.view.SpeedChanger;
import sgs.view.TopPanel;

/**
 * Controller for ButtonManagerView. 
 * Sets up the button panels on top and bottom.
 * @author Kristofer Schweiger, Breiling
 */
public class ButtonController {
	
	
	private JToggleButton bStartStopSim;
  //private FileManager fileManager;
	private GridController gridController;
	private SgsGridModel gridModel;
	private TimeThread timeThread;
	
//	private CSVController csvController;
	public TopPanel topPanel = new TopPanel();
	public BottomPanel bottomPanel = new BottomPanel();
	
	ArrayList<Class<?>> modelClasses;
	Class<?> abstractModelClass;
	Class<?> prosumerClass;
	JPopupMenu modelmenu;
	PrimaryViewI primaryView;

	
	/**
	 * Constructor
	 * @param primaryView setJComponent topPanel to BorderLayout.NORTH, bottomPanel to BorderLayout.SOUTH
	 * @param gridController
	 * @param csvController 
	 */
	public ButtonController(PrimaryViewI primaryView, GridController gridController){
		
		//this.primaryView     = primaryView;
		this.gridController = gridController;
		//this.timeThread 	= timeThread;
		
		this.gridModel      = gridController.getModel();
    //this.fileManager    = new FileManager(gridController);
    	
		initButtonsBottom();
		initButtonsTop();
		
		primaryView.setJComponent(topPanel, BorderLayout.NORTH);
		primaryView.setJComponent(bottomPanel, BorderLayout.SOUTH);
		this.primaryView = primaryView;
		
//		this.csvController = csvController;
	}
	
	/**
	 * Initialize bottom buttons with action listeners,
	 * get the enum list, init button for every enum. Add to each Icon Tooltip and actionlistener.
	 */
	private void initButtonsBottom(){
		
		// --- get enum list ---
		GridDataEnum[] enumValues = GridDataEnum.values();
		
		for(int i=1; i<enumValues.length; i++){		// init button for every Enum
			final GridDataEnum enu = enumValues[i];
			
			bottomPanel.addControl_BottomButton(
					enu.getIcon(),
					enu.getToolTip(),
					new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {	// anonymous class, direct
							gridModel.currentPointer = enu;	
							if(enu.getMappedModel() != null){
								abstractModelClass = enu.getMappedModel();
								prosumerClass = enu.getMappedClass();
								showModelChooser(arg0);
							}
						}
					}
					);			
		}
	}
	/**
	 * @author bbreilin
	 * @param arg0
	 * shows a menu with all available models from a certain type
	 */
	private void showModelChooser(ActionEvent arg0){
		ArrayList<String> modelNames = new ArrayList<String>();
		modelClasses = ReflectionStuff.getClassesForPackage(abstractModelClass);
		modelClasses = ReflectionStuff.getSubClasses(abstractModelClass, modelClasses, false);
		if(modelClasses.isEmpty()) {
			JButton clicked = (JButton) arg0.getSource();
			clicked.setEnabled(false);
			JOptionPane.showMessageDialog((Component) primaryView,
				    "No model of Type: "+ abstractModelClass.getSimpleName(),
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
			return;
		}
		for(Class<?> c: modelClasses){
    		modelNames.add(c.getSimpleName());
    	}
		modelmenu = new JPopupMenu("Models");
		for(int i=0;i<modelNames.size();i++){
			String s = modelNames.get(i);
			JMenuItem item = new JMenuItem(s);
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					JMenuItem item = (JMenuItem) e.getSource();
					int index = modelmenu.getComponentIndex(item);
					try {
						gridModel.modelConstructor = modelClasses.get(index).getConstructor(prosumerClass);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			modelmenu.add(item);
		}
		JButton button = (JButton) arg0.getSource();
		modelmenu.show(button, button.getWidth()/2, button.getHeight()/2);
		
	}
	/**
	 * Initialize bottom buttons with action listeners
	 * buttons: Start Sim, Clear Grid, Color Overlay and Reset Power Consumption
	 */
	private void initButtonsTop(){
		
    	bStartStopSim = topPanel.addControl_TopToggle("Start Sim",new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) { // anonymous class, indirect
	            actionFor_bStartStopSim(arg0);
			}
    	});
    	
    	topPanel.addControl_TopButton("Refresh Attributes",new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) { // anonymous class, indirect
	            gridController.refreshAllObjectParameterWindows();
			}
    	});
    	
//    	topPanel.addControl_TopButton("Export to .xls",new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				System.err.println("Not implemented!");
//	            //System.out.println("Exported!");
//			}
//    	});
    	
//    	change position to MenuController, into the Edit-function
    	
//    	topPanel.addControl_TopButton("Clear Grid",new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//	            int result = JOptionPane.showConfirmDialog(SgsGridModel.mainView, "Do you really want to clear the Grid?", "Clear Grid?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//	            if (result == JOptionPane.YES_OPTION) {
//	            	
//	            	gridController.clearGrid();
//	            }
//			}
//    	});
    	
    	topPanel.addControl_TopButton("Undo", new ActionListener(){
    		@Override
			public void actionPerformed(ActionEvent arg0){
//    			UndoManager um = gridController.gridMapListener.undoManager;
//    			if(um != null){
//    				try{
//    					um.undo();
//    				} catch(Exception e){ System.err.println("Could not undo: "+e.getMessage()); }
//    			}
    			try{
    				gridController.actionfor_undo();
    				
    			} catch(NullPointerException e){
    				System.err.println("Could not undo " + e.getMessage());
    			}
    		}
    	});  	
    	
    	
    	
    	//transfered to ColorOverlayController
//    	topPanel.addControl_TopToggle("Color Overlay",new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				gridModel.overlayUse = !gridModel.overlayUse;
//				gridController.repaintData();
//			}
//    	});
    	
    	
    	
//    	topPanel.addControl_TopButton("Reset Power Consumption",new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//	            for (SmartGridObject o : gridModel.gridObjectList) {
//	                o.resetCurrentPowerConsumption();
//	            }
//	            System.out.println("All Values Reset!");
//				gridController.repaintData();
//			}
//    	});
    	
//    	topPanel.addControl_TopButton("Save Data",new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//	            fileManager.saveGridXML(new File("data.xml"));
//			}
//    	});
		
	}
	
	/**
	 * private method, start and stop simulation
	 * @param arg0 - unused
	 * if {@code bStartStopSim.isSelected()} start simulation and create a new Dialog, with timeThread and gridModel.
	 * 
	 * if creation of Dialog is confirmed, instruct the view to provide field for
	 * StartTime, endTime, currentTime and the Start or Stop button
	 */
	public void actionFor_bStartStopSim(ActionEvent arg0){
		
        if (bStartStopSim.isSelected()) {	// start simulation:
        	SimOptionsDialog optionsDialog = new SimOptionsDialog(timeThread, gridModel);
            
            if (optionsDialog.isConfirmed()) {
            	timeThread.startTime = optionsDialog.getStartTime();
            	timeThread.endTime = optionsDialog.getEndTime();
            	timeThread.currentTime = timeThread.startTime;
            	timeThread.simulationResolutionMinutes = optionsDialog.getResolution();
                if (timeThread.realTime) {
//                	SgsGridModel.sliderDialog = new SpeedChanger(timeThread);
//                	SgsGridModel.sliderDialog.setVisible(true);
                	timeThread.sliderDialog = new SpeedChanger(timeThread);
                	timeThread.sliderDialog.setVisible(true);                
                	}
                bStartStopSim.setText("Stop Sim");
                timeThread.startOrStop(true);
            } else {
                bStartStopSim.setSelected(false);
            }
        } else {						// stop simulation:
            bStartStopSim.setText("Start Sim");
            timeThread.startOrStop(false);
            if (timeThread.realTime) {
            	timeThread.sliderDialog.setVisible(false);
            }
        }
	}
	
	/**
	 * @param timeThread
	 */
	public void setTimeThread(TimeThread timeThread){
		this.timeThread = timeThread;
	}
	
	/**
	 * set Text for the Start/Stop Simulation Button "Start Sim"
	 * when Simulations has been stopped
	 */
	public void resetStartStopSimButton()
	{
		bStartStopSim.setText("Start Sim");
		bStartStopSim.setSelected(false);
	}

	/**
	 * 
	 * @param currentWeather
	 * @param currentTime
	 */
	public void updateDateAndTime(Weather currentWeather, GregorianCalendar currentTime) {
		topPanel.updateDateAndTime(currentWeather, currentTime);
	}
	
}
