package edu.stanford.nlp.sempre.interactive.robot;

import edu.stanford.nlp.sempre.interactive.robot.Point;

/* Represent a relationship between 2 different points.
 * For robot control, an example would be an end effector (OpPoint)
 * being attracted to a goal (PEPoint) or a joint (OpPoint) avoiding
 * an obstacle (PEPoint).
 */
public class Relation {
    public Point source;
    public Point dest;
    // public boolean attract; // Move attract from PEPoint to Relation?

    // Do we ever want OpPoint to follow/avoid another OpPoint
    // or PEPoint to follow/avoid another PEPoint?
    //public OpPoint source;
    //public PEPoint dest;
    
    public Relation() {
	this.source = null;
	this.dest = null;
    }

    public Relation(Point source, Point dest) {
	this.source = source;
	this.dest = dest;
    }

    @Override
    public String toString() {
	return "Source: " + source + ", Dest: " + dest;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result +
	    ((source == null) ? 0 : source.hashCode()) +
	    ((dest == null) ? 0 : dest.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Relation other = (Relation) obj;
	if (this.source.equals(other.source) && this.dest.equals(other.dest))
	    return true;
	return false;
    }
}
