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
    }

    // used as key
    public OpPoint(int row, int col, int height) {
	super(row, col, height);
    }

    public OpPoint(int row, int col, int height, String color) {
	super(row, col, height, color);
    }
    
    public OpPoint move(Direction dir) {
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

    public OpPoint copy(Direction dir) {
	OpPoint c = this.clone();
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
