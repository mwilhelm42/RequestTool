package proteomicsdb.org.requesttool.model.description;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This is the class that is used to store the JSONs from ProteomicsDB in an internal format.
 * These JSONs are structured as an array of data records, where each data record is a set of attribute-value-pairs.
 * This is why the Intern is an ArrayList of LinkedHashMaps: Each entry in the ArrayList corresponds to one data record; 
 * each entry in the LinkedHashMap to one attribute-value-pair.
 * ArrayList and LinkedHashMap are used to ensure the correct ordering.
 * @author Maxi
 * @version 1.0
 */
public class Intern extends ArrayList<LinkedHashMap<String,String>>{
    
    /**
     * Constructs an empty intern.
     */
    public Intern(){
        
    }
    
    /**
     * Adds a new data record to the Intern, that is specified by the LinkedHashMap
     * @param aLinkedHashMap The LinkedHashMap that specifies the data record to be added
     * @return A flag indicating whether the addition was successful
     */
    public boolean add(LinkedHashMap aLinkedHashMap){
        return super.add(aLinkedHashMap); 
    }
    
    /**
     * Returns a List containing the attribute names of the first data record, and hence the attribute names of all data records (since the data from ProteomicsDB is a table, and all data records have the same structure).
     * @return A List containing the attribute names of all data records
     */
    public List<String> getKeys(){
        return new ArrayList(this.get(0).keySet());
    }
    
    /**
     * Converts the Intern to a String by concatenating the results of the toString method of all data records, separating them with linebreaks
     * @return The String representation of the Intern
     */
    @Override
    public String toString(){
        String result = "";
        for(int i = 0; i<size(); i++){
            result = result.concat("\n" + get(i).toString());
        }
        return result;
    }
}
