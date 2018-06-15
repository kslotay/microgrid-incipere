package sgs.controller.fileManagement.csvConstruction;

import java.util.LinkedList;

import sgs.controller.ReflectionStuff;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.gridObjects.Connector;
import sgs.model.simulation.Bus;
import sgs.model.simulation.Path;
import sgs.model.variables.EnumPV;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.StackPane;

/**
 * 
 * @author Sabrina
 *creates the checkBoxObjectsTree. add it to a tab ,and return it to the csvController.
 */
public class ObjectTreeTab {
	
	public CheckBoxActionListener cbActionListener = new CheckBoxActionListener();
	private final SgsGridModel gridModel;; 
	private int pkgSize = ReflectionStuff.getClassesForPackage(SmartGridObject.class).size(); /**pkgSize for the maximum size of the array. we can only have so many different sgo's as we have classes in the package  */

	/**
	 * this list contains has a slot for every possible different sgo 
	 */
	private SmartGridObject [] objectArrayList = new SmartGridObject[ReflectionStuff.getClassesForPackage(SmartGridObject.class).size()];
	private EnumPV[] enums = EnumPV.values();	//array full of enums
	private String[] allParameters = new String[enums.length]; 
	/** TreeItem goes back to GiveDataCollection **/
	public CheckBoxTreeItem<String> objectTree = new CheckBoxTreeItem<>("Object");  //the object checkboxTree
	
	private TreeView<String> objectsTreeView = new TreeView<String>();

	/**
	 * constructor
	 */
	public ObjectTreeTab(SgsGridModel gridModel) {
		super();
		this.gridModel = gridModel;		
	}
	


	
	/**
	 * For each object on each position we check if we already saved it in the true List. The Collection Tree needs every single Object with position and value.
	 * For the Object Tree, first we only need every single object on the grid just once, into that object we can add all siblings. 
	 * Here we check if the object is already in the trueOnGridListArray, if it isnt we add it to the array. 
	 * @param sgo
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private CheckBoxTreeItem<String> createCheckBoxObjectTree(){
		
		CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>();
		
		LinkedList<Bus> busInGrid = gridModel.buses;
		LinkedList<Path> pathInGrid = gridModel.paths;
			
		//first check the bus
		for (Bus bus : busInGrid){
			if(busInGrid != null){
				bus.refreshValues();
				
				for(SmartGridObject sgo : bus.busGridObjects){
					if(!(sgo instanceof Connector)){

						String sgoString = sgo.toString().replaceAll("[^a-zA-Z]", "");
						
						CheckBoxTreeItem<String> checkItem = new CheckBoxTreeItem<>(); //default TreeItem to check equal the parentTreeItem 
						CheckBoxTreeItem<String> allItem = new CheckBoxTreeItem<>("all"); //this is for the option we want all objects  
						
						for(int i = 0; i<pkgSize; i++){
							if(objectArrayList[i] != null){
								String sgoStringList = objectArrayList[i].toString().replaceAll("[^a-zA-Z]", "");								
									if(sgoString.equals(sgoStringList)){
											checkItem = new CheckBoxTreeItem<>(sgo.toString());
											checkItem = paramObjectTree(sgo, checkItem); //checkItem becomes to parameter Node
											
											root.getChildren().get(i).getChildren().addAll(checkItem);
										break;
									}
							}	
							else if(objectArrayList[i] == null){
								objectArrayList[i] = sgo;
								
								allItem = paramObjectTree(sgo, allItem); 
								checkItem = new CheckBoxTreeItem<>(sgoString); 
								CheckBoxTreeItem<String> selfSgo = new CheckBoxTreeItem<>(sgo.toString());
								selfSgo = paramObjectTree(sgo, selfSgo);;
								
								checkItem.getChildren().add(allItem);
								checkItem.getChildren().add(selfSgo);
								 
								root.getChildren().add(checkItem); 
								break;
							}
						}
					}  
				
				}
					
			}else {
				return null;
			}
		}
		
		//searching in the path for specific path grid objects
		for(Path path : pathInGrid){
			if(path != null){ 
				path.refreshValues();
				new CheckBoxTreeItem<>("" + path);
				
				for(SmartGridObject sgo : path.pathObjects){
					if(!(sgo instanceof Connector)){//no super conductors allowed in the view
						String sgoString = sgo.toString().replaceAll("[^a-zA-Z]", "");
						
						CheckBoxTreeItem<String> checkItem = new CheckBoxTreeItem<>(); //default TreeItem to check equal the parentTreeItem 
						CheckBoxTreeItem<String> allItem = new CheckBoxTreeItem<>("all"); //this is for the option we want all objects
						
						for(int i = 0; i<pkgSize; i++){
							if(objectArrayList[i] != null){
								String sgoStringList = objectArrayList[i].toString().replaceAll("[^a-zA-Z]", "");								
									if(sgoString.equals(sgoStringList)){
											checkItem = new CheckBoxTreeItem<>(sgo.toString());
											checkItem = paramObjectTree(sgo, checkItem); 

											root.getChildren().get(i).getChildren().add(checkItem);
										 
										break;
									}
//								break;
							}	
							else if(objectArrayList[i] == null){
								objectArrayList[i] = sgo;
								
								allItem = paramObjectTree(sgo, allItem); 
								checkItem = new CheckBoxTreeItem<>(sgoString); 
								
								CheckBoxTreeItem<String> selfSgo = new CheckBoxTreeItem<>(sgo.toString());
								selfSgo = paramObjectTree(sgo, selfSgo);
//								
								
								checkItem.getChildren().add(allItem);
								checkItem.getChildren().add(selfSgo);
								
								root.getChildren().add(checkItem); 
								 
								break;
							}
						
					}
				}
					
			}
	
			} else {
				return null;		

			}
		}

		return root;
		
	}
	
	


	/**
	 * 
	 * @return resultsTab - in this Tab is the ObjectTree
	 */
	public Tab getResultsTab() {
		
		Tab resultsTab = new Tab();
		
		objectTree = createCheckBoxObjectTree();
		
		if(objectTree.getChildren().size() >= 2){

			objectTree.getChildren().add(0, getParameterForAll()); //Add node on position 0
		}
		
		
		objectTree.setExpanded(true);
		objectsTreeView = new TreeView<String>(objectTree);
		objectsTreeView.setEditable(true);
		
		objectsTreeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

				
		objectsTreeView.setRoot(objectTree);
		objectsTreeView.setShowRoot(false);
		
		StackPane root = new StackPane();
		root.getChildren().add(objectsTreeView);
		
		resultsTab = new Tab("Objects");		
		resultsTab.setContent(root);
		resultsTab.setClosable(false);
		
		cbActionListener.setObjectTree(objectTree);

			
		return resultsTab;
	}
	

		
	/**
	 * the Tree with selected CheckBoxTreeItems 
	 * @return resultsTab 
	 */
	public Tab getChoosenTab() {
		
		Tab resultsTab = new Tab();
		
		TreeItem<String> choosenObjectTree = cbActionListener.getSavedObject(); 
		choosenObjectTree.setExpanded(true);
		
		TreeView<String> objectTreeView = new TreeView<String>(choosenObjectTree);
			
		objectTreeView.setEditable(true);
		objectTreeView.setRoot(choosenObjectTree);
		objectTreeView.setShowRoot(false);
		
		StackPane root = new StackPane();
		root.getChildren().add(objectTreeView);
		
		resultsTab = new Tab("Objects");		
		resultsTab.setContent(root);
		resultsTab.setClosable(false);
		
	
		return resultsTab;
	}
	
	
	
	
	public TreeView<String> getCollectionTreeView(){
		
		return objectsTreeView;
	}

