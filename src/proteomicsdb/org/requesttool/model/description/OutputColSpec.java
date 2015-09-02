package proteomicsdb.org.requesttool.model.description;

/**
 * The third level of the Description Package, together with {@link InputParSpec}.
 * An OutputColSpec describes one output column of an API.
 * @author Maxi
 * @version 1.0
 */
public class OutputColSpec {

    private String name, description, type, example;
    
    /**
     * Constructs an OutputColSpec, settings its attributes to the given values
     * @param name The name of the output column
     * @param description The description of the output column 
     * @param type The type of the output column 
     * @param example An example of a value in this output column
     */
    public OutputColSpec(String name, String description, String type, String example) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.example = example;
    }
    
    //Getter
    
    /**
     * Returns the name of this output column.
     * @return The name of this output column
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of this output column.
     * @return The description of this output column
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the type of this output column.
     * @return The type of this output column
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the example value for this output column.
     * @return The example value for this output column
     */
    public String getExample() {
        return example;
    }
}
