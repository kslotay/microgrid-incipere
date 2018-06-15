package sgs.controller.fileManagement.csvConstruction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sgs.model.SgsGridModel;
import sgs.model.gridObjects.PowerLine;
import sgs.model.gridObjects.SmartGridObject;
import sgs.model.gridObjects.Connector;
import sgs.model.simulation.Bus;
import sgs.model.simulation.Path;

/**
 * 
 * @author Sabrina huber
 *creates the checkBoxCollectionTree. add it to a tab ,and return it to the csvController.
 */
public class CollectionsTreeTab extends Application{
	
	
	private final SgsGridModel gridModel;
	
	private TreeView<String> collectionTreeView = new TreeView<String>();
	private Tab resultsTab = new Tab(); 
	
	private CheckBoxTreeItem<String> objectTreeItem = new CheckBoxTreeItem<>("");
	private final CheckBoxTreeItem<String> collectionRoot = new CheckBoxTreeItem<>("Collection");

	public static TreeItem<String> savedTree = new CheckBoxTreeItem<>();
	
	private static List<String> choosenPath = new ArrayList<String>();
	private StringBuilder header = new StringBuilder();
	public CheckBoxActionListener cbActionListener = new CheckBoxActionListener();
	
	
	/**
	 * constructor
	 * @param gridController
	 * @param gridModel
	 */
	public CollectionsTreeTab(SgsGridModel gridModel){
		this.gridModel = gridModel;
//		new GiveDataObjects(gridModel);
		
		
		
	}	
		

	/**
	 * creates the choosenPath , it helps to create the header and to organise the data
	 */
	private void createChoosenPath(){
		choosenPath.clear();
		List<TreeItem<String>> findChoosenPath = new ArrayList<TreeItem<String>>();
//		for(TreeItem<String> isSelectedNode : savedTree.getChildren()){
		for(TreeItem<String> isSelectedNode : cbActionListener.getSavedCollection().getChildren()){
			recAllLeafes(findChoosenPath, isSelectedNode);
			
		}
		

		for(TreeItem<String> node : findChoosenPath){
			StringBuilder sb = new StringBuilder();
			sb.insert(0,node.getValue().toString()+";");
			header.append(node.getValue().toString());

			while(node.getParent() != null){
				node = node.getParent();
				sb.insert(0,node.getValue().toString()+";");
				if(node.getValue().matches("BUS") || node.getValue().matches("PATH")){
					break;
				} else header.append("_" + node.getValue().toString());
			}
			header.append(';');
			choosenPath.add(sb.toString());
//			haveData = true; 
		}
		
	}


	/**
	 * called by GiveDataCollection, to create the objectPart of its Tree.
	 * first it creates the CheckBoxTreeItem for the transfered Object -> call {@code createObjectNode(SmartGridObject sgo)}
	 * second it adds the transfered SmartGridObject to the method {@code createTrueListArray()}
	 * @param sgo - transfered SmartGridObject
	 * @return objectTreeItem - for the specific transfered SmartGridObject sgo
	 */
	public CheckBoxTreeItem<String> createObject(SmartGridObject sgo){
		if(!(sgo instanceof Connector)){
			objectTreeItem = trueParam(sgo);
		}

		return objectTreeItem; 
	}
	
	/**
	 * 
	 * @return choosenPath
	 */
	public List<String> getChoosenPathList(){
		createChoosenPath(); 
		
		return choosenPath; 
	}

	public Tab getChoosenTab() {
					
			resultsTab = new Tab("Collections");
			
			TreeItem<String> choosenCollection;
//			cbActionListener.choosenCollectionTree.setExpanded(true);
			choosenCollection = cbActionListener.getSavedCollection();
			collectionTreeView = new TreeView<String>(choosenCollection);
			collectionTreeView.setEditable(false);
		
			collectionTreeView.setRoot(choosenCollection);
			collectionTreeView.setShowRoot(false);
			
			StackPane root = new StackPane();
			root.getChildren().add(collectionTreeView);
			
			resultsTab.setContent(root);
			resultsTab.setClosable(false);
			
			return resultsTab; 
		}


