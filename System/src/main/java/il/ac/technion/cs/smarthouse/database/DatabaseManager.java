package il.ac.technion.cs.smarthouse.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.system.file_system.FileSystemEntries;
import il.ac.technion.cs.smarthouse.system.file_system.PathBuilder;

/**
 * 
 * @author Inbal Zukerman
 * @date May 13, 2017
 */
public class DatabaseManager implements DatabaseAPI {

    public static String parseClass = "mainDB";
    public static String pathCol = "path";
    public static String valueCol = "value";

    private final ServerManager serverManager = new ServerManager();

    private static Logger log = LoggerFactory.getLogger(DatabaseManager.class);

    @Override
    public ParseObject addInfo(final String path, final Object value) throws ParseException {
        serverManager.initialize();

        final Map<String, Object> m = new HashMap<>();
        m.put(pathCol, path);
        m.put(valueCol, value.toString());

        return serverManager.putValue(parseClass, m);

    }

    @Override
    public void deleteInfo(final FileSystemEntries fsEntry) {
        serverManager.initialize();

        final ParseQuery<ParseObject> findQuery = ParseQuery.getQuery(parseClass);
        findQuery.whereStartsWith(pathCol, fsEntry.toString().toLowerCase());

        try {
            for (final ParseObject iterator : findQuery.find())
                serverManager.deleteById(parseClass, iterator.getObjectId());

        } catch (final ParseException e) {
            log.error("No matching object was found on the server", e);
        }

    }

    @Override
    public void deleteInfo(final String... path) {
        serverManager.initialize();

        final ParseQuery<ParseObject> findQuery = ParseQuery.getQuery(parseClass);
        findQuery.whereMatches(pathCol, PathBuilder.buildPath(path).toLowerCase());

        try {
            for (final ParseObject iterator : findQuery.find())
                serverManager.deleteById(parseClass, iterator.getObjectId());

        } catch (final ParseException e) {
            log.error("No matching object was found on the server", e);
        }

    }

    @Override
    public String getLastEntry(final String... path) {

        final ParseQuery<ParseObject> findQuery = ParseQuery.getQuery(parseClass);

        findQuery.whereStartsWith(pathCol, PathBuilder.buildPath(path).toLowerCase());

        try {
            if (findQuery.find() != null) {
                findQuery.orderByDescending("createdAt");

                return findQuery.find().get(0).getString(pathCol) + PathBuilder.SEPARATOR
                                + findQuery.find().get(0).getString(valueCol);
            }
        } catch (final ParseException e) {
            log.error("A Parse exception has occured", e);
        }

        return "";

    }

    @Override
    public Collection<String> getPathChildren(String... path) {
        Collection<String> res = new ArrayList<>(); // TODO: inbal
        try {
            final ParseQuery<ParseObject> findQuery = ParseQuery.getQuery(parseClass);

            findQuery.whereStartsWith(pathCol, PathBuilder.buildPath(path).toLowerCase());

            if (findQuery.find() != null) {
                System.out.println("find size is: " + findQuery.find().size());
                for (final ParseObject iterator : findQuery.find())
                    res.add(iterator.getString(pathCol).replaceAll(PathBuilder.buildPath(path), "")); // TODO
                                                                                                      // inbal

            }

        } catch (final ParseException e) {
            // TODO: inbal
        }

        return res;
    }

}
