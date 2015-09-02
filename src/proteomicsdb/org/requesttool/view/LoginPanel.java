package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Maxi
 */
public class LoginPanel extends JPanel {
    private final JLabel loginNameLabel;
    private final JTextField loginNameTextfield;
    private final JLabel loginPasswordLabel;
    private final JPasswordField loginPasswordField;
    private final JButton loginButton;
    private final JLabel loggedInButton;
    private final SpringLayout layout = new SpringLayout();
    
    public LoginPanel(ActionListener theControler) {
        this.setLayout(layout);
        this.setBackground(ViewUtils.BACKGROUND_COLOR_FRONT);
        
        //Name Label
        loginNameLabel = new JLabel("Enter your name here: ", SwingConstants.CENTER);
        this.add(loginNameLabel);
        
        //Name Textfield
        loginNameTextfield = new JTextField();
        loginNameTextfield.setPreferredSize(new Dimension(100,20));
        this.add(loginNameTextfield);
        
        //Password Label
        loginPasswordLabel = new JLabel("Enter your password here: ", SwingConstants.CENTER);
        this.add(loginPasswordLabel);
        
        //Password Textfield
        loginPasswordField = new JPasswordField();
        loginPasswordField.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar()==KeyEvent.VK_ENTER){
                    loginButton.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //Do Nothing
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //Do Nothing
            }
        });
        loginPasswordField.setPreferredSize(new Dimension(100,20));
        this.add(loginPasswordField);
        
        //LoginButton
        loginButton = new JButton("Login");
        loginButton.setName(ViewUtils.BUTTON_NAME_LOGIN);
        loginButton.addActionListener(theControler);
        this.add(loginButton);
        
        //LoggedInButton
        loggedInButton = new JLabel();
        loggedInButton.setToolTipText("Not logged in");
        loggedInButton.setPreferredSize(new Dimension(20,20));
        loggedInButton.setBackground(Color.red);
        loggedInButton.setOpaque(true);
        this.add(loggedInButton);
    }
    
//    @Override
//    public void notifyObservers(boolean flag){
//        for (Observer o: this.observers){
//            o.update(flag);
//        }
//    }

//    @Override
//    public void register(Observer observer) {
//         if(!this.observers.contains(observer)){
//            this.observers.add(observer);
//        }
//    }
//
//    @Override
//    public void unregister(Observer observer) {
//        this.observers.remove(observer);
//    }
//
    
    public String getUsername(){
        return loginNameTextfield.getText();
    }
    
    public String getPassword(){
        char[] pw = loginPasswordField.getPassword();
        String result = "";
        for (int i = 0; i<pw.length; i++){
            result += pw[i];
        }
        return result;
    }
    
    public void setLoggedInTrue(){
        this.loginNameTextfield.setEditable(false);
        this.loginPasswordField.setEditable(false);
        loggedInButton.setBackground(Color.green);
        loggedInButton.setToolTipText("Logged in :)");
    }
    
    public void setLoggedInFalse(){
        this.loginNameTextfield.setEditable(true);
        this.loginPasswordField.setEditable(true);
        loggedInButton.setBackground(Color.red);
        loggedInButton.setToolTipText("Not logged in");
    }
    
    public void resize(){
        layout.removeLayoutComponent(this);
        layout.removeLayoutComponent(this.loggedInButton);
        layout.removeLayoutComponent(this.loginButton);
        layout.removeLayoutComponent(this.loginNameLabel);
        layout.removeLayoutComponent(this.loginNameTextfield);
        layout.removeLayoutComponent(this.loginPasswordField);
        layout.removeLayoutComponent(this.loginPasswordLabel);
        
        layout.putConstraint(SpringLayout.EAST, this, ViewUtils.WINDOW_WIDTH-30, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, this, 40, SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, loginNameLabel, Spring.constant(5, 5, 20), SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, loginNameLabel, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, loginNameTextfield, Spring.constant(10, 10, 20), SpringLayout.EAST, loginNameLabel);
        layout.putConstraint(SpringLayout.NORTH, loginNameTextfield, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, loginPasswordLabel, Spring.constant(10, 10, 20), SpringLayout.EAST, loginNameTextfield);
        layout.putConstraint(SpringLayout.NORTH, loginPasswordLabel, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, loginPasswordField, Spring.constant(10, 10, 20), SpringLayout.EAST, loginPasswordLabel);
        layout.putConstraint(SpringLayout.NORTH, loginPasswordField, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, loginButton, Spring.constant(20, 20, 20), SpringLayout.EAST, loginPasswordField);
        layout.putConstraint(SpringLayout.NORTH, loginButton, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        layout.putConstraint(SpringLayout.WEST, loggedInButton, Spring.constant(20, 20, 20), SpringLayout.EAST, loginButton);
        layout.putConstraint(SpringLayout.NORTH, loggedInButton, Spring.constant(10, 10, 10), SpringLayout.NORTH, this);
        
        this.revalidate();
        this.repaint();
    }
}
