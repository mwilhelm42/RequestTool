package proteomicsdb.org.requesttool.model.description;

import java.util.HashMap;
import java.util.Vector;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;

/**
 * This class is the top level class of the Description package.
 * The key idea of the description is to have all information about the available APIs stored in a hierarchical structure. 
 * {@link AllAPIsSpecification} should exist only once. It is a HashMap of all {@linkplain APISpecification APISpecifications}
 * @author Maxi
 * @version 1.0
 */
public class AllAPIsSpecification extends HashMap<Integer, APISpecification>{
    
    /**
     * Private, since it does not do anything, and should not be called by anything but {@link #buildSpecification(api.Intern, api.Intern) }
     */
    private AllAPIsSpecification() {
        
    }
    
    /**
     * The method that really does the constructing of the AllAPIsSpecification.
     * It gets two {@link Intern} Objects that contain the input parameters and the output columns of all APIs. 
     * Then it constructs all {@linkplain APISpecification APISpecifications} from that information, and adds them to itself (itself being a HashMap<Integer,APISpecification>)
     * Each {@link APISpecification} is addressable via its API_FUNCTION_ID.
     * @param InpPar An Intern containing all input parameters with their respective API. Should be aquired from the Meta-API(see {@link Utils#INITIALISE_INPUT_PARAMS_LINK}
     * @param OutCol An Intern containing all output columns with their respective API. Should be aquired from the Meta-API(see {@link Utils#INITIALISE_OUTPUT_COLS_LINK}
     * @return The finished AllAPIsSpecification containing all information about the APIs which can be requested with the tool.
     */
    public static AllAPIsSpecification buildSpecification(Intern InpPar, Intern OutCol){
        AllAPIsSpecification result = new AllAPIsSpecification();
        //Output
        for (int i = 0; i<OutCol.size(); i++){
            //check, whether the current entry describes an API already in the Map
            int idOfCurrentEntry = -1;
            try{
                idOfCurrentEntry = Integer.parseInt(OutCol.get(i).get("API_FUNCTION_ID")); 
            }
            catch(NumberFormatException exc){
                System.err.println("This should never occur. API_FUNCTION_ID is not a number in AllApiSpecification.buildSpecification().");
                exc.printStackTrace();
            }

            if(result.containsKey(idOfCurrentEntry)){
                result.get(idOfCurrentEntry).addOutput(OutCol.get(i)); //just add the new Column
            }
            else{ //construct a new APISpec
                APISpecification temp = new APISpecification(OutCol.get(i));
                temp.addOutput(OutCol.get(i));
                result.put(idOfCurrentEntry, temp);
            }
        }
        //Input; all APIs are initialized now, so we do not need to check the IDs, but just add all InputPars; 
        //It is important to first add Output, for if we did it the other way round, there could be APIs which are not yet initialized
        try{
            for(int i = 0; i<InpPar.size(); i++){
                int idOfCurrentEntry = Integer.parseInt(InpPar.get(i).get("API_FUNCTION_ID")); 
                result.get(idOfCurrentEntry).addInput(InpPar.get(i));
            }
        }
        catch(NumberFormatException exc){
            System.err.println("This should never occur. API_FUNCTION_ID is not a number in AllApiSpecification.buildSpecification().");
            exc.printStackTrace();
        }
        return result;
    }
    
    /**
     * Gets the {@link APISpecification} with the API_FUNCTION_ID given to this function as id.
     * Has the normal HashMap behaviour of returning null if the ID is not contained in the AllAPIsSpecification.
     * @param id The API_FUNCTION_ID of the API to get
     * @return The requested {@link APISpecification}
     */
    public APISpecification getAPIfromID (int id){
        return this.get(id);
    }
    
    /**
     * Gets the {@link APISpecification} with the API_NAME given to this function as name. 
     * If the name is not contained in the HashMap, throws an InvalidInputException containing all available names.
     * @param name The name of the API to get
     * @return The requested {@link APISpecification}
     * @throws InvalidInputException If there is no APISpecification with this name. This Exception contains a Vector of all available names.
     */
    public APISpecification getAPIfromName(String name) throws InvalidInputException{
        APISpecification result;
        for(int key: this.keySet()){
                result = this.get(key);
                if(result.getApiName().equals(name)){
                    return result;
                }
            }
        throw new InvalidInputException("API Name", name, "One of these: " + this.getAPINames());
    }
    
    /**
     * Is used for initializing the dropdown menu where the user selects an API.
     * @return A Vector of all the names of the APIs in this AllAPIsSpecification.
     */
    public Vector<String> getAPINames(){
        Vector<String> result = new Vector<String>();
        for(int key: this.keySet()){
            result.add(this.getAPIfromID(key).getApiName());
        }
        return result;
    }
    
    /**
     * Is used for initializing the dropdown menu where the user selects an API.
     * @return A Vector of all the titles of the APIs in this AllAPIsSpecification.
     */
    public Vector<String> getAPITitles(){
        Vector<String> result = new Vector<String>();
        for(int key: this.keySet()){
            result.add(this.getAPIfromID(key).getApiTitle());
        }
        return result;
    }
    
}
