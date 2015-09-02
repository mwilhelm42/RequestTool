package proteomicsdb.org.requesttool.view;

import java.awt.Color;
import java.io.File;
import javax.swing.filechooser.FileSystemView;

public class ViewUtils {
    /**
     * The colors of the Request Tool.
     */
    public static Color BACKGROUND_COLOR_FRONT = Color.getHSBColor(0, 0, (float) 0.9607843);
    public static Color BACKGROUND_COLOR_BASIC = Color.lightGray;
    
    /**
     * The names that {@link Control} knows and checks for.
     */
    public static final String BUTTON_NAME_LOGIN = "Login";
    public static final String BUTTON_NAME_GO = "Go";
    public static final String EMPTY_DROPDOWN = "-- Select here --";
    
    public enum MenuNames {
        MENU_NAME_LOGOUT("Logout"),
        MENU_NAME_QUIT("Quit"),
        MENU_NAME_ABOUT("Help"),
        MENU_NAME_LINK("Link");        
                
        private String name;
        MenuNames(String aName){
            this.name = aName;
        }
        
        public String getName(){
            return this.name;
        }
        
        public static MenuNames stringToEnum(String s){
            MenuNames [] values = MenuNames.values();
            for(int i = 0; i<values.length;i++){
                if(s.equals(values[i].name)){
                    return values[i];
                }
            }
            return null;
        }
    }
    
    /**
     * These sizes are used by the resize method in the {@link View}
     */
    public static int START_WINDOW_HEIGHT = 800;
    public static int WINDOW_WIDTH = 900;
    public static int WINDOW_HEIGHT = 800;
    
    /**
     * Some static texts, that occur in the Request Tool.
     */
    public static final String HELPTEXT = "This is version 1.0 of the Request Tool.\n" + "It was programmed by Maxi Weininger (ga67vib@mytum.de).\n" + "For any problems, bugs or questions, please contact Mathias Wilhelm (mathias.wilhelm@tum.de)\n"; //maybe let this contain a description of how the programm works?
    public static final String LINKTEXT = "You can reach our website under the link proteomicsdb.org";
    public static String DEFAULT_OUTPUT_FILE;
    static{
        FileSystemView filesys = FileSystemView.getFileSystemView();
        File[] roots = filesys.getRoots();
        ViewUtils.DEFAULT_OUTPUT_FILE = filesys.getHomeDirectory().getPath() + File.separator + "request";
    }  
}
