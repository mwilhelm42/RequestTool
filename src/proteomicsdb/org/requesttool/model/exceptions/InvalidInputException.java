package proteomicsdb.org.requesttool.model.exceptions;

/**
 * An Exception indicating that some input parameter is invalid.
 * @author Maxi
 * @version 1.0
 */
public class InvalidInputException extends Exception{
    
    String inputParamName, value, expectedType;

    /**
     * Constructs an InvalidInputException
     * @param inputParamName The name of the parameter that is invalid
     * @param value The invalid value given by the user
     * @param expectedType The expected type of the value; possibly just a description of the parameter
     */
    public InvalidInputException(String inputParamName, String value, String expectedType) {
        this.inputParamName = inputParamName;
        this.value = value;
        this.expectedType = expectedType;
    }
    
    /**
     * Returns a message containing the invalid parameter, its expected type and the invalid value that was input by the user.
     * @return A message containing the invalid parameter, its expected type and the invalid value that was input by the user
     */
    @Override
    public String getMessage(){
        return "Error in InputParameter " + inputParamName + ". \nExpected value: " + expectedType + ". \nYour Input Value: " + value + "\n";
    }
    
    
}
