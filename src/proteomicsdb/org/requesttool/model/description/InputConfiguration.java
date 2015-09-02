/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proteomicsdb.org.requesttool.model.description;

import java.util.Map;
import java.util.SortedSet;

/**
 * This class stores the values the user has specified for the input parameters of the API, and the information which output columns he has selected.
 * It belongs to exactly one API.
 * @author Maxi
 * @version 1.0
 */
public abstract class InputConfiguration {
    
    //gets the information, which API is currently active from the Dropdown menu in the UI, where the user selects the API; 
    protected APISpecification currentAPI;
   
    //the Configuration Object knows all String Inputs the User has given,
    //and can ask the APISpec (or the InputParSpec directly) whether this Input is typecorrectand valid
    protected Map<Integer,String> inputParamsValues;
   
    //it also knows, which OutputCols are wanted
    protected SortedSet<String> outputColsChecked; 
    
}
