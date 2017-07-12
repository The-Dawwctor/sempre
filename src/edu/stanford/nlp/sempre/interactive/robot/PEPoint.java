package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.collections.Lists;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.Item;

//individual stacks
public class PEPoint extends Item {
    public Color color;
    int row, col, height;
    boolean attract;
    int age;

    public PEPoint(int row, int col, int height, String color, boolean attract) {
	this(row, col, height);
	this.color = Color.fromString(color);
	this.attract = attract;
    }
    
    public PEPoint(int row, int col, int height, String color) {
	this(row, col, height, color, false);
    }
    
    public PEPoint() {
	this.row = 0;
	this.col = 0;
	this.height = 0;
	this.color = Color.fromString("None");
	this.attract = false;
	this.names = new HashSet<>();
	this.age = 0;
    }

    // used as a key
    public PEPoint(int row, int col, int height) {
	this();
	this.row = row;
	this.col = col;
	this.height = height;
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
	else if (property.equals("age"))
	    propval = new Integer(this.age);
	else if (property.equals("color"))
	    propval = this.color.toString().toLowerCase();
	// else if (property.equals("name"))
	// propval = this.names;
	else
	    throw new RuntimeException("getting property " + property + " is not supported.");
	return propval;
    }

    @Override
    public void update(String property, Object value) {
	// updating with empty set does nothing, throw something?
	if (value instanceof Set && ((Set) value).size() == 0)
	    return;
	if (value instanceof Set && ((Set) value).size() == 1)
	    value = ((Set) value).iterator().next();

	if (property.equals("height") && value instanceof Integer)
	    this.height = (Integer) value;
	else if (property.equals("row") && value instanceof Integer)
	    this.row = (Integer) value;
	else if (property.equals("col") && value instanceof Integer)
	    this.height = (Integer) value;
	else if (property.equals("attract") && value instanceof Boolean)
	    this.attract = (Boolean) attract;
	else if (property.equals("color") && value instanceof String)
	    this.color = Color.fromString(value.toString());
	else if (value instanceof Set)
	    throw new RuntimeException(String.format("Updating %s to %s is not allowed," +
						     " which has %d values, but a property can only have 1 value. ",
						     property, value.toString(), ((Set) value).size()));
	else
	    throw new RuntimeException(String.format("Updating property %s to %s is not allowed! (type %s is not expected for %s) ",
						     property, value.toString(), value.getClass(), property));
    }

    @SuppressWarnings("unchecked")
    public static PEPoint fromJSON(String json) {
	List<Object> props = Json.readValueHard(json, List.class);
	return fromJSONObject(props);
    }

    @SuppressWarnings("unchecked")
    public static PEPoint fromJSONObject(List<Object> props) {
	PEPoint retcube = new PEPoint();
	retcube.row = ((Integer) props.get(0));
	retcube.col = ((Integer) props.get(1));
	retcube.height = ((Integer) props.get(2));
	retcube.color = Color.fromString(((String) props.get(3)));
	retcube.attract = ((Boolean) props.get(4));
	retcube.names.addAll((List<String>) props.get(5));
	return retcube;
    }

    public Object toJSON() {
	List<String> globalNames = names.stream().collect(Collectors.toList());
	List<Object> cube = Lists.newArrayList(row, col, height, color.toString(), attract, globalNames);
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

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	PEPoint other = (PEPoint) obj;

	if (col != other.col)
	    return false;
	if (height != other.height)
	    return false;
	if (row != other.row)
	    return false;

	return true;
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
    public String toString() {
	return this.toJSON().toString();
    }
}
