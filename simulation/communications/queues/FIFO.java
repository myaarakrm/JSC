package simulation.communications.queues;

import simulation.communications.queues.*;

/** Class defining First-In First-Out (FIFO) link layer queue.
 * @author ykk
 */
public class FIFO
    extends Queue
{
    //Members
    /** Maximum queue capacity.
     * Defaulted to 0 (implies infinity).
     */
    public int queueSize = 0;

    //Methods
    /** Constructor for queue.
     * @param queueSize maximum queue capacity
     */
    public FIFO(int queueSize)
    {
	this.queueSize = queueSize;
    }

    /** Constructor for queue.
     * Maximum queue capacity is set to infinity (queueSize=0).
     */    
    public FIFO()
    {
	this.queueSize = 0;
    }

    /** Check if queue is in queue.
     * @param packet packet reference
     */
    public boolean inQueue(Object packet)
    {
	return (this.indexOf(packet) != -1);
    }

    /** Receive packet into queue.  Drops packet if queue is full.
     * @param packet packet to be received by queue
     * @return if packet is dropped
     */
    public boolean receive(Object packet)
    {
	if ((queueSize == 0) || (size() < queueSize))
	{
	    this.add(packet);
	    return true;
	}
	else
	    return false;
    }

    /** Add packet.
     * @param packet packet to add.
     */
    public boolean add(Object packet)
    {
	super.add(packet);
	boolean result = (size() > queueSize);

	if (queueSize != 0)
	    while (size() > queueSize)
		this.remove(size()-1);
	
	return result;
    }

    public Object get()
    {
	if (this.size() == 0)
	    return null;
	else
	    return this.remove(0);
    }

    public Queue newQueue()
    {
	return new FIFO(this.queueSize);
    }
}
