package il.ac.technion.cs.smarthouse.sensors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.networking.messages.AnswerMessage;
import il.ac.technion.cs.smarthouse.networking.messages.AnswerMessage.Answer;
import il.ac.technion.cs.smarthouse.networking.messages.MessageFactory;
import il.ac.technion.cs.smarthouse.networking.messages.RegisterMessage;
import il.ac.technion.cs.smarthouse.networking.messages.UpdateMessage;

/** This class represents a sensor that can get instructions and operate
 * accordingly.
 * @author Yarden
 * @since 31.3.17 */

/** This class was modified: the inner class InstructionChecker was removed (unnecessary).
 * added field for timerTask, so there will be only one instance of timer task.
 * moved the initialization of the timer that runs the timer task in to the c'tor so it will be initiated once! 
 * @author Alex
 * @since 31.5.17 */
public abstract class InteractiveSensor extends Sensor {
    private static Logger log = LoggerFactory.getLogger(InteractiveSensor.class);
   
    public TimerTask tTask;

    protected int instPort;
    protected Socket instSocket;
    protected PrintWriter instOut;
    protected BufferedReader instIn;
    protected InstructionHandler handler;
    protected long period;

    public InteractiveSensor(final String id, final String commName, final String systemIP, final int systemPort, final int instPort) {
        super(id, commName, systemIP, systemPort);

        this.instPort = instPort;
        this.handler = null;
        tTask = new TimerTask() {
			
			@Override
			public void run() {
				operate();				
			}
		};
		new Timer().schedule(tTask,5000, 5000);
        this.sType = SensorType.INTERACTIVE;
    }

    /** Registers the sensor its instructions TCP connection with the system.
     * This method must be called before any instructions are sent.
     * @return <code>true</code> if registration was successful,
     *         <code>false</code> otherwise */
    public boolean registerInstructions() {
        try {

            instSocket = new Socket(InetAddress.getByName(systemIP), instPort);
            instOut = new PrintWriter(instSocket.getOutputStream(), true);
            instIn = new BufferedReader(new InputStreamReader(instSocket.getInputStream()));
        } catch (final IOException e) {
            log.error("I/O error occurred when the sensor's instructions socket was created", e);
        }
        final String $ = new RegisterMessage(id, commName, sType).send(instOut, instIn);
        return $ != null && ((AnswerMessage) MessageFactory.create($)).getAnswer() == Answer.SUCCESS;
    }

    /** Sets the operation to be made when instruction is received. This method
     * must be called before sending instructions. */
    public void setInstructionHandler(InstructionHandler h) {
        this.handler = h;
    }

    /** If there is an incoming instruction, extracts it and executes it.
     * @return <code>true</code> if the instruction was completed successfully
     *         (or if there is no waiting instruction), <code>false</code>
     *         otherwise */
    public boolean operate() {
        try {
        	boolean isBuffReady = instIn.ready();
        	return isBuffReady && (isBuffReady
					&& handler.applyInstruction(((UpdateMessage) MessageFactory.create(instIn.readLine())).getData()));
        } catch (IOException e) {
            log.error("I/O error occurred", e);
            return false;
        }
    }

    /** Checks for waiting instructions by polling the connection repeatedly
     * every specified period of time.
     * @param p The polling period, in milliseconds */
    public void pollInstructions(long p) {
        this.period = p;
    }
}
