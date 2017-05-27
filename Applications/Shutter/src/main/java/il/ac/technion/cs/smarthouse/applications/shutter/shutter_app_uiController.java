package il.ac.technion.cs.smarthouse.applications.shutter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;

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
	@FXML
	private Text statusField;

	// Event Listener on Button[#openButton].onAction
	@FXML
	public void openClick(ActionEvent event) {
		statusField.setText("open");
	}
	// Event Listener on Button[#closeButton].onAction
	@FXML
	public void closeClick(ActionEvent event) {
		statusField.setText("closed");
	}
	// Event Listener on Button[#autoButton].onAction
	@FXML
	public void autoClick(ActionEvent event) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());  
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		
		if(Integer.parseInt(fromField.getText()) <= hours  &&  Integer.parseInt(toField.getText()) >= hours){
			statusField.setText("open");
			
		}
		else {
			statusField.setText("close");
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
}
