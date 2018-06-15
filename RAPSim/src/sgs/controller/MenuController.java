package sgs.controller;

import static sgs.model.gridData.GridDataEnum.EMPTY;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import jGridMap.view.FileChooser;
import jGridMap.view.FileChooser.DialogType;
import jGridMap.view.MenuOrganizer.MenuItemType;
import jGridMap.view.MenuOrganizer.MenuSpecialType;
import jGridMap.view.PrimaryView;
import jGridMap.view.interfaces.MenuViewI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import sgs.model.SgsGridModel;
import sgs.controller.fileManagement.FileManager;
import sgs.controller.simulation.AbstractDistributionAlgorithm;
import sgs.controller.simulation.TimeThread;
import sgs.model.ProgramConstants;
import sgs.view.AboutDialog;
import sgs.view.AlgorithmDescriptionDialog;
import sgs.view.PropertiesDialog;
import sgs.view.SpeedChanger;
import sgs.view.showResultsCSVView;
import testing.Out;


/**
 * Controller component for the menu.
 * @author Tobi, Kristofer Schweiger, Huber
 */
public class MenuController  {

    private final JFrame frame;
    private final MenuViewI menuView;
    private static JMenuItem showResults;
    public static JMenuItem openSlider;
    public JCheckBoxMenuItem allLegendsCB , nominalPowerCB, voltageAngleCB, nominalVoltageCB;

    
    private FileChooser fileChooser;
    private FileFilter fileFilter;
    private final FileManager fileManager;
    private final GridController gridController;
    private final TimeThread timeThread;
    public static boolean treeNotEmpty = false;
    private final ColorOverlayController colorOverlayController; 


    /**
     * Constructor for controller
     * @param menuView
     * @param frame
     * @param gridController
     * @param timeThread
     * @param primaryView 
     */
    public MenuController(MenuViewI menuView, JFrame frame, GridController gridController, TimeThread timeThread, PrimaryView primaryView) {
    	this.menuView = menuView;
    	this.frame = frame;
    	this.gridController = gridController;
    	this.fileManager = new FileManager(gridController);
    	this.timeThread = timeThread;
    	addMenu();
    	initFileChooser();
        this.colorOverlayController = gridController.colorOverlayController; 


	}
    
 

	/**
     * FileChooser related initialization.
     */
    private void initFileChooser(){
    	
    	fileChooser = new FileChooser(frame);
    	
    	fileFilter = new FileFilter() {
			
            @Override
			public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".zip") || f.isDirectory();
            }

