package sgs.controller.fileManagement.csvConstruction;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem; 
/**
 * 
 * @author Sabrina Huber
 * the actionListener for the checkBoxActions in the Tab.
 * when a checkBox is selected or unselected, the actionlistener will add the node into the correct choosenTree
 * it also clicks onto the equivalent object in the other tree.
 * check if the number of objects on the grid, is the same number in our trees
 */
public class CheckBoxActionListener {
	
	
//	public static boolean created = false;
//	public static boolean dataChanged = false;
//	private boolean foundParameter = false;
	private static boolean haveData ;
	private static boolean savedChanges = false;
	private static boolean dataChanged;
	private boolean foundInconsistency; 
	private boolean siblingChangesAllNode = false; //change one of the siblings on purpose
	
	//Trees must be static, or they lose there data when you change the tab
	private static TreeItem <String> choosenObjectTree = new TreeItem<>("Objects"); //our TrueList Tree
	private static TreeItem <String> choosenCollectionTree = new TreeItem<>("Collections");
	
	
	//if these trees are not static, they interact with each other
	private static CheckBoxTreeItem<String> collectionTree = new CheckBoxTreeItem<>("Collection");
	private static CheckBoxTreeItem<String> objectTree = new CheckBoxTreeItem<>("Object");  //the object checkboxTree
	
	private TreeItem<String> savedTree = new TreeItem<>("Collections");
	
	
	/**
	 * set the choosenCollectionTree back, to the old values, that are stored in the savedTree.
	 * set haveData back to false.
	 */
	public void actionfor_cancelButton() {
		
		choosenCollectionTree = savedTree;
		
//		haveData = false;
		savedChanges = false;
		setDataChanged(false);
		
	}

	
	/**
	 * actions for save Button
	 * set savedChanges to true
	 * set dataChanged to false
	 * set haveData to false
	 */
	public void actionfor_saveButton(){		
		savedChanges = true; 
		setDataChanged(false);
//		haveData = true;
		
		
	}


	/**
			 * add a TreeItem to the ChoosenObjectTree
			 * @param node
			 */
		public void addNodeChoosenObjectTree(CheckBoxTreeItem<String> node){
				
				String childNodeString = node.getValue().toString();//kind
				String parentNodeString = node.getParent().getValue().toString(); //mutter 
				String rootNodeString = node.getParent().getParent().getValue(); //groﬂmutter
				
				TreeItem <String> childNode = new TreeItem<>(childNodeString);
				TreeItem <String> parentNode = new TreeItem<>(); 
				TreeItem <String> rootNode = new TreeItem<>(); //the parent of the sgo, the capital (sgo without coordinates)
				
				boolean foundParent = false; 
				boolean foundRoot = false; 
				savedChanges = false;
//				dataChanged = true;
				
					if(choosenObjectTree.getChildren().isEmpty()){
						
						parentNode = new TreeItem<>(parentNodeString);
						parentNode.getChildren().add(childNode);
						
						rootNode = new TreeItem<>(rootNodeString);
						rootNode.getChildren().add(parentNode);
						
						choosenObjectTree.getChildren().add(rootNode);
						
								
						
						
					} else {
						
						for(TreeItem<String> findRootNode : choosenObjectTree.getChildren()){
							foundRoot = false; 
							if(findRootNode.getValue().contains(rootNodeString)){
								foundRoot = true;
								for(TreeItem<String> findParentNode : findRootNode.getChildren()){
									foundParent = false; 
									if(findParentNode.getValue().contentEquals(parentNodeString)){
										if(findParentNode.getChildren().isEmpty()){
											findParentNode.getChildren().add(childNode);
										} else {
											boolean foundParameter = false; 
											for(TreeItem<String> findParameter : findParentNode.getChildren()){
												if(findParameter.getValue().contentEquals(childNodeString)){
													foundParameter = true;
													break;
												}
											} if(foundParameter == false){
												findParentNode.getChildren().add(childNode);
											}
										}
										
										foundParent = true;
										break; 
									}
									
								}
								if(foundParent == false){
									parentNode = new TreeItem<>(parentNodeString);
									parentNode.getChildren().add(childNode);
									findRootNode.getChildren().add(parentNode);
								}
							} else { //not found rootNode. make new one
								
							}
						}
						if(foundRoot == false){
							parentNode = new TreeItem<>(parentNodeString);
							parentNode.getChildren().add(childNode);
							rootNode = new TreeItem<>(rootNodeString);
							rootNode.getChildren().add(parentNode);
							choosenObjectTree.getChildren().add(rootNode);
						}

					}
					
				
					
				}

