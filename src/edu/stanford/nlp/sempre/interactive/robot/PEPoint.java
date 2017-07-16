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

    public PEPoint(int x, int y, int z, Quaternion orientation, String color, boolean attract) {
	super(x, y, z, orientation, color);
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
	PEPoint retcube = new PEPoint();
	retcube.names.addAll((List<String>) props.get(0));
	retcube.x = ((Integer) props.get(1));
	retcube.y = ((Integer) props.get(2));
	retcube.z = ((Integer) props.get(3));
	List<Double> qElems = (List<Double>) props.get(4);
	retcube.rot = new Quaternion(qElems.get(0), qElems.get(1), qElems.get(2), qElems.get(3));
	retcube.color = Color.fromString(((String) props.get(5)));
	retcube.attract = ((Boolean) props.get(6));
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
	return new PEPoint(this.x, this.y, this.z, this.rot, this.color.toString(), this.attract);
    }
}
