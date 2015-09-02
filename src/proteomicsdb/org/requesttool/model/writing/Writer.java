package proteomicsdb.org.requesttool.model.writing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import proteomicsdb.org.requesttool.controller.Control;
import proteomicsdb.org.requesttool.model.description.InputParSpec;
import proteomicsdb.org.requesttool.model.description.Intern;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.exceptions.CollectedException;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;

/**
 * The class all Writers have to extend. Every non abstract class that extends this class will be included in {@link WriterFactory#allWriters}
 * @author Maxi
 * @version 1.0
 */
public abstract class Writer{
    
    /**
     * The type of the writer
     */
    protected String type;
    /**
     * The specific settings of a Writer
     */
    protected Map<String, InputParSpec> specificSettings;
    
    /**
     * The method that converts the data to the format of the Writer and actually puts it into the file. 
     * @param configuration The {@link OutputConfiguration} specifying how the data shall be written
     * @param input The data to be written
     * @param writer A {@link PrintWriter} to the already opened file
     */
    public abstract void actuallyWrite(OutputConfiguration configuration, Intern input, PrintWriter writer);
    
    /**
     * Calls the constructor of the specific Writer and returns the resulting object. Used by the Factory to create new Writers.
     * @return An object of the type of the specific Writer
     */
    public abstract Writer getNewWriter();
    
    /**
     * Returns the file ending a file of the type of this Writer must have. 
     * @return The file ending a file of the type of this Writer must have
     */
    public abstract String getSuffix();
    
    /**
     * Called by the constructor of each Writer to specify which settings this Writer has and needs.
     */
    protected abstract void configureSpecificSettings();
    
    /**
     * Returns the type of the Writer.
     * @return The type of the Writer
     */
    public String getType(){
        return type;
    }

    /**
     * Returns the specificSettings of the Writer.
     * @return The specificSettings of the Writer
     */
    public Map<String, InputParSpec> getSpecificSettings() {
        return specificSettings;
    }
    
    /**
     * This method is called by an {@link OutputConfiguration#validate(RequestTool.Controller.Control)}. It tests, whether the specificSettings given in the {@link OutputConfiguration} are valid for this writer.
     * Also checks whether the destination file already exists and uses {@link Control#notifyUser(java.lang.String, java.lang.String)} to ask, if the file can be overwritten.
     * @param configuration The {@link OutputConfiguration} whose settings shall be validated
     * @param theControl The {@link Control}, that is used to ask the user if he wants to overwrite.
     * @return True, if the settings of the {@link OutputConfiguration} are valid; otherwise an Exception is thrown
     * @throws InvalidInputException to indicate, which input parameter was invalid
     * @throws CollectedException to indicate all of the specificSettings that were invalid
     */
    public boolean validate(OutputConfiguration configuration, Control theControl) throws InvalidInputException, CollectedException{
        boolean result = true;
        String destFile = configuration.getDestinationFile();
        if(!(configuration.getType().equals(type))){
            throw new InvalidInputException("Output Configuration type", configuration.getType(), this.type);
        }
        if(!(specificSettings.size()==configuration.getSettingsValues().size())){
            throw new InvalidInputException("Output Configuration settings", "The number of your settings is not equal to the number of required settings." ,"The correct number of settings.");
        }
        if(new File(destFile).exists()&&!configuration.getDoAppend()){
            if(!configuration.getAskedDoOverwrite() && !theControl.notifyUser("The file you want to write to (" + configuration.getDestinationFile() +") already exists. Are you sure you want to continue?", "File already exists!")){
                return false;
            }
        }
        if(destFile.equals("")){
            throw new InvalidInputException("Destination File", "Nothing" ,"Please enter a filename");
        }
        CollectedException collectedException = new CollectedException();
        for(String nameOfSetting: specificSettings.keySet()){
            try{
                result &= this.specificSettings.get(nameOfSetting).validate(configuration.getSettingsValues().get(nameOfSetting));
            }
            catch(Exception e){
                collectedException.addException(e);
            }
            if(!collectedException.isEmpty()){
                throw collectedException;
            }
        }
        if(!result){
            throw new InvalidInputException("Output Configuration invalid for unknown reason", configuration.toString(), "A valid output configuration");
        }
        return result;
    }
    
    /**
     * This method is called by the WriterFactory to start the writing. 
     * It opens a {@link PrintWriter} to the file specified in the configuration, and then calls the specific {@link #actuallyWrite(RequestTool.Model.Description.OutputConfiguration, RequestTool.Model.Description.Intern, java.io.PrintWriter) } method
     * After that it closes the {@link PrintWriter} again.
     * It also sets the doAppend-flag of the OutputConfiguration to false, if appending is enabled, but the file is empty; 
     * this is needed because a Writer might have different behaviour if it appends then if it writes a new file; but in an empty file, this different behavior does not make sense.
     * @param configuration The {@link OutputConfiguration} that specifies, how the data shall be written
     * @param input The data to be written
     * @throws InvalidInputException If there occurs a problem with the destination file
     */
    public void write(OutputConfiguration configuration, Intern input) throws InvalidInputException{ //does not check for validity of outputconf; take care!
        //Check whether we have to append, and if so, if we can just append, or if we have to write the header
        if(configuration.getDoAppend() && new File(configuration.getDestinationFile()).exists()){
            try{
                BufferedReader br = new BufferedReader(new FileReader(configuration.getDestinationFile())); 
                String temp = br.readLine();
                //System.out.println("The first line: " + temp);
                if (temp == null || temp.equals("")) {
                    configuration.setDoAppend(false);
                }
                else{
                    //let it remain true, so we append without writing the header
                }
                br.close();
            }
            catch(Exception e){
                throw new InvalidInputException("Destination File is not accessible!\n" + e.getMessage(), configuration.getDestinationFile(), "A valid file");
            }      
        }
        else{
            configuration.setDoAppend(false);
        }
        
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(configuration.getDestinationFile(), configuration.getDoAppend())));
            actuallyWrite(configuration,input,writer);
            writer.close(); //no problem occurs, of the PrintWriter has already been closed by actuallyWrite
        }
        catch (FileNotFoundException ex) {
            throw new InvalidInputException("Output Configuration File", configuration.getDestinationFile(), "A valid file");
        }
        catch (IOException exc){
            throw new InvalidInputException("Output Configuration File", configuration.getDestinationFile(), "The file you specified can not be opened by the writer.");
        }
    }
    
    /**
     * Returns the String representation of the Writer, which is his type.
     * @return The type of the Writer
     */
    @Override
    public String toString(){
        return this.type;
    }
}
