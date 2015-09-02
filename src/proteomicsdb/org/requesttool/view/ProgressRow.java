package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import proteomicsdb.org.requesttool.model.request.RequestEvent;
import proteomicsdb.org.requesttool.model.request.RequestHandler;
import proteomicsdb.org.requesttool.model.request.RequestListener;
import proteomicsdb.org.requesttool.model.request.RequestNotifier;

/**
 *
 * @author Maxi
 */
public class ProgressRow implements RequestListener{
    
    private final String requestID;
    private String status;
    private final JProgressBar progress;
    private final JLabel error;
    private boolean errorHasOccured = false;
    private String errormessage = "No error has occured as of now";
    private ChangeListener changeListener;
    
    public ProgressRow(RequestHandler aRequestHandler){
//        this.setBorder(BorderFactory.createLineBorder(Color.yellow));
//        this.setMinimumSize(new Dimension(0,20));
//        this.setLayout(new GridBagLayout());
        this.requestID = aRequestHandler.getID();
        this.status = "Starting";
        this.progress = new JProgressBar(0, 100);
        this.error = new JLabel(":)", SwingConstants.CENTER);
        this.error.setPreferredSize(new Dimension(20,20));
        this.error.setBorder(BorderFactory.createLineBorder(Color.black,1));
        this.error.setToolTipText("Click to get Error message");
//        this.error.addMouseListener(new OnlyClickMouseListener() {
//                    @Override
//                    public void mouseClicked(MouseEvent e) {
//                        if(errorHasOccured){
//                            JOptionPane.showMessageDialog(error, errormessage, "This request has failed", JOptionPane.ERROR_MESSAGE);
//                        }
//                        else{
//                            JOptionPane.showMessageDialog(error, "No error occured as of now", "This request is running fine", JOptionPane.INFORMATION_MESSAGE);
//                        }
//                    }
//                });
        aRequestHandler.register(this);
//        GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 0.5, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,10), 0, 0);
//        this.add(requestID, c);
//        c.gridx = 1;
//        this.add(status, c);
//        c.weightx = 0;
//        c.gridx = 2;
//        this.add(error,c);
//        c.gridx = 3;
//        this.add(progress, c);
        
    }

    
    @Override
    public void update(RequestEvent message, RequestNotifier notifier) {
        status = message.name();
        switch (message.getProgress()) {
            case -1:    
                errormessage = breakLongStrings(message.getMessage(), 150);
                errorHasOccured = true;
                progress.setForeground(Color.red);
                error.setText(":(");
                error.setBorder(BorderFactory.createLineBorder(Color.red));
                error.setForeground(Color.red);
                break;
            case 100:   
                progress.setForeground(Color.green); 
                progress.setValue(100); 
                error.setBorder(BorderFactory.createLineBorder(Color.green));
                error.setForeground(Color.green);
                errormessage = "This request has finished succesfully";
                break;
            default:    
                progress.setValue(message.getProgress());
        }
        
        this.changeListener.stateChanged(new ChangeEvent(this));
        
    }
    
    public void showError(){
        if(errorHasOccured){
            JOptionPane.showMessageDialog(error, errormessage, "This request has failed", JOptionPane.ERROR_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(error, errormessage, "This request is fine", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public String getRequestID() {
        return requestID;
    }

    public String getStatus() {
        return status;
    }

    public JProgressBar getProgress() {
        return progress;
    }

    public JLabel getError() {
        return error;
    }
    
    public void setChangeListener(ChangeListener aListener){
        this.changeListener = aListener;
    }
    
    public static String breakLongStrings(String input, int limit) {
        if (input.length() > limit) {
            String result = "";
            String[] inputLines = input.split("\n");
            for (int i = 0; i < inputLines.length; i++) {
                if (inputLines[i].length() < limit) {
                    result += inputLines[i] + "\n";
                } else {
                    result += inputLines[i].substring(0, limit) + "\n";
                    inputLines[i] = inputLines[i].substring(limit);
                    i--;
                }
            }
            return result;
        }
        return input;
    }
    
}
