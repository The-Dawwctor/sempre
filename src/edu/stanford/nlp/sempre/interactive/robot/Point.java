package edu.stanford.nlp.sempre.interactive.robot;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.testng.collections.Lists;
import org.apache.commons.math3.complex.Quaternion;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.Item;

public class Point extends Item {
    public int id;
    public Color color;
    public int x, y, z;
    public Quaternion rotate;

    public Point() {
	this.names = new HashSet<>();
	this.names.add("Point");
	this.x = 0;
	this.y = 0;
	this.z = 0;
	this.id = -1;
	this.rotate = Quaternion.ZERO;
	this.color = Color.fromString("None");
    }

    // used as key
    public Point(int x, int y, int z) {
	this();
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public Point(int id, int x, int y, int z, Quaternion orientation, String color) {
	this(x, y, z);
	this.id = id;
	this.color = Color.fromString(color);
	this.rotate = orientation;
    }

    public void setOrientation(Quaternion newQ) {
	rotate = new Quaternion(newQ.getQ0(), newQ.getQ1(), newQ.getQ2(), newQ.getQ3());
    }
    
    // Rotates given point by axis and angle theta.
    // Determined by whether rotation with respect to local axis or world axis.
    public void rotatePoint(String axis, boolean local, double theta) {
	Quaternion axisVec;
	if (axis.equalsIgnoreCase("x")) {
	    axisVec = new Quaternion(new double[]{1, 0, 0});
	} else if (axis.equalsIgnoreCase("y")) {
	    axisVec = new Quaternion(new double[]{0, 1, 0});
	} else if (axis.equalsIgnoreCase("z")) {
	    axisVec = new Quaternion(new double[]{0, 0, 1});
	} else {
	    // Invalid axis, don't rotate
	    System.out.print("Invalid axis. Specify x, y, or z axis");
	    return;
	}
	Quaternion correctAxis;
	if (local) {
	    // if local frame, rotate axis by current orientation to get correct axis for axis-angle calculation
	    // v' = qvq^-1
	    correctAxis = rotate.getInverse().multiply(axisVec.multiply(rotate));
	} else {
	    correctAxis = axisVec;
	}
	double ct = Math.cos(theta/2);
	double st = Math.sin(theta/2);
	Quaternion rotateQ = new Quaternion(ct,
					    st * correctAxis.getQ1(),
					    st * correctAxis.getQ2(),
					    st * correctAxis.getQ3());
	// multiply quaternions to get rotation composition
	// q' = q2q1, q1 = current orientation, q2 = applied rotation
	rotate = rotate.multiply(rotateQ).normalize();
    }
    
    public Point move(Direction dir) {
	switch (dir) {
	case Back:
	    this.x += 1;
	    break;
	case Front:
	    this.x -= 1;
	    break;
	case Left:
	    this.y += 1;
	    break;
	case Right:
	    this.y -= 1;
	    break;
	case Top:
	    this.z += 1;
	    break;
	case Bot:
	    this.z -= 1;
	    break;
	case None:
	    break;
	}
	return this;
    }

    // TODO: Remove method since need unique IDs for each block.
    // Need to change implementaiton of add in RobotWorld
    public Point copy(Direction dir) {
	Point c = this.clone();
	switch (dir) {
	case Back:
	    c.x += 1;
	    break;
	case Front:
	    c.x -= 1;
	    break;
	case Left:
	    c.y += 1;
	    break;
	case Right:
	    c.y -= 1;
	    break;
	case Top:
	    c.z += 1;
	    break;
	case Bot:
	    c.z -= 1;
	    break;
	case None:
	    break;
	}
	return c;
    }
    
    @Override
    public Object get(String property) {
	Object propval;
	if (property.equals("z"))
	    propval = new Integer(this.z);
	else if (property.equals("x"))
	    propval = new Integer(this.x);
	else if (property.equals("y"))
	    propval = new Integer(this.y);
	else if (property.equals("color"))
	    propval = this.color.toString().toLowerCase();
	else if (property.equals("name"))
	    propval = this.names;
	else if (property.equals("rotate"))
	    propval = new Quaternion(this.rotate.getScalarPart(), this.rotate.getVectorPart());
	else
	    throw new RuntimeException("getting property " + property + " is not supported.");
	return propval;
    }

    @Override
    public void update(String property, Object value) {
	if (value instanceof Set) {
	    Set valueSet = (Set) value;
	    if (valueSet.size() == 0) {
		// updating with empty set does nothing, throw something?
		return;
	    } else if (valueSet.size() == 1) {
		value = valueSet.iterator().next();
	    } else {
		throw new RuntimeException(String.format("Updating %s to %s not allowed," +
							 " which has %d values, but a property can only have 1 value. ",
							 property, value.toString(), valueSet.size()));
	    }
	}

	if (value instanceof Integer) {
	    if (property.equals("z"))
		this.z = (Integer) value;
	    else if (property.equals("x"))
		this.x = (Integer) value;
	    else if (property.equals("y"))
		this.y = (Integer) value;
	} else if (property.equals("color") && value instanceof String) {
	    this.color = Color.fromString(value.toString());
	} else if (property.equals("rotate") && value instanceof Quaternion) {
	    this.rotate = (Quaternion) value;
	} else {
	    throw new RuntimeException(String.format("Updating property %s to %s not allowed! (type %s not expected for %s) ",
						     property, value.toString(), value.getClass(), property));
	}
    }

    // Current format: Names, ID, x, y, z (Position), Array of Rotation Quaternion, Color
    public Object toJSON() {
	List<String> globalNames = names.stream().collect(Collectors.toList());
	List<Double> qElems = Lists.newArrayList(rotate.getQ0(), rotate.getQ1(), rotate.getQ2(), rotate.getQ3());
	List<Object> point = Lists.newArrayList(globalNames, id, x, y, z, qElems, color.toString());
	return point;
    }

    @Override
    public boolean selected() {
	return names.contains("S");
    }

    @Override
    public void select(boolean s) {
	if (s)
	    names.add("S");
	else
	    names.remove("S");
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Point other = (Point) obj;

	if (y != other.y)
	    return false;
	if (z != other.z)
	    return false;
	if (x != other.x)
	    return false;

	return true;
    }

    @SuppressWarnings("unchecked")
    public static Point fromJSON(String json) {
	List<Object> props = Json.readValueHard(json, List.class);
	return fromJSONObject(props);
    }

    @SuppressWarnings("unchecked")
    public static Point fromJSONObject(List<Object> props) {
	Point retpoint = new Point();
	retpoint.names.addAll((List<String>) props.get(0));
	retpoint.id = ((Integer) props.get(1));
	retpoint.x = ((Integer) props.get(2));
	retpoint.y = ((Integer) props.get(3));
	retpoint.z = ((Integer) props.get(4));
	
	List<Object> ps = (List<Object>) props.get(5);
	List<Double> ws = new ArrayList<Double>();
	// Fill out orientation Quaternion and convert to double values
	for (Object p : ps) {
	    if (p instanceof Integer) {
		ws.add(((Integer) p).doubleValue());
	    } else if (p instanceof Double) {
		ws.add((Double) p);
	    } else {
		throw new RuntimeException("Invalid data type in orientation found");
	    }
	}
	retpoint.rotate = new Quaternion(ws.get(0), ws.get(1), ws.get(2), ws.get(3));
	
	retpoint.color = Color.fromString(((String) props.get(6)));
	return retpoint;
    }
    
    @Override
    // TODO: remove since no longer valid with id system. Unique points only.
    public Point clone() {
	return new Point(this.id, this.x, this.y, this.z, this.rotate, this.color.toString());
    }
    
    @Override
    public int hashCode() {
	final int prime = 19;
	int result = 1;
	result = prime * result + y;
	result = prime * result + z;
	result = prime * result + x;
	return result;
    }
    
    @Override
    public String toString() {
	return this.toJSON().toString();
    }
}
