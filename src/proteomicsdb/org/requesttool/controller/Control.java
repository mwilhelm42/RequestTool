package proteomicsdb.org.requesttool.controller;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import proteomicsdb.org.requesttool.model.ModelUtils;
import proteomicsdb.org.requesttool.model.description.APISpecification;
import proteomicsdb.org.requesttool.model.description.AllAPIsSpecification;
import proteomicsdb.org.requesttool.model.description.InputConfiguration;
import proteomicsdb.org.requesttool.model.description.InputConfigurationChangeable;
import proteomicsdb.org.requesttool.model.description.InputConfigurationValidated;
import proteomicsdb.org.requesttool.model.description.Intern;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;
import proteomicsdb.org.requesttool.model.login.SingletonLoginHandler;
import proteomicsdb.org.requesttool.model.request.RequestEvent;
import proteomicsdb.org.requesttool.model.request.RequestHandler;
import proteomicsdb.org.requesttool.model.request.RequestThreadPool;
import proteomicsdb.org.requesttool.model.request.RunnableRequestHandler;
import proteomicsdb.org.requesttool.view.ProgressPanel;
import proteomicsdb.org.requesttool.view.View;
import proteomicsdb.org.requesttool.view.ViewUtils;

/**
 * This class contains the main method of the Request Tool and handles the communication between the model and the view.
 * It implements ActionListener, to receive ActionEvents out of the view, which specify the actions of the user.
 * @author Maxi
 * @version 1.0
 */
public class Control implements ActionListener{
    
    /**
     * The GUI that this Control manages.
     */
    private View view;
    
    /**
     * The meta information that the request build upon.
     */
    private AllAPIsSpecification aas;
    
    /**
     * The API which is currently selected by the user.
     */
    private APISpecification selectedAPI;
    
    /**
     * The RequestThreadPool which manages all requests that are sent.
     */
    private RequestThreadPool threadPool;
    
    /**
     * Constructs a Control, setting the LookAndFeel and initializing a new View object for the Control and a new RequestThreadPool.
     */
    public Control(){
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
//              UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } 
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        view = new View(this);
        threadPool = new RequestThreadPool();
    }
    
