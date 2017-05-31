package il.ac.technion.cs.smarthouse.applications.shutter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.event.ActionEvent;

/** This interface necessary to handle the shutter_app_uiController events
 *  in the shutterAppGui class.
 * application.
 * @author Alex
 * @since 31.5.17 */
interface shutterAppListner {
	void sensorAppAction(shutter_app_uiController.eAction act);
}
/** This class is manage the Shutters application 
 * application.
 * @author Alex
 * @since 31.5.17 */
public class shutter_app_uiController implements Initializable {

	@FXML
	private Button openButton;
	@FXML
	private Button closeButton;
	@FXML
	private Button autoButton;
	@FXML
	private TextField fromField;
	@FXML
	private TextField toField;

	public boolean shutterOpen;

	public enum eAction {
		openShutters, closeShutters, autoShutters
	};

	private ArrayList<shutterAppListner> listeners = new ArrayList<shutterAppListner>();

	public void addListener(shutterAppListner toAdd) {
		listeners.add(toAdd);
	}

	// Event Listener on Button[#openButton].onAction
	@FXML
	public void openClick(ActionEvent event) {
		shutterOpen = true;
		for (shutterAppListner Listner : listeners) {
			Listner.sensorAppAction(eAction.openShutters);
		}
	}

	// Event Listener on Button[#closeButton].onAction
	@FXML
	public void closeClick(ActionEvent event) {
		shutterOpen = false;
		for (shutterAppListner Listner : listeners) {
			Listner.sensorAppAction(eAction.closeShutters);
		}
	}

	@FXML
	public void autoClick(ActionEvent event) {

		LocalDateTime currentTime = LocalDateTime.now();
		int currentHour = currentTime.getHour(), from = Integer.parseInt(fromField.getText()),
				to = Integer.parseInt(toField.getText());
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LocalDateTime alarm = currentTime;
		eAction act = eAction.openShutters;
		Date date;
		if (to > 23 || to < 0 || from > 23 || from < 0) {// TODO: check more
															// edge cases.
		} else
			try {
				if (currentHour <= to && currentHour >= from) {
					shutterOpen = true;
					act = eAction.openShutters;
					date = formater.parse(alarm.getYear() + "-" + alarm.getMonthValue() + "-" + alarm.getDayOfMonth()
							+ " " + to + ":00:00");
					System.out.println("date: " + date.toString());
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							shutterOpen = false;
							for (shutterAppListner Listner : listeners) {
								Listner.sensorAppAction(eAction.closeShutters);
							}
						}
					}, date);
				} else {
					shutterOpen = false;
					act = eAction.closeShutters;
					alarm = currentHour <= to ? currentTime : currentTime.plusDays(1);
					date = formater.parse(alarm.getYear() + "-" + alarm.getMonthValue() + "-" + alarm.getDayOfMonth()
							+ " " + from + ":00:00");
					System.out.println("date: " + date.toString());
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							shutterOpen = true;
							for (shutterAppListner Listner : listeners) {
								Listner.sensorAppAction(eAction.openShutters);
							}
						}
					}, date);
				}
				for (shutterAppListner Listner : listeners)
					Listner.sensorAppAction(act);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	}

	public String getFromTime() {
		return fromField.toString();
	}

	public String getTomTime() {
		return toField.toString();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
