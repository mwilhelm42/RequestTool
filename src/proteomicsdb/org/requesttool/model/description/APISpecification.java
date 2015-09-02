package proteomicsdb.org.requesttool.model.description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The second level of the Description package.
 * One APISpecification specifies the needed input parameters and available output columns of one API that can be requested by the user.
 * It also contains several informations about the API, i.d. ID, name, title, procedure-name, description and example URL.
 * @author Maxi
 * @version 1.0
 */
public class APISpecification {

    private int apiFunctionId;
    private String procedureName, apiName, apiTitle, apiDescription, apiExampleURL;
    private List<OutputColSpec> outputcolumns = new ArrayList<OutputColSpec>();
    private Map<Integer, InputParSpec> inputparameters = new HashMap<Integer, InputParSpec>();
    
    /**
     * Constructs an APISpecification from a LinkedHashMap that contains all information on the attributes of the API.
     * @param aLinkedHashmap A LinkedHashMap containing the attributes of the APISpecification, namely API_FUNCTION_ID, PROCEDURE_NAME, API_NAME, API_TITLE, API_DESCRIPTION, API_EXAMPLE_URL
     */
    public APISpecification(LinkedHashMap<String,String> aLinkedHashmap){
        try{
            this.apiFunctionId = Integer.parseInt(aLinkedHashmap.get("API_FUNCTION_ID"));
            this.procedureName = aLinkedHashmap.get("PROCEDURE_NAME");
            this.apiName = aLinkedHashmap.get("API_NAME");
            this.apiTitle = aLinkedHashmap.get("API_TITLE");
            this.apiDescription = aLinkedHashmap.get("API_DESCRIPTION");
            this.apiExampleURL = aLinkedHashmap.get("API_EXAMPLE_URL");
        }
        catch(NumberFormatException exc){
            System.err.println("This should never happen: API_FUNCTION_ID not a number in constructor of APISpecification."); 
            exc.printStackTrace();
        }
    }
    
    /**
     * Adds an output column to the List of output columns this APISpecification has. 
     * @param outputDescr a LinkedHashMap containing the attributes OUTPUT_NAME, OUTPUT_DESCRIPTION and OUTPUT_TYPE of the output column to add.
     */
    public void addOutput(LinkedHashMap<String,String> outputDescr){
        outputcolumns.add(new OutputColSpec(outputDescr.get("OUTPUT_NAME"), outputDescr.get("OUTPUT_DESCRIPTION"), outputDescr.get("OUTPUT_TYPE"), outputDescr.get("OUTPUT_EXAMPLE")));
    }
    /**
     * Adds an input parameter to the Map of input parameters this APISpecification has.
     * @param inputDescr a LinkedHashMap containing the attributes API_INPUT_ID, INPUT_NAME, INPUT_DESCRIPTION, INPUT_TYPE and INPUT_EXAMPLE
     */
    public void addInput(LinkedHashMap<String,String> inputDescr){
        try{
            int idOfInput = Integer.parseInt(inputDescr.get("API_INPUT_ID"));
            if(inputparameters.containsKey(idOfInput)){
                inputparameters.get(idOfInput).addPredefinedValue(inputDescr);
            }
            else{
                inputparameters.put(idOfInput, new InputParSpec(Integer.parseInt(inputDescr.get("API_INPUT_ID")), inputDescr.get("INPUT_NAME"), inputDescr.get("INPUT_DESCRIPTION"), inputDescr.get("INPUT_TYPE"), inputDescr.get("INPUT_EXAMPLE")));
                inputparameters.get(idOfInput).addPredefinedValue(inputDescr);
            }
        }
        catch(NumberFormatException exc){
            System.err.println("This should never happen. API_INPUT_ID not an Integer in APISpecification.addInput");
            exc.printStackTrace();
        }
    }

    
    //Getter

    /**
     * Returns the ID of this API
     * @return The ID of this API
     */
    public int getApiFunctionId() {
        return apiFunctionId;
    }

    /**
     * Returns the procedure name of this API
     * @return The procedure name of this API
     */
    public String getProcedureName() {
        return procedureName;
    }

    /**
     * Returns the name of this API
     * @return The name of this API
     */
    public String getApiName() {
        return apiName;
    }

    /**
     * Returns the title of this API
     * @return The title of this API
     */
    public String getApiTitle() {
        return apiTitle;
    }

    /**
     * Returns the description of this API
     * @return The description of this API
     */
    public String getApiDescription() {
        return apiDescription;
    }

    /**
     * Returns the example URL of this API
     * @return The example URL of this API
     */
    public String getApiExampleURL() {
        return apiExampleURL;
    }

    /**
     * Returns the List of the output columns of this API
     * @return The List of the output columns of this API
     */
    public List<OutputColSpec> getOutputcolumns() {
        return outputcolumns;
    }

    /**
     * Returns the Map of the input parameters of this API
     * @return The Map of the input parameters of this API
     */
    public Map<Integer, InputParSpec> getInputparameters() {
        return inputparameters;
    }
    
    
    //Interesting getters
    
    /**
     * Returns a set of all the names of the output columns of this API
     * @return A set of all the names of the output columns of this API
     */
    public Set<String> getAllOutputColumnsByName (){
        Set<String> result = new TreeSet<String>();
        for (int i = 0; i < this.outputcolumns.size(); i++){
            result.add(this.outputcolumns.get(i).getName());
        }
        return result;
    }
    
    /**
     * Returns a set of all the names of the input parameters of this API
     * @return A set of all the names of the input parameters of this API
     */
    public Set<String> getAllInputParametersByName(){
        Set<String> result = new TreeSet<String>();
        for (int key : this.inputparameters.keySet()){
            result.add(this.inputparameters.get(key).getName());
        }
        return result;
    }
    
    /**
     * Returns a set of all the IDs of the input parameters of this API
     * @return A set of all the IDs of the input parameters of this API
     */
    public Set<Integer> getInputParamIDs(){
        return this.inputparameters.keySet();
    }
    
    /**
     * Returns the {@link InputParSpec} of the input parameter specified by the id.
     * @param id The ID of the {@link InputParSpec} to return
     * @return The {@link InputParSpec} of the input parameter specified by the id
     */
    public InputParSpec getSingleInputPar(int id){
        return this.inputparameters.get(id);
    }
    
}
