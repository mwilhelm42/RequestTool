package proteomicsdb.org.requesttool.model.description;

import java.util.HashMap;
import java.util.TreeSet;
import proteomicsdb.org.requesttool.controller.Control;
import proteomicsdb.org.requesttool.model.ModelUtils;
import proteomicsdb.org.requesttool.model.exceptions.CollectedException;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;

/**
 * This class implements the abstract class {@link InputConfiguration}.
 * It is changeable, because user values can still be added to it.
 * @author Maxi
 * @version 1.0
 */
public class InputConfigurationChangeable extends InputConfiguration{
    
    /**
     * This is used by the method {@link Control#buildSetOfInputConfigurations(RequestTool.Model.Description.InputConfigurationChangeable, java.util.Map, java.util.Set)}
     */
    private String pathOfMultivaluedParameters;
    
    /**
     * Constructs an InputConfigurationChangeable, by setting the API it belongs to and initializing the Map for the input parameters and the set for the output columns.
     * @param apispec The API this InputConfiguration belongs to
     */
    public InputConfigurationChangeable (final APISpecification apispec){
        this.currentAPI = apispec;
        this.inputParamsValues = new HashMap<Integer, String>(); 
        this.outputColsChecked = new TreeSet<String>();
        this.pathOfMultivaluedParameters = "";
    }
    
    /**
     * Constructs an InputConfigurationChangeable as a copy of another object of that type.
     * It uses deep copies to ensure that both configuration can be altered separately.
     * @param anotherInputConf The InputConfigurationChangeable, that the new one is a copy of
     */
    public InputConfigurationChangeable (final InputConfigurationChangeable anotherInputConf){
        this.currentAPI = anotherInputConf.currentAPI;
        this.inputParamsValues = new HashMap<Integer, String> (anotherInputConf.inputParamsValues);
        this.outputColsChecked = new TreeSet<String> (anotherInputConf.outputColsChecked);
        this.pathOfMultivaluedParameters = anotherInputConf.pathOfMultivaluedParameters; //deep copy?
    }
    
    /**
     * Adds a value for an input parameter that is specified by its ID.
     * Checks, that if batch jobs are enabled, no batch job separator is in the value, because one InputConfiguration will start one request, and having a separator here would mean an error has occurred.
     * @param value The value the user has input
     * @param inputParamID The ID of the parameter that the value is specifying
     * @throws InvalidInputException If the ID does not belong to an input parameter of the API this InputConfiguration belongs to
     */
    public void inputHappened(String value, int inputParamID) throws InvalidInputException{
        //check, whether the InputID is in the current API; error should never occur, but to be safe
        if(ModelUtils.DO_BATCH_JOB && value.contains(ModelUtils.BATCH_JOB_SEPARATOR)){
            throw new InvalidInputException(this.currentAPI.getSingleInputPar(inputParamID).getName(), value, "Input Configuration sees a Badge Job Separator. This should not be able to occur.");
        }
        if(this.currentAPI.getInputParamIDs().contains(inputParamID)){
            this.inputParamsValues.put(inputParamID, value);
        }
        else{
            //this error can only occur, if someone uses his own view
            System.err.println("Invalid InputParamID for Configuration.");
        }
    }
    
    /**
     * Adds an output column to the set of output columns that the user wants to request. 
     * @param outputName The name of the column the user has selected to be requested
     */
    public void outputChecked(String outputName){
        //check, whether the outputName is in the current API; error should never occur, but to be safe
        if(this.currentAPI.getAllOutputColumnsByName().contains(outputName)){
            this.outputColsChecked.add(outputName);
        }
        else{
            //this error can only occur, if someone uses his own view
            System.err.println("Invalid OutputName for Configuration."); 
        }
    }
    
    /**
     * Removes an output column from the set of output columns that the user wants to request. 
     * @param outputName The name of the column the user has selected to not be requested anymore
     */
    public void outputUnchecked(String outputName){
        this.outputColsChecked.remove(outputName);
    }
    
    /**
     * Validates the input parameters calling {@link InputParSpec#validate(java.lang.String)} for each parameter with the value specified by the user, and the output columns by ensuring at least one column is selected.
     * @return A {@link InputConfigurationValidated}, that can then build the link
     * @throws CollectedException Containing all InvalidInputException that occurred during the validation
     * @throws InvalidInputException If no API was selected
     */
    public InputConfigurationValidated validateAll() throws CollectedException, InvalidInputException{ 
        CollectedException collectedException = new CollectedException();
        
        if(this.currentAPI==null){
            throw new InvalidInputException("Selected API", "No API was specified", "Please select something.");
        }
        
        //Validate Input
        for (int id: this.currentAPI.getInputParamIDs()){
            try{
                this.currentAPI.getSingleInputPar(id).validate(this.inputParamsValues.get(id));
            }
            catch(InvalidInputException iie){
                collectedException.addException(iie);
            }
        }
        
        
        //Validate Output
        if(outputColsChecked.isEmpty()){ 
            collectedException.addException(new InvalidInputException("OutputColumns", "Nothing selected", "At least one column selected"));
        }
        
        
        //build InputConfigurationValidates 
        if(collectedException.isEmpty()){
            return new InputConfigurationValidated(currentAPI, outputColsChecked, inputParamsValues);
        }
        //throw exception
        else{
            throw collectedException;
        }
    }

    /**
     * Returns the API this InputConfiguration belongs to.
     * @return The API this InputConfiguration belongs to
     */
    public APISpecification getCurrentAPI() {
        return currentAPI;
    }

    /**
     * Returns the path of multivalued parameters; this is only used by {@link Control#buildSetOfInputConfigurations(RequestTool.Model.Description.InputConfigurationChangeable, java.util.Map, java.util.Set)}.
     * @return The path of multivalued parameters
     */
    public String getPathOfMultivaluedParameters() {
        return pathOfMultivaluedParameters;
    }
    
    /**
     * Adds the value to the path of multivalued parameters; this is only used by {@link Control#buildSetOfInputConfigurations(RequestTool.Model.Description.InputConfigurationChangeable, java.util.Map, java.util.Set)}.
     * @param value The value which is added to the path of multivalued parameters
     */
    public void addValueToPath(String value){
        value = value.replaceAll("[^a-zA-Z0-9]", ""); //i assume that each value contains enough letters and numbers to identify it, so I can just remove all special characters
        this.pathOfMultivaluedParameters += "_" + value;
    }
    
//    public static void main (String[] args){
//        String value = "BTO:2abc";
//        System.out.println(value.replaceAll("[^a-zA-Z0-9]", ""));
//    }
    
    
}
