package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import proteomicsdb.org.requesttool.model.description.OutputColSpec;

/**
 *
 * @author Maxi
 */
public class OutputPanel extends JPanel{ //TODO: Have a List of all checkboes, to check whether at least one is checked; have default all checked; have checkAll/uncheckALL button

    private JLabel startText;
    private JCheckBox selectAll;
    private HashMap<String,JCheckBox> columns = new HashMap<String,JCheckBox>();
    
    public OutputPanel(){
        this.setLayout(new FlowLayout());
        startText = new JLabel("Output Columns will be specified here after selecting an API");
        startText.setEnabled(false);
        this.add(startText);
    }
    
    public void setOutputPanel(List<OutputColSpec> outputCols){
        this.removeAll();
        this.columns = new HashMap<String,JCheckBox>();
        
        if(outputCols == null){
            this.setLayout(new FlowLayout());
            startText = new JLabel("Output Columns will be specified here after selecting an API");
            this.add(startText);
        }
        
        else{
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 10, 5, 10), 0, 0);
            this.add(new JLabel("Select at least one Outputcolumn to be displayed."), c);
            
            c = new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
            this.selectAll = new JCheckBox("Select all", true);
            this.selectAll.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
            this.selectAll.addActionListener(new ActionListener(){
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (String key: columns.keySet()){
                        columns.get(key).setSelected(selectAll.isSelected());
                    }
                }
                
            });
            this.add(selectAll, c);

            c = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            GridBagConstraints d = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);

            for (int i = 0; i<outputCols.size(); i++){
                c.gridy = i+2;
                d.gridy = i+2;
                JCheckBox box = new JCheckBox(outputCols.get(i).getName(), true);
                box.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
                this.columns.put(outputCols.get(i).getName(), box);
                this.add(box, c);
                
                final String toolTipString = outputCols.get(i).getDescription();
                final JLabel help = new JLabel("?", SwingConstants.CENTER);
                help.setPreferredSize(new Dimension(15,15));
                help.setBorder(BorderFactory.createLineBorder(Color.black,1));
                help.addMouseListener(new MouseListener(){ //TODO: Use own mouseListener with only mouseClicked() as abstract

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(help, toolTipString , "Tooltip", JOptionPane.QUESTION_MESSAGE);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        //nothing
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        //nothing
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        //nothing
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        //nothing
                    }

                });
                this.add(help, d);
            }
        }
        this.revalidate();
        this.repaint();
    }
    
    public SortedSet<String> getCheckedOutput(){
        TreeSet<String> checkedColumns = new TreeSet();
        for (String key: columns.keySet()){
            if(columns.get(key).isSelected()){
                checkedColumns.add(key);
            }
        }
        return checkedColumns;
    }
    
}