		/**
		 * add a node to a TreeItem given in the param list from Collections
		 * @param node - node from the CheckBoxTree that trows the ActionListener
		 * @param list - in which Tree (without CeckBoxes) we want to add a node
		 * @param node2 - a node(should be leaf) from the choosenCollectionTree
		 */
		public void addNodeChoosenCollectionTree(CheckBoxTreeItem<String> node, TreeItem<String> list, TreeItem<String> node2){
			
			String parameterNodeString;
			String sgoNodeString;
			String busPathNodeString;
			String headerNodeString;
			
			if(node2 == null){
				parameterNodeString = node.getValue().toString();
				sgoNodeString = node.getParent().getValue().toString();
				busPathNodeString = node.getParent().getParent().getValue().toString();
				headerNodeString = node.getParent().getParent().getParent().getValue().toString();
			} else{
				parameterNodeString = node2.getValue().toString();
				sgoNodeString = node2.getParent().getValue().toString();
				busPathNodeString = node2.getParent().getParent().getValue().toString();
				headerNodeString = node2.getParent().getParent().getParent().getValue().toString();
			}
						
				//TreeItem for an empty choosenColletionTree
				TreeItem <String> parameterNode = new TreeItem<>(parameterNodeString);
				TreeItem <String> sgoNode = new TreeItem<>(); 
				TreeItem <String> busPathNode = new TreeItem<>(); 
				TreeItem <String> headerNode = new TreeItem<>();
				
	//			choosenObjectTree.getChildren().add(childNode);
				boolean foundBusPathNode = false; 
				boolean foundHeaderNode = false; 
				boolean foundSgoNode = false;
				
				if(list.getChildren().isEmpty()){
					
					sgoNode = new TreeItem<>(sgoNodeString);
					sgoNode.getChildren().add(parameterNode);
					
					busPathNode = new TreeItem<>(busPathNodeString);
					busPathNode.getChildren().add(sgoNode);
					
					headerNode = new TreeItem<>(headerNodeString);
					headerNode.getChildren().add(busPathNode);
					
					list.getChildren().add(headerNode);
//					if(node2 == null) haveData = true; 
					
				} else{ 
					for(TreeItem<String> searchHeaderNode : list.getChildren()){ 
						if(searchHeaderNode.getValue().contentEquals(headerNodeString)){
							foundHeaderNode = true;
							for(TreeItem<String> searchBusPathNumber : searchHeaderNode.getChildren()){
								if(searchBusPathNumber.getValue().contentEquals(busPathNodeString)){
									foundBusPathNode = true;
									for(TreeItem<String> searchSgoNode : searchBusPathNumber.getChildren()){
										if(searchSgoNode.getValue().contentEquals(sgoNodeString)){
											foundSgoNode = true;
											if(searchSgoNode.getChildren().isEmpty()){
												searchSgoNode.getChildren().add(parameterNode);
											} else {
												boolean foundParameter = false; 
												for(TreeItem<String> findParameter : searchSgoNode.getChildren()){
													if(findParameter.getValue().contentEquals(parameterNodeString)){
														foundParameter = true;
														break;
													}
												} if(foundParameter == false){
													searchSgoNode.getChildren().add(parameterNode);
												}
											}
											
	//										foundSgoNode = true;
	//										searchSgoNode.getChildren().add(parameterNode);
											break;
										}
									}if(foundSgoNode == false){
										sgoNode = new TreeItem<>(sgoNodeString);
										sgoNode.getChildren().add(parameterNode);
										searchBusPathNumber.getChildren().add(sgoNode);
										
									}
								}
							}if(foundBusPathNode == false){
								sgoNode = new TreeItem<>(sgoNodeString);
								sgoNode.getChildren().add(parameterNode);
								
								busPathNode = new TreeItem<>(busPathNodeString);
								busPathNode.getChildren().add(sgoNode);
								
								searchHeaderNode.getChildren().add(busPathNode);
							}
							
						}
					}if(foundHeaderNode == false){
						sgoNode = new TreeItem<>(sgoNodeString);
						sgoNode.getChildren().add(parameterNode);
						
						busPathNode = new TreeItem<>(busPathNodeString);
						busPathNode.getChildren().add(sgoNode);
						
						headerNode = new TreeItem<>(headerNodeString);
						headerNode.getChildren().add(busPathNode);
						
						list.getChildren().add(headerNode);
//						if(node2 == null) haveData = true;
					}
					
				}
//				haveData = true;
				savedChanges = false;
//				dataChanged = true;
				}

//	/**
//		 * 
//		 */
//		private void changeNode(CheckBoxTreeItem<String> node, TreeItem<String> searchedNode, TreeItem<String> conditionNode){ 
//			if(searchedNode != null && conditionNode == null){
//				TreeItem<String> parent = searchedNode.getParent();
//				if(node.isSelected()) parent.getChildren().add(searchedNode);
//				else if(!(node.isSelected())) parent.getChildren().remove(searchedNode);
//			} else if(searchedNode != null && conditionNode != null){
//				TreeItem<String> parent = conditionNode;
//				if(node.isSelected()) parent.getChildren().add(searchedNode);
//				else if(!(node.isSelected())) parent.getChildren().remove(searchedNode);
//			}
//				
//		}

//	/**
//	 * 	
//	 * @param list
//	 * @param node
//	 */
//	private void changeNodeInChoosenTree(TreeItem<String> list, CheckBoxTreeItem<String> node){ 
//		
//		TreeItem<String> parameter = new TreeItem<>(node.getValue().toString());
//		TreeItem<String> mother = new TreeItem<>();
//		
//		if(list.getChildren().isEmpty()){
//			while(node.getParent() != null){
//				try{
//					mother = new TreeItem<>(node.getParent().getValue().toString());
//					
//					mother.getChildren().add(parameter);
//					
//					parameter = mother;
//	
//				} catch (Exception e){}
//				
//				node = (CheckBoxTreeItem<String>) node.getParent();
//			
//			}
//			
//			list.getChildren().add(mother);
//			
//		} else {
//			recSearch(list, node);		
//		}
//		
//
//		haveData = true;
//		savedChanges = false;
//		dataChanged = true;
//	}
		
		
	/**
	 * one methode for adding and removing nodes from the choosenDataTrees.
	 * At first check if the node.getParent() is an all-node or a sgo-node.
	 * If it's an all-node, the node can only be in the objectTab, because only there are the nodes with the Titel "all .." .
	 * 
	 * @param node - the node from the Tree that throws the actionlistener
	 */
	public void changeNodeInCheckBoxTree(CheckBoxTreeItem<String>node){
		
		
		if(node.getParent().getValue().contains("all")){
			if(node.getValue().contentEquals("all Parameters")){//found Node all Parameters in Tree Object
				selectNode(node,objectTree);
				
			} else if(node.getParent().getValue().contentEquals("all Parameters from Objects")){//Parent is an all-node but given node itself not. so search for everything das is similar to me
				
				recSearch(objectTree, node, null);
				recSearch(collectionTree, node, null);
				
			} else if(node.getParent().getValue().contentEquals("all")){ 
				
				CheckBoxTreeItem<String> allNodeParent = (CheckBoxTreeItem<String>) node.getParent().getParent();
				
				if(siblingChangesAllNode == false){ //if we changed it with a sibling, we dont want that it deactivated or activated the others
					recSearch(allNodeParent, node, null);
					recSearch(collectionTree, node, (CheckBoxTreeItem<String>) node.getParent().getParent()); //here condition, because we only want the same objects in the other tree, an not only the similar parameters
					
				}
				siblingChangesAllNode = false;
			
			}
		} else {
			//parent is not a all-Node neither is itself a all-node. still parent could be a smartGridObject in ObjectsTree or in CollectionTree
			boolean checkForCollectionTree = checkList(node);
			
			if(collectionTree.isSelected() == true){
				objectTree.setSelected(true);
			} else {//node is in collections or objects tree
				if(checkForCollectionTree == true){
					if(node.isSelected() == true) addNodeChoosenCollectionTree(node, choosenCollectionTree, null);
					else removeNodeChoosenCollectionTree(node, choosenCollectionTree,null);
					recSearch(objectTree, node, (CheckBoxTreeItem<String>) node.getParent());
					
				
				} else {
					if(node.isSelected() == true) addNodeChoosenObjectTree(node);
					else removeNodeChoosenObjectTree(node, objectTree);
					recSearch(collectionTree,node,(CheckBoxTreeItem<String>)node.getParent());
//					changeNodeInChoosenTree(choosenObjectTree,node);
					checkSiblings(node); 
					
				}			
			}
	
		}
		
		recExpandTree(choosenObjectTree);
		recExpandTree(choosenCollectionTree);
		setDataChanged(true); 
		}
	
