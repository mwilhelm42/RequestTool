package proteomicsdb.org.requesttool.view;

import java.util.Vector;
import proteomicsdb.org.requesttool.model.exceptions.InvalidInputException;


public class DropdownItem {

    private String label, value;
    
    public DropdownItem(String aLabel, String aValue ){
        this.label = aLabel;
        this.value = aValue;
    }
    
    @Override
    public String toString(){
        return this.label;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }
    
    public static Vector<DropdownItem> getVectorOfDropdownItems(Vector<String> labels, Vector<String> values) throws InvalidInputException{
        if(labels.size()!=values.size()){
            throw new InvalidInputException("The Input of some Dropdownmenu", "Vectors of different length; this is probably not your fault.", "Vectors of the same length");
        }
        Vector<DropdownItem> result = new Vector<DropdownItem>();
        
        for (int i = 0; i<labels.size(); i++){
            result.add(new DropdownItem(labels.get(i), values.get(i)));
        }
        
        return result;
    }
    
    
}
