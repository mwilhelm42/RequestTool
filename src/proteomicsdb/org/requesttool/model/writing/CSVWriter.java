package proteomicsdb.org.requesttool.model.writing;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.Intern;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;

/**
 * One of the specific {@link Writer}s of the Request Tool. It writes the data in the format of CSV.
 * @author Maxi
 * @version 1.0
 */
public class CSVWriter extends Writer{

    /**
     * Constructs a CSVWriter; calls configureSpecificSettings.
     */
    public CSVWriter() {
        type="csv";
        specificSettings = new HashMap<String, InputParSpec>();
        this.configureSpecificSettings();
    }

    /**
     * Defines, that a separator has to be specified for this writer, which has one of three predefined values.
     */
    @Override
    protected void configureSpecificSettings(){
        InputParSpec setting1 = new InputParSpec(-1, "Separator", "Which charakter is used to separate entries", "String", ",");
        setting1.addPredefinedValue("','", ",", "Comma");
        setting1.addPredefinedValue("';'", ";", "Semikolon");
        setting1.addPredefinedValue("'\t'", "\t", "Tab");
        specificSettings.put(setting1.getName(), setting1);
    }
    
    /**
     * Returns a new CSVWriter. This is used by the {@link WriterFactory} to create new {@link Writer}s for each task.
     * @return a new CSVWriter
     */
    @Override
    public Writer getNewWriter() {
        return new CSVWriter();
    }

    /**
     * Returns the file ending of files this Writer writes to. In this case it is csv.
     * @return "csv", the file ending of files the CSVWriter writes to
     */
    @Override
    public String getSuffix() {
        return "csv";
    }

    /**
     * Converts the input to CSV and writes it to the file that the PrintWriter has already opened.
     * If the data shall be appended, it does not write the header again, but only the data records.
     * @param configuration The {@link OutputConfiguration} specifying how the data shall be written. Specifies the separator.
     * @param input The data to be written
     * @param writer The already opened {@link PrintWriter} to the destination file
     */
    @Override
    public void actuallyWrite(OutputConfiguration configuration, Intern input, PrintWriter writer){
        String separator = configuration.getSettingsValues().get("Separator");
        if(input.isEmpty()){
            return;
        }
        
        List<String> keys = input.getKeys();
        
        //Header
        if(!configuration.getDoAppend()){ //write header only if we are not appending
            for(int j = 0; j<keys.size()-1; j++){ 
                writer.write(keys.get(j) + separator);
            }
            writer.write(keys.get(keys.size()-1) + "\n");//last entry of a record must not end with a separator. Since I can't modify anything after writing out, I have to check before writing out
        }
        //Data
        for(int i = 0; i<input.size(); i++){
            for(int j = 0; j<keys.size(); j++){
                String nextThingToWrite = input.get(i).get(keys.get(j));
                if(nextThingToWrite == null){
                    nextThingToWrite = "";
                }
                else if(nextThingToWrite.contains(separator)){ //output may contain separator; there is no better way to handle this, than to just check every output; sadly...
                    //TODO: Double quote to escape quotes
                    nextThingToWrite = "\"" + nextThingToWrite + "\""; //if it contains a separator, the way to handle this defined in RFC4180 is to enclose the data in ""
                }
                //i could also check for all the other separators and " and so on; see RFC for what I need to check
                if(j<keys.size()-1){ //last entry of a record must not end with a separator. Since I can't modify anything after writing out, I have to check before writing out
                    writer.write(nextThingToWrite + separator);
                }
                else{
                    writer.write(nextThingToWrite);
                }
            }
            writer.write("\n");
        }
        
    } 
}
    
    
    
    
//    public void writeToConsole(OutputConfiguration configuration, Intern input) {
//        System.out.println(this.getCSVString(configuration, input));
//    }
//    
//    public String getCSVString(OutputConfiguration configuration, Intern input){ //can probably be deleted, for it is very slow.
//        String result = "";
//        String separator = configuration.getSettingsValues().get("Separator");
//        if(this.validate(configuration)){
//            try{
//                List<String> keys = input.getKeys();
//                for(int j = 0; j<keys.size(); j++){
//                    result += keys.get(j) + separator;
//                }
//                for(int i = 0; i<input.size(); i++){
//                    result += "\n";
//                    for(int j = 0; j<keys.size(); j++){
//                        result += input.get(i).getString(keys.get(j)) + separator;
//                    }
//                }
//            }
//            catch(JSONException e){
//                System.err.println("Writing failed. JSONException.");
//            }
//        }
//        else{
//            //TODO: throw something
//            System.err.println("Writing did not work, since OutputConfiguration was invalid");
//            return "ERROR";
//        }
//        
//        return result;
//    }
    
