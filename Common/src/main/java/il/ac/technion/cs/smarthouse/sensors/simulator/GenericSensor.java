package il.ac.technion.cs.smarthouse.sensors.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.sensors.InteractiveSensor;
import il.ac.technion.cs.smarthouse.sensors.PathType;

/**
 * @author Elia Traore
 * @since Jun 17, 2017
 */
public class GenericSensor {
	private static Logger log = LoggerFactory.getLogger(GenericSensor.class);

	@SuppressWarnings("rawtypes")
	private class MsgStreamerThread extends Thread {
		private Map<String, List> ranges;
		private Boolean keepStreaming = true;

		public MsgStreamerThread(final Map<String, List> ranges) {
			this.ranges = ranges;
		}

		@Override
		public void interrupt() {
			keepStreaming = false;
			log.debug("streamer was interruped. Stopping.");
			super.interrupt();
		}

		@Override
		public void run() {
			if (!legalRanges())
				return;
			while (keepStreaming) {
				Map<String, Object> data = new HashMap<>();
				ranges.keySet().forEach(path -> data.put(path, random(path)));
				sendMessage(data);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					keepStreaming = false;
				}
			}
		}

		@SuppressWarnings("unchecked")
		private boolean legalRanges() {
			return paths.get(PathType.INFO_SENDING).keySet().stream().map(path -> {
				Class c = paths.get(PathType.INFO_SENDING).get(path);
				List vals = ranges.get(path);
				Boolean sizeOk = Integer.class.isAssignableFrom(c) || Double.class.isAssignableFrom(c)
						? vals.size() == 2 : true,
						valsOk = (Boolean) vals.stream().map(o -> c.isAssignableFrom(o.getClass()))
								.reduce((x, y) -> (Boolean) x && (Boolean) x).orElse(true);
				return sizeOk && valsOk;
			}).reduce((x, y) -> x && y).orElse(false);
		}