	/**
	 * the methode looks which Tree is the mother of the selected node, to avoid activating CheckBoxes and creating choosenTrees twice
	 * @return collectionTreeIsMother - is node a part of collectionTree
	 */
	private boolean checkList(CheckBoxTreeItem<String> node) { 		
		boolean collectionTreeIsMother = false;
		
		while(node.getParent() != null){
			node = (CheckBoxTreeItem<String>) node.getParent();
		}
		
		try{
			if(node.getValue().contains(collectionTree.getValue())) collectionTreeIsMother = true;
		}catch (Exception e){}
	
		return collectionTreeIsMother;
	}

	/**
	 * this method checks if the siblings of a parent are also isSelected(true); if yes, then it will set the all-node also isSelected(true);
	 * other way around for isSelected(false) also
	 * @param node
	 */
	private void checkSiblings(CheckBoxTreeItem<String> node){
		
		CheckBoxTreeItem<String> grandparent = (CheckBoxTreeItem<String>) node.getParent().getParent();
		CheckBoxTreeItem<String> allNode = new CheckBoxTreeItem<String>("all");
		
		boolean allNodeFalse = false;
		int setTrue = 0;
		
		for(TreeItem<String> mother : grandparent.getChildren()){
			if(!(mother.getValue().contentEquals("all"))){ //mother not supposed to be the all node - special case for that see changeNodeInCheckBoxTree
				for(TreeItem<String>child : mother.getChildren()){
					if(child.getValue().contentEquals(node.getValue()) && ((CheckBoxTreeItem<String>)child).isSelected() == node.isSelected()){ //check if found the node, if it has the same value and is activated the same way
						if(node.isSelected() == true){
							setTrue++;
						} else if(node.isSelected() == false){  
							recSearch(grandparent, node, allNode);
						}
					} else {
					}
				}
			} else {
				allNodeFalse = true;
			}
		}
		
		
		if(true && grandparent.getChildren().size() == setTrue+1){ //+1 because we cant count the all-node
			recSearch(grandparent, node, allNode);
			siblingChangesAllNode = true;
		} else if(allNodeFalse == true){
			siblingChangesAllNode = false;
		}
	}
	

