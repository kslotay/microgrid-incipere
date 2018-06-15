package sgs.model.gridData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import sgs.model.gridObjects.SmartGridObject;

public class GridDataTree {

	/** Class --> ObjectEnum, Enum to Class mapping is available directly in Enum **/
	public final static HashMap<Class<? extends SmartGridObject>,GridDataEnum> classToEnumMap = new HashMap<Class<? extends SmartGridObject>,GridDataEnum>();
	/** Class --> GridDataNode **/
	public final static HashMap<Class<? extends SmartGridObject>,GridDataNode> classToNodeMap = new HashMap<Class<? extends SmartGridObject>,GridDataNode>();
	
	
	/**
	 * Initialize the static grid data tree.
	 * @param classes - all relevant or more classes.
	 */
	@SuppressWarnings("unchecked")
	public static void INIT_TREE(ArrayList<Class<?>> classes){
		
		// --- add all Enums with Classes to class to enum mapping ---
		//
		GridDataEnum[] enums = GridDataEnum.values();
		for(GridDataEnum enu : enums){
			if(enu.getMappedClass()!=null)
				GridDataTree.classToEnumMap.put(enu.getMappedClass(), enu);	
		}
		
		// --- add all Classes,Enums,Nodes to the tree ---
		//
		for(Class<?> nodeClassTmp : classes){
			
			Class<? extends SmartGridObject> nodeClass;
			Class<? extends SmartGridObject> superClass;
			{	// redundant test allowing only instances of SGO
				Class<?> superClassTmp = nodeClassTmp.getSuperclass();
				if(SmartGridObject.class.isAssignableFrom(superClassTmp)){		// superClassTmp is sub class of SmartGridObject
					superClass = (Class<? extends SmartGridObject>) superClassTmp;
					nodeClass  = (Class<? extends SmartGridObject>) nodeClassTmp;
				}
				else{
					continue;
				}
			}
			
			GridDataNode node = getGridDataNode(nodeClass);
			GridDataNode superNode = getGridDataNode(superClass);
			
			assert node.superNode==null;	//super node wasn't set before (should be impossible)
			node.superNode = superNode;
			superNode.subNodes.add(node);
		}
		
		// --- The class to enum mapping must be finished before this ---
		assert correctClassToEnumMapping();
		
		printData();	//TODO: For testing only
	}
	
	/**
	 * For testing.
	 */
	public static void printData(){
		
		System.out.println("> classToEnumMap:");
		Set<Entry<Class<? extends SmartGridObject>, GridDataEnum>> entries1 = classToEnumMap.entrySet();
		for(Entry<Class<? extends SmartGridObject>, GridDataEnum> entry : entries1){
			System.out.println("\t" + entry.getKey().getSimpleName()+" --> "+entry.getValue());
		}
		System.out.println("> classToNodeMap:");
		Set<Entry<Class<? extends SmartGridObject>, GridDataNode>> entries2 = classToNodeMap.entrySet();
		for(Entry<Class<? extends SmartGridObject>, GridDataNode> entry : entries2){
			System.out.println("\t" + entry.getKey().getSimpleName()+" --> "+entry.getValue());
			assert entry.getKey()==entry.getValue().nodeClass;
		}
		
	}
	
	/**
	 * Get or initialize grid data node (adds new initialized nodes to mapping).
	 * @param c - Class with or without existing mapping to a node.
	 * @return initialized node.
	 */
	private static GridDataNode getGridDataNode(Class<? extends SmartGridObject> c){
		
		GridDataNode node = classToNodeMap.get(c);
		if(node==null){
			node = new GridDataNode(c, classToEnumMap.get(c));
			classToNodeMap.put(c, node);
		}
		return node;
	}
	
	/**
	 * Test for important internal assertion:
	 * Correct mapping from GridObjects to Enums and from Enums to GridObjects.
	 * 
	 * @return true if no errors in Enum to Object and Object to Enum mappings were found.
	 * (tests getEnum() and static factory method.)
	 */
	private static boolean correctClassToEnumMapping(){	
		
		GridDataEnum[] values = GridDataEnum.values();
		for(GridDataEnum enu : values){
			
			if(enu.ordinal()>=GridDataEnum.PLINE.ordinal()){	// Skip other objects
				Class<?> sgo = enu.getMappedClass();
				GridDataEnum enu2 = classToEnumMap.get(sgo);
				if(enu2 != enu){
					return false;
				}
			}
		}
		return true;
	}
	
}