            @Override
			public String getDescription() {
                return "ZIP Files (*.zip) ";
            }
		};
    }

    
    
	/**
     * 
     * @param frame
     */
    public void addMenu() {
    	addMenu_File();
    	addMenu_Edit();
    	addMenu_Algorithm();
    	addMenu_Results();
    	

	
    
    	// --- About --- ----------------
    	//
    	menuView.addControl_Menu(new String[]{"?", "About"}, MenuItemType.jMenueItem, 
    		new ActionListener() {

    			@Override
				public void actionPerformed(ActionEvent arg0) {
   					new AboutDialog(frame).setVisible(true);
   				}
   			});
    }
    
   

	private void addMenu_File() {
		
    	// --- File --- -----------------
    	//
    	menuView.addControl_Menu(new String[]{"File", "New"}, MenuItemType.jMenueItem, 
    			new ActionListener() {

    				@Override
					public void actionPerformed(ActionEvent arg0) {
    					actionFor_new();
    				}
    			});

    	
    	
    	menuView.addControl_Menu(new String[]{"File", "Open"}, MenuItemType.jMenueItem, 
    			new ActionListener() {

    				@Override
					public void actionPerformed(ActionEvent arg0) {
    					actionFor_open();
    				}
    			});
    	
    	menuView.addSpecialControl_Menu(null, MenuSpecialType.JSeperator);
    	

    	JMenuItem save = menuView.addControl_Menu(new String[]{"File", "Save"}, MenuItemType.jMenueItem, 
    			new ActionListener() {

    				@Override
					public void actionPerformed(ActionEvent arg0) {
    					actionFor_save();
    				}
    			});
    	save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
    	

    	menuView.addControl_Menu(new String[]{"File", "Save as"}, MenuItemType.jMenueItem, 
    			new ActionListener() {

    				@Override
					public void actionPerformed(ActionEvent arg0) {
    					actionFor_saveAs();
    					
    				} 
    			});

    	
//    	menuView.addSpecialControl_Menu(null, MenuSpecialType.JSeperator);
//
//    	menuView.addControl_Menu(new String[]{"File", "Options"}, MenuItemType.jMenueItem, 
//    			null);

    	menuView.addSpecialControl_Menu(null, MenuSpecialType.JSeperator);

    	menuView.addControl_Menu(new String[]{"File", "Exit"}, MenuItemType.jMenueItem, 
   			new ActionListener() {
    		
   				@Override
				public void actionPerformed(ActionEvent arg0) {
   					PrimaryController.exitProgramm();
   				}
   			});
		
	}


	private void addMenu_Edit() {
	    	// --- Edit --- -----------------
	    	// hs
	    	/**
	    	 *@author hs
	    	 * expansion of the function Edit
	    	 * Undo deleted Objects, Open&Close&Refresh all Property Windows
	    	 */
	    	menuView.addControl_Menu(new String[]{"Edit"}, MenuItemType.jMenue, null);
	    	
	    	
	    	/**
	    	 * Undo only deleted Objects
	    	 */
	    	JMenuItem undo = menuView.addControl_Menu(new String[]{"Edit",  "Undo"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
	    			gridController.actionfor_undo();
	    			
	    			}
	    	});
	    	
	    	undo.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	    	
	    	
	    	/**
	    	 * ReDo deleted Objects
	    	 */
	    	JMenuItem redo = menuView.addControl_Menu(new String[]{"Edit",  "Redo"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
//	    			KeyStroke.getKeyStroke('Y', KeyEvent.CTRL_DOWN_MASK);
	    			gridController.actionfor_redo();
	    			frame.repaint();
	    		}
			
	    	});
	    	redo.setAccelerator(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	    	
	    menuView.addSpecialControl_Menu(null, MenuSpecialType.JSeperator);

	    JMenuItem cut = menuView.addControl_Menu(new String []{"Edit", "Cut"}, MenuItemType.jMenueItem, new ActionListener(){
	    	@Override
	    	public void actionPerformed(ActionEvent arg0){
	    	
	    		gridController.actionfor_cutOffObject();
	    		frame.repaint();
	    		
	    	}
	    });
	    	
	    cut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	    
	    	//copy and paste functions. realize with stack, able to copy more than one.. five perhaps as limit
	    	/**
	    	 * Copy Sgs Obj, put it on Stack
	    	 * limited to 5 Obj
	    	 */
	    	
	    JMenuItem copy = menuView.addControl_Menu(new String[]{"Edit",  "Copy"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
	   // 	});
	    			gridController.actionfor_copyGridObject();
	    			frame.repaint();
	    		}
	    	});

	    	copy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

	    	
	    	/**
	    	 * Paste Sgs Obj, pop out the Stack
	    	 * limited to the max 
	    	 */
	    JMenuItem paste = menuView.addControl_Menu(new String[]{"Edit",  "Paste"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
	    			gridController.actionfor_pasteCopiedObject();
	    			frame.repaint();
	    		}
	    	});
	    	paste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	    			
	    	
	    JMenuItem delete = menuView.addControl_Menu(new String[]{"Edit","Delete"},  MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
	    		public void actionPerformed(ActionEvent arg0){
	    			gridController.actionfor_deleteObject();
	    			frame.repaint();
	    		}
	    	});
	    	paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
  	
