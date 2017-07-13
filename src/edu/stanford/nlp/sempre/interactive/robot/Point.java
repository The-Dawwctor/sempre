package edu.stanford.nlp.sempre.interactive.robot;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.collections.Lists;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.Item;

public class Point extends Item {
    public Color color;
    public int row, col, height;

    public Point() {
	this.names = new HashSet<>();
	this.names.add("Point");
	this.row = 0;
	this.col = 0;
	this.height = 0;
	this.color = Color.fromString("None");
    }

    // used as key
    public Point(int row, int col, int height) {
	this();
	this.row = row;
	this.col = col;
	this.height = height;
    }

    public Point(int row, int col, int height, String color) {
	this(row, col, height);
	this.color = Color.fromString(color);
    }

    public Point move(Direction dir) {
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

    public Point copy(Direction dir) {
	Point c = this.clone();
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
	} else {
	    throw new RuntimeException(String.format("Updating property %s to %s not allowed! (type %s not expected for %s) ",
						     property, value.toString(), value.getClass(), property));
	}
    }

    public Object toJSON() {
	List<String> globalNames = names.stream().collect(Collectors.toList());
	List<Object> point = Lists.newArrayList(globalNames, row, col, height, color.toString());
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

	if (col != other.col)
	    return false;
	if (height != other.height)
	    return false;
	if (row != other.row)
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
	retcube.row = ((Integer) props.get(1));
	retcube.col = ((Integer) props.get(2));
	retcube.height = ((Integer) props.get(3));
	retcube.color = Color.fromString(((String) props.get(4)));
	return retcube;
    }
    
    @Override
    public Point clone() {
	return new Point(this.row, this.col, this.height, this.color.toString());
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + col;
	result = prime * result + height;
	result = prime * result + row;
	return result;
    }
    
    @Override
    public String toString() {
	return this.toJSON().toString();
    }
}
