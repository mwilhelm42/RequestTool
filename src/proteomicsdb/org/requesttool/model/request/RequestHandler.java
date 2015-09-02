package proteomicsdb.org.requesttool.model.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.json.Json;
import javax.json.stream.JsonParser;
import proteomicsdb.org.requesttool.model.description.InputConfigurationValidated;
import proteomicsdb.org.requesttool.model.description.Intern;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;
import proteomicsdb.org.requesttool.model.login.SingletonLoginHandler;
import proteomicsdb.org.requesttool.model.writing.WriterFactory;


/**
 * Request Handler is the class that takes care of handling the connection, getting the received data into an intern data format and writing the data.
 * @author Maxi
 * @version 1.0
 */
public class RequestHandler implements RequestNotifier{
    /**
     * The link to the ODatabase. Contains the query. 
     * Implements {@link RequestNotifier}, to notify a number of observers about progress and errors of the Request.
     */
    private String link;
    /**
     * An {@link OutputConfiguration} defining what should be done with the data received.
     */
    private OutputConfiguration outputConf;
    
    /**
     * The received data will be put into this attribute. Protected, so that the {@link RunnableRequestHandler} can also access this.
     */
    protected Intern data;
    /**
     * A unique id to identify the RequestHandler. Protected, so that the {@link RunnableRequestHandler} can also access this.
     */
    protected String id;
    
    /**
     * A list of the observers, that are notified of the progress and errors of the request.
     */
    private List<RequestListener> observers;
    /**
     * The current state of the RequestHandler. 
     * It is told to the observers by the {@link #notifyObservers()} method, and changed via the {@link #changeState(RequestEvent event)} method.
     */
    private RequestEvent state;
    /**
     * Used to make the [@link #id} unique: This counter is incremented with each RequestHandler, and the number is part of its id.
     */
    static int idCounter = 1;
    
    /**
     * The standard constructor for the RequestHandler, that is used during the request phase. Assigns a unique {@link #id} to the RequestHandler.
     * @param validatedInputConf  an {@link InputConfigurationValidated} that is used to build the link.
     * @param anOutputConf  an {@link OutputConfiguration} that specifies how to deal with the received data
     * @param aRequestListener  a {@link RequestListener} that is registered to the observers of the RequestHandler
     */
    public RequestHandler(InputConfigurationValidated validatedInputConf, OutputConfiguration anOutputConf, RequestListener aRequestListener) {
        id = ("Request " + Integer.toString(idCounter++) + ": " + validatedInputConf.getTitleOfRequestedAPI());
        
        this.observers = new ArrayList<RequestListener>();
        if (aRequestListener!=null){
            this.register(aRequestListener);
        }
        this.changeState(RequestEvent.REQUEST_STARTED);
        this.link = validatedInputConf.buildLink();
        this.changeState(RequestEvent.LINK_BUILT);
        this.outputConf = anOutputConf;
    }
    
    /**
     * The constructor for the first two Requests where the {@link api.Description.AllAPIsSpecification} is built. 
     * Assigns a unique {@link #id} to the RequestHandler.
     * It does not get an {@link api.Description.InputConfiguration}, but it directly gets the link to request the MetaData about the structure of the APIs.
     * Careful: After constructing a RequestHandler with this constructor, do not call {@link #fullRequest()} or {@link #dumpData()}, 
     * for there is no {@link api.OutputConfiguration} and it will result in a {@link java.lang.NullPointerException}.
     * @param aLink  a Link that is requested. Probably a MetaData request
     * @param aRequestListener  a RequestListener that is registered to the observers of the RequestHandler
     */
    public RequestHandler(String aLink, RequestListener aRequestListener){ 
        id = "A request from the other constructor";
        
        this.observers = new ArrayList<RequestListener>();
        if (aRequestListener!=null){
            this.register(aRequestListener);
        }
        
        this.link = aLink;
    }
    
