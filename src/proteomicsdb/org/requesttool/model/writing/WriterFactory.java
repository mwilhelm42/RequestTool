package proteomicsdb.org.requesttool.model.writing;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import net.sourceforge.stripes.util.ResolverUtil;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.Intern;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;

/**
 * This class is used for writing the data. It has a List of all possible {@link Writer}s, and generates a new {@link Writer} for each task it is given.
 * @author Maxi
 * @version 1.0
 */
public class WriterFactory {
    
//    private static Set<Class<? extends Writer>> writerClasses; //this would be better, for we would not have objects lying around, but I don't know
                                                                 //how to get to the attributes of the writers without instanciating them
    
    /**
     * The List of all possible {@link Writer}s; it is initialized in the static block, using reflection to find all classes that extend 
     */
    private static List<Writer> allWriters; //TODO: make this hashmap, to check that type is a key
    
    /**
     * This static block finds all Writers (so classes that extend the abstract {@link Writer} in the package api.writingPhase and adds them to the possible Writers the factory knows.
     */ 
    static { 
        ResolverUtil<Writer> resolver = new ResolverUtil<Writer>();
        resolver.findImplementations(Writer.class, "proteomicsdb.org.requesttool.model.writing");
        Set<Class<? extends Writer>> writerClasses = resolver.getClasses();
        allWriters = new ArrayList<Writer>();
        for (Class<? extends Writer> writer : writerClasses) {
            try{
                int a = writer.getModifiers();
                if(!(Modifier.isAbstract(writer.getModifiers()))){ //else the abstract writer is tried to be instanciated and always gives an error
                    register(writer.newInstance());
                }
            }
            catch(Exception e){
                System.err.println("The writer " + writer.toString() + " could not be added to the possible Writers");
                //e.printStackTrace();
            }
        }
    }
    
    /**
     * This is the method that initiates the writing. It generates a new {@link Writer}, and calls its write method.
     * @param outputConf The {@link OutputConfiguration} that describes how the data shall be written.
     * @param data The data to write
     * @throws InvalidInputException Throws the exception that {@link Writer#write(RequestTool.Model.Description.OutputConfiguration, RequestTool.Model.Description.Intern)} has thrown to it.
     */
    public static void write(OutputConfiguration outputConf, Intern data) throws InvalidInputException{
        Writer theWriter = WriterFactory.getWriterFromType(outputConf.getType()).getNewWriter();
        theWriter.write(outputConf, data);
    }
    
    /**
     * Returns the {@link Writer} out of the List of all possible {@link Writer}s, that has the specified type. 
     * Does NOT generate a new {@link Writer}, so the {@link Writer} returned by this should not be modified.
     * @param type The type of the {@link Writer} that shall be returned
     * @return The {@link Writer} with the specified type
     * @throws InvalidInputException If no {@link Writer} of the given type exists
     */
    public static Writer getWriterFromType(String type) throws InvalidInputException{
        for (int i = 0; i<allWriters.size(); i++){
            if(allWriters.get(i).getType().equals(type)){
                return allWriters.get(i);
            }
        }
        throw new InvalidInputException("Output Configuration Type", type, "A type that exists. One of these " + getAllPossibleWritersAsString());
    }
    
    /**
     * Returns a String that contains the types of all possible {@link Writer}s, separated by line breaks, preceded by "Writers: \n".
     * @return A String that contains the types of all possible {@link Writer}s, separated by line breaks, preceded by "Writers: \n"
     */
    public static String getAllPossibleWritersAsString (){
        String result = "Writers: \n";
        for (int i = 0; i<allWriters.size(); i++){
            result += allWriters.get(i).getType() + "\n";
        }
        return result;
    }
    
    /**
     * Returns a Vector containing all possible {@link Writer}s.
     * @return A Vector containing all possible {@link Writer}s
     */
    public static Vector<Writer> getAllPossibleWritersAsVector(){
        Vector<Writer> result = new Vector<Writer>();
        for (int i = 0; i<allWriters.size(); i++){
            result.add(allWriters.get(i));
        }
        return result;
    }
    
    /**
     * Returns the {@link Writer#specificSettings} of the {@link Writer} with the specified type
     * @param type The type of the {@link Writer} whose specificSettings shall be given
     * @return A Map containing the {@link Writer#specificSettings} of the specified {@link Writer}
     * @throws InvalidInputException if no {@link Writer} of the specified type exists
     */
    public static Map<String,InputParSpec> getSpecificSettingFromType(String type) throws InvalidInputException{
        return getWriterFromType(type).getSpecificSettings();
    }
    
    /**
     * Adds a {@link Writer} to List of all possible Writers. Is called only in the static block of the WriterFactory.
     * @param aWriter The {@link Writer} to be added to the List.
     */
    public static void register(Writer aWriter){
        allWriters.add(aWriter);
    }
}
