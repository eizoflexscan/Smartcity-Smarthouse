package il.ac.technion.cs.eldery.applications.sos;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class SosController implements Initializable {

    @FXML public Label stateLabel;
    @FXML public Button killerButton;
    @FXML public AnchorPane mainPane;
    
    boolean isDead;

    @Override public void initialize(final URL arg0, final ResourceBundle arg1) {
        stateLabel.setFont(new Font("Arial", 20));
        mainPane.setStyle("-fx-background-color: green");
        killerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(final ActionEvent __) {
                if (!isDead)
                    killElder();
                else
                    respawnElder();
            }

        });
    }
    
    public void killElder() {
        stateLabel.setText("Elderly is DEAD!!!");
        killerButton.setText("Respawn");
        mainPane.setStyle("-fx-background-color: red");
        isDead = true;
    }
    
    public void respawnElder() {
        stateLabel.setText("Elderly is OK");
        killerButton.setText("Kill Elderly");
        mainPane.setStyle("-fx-background-color: green");
        isDead = false;
    }

}