    /**
     * Handles the request.
     * It opens a connection to the url specified by the {@link InputConfigurationValidated} (or that was specified directly in the second constructor)
     * using the Login Information defined in the {@link SingletonLoginHandler}
     * and parses the JSON that is received from the ODatabase into an {@link Intern}.
     * Notifies the registered {@linkplain RequestListener RequestListeners} of {@link RequestEvent#CONNECTION_ESTABLISHED} and {@link RequestEvent#CONNECTION_CLOSED}.
     * 
     * If an error occurs, the method returns null. This MUST be handled by whomever has called request().
     * What type of error occured is told to the registered {@linkplain RequestListener RequestListeners} via 
     * {@link RequestEvent#CONNECTION_ERROR} and the corresponding {@link RequestEvent#exception}.
     * @return The {@link Intern} containing the data.
     */
    public Intern request(){ //TODO: check the header further; know what is valid, and check for that
        try {
            //check for null, cause base case
            //WriterFactory.getWriterFromConfig(outputConf).validate(outputConf); //TODO: Maybe not two times outputConf; check what validate returns
            this.changeState(RequestEvent.ESTABLISHING_CONNECTION);
            URL url = new URL(link);
            //System.setProperty("java.net.useSystemProxies","true"); //has not helped with the proxies...
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            this.changeState(RequestEvent.CONNECTION_ESTABLISHED);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + SingletonLoginHandler.getEncoding());
            this.changeState(RequestEvent.RECEIVING_DATA);
            InputStream content = (InputStream)connection.getInputStream();
            BufferedReader in   = 
            new BufferedReader (new InputStreamReader (content));
            if(connection.getHeaderField("x-sap-login-page")!=null){ 
                throw new InvalidInputException("You got redirected to the SAP Login Page. Probably your User Data is false.", "", "");
            }
            data = parse (in.readLine());
            connection.disconnect();
            this.changeState(RequestEvent.CONNECTION_CLOSED);
            
            //check, whether the data makes sense
            return data;
        }
        catch(IOException e){
            RequestEvent.CONNECTION_ERROR.setException(e);
            RequestEvent.CONNECTION_ERROR.setMessage("IO Error when instantiating the connection or when getting the Input\n" + e.getMessage());
            changeState(RequestEvent.CONNECTION_ERROR);
        }
        catch (NullPointerException e){
            RequestEvent.CONNECTION_ERROR.setException(e);
            RequestEvent.CONNECTION_ERROR.setMessage("Null pointer in RequestHandler.request(). You probably have not initialized your Login Handler\n" + e.getMessage());
            changeState(RequestEvent.CONNECTION_ERROR);
        }
        catch(Exception e) { 
            RequestEvent.CONNECTION_ERROR.setException(e);
            RequestEvent.CONNECTION_ERROR.setMessage(e.getMessage());
            changeState(RequestEvent.CONNECTION_ERROR);
        }
        
