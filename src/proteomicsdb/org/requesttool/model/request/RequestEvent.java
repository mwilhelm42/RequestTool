package proteomicsdb.org.requesttool.model.request;

/**
 * An enum, that contains all possible values of the state of the {@link RequestHandler}. 
 * It has values for the standard workflow and several error values. 
 * Each value is assigned a number to indicate the estimated progress of the {@link RequestHandler}; errors have the value -1. 
 * @version 1.0
 * @author Maxi
 */
public enum RequestEvent {
    //Standard workflow
    REQUEST_STARTED("Thread started working", 0),
    LINK_BUILT("Link was built without errors", 15),
    ESTABLISHING_CONNECTION("Trying to connect to server", 20),
    CONNECTION_ESTABLISHED("Connection was established", 30),
    RECEIVING_DATA("Starting to receive", 40),
    DATA_RECEIVED("Data was received", 50),
    DATA_CONVERTED("Data was converted to Intern", 60),
    CONNECTION_CLOSED("Connection was closed", 65),
    READY_TO_WRITE("Data has been put into the data attribute of the RRH", 70), //only for rrh
    WRITING_STARTED("Writing has started", 75),
    WRITING_FINISHED("Writing is finished", 100),
    
    //error messages
    LINK_ERROR("", -1),
    CONNECTION_ERROR("", -1),
    WRITING_ERROR("",-1),
    INPUT_ERROR("",-1)
    ;
    
    
    private String message;
    private int progress; //between 0 and 100
    private Exception exception;
    
    /**
     * The constructor of RequestEvent.
     * @param aMessage Sets the message for the RequestEvent
     * @param aProgress Sets the estimated completetion of a {@link RequestHandler} in the state of the constructed RequestEvent
     */
    RequestEvent(String aMessage, int aProgress){
        this.message = aMessage;
        this.progress = aProgress;
    }

    /**
     * Sets the message of the RequestEvent. Used to enclose error messages in the state, to give them to the observers of the {@link RequestHandler}.
     * @param aMessage The new message of the RequestEvent
     */
    public void setMessage(String aMessage){
        this.message = aMessage;
    }
    
    /**
     * Sets the Exception of the RequestEvent. Used to enclose Exceptions in the state, to give them to the observers of the {@link RequestHandler}.
     * @param anException The Exception that shall be enclosed in the RequestEvent
     */
    public void setException(Exception anException) {
        this.exception = anException;
    }

    /**
     * Returns the Exception that was enclosed in this RequestEvent, or null, if no exception has been enclosed.
     * @return The Exception that was enclosed in this RequestEvent
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Returns the message that was enclosed in this RequestEvent, possibly one of the predefined standard messages.
     * @return The message that was enclosed in this RequestEvent
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the progress value that the RequestEvent was assigned, to estimate the completion of a {@link RequestHandler} that has this state.
     * @return The progress value of the RequestEvent
     */
    public int getProgress() {
        return progress;
    }
    
    
    
    
    
}
