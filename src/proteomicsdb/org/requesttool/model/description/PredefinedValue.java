package proteomicsdb.org.requesttool.model.description;
/**
 * The fourth level of the Description Package. 
 * This class describes a single predefined value of an input parameter.
 * @author Maxi
 */
public class PredefinedValue {
        
    private String name, value, description;

    /**
     * Sets the attributes of the predefinedValue to the given values.
     * @param name The name of the predefined value
     * @param value The value of the predefined value
     * @param description The desription of the predefined value
     */
    public PredefinedValue(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }
    
    //getter
    
    /**
     * Returns the name of the predefinedValue.
     * @return the name of the predefinedValue
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the predefinedValue.
     * @return the value of the predefinedValue
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the description of the predefinedValue.
     * @return the description of the predefinedValue
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns a String representation of the predefinedValue, in the form "Name: $name Value: $value Description: $description".
     * @return A String representation of the predefinedValue
     */
    @Override
    public String toString(){
        return "Name: " + name + " Value: " +  value + " Description: " + description;
    }
}
