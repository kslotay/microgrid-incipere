package sgs.view;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import sgs.model.other.JTableModelVariables;
import sgs.model.variables.VariableSet;

/**
 * JTable selecting all text on cell edit. 
 * 
 * @author Kristofer Schweiger
 */
public class JTableSGS extends JTable {

	private static final long serialVersionUID = 1L;

	public JTableSGS(VariableSet variableSet){
		super();
		new JTableModelVariables(this, variableSet);
	}

	@Override
	public boolean editCellAt(int row, int column, EventObject e) {

		boolean result = super.editCellAt(row, column, e);

		final Component editor = getEditorComponent();

		/*  E.g. a mouse click was used to activate the editor.
		 *  Generally this is a double click and the second mouse click is
		 *  passed to the editor which would remove the text selection unless
		 *  we use the invokeLater()
		 */
		if ( editor!=null && editor instanceof JTextComponent ) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					((JTextComponent)editor).selectAll();
				}
			});
		}

		return result;
	}// end of editCellAt
	public void setTableModelVariables(VariableSet variableSet){
		new JTableModelVariables(this, variableSet);
	}
	
}
