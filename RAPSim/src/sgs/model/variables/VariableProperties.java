package sgs.model.variables;

public class VariableProperties {
	
	public static final VariableProperties DefaultProperties = new VariableProperties(false,false);
	
	//public enum UserAvailability {EDITABLE, VISIBLE, NOT_VISIBLE};
	//public UserAvailability userAvailability = UserAvailability.NOT_VISIBLE;
	
	/** true if variable should be visible for the user **/
	private boolean isVisible;
	/** true if variable should be editable for the user, or true if not visible and variable is used internally **/
	private boolean isEditable;
	/** true if properties were set **/
	public boolean isSet=false;
	
	VariableProperties(){
		this(false,false);
	}
	/**
	 * Use false/true if variable is used internally, but not by the user.
	 * 
	 * @param isVisible
	 * @param isEditable
	 */
	public VariableProperties(boolean isVisible, boolean isEditable){
		this.isVisible = isVisible;
		this.isEditable = isEditable;
	}

	
	
	/**
	 * @return true if variable should be visible to user
	 */
	public boolean isVisible(){
		return isVisible;
	}
	/**
	 * @return true if variable should be editable by user
	 */
	public boolean isEditable(){
		return isEditable;
	}

	/**
	 * @param b
	 */
	public void set(boolean visible, boolean editable){
		if(this==DefaultProperties){
			System.err.println("Can not change default properties!");
		}
		isVisible = visible;
		isEditable = editable;
		isSet = true;
	}
	
	/**
	 */
	public void reset(){
		isVisible = DefaultProperties.isVisible;
		isEditable = DefaultProperties.isEditable;
		isSet = false;
	}
}
