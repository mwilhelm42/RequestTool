package proteomicsdb.org.requesttool.model.description;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import proteomicsdb.org.requesttool.model.ModelUtils;
import proteomicsdb.org.requesttool.view.ViewUtils;

/**
 * This class is used to build the link that specifies the request to ProteomicsDB. 
 * An InputConfigurationValidated is an unchangeable InputConfiguration that results from the method {@link InputConfigurationChangeable#validateAll()}.
 * @author Maxi
 * @version 1.0
 */
public class InputConfigurationValidated extends InputConfiguration{
    
    /**
     * Constructs an InputConfigurationValidated, using the given values.
     * @param theCurrentAPI The API this InputConfiguration belongs to
     * @param outputCols The Set of selected output columns
     * @param inputParams The Map of the InputParameter values specified by the user
     */
    public InputConfigurationValidated(APISpecification theCurrentAPI, SortedSet<String> outputCols, Map<Integer,String> inputParams){
        this.currentAPI = theCurrentAPI;
        
        //ensure we have deep copies, so changing something in the InputConfigurationChangeable does not change our validated Conf
        this.inputParamsValues = new HashMap<Integer, String>();
        this.inputParamsValues.putAll(inputParams);
        this.outputColsChecked = new TreeSet<String>();
        this.outputColsChecked.addAll(outputCols);
    }
    
    /**
     * Returns a String containg all selected output columns separated by commas.
     * @return A String containg all selected output columns separated by commas
     */
    private String getOutputColumnsString(){ 
        String result = "";
        
        for (String name: this.outputColsChecked){
                result += name + ",";
        }
        
        result = result.substring(0,result.length()-1); //remove the last comma
        return result;
        
    }
    
    /**
     * Returns a String, which contains all input parameter names of the API this InputConfiguration belongs to with their respective values.
     * The String matches the format which is expected from ProteomicsDB for the input parameters
     * @return A String of all input parameters with their respective values
     */
    private String getInputNameAndValueString(){ //also adds the ' and the ,; TODO: Better typecheck
        String result = "";
        
        for (int id: this.inputParamsValues.keySet()){
            if(this.currentAPI.getSingleInputPar(id).getType().contains("String")){
                if(this.inputParamsValues.get(id).contains(" ")){
                    this.inputParamsValues.put(id, this.inputParamsValues.get(id).replace(" ", "%20"));//because space is not recognized by ProteomicsDB
                }
                result = result + this.currentAPI.getSingleInputPar(id).getName() + "='" + this.inputParamsValues.get(id) + "',";
            }
            else{
                result = result + this.currentAPI.getSingleInputPar(id).getName() + "=" + this.inputParamsValues.get(id) + ",";
            }
        }
        
        if(!result.equals("")){
            result = result.substring(0,result.length()-1); //remove the last comma
        }
        return result;
        
        
    }

    /**
     * Returns the link that is used to identify the query to ProteomicsDB, by concatenating the static parts of the link with the results of the methods {@link #getInputNameAndValueString()} abd {@link #getOutputColumnsString()}
     * @return The link that is used to identify the query to ProteomicsDB
     */
    public String buildLink(){ 
        
        if(this.inputParamsValues.isEmpty()){
            return(ModelUtils.FIRST_PART_OF_THE_LINK + this.currentAPI.getApiName() +
                    ".xsodata/" + this.currentAPI.getProcedureName() +
                    "?$select=" + getOutputColumnsString()  +
                    "&$format=json");
        }
        
        return (ModelUtils.FIRST_PART_OF_THE_LINK + this.currentAPI.getApiName() + 
            ".xsodata/InputParams(" + getInputNameAndValueString() + 
            ")/Results?$select=" + getOutputColumnsString()  +
            "&$format=json");
    }
    
    /**
     * Returns the title of the API this InputConfiguration belongs to.
     * @return The title of the API this InputConfiguration belongs to
     */
    public String getTitleOfRequestedAPI(){
        if(this.currentAPI == null){
            return ViewUtils.EMPTY_DROPDOWN;
        }
        return this.currentAPI.getApiTitle();
    }
}
