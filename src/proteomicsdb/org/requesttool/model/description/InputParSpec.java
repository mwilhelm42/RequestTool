package proteomicsdb.org.requesttool.model.description;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import proteomicsdb.org.requesttool.model.ModelUtils;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;
import proteomicsdb.org.requesttool.model.writing.Writer;

/**
 * The third level of the Description Package, together with {@link OutputColSpec}
 * An InputParSpec describes one input parameter and provides the functionality to validate it.
 * @author Maxi
 * @version 1.0
 */
public class InputParSpec {
    
    private int inputID;
    private String name, description, type, example; //TODO: Type as Enum (with attribute length)
    private List<PredefinedValue> predefinedValues = new ArrayList<PredefinedValue>();
    
    /**
     * Constructs an InputParSpec, setting all its attributes to the specifies values.
     * @param inputID The ID of the parameter
     * @param name The name of the parameter
     * @param description The description of the parameter
     * @param type The type of the parameter; this only differenciates between String, int and double, but does not check the length, which would also be included in the meta information
     * @param example An example value for the parameter
     */
    public InputParSpec(int inputID, String name, String description, String type, String example) {
        
        this.inputID = inputID;
        this.name = name;
        this.description = description;
        this.type = type;
        this.example = example;
    }
    
    /**
     * Checks whether the given value is a valid submission for this input parameter.
     * It does this by checking the type of the parameter, and trying to parse the value into this type (does nothing for String); 
     * if predefined values exist, it is checked, that the given value is one of them.
     * @param value The value to check
     * @return True, if the value is valid; otherwise an Exception is thrown
     * @throws InvalidInputException If the value is not a valid submission for this parameter
     */
    public boolean validate(String value) throws InvalidInputException{ 
        if(value==null){
            throw new InvalidInputException(this.name, "empty", this.type);
        }
        if(ModelUtils.DO_BATCH_JOB && value.contains(ModelUtils.BATCH_JOB_SEPARATOR)){ //only look at this if we do Badge Jobs
            boolean result = true;
            String[] values = value.split(ModelUtils.BATCH_JOB_SEPARATOR);
            for(int i = 0; i<values.length; i++){
                result &= validate(values[i]);
            }
            return result;
        }
        if(this.type.contains("String")){
            if(!checkValueWithPredifinedValues(value)){
                throw new InvalidInputException("Value not matching predefined Values in " + this.name, value, "One of these" + this.getPredefinedValuesNames());
            }
            return true;
        }
        if(this.type.contains("Integer")){
            try{
                Integer.parseInt(value);
                if(!checkValueWithPredifinedValues(value)){
                    throw new InvalidInputException("Value not matching predefined Values in " + this.name, value, "One of these" + this.getPredefinedValuesNames());
                }
                return true;
            }
            catch(NumberFormatException e){
                throw new InvalidInputException(name, value, type);
            }
        }
        if(this.type.contains("Double")){
            try{
                Double.parseDouble(value);
                if(!checkValueWithPredifinedValues(value)){
                    throw new InvalidInputException("Value not matching predefined Values in " + this.name, value, "One of these" + this.getPredefinedValuesNames());
                }
                return true;
            }
            catch(NumberFormatException e){
                throw new InvalidInputException(name, value, type);
            }
        }
        //System.err.println("Validating the input parameter " + this.name + " has detected a new type. This error has to be handled by a programmer. The type is: " + this.type);
        throw new InvalidInputException(this.name, value, "The type of this input parameter (" + this.type + ") is not included in the validate method. It has to be added by a programmer. \n"
                + "Please report this to the mail address given under Help->About"); 
    }
    
    /**
     * Checks whether the given value is one of the predefined values of this input parameter. If there are no predefined values, returns true
     * @param valueToCheck The value to check
     * @return True, if this value matches the predefined values of this parameter; false otherwise.
     */
    public boolean checkValueWithPredifinedValues(String valueToCheck){
        if (this.predefinedValues.isEmpty()){
            return true;
        }
        else{
            for(PredefinedValue pValue: this.predefinedValues){
                if(pValue.getValue().equals(valueToCheck)){return true;}
            }
            return false;
        }
    }
    
    /**
     * Returns true, if this input parameter has predefined values; false otherwise. 
     * @return True, if this input parameter has predefined values; false otherwise. 
     */
    public boolean hasPredefinedValues(){
        return this.predefinedValues.size()>0;
    }
    
    //Getter

    /**
     * Returns a String containing the names of all {@link PredefinedValue}s of this input parameter in the form of "["pv1", "pv2"]
     * @return A String containing the names of all {@link PredefinedValue}s of this input parameter in the form of "["pv1", "pv2"]
     */
    public String getPredefinedValuesNames(){
        String result = "[";
        for (PredefinedValue pV: this.predefinedValues){
            result += "\"" + pV.getName() + "\", ";
        }
        result = result.substring(0, result.length()-2);
        result+="]";
        return result;
    }
    
    /**
     * Returns the ID of this input parameter. 
     * @return The ID of this input parameter
     */
    public int getInputID() {
        return inputID;
    }

    /**
     * Returns the name of this input parameter. 
     * @return The name of this input parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of this input parameter. 
     * @return The description of this input parameter
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the type of this input parameter. 
     * @return The type of this input parameter
     */
    public String getType() {
        return type;
    }

    /**
     * Returns an example value of this input parameter. 
     * @return An example value of this input parameter
     */
    public String getExample() {
        return example;
    }
    
    /**
     * Returns the List of {@link PredefinedValue}s of the this input parameter.
     * @return The List of {@link PredefinedValue}s of the this input parameter
     */
    public List<PredefinedValue> getPredefinedValues(){
        return predefinedValues;
    }
    
    /**
     * Adds a {@link PredefinedValue} to the List of {@link PredefinedValue}s of this parameter. The value is specified by its attributes.
     * This method is used by the {@link Writer}s to specify their settings.
     * @param aName The name of the predefined value
     * @param aValue The value of the predefined value
     * @param aDescription The description of the predefined value
     */
    public void addPredefinedValue(String aName, String aValue, String aDescription){
        this.predefinedValues.add(new PredefinedValue(aName, aValue, aDescription));
    }
    
    /**
     * Adds a {@link PredefinedValue} to the List of {@link PredefinedValue}s of this parameter. The value is specified by the LinkedHashMap, that is part of the Intern specifying the complete meta information.
     * Checks, whether the LinkedHashMap really describes a predefined value.
     * @param valueDescr The LinkedHashMap containing the description of the {@link PredefinedValue}
     */
    public void addPredefinedValue(LinkedHashMap<String,String> valueDescr){
        if(!(valueDescr.get("PREDEFINED_VALUE") == null)){
            this.predefinedValues.add(new PredefinedValue(valueDescr.get("PREDEFINED_NAME"), valueDescr.get("PREDEFINED_VALUE"), valueDescr.get("PREDEFINED_DESCRIPTION")));
        }
    }
    
}
