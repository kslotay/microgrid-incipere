package sgs.controller;

import javax.swing.JFrame;
import sgs.model.SgsGridModel;
import sgs.view.GridPopUpMenu;


/**
 * 
 * @author Sabrina
 *
 */
public class GridPopUpMenuController {

	private final SgsGridModel sgsModel;
	private JFrame primaryViewI ; 
	
	public GridPopUpMenuController(SgsGridModel gridModel, int x, int y, JFrame view) {
		
		this.sgsModel = gridModel;
		this.primaryViewI = view;
			     
		GridPopUpMenu popUpMenu = new GridPopUpMenu(sgsModel, x, y);
//		GridPopUpMenu popUpMenu = new GridPopUpMenu();
		popUpMenu.setLocation(x, y);
//		popUpMenu.setVisible(true);
		primaryViewI.add(popUpMenu);
		
		System.out.println("popupMenu"); 

	}
	
}