package sgs.view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.swing.*;

import sgs.controller.MenuController;
import sgs.controller.fileManagement.csvConstruction.CSVController;
import sgs.model.SgsGridModel;


/**
 * 
 * @author Sabrina Huber
 * The ResultsCSVView show us the Results Window
 * the class unites java swing and javafx . the constructor acts as a bridge between the two libraries.
 * every JavaFX objects has a FX in its name, to seperate it from the normal Swing Objects.
 *
 */

public class ResultsCSVView extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  CSVController csvController;
//	public boolean checkforData; 
	private boolean haveDestination = false; 
	private static String filename = ""; 
	//swing
	private JFrame resultsFrame;
	
	//FX
	private static JFXPanel resultsFXPanel = new JFXPanel();
	private Button saveFXButton = new Button();
	private Button cancelFXButton = new Button();
	private File file; 
	private VBox vBox = new VBox(); /**in it are all other hBoxes  **/
	
	
	/**
	 * constructor builds a bridge between Java Swing and JavaFX.
	 * It starts the Swing Utilities in which the FX Utilities are included as Panel
	 * @param gridModel
	 * @param gridController
	 */
	public ResultsCSVView(SgsGridModel gridModel, CSVController csvController){
		
		this.csvController = csvController;		
	}
	
	
	
	public void  startResultsView(){
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            
//                initAndShowGUI();
            
            	 // This method is invoked on the EDT thread
        	    resultsFrame = new JFrame("Select Results");
        	    
        	    
        	    SgsGridModel.mainView.setEnabled(false);
        	    resultsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        		
        		//add a Window Listener, if the frame is closing, the mainView need to be enabled 
        		resultsFrame.addWindowListener(new WindowListener(){

        			@Override
        			public void windowClosing(WindowEvent arg0) {
        				actionfor_cancel();		
        			}
        			
        			public void windowActivated(WindowEvent e) {}

        			public void windowClosed(WindowEvent e) {}

        			public void windowDeactivated(WindowEvent e) {}

        			public void windowDeiconified(WindowEvent e) {}

        			public void windowIconified(WindowEvent e) {}

        			public void windowOpened(WindowEvent e) {
        				
        				
        			}

        		});
        	    
        	    ImageIcon RAPSimIcon = new ImageIcon("./Data2/RAPSim_ICON.png");
        	    resultsFrame.setIconImage((RAPSimIcon).getImage());
        	    
        	    Platform.runLater(new Runnable() {
        	        @Override
        	        public void run() {
        	            initFX(resultsFXPanel);
        	        }
        	   });
        	    
        	    
        	    resultsFrame.add(resultsFXPanel);
        	    resultsFrame.setSize(540, 585);
        	    resultsFrame.setLocationRelativeTo(SgsGridModel.mainView);
    
        	    resultsFrame.setVisible(true);
        	    resultsFrame.setResizable(false);
        	
        	    
            	
            }
        });
//		System.out.println("JavaFX Library started");
	
	}
	

	/**
	 * actionfor_cancel look if you changed something and didn't saved it. if you did, it reminds you, that all changes will be lost.
	 * Gives you the chance to save it now. setVisible(false) no matter what you choose
	 */
	private void actionfor_cancel() {
		
		boolean savedData = csvController.dataColl.cbActionListener.isSavedChanges(); //check if we saved something
		boolean haveAnyData = csvController.dataColl.cbActionListener.isHaveData(); //check if we have data
		
		//check if we have data, that hasnt been saved yet
		if(savedData == false && haveAnyData == true){
			
			Object[] options = {"yes", "no"};
			
			String title = "Warning";
			String message = "Current changes will be lost! Do you want to save now?"; 
			
			
			int choice = JOptionPane.showOptionDialog(resultsFrame, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			
			if(choice == 0 ){
				
				actionfor_saveButton();
				
			} else if (choice == 1){ 
				csvController.dataColl.cbActionListener.actionfor_cancelButton();
//				csvController.dataColl.cbActionListener.setHaveData(false);	
				
				closeView();
			
			}
	
		} else if(savedData == true|| haveAnyData == false){
			closeView();
//			csvController.dataColl.cbActionListener.actionfor_cancelButton();
			
		}
		
	}

	/**
	 * action for the save button. 
	 * ceck if there are data from the tree and a destination where to save the file.
	 * if everything is fine, save it
	 */
	private void actionfor_saveButton() {
		
		if(csvController.dataColl.cbActionListener.isHaveData() == false){
			String title = "Error";
			String message = "Missing data for CSV-File!"; 
			
			JOptionPane.showMessageDialog(resultsFXPanel, message, title, JOptionPane.ERROR_MESSAGE);

		} else if(haveDestination == false){
			String title = "Error";
			String message = "Missing name and directory for CSV-File!"; 
			
			JOptionPane.showMessageDialog(resultsFXPanel, message, title, JOptionPane.ERROR_MESSAGE);
		}else {
			csvController.dataObj.cbActionListener.actionfor_saveButton();//same here
			closeView();
			
			
			//enable the menuitem to see the choosen Tree
			MenuController.setTreeNotEmpty(true);
		}
		
		
	
	}
	
	/**
     * it calls the methods to create the TabPanes. In them there are the trees.
     * this tabs will be sorted horizontal with the help of an horizontalBox 
     * @return hBoxTabs
     */
	private HBox addHboxTabs() {
			 
		TabPane choosenPane = new TabPane();
		TabPane choicePane = new TabPane();
		
		choicePane = csvController.getChoiceTabPane();
		choosenPane = csvController.getChoosenTabPane();

		HBox hBoxTabs = new HBox();
		
		hBoxTabs.setPadding(new Insets(10,10,10,10));
		hBoxTabs.setSpacing(10);
		hBoxTabs.getChildren().addAll(choicePane, choosenPane);
		
		return hBoxTabs;
	}
	
	
	
	/**
	 * In this horizontal Box is the File Chooser.
	 * you can decide where you and under which name you want to save the File. 
	 * @return hboxSaveFile
	 */
	private HBox addHboxSaveFile(){
		
		
		Label saveToLabel = new Label();
		saveToLabel.setText("Save to... :");
		
		final TextField saveCSVField = new TextField();
		
		
		if(filename.equals("")){
			saveCSVField.setPromptText("directory..");
			haveDestination = false;
		} else{
			saveCSVField.setPromptText(filename);
			haveDestination = true;
			resultsFrame.repaint();
		}

		saveCSVField.setPrefWidth(430);
		
		saveCSVField.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
       	   	
				
			    FileChooser fileChooser1 = new FileChooser();
			    fileChooser1.setTitle("Save CSV to...");
			   
			    if(haveDestination == true){
			    	fileChooser1.setInitialDirectory(new File(filename.substring(0, filename.lastIndexOf(File.separator)))); //directory where File is
			    } else fileChooser1.setInitialDirectory(new File("./")); //directoy workspace
			    
			    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
	            fileChooser1.getExtensionFilters().add(extFilter);
			    
			    file = fileChooser1.showSaveDialog(null);
			    
			    if(file != null){
			    	if(!file.getAbsolutePath().endsWith(".csv")){
			    	file = new File(file.getAbsolutePath()+ ".csv");
			    }
  
			    	
//			    	csvController.csvModel.setCSVFile(file);
			    	csvController.setCSVSpecifications(file);
			    filename = file.getAbsolutePath(); 
			    haveDestination = true;
			    
			    	saveCSVField.setText(filename);
			    } 
			 
            }
			    
    		
		});

		HBox hboxSaveFile = new HBox();
		hboxSaveFile.setPadding(new Insets(10,10,10,10));
		hboxSaveFile.setSpacing(20);
		hboxSaveFile.getChildren().addAll(saveToLabel, saveCSVField);
		
		return hboxSaveFile;
	}
	
