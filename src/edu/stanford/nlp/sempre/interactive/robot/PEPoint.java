package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.collections.Lists;
import org.apache.commons.math3.complex.Quaternion;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

//individual stacks
public class PEPoint extends Point {
    boolean attract;
    
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

    public PEPoint(int id, int x, int y, int z, Quaternion rotate, String color, boolean attract) {
	super(id, x, y, z, rotate, color);
	this.attract = attract;
	this.names.add("PEPoint");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object get(String property) {
	Object propval;
	if (property.equals("attract"))
	    propval = new Boolean(this.attract);
	else
	    propval = super.get(property);
	return propval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(String property, Object value) {
	if (property.equals("attract") && value instanceof Boolean) {
	    this.attract = (Boolean) attract;
	} else {
	    super.update(property, value);
	}
    }

    @SuppressWarnings("unchecked")
    public static PEPoint fromJSONObject(List<Object> props) {
	Point p = Point.fromJSONObject(props);
	PEPoint retpoint = new PEPoint();
	retpoint.names = p.names;
	retpoint.id = p.id;
	retpoint.x = p.x;
	retpoint.y = p.y;
	retpoint.z = p.z;
	retpoint.rotate = p.rotate;
	retpoint.color = p.color;
	retpoint.attract = ((Boolean) props.get(7));
	return retpoint;
    }

    public Object toJSON() {
	List<Object> pepoint = (List<Object>)super.toJSON();
	List<Object> additional = Lists.newArrayList(attract);
	pepoint.add(attract);
	return pepoint;
    }

    @Override
    public PEPoint clone() {
	return new PEPoint(this.id, this.x, this.y, this.z, this.rotate, this.color.toString(), this.attract);
    }
}
