package proteomicsdb.org.requesttool.model.description;

import java.util.HashMap;
import java.util.Map;
import proteomicsdb.org.requesttool.controller.Control;
import proteomicsdb.org.requesttool.model.exceptions.CollectedException;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;
import proteomicsdb.org.requesttool.model.writing.Writer;
import proteomicsdb.org.requesttool.model.writing.WriterFactory;

/**
 * This class specifies, how and where the received data shall be written.
 * @author Maxi
 * @version 1.0
 */
public class OutputConfiguration {
    
    private String destinationFile, type;
    /**
     * doAppend is a flag indicating whether the {@link Writer} shall append the result to the destination file
     */
    private boolean doAppend;
    /**
     * askedDoOverwrite indicates, whether the user has already been asked if he wants to overwrite the file
     */
    private boolean askedDoOverwrite;
    private Map<String, String> settingsValues;
    
    /**
     * Constructs an OutputConfiguration, using the given destination flie and type. 
     * The flags doAppend and askedDoOverwrite by default are false.
     * @param aFile The destination file to write to
     * @param aType The type of the {@link Writer}
     */
    public OutputConfiguration(String aFile, String aType){
        this.destinationFile = aFile;
        this.type = aType;
        this.doAppend = false;
        this.askedDoOverwrite = false;
        this.settingsValues = new HashMap<String,String>();
    }

    /**
     * This method validates the OutputConfiguration. Actually it just calls the validate method of the {@link Writer}, which then takes care of the validating
     * @param theControl The controller, which is needed, because in the validating process the user might be asked, if he wants to overwrite the destination file. To ask this question, a reference to the controller is needed. 
     * @return True, if the OutputConfiguration is valid; throws an Exception otherwise
     * @throws CollectedException If there are several invalid settings values for the {@link Writer}
     * @throws InvalidInputException If the destination file or the type are invalid
     */
    public boolean validate(Control theControl) throws CollectedException, InvalidInputException{ //control needed for error handling, if file already exists
        return WriterFactory.getWriterFromType(this.type).validate(this, theControl);
    }
    
    /**
     * Appends the stringToAppend to the destination file, taking care, that the file ending is not touched by this.
     * This method is used by {@link Control} to append the path of multivalued parameters to the destination file.
     * @param stringToAppend The String that is appended to the destination file
     */
    public void appendToDestinationFile(String stringToAppend){
        if(this.destinationFile.endsWith("." + this.type)){
            this.destinationFile = destinationFile.substring(0, destinationFile.lastIndexOf(".")) + stringToAppend + destinationFile.substring(destinationFile.lastIndexOf("."));
        }
        else{
            this.destinationFile += stringToAppend;
        }
    }
    
    /**
     * Returns the destination file. 
     * If the destination file is the empty String, returns that.
     * If it is something else, but does not end with the correct suffix, this suffix is appended to the destination file before returning it.
     * @return The destination file with the correct suffix, or the empty String if no destination file has been specified.
     * @throws InvalidInputException if the OutputConfiguration has an invalid type
     */
    public String getDestinationFile() throws InvalidInputException{
        if(this.destinationFile.equals("")){
            return "";
        }
        String suffix = WriterFactory.getWriterFromType(this.type).getSuffix();
        if(!this.destinationFile.endsWith("." + suffix)){
                return this.destinationFile + "." + suffix;
        }
        return this.destinationFile;
    }

    /**
     * Returns the type of this OutputConfiguration.
     * @return The type of this OutputConfiguration
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the values for the settings of the {@link Writer} of this OutputConfiguration.
     * @return The values for the settings of the {@link Writer} of this OutputConfiguration
     */
    public Map<String, String> getSettingsValues() {
        return settingsValues;
    }
    
    /**
     * Adds a value for a specific setting of a [@link Writer} to this OutputConfiguration.
     * @param value The value for the setting
     * @param nameOfSetting The name of the setting that is given the value
     */
    public void addSettingsValue(String value, String nameOfSetting){
        this.settingsValues.put(nameOfSetting, value);
    }

    /**
     * Returns the flag indicating, whether this OutputConfiguration wants the data to be appended to the destination file.
     * @return The flag indicating, whether this OutputConfiguration wants the data to be appended to the destination file
     */
    public boolean getDoAppend() {
        return doAppend;
    }

    /**
     * Sets the flag indicating, whether this OutputConfiguration wants the data to be appended to the destination file.
     * @param flag The value for the doAppend flag
     */
    public void setDoAppend(boolean flag) {
        this.doAppend = flag;
    }

    /**
     * Returns the flag indicating, whether the user has already been asked if he wants to overwrite the file.
     * @return The flag indicating, whether the user has already been asked if he wants to overwrite the file
     */
    public boolean getAskedDoOverwrite() {
        return askedDoOverwrite;
    }

    /**
     * Sets the flag indicating, whether the user has already been asked if he wants to overwrite the file.
     * @param alreadyAskedExist Specifies, whether the user has already been asked, if he does want to overwrite although the file already exists
     */
    public void setAskedDoOverwrite(boolean alreadyAskedExist) {
        this.askedDoOverwrite = alreadyAskedExist;
    }
    
}
