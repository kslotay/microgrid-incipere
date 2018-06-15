package sgs.controller.fileManagement.csvConstruction;

import java.io.File;

import javafx.scene.control.TabPane;
import sgs.model.CSVModel;
import sgs.model.SgsGridModel;
import sgs.view.ResultsCSVView;


/**
 * 
 * @author Huber Sabrina
 * its a bridge between the view and the java classes that generates and fill the TabPane with data. 
 * it also saves the the from the user specified filename and the path . 
 */
public class CSVController{
	
	public CollectionsTreeTab dataColl;
	public ObjectTreeTab dataObj;
	public CheckBoxActionListener cbActionListener = new CheckBoxActionListener();

	private File csvFile = new File("");
	public CSVModel csvModel;
	public SgsGridModel gridModel;
//	
//	private String csvFilename = "";
//	private String csvFilePath = "";
	
	/**
	 * constructor
	 * @param gridModel
	 */
	public CSVController(SgsGridModel gridModel){
		super();
		
		this.csvModel = new CSVModel(gridModel); 
		this.gridModel = gridModel;
		
	}

	
	public void initRest() {
		
		ResultsCSVView csvOptions = new ResultsCSVView(gridModel, this);
		csvOptions.startResultsView();
		csvOptions.setVisible(true);
				
		dataColl = new CollectionsTreeTab(gridModel);
		dataObj = new ObjectTreeTab(gridModel);
		
		
		
	}
	


	/**
	 * start creating the csv file
	 * @param gridModel 
	 */
	public void run() {

		dataColl = new CollectionsTreeTab(gridModel);
		csvModel.setCSVFile(csvFile);
		csvModel.run(dataColl.getChoosenPathList(), true , dataColl.getHeader());
		
	}

	
	/**
	 * 
	 * @return resultsPanes - in it are all Pane on the left Side of the window.
	 * in this we can choose what we want to save
	 */
	public TabPane getChoiceTabPane() {
		
		TabPane resultsPanes = new TabPane();
		
		resultsPanes.getTabs().addAll(dataColl.getResultsTab(), dataObj.getResultsTab());
		resultsPanes.setTabMaxHeight(Double.MAX_VALUE);
		resultsPanes.setTabMaxWidth(Double.MAX_VALUE);
	 
		return resultsPanes;
	}
	
	/**
	 * 
	 * @return trueListPanes - in it are all Panes on the right side. It shows what we have choosen.
	 */
	public TabPane getChoosenTabPane(){
		
		TabPane trueListPanes = new TabPane();

		trueListPanes.getTabs().addAll(dataColl.getChoosenTab(), dataObj.getChoosenTab());
		trueListPanes.setTabMaxHeight(Double.MAX_VALUE);
		trueListPanes.setTabMaxWidth(Double.MAX_VALUE);
		cbActionListener.setDataChanged(false);//when we create the Tree for the first time. datachanged is always true, but we want it to be false in the end

		return trueListPanes; 
	
	}
	

/**
 * set the from user defined filename and the location where the file will be saved 
 * @param filename
 * @param pathOfSaveLocation
 */
	public void setCSVSpecifications(File file) {		
		csvFile = file;
		
	}
	
	public File getCSVFile(){
		return csvFile;
	}
	
//	
//	/**
//	 * 
//	 * @return csvFilename - the filename of the csv, specified by the user
//	 */
//	public String getCSVSpecification_filename(){
//		return csvFilename;
//	}
//	
//	public void setCSVSpecifications_filename(String filename){
//		this.csvFilename = filename;
//	}
	
//	/**
//	 * 
//	 * @return csvFilePath - the FilePath
//	 */
//	public String getCSVSpecification_filepath(){
//		return csvFilePath;
//	}


	public boolean isHaveData() {
		return cbActionListener.haveDataC();
	}



}