		/**
		 * add a listener to every node in the CheckboxTree.
		 * this listener check from the clicked node, is it a leaf, is it the last parent? 
		 * create a sub tree, and add it to the existend TreeItem in choosenObjectTree, 
		 * 
		 * @param node
		 */
	private void makeEventListener(final CheckBoxTreeItem<String> node) {

		node.addEventHandler(CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(), new EventHandler<TreeModificationEvent<String>>(){

			@Override
			public void handle(TreeModificationEvent<String> arg0) {
				cbActionListener.changeNodeInCheckBoxTree(node); 
			
			}
		});
	} 
	

	
	/**
		 * 
		 * @param sgo
		 * @param allItem
		 * @return
		 */
		private CheckBoxTreeItem<String> paramObjectTree(SmartGridObject sgo,CheckBoxTreeItem<String> allItem) {
			
			CheckBoxTreeItem<String> param = new CheckBoxTreeItem<>();
			CheckBoxTreeItem<String> trueListObjectNode  = allItem;  
			
			String [] splitParameter = sgo.getParameterOfSGO().split("/");
		
			for(int i = 0; i<splitParameter.length; i++){ //für jeden Parameter eines Objekts
				
					String splitVariable = splitParameter[i];
					
					if(splitVariable == ""){
						splitVariable = "'Dummy'-Attribute";
					}
				
					param = new CheckBoxTreeItem<>(splitVariable);
					makeEventListener(param);
					trueListObjectNode.getChildren().add(param);
														
					boolean foundNULL = false, foundParameter = false;
										
					while((foundParameter == false) && (foundNULL == false)){
						for(int j = 0; j<allParameters.length; j++){
							if(allParameters[j] != null && allParameters[j].matches(splitVariable)){
								
								allParameters[j] = splitVariable; //it s easier to overwrite it, instead of search it alwasy again
								foundParameter = true;
								break;
								
							} else if(allParameters[j] == null) {
								foundNULL = true;
								allParameters[j] = splitVariable;
								break;
							}
						}
					}			
					
			}
			return trueListObjectNode;
		}
			
		
		/**
		 * It walks trough the array allParamters, and convert every single item that is not null into a CheckBoxTreeItem
		 * and return it after that to the caller
		 * @param allParameters2 
		 * @param allNode 
		 * @return allParameters
		 */
		private CheckBoxTreeItem<String> getParameterForAll(){ 
			
			CheckBoxTreeItem<String> allNode = new CheckBoxTreeItem<> ("all Parameters from Objects");
			CheckBoxTreeItem<String> allParametersCB = new CheckBoxTreeItem<> ("all Parameters");
			makeEventListener(allParametersCB);

			for(int i = 0; i<allParameters.length; i++){
				if(allParameters[i] != null){
					CheckBoxTreeItem <String> allParametersCheckBox = new CheckBoxTreeItem<>(allParameters[i]);
					makeEventListener(allParametersCheckBox);
					allNode.getChildren().add(allParametersCheckBox);
				}
				
			}
			
			allNode.getChildren().add(0, allParametersCB);
			
			return allNode;
		}

	
}
