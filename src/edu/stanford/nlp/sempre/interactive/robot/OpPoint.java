package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.collections.Lists;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

//individual stacks
public class OpPoint extends Point {
    public OpPoint() {
	super();
	this.names.add("OpPoint");
    }

    // used as key
    public OpPoint(int row, int col, int height) {
	super(row, col, height);
	this.names.add("OpPoint");
    }

    public OpPoint(int row, int col, int height, String color) {
	super(row, col, height, color);
	this.names.add("OpPoint");
    }

    @Override
    public OpPoint clone() {
	return (OpPoint)super.clone();
    }
    
    @Override
    public int hashCode() {
	final int prime = 17;
	int result = 1;
	result = prime * result + col;
	result = prime * result + height;
	result = prime * result + row;
	return result;
    }
}
