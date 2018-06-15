package testing.t2;

import jGridMap.view.MenuOrganizer;
import jGridMap.view.MenuOrganizer.MenuItemType;
import jGridMap.view.MenuOrganizer.MenuSpecialType;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuItem;

import testing.Out;


public class TestMenue {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// --- test organizer ---
		//
		MenuOrganizer org = new MenuOrganizer();
		String[] path;
		
		path = new String[] {"menu1", "checkbox1"};
		org.makeOrGet(path, MenuItemType.jCheckboxMenuItem);
		Out.pl("Creations 1A: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu1", "checkbox2"};
		org.makeOrGet(path, MenuItemType.jCheckboxMenuItem);
		Out.pl("Creations 1B: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu1", ""};
		org.makeSpecial(path, MenuSpecialType.JSeperator);
		Out.pl("Creations 1C: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu1", "item1"};
		org.makeOrGet(path, MenuItemType.jMenueItem);
		Out.pl("Creations 1C: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu1", "item2"};
		org.makeOrGet(path, MenuItemType.jMenueItem);
		Out.pl("Creations 1D: "+org.getLastNrOfCreations());
		
		// ---
		
		path = new String[] {"menu2", "subMenu","radio1"};
		JMenuItem r1 = org.makeOrGet(path, MenuItemType.jRadioButtonMenuItem);
		Out.pl("Creations 2A: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu2", "subMenu", "radio2"};
		JMenuItem r2 = org.makeOrGet(path, MenuItemType.jRadioButtonMenuItem);
		Out.pl("Creations 2B: "+org.getLastNrOfCreations());
		
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(r1);
		bgroup.add(r2);
		
		path = new String[] {"menu2", ""};
		org.makeSpecial(path, MenuSpecialType.JSeperator);
		Out.pl("Creations 1C: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu2", "item1"};
		org.makeOrGet(path, MenuItemType.jMenueItem);
		Out.pl("Creations 2C: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu2", "item2"};
		org.makeOrGet(path, MenuItemType.jMenueItem);
		Out.pl("Creations 2D: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu2", "item1"};
		org.makeSpecial(path, MenuSpecialType.JSeperator);
		Out.pl("Creations 1C: "+org.getLastNrOfCreations());
		
//		path = new String[] {"menu2", "aEmptySubMenu"};
//		org.makeOrGet(path, MenuItemType.jMenue);
//		Out.pl("Creations 2E: "+org.totalNrOfCreations);
		
		path = new String[] {"menu2", "aSubMenu", "aEmptySubMenu", ""};
		org.makeSpecial(path, MenuSpecialType.JSeperator);
		Out.pl("Creations 1C: "+org.getLastNrOfCreations());
		
		path = new String[] {"menu2", "long...", "long...", "long...", "long...", 
				 "long...",  "...path!"};
		org.makeOrGet(path, MenuItemType.jMenueItem);
		Out.pl("Creations 2E: "+org.getLastNrOfCreations());
		org.makeSpecial(org.getLastPath(), MenuSpecialType.JSeperator);
		org.makeSpecial(org.getLastPath(), MenuSpecialType.JSeperator);
		org.makeSpecial(org.getLastPath(), MenuSpecialType.JSeperator);
		org.makeSpecial(org.getLastPath(), MenuSpecialType.JSeperator);
		
		path = new String[] {"menu2", "long...", "long...", "long...", "long...", ""};
		org.makeSpecial(path, MenuSpecialType.JSeperator);
		org.makeSpecial(org.getLastPath(), MenuSpecialType.JSeperator);
		
		// --- make Frame ---
		//
		
		JFrame frame = new JFrame("Menu test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(org.getJMenuBar());
		frame.pack();
		frame.setSize(400, 300);
		frame.setVisible(true);
	}
	
	

}
