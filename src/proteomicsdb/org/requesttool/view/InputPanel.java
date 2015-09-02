package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import proteomicsdb.org.requesttool.model.ModelUtils;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.PredefinedValue;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;

/**
 *
 * @author Maxi
 */
public class InputPanel extends JPanel{
    
    private JLabel startText;
    private ActionListener control;
    private HashMap<Integer, JTextField> textFields = new HashMap<Integer, JTextField>();
    private HashMap<Integer, JComboBox<DropdownItem>> dropdowns = new HashMap<Integer, JComboBox<DropdownItem>>();
    
    public InputPanel(ActionListener theControl){
        this.control = theControl;
        this.setLayout(new FlowLayout());
        startText = new JLabel("Input Parameters will be specified here after selecting an API");
        startText.setEnabled(false);
        this.add(startText);
    }
    
    public void setInputPanel(Map<Integer, InputParSpec> inputPars) { //Layout does not work
        this.removeAll();
        this.textFields = new HashMap<Integer, JTextField>();
        this.dropdowns = new HashMap<Integer, JComboBox<DropdownItem>>();
        
        if(inputPars == null){
            this.setLayout(new FlowLayout());
            startText = new JLabel("Input Parameters will be specified here after selecting an API");
            this.add(startText);
        }
        else{
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints(0, 0, 3, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0);
            this.add(new JLabel("Please specify the Input Parameters. For help click the \"?\" Buttons. For multiple Inputs, separate your inputs with " + ModelUtils.BATCH_JOB_SEPARATOR), c);

            int gridy = 1;
            c = new GridBagConstraints(0, 0, 1, 1, 0.2, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 10, 0);
            GridBagConstraints d = new GridBagConstraints(1, 0, 1, 1, 0.6, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
            GridBagConstraints e = new GridBagConstraints(2, 0, 1, 1, 0.2, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0);

            for(int key: inputPars.keySet()){
                InputParSpec currentInputParameter = inputPars.get(key);
                c.gridy = gridy;
                d.gridy = gridy;
                e.gridy = gridy++;
                //Parameter Name
                this.add(new JLabel(currentInputParameter.getName() +": "),c);
                final String toolTipString = currentInputParameter.getDescription() + "\nExpected Type: " + currentInputParameter.getType() + "\nExample Input: " + currentInputParameter.getExample();
                 
                //Getting the Input
                if(currentInputParameter.hasPredefinedValues()){
                    List<PredefinedValue> predefValuesList = currentInputParameter.getPredefinedValues();
                    Vector<DropdownItem> predefValuesVector = new Vector<DropdownItem>();
                    for (int i = 0; i < predefValuesList.size(); i++){
                        predefValuesVector.add(new DropdownItem(predefValuesList.get(i).getName(), predefValuesList.get(i).getValue()));
                    }
                    JComboBox<DropdownItem> dropdown = new JComboBox(predefValuesVector);
                    this.dropdowns.put(key, dropdown);
                    dropdown.setName("" + key);
                    dropdown.setPreferredSize(new Dimension(100,20));
                    this.add(dropdown, d);
                }
                else{
                    final JTextField text = new JTextField();
                    this.textFields.put(key, text);
                    text.setName("" + key);
                    text.setText(currentInputParameter.getExample());
                    text.addFocusListener(new FocusAdapter() {
                        @Override
                        public void focusGained(FocusEvent evt) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    text.selectAll();
                                }
                            });
                        }
                    });
                    text.addKeyListener(new KeyListener(){//have own keyListener only for keyRealeased

                        @Override
                        public void keyPressed(KeyEvent e) {
                            //nothing
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                            control.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "Validate:ID:" + text.getName() + ":Value:" + text.getText(),e.getWhen(),e.getModifiers()));  
                        }

                        @Override
                        public void keyTyped(KeyEvent e) {
                        } 
                    });
                    //To validate the default input
                    control.actionPerformed(new ActionEvent(text, -1, "Validate:ID:" + text.getName() + ":Value:" + text.getText(),-1,-1));
                    text.setPreferredSize(new Dimension(100,20));
                    this.add(text, d);
                }
                //Tooltip    
                final JLabel help = new JLabel("?", SwingConstants.CENTER);
                help.setPreferredSize(new Dimension(20,20));
                help.setBorder(BorderFactory.createLineBorder(Color.black,1));
                help.addMouseListener(new OnlyClickMouseListener(){
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(help, toolTipString , "Tooltip", JOptionPane.QUESTION_MESSAGE);
                    }
                });
                this.add(help, e);
            }
        }
        this.revalidate();
        this.repaint();
    }

    public void showInputWrong(int key, InvalidInputException iie){
        JTextField wrongField = this.textFields.get(key);
        wrongField.setBackground(Color.red);
        wrongField.setOpaque(true);
        wrongField.setToolTipText("\nYour input is not valid: \n" + iie.getMessage()); //do this for the ?
    }
    
    public void showInputValid(int key){
        JTextField validField = this.textFields.get(key);
        validField.setBackground(Color.green);
        validField.setOpaque(true);
        validField.setToolTipText("");
    }
    
    public void showInputEmpty(int key){
        JTextField emptyField = this.textFields.get(key);
        emptyField.setBackground(Color.white);
        emptyField.setOpaque(true);
    }
    
    public Map<Integer,String> getUserInput(){
        HashMap<Integer,String> userInput = new HashMap<Integer,String>();
        for(int key: this.textFields.keySet()){
            userInput.put(key,this.textFields.get(key).getText());
        }
        for(int key: this.dropdowns.keySet()){
            userInput.put(key,((DropdownItem)this.dropdowns.get(key).getSelectedItem()).getValue());
        }
        return userInput;
    }
}
