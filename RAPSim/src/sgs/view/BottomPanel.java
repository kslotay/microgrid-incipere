package sgs.view;

import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * Panel with all elements at the bottom of the GUI.
 * 
 * @author Kristofer Schweiger
 */
public class BottomPanel extends JToolBar {

	private static final long serialVersionUID = 1L;
	public static Dimension defaultButtonDimension = new Dimension(85,60);
    
	/**
	 * Make button, add Action listener button, add the button to the view.
	 * @param iconPath	- path to icon (+name)
	 * @param l 		- ActionListener to add, can be null.
	 * @return JButton
	 */
    public JButton addControl_BottomButton(String iconPath, String toolTip, ActionListener l){
    	
    	ImageIcon icon = new ImageIcon(iconPath);
    	assert icon.getImageLoadStatus()==MediaTracker.COMPLETE;
    	JButton button = new JButton(icon);
    	
    	button.setPreferredSize(defaultButtonDimension);
    	//button.setSize(defaultButtonDimension);
    	button.setMaximumSize(defaultButtonDimension);
    	button.setMinimumSize(defaultButtonDimension);
    	this.add(button);
    	if(toolTip != null)
    		button.setToolTipText(toolTip);
    	if(l != null)
    		button.addActionListener(l);
    	
    	return button;
    }
    
}
