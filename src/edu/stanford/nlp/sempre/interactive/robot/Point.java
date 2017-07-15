package edu.stanford.nlp.sempre.interactive.robot;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.collections.Lists;
import Jama.*;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.Item;

public class Point extends Item {
    public Color color;
    public int x, y, z;
    public Matrix orientation;

    public Point() {
	this.names = new HashSet<>();
	this.names.add("Point");
	this.x = 0;
	this.y = 0;
	this.z = 0;
	this.orientation = new Matrix(new double[]{0, 0, 0, 0}, 4);
	this.color = Color.fromString("None");
    }

    // used as key
    public Point(int x, int y, int z) {
	this();
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public Point(int x, int y, int z, String color) {
	this(x, y, z);
	this.color = Color.fromString(color);
    }

    // Rotates given point by axis and angle theta.
    // Determined by whether rotation with respect to local axis or world axis.
    public void rotate(String axis, boolean local, double theta) {
	Matrix axisVec;
	if (axis.equalsIgnoreCase("x")) {
	    axisVec = new Matrix(new double[]{1, 0, 0}, 3);
	} else if (axis.equalsIgnoreCase("y")) {
	    axisVec = new Matrix(new double[]{0, 1, 0}, 3);
	} else if (axis.equalsIgnoreCase("z")) {
	    axisVec = new Matrix(new double[]{0, 0, 1}, 3);
	} else {
	    // Invalid axis, don't rotate
	    System.out.print("Invalid axis. Specify x, y, or z axis");
	    return;
	}
	Matrix correctAxis;
	if (local) {
	    // perform rotation on axisVec to change axis we're rotating around
	    correctAxis = axisVec;
	} else {
	    correctAxis = axisVec;
	}
	double ct = Math.cos(theta/2);
	double st = Math.sin(theta/2);
	Matrix rotateQ = new Matrix(new double[]{
		ct,
		st * correctAxis.get(0, 0),
		st * correctAxis.get(1, 0),
		st * correctAxis.get(2, 0)
	    }, 1);
	
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
	if (property.equals("height"))
	    propval = new Integer(this.z);
	else if (property.equals("row"))
	    propval = new Integer(this.x);
	else if (property.equals("col"))
	    propval = new Integer(this.y);
	else if (property.equals("color"))
	    propval = this.color.toString().toLowerCase();
	else if (property.equals("name"))
	    propval = this.names;
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
	    if (property.equals("height"))
		this.z = (Integer) value;
	    else if (property.equals("row"))
		this.x = (Integer) value;
	    else if (property.equals("col"))
		this.y = (Integer) value;
	} else if (property.equals("color") && value instanceof String) {
	    this.color = Color.fromString(value.toString());
	} else {
	    throw new RuntimeException(String.format("Updating property %s to %s not allowed! (type %s not expected for %s) ",
						     property, value.toString(), value.getClass(), property));
	}
    }

    public Object toJSON() {
	List<String> globalNames = names.stream().collect(Collectors.toList());
	List<Object> point = Lists.newArrayList(globalNames, x, y, z, color.toString());
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
	Point retcube = new Point();
	retcube.names.addAll((List<String>) props.get(0));
	retcube.x = ((Integer) props.get(1));
	retcube.y = ((Integer) props.get(2));
	retcube.z = ((Integer) props.get(3));
	retcube.color = Color.fromString(((String) props.get(4)));
	return retcube;
    }
    
    @Override
    public Point clone() {
	return new Point(this.x, this.y, this.z, this.color.toString());
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