	/**
	 * 
	 * @return foundInconsitency - boolean to tell if the user wanted to save data from an object, that he/she delayed later from the grid
	 */
	public boolean getFoundInconsistency() {
		
		return foundInconsistency;
	}


	/**
	 * @return the choosenCollectionTree;
	 */
	public static TreeItem<String> getResultsCollection() {
			
		return choosenCollectionTree;
	}

	/**
	 * 
	 * @return choosenObjectTree
	 */
	public TreeItem<String> getSavedObject() {
		
		return choosenObjectTree;
	}

	
	/**
	 * clear the choosenObjectTree
	 * call recAllLeafes function to search for the leaves in the choosenCollectionTree, and save them in the savedTreeCollection
	 * call recExpandTree function to expand each node of the tree
	 * @return choosenCollectionTree
	 */
	public TreeItem<String> getSavedCollection() {
		
		choosenObjectTree.getChildren().clear();
		
		recAllLeafes(savedTree, choosenCollectionTree);
//		haveData = false; //we dont have new datas. thats just old data
		
		recExpandTree(choosenCollectionTree);

		return choosenCollectionTree;
	}

	
	/**
	 * 
	 * @return haveData - boolean to check if a notification to save the changes is needed
	 * 
	 */
	public boolean isHaveData() {

		if((choosenCollectionTree.getChildren().isEmpty() && dataChanged == true) || dataChanged == true ){
			haveData = true;
		} else haveData = false;
		
		return haveData;
	}
	