    /**
     * Only constructs a new control. All other actions of the program are reactions to user actions.
     * @param args The parameters which can be given to the main method; they are ignored.
     */
    public static void main(String[] args){
        Control control = new Control();
    }

    
    /**
     * This method is used for communication from the view to the controller. 
     * It receives an ActionEvent and finds out to which JComponent this event belongs by looking at the class and name of the source.
     * Then Control takes the according action.
     * @param e The ActionEvent that was thrown by the view
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JButton){
            JButton source = (JButton) e.getSource();
            
            //Login Button
            if(source.getName().equals(ViewUtils.BUTTON_NAME_LOGIN)){ //static String; rather use name
                if(isLoggedIn()){ 
                    view.showErrorMessage("You are already logged in.", "Already logged in");
                }
                else{
                    try{
//                          if(this.login("MaxiW", "Thunderstrike1")){
                        if(this.login(view.getUsername(), view.getPassword())){
                            this.buildAAS();
                            Vector <String> apiNames = aas.getAPINames();
                            Vector <String> apiTitles = aas.getAPITitles();
                            apiNames.add(0, ViewUtils.EMPTY_DROPDOWN);
                            apiTitles.add(0, ViewUtils.EMPTY_DROPDOWN);
                            view.setAPIDropdownMenu(apiNames, apiTitles);
                            this.selectedAPI = null; //For relogin, we need to set this back to null
                            view.setInputPanel(null);
                            view.setOutputPanel(null);
                        }
                    }
                    catch(Exception exc){
                        view.showErrorMessage(exc.getMessage(), "Login Failed!");
                    }
                }
            }
            //Go Button
            else if(source.getName().equals(ViewUtils.BUTTON_NAME_GO)){
                try{
                    if(ModelUtils.DO_BATCH_JOB){
                        Set<InputConfigurationChangeable> inputConfigs = getSetOfInputConfigurationFromUser();
                        if(inputConfigs.size()>1){
                            Vector<InputConfigurationChangeable> inputConfsSorted = new Vector<InputConfigurationChangeable>(); //needed, to start the requests after asking the general "Files already exist" question
                            Vector<OutputConfiguration> outputConfsSorted = new Vector<OutputConfiguration>();
                            int existCounter = 0; //counts how many files already exist
                            
                            for(InputConfigurationChangeable aConfig: inputConfigs){
                                OutputConfiguration outputConf = getOutputConfigurationFromUser();
                                if(!outputConf.getDoAppend()){ //if we do not want to append, make all filenames different; if we do want to concatenate, let all filenames remain the same
                                    outputConf.appendToDestinationFile(aConfig.getPathOfMultivaluedParameters());
                                }
                                inputConfsSorted.add(aConfig);
                                outputConfsSorted.add(outputConf);
                                if(new File(outputConf.getDestinationFile()).exists()){
                                    existCounter++;
                                }
                            }
                               
                            if(existCounter > 1){ //if i would ask more than once
                                String[] options = new String[] {"Overwrite all", "Ask me for each request", "Cancel all requests"};
                                int userResponse = JOptionPane.showOptionDialog(view, 
                                        "You have issued " + inputConfigs.size() + " requests, but " + existCounter + " of the destination files already exist.", 
                                        existCounter + " files already exists", 
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE ,null, options, options[0]);
                                if(userResponse == 0){ //overwrite all
                                    for(int i = 0; i<inputConfsSorted.size();i++){
                                        outputConfsSorted.get(i).setAskedDoOverwrite(true);
                                    }
                                }
                                else if(userResponse == 1){ //ask me for each request
                                    //do nothing, just continue and start all requests as usual
                                }
                                else{ //cancel
                                    return;
                                }
                            }
                            
                            //finally start requests
                            for(int i = 0; i<inputConfsSorted.size();i++){
                                startRequest(inputConfsSorted.get(i), outputConfsSorted.get(i));
                            }
                            
                        }
                        else{
                            startRequest(inputConfigs.iterator().next(),getOutputConfigurationFromUser());
                        } 
                    }
                    else{
                        InputConfigurationChangeable inputConf = getSingleInputConfigurationFromUser();
                        if(inputConf!=null){
                            startRequest(inputConf,getOutputConfigurationFromUser());
                        }
                    }
                    
                    
                }
                catch(InvalidInputException exc){
                    notifyInputError(exc);
                }
            }
        }
        //ApiSelect
        else if (e.getSource() instanceof JComboBox){
            if(e.getActionCommand().equals(ViewUtils.EMPTY_DROPDOWN)){
                this.selectedAPI = null;
                view.setAPIHelpButton("Click on the dropdown menu to select an API.");
                view.setInputPanel(null);
                view.setOutputPanel(null);
            }
            else{
                try{
                    selectedAPI = aas.getAPIfromName(e.getActionCommand());
                    view.setAPIHelpButton(selectedAPI.getApiDescription());
                    view.setInputPanel(selectedAPI.getInputparameters());
                    view.setOutputPanel(selectedAPI.getOutputcolumns());
                }
                catch(InvalidInputException iie){
                    view.showErrorMessage(iie.getMessage() + "\nContact a programmer. This error should not be able to occur.", "How did you do this? Invalid APIName!");
                }
            }
        }
        //Textfield validate
        else if (e.getSource() instanceof JTextField){
            if(e.getActionCommand().contains("Validate")){
                String[] actionCommand = e.getActionCommand().split(":");
                if(actionCommand[0].equals("Validate") && actionCommand[1].equals("ID") && actionCommand[3].equals("Value")){
                    int id = Integer.parseInt(actionCommand[2]);
                    String value="";
                    try{
                        value = actionCommand[4];
                    }
                    catch(IndexOutOfBoundsException exc){
                        if(this.selectedAPI.getInputparameters().get(id).getType().contains("String")){
                            view.showInputEmpty(id);
                        }
                        else{
                            view.showInputWrong(id, new InvalidInputException(this.selectedAPI.getInputparameters().get(id).getName(), "Nothing", this.selectedAPI.getInputparameters().get(id).getType()));
                        }
                    }
                        try{
                            this.selectedAPI.getInputparameters().get(id).validate(value);
                            view.showInputValid(id);
                        }
                        catch(InvalidInputException iie){
                            view.showInputWrong(id, iie);
                        }
                }
                else{
                    view.showErrorMessage("Control.actionPerformed() has an unexplainable actionEvent. Most probably the validating of input parameters is broken.", "Weird action event.");
                }
            }
        }
        //Menus
        else if(e.getSource() instanceof JMenuItem){
            JMenuItem source = (JMenuItem) e.getSource();
            switch(ViewUtils.MenuNames.stringToEnum(source.getName())){
                case MENU_NAME_LOGOUT:
                    logout();
                    break;
                case MENU_NAME_QUIT:
                    if(notifyUser("Do you really want to quit?", "Quit programm?")){
                        System.exit(0);
                    }
                    break;
                case MENU_NAME_ABOUT:
                    view.showInformationMessage(ViewUtils.HELPTEXT, "Help");
                    break;
                case MENU_NAME_LINK:
                    view.showInformationMessage(ViewUtils.LINKTEXT, "To our website");
                    break;
                default:
                    view.showErrorMessage("Unknown menu item. Contact a programmer, this should not happen.", "Error in Menubar");
            }
        }
    }
    
    //Logic enacting methods
    
    /**
     * This method tries to login to ProteomicsDB, using the username and the password given; if the login is successful, it updates the view and returns true; otherwise it throws an Exception
     * @param username The username the user has specified
     * @param password The password the user has specified
     * @return True, if the login was successful
     * @throws Exception If the login failed, throws the Exception that made it fail
     */
    private boolean login (String username, String password) throws Exception{
        SingletonLoginHandler.getInstance(username, password);
        view.setLoggedInTrue();
//        System.out.println("Logged in");
        return true;    
    }

