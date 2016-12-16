package il.ac.technion.cs.eldery.system.sensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import il.ac.technion.cs.eldery.utils.Table;

/**
 * 
 * @author Inbal Zukerman
 * @since 16.12.2016
 */
public class SensorInfo{
    
    private Table<Object, Object> information = new Table<>();
    List<Consumer<HashMap<Object, Object>>> listeners = new ArrayList<>();
    
    /*
     * Creates SensorInfo without information capacity limit
     */
    public SensorInfo() { }
    
    /**
     * @param infoLimit - information capacity limit to enforce
     */
    public SensorInfo(final int infoLimit) {
        information.changeMaxCapacity(infoLimit);
    }
    
    private SensorInfo(final Table<Object, Object> information){
        this.information = information;
    }
    
    
    public void addListener(final Consumer<HashMap<Object, Object>> listener){
        listeners.add(listener);
    }
    
    public void addRecord(final HashMap<Object, Object> record){
        information.addEntry(record);
    }
    
    public int getIfoLimit(){
        return information.getMaxCapacity();
    }
    
    public int getNumRecords(){
        return information.getCurrentCapacity();
    }
    
    public void changeInfoLimit(final int newLimit){
        information.changeMaxCapacity(newLimit);
    }
    
    public void unlimitInfo(){
        information.disableCapacityLimit();
    }
    
    public SensorInfo getLastKRecords (final int k){
        return new SensorInfo(information.receiveKLastEntries(k));
    }
    
    public Map<Object, Object> getLastRecord(){
        return information.getLastEntry();
    }
   
    public Object getLastRecordAtCol(final Object colName){
        return information.getLastEntryAtCol(colName);
    }
}