		private Object random(String path) {
			Class c = paths.get(PathType.INFO_SENDING).get(path);
			List vals = ranges.get(path);
			if (Integer.class.isAssignableFrom(c))
				return ThreadLocalRandom.current().nextInt((Integer) vals.get(0), (Integer) vals.get(1));
			return Double.class.isAssignableFrom(c)
					? ThreadLocalRandom.current().nextDouble((Double) vals.get(0), (Double) vals.get(1))
					: vals.get(ThreadLocalRandom.current().nextInt(vals.size()));
		}
	}

	private Map<PathType, List<Consumer<String>>> loggers = new HashMap<>();
	private Map<PathType, Map<String, Class>> paths = new HashMap<>();
	private Boolean interactive = false, connected = false;
	private Long pollInterval = TimeUnit.SECONDS.toMillis(5);

	private Map<String, List> lastReceivedRanges;

	private InteractiveSensor sensor;

	private MsgStreamerThread msgStreamer;

	GenericSensor() {
	}

	GenericSensor(GenericSensor other) {
		if (other.loggers != null)
			loggers = new HashMap<>(other.loggers);

		paths = new HashMap<>();
		Stream.of(PathType.values()).filter(t -> other.paths.containsKey(t))
				.forEach(t -> paths.put(t, new HashMap<>(other.paths.get(t))));
		interactive = other.interactive;
		connected = other.connected;
		pollInterval = other.pollInterval;
		if (lastReceivedRanges != null)
			lastReceivedRanges = new HashMap<>(other.lastReceivedRanges);
	}

	private void connectIfNeeded() {
		if (connected)
			return;
		while (!sensor.register())
			;
		if (interactive) {
			while (!sensor.registerInstructions())
				;
			sensor.pollInstructions(pollInterval);
		}
		connected = true;
	}

	// ------------------------ setters --------------------------------------
	void addPath(PathType t, String path, Class pathClass) {
		if (!paths.containsKey(t))
			paths.put(t, new HashMap<>());
		paths.get(t).put(path, pathClass);
		interactive |= PathType.INSTRUCTION_RECEIVING.equals(t);
	}

	void addRange(String path, @SuppressWarnings("rawtypes") List values) {
		if (lastReceivedRanges == null)
			lastReceivedRanges = new HashMap<>();
		lastReceivedRanges.put(path, values);
	}

	void addLogger(PathType t, Consumer<String> logger) {
		if (!loggers.containsKey(t))
			loggers.put(t, new ArrayList<>());
		loggers.get(t).add(logger);
	}

	void setPollingInterval(Long milliseconds) {
		pollInterval = milliseconds;
	}

	void setSensor(InteractiveSensor s) {
		sensor = s;
	}

	// ------------------------ getters --------------------------------------
	List<String> getPaths(PathType t) {
		return Optional.ofNullable(paths.get(t)).map(ps -> new ArrayList<>(ps.keySet())).orElse(new ArrayList<>());
	}

	InteractiveSensor getSensor() {
		return sensor;
	}

	// ------------------------ logging --------------------------------------
	void logInstruction(String path, String inst) {
		Optional.ofNullable(loggers.get(PathType.INSTRUCTION_RECEIVING)).ifPresent(ls -> ls
				.forEach(logger -> logger.accept("Received instruction:\n\t" + inst + "\nOn path:\n\t" + path)));
	}

	void logMsgSent(Map<String, String> data) {
		final List<String> $ = new ArrayList<>();
		$.add("Sending following msg:\n");
		data.keySet().forEach(path -> $.add("path:" + path + "\tvalue:" + data.get(path)));

		$.stream().reduce((x, y) -> x + y)
				.ifPresent(formatedData -> Optional.ofNullable(loggers.get(PathType.INSTRUCTION_RECEIVING))
						.ifPresent(ls -> ls.forEach(logger -> logger.accept(formatedData))));
	}

	// ------------------------ Data sending methods -------------------------
	/**
	 * the list object is interperated by the path (key type) if the type is
	 * string or boolean, the list contains all the legal values if the type is
	 * double or integer, the list contains (low,high) so that low <= legal
	 * values < high
	 */
	void streamMessages(final Map<String, List> ranges) {
		if (!paths.containsKey(PathType.INFO_SENDING))
			return;

		lastReceivedRanges = ranges;

		if (msgStreamer != null) {
			msgStreamer.interrupt();
			try {
				msgStreamer.join();
			} catch (InterruptedException e) {
				log.warn("Simulator was interrupted will waiting for a msg streamer");
			}
		}

		msgStreamer = new MsgStreamerThread(ranges);
		msgStreamer.start();
	}

	/**
	 * streams with the ranges given through the builder
	 */
	void streamMessages() {
		if (lastReceivedRanges == null)
			return;
		streamMessages(lastReceivedRanges);
	}

	// ------------------------ public method -------------------------------
	/** blocks until an instruction is sent */
	public void waitForInstruction() {
		while (!sensor.operate())
			;
	}

	/** blocking method */
	public GenericSensor connect() {
		connectIfNeeded();
		return this;
	}

	/**
	 * will also connect if sensor not connected yet
	 */
	public void sendMessage(Map<String, Object> data) {// todo: change to
														// package level?
		connectIfNeeded();

		if (!paths.containsKey(PathType.INFO_SENDING))
			return;
		Map<String, String> d = new HashMap<>();
		data.keySet().forEach(k -> d.put(k, data.get(k) + ""));
		sensor.updateSystem(d);
		logMsgSent(d);
	}

	public String getCommname() {
		return sensor.getCommname();
	}

	public String getId() {
		return sensor.getId();
	}

	public String getAlias() {
		return sensor.getAlias();
	}

	public List<String> getObservationSendingPaths() {
		return sensor.getObservationSendingPaths();
	}

	public List<String> getInstructionRecievingPaths() {
		return sensor.getInstructionRecievingPaths();
	}

}
