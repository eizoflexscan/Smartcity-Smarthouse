package il.ac.technion.cs.smarthouse.system.gui.mapping;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.mvp.GuiController;
import il.ac.technion.cs.smarthouse.mvp.system.SystemGuiController;
import il.ac.technion.cs.smarthouse.mvp.system.SystemMode;
import il.ac.technion.cs.smarthouse.system.SystemCore;
import il.ac.technion.cs.smarthouse.system.file_system.FileSystemEntries;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MappingController extends SystemGuiController {
    private static Logger log = LoggerFactory.getLogger(MappingController.class);
    private static final int ROOM_IN_ROW = 4;
    private static final int MARGIN = 20;
    private static final int HEIGHT = 150;
    private static final int WIDTH = 160;
    private List<String> allLocations = new ArrayList<>();
    private final Map<String, SensorInfoController> sensors = new HashMap<>();
    private final Map<String, List<String>> locationsContents = new HashMap<>();
    private final Map<String, String> sensorsLocations = new HashMap<>();
    private final House house = new House();
    private int roomNumbers;
    int xPlusRoom;
    int yPlusRoom;

    @FXML private VBox sensorsPaneList;
    @FXML private Canvas canvas;

    void addRoom(String roomName) {
        allLocations.add(roomName);
        house.addRoom(new Room(MARGIN + (roomNumbers % ROOM_IN_ROW) * WIDTH,
                        MARGIN + (roomNumbers / ROOM_IN_ROW) * HEIGHT, WIDTH, HEIGHT, roomName));
        ++roomNumbers;
        sensors.values().forEach(e->e.updateRooms());
        drawMapping();
    }

    @Override
    protected <T extends GuiController<SystemCore, SystemMode>> void initialize(SystemCore model, T parent,
                    SystemMode m, URL location, ResourceBundle b) {
        log.debug("initialized the map gui");

        canvas.setWidth(2000);
        canvas.setHeight(2000);

        addRoom("UNDEFINED");

        log.debug("subscribed to sensors root");
        // this is somewhat whiteboxing. todo: reactor nicer.
        model.getFileSystem().subscribe((p, l) -> {
            log.debug("map gui was notified on (path,val)=(" + p + "," + l + ")");
            final String commname = p.split("\\.")[1];
            final String id = p.split("\\.")[2];
            if (l != null && allLocations.contains(l) && !sensors.containsKey(id))
                Platform.runLater(() -> {
                    try {
                        addSensor(id, commname);
                    } catch (final Exception e) {
                        log.warn("failed to add sensor: " + id + " (received path " + p
                                        + ") to GuiMapping.\nGot execption" + e);
                    }
                });

        }, FileSystemEntries.SENSORS.buildPath(""));
        log.debug("finished initialized the map gui");

    }

    public void addSensor(final String id, final String commName) throws Exception {
        if (sensors.containsKey(id))
            return;
        final SensorInfoController controller = createChildController("sensor_info.fxml");
        sensorsPaneList.getChildren().add(controller.getRootViewNode());

        controller.setId(id).setName(commName).setMapController(this);
        sensors.put(id, controller);
    }

    public void updateSensorLocation(final String id, final String l) {
        if (sensorsLocations.containsKey(id) && locationsContents.containsKey(sensorsLocations.get(id)))
            locationsContents.get(sensorsLocations.get(id)).remove(id);

        sensorsLocations.put(id, l);

        if (!locationsContents.containsKey(l))
            locationsContents.put(l, new ArrayList<>());

        locationsContents.get(l).add(id);

        drawMapping();

    }

    public List<String> getAlllocations() {
        return allLocations;
    }

    private void drawMapping() {
        final GraphicsContext g = canvas.getGraphicsContext2D();

        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g.setFont(new Font(14.0));
        g.setStroke(Color.BLACK);

        for (final Room room : house.getRooms()) {
            g.strokeRect(room.x, room.y, room.width, room.height);
            g.strokeLine(room.x, room.y + 20, room.x + room.width, room.y + 20);
            g.setFill(Color.BLACK);
            g.fillText(room.location, room.x + 4, room.y + 15);
            if (locationsContents.containsKey(room.location)) {
                int dy = 20;

                for (final String id : locationsContents.get(room.location)) {
                    g.fillText(" (" + id + ")", room.x + 10, room.y + dy + 20);
                    dy += 20;
                }
            }
        }

        xPlusRoom = MARGIN + (roomNumbers % ROOM_IN_ROW) * WIDTH;
        yPlusRoom = MARGIN + (roomNumbers / ROOM_IN_ROW) * HEIGHT;
        g.strokeRect(xPlusRoom, yPlusRoom, WIDTH, HEIGHT);
        g.setFont(new Font(45.0));
        g.fillText("+", xPlusRoom + 70, yPlusRoom + 75);
        g.setFill(Color.BLUE);
        g.setFont(new Font(14.0));
        canvas.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getX();
            double y = mouseEvent.getY();
            if (x > xPlusRoom && x < xPlusRoom + WIDTH && y > yPlusRoom && y < yPlusRoom + HEIGHT) {
                final TextInputDialog dialog = new TextInputDialog("sensor name");
                dialog.setTitle("Create Room");
                dialog.setHeaderText("Config your smarthouse");
                dialog.setContentText("Please enter room name:");
                final Optional<String> result = dialog.showAndWait();
                if (!result.isPresent())
                    return;
                final String name = result.get();
                addRoom(name);
            }
        });

    }
}