        return null; //is reached after connection error
    }
    
    /**
     * Writes {@link #data} in the way specified in the {@link OutputConfiguration} given to the constructor.
     * Notifies the registered {@linkplain RequestListener RequestListeners} of {@link RequestEvent#WRITING_STARTED} and {@link RequestEvent#WRITING_FINISHED}.
     * If a {@link RequestEvent#WRITING_ERROR} occurs, the RequestHandler will notify its listeners of it.
     */
    public void dumpData(){
        this.changeState(RequestEvent.WRITING_STARTED);
        try{
            WriterFactory.write(outputConf, data);
            this.changeState(RequestEvent.WRITING_FINISHED);
        }
        catch(Exception e){
            RequestEvent.WRITING_ERROR.setException(e);
            RequestEvent.WRITING_ERROR.setMessage("WRITING_ERROR in Thread " + this.id + "\n" + e.getMessage());
            this.changeState(RequestEvent.WRITING_ERROR);
        }
        
    }
    
    /**
     * Registers a {@link RequestListener} to this RequestHandler, that will furthermore be notified of any state changes.
     * @param observer the {@link RequestListener} to be added to the observers
     */
    @Override
    public void register(RequestListener observer){ //TODO: Null handling, Mutex?
        if(!this.observers.contains(observer)){
            this.observers.add(observer);
        }
    }
    
    /**
     * Removes a {@link RequestListener} from the observers, so it will not be notified anymore. 
     * Specifying a {@link RequestListener} that is not in observers will not change anything.
     * @param observer the {@link RequestListener} to be removed from the observers
     */
    @Override  
    public void unregister(RequestListener observer) {
        this.observers.remove(observer);
    }
    
    /**
     * Notifies all {@linkplain RequestListener RequestListeners} of the current state of the RequestHandler.
     * Since state is not synchronized, it may occur, that two {@linkplain RequestListener RequestListeners} get different states 
     * from the same call of notifyObservers.
     * This does not arise problems, because usually the state is only used to inform about progress and at the few significant states the RequestHandler will wait.
     * (see also {@link RunnableRequestHandler#run()})
     */
    @Override
    public void notifyObservers(){
        for (RequestListener o: this.observers){
            o.update(this.state, this); //this.state may have already changed to the next state; this results in one state being given to the progress bar twice; but it should not arise any problems
        }
    }
    
    /**
     * Changes the state of the RequestHandler to the specified RequestEvent. 
     * Calls {@link #notifyObservers() } to notify all registered {@linkplain RequestListener RequestListeners} of the change.
     * @param event the new state the RequestHandler is in.
     */
    public final void changeState(RequestEvent event){
        this.state = event;
        notifyObservers();
    }
    
            
         
    /**
     * Parses the String containing the data received from the ODatabase into an {@link Intern}
     * The String must be a JSON and follow certain structural criteria. The data records must be contained in one array, and each data record must start with an object that specifies its metadata. 
     * This metadata is ignored by the parse method.
     * 
     * @param data the String containing the JSON that shall be parsed
     * @return an {@link Intern} representing the data given
     */
    private Intern parse(String data) {
        this.changeState(RequestEvent.DATA_RECEIVED);
        JsonParser parser = Json.createParser(new StringReader(data));
        Intern result = new Intern();
        int counter = -1;
        String key = "default"; //needed for netbeans not to complain

        //first go to "results:["
        JsonParser.Event jsonParserEvent = parser.next();
        while (jsonParserEvent != JsonParser.Event.START_ARRAY) {
            jsonParserEvent = parser.next();
        }
        
        while (parser.hasNext()) {
            jsonParserEvent = parser.next();
            switch (jsonParserEvent) {
                case START_OBJECT:
                    jsonParserEvent = parser.next();
                    if (parser.getString().equals("__metadata")) {
                        while (jsonParserEvent != JsonParser.Event.END_OBJECT) { //Go over the metadata
                            jsonParserEvent = parser.next(); 
                        }
                        result.add(new LinkedHashMap());
                        counter++;
                    } else {
                        result.add(new LinkedHashMap());
                        counter++;
                        key = parser.getString();
                    }
                    break;
                case END_OBJECT:
                    break;
                case START_ARRAY:
                    System.err.println("This should not happen (Start_Array encountered).");
                    break;
                case END_ARRAY:
                    //System.out.println("Request finished");
                    break;
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_NULL:
                    result.get(counter).put(key, null); //maybe put just 0, so i don't get null when printing the csv
                    break;
                default:
                    result.get(counter).put(key, parser.getString()); //TODO: look for the ordering
            }
        }

        this.changeState(RequestEvent.DATA_CONVERTED);
        return result;
    }
    
    /**
     * Returns the {@link #data}. May return null, if {@link #request() } has not been succesfully called.
     * @return the [@link #data}
     */
    public Intern getData() {
        return data;
    }

    /**
     * Returns the unique {@link #id}
     * @return {@link #id}
     */
    public String getID() {
        return id;
    }
    
    
}
