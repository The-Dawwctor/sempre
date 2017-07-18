package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.collections.Lists;
import org.apache.commons.math3.complex.Quaternion;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

/* Represents an Operational Point, or a point that
 * can be used to represent a point on the robot in operational
 * space, like an end-effector or joint.
 */
public class OpPoint extends Point {
    public int frame;
    
    public OpPoint() {
	super();
	this.frame = 0;
	this.names.add("OpPoint");
    }

    // used as key
    public OpPoint(int x, int y, int z) {
	super(x, y, z);
	this.frame = 0;
	this.names.add("OpPoint");
    }

    public OpPoint(int id, int x, int y, int z, Quaternion rotate, String color, int frame) {
	super(id, x, z, z, rotate, color);
	this.frame = frame;
	this.names.add("OpPoint");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String property) {
	Object propval;
	if (property.equals("frame"))
	    propval = new Integer(this.frame);
	else
	    propval = super.get(property);
	return propval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(String property, Object value) {
	if (property.equals("frame") && value instanceof Integer) {
	    this.frame = (Integer) value;
	} else {
	    super.update(property, value);
	}
    }

    @SuppressWarnings("unchecked")
    public static OpPoint fromJSONObject(List<Object> props) {
	Point p = Point.fromJSONObject(props);
	OpPoint retpoint = new OpPoint();
	retpoint.names = p.names;
	retpoint.id = p.id;
	retpoint.x = p.x;
	retpoint.y = p.y;
	retpoint.z = p.z;
	retpoint.rotate = p.rotate;
	retpoint.color = p.color;
	retpoint.frame = ((Integer) props.get(7));
	return retpoint;
    }

    @SuppressWarnings("unchecked")
    public Object toJSON() {
	List<Object> oppoint = (List)super.toJSON();
	oppoint.add(frame);
	return oppoint;
    }

    @Override
    public OpPoint clone() {
	return new OpPoint(this.id, this.x, this.y, this.z, this.rotate, this.color.toString(), this.frame);
    }
}
