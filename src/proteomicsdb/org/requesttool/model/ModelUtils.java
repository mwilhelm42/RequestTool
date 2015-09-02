package proteomicsdb.org.requesttool.model;

import proteomicsdb.org.requesttool.model.description.InputConfigurationValidated;
import proteomicsdb.org.requesttool.model.description.OutputConfiguration;
import proteomicsdb.org.requesttool.model.login.SingletonLoginHandler;
import proteomicsdb.org.requesttool.view.SettingsWindow;



/**
 *
 * @author Maxi
 */
public class ModelUtils {
    /**
     * The links used to call the Meta-API
     */
    public static final String INITIALISE_OUTPUT_COLS_LINK = "https://www.proteomicsdb.org/proteomicsdb/logic/api/apioutputcolumns.xsodata/AT_API_OUTPUT_COLUMNS?$select=API_FUNCTION_ID,PROCEDURE_NAME,API_NAME,API_TITLE,API_DESCRIPTION,API_EXAMPLE_URL,OUTPUT_NAME,OUTPUT_DESCRIPTION,OUTPUT_TYPE,OUTPUT_EXAMPLE&$format=json";
    public static final String INITIALISE_INPUT_PARAMS_LINK = "https://www.proteomicsdb.org/proteomicsdb/logic/api/apiinputparameters.xsodata/AT_API_INPUT_PARAMETER?$select=API_FUNCTION_ID,PROCEDURE_NAME,API_NAME,API_TITLE,API_DESCRIPTION,API_EXAMPLE_URL,API_INPUT_ID,INPUT_NAME,INPUT_DESCRIPTION,INPUT_TYPE,INPUT_EXAMPLE,PREDEFINED_VALUE,PREDEFINED_NAME,PREDEFINED_DESCRIPTION&$format=json";
    
    /**
     * The link used by the {@link SingletonLoginHandler} to start its small test request. One of the Meta-API links is used to ensure, that this is always a valid link.
     */
    public static final String TEST_ENCODING_LINK = INITIALISE_INPUT_PARAMS_LINK;
    
    /**
     * The first part that is used when building the link in the {@link InputConfigurationValidated}. This is static, so that transferring the Request Tool to another OData service is easy.
     */
    public static final String FIRST_PART_OF_THE_LINK = "https://www.proteomicsdb.org/proteomicsdb/logic/api/";
    
    /**
     * The character that is used to separate multiple inputs for batch jobs.
     */
    public static final String BATCH_JOB_SEPARATOR = "/";
    
    /**
     * A flag to indicate, whether batch jobs are currently allowed. This is set by the {@link SettingsWindow}, breaking the model view control pattern a little.
     * It is a static flag rather than a flag in every {@link OutputConfiguration}, because it is the same for all requests that are started at a time, and saves some space in the OutputConfigurations.
     */
    public static boolean DO_BATCH_JOB = true;

}
