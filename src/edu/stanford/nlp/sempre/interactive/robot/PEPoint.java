package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.collections.Lists;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

//individual stacks
public class PEPoint extends Point {
    boolean attract;

    public PEPoint(int row, int col, int height, String color, boolean attract) {
	super(row, col, height, color);
	this.attract = attract;
	this.names.add("PEPoint");
    }
    
    public PEPoint(int row, int col, int height, String color) {
	this(row, col, height, color, false);
    }
    
    public PEPoint() {
	super();
	this.attract = false;
	this.names.add("PEPoint");
    }

    // used as a key
    public PEPoint(int row, int col, int height) {
	super(row, col, height);
	this.attract = false;
	this.names.add("PEPoint");
    }

    public PEPoint move(Direction dir) {
	switch (dir) {
	case Back:
	    this.row += 1;
	    break;
	case Front:
	    this.row -= 1;
	    break;
	case Left:
	    this.col += 1;
	    break;
	case Right:
	    this.col -= 1;
	    break;
	case Top:
	    this.height += 1;
	    break;
	case Bot:
	    this.height -= 1;
	    break;
	case None:
	    break;
	}
	return this;
    }

    public PEPoint copy(Direction dir) {
	PEPoint c = this.clone();
	switch (dir) {
	case Back:
	    c.row += 1;
	    break;
	case Front:
	    c.row -= 1;
	    break;
	case Left:
	    c.col += 1;
	    break;
	case Right:
	    c.col -= 1;
	    break;
	case Top:
	    c.height += 1;
	    break;
	case Bot:
	    c.height -= 1;
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
	    propval = new Integer(this.height);
	else if (property.equals("row"))
	    propval = new Integer(this.row);
	else if (property.equals("col"))
	    propval = new Integer(this.col);
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
		this.height = (Integer) value;
	    else if (property.equals("row"))
		this.row = (Integer) value;
	    else if (property.equals("col"))
		this.height = (Integer) value;
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
	retcube.row = ((Integer) props.get(1));
	retcube.col = ((Integer) props.get(2));
	retcube.height = ((Integer) props.get(3));
	retcube.color = Color.fromString(((String) props.get(4)));
	retcube.attract = ((Boolean) props.get(5));
	return retcube;
    }

    public Object toJSON() {
	List<String> globalNames = names.stream().collect(Collectors.toList());
	List<Object> cube = Lists.newArrayList(globalNames, row, col, height, color.toString(), attract);
	return cube;
    }

    @Override
    public PEPoint clone() {
	return new PEPoint(this.row, this.col, this.height, this.color.toString(), this.attract);
    }

    @Override
    public int hashCode() {
	final int prime = 19;
	int result = 1;
	result = prime * result + col;
	result = prime * result + height;
	result = prime * result + row;
	return result;
    }
}