//	
//	private TextField checkFileName(TextField saveCSVField) {
//		if(filename.equals("")){
//			saveCSVField.setPromptText("directory..");
//			haveDestination = false;
//		} else{
////			saveCSVField.setText(filename);
//			saveCSVField.setPromptText(filename);
//			haveDestination = true;
//			
//			 
//		}
//		return saveCSVField;
//	}


	/**
	 * specifies the buttons cancel and save
	 * @return hboxButtons - horizontal sorted Buttons
	 */
	private HBox addHboxButtons(){
		
		
		cancelFXButton.setPrefSize(100, 20);
		cancelFXButton.setText("cancel");
		cancelFXButton.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e){
				actionfor_cancel();
			}
			
		});
			
		saveFXButton.setPrefSize(100,20);		
		saveFXButton.setText("save");
		saveFXButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				actionfor_saveButton();
			}
		});
		HBox hboxButtons = new HBox();
		
		hboxButtons.setPadding(new Insets(10,10,10,10)); //padding(Abstand nach oben, 
		hboxButtons.setSpacing(300);
		
//		hboxButtons.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE); //das PrefSize führt dazu, dass die HBox mehr Platz verbraucht und somit über den gültigen Framerahmen des FX rutscht
		hboxButtons.getChildren().addAll(cancelFXButton, saveFXButton); 
	
		
		return hboxButtons;
	}
	
	
	/**
	 * 
	 * @param hbox1
	 * @param hbox2
	 * @param hbox3
	 * @return vBox - Tabs, saveFile and Buttons are on  one vertical line
	 */
	private VBox addVBox(HBox hbox1, HBox hbox2, HBox hbox3){
		
		vBox.setFillWidth(true);
		VBox.setVgrow(hbox1, Priority.ALWAYS);
//		vBox.setStyle("-fx-background-color: #F2F2F2;");
		
		vBox.getChildren().addAll(hbox1, hbox2, hbox3);
    		
		return vBox;
		
	}
	




	
	/**close ResultsCSVView Frame and enable he mainView from SgsGridModel 
	 * 
	 */
	private void closeView(){
			
			SgsGridModel.mainView.setEnabled(true);
			resultsFrame.setVisible(false);
		}


	/**
	 * create scene, organise the content in the view
	 * @return the resultsScene
	 */
	private  Scene createScene() {
		
		Group rootGroup = new Group();
		 
		
		HBox hboxTabs = addHboxTabs();
		HBox hboxSaveFile = addHboxSaveFile();
		HBox hboxButtons = addHboxButtons(); //TODO Buttons sind für IlleagalArgumentException verantwortlich. wahrscheinlich sind sie zu weit auseinander
		vBox = addVBox(hboxTabs, hboxSaveFile, hboxButtons);
		
		rootGroup.getChildren().add(vBox);
//		
//		rootGroup.getChildren().add(hboxButtons);
		
		Scene resultsScene = new Scene(rootGroup);
		this.repaint();
		
	    return (resultsScene);
	}

	
	/**
	 * add the scene into the fxPanel
	 * @param fxPanel
	 */
	private void initFX(JFXPanel fxPanel) {	
			
	    // This method is invoked on the JavaFX thread
	    Scene scene = createScene();	   
	    fxPanel.setScene(scene);
	    
	}

//	/**
//	 * initialize the Swing Frame and add to it the JavaFX Panel resultsFXPanel
//	 */
//	private void initAndShowGUI() {
//	   
//	    
//	  
//	}

	/**
     * set the resultsFrame visible true or false
     * @param b
     */
	public void setVisible(boolean b) {
//		this.isVisible();
	}

    

}
