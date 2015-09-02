package proteomicsdb.org.requesttool.model.writing;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import proteomicsdb.org.requesttool.model.request.RequestEvent;
import proteomicsdb.org.requesttool.model.request.RequestHandler;

/**
 * The ThreadPoolExecutor that manages the writes. 
 * @author Maxi
 * @version 1.0
 */
public class WritingThread extends ThreadPoolExecutor{
    
    /**
     * The WritingThread has a maximumPoolSize of 1, so no two writes occur at the same time, to avoid that two writers open the same file. 
     * corePoolSize and keepAliveTime are not important, because the queue saving the tasks has infinite length.
     */
    public WritingThread(){
        super(1,1,10,TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }
    
    /**
     * An internal class, that is used to wrap the {@link RequestHandler}s whose data shall be written. 
     * The WritingThread executes WrappedRequestHandlers, and the WrappedRequestHandlers just call {@link RequestHandler#dumpData()}. 
     * The RequestHandlers need to be wrapped, because executing them will just call request again.
     */
    public class WrappedRequestHandler implements Runnable{
                RequestHandler rrh;
                public WrappedRequestHandler(RequestHandler aRRH){
                    this.rrh = aRRH;
                }
                @Override
                public void run(){
                    rrh.dumpData();
                }
                }
    
    /**
     * This method wraps the RequestHandler that is given to it in a {@link WrappedRequestHandler}, and then puts this {@link WrappedRequestHandler} in its queue of tasks.
     * Thereby {@link RequestHandler#dumpData()} is called for every enqueued {@link RequestHandler}, but only one at a time is allowed to write.
     * @param aRequestEvent The state of the {@link RequestHandler} that is to be enqueued. MUST be {@link RequestEvent#READY_TO_WRITE}
     * @param theRequestHandler The {@link RequestHandler} that is to be enqueued, so its data is written
     */
    public void enqueueWrite (RequestEvent aRequestEvent, RequestHandler theRequestHandler) { //TODO: Allow multiple writes, if they dont have the same file
        if (aRequestEvent.equals(RequestEvent.READY_TO_WRITE)){
            this.execute(new WrappedRequestHandler(theRequestHandler)); 
        }
    }
}
