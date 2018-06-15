package sgs.model.variables;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.apache.commons.math3.complex.Complex;

/**
 * Based an TreeSet.
 * Holds a set of variables for grid objects.
 * 
 * Only one variable with a certain name (ignore case) is allowed in the set.
 * Remove variables before overwriting them.
 * 
 * @author Kristofer Schweiger
 */
public class VariableSet extends TreeSet<SingleVariable> {

	private static final long serialVersionUID = 1L;
	
	protected LinkedList<String> modified = new LinkedList<String>();
	
	/**
	 * Constructor: Empty
	 */
	public VariableSet(){
		//
	}
	
	/** 
	 * Does not allow overwriting names!
	 * @return true.
	 * @throws SecurityException if you try to overwrite a name.
	 */
	@Override
	public boolean add(SingleVariable a){
		//a.referenceSet = this;
		boolean success = super.add(a);
		
		if(!success)
			throw new SecurityException("Not allowed, attribute \""+a.name()+"\" already contained in set.");
		
		return success;
	}
	
	/**
	 * Factory method
	 * @param currentpowerconsumption
	 * @param d
	 * @return the SingleVariable
	 */
	public SingleVariable add(EnumPV name) {
		
		Object value = name.getDefaultValue();
		if(value==null){
			System.err.println("value was null");
			value = "";
			//throw new RuntimeException("Value was null!");
		}
		
		// --- build the valueContainer ---
		//
		AbstractValue valueContainer = null;
		// TODO: numeric value with Double, maybe Complex.
		if(value instanceof Double){
			valueContainer = new NumericValue((Double)value);
		}
		else if(value instanceof Complex){
			valueContainer = new NumericValue((Complex)value);
		}
		else if(value instanceof NumericValue){
			valueContainer = new NumericValue((NumericValue)value);
		}
		else{
			valueContainer = new StringValue(value.toString());
		}
		
		// --- build the variable ---
		//
		SingleVariable variable = new SingleVariable(name, valueContainer);
		
		// --- add the variable ---
		//
		this.add(variable);
		return variable;
	}

	/**
	 * @param a
	 * @return see add()
	 * @return true if name was not contained (added)
	 */
	public boolean addIfNotContained(SingleVariable a){
		boolean success = super.add(a);
		return success;
	}
	/**
	 * @param a
	 * @return see add()
	 * @return true if name was not contained (added)
	 */
	public boolean addAllIfNotContained(VariableSet a){
		boolean success = super.addAll(a);
		return success;
	}
	
	@Override
	public boolean remove(Object o){
		//throw new SecurityException("Not allowed, attribute \""+a.getName()+"\" already contained in set.");
		throw new SecurityException("You don't want to do that.");
	}
	
	/**
	 * Remove attribute with name.
	 * @param name
	 * @return true if success
	 */
	public boolean remove(EnumPV name){
		return remove(name.name());
	}
	/**
	 * @see remove(PredefinedVariablesEnum)
	 */
	public boolean remove(String name){
		return remove(new SingleVariable(name));
	}
	/**
	 * @see remove(PredefinedVariablesEnum)
	 */
	public boolean remove(SingleVariable a){
		//a.referenceSet = null;
		return super.remove(a);
	}
	
	/**
	 * @param name
	 * @return attribute with 'name' or null.
	 */
	public SingleVariable get(EnumPV name){
		return get(name.name());
	}
	
	
	public SingleVariable get(int row) {
		
		Iterator<SingleVariable> it = super.iterator();
		SingleVariable v = null;
		
		for(int i=0; i<=row; i++)
			v = it.next();
		
		return v;
	}
	
	/**
	 * @see get(PredefinedVariablesEnum)
	 */
	protected SingleVariable get(String name){
		
		SingleVariable tmp = new SingleVariable(name);
		SingleVariable a = super.floor(tmp);
		
		if(a!=null && a.compareTo(tmp)==0)
			return a;
		else
			return null;
	}
	
