package simulation.utilities.processors;

import java.util.*;

/** Class to provide operations on vector.
 * @author ykk
 */
public class VectorOp
{
    /** Returns index of object in the vector.
     * Returns -1 if not found.
     * @param objects vector to search
     * @param object object to search for
     * @return index of object in array; else -1
     */
    public static int indexOf(Vector objects, Comparable object)
    {
	for (int i = 0; i < objects.size(); i++)
	    if (object.compareTo(objects.get(i)) == 0)
		return i;

	return -1;
    }

    /** Return string vector of objects.
     * @param objects vector of objects
     * @return vector of objects' string representation
     */
    public static Vector vecString(Vector objects)
    {
	Vector tmpVec = new Vector();

	for (int i = 0; i < objects.size(); i++)
	    tmpVec.add(objects.get(i).toString());

	return tmpVec;
    }
}
