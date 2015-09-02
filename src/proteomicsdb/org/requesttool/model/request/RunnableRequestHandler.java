package proteomicsdb.org.requesttool.model.request;

import proteomicsdb.org.requesttool.controller.Control;
import proteomicsdb.org.requesttool.model.description.InputConfigurationValidated;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;
import proteomicsdb.org.requesttool.model.writing.WritingThread;
import proteomicsdb.org.requesttool.view.ProgressPanel;

/**
 * The runnable version of the {@link RequestHandler}. Basically adds only the run method.
 * @author Maxi
 * @version 1.0
 */
public class RunnableRequestHandler extends RequestHandler implements Runnable{
    
    /**
     * Gets the same things as the {@link RequestHandler}, and just calls the super constructor.
     * @param anInputConf The {@link InputConfigurationValidated} that is used to build the link
     * @param anOutputConf The {@link OutputConfiguration} that describes how the data shall be written
     * @param theRequestThreadPool The {@link RequestThreadPool} that is used as {@link RequestListener}; this is needed to be able to enqueue this RunnableRequestHandler in the {@link WritingThread}
     */
    public RunnableRequestHandler(InputConfigurationValidated anInputConf, OutputConfiguration anOutputConf, RequestThreadPool theRequestThreadPool) {
        super(anInputConf, anOutputConf, theRequestThreadPool);
    }
    
    /**
     * A private constructor, that is used only by the {@link #getRunnableRequestHandlerForErrorMessage(RequestTool.Model.Description.InputConfigurationValidated, RequestTool.Model.Request.RequestListener) } method
     * Using this constructor for anything else than this method is dangerous.
     * @param anInputConfWithOnlyCurrentAPI An InputConfiguration, that only has the current API set, but no checked values, that hence can build no valid link. 
     * @param aListener A {@link RequestListener}
     */
    private RunnableRequestHandler(InputConfigurationValidated anInputConfWithOnlyCurrentAPI, RequestListener aListener){
        super("", aListener);
        id = ("Request " + Integer.toString(idCounter++) + ": " + anInputConfWithOnlyCurrentAPI.getTitleOfRequestedAPI());
        
    }
    
    /**
     * This method is used by {@link Control} to include requests, where the InputConfiguration is invalid, in the {@link ProgressPanel}
     * Usually, without a valid InputConfiguration, no {link RequestHandler} can be constructed, because its constructor already builds the link.
     * This is why this method uses a special private constructor to include an invalid InputConfiguration in a RequestHandler. 
     * This RunnableRequestHandler can then be put into the {@link ProgressPanel}, and its state can be set to {@link RequestEvent#INPUT_ERROR}, 
     * including the {@link InvalidInputException} that occurred.
     * @param anInputConfWithOnlyCurrentAPI An InputConfiguration, that only has the current API set, but no checked values, that hence can build no valid link. 
     * @param aListener A {@link RequestListener}
     * @return A dummy RunnableRequestHandler, that can be used only to display error messages, but can send no request.
     */
    public static RunnableRequestHandler getRunnableRequestHandlerForErrorMessage(InputConfigurationValidated anInputConfWithOnlyCurrentAPI, RequestListener aListener){
        return new RunnableRequestHandler(anInputConfWithOnlyCurrentAPI, aListener);
    }
    
    /**
     * This method implements the Runnable interface. It only calls {@link RequestHandler#request} and then sets its state to {@link RequestEvent#READY_TO_WRITE},
     * to then be enqueued in the {@link WritingThread}.
     * The structure of parallelizing the requests looks like this: The RunnableRequestHandlers are executed parallely, though they number is limited by the {@link RequestThreadPool}.
     * When they indicate, that the request is finished, they are given to the {@link WritingThread}, so it can then handle the writes, which are not parallel.
     * It is important, that not only the data, but also the {@link RequestHandler} is given to the WritingThread, so that it calls {@link RequestHandler#dumpData()},
     * so that progress information is still handed out.
     */
    @Override
    public void run(){
        this.data = this.request();
        this.changeState(RequestEvent.READY_TO_WRITE);   
    }
}