//	    delete.setAccelerator(KeyStroke.getKeyStroke('D'));
	    delete.setMnemonic(127);
	    
	    menuView.addSpecialControl_Menu(null, MenuSpecialType.JSeperator);

	    	
	    	menuView.addControl_Menu(new String[]{"Edit",  "Clear Grid"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
	    			int result = JOptionPane.showConfirmDialog(SgsGridModel.mainView, "Undo the deleted objects will not be possible. Do you really want to clear the Grid?", "Clear Grid?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		            if (result == JOptionPane.YES_OPTION) {	            	
		            	gridController.clearGrid();
		            }
	    		}
	    	});
	
	    menuView.addSpecialControl_Menu(null, MenuSpecialType.JSeperator);
	   
	    
	   //    final JCheckBoxMenuItem allLegendsCB , nominalPowerCB, voltageAngleCB, nominalVoltageCB;   // check MERGE
	    
	    allLegendsCB = menuView.addControl_MenuCheckBox(new String[]{"Edit", "Color Overlays", "show all"}, MenuItemType.jCheckboxMenuItem, new ActionListener(){
	    	@Override
			public void actionPerformed(ActionEvent agr0){
				actionFor_ColorOverlays(0);
			}
	    });
	    
	   nominalPowerCB =  menuView.addControl_MenuCheckBox(new String []{"Edit", "Color Overlays", "nominal power"}, MenuItemType.jCheckboxMenuItem, new ActionListener(){
      		@Override
      		public void actionPerformed(ActionEvent agr0){
      				actionFor_ColorOverlays(1);
     		}
      	});
      
      
	   voltageAngleCB =  menuView.addControl_MenuCheckBox(new String []{"Edit", "Color Overlays", "voltageangle"}, MenuItemType.jCheckboxMenuItem, new ActionListener(){
      		@Override
      		public void actionPerformed(ActionEvent agr0){
      			actionFor_ColorOverlays(2);
     		}
      	});
      	
      	
	  nominalVoltageCB = menuView.addControl_MenuCheckBox(new String []{"Edit", "Color Overlays", "nominal voltage"}, MenuItemType.jCheckboxMenuItem, new ActionListener(){
      		@Override
      		public void actionPerformed(ActionEvent agr0){ //für die nennspannung
      			actionFor_ColorOverlays(3);
      		}
      	});
	    
	    
	    
	    	
	    	menuView.addControl_Menu(new String[]{"Edit",  "Property Window"}, MenuItemType.jMenue, null);
	    	
	    	//der code hier ist redundant wie nur irgendmöglich. in einem separaten controller auslagern und sparen
	    	menuView.addControl_Menu(new String[]{"Edit","Property Window", "Open", "All"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
	    	
	    			int [][] gridData = gridController.gridModel.gridData;
	    			
	    			for(int y = 0; y<gridData.length; y++){
	    				for(int x=0; x<gridData.length; x++){
	    					if (gridData[y][x] != EMPTY.getID()) {
	    						new PropertiesDialog(gridController.gridModel, x, y);
	    					}
	    				}
	    			}
	
	    		}
	    	});
	    	
	    	menuView.addControl_Menu(new String[]{"Edit","Property Window", "Open", "Marked"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
	    			
	    			int[][][] gridData3D = gridController.gridModel.gridData3D;
	    			
	    			for (int y = 0; y < gridData3D[4].length; y++) {
	    			 	for (int x = 0; x < gridData3D[4][y].length; x++) {
	    			 		if(gridData3D[4][y][x] != 0) new PropertiesDialog(gridController.gridModel, x, y);
	    			 		
	    			 	}
	    			 }
	    			
	    			
	    		}
	    	});
	    	
	    	/**
	    	 * Refresh all Property Windows
	    	 * count open Windows
	    	 */

	    	JMenuItem refresh = menuView.addControl_Menu(new String[]{"Edit",  "Property Window", "Refresh all"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent arg0){
	    			gridController.refreshAllObjectParameterWindows();
	    			
	    		}
	    	});
	    	KeyStroke f5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
	  		refresh.setAccelerator( f5 );

	    	/**
	    	 * Close all open Property Windows
	    	 */
	  		JMenuItem close = menuView.addControl_Menu(new String[]{"Edit",  "Property Window", "Close all"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent agr0){
	    			gridController.closeAllObjectParameterWindows();
	    			
	    		}
	    	});
	       	 ;
	      	close.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0) );
	      	

	      	
	      	menuView.addControl_Menu(new String[]{"Edit","Simulation","Start Simulation"}, MenuItemType.jMenueItem, new ActionListener(){
	      		@Override
	      		public void actionPerformed(ActionEvent arg0){
//	      			ButtonController.actionFor_bStartStopSim(arg0);
	      	
//	      			ButtonController bC = new ButtonController( , gridController);
//	      			
//	      			bC.actionFor_bStartStopSim(arg0);
	      	
	      			
	      		}
	      	});
	      	
	    	/**
	    	 * Open speed Changer
	    	 */
	  		openSlider = menuView.addControl_Menu(new String[]{"Edit",  "Simulation", "open Slider"}, MenuItemType.jMenueItem, new ActionListener(){
	    		@Override
				public void actionPerformed(ActionEvent agr0){		
	    			if ( TimeThread.isRunning() && !timeThread.sliderDialog.isShowing() ){
	    			    timeThread.sliderDialog = new SpeedChanger(timeThread);
	    			    timeThread.sliderDialog.setVisible(true);		
	    			}
	    			else {
	    				return;
	    			}
	    		}
	    	});
	       	 ;
	       	 
	       	openSlider.setEnabled(timeThread.isDoRun());
