package proteomicsdb.org.requesttool.model.request;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import proteomicsdb.org.requesttool.model.writing.WritingThread;

/**
 * The ThreadPoolExecutor that contains all requests, manages, that at most 4 of them are executed at once, and that listens to all updates.
 * @author Maxi
 * @version 1.0
 */
public class RequestThreadPool extends ThreadPoolExecutor implements RequestListener{
    
    /**
     * The ThreadPoolExecutor that is used to manage the writes
     */
    private WritingThread wt;
    
    /**
     * Initializes the ThreadPoolExecutor by calling the super constructor. It sets the maximumPoolSize to 4; the corePoolSize and the keepAliveTime are not relevant, 
     * because the queue where the tasks of the RequestThreadPool are saved in has infinite size. 
     * Also initializes the WritingThread.
     */
    public RequestThreadPool(){
        super(4,4,10,TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        wt = new WritingThread();
    }

    /**
     * Returns the {@link WritingThread} that the RequestThreadPool knows.
     * @return The {@link WritingThread} 
     */
    public WritingThread getWt() {
        return wt;
    }
    
    /**
     * Listens to all updates of all RequestNotifiers this RequestThreadPool knows. 
     * Could be used for error handling in the model; this was done in the beginning, but in version 1.0 it is not needed, 
     * because all updates are given to the view and displayed there.
     * The only important task of this method is to give RunnableRequestHandlers, that have finished the requesting, to the {@link WritingThread}, 
     * which then handles the writing. 
     * @param message The current state of the {@link RequestNotifier}, a {@link RequestEvent}
     * @param notifier The notifier itself, that is then given to the {@link WritingThread}
     */
    @Override
    public void update(RequestEvent message, RequestNotifier notifier){
        //The switch is left here, although it only listens to one state, to be able to easily expand this method again, to listen to more states.
        switch (message){
                case READY_TO_WRITE: 
                    if(notifier instanceof RequestHandler){
                        wt.enqueueWrite(message, (RequestHandler) notifier);  
                    }
                    else{
                        //This can never occur in the current implementation, since there is only the class of RequestHandler that implements the interface RequestNotifier
                        System.err.println("RequestThreadGroup received a READY_TO_WRITE from a notifier that is NOT a RequestHandler.");
                    }
                default: 
                    break;
        }
    }
}
