package sgs.model.gridData;

import java.util.ArrayList;

import sgs.model.gridObjects.SmartGridObject;
import sgs.model.variables.VariableSet;

/**
 * Node holding information of class tree ad enums.
 * 
 * @author Kristofer Schweiger
 */
public class GridDataNode {
	
	/** SmartGridObject-Class for this node **/
	public final Class<? extends SmartGridObject> nodeClass;
	/** Enum corresponding to node class. Null for (abstract) class with no corresponding enum **/
	public final GridDataEnum nodeEnum;
	/** Variable set corresponding to class and enum. Null for (abstract) classes with no enum **/
	public final VariableSet nodeVariableSet;
	
	/** Super class (extends SmartGridObject) for this node class. Null if this is the SmartGridObject **/
	public GridDataNode superNode = null;
	/** Sub nodes (~sub classes) of this node **/
	public final ArrayList<GridDataNode> subNodes = new ArrayList<GridDataNode>();
	
	/**
	 * Node constructor.
	 * @param nodeClass
	 * @param nodeEnum - enum corresponding to class. Null if class abstract and variable set will not be used.
	 */
	GridDataNode(Class<? extends SmartGridObject> nodeClass, GridDataEnum nodeEnum){
		assert nodeClass!=null;
		this.nodeClass = nodeClass;
		
		if(nodeEnum!=null){
			this.nodeEnum = nodeEnum;
			this.nodeVariableSet = VariableSet.makeDefaultVariableSet();
		}
		else{
			this.nodeEnum = null;
			this.nodeVariableSet = null;
		}
	}
	
	@Override
	public String toString(){
		
		String thisC  = nodeClass.getSimpleName();
		String superC = superNode!=null?superNode.nodeClass.getSimpleName():"null";
		StringBuffer subC = new StringBuffer(subNodes.size()*10);
		for(GridDataNode n : subNodes){
			subC.append( n.nodeClass.getSimpleName()+" " );
		}
		
		return "[Node ["+thisC+"/"+nodeEnum+", superClass="+superC+", subClasses="+subC+"]]";
	}
	
}