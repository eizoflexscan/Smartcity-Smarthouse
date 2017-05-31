package il.ac.technion.cs.smarthouse.applications.shutter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.awt.List;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import il.ac.technion.cs.smarthouse.system.services.ServiceType;
import il.ac.technion.cs.smarthouse.system.services.sensors_service.SensorsManager;
import javafx.event.ActionEvent;
import javafx.event.Event;

interface sensorAppListner{
	void sensorAppAction(shutter_app_uiController.eAction act);
}

public class shutter_app_uiController implements Initializable {
	SensorsManager sensorsManager;
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
	
	public enum eAction{openShutters,closeShutters,autoShutters};
//	public ActionEvent openShutters;
//	public ActionEvent closeShutters;
//	public ActionEvent autoShutters;
	private ArrayList<sensorAppListner> listeners= new ArrayList<sensorAppListner>();
	public void addListener(sensorAppListner toAdd) {
	   listeners.add(toAdd);
	}
	// Event Listener on Button[#openButton].onAction
	@FXML
	public void openClick(ActionEvent event) {
		shutterOpen = true;
		for (sensorAppListner Listner : listeners) {
			Listner.sensorAppAction(eAction.openShutters);
		}
	}
	// Event Listener on Button[#closeButton].onAction
	@FXML
	public void closeClick(ActionEvent event) {
		shutterOpen = false;
		for (sensorAppListner Listner : listeners) {
			Listner.sensorAppAction(eAction.closeShutters);
		}
	}
	@FXML
	public void autoClick(ActionEvent event) {
//		for (sensorAppListner Listner : listeners) {
//			Listner.sensorAppAction(eAction.autoShutters);
//		}
//		
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(new Date());  
//		int hours = cal.get(Calendar.HOUR_OF_DAY);
//		
//		if(Integer.parseInt(fromField.getText()) <= hours  &&  Integer.parseInt(toField.getText()) >= hours){
//			statusField.setText("open");
//			
//		}
//		else {
//			statusField.setText("close");
//		}
	}
	public String getFromTime(){
		return fromField.toString();
	}
	public String getTomTime(){
		return toField.toString();
	}	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
}