	/**
		 * 
		 * @return
		 */
		private CheckBoxTreeItem<String> getBusTree(){
			
			CheckBoxTreeItem<String> busNodeRoot = new CheckBoxTreeItem<>("BUS");
		
			
			LinkedList<Bus> busInGrid = gridModel.buses;
			
			
			for (Bus bus : busInGrid){
				if(busInGrid != null){
					bus.refreshValues();
					
					final CheckBoxTreeItem<String> busNode = new CheckBoxTreeItem<>(""+bus);
					
					//list all smart Grid Objects of the Bus Nr 1 -> n 
					for(SmartGridObject sgo : bus.busGridObjects){
						if(!(sgo instanceof Connector || sgo instanceof PowerLine)){
							CheckBoxTreeItem<String> objectNode = createObject(sgo);
							if(!(objectNode.getChildren().isEmpty())){
								busNode.getChildren().add(objectNode);
							}						
						}	
					}
					
					if(!(busNode.getChildren().isEmpty()))
						busNodeRoot.getChildren().add(busNode);
	
				}else {
					return null;
				}
			}
			
			if(busNodeRoot.getChildren().isEmpty()){
				return null;
			} else 	return busNodeRoot;
		}


		/**
		 * 
		 * @return header
		 */
	public StringBuilder getHeader() {
		return header;
	}

	
	
	/**
	 * 
	 * @return objectTreeItem
	 */
	public CheckBoxTreeItem<String> getobjectTreeItem(){
		
		return objectTreeItem; 
	}

	/**
		 * The Collection Node has two children. One is the BusTree, and the other is the PathTree.
		 * Careful: Algorithm tells if we haven an path or not. so if the algorithm don't need paths, there's only an empty path tree. 
		 * @return pathTree - check all paths on the grid. each object that is not a Super Conductor/Connector should be in the tree.
		 * 
		 */
		private CheckBoxTreeItem<String> getPathTree(){
			
			CheckBoxTreeItem<String> pathNodeRoot = new CheckBoxTreeItem<>("PATH");
			
			LinkedList<Path> pathInGrid = gridModel.paths;
			
			for(Path path : pathInGrid){
				if(path != null){ 
					path.refreshValues();
					CheckBoxTreeItem<String> pathNode = new CheckBoxTreeItem<>("" + path);
					
					for(SmartGridObject sgo : path.pathObjects){
						if(!(sgo instanceof Connector)){//no super conductors allowed in the view
							pathNode.getChildren().add(createObject(sgo));
						}
					}
					
					pathNodeRoot.getChildren().add(pathNode);
				
				} else {
					return null;
				}
			}
			if(pathNodeRoot.getChildren().isEmpty()){
				return null;
			} else 	return pathNodeRoot;
		}


	/**
		 * 
		 * @return resultsTab
		 */
		public Tab getResultsTab(){
			
			resultsTab = new Tab("Collections");
			
			collectionRoot.setExpanded(true);
			
			collectionTreeView = new TreeView<String>(collectionRoot);
			collectionTreeView.setEditable(true);
			
	
			collectionTreeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
			
		
			collectionRoot.getChildren().add(getBusTree()); 	//call for Busses
			collectionRoot.getChildren().add(getPathTree()); 	//call for Paths   
			 
			
			collectionTreeView.setRoot(collectionRoot);
			collectionTreeView.setShowRoot(false);
			
			StackPane root = new StackPane();
			root.getChildren().add(collectionTreeView);
			
			resultsTab.setContent(root);
			resultsTab.setClosable(false);

			cbActionListener.setCollectionTree(collectionRoot);
			
			return resultsTab; 
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
	 * @param nodes
	 * @param isSelectedNode
	 */
	private void recAllLeafes(List<TreeItem<String>> nodes, TreeItem<String> isSelectedNode){
			for(TreeItem<String> search : isSelectedNode.getChildren()){
				if(search.isLeaf()){

					nodes.add(search);
				}
				else {
					recAllLeafes(nodes, search);
				}
			}
		}
	
	
	@Override
	public void start(Stage arg0) throws Exception {
		
		
	}
	
	/**
	 * 
	 * @param newObjectTree
	 */
	public void setObjectTreeItem(CheckBoxTreeItem <String> newObjectTree){
		objectTreeItem = newObjectTree; 
	}


	/**
	 * 
	 * @param sgo
	 *
	 * @return trueListObjectNode	- a Node with node and leave 
	 */
	private CheckBoxTreeItem<String> trueParam(SmartGridObject sgo) {
		
		CheckBoxTreeItem<String> param = new CheckBoxTreeItem<>();
		CheckBoxTreeItem<String> trueListObjectNode = new CheckBoxTreeItem<>(); 
		String [] splitParameter = sgo.getParameterOfSGO().split("/");
	
		trueListObjectNode = new CheckBoxTreeItem<>(sgo.toString());
			
			for(int i = 0; i<splitParameter.length; i++){
				param = new CheckBoxTreeItem<>(splitParameter[i]);
				makeEventListener(param);
				trueListObjectNode.getChildren().add(param);				
			}

		return trueListObjectNode;
	}


}
