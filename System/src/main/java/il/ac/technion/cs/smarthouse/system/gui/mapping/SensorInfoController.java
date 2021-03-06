package il.ac.technion.cs.smarthouse.system.gui.mapping;

import java.net.URL;
import java.util.ResourceBundle;

import il.ac.technion.cs.smarthouse.mvp.GuiController;
import il.ac.technion.cs.smarthouse.mvp.system.SystemGuiController;
import il.ac.technion.cs.smarthouse.mvp.system.SystemMode;
import il.ac.technion.cs.smarthouse.system.SystemCore;
import il.ac.technion.cs.smarthouse.system.file_system.FileSystem;
import il.ac.technion.cs.smarthouse.system.file_system.FileSystemEntries;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

public class SensorInfoController extends SystemGuiController {
    private FileSystem fileSystem;

    private String commName;
    private String id;
    @FXML private TitledPane titledPane;
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private ComboBox<String> room;
    private MappingController mapController;
    
    @Override
    protected <T extends GuiController<SystemCore, SystemMode>> void initialize(SystemCore model, T parent,
                    SystemMode m, URL location, ResourceBundle b) {
        fileSystem = model.getFileSystem();

        room.getSelectionModel().select(0);
        room.valueProperty().addListener((ov, prevVal, newVal) -> {
            this.<MappingController>getParentController().updateSensorLocation(id, newVal);
            // update model
            fileSystem.sendMessage(newVal, FileSystemEntries.LOCATION.buildPath(commName, id));
        });
    }

    public SensorInfoController setName(final String name) {
        commName = name;
        nameLabel.setText("Name: " + name);
        updateUI();

        return this;
    }
    
    public SensorInfoController setMapController(final MappingController controller){
        mapController = controller;
        room.getItems().setAll(mapController.getAlllocations());
        return this;
    }
    
    public SensorInfoController updateRooms(){
        room.getItems().setAll(mapController.getAlllocations());
        return this;
    }

    public SensorInfoController setId(final String id) {
        this.id = id;
        idLabel.setText("ID: " + id);
        updateUI();

        return this;
    }

    private void updateUI() {
        titledPane.setText(String.format("%s (ID %s)", commName, id));
    }
}
