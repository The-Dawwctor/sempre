package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.collections.Lists;
import org.apache.commons.math3.complex.Quaternion;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

//individual stacks
public class OpPoint extends Point {
    public OpPoint() {
	super();
	this.names.add("OpPoint");
    }

    // used as key
    public OpPoint(int x, int y, int z) {
	super(x, y, z);
	this.names.add("OpPoint");
    }

    public OpPoint(int x, int y, int z, Quaternion orientation, String color) {
	super(x, z, z, orientation, color);
	this.names.add("OpPoint");
    }

    @Override
    public OpPoint clone() {
	return (OpPoint)super.clone();
    }
}
