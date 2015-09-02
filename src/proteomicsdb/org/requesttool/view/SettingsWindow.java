package proteomicsdb.org.requesttool.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import proteomicsdb.org.requesttool.model.ModelUtils;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.description.PredefinedValue;
import proteomicsdb.org.requesttool.model.writing.Writer;
import proteomicsdb.org.requesttool.model.writing.WriterFactory;

/**
 *
 * @author Maxi
 */
public class SettingsWindow extends JFrame {
    private final JLabel selectType = new JLabel("Select type of outputfile: ");
    private final JCheckBox batchJobAllowed = new JCheckBox("Allow batch jobs?", true);
//    private final JCheckBox useParametersInFileName = new JCheckBox("Use the multivalued parameters in the filename?", true);
    private final JCheckBox doAppend = new JCheckBox("Append result of request to the destination file?", false);
    private final JComboBox typeBox;
    private final JPanel basic = new JPanel(new GridBagLayout());
    private final JButton ok = new JButton("OK");
    private HashMap<String,JTextField> textFields = new HashMap<String, JTextField>();
    private HashMap<String,JComboBox> dropdowns = new HashMap<String, JComboBox>();
    
    private final GoPanel parent;

    public SettingsWindow(GoPanel theGoPanel){
        this.parent = theGoPanel;
        this.setTitle("Settings");
        
        Vector writers = WriterFactory.getAllPossibleWritersAsVector();
        Collections.sort(writers, new Comparator(){

        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
        
        });
        
        typeBox = new JComboBox(writers);
        batchJobAllowed.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ModelUtils.DO_BATCH_JOB = batchJobAllowed.isSelected();
            }
            
        });
        
        int gridy = 0;
        GridBagConstraints c;
        
        c = new GridBagConstraints(0, gridy, 2, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);
        basic.add(batchJobAllowed,c);
        c = new GridBagConstraints(2, gridy++, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);
        basic.add(new HelpButton("If you check this box, you can specify multiple input values, separating your values with a " + ModelUtils.BATCH_JOB_SEPARATOR + "\n" 
                        + "Then a request will be started for each combination of multivalued input parameters.", basic), c);//TODO: Make this more understandable. 
        
        c = new GridBagConstraints(0, gridy, 2, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);
        basic.add(doAppend,c);
        c = new GridBagConstraints(2, gridy++, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);
        basic.add(new HelpButton("If you check this box, the file you write to will not be overwritten, but the result of your request will be appended to it\n"
                        + "Take care: Appending does not check the sanity of the result. You should check the same columns names in the request to be appended.\n"
                        + "Regarding batch jobs: If you check this box, the results of your request will all be written into one file.", basic), c);
        
        c = new GridBagConstraints(0, gridy, 1, 2, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);
        basic.add(selectType, c);
        
        c = new GridBagConstraints(1, gridy++, 2, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);
        basic.add(typeBox, c);
        
        c = new GridBagConstraints(0, gridy++, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15,0,0,0), 0, 0);
        basic.add(ok, c);
        
        final int staticComponentCount = basic.getComponentCount();
        final int gridyToGive = gridy;
        
        typeBox.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) { 
                textFields = new HashMap<String,JTextField>();
                dropdowns = new HashMap<String,JComboBox>();
                int threshold = basic.getComponentCount(); //needed, since when I delete components the count will decrease
                int innerGridy = gridyToGive;
                for(int i = staticComponentCount-1; i<threshold;i++){
                    basic.remove(staticComponentCount-1);
                }
                
                GridBagConstraints a = new GridBagConstraints(0, 0, 1, 1, 0.2, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);
                GridBagConstraints b = new GridBagConstraints(1, 0, 1, 1, 0.6, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,10,0,10), 0, 0);
                GridBagConstraints c = new GridBagConstraints(2, 0, 1, 1, 0.2, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,10,0,10), 0, 0);

                
                Map<String, InputParSpec> specificSettings = ((Writer) typeBox.getSelectedItem()).getSpecificSettings();
                for(String key : (specificSettings.keySet())){
                    a.gridy = innerGridy;
                    b.gridy = innerGridy;
                    c.gridy = innerGridy++;
                    
                    InputParSpec currentSetting = specificSettings.get(key);
                    
                    basic.add(new JLabel(key +": "),a);
                    final String toolTipString = currentSetting.getDescription() + "\nExample: " + currentSetting.getExample(); //TODO: Dropdown for predefined
                    
                    if(currentSetting.hasPredefinedValues()){
                        List<PredefinedValue> predefValuesList = currentSetting.getPredefinedValues();
                        Vector<DropdownItem> predefValuesVector = new Vector<DropdownItem>();
                        for (int i = 0; i < predefValuesList.size(); i++){
                            predefValuesVector.add(new DropdownItem(predefValuesList.get(i).getDescription(), predefValuesList.get(i).getValue()));
                        }
                        JComboBox<DropdownItem> dropdown = new JComboBox(predefValuesVector);
                        dropdowns.put(key, dropdown);
                        dropdown.setName("" + key);
                        dropdown.setPreferredSize(new Dimension(100,20));
                        basic.add(dropdown, b);
                    }
                    else{
                        final JTextField text = new JTextField();
                        textFields.put(key, text);
                        text.setName(key);
                        text.setPreferredSize(new Dimension(100,20));
                        basic.add(text, b);
                    }
                    
                    basic.add(new HelpButton(toolTipString, basic), c);
                }
                c = new GridBagConstraints(0, innerGridy, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15,0,0,0), 0, 0);
                basic.add(ok,c);
                basic.revalidate();
                basic.repaint();
            }
        });
        typeBox.setSelectedIndex(0);//So csv is the standard
        
        ok.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                parent.getSettingsWindow().dispose();
            }
            
        });
        
        this.add(basic);
        this.setMinimumSize(new Dimension(600,100));
        this.setPreferredSize(new Dimension(600,300));
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public String getOutputType(){
        return this.typeBox.getSelectedItem().toString();
    }
    
    public OutputConfiguration addAllSettingsValues(OutputConfiguration input){
        input.setDoAppend(this.doAppend.isSelected());
        for(String key: this.textFields.keySet()){
            input.addSettingsValue(textFields.get(key).getText(), key);
        }
        for(String key: this.dropdowns.keySet()){
            input.addSettingsValue(((DropdownItem) dropdowns.get(key).getSelectedItem()).getValue(), key);
        }
        return input;
    }
    
}