	/**
	 * @param name
	 * @return true if name is contained in set
	 */
	protected boolean contains(EnumPV name){
		return contains(name.name());
	}
	/**
	 * @see contains(PredefinedVariablesEnum)
	 */
	public boolean contains(String name){
		return contains(new SingleVariable(name));
	}
	/**
	 * @see contains(PredefinedVariablesEnum)
	 */
	public boolean contains(SingleVariable a){
		return super.contains(a);
	}
	
	@Override
	public String toString(){
		
		StringBuffer content = new StringBuffer(VariableSet.class.getSimpleName()+" with "+size()+" entries:");
		
		for(SingleVariable a : this){
			content.append(" \""+a.name()+"\"");
		}
		content.append(".");
		
		return content.toString();
	}
	
	/**
	 * @return full content of set.
	 */
	public String toStringFull(){
		
		StringBuffer content = new StringBuffer(VariableSet.class.getSimpleName()+" with "+size()+" entries:");
		
		for(SingleVariable a : this){
			content.append(" "+a.toString());
		}
		content.append(".");
		
		return content.toString();
	}

	/**
	 * @return Object array with dimensions [size][2]
	 * where 
	 * 	array[i][0] = name
	 *  array[i][1] = value
	 */
	public Object[][] getObjectArray() {
		
		Object[][] array = new Object[this.size()][2]; 
		
		{
			int i=0;
			for(SingleVariable s : this){
				array[i][0] = s.name();
				array[i][1] = s.value();
				i++;
			}
		}
		return array;
	}
	
	/**
	 * Set an EXISTING variable with name 'name'.
	 * @param name
	 * @param value
	 */
	public void set(String name, String value){
		//SingleVariable var = get(name);
		//if(var==null){
		//	System.err.println("Variable with the name '"+name+"' does not exist!");
		//	return;
		//}
		get(name).value().setValue(value);
	}
	/**
	 * Set an EXISTING variable with name 'name'.
	 * @param name
	 * @param value
	 */
	public void set(EnumPV name, Object value){
		get(name).setValue(value.toString());
	}

	/**
	 * @param sv
	 * @return true if Variable exists AND isVisible OR isEditable (definition)
	 */
    public boolean isUsed(EnumPV name){
    	SingleVariable sv = get(name);
    	if(sv==null)
    		return false;
    	return sv.properties.isVisible() || sv.properties.isEditable();
    }
	
	/**
	 * Get combined variable set.
	 * Makes no deep copy!
	 * @param a
	 * @param b
	 * @return new set with variables from a and b. Prefers variables in b.
	 */
	public static VariableSet combine(VariableSet a, VariableSet b){
		
		VariableSet nSet = new VariableSet();
		
		for(SingleVariable v : b){
			nSet.addIfNotContained(v);
		}
		for(SingleVariable v : a){
			nSet.addIfNotContained(v);
		}
		
		return nSet;
	}
	
	/**
	 * @return a Copy up to value from this Set.
	 */
	public VariableSet copyKeepProperties(){
		
		VariableSet nSet = new VariableSet();
		
		for(SingleVariable v : this){
			nSet.add(v.copyKeepProperties());
		}
		return nSet;
	}
	
	/**
	 * @return a variableSet with all possible variables
	 */
	public static VariableSet makeDefaultVariableSet(){
		
		EnumPV[] enums = EnumPV.values();
		VariableSet varSet = new VariableSet();
		
		for(int i=0; i<enums.length; i++){
			EnumPV enu = enums[i];
			varSet.add(enu);
		}
		
		return varSet;
	}
	
	/**
	 * @return a variable set containing only the visible ones.
	 */
	public VariableSet getVisibles(){
		VariableSet visible = new VariableSet();
		for(SingleVariable v : this){
			if(v.properties.isVisible())
				visible.add(v);
		}
		return visible;
	}
	
	
}
