package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.OutputColSpec;

/**
 *
 * @author Maxi
 */
public class CentralPanel extends JPanel{

    private InputPanel inputPanel;
    private OutputPanel outputPanel;
    private JScrollPane inputScrollPane;
    private JScrollPane outputScrollPane;
    private SpringLayout layout = new SpringLayout();
    
    public CentralPanel(ActionListener control){
        
        this.setLayout(layout);
        
        //JScrollPane for the Input
        inputPanel = new InputPanel(control);
        inputPanel.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
        inputScrollPane = new JScrollPane(inputPanel);
        inputScrollPane.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        inputScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(inputScrollPane);
        
//        //Separator
//        JSeparator sepC = new JSeparator(SwingConstants.VERTICAL);
//        sepC.setPreferredSize(new Dimension(7,300));
//        sepC.setForeground(Color.black);
//        layout.putConstraint(SpringLayout.WEST, sepC, 5, SpringLayout.EAST, inputScrollPane);
//        layout.putConstraint(SpringLayout.NORTH, sepC, 0, SpringLayout.NORTH, this);
//        this.add(sepC);
        
        //JScrollPane for the Output
        outputPanel = new OutputPanel();
        outputPanel.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
        outputScrollPane = new JScrollPane(outputPanel);
        outputScrollPane.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        outputScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(outputScrollPane);
        
        Component[] allComponents = this.getComponents();
        for(int i = 0; i<allComponents.length; i++){
            allComponents[i].setEnabled(false);
        }
    }

    
    public void setInputPanel(Map<Integer, InputParSpec> inputPars){
        this.inputPanel.setInputPanel(inputPars);
    }
    
    public void setOutputPanel(List<OutputColSpec> outputCols){
        this.outputPanel.setOutputPanel(outputCols);
    }


    @Override
    public Component[] getComponents(){
        Component[] result = new Component[4];
        result[0] = this.inputPanel;
        result[1] = this.outputPanel;
        result[2] = this.outputScrollPane;
        result[3] = this.inputScrollPane;
        return result;
        
    }

    public InputPanel getInputPanel() {
        return inputPanel;
    }

    public OutputPanel getOutputPanel() {
        return outputPanel;
    }
    
    public void resize(){
        layout.removeLayoutComponent(inputScrollPane);
        layout.removeLayoutComponent(outputScrollPane);
        layout.removeLayoutComponent(this);
        
        int difference = ViewUtils.START_WINDOW_HEIGHT - ViewUtils.WINDOW_HEIGHT;
        
        layout.putConstraint(SpringLayout.EAST, inputScrollPane, (ViewUtils.WINDOW_WIDTH-440), SpringLayout.WEST, inputScrollPane);
        layout.putConstraint(SpringLayout.SOUTH, inputScrollPane, 280 - (difference/2), SpringLayout.NORTH, inputScrollPane);
        layout.putConstraint(SpringLayout.WEST, inputScrollPane, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, inputScrollPane, 0, SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.EAST, outputScrollPane, 0, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.SOUTH, outputScrollPane, 280 - (difference/2), SpringLayout.NORTH, outputScrollPane);
        layout.putConstraint(SpringLayout.WEST, outputScrollPane, 10, SpringLayout.EAST, inputScrollPane);
        layout.putConstraint(SpringLayout.NORTH, outputScrollPane, 0, SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.EAST, this, ViewUtils.WINDOW_WIDTH-28, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, this, 280 - (difference/2), SpringLayout.NORTH, this);
        this.revalidate();
        this.repaint();
    }
    
}
