package proteomicsdb.org.requesttool.model.request;

/**
 * The interface the subjects of the observer pattern have to implement. Only implemented by the RequestHandler in 1.0.
 * A RequestNotifier should contain a variable state of type RequestEvent, and a list of {@link RequestListener}s, so a list of its observers.
 * @author Maxi
 * @version 1.0
 */
public interface RequestNotifier {
    /**
     * Registers a {@link RequestListener} to the RequestNotifier. This can most intuitively be implemented by adding it to the list of observers.
     * @param observer The {@link RequestListener} that shall be registered.
     */
    public void register (RequestListener observer);
    
    /**
     * Unregisters a {@link RequestListener} from the RequestNotifier. This can most intuitively be implemented by removing it from the list of observers.
     * @param observer The {@link RequestListener} that shall be unregistered.
     */
    public void unregister (RequestListener observer);
    
    /**
     * Notifies all observers. This should most intuitively be implemented by calling the update method of all registered {link RequestListener}s.
     */
    public void notifyObservers();
    
    
}