    /**
     * Returns true, if the user is logged in (so if the {@link SingletonLoginHandler} has an instance, and false otherwise.
     * @return True, if the user is logged in; false otherwise.
     */
    private boolean isLoggedIn(){ 
        return SingletonLoginHandler.hasInstance();
    }    
        
    /**
     * Sends to requests to ProteomicsDB to request the meta information, and then builds the {@link AllAPIsSpecification} out of this information.
     */
    private void buildAAS(){
        Intern InputParameters = new RequestHandler(ModelUtils.INITIALISE_INPUT_PARAMS_LINK, threadPool).request();
        Intern OutputColumns = new RequestHandler(ModelUtils.INITIALISE_OUTPUT_COLS_LINK, threadPool).request();
                
        this.aas = AllAPIsSpecification.buildSpecification(InputParameters, OutputColumns); 
    }
    
    /**
     * This method builds an {@link InputConfigurationChangeable} out of the user input specified in the view. 
     * It is used only if {@link ModelUtils#DO_BATCH_JOB} is false, so if a single request shall be sent.
     * @return The {@link InputConfigurationChangeable} the user has specified.
     * @throws InvalidInputException If the ID of some input parameter the user has checked does not belong to the selected API; this can never happen when using the view included in the Request Tool
     */
    private InputConfigurationChangeable getSingleInputConfigurationFromUser() throws InvalidInputException{
        InputConfigurationChangeable inputConf = new InputConfigurationChangeable(selectedAPI);
        SortedSet<String> checkedOutput = view.getCheckedOutput();
        Map<Integer,String> userInput = view.getUserInput();
        for(String outputName: checkedOutput){
            inputConf.outputChecked(outputName);
        }
        boolean hasNotBeenAskedYet = true;
        for(Integer key: userInput.keySet()){
            if(userInput.get(key).contains(ModelUtils.BATCH_JOB_SEPARATOR) && hasNotBeenAskedYet && !notifyUser("You use the badge job separator in the Input Parameter " + selectedAPI.getSingleInputPar(key).getName() + ", but you have disallowed badge jobs.\nDo you want to send this request regardless?", null)){
                return null;
            }
            inputConf.inputHappened(userInput.get(key), key);
            
        }
        return inputConf;
    }
    
    /**
     * Builds a set of all {@link InputConfigurationChangeable}, that the user has specified, by getting the values he defined out of the view. This method is only called if {@link ModelUtils#DO_BATCH_JOB} is true.
     * It calls {@link #buildSetOfInputConfigurations(RequestTool.Model.Description.InputConfigurationChangeable, java.util.Map, java.util.Set)}
     * @return A set of all the InputConfigurations the user has specified
     * @throws InvalidInputException If the ID of some input parameter the user has checked does not belong to the selected API; this can never happen when using the view included in the Request Tool
     */
    private Set<InputConfigurationChangeable> getSetOfInputConfigurationFromUser() throws InvalidInputException{
        InputConfigurationChangeable inputConf = new InputConfigurationChangeable(selectedAPI);
        SortedSet<String> checkedOutput = view.getCheckedOutput();
        Map<Integer,String> userInput = view.getUserInput();
        for(String outputName: checkedOutput){
            inputConf.outputChecked(outputName);
        }
        Set<InputConfigurationChangeable> result = new HashSet<InputConfigurationChangeable>();
        buildSetOfInputConfigurations(inputConf, userInput, result);
        return result;
    }
    
