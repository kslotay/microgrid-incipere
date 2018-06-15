package sgs.view;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import sgs.controller.fileManagement.csvConstruction.CheckBoxActionListener;
import sgs.model.SgsGridModel;

/**
 * called by the MenuController, when the user wants to know the parameters of the objects to save during the simulation, that he has choosen.
 * @author Sabrina Huber
 *
 */
public class showResultsCSVView{
	
	private static JFXPanel resultsFXPanel = new JFXPanel();
	/**
	 * constructor
	 */
	public showResultsCSVView() {
		
	
//		closeAllWindows();
//		createGUI();
		
//		addWindow(this); //TODO das muss noch hinzugefügt werden, aber keine ahnung warum er das nicht als windows erkennt.
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	
            	JDialog resultsDialog = new JDialog();
            	resultsDialog.setTitle("Show specified Data for CSV File");
//            	resultsDialog.pack();
            	resultsDialog.setSize(370,520);
            	resultsDialog.dispose();
        	    ImageIcon RAPSimIcon = new ImageIcon("./Data2/RAPSim_ICON.png");
        	    resultsDialog.setIconImage((RAPSimIcon).getImage());
        	    
  				resultsDialog.add(resultsFXPanel);

        	    resultsDialog.setLocationRelativeTo(SgsGridModel.mainView);
        	    
        	    resultsDialog.setVisible(true);
        	    resultsDialog.setResizable(true);
        	    
        	    Platform.runLater(new Runnable() {
        	        @Override
        	        public void run() {
        	            initFX(resultsFXPanel);
        	        }
        	   });
        	    
        	
        	    
            }
        });
		
	}


//	/**
//	 * creates the GUI 
//	 * creates an java swing frame, set the title and the size of it.
//	 * it also initialize the fx libary to create the resultsFXPanel 
//	 */
//	public void createGUI(){	
//	}
	
	/**
	 * Initialize the scene, get the data from the function createScene() and add the result to the fxPanel. 
	 * @param fxPanel
	 */
	private void initFX(JFXPanel fxPanel) {	
	
	    Scene scene = createScene();
	    fxPanel.setScene(scene);
	    
	}
	
	
	/**
	 * create the fx scene. 
	 * into a group of the scene, the tree will be added 
	 * @return the resultsScene
	 */
	private  Scene createScene() {
		
		Group rootGroup = new Group();
		 	
		VBox vBox = new VBox();
		
		vBox.setPadding(new Insets(10,10,10,10));
		vBox.setSpacing(10);
			
		Text expl = new Text("This Tree shows the specified objects and parameters,\n that will be saved in the csv file when you start the simulation.");
		expl.autosize();

		TreeItem<String> tree = CheckBoxActionListener.getResultsCollection();
		recExpandTree(tree);
		
		TreeView<String> view = new TreeView<>(tree);
		
		vBox.getChildren().addAll(expl, view);
		
		rootGroup.getChildren().add(vBox);
		
		Scene resultsScene = new Scene(rootGroup);
		
	    return (resultsScene);
	   
	}

	/**
	 * expands the tree
	 * @param tree
	 */
	private void recExpandTree(TreeItem<String> tree) {
		
		for(TreeItem<String> node : tree.getChildren()){
			if(!(node.isLeaf())){
				node.setExpanded(true);
				recExpandTree(node);
			}  
		} 
	}
	


}
