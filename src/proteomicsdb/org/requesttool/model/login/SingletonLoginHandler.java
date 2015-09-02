package proteomicsdb.org.requesttool.model.login;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import proteomicsdb.org.requesttool.model.ModelUtils;
import proteomicsdb.org.requesttool.model.description.Intern;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;
import proteomicsdb.org.requesttool.model.request.RequestEvent;
import proteomicsdb.org.requesttool.model.request.RequestHandler;
import proteomicsdb.org.requesttool.model.request.RequestListener;
import proteomicsdb.org.requesttool.model.request.RequestNotifier;

/**
 * SingletonLoginHandler is the class that checks, wether the username and password the user has input is valid, and then generates the encoding that is needed to authenticate requests.
 * It is a singleton, to ensure that only one LoginData exists at a time.
 * @author Maxi
 * @version 1.0
 */
public class SingletonLoginHandler implements RequestListener{ 
  /**
   * The instance of the SingletonLoginHandler
   */
  private static SingletonLoginHandler instance;
  
  /**
   * The encoding that is needed to authenticate requests.
   */
  private static String encoding;
  
  /**
   * This is used to throw the Exception in the getInstance method. The Exception must contain the error that occurred during the Request. 
   * This error can only be given to the SingeltonLoginHandler by being an observer of the RequestHandler. 
   * To give it from the update method to getInstance, this variable is used.
   */
  private static String errorMessage=null;

    
  
  
  /**
   * Private, since it may only be used from the getInstance method.
   */
  private SingletonLoginHandler(){
      //nothing
  }
  
  /**
   * Returns the instance of the SingletonLoginHandler.
   * @return the instance of the SingletonLoginHandler
   * @throws NullPointerException If instance has not yet been initialized, so if no valid username and password has been given yet.
   */
  public static SingletonLoginHandler getInstance() throws NullPointerException{ 
      if (instance == null){
          throw new NullPointerException();
      }
      return SingletonLoginHandler.instance;
  }  
  
  /**
   * If there already exists an instance of the SingletonLoginHandler, just returns that instance. 
   * (And tells you, on System.err. that you should not have called this method, but {@linkplain #getInstance() the one without parameters }
   * Else calls {@link #testPassword(java.lang.String, java.lang.String) } to check the username and password. 
   * If they are valid, constructs the instance of the SingeltonLoginHandler and sets the encoding,
   * otherwise throws the {@link Exception} that occured during testPassword.
   * @param username the username input by the user
   * @param password the password input by the user
   * @return the instance of the SingletonLoginHandler
   * @throws Exception The Exception that occured during testPassword
   */
  public static SingletonLoginHandler getInstance(final String username, final String password) throws Exception{ 
      if (SingletonLoginHandler.instance == null) {
        if (testPassword(username, password)){
            //test password already does this
//          Singleton.instance = new Singleton ();
//          encoding = Utils.bytetoString(Base64.encodeBase64((name + ":" + pw).getBytes()));
        }
        else{
            throw new Exception(errorMessage); //Control catches this and prints it out
        }
      }
      else{
          System.err.println("There already exists a LoginHandler; I'll give it to you");
      }
      
      return SingletonLoginHandler.instance;
  }
  
  /**
   * Sends a small request, trying the username and password. If the request works, instance and encoding are set to the according values.
   * If it does not work, they are set back to null.
   * @param username the username input by the user
   * @param password the password input by the user
   * @return true if username and password are valid, false if they are not
   */
  private static boolean testPassword(String username, String password){
        instance = new SingletonLoginHandler();
        encoding = buildEncoding(username, password);
        

        Intern testResult = new RequestHandler(ModelUtils.TEST_ENCODING_LINK, instance).request();
        if(testResult == null){
            instance = null;
            encoding = null;
            return false;
        }
        else{
            return true;
        }
  }

  /**
   * This method is used to get the Exception that occurred during the small request in testPassword. 
   * If the updates from the {@link RequestHandler} contain an exception, it sets the {@link #errorMessage} to the message of that exception.
   * @param message The RequestEvent describing the current state of the {@link RequestNotifier}
   * @param notifier The notifier itself; not needed by this implementation; still included, since it is defined in the interface.
   */
    @Override
    public void update(RequestEvent message, RequestNotifier notifier) {
        if(message.getException() != null){
            if(message.getException() instanceof IOException){
                errorMessage = ("IOException, probably no connection to the internet.\nExplicit errormessage: " + message.getException());
            }
            else if (message.getException() instanceof InvalidInputException){
                errorMessage = ("Invalid username or password.\nIf you forgot your password, go to proteomicsdb.org, click on \"Login\" and select \"Forgot password\"\n");
            }
            else{
                errorMessage = (message.getException().getMessage());
            }
        }
    }
  
  
  
  /**
   * Returns the encoding needed to authenticate requests.
   * @return the encoding needed to authenticate requests.
   * @throws NullPointerException if instance has not yet been set, so if no valid username and password has been input yet.
   */
  public static String getEncoding() throws NullPointerException{ 
      return instance.encoding;
  }
  
  /**
   * Returns the String: {"Encoding: " + encoding} where encoding is the {@link #encoding}
   * @return the String: {"Encoding: " + encoding} where encoding is the {@link #encoding}
   */
  public String toString (){
      return "Encoding: " + encoding;
  }
  
  /**
   * This method sets {@link #instance} to null. If it already is null, throws a {@link NullPointerException}. Used to log the user out.
   * @throws NullPointerException If {@link #instance} is null.
   */
  public static void reset() throws NullPointerException{
      if (instance== null){
          throw new NullPointerException();
      }
      instance=null;
  }
  
  /**
   * Returns a boolean indicating whether instance is not null. 
   * @return True if instance has been set, false if it is null.
   */
  public static boolean hasInstance(){
      return instance!=null;
  }

  /**
   * Builds the encoding out of the username and the password.
   * It does NOT set the encoding; this can only be done by {@link #getInstance(java.lang.String, java.lang.String)}.
   * @param username
   * @param password
   * @return The encoding built out of the username and password
   */
  public static String buildEncoding(String username, String password){
      byte[] byte_array = Base64.encodeBase64((username + ":" + password).getBytes());
      String file_string = "";
      for (int i = 0; i < byte_array.length; i++) {
          file_string += (char) byte_array[i];
      }
      return file_string;
  }
}