package il.ac.technion.cs.eldery.sensors.vitals;

import java.util.HashMap;
import java.util.Map;

import il.ac.technion.cs.eldery.sensors.Sensor;

/** @author Yarden
 * @since 16.1.17 */
public class VitalsSensor extends Sensor {
    public VitalsSensor(final String id, final String commName, final String systemIP, final int systemPort) {
        super(id, commName, systemIP, systemPort);
    }

    public void updateSystem(final int pulse, final int bloodPressure) {
        final Map<String, String> data = new HashMap<>();
        data.put("pulse", pulse + "");
        data.put("blood pressure", bloodPressure + "");
        super.updateSystem(data);
    }

    @Override public String[] getObservationsNames() {
        return new String[] { "pulse", "blood pressure" };
    }

}
