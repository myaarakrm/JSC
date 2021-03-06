package simulation.eventbased.mediumaccess;

import simulation.eventbased.*;
import simulation.communications.nodes.*;
import simulation.communications.channels.*;
import simulation.communications.queues.*;
import simulation.communications.packets.*;
import simulation.networks.*;
import simulation.networks.nodes.*;
import simulation.networks.pointprocesses.*;
import simulation.networks.channels.*;
import simulation.utilities.structures.*;
import simulation.utilities.packetprocessors.*;
import simulation.distributions.*;

/** Class to implement ALOHA medium access control.
 * @author ykk
 */
public class ALOHA
    extends MACNode
{
    //Members
    /** Current state of node.
     * @see #stateIdle
     * @see #stateWaiting
     * @see #stateTransmitting
     * @see #stateReceiving 
     * @see #stateCollided
     */
    public int state = stateIdle; //Idle
    /** Idle state.
     */
    public static final int stateIdle = 0;
    /** Waiting state.
     */
    public static final int stateWaiting = 1;
    /** Transmitting state.
     */
    public static final int stateTransmitting = 2;
    /** Receiving state.
     */
    public static final int stateReceiving = 3;
    /** Collided state.
     */
    public static final int stateCollided = 4;
    /** Array of events node can process.
     * Events = 0 "Receive Ended",
     *          1 "Transmission Ended", 
     *          2 "Wait Ended"
     */
    public static final String[] events = {"Receive Ended", 
					   "Transmission Ended", 
					   "Wait Ended"};
    /** Number of ongoing receptions.
     */
    protected int onGoing = 0;
    /** Indicate of transmitting.
     */
    protected boolean isTransmitting = false;
    /** Distribution of random wait.
     */
    public Distribution waitTime;
    /** Currently receiving packet.
     */
    protected Object packet;
    /** Currently receiving from.
     */
    protected CommNode currSource;

    //Methods
    /** Event triggered interface.
     * State diagram is as shown below.
     * <BR><img src="doc-files/aloha.jpg"><BR>
     * @param time current time
     * @param event event string definition
     * @param simulator reference to simulator
     * @see #events
     * @see #state
     * @see #receive(CommNode source, Object packet, Simulator simulator)
     * @see #trigger(Simulator simulator)
     */
    public void run(double time, String event, Simulator simulator)
    {
	switch (Array.indexOf(events, event))
	{
	case 0: //Receive Ended
	    onGoing--;
	    scheduleWait(simulator);
	    if ((onGoing == 0) && (state != stateCollided))
		processor.receive(currSource,this,packet,queue);
	    break;
	case 1: //Transmission Ended
	    isTransmitting = false;
	    scheduleWait(simulator);
	    break;
	case 2: //Wait Ended
	    if (processor.hasPkt(queue))
		startTransmission(simulator);
	    else
		state = stateIdle;
	    break;
	default:
	    throw new RuntimeException(this+" encounters unknown event "+event);
	}
    }

    /** Schedule wait time.
     * @param simulator reference to simulator
     */
    public void scheduleWait(Simulator simulator) 
    {
	if (!isTransmitting & (onGoing == 0))
	{
	    state = stateWaiting;
	    simulator.add(new Event(simulator.time()+waitTime.getInstance(),
				    this,
				    this.events[2])); //Wait Ended Scheduled
	}
    }

    /** Start transmission.
     * @param simulator reference to simulator
     */
    public void startTransmission(Simulator simulator)
    {
	if (onGoing != 0)
	    state = stateCollided;
	else
	    state = stateTransmitting;

	Packet packet = (Packet) processor.get(queue);
	isTransmitting = true;
	simulator.add(new Event(simulator.time()+
				commChannel.transmitDuration(packet),
				this,
				this.events[1])); //Transmission Ended scheduled

	//Set neighbors' receive
	for (int i = 0; i < transmitPartners.size(); i++)
	    commChannel.transmit(this, (ALOHA) transmitPartners.get(i),
				 packet,simulator);
    }

    /** Receive packets.
     * @param source reference to source node
     * @param packet reference to packet
     * @param simulator reference to simulator
     */
    public void receive(CommNode source, Object packet, Simulator simulator)
    {
	if ((state == stateTransmitting) || 
	    (state == stateReceiving) || (state == stateCollided))
	    state = stateCollided;
	else
	    state = stateReceiving;
	onGoing++;
	simulator.add(new Event(simulator.time()+
				commChannel.transmitDuration((Packet) packet),
				this,
				this.events[0])); //Receive Ended scheduled
	this.packet = packet;
	this.currSource = source;
    }

    public void trigger(Simulator simulator)
    {
	if (state == stateIdle)
	{
	    if (processor.hasPkt(queue))
		startTransmission(simulator);
	}
    }

    public Node newNode(Coordinate coordinate)
    {
	return new ALOHA(coordinate, this.channel, this.commChannel, 
			 this.queue.newQueue(), this.processor, this.waitTime);
    }

    /** Constructor.
     * @param coordinate coordinate of ALOHA node
     * @param channel network channel in use
     * @param commChannel communication channel
     * @param queue queue of the node
     * @param processor reference to packet processor
     * @param waitTime distribution of waiting time
     */
    public ALOHA(Coordinate coordinate, Channel channel, 
		 CommChannel commChannel, Queue queue, 
		 PacketProcessor processor, Distribution waitTime)
    {
	super(coordinate, channel, commChannel, queue, processor);
	this.waitTime = waitTime;
    }

    /** Trial run of MAC simulation.
     * @param args 1st argument can be density of Grid network [optional]
     */
    public static void main(String[] args)
    {
	MACTrial trial = new MACTrial(new Simulator());
	if (args.length >= 1)
	    trial.pointprocess = new Grid(Double.parseDouble(args[0]));
	trial.generateNetwork(new ALOHA(new Coordinate(0,0), 
					trial.networkChannel, trial.commChannel,
					trial.queue, trial.processor,
					trial.waitTime));
	//Trigger by scheduling end of wait.
	for (int i = 0; i < trial.network.nodes.size(); i++)
	    trial.simulator.add(new Event(trial.waitTime.getInstance(),
					  ((ALOHA) trial.network.nodes.get(i)),
					  ((ALOHA) trial.network.nodes.get(i)).
					  events[2]));
	trial.run();
    }
}
