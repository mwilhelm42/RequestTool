package proteomicsdb.org.requesttool.model.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * An Exception used to collect multiple Exceptions, and then throw and display them all at once.
 * @author Maxi
 * @version 1.0
 */
public class CollectedException extends Exception {
    
    /**
     * A List containing all Exceptions that were collected
     */
    private List<Exception> exceptions; 
    
    /**
     * Constructs a CollectedException, initializes the List as ArrayList.
     */
    public CollectedException(){
        exceptions = new ArrayList<Exception>();
    }
    
    /**
     * Adds an Exception to the List of exceptions.
     * @param e The Exception to be added
     */
    public void addException(Exception e){
        exceptions.add(e);
    }
    
    /**
     * Returns a boolean flag indicating whether this CollectedException already contains Exceptions.
     * @return A boolean flag indicating whether this CollectedException already contains Exceptions
     */
    public boolean isEmpty(){
        return exceptions.isEmpty();
    }
    
    /**
     * Returns a String containing the messages of all Exceptions contained in this CollectedException, separated by linebreaks, preceded with "There were several errors: \n".
     * @return A String containing the messages of all Exceptions contained in this CollectedException, separated by linebreaks, preceded with "There were several errors: \n"
     */
    @Override
    public String getMessage(){
        String result = "There were several errors: \n";
        
        for (int i = 0; i<exceptions.size(); i++){
            result += exceptions.get(i).getMessage() + "\n";
        }
        
        return result;
    }
    
}