    /**
     * This method is called by {@link #getSetOfInputConfigurationFromUser()} to construct all InputConfigurations.
     * The method constructs an {@link InputConfigurationChangeable} for each combination of multivalued parameters using recursion, 
     * and sets the attribute {@link InputConfigurationChangeable#pathOfMultivaluedParameters} to indicate, which value it has taken for which parameter.
     * The resulting set is then contained in the set result, that is given as a parameter to the method.
     * @param inputConf The InputConfiguration containing all values that were added up to this point
     * @param userInput A Map containing all values the user has specified, that were not yet added to the configuration.
     * @param result The set where all finished InputConfiguration lie in.
     * @throws InvalidInputException If the ID of some input parameter the user has checked does not belong to the selected API; this can never happen when using the view included in the Request Tool
     */
    private void buildSetOfInputConfigurations(InputConfigurationChangeable inputConf, Map<Integer,String> userInput, Set<InputConfigurationChangeable> result) throws InvalidInputException{
        //Base Case
        if(userInput.isEmpty()){
            result.add(inputConf);
            return;
        }
        
        //get one element out of the userInput; remove it from the Map to eventually reach the base case
        Integer key = userInput.keySet().iterator().next();
        String input = userInput.get(key);
        userInput.remove(key);
        
        //if there is only one value, just let input happen for that value, and continue
        if(!input.contains(ModelUtils.BATCH_JOB_SEPARATOR)){
            inputConf.inputHappened(input, key);
            buildSetOfInputConfigurations(inputConf, userInput, result);
        }
        else{
            //split the value according to the separator
            String[] values = input.split(ModelUtils.BATCH_JOB_SEPARATOR);
            //for each value v make an InputConf that has the same values as the one given to this method, and let that v be input
            for (int i = 0; i<values.length; i++){
                InputConfigurationChangeable newConf = new InputConfigurationChangeable(inputConf);
                newConf.inputHappened(values[i], key);
                newConf.addValueToPath(values[i]);
                //call buildSetOfInputConfigurations again, to input the remaining values
                buildSetOfInputConfigurations(newConf, new HashMap<Integer,String>(userInput), result);
            }
        }
        
    }
    
    /**
     * Constructs the {@link OutputConfiguration} the user has specified, by getting the values he defined out of the view.
     * @return The {@link OutputConfiguration} the user has specified
     * @throws InvalidInputException If the user has specified an invalid value for a settings parameter
     */
    private OutputConfiguration getOutputConfigurationFromUser() throws InvalidInputException{
        OutputConfiguration outputConf = new OutputConfiguration(view.getFileName(), view.getOutputType());
        view.addAllSettingsValues(outputConf);
        return outputConf;
    }
    
    /**
     * Starts a request by constructing a {@link RunnableRequestHandler} out of the given {@link InputConfigurationValidated} and the {@link OutputConfiguration}, 
     * adding it to the {@link RequestThreadPool} and the {@link ProgressPanel}, and then executing it.
     * @param inputConf The {@link InputConfigurationValidated} specifying the request to start
     * @param outputConf The {@link OutputConfiguration} specifying how to handle the received data
     */
    private void startRequest(InputConfigurationChangeable inputConf, OutputConfiguration outputConf){ 
        try{
            if(!outputConf.validate(this)){
                return;
            }
            InputConfigurationValidated validInputConf = inputConf.validateAll();
            RunnableRequestHandler requestHandler = new RunnableRequestHandler(validInputConf, outputConf, threadPool);
            view.addRequestToProgressPanel(requestHandler);
            threadPool.execute(requestHandler);
        }
        catch(Exception exc){ 
            notifyInputError(exc);
        }
    }
    
    /**
     * This method is used to include input errors in the {@link ProgressPanel}. 
     * If the {@link InputConfiguration} is invalid, no {@link RunnableRequestHandler} can be constructed out of it and added to the {@link ProgressPanel}.
     * This is why this method constructs a dummy {@link RunnableRequestHandler}, to display the input error that it is given.
     * It accomplishes this by settings the state of the dummy {@link RunnableRequestHandler} to {@link RequestEvent#INPUT_ERROR} and including the given Exception in that {@link RequestEvent}.
     * @param exc The Exception describing the input error that shall be displayed in the {@link ProgressPanel}
     */
    private void notifyInputError(Exception exc){
        RunnableRequestHandler requestHandler = RunnableRequestHandler.getRunnableRequestHandlerForErrorMessage(new InputConfigurationValidated(selectedAPI, new TreeSet<String>(), new HashMap<Integer,String>()), threadPool);
        view.addRequestToProgressPanel(requestHandler);
        RequestEvent.INPUT_ERROR.setException(exc);
        RequestEvent.INPUT_ERROR.setMessage(exc.getMessage());
        requestHandler.changeState(RequestEvent.INPUT_ERROR);
    }

    /**
     * Shows the user a JOptionPane with the message and two button yes and no, and returns his selection as a boolean flag.
     * @param message The message to display to the user
     * @param title The title of the message
     * @return True, if the user selected yes, false otherwise
     */
    public boolean notifyUser(String message, String title){ 
        return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this.view, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
    
    /**
     * Resets the {@link SingletonLoginHandler} and the view, to log out the user.
     */
    private void logout() {
        try{
            SingletonLoginHandler.reset();
            view.setLoggedInFalse();
        }
        catch(NullPointerException exc){
            view.showErrorMessage("You can not log out when not being logged in", "Not logged in");
        }
    } 
}
