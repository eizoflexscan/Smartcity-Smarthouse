package il.ac.technion.cs.smarthouse.sensors.shutter.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

import il.ac.technion.cs.smarthouse.sensors.shutter.ShutterSensor;
import il.ac.technion.cs.smarthouse.utils.Random;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

/**
 * This class is responsible for the visual part and the control of the shutters
 * sensor.
 * 
 * @author Alex
 * @since 9.05.17
 */
public class shutter_uiController implements Initializable {
	ShutterSensor sensor;
	@FXML
	private Button openButton;
	@FXML
	private Button closeButton;
	@FXML
	private Text stateText;

	// Event Listener on Button[#openButton].onAction
	@FXML
	public void openClick(ActionEvent e) {

		stateText.setText("Shutters Open");
	}

	// Event Listener on Button[#closeButton].onAction
	@FXML
	public void closeClick(ActionEvent e) {
		stateText.setText("Shutters Closed");
	}

	public void openShutter() {
		stateText.setText("Shutters Open");
	}

	public void closeShutter() {
		stateText.setText("Shutters Closed");
	}

	@Override
	public void initialize(URL location, ResourceBundle b) {
		sensor = new ShutterSensor(Random.sensorId(), "iShutter", "127.0.0.1", 40001, 40002);

		while (!sensor.register())
			;
		while (!sensor.registerInstructions())
			;

		sensor.setInstructionHandler(inst -> {
			String op = (inst.keySet().toArray()[0]).toString();
			switch (op) {
			case "open": {
				this.openShutter();
				return true;
			}
			case "close": {
				this.closeShutter();
				return true;
			}

			}
			return true;
		});

	}
}
