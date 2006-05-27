package simulation.eventbased;

import simulation.eventbased.*;

/** Abstract class for event triggered object.
 * @author ykk
 */
public interface EventTriggered
{
    /** Function to run events.
     * @param time current time
     * @param event event string definition
     * @param simulator reference to simulator
     * @see Event#event
     */
    public void run(double time, String event, Simulator simulator);
}
