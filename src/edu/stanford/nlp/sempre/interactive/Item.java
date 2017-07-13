package edu.stanford.nlp.sempre.interactive;

import java.util.Set;
import java.util.HashSet;
import edu.stanford.nlp.sempre.interactive.robot.Color;

// Individual items with some properties
public abstract class Item {
    public Color color;
    public int row, col, height;
    public Set<String> names;

    public Item() {
	this.row = 0;
	this.col = 0;
	this.height = 0;
	this.color = Color.fromString("None");
	this.names = new HashSet<>();
    }

    // used as a key
    public Item(int row, int col, int height) {
	this();
	this.row = row;
	this.col = col;
	this.height = height;
    }

    public Item(int row, int col, int height, String color) {
	this(row, col, height);
	this.color = Color.fromString(color);
    }
    
    public abstract boolean selected(); // explicit global selection

    public abstract void select(boolean sel);

    public abstract void update(String rel, Object value);

    public abstract Object get(String rel);
}
