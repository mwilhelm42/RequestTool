package proteomicsdb.org.requesttool.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;

/**
 *
 * @author Maxi
 */
public class APIPanel extends JPanel{
    
    private final JLabel apiSelectLabel;
    private final JComboBox apiSelectDropdown;
    private final SpringLayout layout = new SpringLayout();
    private HelpButton help;
    
    public APIPanel(final ActionListener theControler){
        
        this.setLayout(layout);
        
        //Label "Choose API"
        apiSelectLabel = new JLabel("Select an API from the Dropdown menu", SwingConstants.CENTER);
        apiSelectLabel.setToolTipText(apiSelectLabel.getText());
        this.add(apiSelectLabel);
        
        //Dropdown menu
        apiSelectDropdown = new JComboBox<DropdownItem>();
        apiSelectDropdown.setMinimumSize(new Dimension(250, 20));
        apiSelectDropdown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                theControler.actionPerformed(new ActionEvent(apiSelectDropdown, e.getID(), ((DropdownItem)apiSelectDropdown.getSelectedItem()).getValue(), e.getWhen(), e.getModifiers()));
            }
            
        });
        
        this.add(apiSelectDropdown);
        
        help = new HelpButton();
        this.add(help);
        
        Component[] allComponents = this.getComponents();
        for(int i = 0; i<allComponents.length; i++){
            allComponents[i].setEnabled(false);
        }
    }

    
    public void setAPIDropdownMenu(Vector<String> apiNames, Vector<String> apiTitles) throws InvalidInputException{
        apiSelectDropdown.setModel(new DefaultComboBoxModel(DropdownItem.getVectorOfDropdownItems(apiTitles, apiNames)));
    }
    
    public void setAPIHelpButton(String toolTipString){
        this.remove(help);
        if(toolTipString.equals("")){
            toolTipString = "No description available as of now.";
        }
        this.help = new HelpButton(toolTipString,this);
        this.add(help);
        resize();
    }
    
    public void resize(){
        layout.removeLayoutComponent(this);
        layout.removeLayoutComponent(this.apiSelectLabel);
        layout.removeLayoutComponent(this.apiSelectDropdown);
        
        
        layout.putConstraint(SpringLayout.EAST, this, ViewUtils.WINDOW_WIDTH-30, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, this, 40, SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, apiSelectLabel, Spring.constant(5, 5, 20), SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, apiSelectLabel, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.EAST, apiSelectDropdown, ViewUtils.WINDOW_WIDTH - 350, SpringLayout.WEST, apiSelectDropdown);
        layout.putConstraint(SpringLayout.SOUTH, apiSelectDropdown, 30, SpringLayout.NORTH, apiSelectDropdown);
        layout.putConstraint(SpringLayout.WEST, apiSelectDropdown, Spring.constant(20, 20, 20), SpringLayout.EAST, apiSelectLabel);
        layout.putConstraint(SpringLayout.NORTH, apiSelectDropdown, Spring.constant(5, 5, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, help, Spring.constant(20, 20, 20), SpringLayout.EAST, apiSelectDropdown);
        layout.putConstraint(SpringLayout.NORTH, help, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        this.revalidate();
        this.repaint();
    }
    
    
    
    
}