	/**
	 * this is a getter - Method for the Controller. 
	 * in some possibilities the JavaFx has not initialized and would throw an exception
	 * 
	 * @return
	 */
	public boolean haveDataC(){
		return haveData;
	}
	
	
	/**
	 * 
	 * @return savedChanges - 
	 */
	public boolean isSavedChanges() {
			
		return savedChanges;
	}

		
		

	/**
	 * the recAllLeaves in the GiveDataCollections, put all leafs into a list, we want here to put it into a new tree. 
	 * also if it found the leaf, it should check if the path even exists in the checkboxtree Collection and set the isSelected to true.
	 */
	public void recAllLeafes(TreeItem<String> list, TreeItem<String> choosenTree){
		
//		boolean foundCollectionLeaf = false;
		foundInconsistency = true;
		
		for(TreeItem<String> search : choosenTree.getChildren()){
			if(search.isLeaf()){

				recSearchLeafeInCB(search, collectionTree);
				
				if(foundInconsistency == false) {
					addNodeChoosenCollectionTree(null, list, search);
				} else if (foundInconsistency == true){
//					removeNodeChoosenCollectionTree(null, list, search);
				}
			}
			else {				
				recAllLeafes(list, search);				
			}
		}	
	}

	
	
	/**
	 * when the user deleted one object, but has choosen paramaters from it, to save it during the simulation, its action creates an inconsistency
	 * we cant save anything, that isnt there anymore.
	 * the choosenTrees will always be new generated by each start of the frame. 
	 * so we have to check if there are to many objects in the savedTree, than in the tree from the grid
	 * this function searchs rec for inconsistency
	 * @param leaf - from the savedTree
	 * @param list - the new generated choosenTree
	 */
	private void recSearchLeafeInCB(TreeItem<String> leaf, CheckBoxTreeItem<String> list){ 
		
		for(TreeItem<String> search : list.getChildren()){
			if(search != null && search.isLeaf() && search.getValue().equals(leaf.getValue()) && search.getParent().getValue().equals(leaf.getParent().getValue())){ 
				((CheckBoxTreeItem<String>) search).setSelected(true);				
				foundInconsistency = false;
				
			}else if(search != null) {
					
					recSearchLeafeInCB(leaf, (CheckBoxTreeItem<String>) search);	
					
			}
		
		}		
	
	}

