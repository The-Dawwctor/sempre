package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.collections.Lists;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

//individual stacks
public class PEPoint extends Point {
    boolean attract;

    public PEPoint(int x, int y, int z, String color, boolean attract) {
	super(x, y, z, color);
	this.attract = attract;
	this.names.add("PEPoint");
    }
    
    public PEPoint(int x, int y, int z, String color) {
	this(x, y, z, color, false);
    }
    
    public PEPoint() {
	super();
	this.attract = false;
	this.names.add("PEPoint");
    }

    // used as a key
    public PEPoint(int x, int y, int z) {
	super(x, y, z);
	this.attract = false;
	this.names.add("PEPoint");
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
	else if (property.equals("attract"))
	    propval = new Boolean(this.attract);
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
	} else if (property.equals("attract") && value instanceof Boolean) {
	    this.attract = (Boolean) attract;
	} else {
	    throw new RuntimeException(String.format("Updating property %s to %s not allowed! (type %s not expected for %s) ",
						     property, value.toString(), value.getClass(), property));
	}
    }

    @SuppressWarnings("unchecked")
    public static PEPoint fromJSONObject(List<Object> props) {
	PEPoint retcube = new PEPoint();
	retcube.names.addAll((List<String>) props.get(0));
	retcube.x = ((Integer) props.get(1));
	retcube.y = ((Integer) props.get(2));
	retcube.z = ((Integer) props.get(3));
	retcube.color = Color.fromString(((String) props.get(4)));
	retcube.attract = ((Boolean) props.get(5));
	return retcube;
    }

    public Object toJSON() {
	List<Object> pepoint = (List<Object>)super.toJSON();
	List<Object> additional = Lists.newArrayList(attract);
	pepoint.add(attract);
	return pepoint;
    }

    @Override
    public PEPoint clone() {
	return new PEPoint(this.x, this.y, this.z, this.color.toString(), this.attract);
    }
}
