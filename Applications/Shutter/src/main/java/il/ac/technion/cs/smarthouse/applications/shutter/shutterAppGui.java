package il.ac.technion.cs.smarthouse.applications.shutter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.sensors.shutter.gui.ShutterSensorSimulator;
import il.ac.technion.cs.smarthouse.system.applications.api.SmartHouseApplication;
import il.ac.technion.cs.smarthouse.system.services.ServiceType;
import il.ac.technion.cs.smarthouse.system.services.sensors_service.SensorData;
import il.ac.technion.cs.smarthouse.system.services.sensors_service.SensorsManager;

public class shutterAppGui extends SmartHouseApplication {

	private static Logger log = LoggerFactory.getLogger(shutterAppGui.class);
	private shutter_app_uiController shuttercntrl;
	boolean shutterOpen;
	int fromTime;
	int toTime;

	public static void main(String[] args) throws Exception {
		launch(ShutterSensorSimulator.class);
	}

	@Override
	public void onLoad() throws Exception {
		log.debug("App starting - in onLoad");

		SensorsManager sensorsManager = (SensorsManager) super.getService(ServiceType.SENSORS_SERVICE);
		sensorsManager.getDefaultSensor(ShutterSensor.class, "iShutter").subscribe(shutter -> {
			if (shutter.isOpen()) {
				shuttercntrl.openClick(null);
			} else {
				shuttercntrl.closeClick(null);
			}
			

		});
		shuttercntrl = super.setContentView("shutter_app_ui.fxml");
	}

	@Override
	public String getApplicationName() {
		return "Shutters Application";

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