//	      	close.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0) );
	      	
	    	
			
		}
	
	


	private void addMenu_Algorithm(){
		// --- Algorithm --- -----------------
    	//
    	
    	// the info window:
    	menuView.addControl_Menu(new String[]{"Algorithm", "Alogrithm Description"}, MenuItemType.jMenueItem, 
       			new ActionListener() {
    		
				@Override
			public void actionPerformed(ActionEvent arg0) {
					new AlgorithmDescriptionDialog(frame, timeThread.getAlgorithm());
				}
			});
    	
    	menuView.addSpecialControl_Menu(null, MenuSpecialType.JSeperator);		// a JSeperator
    	
    	
    	// get all sub classes: 
    	ArrayList<Class<?>> classes = ReflectionStuff.getClassesForPackage(AbstractDistributionAlgorithm.class);
    	// get implemented sub classes only:
    	classes = ReflectionStuff.getSubClasses(AbstractDistributionAlgorithm.class, classes, false);
    	 
    	menuView.startNewButtonGroup();		// Group of radio buttons
    	for(Class<?> c : classes){			// for all classes of Algorithms:
        	try {
        		// get an instance from the algorithm class. Constructor must have exactly one GridController parameter.
        		Constructor<?> constructor = c.getConstructor(GridController.class);
        		final AbstractDistributionAlgorithm instance = (AbstractDistributionAlgorithm)constructor.newInstance(gridController);
    			
        		JMenuItem item = menuView.addControl_Menu(new String[]{"Algorithm", instance.getName()}, MenuItemType.jRadioButtonMenuItem, 
    	        		new ActionListener() {

    	        			@Override
    	        			public void actionPerformed(ActionEvent arg0) {
    	        				timeThread.changeAlgorithm(instance);	// change used algorithm
    	        			}
    	        		});
        		
        		if(c == timeThread.getAlgorithm().getClass())	// select default algorithm
        			item.setSelected(true);
        		
    		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
    			e.printStackTrace();
    		}
    	}
    	menuView.finishButtonGroup();		// End group of radio buttons
    	
	}

	private void addMenu_Results(){
		//---------RESULTS-------
    	
    	menuView.addControl_Menu(new String []{"Results",  "specify Result Output"}, MenuItemType.jMenueItem, 
    			new ActionListener(){
    		
    		@Override
			public void actionPerformed(ActionEvent arg0){
    			//    			actionResultsforCSV(arg0);
//    			
//    				ResultsCSVView csvOptions = new ResultsCSVView(gridController.gridModel);
//    				csvOptions.startResultsView();
//    				csvOptions.setVisible(true);
    			
    			timeThread.csvController.initRest();
    			
//    			if(csvOptions.csvController.dataColl.cbActionListener.getFoundInconsistency() == true){
//    		    	
//    		    	String title = "Please note: ";
//    				String message = "specified parameters from objects, that no longer exists, has been deleted from your specified list."; 
//    					
//    				JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
//    					
//    		    }
    		
    		}
    	});
  
    	showResults = menuView.addControl_Menu(new String[]{"Results", "show specified Results"},  MenuItemType.jMenueItem, new ActionListener(){
    		
    		
    		
        	@Override
			public void actionPerformed(ActionEvent arg0){
        		
        		new showResultsCSVView();
          		
        	}
        });
    	
    	showResults.setEnabled(treeNotEmpty);
    	
	}
	
	/**
	 * action performance for Overlays
	 * activate all, or just the wished overlay. call init-methode in ColorOverlayController
	 * @param overlayNumber - 
	 */
	private void actionFor_ColorOverlays(int overlayNumber) {
		
		if(overlayNumber == 0){
			
			if(allLegendsCB.isSelected()){				
				if(!nominalPowerCB.isSelected()){
					nominalPowerCB.setSelected(true);
					colorOverlayController.init(1);
				}
				
				if(!voltageAngleCB.isSelected()){
					voltageAngleCB.setSelected(true);
					colorOverlayController.init(2);
				}
				
				if(!nominalVoltageCB.isSelected()){
					nominalVoltageCB.setSelected(true);
					colorOverlayController.init(3);
				}
				
			} else {
				nominalPowerCB.setSelected(false);
				voltageAngleCB.setSelected(false);
				nominalVoltageCB.setSelected(false);
				colorOverlayController.initAll();
			}			
	
		} else {
			colorOverlayController.init(overlayNumber);
			if(nominalPowerCB.isSelected() && voltageAngleCB.isSelected() && nominalVoltageCB.isSelected()){
				allLegendsCB.setSelected(true);
			} else allLegendsCB.setSelected(false);
		}
			
	}



	/** Create new save file, clear Grid **/
    private void actionFor_new() {
    	
    	String msg = "Do you really want to create a new Grid?\n " +
    				 "You will lose all your unsaved progress.";
        String title = "Clear Grid?";
    	int result = JOptionPane.showConfirmDialog(frame, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        
    	if (result == JOptionPane.YES_OPTION) {

        	File file = fileChooser.chooseFile(DialogType.New, fileFilter);

        	if (file != null) {
        		gridController.clearGrid();
        		Out.pl(ProgramConstants.defaultSimulationFile.getName());
        		fileManager.saveGridXML(file);
        	}
        }
    }
    
    /** Open a save file **/
    private void actionFor_open() {
    	
    	File file = fileChooser.chooseFile(DialogType.Open, fileFilter);

        if (file != null) {
            Out.pl(ProgramConstants.defaultSimulationFile.getName());
            gridController.clearGrid();
            fileManager.loadGridXML(file);
        }
    }

    /** Save to current file **/
    private void actionFor_save() {
        fileManager.saveGridXML(ProgramConstants.defaultSimulationFile);
    }

    /** Save to other file **/
    private void actionFor_saveAs() {
    	
    	File file = fileChooser.chooseFile(DialogType.SaveAs, fileFilter, ".zip");

        if (file != null) {
            Out.pl(ProgramConstants.defaultSimulationFile.getName());
            fileManager.saveGridXML(file);
        }
    } 
 

	public static void setTreeNotEmpty(boolean b) {
		showResults.setEnabled(b);
		
		
	}
    
}
