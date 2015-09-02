package proteomicsdb.org.requesttool.model.request;

/**
 * The interface an observer of a {@link RequestNotifier} has to implement. 
 * @author Maxi
 * @version 1.0
 */
public interface RequestListener {
    
    /**
     * This method handles the updates received from the {@link RequestNotifier}. 
     * It gets the {@link RequestEvent} that specifies the current state of the {@link RequestNotifier} and a link to the notifier itself.
     * @param message The state of the RequestNotifier, a {@link RequestEvent}
     * @param notifier The notifier itself
     */
    public void update(RequestEvent message, RequestNotifier notifier);
    
}