	/**
	 * recSearch search recursive for a spacific searchedNode in a transfered list (mostly the collectionTree or objectTree).
	 * Sometimes we want an extra conditionNode (like search all parameters where the parent of this node is a house)
	 * @param list - a checkBoxTreeItem node, through which the algorithm is searching for a specific node
	 * @param searchedNode - is the node we are searching. at the start, the selected node which throws the actionlistener
	 * @param conditionNode - extras we want, like that the parent is a specific node.
	 */
	private void recSearch(CheckBoxTreeItem<String> list, CheckBoxTreeItem<String> searchedNode, CheckBoxTreeItem<String> conditionNode){
		
		for(TreeItem<String> node : list.getChildren()){
			if(node != null && conditionNode == null && node.getValue().contentEquals(searchedNode.getValue())){
				selectNode(searchedNode, (CheckBoxTreeItem<String>)node);
			} else if (node != null && conditionNode != null && node.getParent().getValue() != null && node.getParent().getValue().contentEquals(conditionNode.getValue()) && node.getValue().contentEquals(searchedNode.getValue())){
				selectNode(searchedNode, (CheckBoxTreeItem<String>)node);	
			} else if(node!= null) recSearch((CheckBoxTreeItem<String>)node, searchedNode, conditionNode);
		}
			
	}
	
	
//	/**
//	 * @param list - TreeItem the right trees from the tab on the right side from the resultswindow
//	 * @param node - the node which activated the actionlistener
//	 */
//	private void recSearch(TreeItem<String> list, CheckBoxTreeItem<String> node){
//		
//		for(TreeItem<String> searchedNode : list.getChildren()){
//			if(searchedNode.getValue().contentEquals(node.getParent().getValue())){
//				TreeItem <String> par = new TreeItem<String>();
//				for(TreeItem<String> parameter : searchedNode.getChildren()){
//					if(searchedNode.getValue().contentEquals(node.getValue())){
//						foundParameter = true;
//						par = parameter;
//					} else {
//						foundParameter = false;
////						par = parameter;
//					}
//				}
//				if(foundParameter == false){ //node is not existing , so we must create a new one in the searchedNode parent
//					changeNode(node,node,searchedNode);
//				} else if(foundParameter == true && node.isSelected() == true){
//					changeNode(node,par, null);
//				}
//				
//			} else if(list.getChildren().isEmpty()) { 
////				System.out.println("leer");
//				
//			} else recSearch(searchedNode, node);
//		}
//	}

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

	/**
	 * remove nodes and the parents if necessary from the choosenObjectTree
	 * @param node - selected Node throws the action listener
	 * @param objectTree -  
	 */
	public void removeNodeChoosenObjectTree(CheckBoxTreeItem<String> node, CheckBoxTreeItem<String> objectTree){

		String childNodeString = node.getValue().toString();
		String parentNodeString = node.getParent().getValue().toString();
		String rootNodeString = node.getParent().getParent().getValue(); 
			
		//if the node was an all node, it checks the parent that should be the root, that will automatically activates all others
		if(node.getParent().getValue().contains("all")|| node.getValue().contains("all")){ 
					
					CheckBoxTreeItem <String> parentCheckBoxNode = (CheckBoxTreeItem<String>) node.getParent().getParent();
					
					parentCheckBoxNode.setSelected(false);
									
		} else if(node.getParent().getValue().equals(objectTree.getChildren().toString())){
			CheckBoxTreeItem <String> parentCheckBoxNode = (CheckBoxTreeItem<String>) node.getParent();
			parentCheckBoxNode.setSelected(false);
			
		} else {
			
			for(TreeItem<String> findRootNode : choosenObjectTree.getChildren()){
				if(findRootNode.getValue().contentEquals(rootNodeString)){//found root 
					for(TreeItem<String> findParentNode : findRootNode.getChildren()){
						if(findParentNode.getValue().contentEquals(parentNodeString)){//found specific sgo 
							for(TreeItem<String> findChildNode : findParentNode.getChildren()){
								if(findChildNode.getValue().contentEquals(childNodeString)){
									findParentNode.getChildren().remove(findChildNode); //delete the parameter
									break;
								}	
							}
								
							if(findParentNode.getChildren().isEmpty()){
								findRootNode.getChildren().remove(findParentNode);
								
								break;
							}
						}
					}
					if(findRootNode.getChildren().isEmpty()){
						choosenObjectTree.getChildren().remove(findRootNode);
						break;
					}
				}
			}
		
		}
			
	}
	
	
	
