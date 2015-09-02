package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import proteomicsdb.org.requesttool.controller.Control;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.OutputColSpec;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;
import proteomicsdb.org.requesttool.model.request.RequestHandler;

/**
 *
 * @author Maxi
 */
public class View extends JFrame{
    
    private final JPanel basic;
    private final LoginPanel loginPanel;
    private final APIPanel apiPanel;
    private final CentralPanel centralPanel;
    private final GoPanel goPanel;
    private final ProgressPanel progressPanel;
    private final JSeparator sep1,sep2,sep3,sep4;
    private final SpringLayout basicLayout = new SpringLayout();
    private final JMenuBar menuBar = new JMenuBar(); 
    
    
    public View(Control theControler){
        
//        System.out.println("Building Window");
        this.setTitle("Request Tool");
        
        
        ImageIcon image = new ImageIcon(getClass().getResource("ProteomicsDBLogo.png"));
        this.setIconImage(image.getImage());
        
        JMenu menu = new JMenu("Menu");
        
        JMenuItem logout = new JMenuItem("Logout");
        logout.setName(ViewUtils.MenuNames.MENU_NAME_LOGOUT.getName());
        logout.addActionListener(theControler);
        menu.add(logout);
        
        JMenuItem quit = new JMenuItem("Quit");
        quit.setName(ViewUtils.MenuNames.MENU_NAME_QUIT.getName());
        quit.addActionListener(theControler);
        menu.add(quit);

        JMenu help = new JMenu("Help");
        
        JMenuItem about = new JMenuItem("About");
        about.setName(ViewUtils.MenuNames.MENU_NAME_ABOUT.getName());
        about.addActionListener(theControler);
        help.add(about);
        
        JMenuItem link = new JMenuItem("To our Website");
        link.setName(ViewUtils.MenuNames.MENU_NAME_LINK.getName());
        link.addActionListener(theControler);
        help.add(link);
        
        menuBar.add(menu);
        menuBar.add(help);
        menuBar.setOpaque(true);
        this.setJMenuBar(menuBar);
        
        basic = new JPanel(basicLayout);
        basic.setBackground(ViewUtils.BACKGROUND_COLOR_BASIC);
//        basic.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        
        loginPanel = new LoginPanel(theControler);
        loginPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        loginPanel.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
        basic.add(loginPanel);
        
        sep1 = new JSeparator(SwingConstants.HORIZONTAL);
        sep1.setPreferredSize(new Dimension(ViewUtils.WINDOW_WIDTH,1));
        sep1.setForeground(Color.black);
        basic.add(sep1);
        
        apiPanel = new APIPanel(theControler);
        apiPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        apiPanel.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
        basic.add(apiPanel);
        
        sep2 = new JSeparator(SwingConstants.HORIZONTAL);
        sep2.setPreferredSize(new Dimension(ViewUtils.WINDOW_WIDTH,1));
        sep2.setForeground(Color.black);
        basic.add(sep2);
        
        centralPanel = new CentralPanel (theControler); 
        centralPanel.setBackground(ViewUtils.BACKGROUND_COLOR_BASIC);
//        centralPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        basic.add(centralPanel);
        
        sep3 = new JSeparator(SwingConstants.HORIZONTAL);
        sep3.setPreferredSize(new Dimension(ViewUtils.WINDOW_WIDTH,1));
        sep3.setForeground(Color.black);
        basic.add(sep3);

        goPanel = new GoPanel(theControler);
        goPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        goPanel.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
        basic.add(goPanel);
//        
        sep4 = new JSeparator(SwingConstants.HORIZONTAL);
        sep4.setPreferredSize(new Dimension(ViewUtils.WINDOW_WIDTH,1));
        sep4.setForeground(Color.black);
        basic.add(sep4);
        
        progressPanel = new ProgressPanel();
        progressPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        progressPanel.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
        basic.add(progressPanel);
        
        this.addComponentListener(new ComponentListener() { //needed, so the SettingsWindow knows its new position

            @Override
            public void componentResized(ComponentEvent e) {
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                goPanel.resize();
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        this.add(basic);
        this.setMinimumSize(new Dimension(680,500));
        this.setPreferredSize(new Dimension(1100,800));
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    
    @Override
    public Dimension getMaximumSize(){
        return new Dimension(5000,1200);
    }
    
    @Override
    public void paint(Graphics g) {
        Dimension d = getSize();
        ViewUtils.WINDOW_WIDTH = d.width;
        ViewUtils.WINDOW_HEIGHT = d.height;
        loginPanel.resize();
        apiPanel.resize();
        centralPanel.resize();
        goPanel.resize();
        progressPanel.resize();
        this.resize();
        Dimension m = getMaximumSize();
        boolean resize = d.width > m.width || d.height > m.height;
        d.width = Math.min(m.width, d.width);
        d.height = Math.min(m.height, d.height);
        if (resize) {
        Point p = getLocation();
        setVisible(false);
        setSize(d);
        setLocation(p);
        setVisible(true);
        }
        super.paint(g);
    }

    
    //standard getters
    
    public ProgressPanel getProgressPanel() {
        return progressPanel;
    }
    
    //loginPanel
    
    public String getUsername(){
        return loginPanel.getUsername();
    }
    
    public String getPassword(){
        return loginPanel.getPassword();
    }
    
    public void setLoggedInTrue(){
        loginPanel.setLoggedInTrue();
        setComponentsEnabled(basic);
    }
    
    public void setLoggedInFalse(){
        loginPanel.setLoggedInFalse();
        setComponentsDisabled(basic);
    }
    
    //APIPanel
    
    public void setAPIDropdownMenu(Vector<String> apiNames, Vector<String> apiTitles) throws InvalidInputException{
        this.apiPanel.setAPIDropdownMenu(apiNames, apiTitles);
        this.setAPIHelpButton("Click on the dropdown menu to select an API.");
    }
    
    public void setAPIHelpButton(String toolTipString){
        this.apiPanel.setAPIHelpButton(toolTipString);
    }
    
    //CentralPanel
    
    public void setInputPanel(Map<Integer, InputParSpec> inputPars){
        this.centralPanel.setInputPanel(inputPars);
    }
    
    public void setOutputPanel(List<OutputColSpec> outputCols){
        this.centralPanel.setOutputPanel(outputCols);
    }
    
    public void showInputWrong(int key, InvalidInputException iie){
        this.centralPanel.getInputPanel().showInputWrong(key, iie);
    }
    
    public void showInputValid(int key){
        this.centralPanel.getInputPanel().showInputValid(key);
    }
    
    public void showInputEmpty(int key){
        this.centralPanel.getInputPanel().showInputEmpty(key);
    }
    
    public Map<Integer,String> getUserInput(){
        return this.centralPanel.getInputPanel().getUserInput();
    }
    
    public SortedSet<String> getCheckedOutput(){
        return this.centralPanel.getOutputPanel().getCheckedOutput();
    }
    
    //Go Panel
    
    public String getFileName(){
        return this.goPanel.getFileName();
    }
    
    public String getOutputType() throws InvalidInputException{
        return this.goPanel.getOutputType();
    }
    
    public OutputConfiguration addAllSettingsValues(OutputConfiguration input)throws InvalidInputException{
        return this.goPanel.addAllSettingsValues(input);
    }
    
    //Progress Panel
    
    public void addRequestToProgressPanel(RequestHandler aRequestHandler){
        this.progressPanel.addRequest(aRequestHandler);
        basic.revalidate();
        this.repaint();
    }
    
    //Functionality of the view
    
    public void showErrorMessage(String text, String title){
        JTextArea outputText = new JTextArea(text);
        outputText.setEditable(false);
        JOptionPane.showMessageDialog(this, outputText, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public void showInformationMessage(String text, String title){ //TODO: Refactor and use this for subclasses of frontend...
        JTextArea outputText = new JTextArea(text);
        outputText.setEditable(false);
        JOptionPane.showMessageDialog(this, outputText, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setComponentsEnabled(JPanel panel){
        Component[] allComponents = panel.getComponents();
        for(int i = 0; i<allComponents.length; i++){
            Component temp = allComponents[i];
            temp.setEnabled(true);
            if(temp instanceof JPanel){
                setComponentsEnabled((JPanel) temp);
            }
        }
    }
    
    public void setComponentsDisabled(JPanel panel){
        Component[] allComponents = panel.getComponents();
        for(int i = 0; i<allComponents.length; i++){
            Component temp = allComponents[i];
            if(temp instanceof LoginPanel){
                //nothing
            }
            else{
                temp.setEnabled(false);
                if(temp instanceof JPanel){
                    setComponentsDisabled((JPanel) temp);
                }
            }
        }
        basic.revalidate();
        this.repaint();
    }
    
    //for layout
    public void resize(){
        basicLayout.removeLayoutComponent(basic);
        basicLayout.removeLayoutComponent(loginPanel);
        basicLayout.removeLayoutComponent(sep1);
        basicLayout.removeLayoutComponent(apiPanel);
        basicLayout.removeLayoutComponent(sep2);
        basicLayout.removeLayoutComponent(centralPanel);
        basicLayout.removeLayoutComponent(sep3);
        basicLayout.removeLayoutComponent(goPanel);
        basicLayout.removeLayoutComponent(sep4);
        basicLayout.removeLayoutComponent(progressPanel);
        
        basicLayout.putConstraint(SpringLayout.EAST, basic, Spring.constant(0, ViewUtils.WINDOW_WIDTH, 1200), SpringLayout.WEST, basic);
        basicLayout.putConstraint(SpringLayout.SOUTH, basic, Spring.constant(0, ViewUtils.WINDOW_HEIGHT, 1200), SpringLayout.NORTH, basic);
        
        basicLayout.putConstraint(SpringLayout.WEST, loginPanel, 10, SpringLayout.WEST, basic);
        basicLayout.putConstraint(SpringLayout.NORTH, loginPanel, 10, SpringLayout.NORTH, basic);
        
//        basicLayout.putConstraint(SpringLayout.WEST, sep1, 0, SpringLayout.WEST, basic);
//        basicLayout.putConstraint(SpringLayout.NORTH, sep1, 50, SpringLayout.NORTH, basic);
//        basicLayout.putConstraint(SpringLayout.EAST, sep1, 0, SpringLayout.EAST, basic);
        
        basicLayout.putConstraint(SpringLayout.WEST, apiPanel, 10, SpringLayout.WEST, basic);
        basicLayout.putConstraint(SpringLayout.NORTH, apiPanel, 65, SpringLayout.NORTH, basic);
        
//        basicLayout.putConstraint(SpringLayout.WEST, sep2, 0, SpringLayout.WEST, basic);
//        basicLayout.putConstraint(SpringLayout.NORTH, sep2, 100, SpringLayout.NORTH, basic);
//        basicLayout.putConstraint(SpringLayout.EAST, sep2, 0, SpringLayout.EAST, basic);
        
        basicLayout.putConstraint(SpringLayout.WEST, centralPanel, 10, SpringLayout.WEST, basic);
        basicLayout.putConstraint(SpringLayout.NORTH, centralPanel, 120, SpringLayout.NORTH, basic);
        
//        basicLayout.putConstraint(SpringLayout.WEST, sep3, 0, SpringLayout.WEST, basic);
//        basicLayout.putConstraint(SpringLayout.NORTH, sep3, 5, SpringLayout.SOUTH, centralPanel);
//        basicLayout.putConstraint(SpringLayout.EAST, sep3, 0, SpringLayout.EAST, basic);
        
        basicLayout.putConstraint(SpringLayout.WEST, goPanel, 10, SpringLayout.WEST, basic);
        basicLayout.putConstraint(SpringLayout.NORTH, goPanel, 10, SpringLayout.SOUTH, centralPanel);
        
//        basicLayout.putConstraint(SpringLayout.WEST, sep4, 0, SpringLayout.WEST, basic);
//        basicLayout.putConstraint(SpringLayout.NORTH, sep4, 5, SpringLayout.SOUTH, goPanel);
//        basicLayout.putConstraint(SpringLayout.EAST, sep4, 0, SpringLayout.EAST, basic);
        
        basicLayout.putConstraint(SpringLayout.WEST, progressPanel, 10, SpringLayout.WEST, basic);
        basicLayout.putConstraint(SpringLayout.NORTH, progressPanel, 10, SpringLayout.SOUTH, goPanel);
        
//        basic.revalidate();
//        this.repaint();
    }

    
    
}
