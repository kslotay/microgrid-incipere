package sgs.model.other;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import sgs.model.variables.SingleVariable;
import sgs.model.variables.VariableSet;

/**
 * TableModel for SGS.
 * Should have advanced configurations of editable fields later. // TODO //
 * 
 * @author Kristofer Schweiger
 */
public class JTableModelVariables extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private VariableSet variableSet;
	private String[] columnNames = new String[] {"Name", "Value", "Unit"};
	

	public JTableModelVariables(JTable table, VariableSet variableSet) {
		this.variableSet = variableSet;
		table.setModel(this);
		
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	/** Getter, @return column name **/
	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}


	@Override
	public int getRowCount() {
		return variableSet.size();
	}

	@Override
	public void setValueAt(Object aValue, int row, int col){
		SingleVariable s = variableSet.get(row);
		s.value().setValue(aValue.toString());
	}

	@Override
	public Object getValueAt(int row, int col) {
		
		SingleVariable s = variableSet.get(row);
		
		if(col==0){
			return s.name();
		}
		else if(col==1){
			return s.value().getValue();
		}
		else if(col==2){
			return s.unit();
		}
		else {
			throw new RuntimeException("Undefined column!");
		}
	}
	
	public SingleVariable getVariableAt(int row) {
		SingleVariable s = variableSet.get(row);
		return s;
	}
	
    @Override
    public boolean isCellEditable(int row, int column) {
        
    	if (column == 1) {
    		SingleVariable s = variableSet.get(row);
            return s.properties.isEditable();
        }
        return false;
    }
	
    
    
    
	
	
	
	
}
