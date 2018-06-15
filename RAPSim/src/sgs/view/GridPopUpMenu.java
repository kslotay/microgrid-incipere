package sgs.view;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import sgs.model.SgsGridModel;

/**
 * 
 * @author Huber
 * if a single rightclick was detected by the GridMapListener , the popupmenu opens on the x/y coordinate of the object. 
 * 
 */

public class GridPopUpMenu extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GridPopUpMenu(SgsGridModel gridModel, int x, int y){
		
		JPopupMenu editPopUp = new JPopupMenu();
		
        
		JMenuItem item = new JMenuItem("Test Pop Up Item");
		item.setHorizontalTextPosition(SwingConstants.RIGHT);
		
		editPopUp.add(item);
		
//		editPopUp.setLocation(x, y);
//		editPopUp.setDefaultLocale(SgsGridModel.mainView);
		
		
		
		
		
		
		editPopUp.setVisible(true);
//		this.setLocation(SgsGridModel.mainView);
		this.setComponentPopupMenu(editPopUp);
		
		
	}

}
