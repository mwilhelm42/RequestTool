package proteomicsdb.org.requesttool.model.writing;

import java.io.PrintWriter;
import java.util.HashMap;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.Intern;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;

/**
 * One of the specific {@link Writer}s of the Request Tool. It writes the data in the format of JSON.
 * @author Maxi
 * @version 1.0
 */
public class JSONWriter extends Writer{

    /**
     * Constructs a JSONWriter
     */
    public JSONWriter() {
        type="json";
        specificSettings = new HashMap<String, InputParSpec>();
        this.configureSpecificSettings();
    }

    /**
     * Since the Writer has no specificSettings, does nothing; needs to be implemented nontheless, because it is an abstract method of {@link Writer}
     */
    @Override
    protected void configureSpecificSettings(){
        //nothing
    }
    
    /**
     * Returns a new JSONWriter. This is used by the {@link WriterFactory} to create new {@link Writer}s for each task.
     * @return a new JSONWriter
     */
    @Override
    public Writer getNewWriter() {
        return new JSONWriter();
    }

    /**
     * Returns the file ending of files this Writer writes to. In this case it is txt, because .json is not understood by many programs.
     * @return "txt", the file ending of files the JSONWriter writes to
     */
    @Override
    public String getSuffix() {
        return "txt";
    }

    
    /**
     * Converts the input to a formatted JSON and writes it to the file that the PrintWriter has already opened.
     * Does not handle appending separately; appending a JSON will result in a file with two (or more, if appending more often) objects with the attribute results, which has as value an array of the data records.
     * @param configuration The {@link OutputConfiguration} specifying how the data shall be written. Not needed by this Writer.
     * @param input The data to be written
     * @param writer The already opened {@link PrintWriter} to the destination file
     */
    @Override
    public void actuallyWrite(OutputConfiguration configuration, Intern input, PrintWriter writer) {
        if(input.isEmpty()){
            return;
        }
        
        writer.write("{results: [\n");
        for(int i = 0; i<input.size()-1; i++){
            writer.write(input.get(i).toString()+",");
        }

        writer.write(input.get(input.size()-1).toString() + "]}");
        
    }
    
    
    
}
