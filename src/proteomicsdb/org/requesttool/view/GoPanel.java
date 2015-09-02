package proteomicsdb.org.requesttool.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;

/**
 *
 * @author Maxi
 */
public class GoPanel extends JPanel{

    private final JButton go;
    private final JButton settings, browse;
    private final JFileChooser fileChooser;
    private final JLabel currentFile;
    private final JTextField currentFileTextField;
    private final SpringLayout layout = new SpringLayout();
    private SettingsWindow settingsWindow;
    
    public GoPanel(ActionListener control){
        this.setLayout(layout);
        
        
        fileChooser = new JFileChooser();
        
        currentFile = new JLabel("Destination File: ");
        this.add(currentFile);
        
        currentFileTextField = new JTextField();
        currentFileTextField.setPreferredSize(new Dimension(200,20));
        currentFileTextField.setText(ViewUtils.DEFAULT_OUTPUT_FILE);
        currentFileTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        currentFileTextField.selectAll();
                    }
                });
            }
        });
        this.add(currentFileTextField);
        
        browse = new JButton("Browse");
        this.add(browse);
        browse.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(browse);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
            
        });
        
        settings = new JButton("Settings");
        settings.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                settingsWindow.setVisible(true);
            }
            
        });
        this.add(settings);
        
        go = new JButton("Start Request");
        go.setName(ViewUtils.BUTTON_NAME_GO);
        go.addActionListener(control);
        this.add(go);
        
        settingsWindow = new SettingsWindow(this);
        settingsWindow.dispose();
        
        this.setEnabled(false);
        this.settings.setEnabled(false);
        this.go.setEnabled(false);
        this.fileChooser.setEnabled(false);
        this.currentFile.setEnabled(false);
        this.currentFileTextField.setEnabled(false);
        this.browse.setEnabled(false);
    }


    public String getFileName(){
        return currentFileTextField.getText();
    }
    
    public String getOutputType() throws InvalidInputException{
        if(!(settingsWindow==null)){
            return settingsWindow.getOutputType();
        }
        throw new InvalidInputException("Output Type", "Nothing selected", "Please use the Settings button to select a type");
    }
    
    public OutputConfiguration addAllSettingsValues(OutputConfiguration input)throws InvalidInputException{
        if(!(settingsWindow==null)){
            return this.settingsWindow.addAllSettingsValues(input);
        }
        throw new InvalidInputException("Output Type", "Nothing selected", "Please use the Settings button to select a type");
    }
    
    protected SettingsWindow getSettingsWindow(){
        return this.settingsWindow;
    }
    
    public void resize(){
        settingsWindow.setLocationRelativeTo(settings);
        
        layout.removeLayoutComponent(this);
        layout.removeLayoutComponent(this.go);
        
        
        layout.putConstraint(SpringLayout.EAST, this, ViewUtils.WINDOW_WIDTH-30, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, this, 40, SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, this.currentFile, Spring.constant(5, 5, 5), SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, this.currentFile, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, this.currentFileTextField, Spring.constant(10, 10, 10), SpringLayout.EAST, this.currentFile);
        layout.putConstraint(SpringLayout.NORTH, this.currentFileTextField, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, this.browse, Spring.constant(10, 10, 10), SpringLayout.EAST, this.currentFileTextField);
        layout.putConstraint(SpringLayout.NORTH, this.browse, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, this.settings, Spring.constant(20, 20, 20), SpringLayout.EAST, this.browse);
        layout.putConstraint(SpringLayout.NORTH, this.settings, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.EAST, this.go, Spring.constant(-10, -10, -10), SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, this.go, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        
        this.revalidate();
        this.repaint();
    }
}
