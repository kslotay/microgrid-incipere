package sgs.model.other;

import java.awt.Window;
import java.util.LinkedList;

/**
 * Objects may be directly related to windows, which means the window should stop to exist
 * of this object stops to exist.
 * 
 * This is a simple concept which could be advanced with adding a call to a special window 
 * interface, which would store the lated data and handle other closing relevant actions.
 * (Not necessary in current context, therefore the simpler solution) 
 * 
 * @author Kristofer Schweiger
 */
public interface WindowOwner {
	
	/**
	 * Adds a window w which is owned exclusively by this object.
	 * The window should be closed if this object stops to exist or will not be used any more.
	 * 
	 * @param w
	 * @return like in {@link LinkedList#add}
	 * @see #removeWindow()
	 * @see #closeAllWindows()
	 */
	public boolean addWindow(Window w);
	
	/**
	 * Remove a window w which was owned exclusively by this object.
	 * Should also call w.dispose().
	 * 
	 * @param w
	 * @return like in {@link LinkedList#remove}
	 * @see #removeAllWindows()
	 * @see #add(Window)
	 */
	public boolean removeWindow(Window w);
	
	/**
	 * Will close all windows owned exclusively by this object.
	 * Close is executed by calling {@linkplain Window.dispose()}
	 * 
	 * @return number of windows.
	 * @see #add(Window)
	 */
	public int closeAllWindows();
	
//	/**
//	 * Will refresh all windows owned exclusively by this object. 
//	TODO: not possible with general window object.
//	 */
//	public void refreshAllWindows();
	
	
}
