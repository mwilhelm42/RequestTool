package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;


public class HelpButton extends JLabel {

    public HelpButton(final String toolTipString, final JComponent parent){
        super("?", SwingConstants.CENTER);
        this.setPreferredSize(new Dimension(20,20));
        this.setBorder(BorderFactory.createLineBorder(Color.black,1));
        this.addMouseListener(new OnlyClickMouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(parent, toolTipString , "Tooltip", JOptionPane.QUESTION_MESSAGE);
            }

        });
    }
    
    public HelpButton(){
        super("?", SwingConstants.CENTER);
        this.setPreferredSize(new Dimension(20,20));
        this.setBorder(BorderFactory.createLineBorder(Color.black,1));
    }
}
