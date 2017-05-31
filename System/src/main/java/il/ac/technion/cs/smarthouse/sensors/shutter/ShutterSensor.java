package il.ac.technion.cs.smarthouse.sensors.shutter;

import java.util.HashMap;
import java.util.Map;

import il.ac.technion.cs.smarthouse.sensors.InteractiveSensor;

/** This class represents shutters sensor and contains its logic.
 * @author Alex
 * @since 8.5.17 */
public class ShutterSensor extends InteractiveSensor {

    public ShutterSensor(String id, String commName, String systemIP, int systemPort , int instPort) {
        super(id, commName, systemIP, systemPort, instPort);

       
    }
    
    
    public void updateSystem(final boolean open, final int fromTime , final int toTime) {
        final Map<String, String> data = new HashMap<>();
        data.put("open", open + "");
        data.put("fromTime", fromTime + "");
        data.put("toTime", toTime + "");       
        super.updateSystem(data);
    }
    
    

    @Override public String[] getObservationsNames() {
        return new String[] { "open", "fromTime", "toTime" };
       
    }

}
