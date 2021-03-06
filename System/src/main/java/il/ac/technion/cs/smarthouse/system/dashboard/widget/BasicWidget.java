package il.ac.technion.cs.smarthouse.system.dashboard.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import il.ac.technion.cs.smarthouse.system.dashboard.InfoCollector;
import il.ac.technion.cs.smarthouse.system.dashboard.WidgetType;
import il.ac.technion.cs.smarthouse.system.file_system.FileSystem;
import il.ac.technion.cs.smarthouse.system.file_system.FileSystemEntries;

/**
 * @author Elia Traore
 * @since Jun 2, 2017
 */
public abstract class BasicWidget {
    private static Logger log = LoggerFactory.getLogger(BasicWidget.class);
    protected final WidgetType type;
    protected final Double tileSize;
    protected InfoCollector data;
    protected TileBuilder builder;
    protected Tile tile;

    public BasicWidget(final WidgetType type, final Double tileSize, final InfoCollector data) {
        this.type = type;
        this.tileSize = tileSize;
        this.data = data;
        builder = type.createTileBuilder(tileSize).title(getTitle());
    }

    public abstract String getTitle();

    public void updateAutomaticallyFrom(final FileSystem s) {
        data.getInfoEntries().keySet().forEach(path -> updateAutomaticallyFrom(s, path));
    }

    protected Double cast(final Object data) {
        final String sdata = (String) data;
        try {
            return Double.valueOf(sdata);
        } catch (NumberFormatException | ClassCastException e) {}
        try {
            return Integer.valueOf(sdata) + 0.0;
        } catch (final NumberFormatException e) {}
        try {
            return Boolean.valueOf(sdata) ? 1.0 : 0.0;
        } catch (final NumberFormatException e) {}
        return null;

    }

    protected void updateAutomaticallyFrom(final FileSystem s, final String path) {
        s.subscribe((rPath, data) -> update(cast(data), path), FileSystemEntries.SENSORS_DATA.buildPath(path));
    }

    public void update(final Double value, final String key) {
        getTile().setValue(value);
    }

    public Tile getTile() {
        if (tile == null) {
            tile = builder.build();
            setSize(getTileSize());
        }
        return tile;
    }

    public Double getTileSize() {
        return tileSize;
    }

    public WidgetType getType() {
        return type;
    }

    public InfoCollector getInitalInfo() {
        return data;
    }

    public void setSize(final double tileSize2) {
        tile.setMaxSize(tileSize2, tileSize2);
        tile.setMinSize(tileSize2, tileSize2);
    }
}
