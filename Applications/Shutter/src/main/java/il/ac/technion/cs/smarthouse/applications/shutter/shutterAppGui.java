package il.ac.technion.cs.smarthouse.applications.shutter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.sensors.shutter.gui.ShutterSensorSimulator;
import il.ac.technion.cs.smarthouse.system.applications.api.SmartHouseApplication;
import il.ac.technion.cs.smarthouse.system.exceptions.SensorNotFoundException;
import il.ac.technion.cs.smarthouse.system.services.ServiceType;
import il.ac.technion.cs.smarthouse.system.services.sensors_service.SensorData;
import il.ac.technion.cs.smarthouse.system.services.sensors_service.SensorLostRuntimeException;
import il.ac.technion.cs.smarthouse.system.services.sensors_service.SensorsManager;

public class shutterAppGui extends SmartHouseApplication implements shutterAppListner {

	private static Logger log = LoggerFactory.getLogger(shutterAppGui.class);
	private shutter_app_uiController shuttercntrl;
	boolean shutterOpen;
	int fromTime;
	int toTime;
	SensorsManager sensorsManager;

	public static void main(String[] args) throws Exception {
		launch(ShutterSensorSimulator.class);
	}

	@Override
	public void onLoad() throws Exception {
		log.debug("App starting - in onLoad");

		sensorsManager = (SensorsManager) super.getService(ServiceType.SENSORS_SERVICE);
		shuttercntrl = super.setContentView("shutter_app_ui.fxml");
		shuttercntrl.addListener(this);
	}

	@Override
	public String getApplicationName() {
		return "Shutters Application";

	}

	@Override
	public void sensorAppAction(shutter_app_uiController.eAction a) { //
		Map<String, String> instruction = new HashMap<>();

		switch (a) {
		case openShutters: {
			instruction.put("open", true + "");
			break;
		}
		case closeShutters: {
			instruction.put("close", true + "");
			break;
		}

		default:
			return;
		}
		try {
			sensorsManager.getDefaultSensor(ShutterSensor.class, "iShutter").instruct(instruction);
		} catch (SensorLostRuntimeException | SensorNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ShutterSensor extends SensorData {
	private boolean open;
	private int fromTime;
	private int toTime;

	boolean isOpen() {
		return open;
	}

	int fromTime() {
		return fromTime;
	}

	int toTime() {
		return toTime;
	}
}