package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import proteomicsdb.org.requesttool.model.request.RequestHandler;

/**
 *
 * @author Maxi
 */
public class ProgressPanel extends JPanel{
    
//    private int lineCounter=1;
    
//    private final JLabel requestID = new JLabel("Request ID", SwingConstants.CENTER);
//    private final JLabel status = new JLabel("Status", SwingConstants.CENTER);
//    private final JLabel progress = new JLabel("Progress", SwingConstants.CENTER);
//    private final JPanel progressPane = new JPanel();
    private final JScrollPane progressScrollPane;
    private SpringLayout layout = new SpringLayout();
    private final ProgressRowTableModel model;
    private final JTable table;
    
    public ProgressPanel(){
        this.setLayout(layout);
        
//        this.requestID.setEnabled(false);
//        this.status.setEnabled(false);
//        this.progress.setEnabled(false);
        
        
        model = new ProgressRowTableModel();
        DefaultTableColumnModel columns = new DefaultTableColumnModel();
        TableColumn a = new TableColumn(0,300);
        a.setHeaderValue("RequestID");
        TableColumn b = new TableColumn(1,50);
        b.setHeaderValue("Status");
        TableColumn c = new TableColumn(2,0);
        c.setHeaderValue("Errors?");
        TableColumn d = new TableColumn(3,100);
        d.setHeaderValue("Progress");
        columns.addColumn(a);
        columns.addColumn(b);
        columns.addColumn(c);
        columns.addColumn(d);
        
        table = new JTable(model, columns);
        table.setBackground(Color.white);
        table.getTableHeader().setReorderingAllowed(false);
//        table.setFillsViewportHeight(true);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if(col==2){
                    model.getRow(row).showError();
                }
            }
        });
        c.setCellRenderer(new TableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return (Component) value;
            }
            
        });
        d.setCellRenderer(new TableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return (Component) value;
            }
            
        });
        
        progressScrollPane = new JScrollPane(table);
        progressScrollPane.getViewport().setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
//        progressScrollPane.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        progressScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
//        GridLayout innerLayout = new GridLayout(0, 1, 5, 0);
//        progressPane.setLayout(innerLayout);
//        progressPane.setBackground(Utils.BACKGROUND_COLOR_BASIC);
        
        
//        this.add(this.requestID);
//        this.add(this.status);
//        this.add(this.progress);
        this.add(this.progressScrollPane);
        
    }
    
    public void addRequest(RequestHandler aRequestHandler){
        model.addRow(new ProgressRow(aRequestHandler));
    }
    
    public void clear(){
        //TODO
    }
    
    public void resize(){
//        this.progressPane.setBackground(Utils.BACKGROUND_COLOR_BASIC);
//        layout.removeLayoutComponent(requestID);
//        layout.removeLayoutComponent(status);
//        layout.removeLayoutComponent(progress);
        
//        int aThird = (Utils.WINDOW_WIDTH-50)/3;
//        int aSixth = (Utils.WINDOW_WIDTH-50)/6;
        int difference = ViewUtils.START_WINDOW_HEIGHT - ViewUtils.WINDOW_HEIGHT;
        
        layout.putConstraint(SpringLayout.EAST, this, ViewUtils.WINDOW_WIDTH-30, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, this, 275-(difference/2), SpringLayout.NORTH, this);
        
//        layout.putConstraint(SpringLayout.WEST, requestID, (aSixth), SpringLayout.WEST, this);
//        layout.putConstraint(SpringLayout.NORTH, requestID, 5, SpringLayout.NORTH, this);
//        
//        layout.putConstraint(SpringLayout.WEST, status, (aThird+aSixth), SpringLayout.WEST, this);
//        layout.putConstraint(SpringLayout.NORTH, status, 5, SpringLayout.NORTH, this);
//        
//        layout.putConstraint(SpringLayout.WEST, progress, (2*aThird+aSixth), SpringLayout.WEST, this);
//        layout.putConstraint(SpringLayout.NORTH, progress, 5, SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, progressScrollPane, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, progressScrollPane, 0, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, progressScrollPane, 0, SpringLayout.EAST,  this);
        layout.putConstraint(SpringLayout.SOUTH, progressScrollPane, 0, SpringLayout.SOUTH, this);
        
//        layout.putConstraint(SpringLayout.EAST, progressPane, (Utils.WINDOW_WIDTH-35), SpringLayout.WEST,  progressPane);
        
        
        this.revalidate();
        this.repaint();
        
    }
    
}
