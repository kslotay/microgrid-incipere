package sgs.model.other;

import javax.swing.table.DefaultTableModel;

/**
 * TableModel for SGS.
 * Should have advanced configurations of editable fields later. // TODO //
 * 
 * @author Kristofer Schweiger
 */
public class JTableModelSGS extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public JTableModelSGS(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	
    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0) {
            return false;
        }
        return true;
    }
	
	
	
	
	
	
}