	/**
		 * remove a node + parents if necessary from the choosenCollectionTree
		 * @param node
		 */
		public void removeNodeChoosenCollectionTree(CheckBoxTreeItem<String> node, TreeItem<String> list, TreeItem<String> node2) {
			
			
			String parameterNodeString;
			String sgoNodeString;
			String busPathNodeString;
			String headerNodeString;
			
			if(node2 == null){
				parameterNodeString = node.getValue().toString();
				sgoNodeString = node.getParent().getValue().toString();
				busPathNodeString = node.getParent().getParent().getValue().toString();
				headerNodeString = node.getParent().getParent().getParent().getValue().toString();
			} else{
				parameterNodeString = node2.getValue().toString();
				sgoNodeString = node2.getParent().getValue().toString();
				busPathNodeString = node2.getParent().getParent().getValue().toString();
				headerNodeString = node2.getParent().getParent().getParent().getValue().toString();
			}
			
			
			for(TreeItem<String> findHeaderNode : choosenCollectionTree.getChildren()){
				if(findHeaderNode.getValue().contentEquals(headerNodeString)){
					for(TreeItem <String> findBusPathNumber : findHeaderNode.getChildren()){
						if(findBusPathNumber.getValue().contentEquals(busPathNodeString)){
							for(TreeItem<String> findSgoNode : findBusPathNumber.getChildren()){
								if(findSgoNode.getValue().contentEquals(sgoNodeString)){
									for(TreeItem<String> findParameterNode : findSgoNode.getChildren()){
										if(findParameterNode.getValue().contentEquals(parameterNodeString)){
											findSgoNode.getChildren().remove(findParameterNode);
//											haveData = true;
											break;
										}
									}
								}
								if(findSgoNode.getChildren().isEmpty()){
									findBusPathNumber.getChildren().remove(findSgoNode);
									break;
								}
							}
						}
						if(findBusPathNumber.getChildren().isEmpty()){
							findHeaderNode.getChildren().remove(findBusPathNumber);
							break;
						}
					}
				}
				
				if(findHeaderNode.getChildren().isEmpty()){
					choosenCollectionTree.getChildren().remove(findHeaderNode);
					if(choosenCollectionTree.getChildren().isEmpty()){
	//					choosenPath.clear(); //clear the List, because there is nothing in it. 
//						haveData = false; 	//Liste ist leer, keine Daten mehr da
					}
						
					break;
				}
			}
		
		}
	
	/**
	 * 
	 * @param collTree
	 */
	public void setCollectionTree (CheckBoxTreeItem<String> collTree){
		collectionTree = collTree;
	}

	
	
	/**
	 * Methode checks how the node is set (isSelected, isNotSelected), and than we set the other node also that way
	 * @param node - which throws the action listener
	 * @param setNode - node which we want be selected or unselected
	 */
	private void selectNode(CheckBoxTreeItem<String> node, CheckBoxTreeItem<String> setNode){ 
		if(node.isSelected() && node.isLeaf()){ 
			setNode.setSelected(true);		
		}else if(!(node.isSelected())){
			setNode.setSelected(false);
			
		}
	}


	/**
	 * set the objTree into the objectTree, and so the actionListener to the address of the tree
	 * @param objTree
	 */
	public void setObjectTree(CheckBoxTreeItem<String> objTree){
		objectTree = objTree;
	}


	public void setDataChanged(boolean b) {
		dataChanged = b;
		
	}

	
	
	
	
}
