package proteomicsdb.org.requesttool.view;

import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Maxi
 */
public class ProgressRowTableModel extends AbstractTableModel implements ChangeListener{
    
    private Vector<ProgressRow> rows = new Vector<ProgressRow>();
    
    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0: 
                return rows.get(rowIndex).getRequestID();
            case 1:
                return rows.get(rowIndex).getStatus();
            case 2:
                return rows.get(rowIndex).getError();
            case 3:
                return rows.get(rowIndex).getProgress();
            default:
                return null;
        }
    }

    public void addRow(ProgressRow row){
        rows.add(0, row);
        row.setChangeListener(this);
        fireTableChanged(new TableModelEvent(this, 0, rows.size(),
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }
    
    public ProgressRow getRow(int index){
        return rows.get(index);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireTableChanged(new TableModelEvent(this, 0, rows.size(),
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }
    
    
    
}
