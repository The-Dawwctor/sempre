package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import edu.stanford.nlp.sempre.ContextValue;
import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.NaiveKnowledgeGraph;
import edu.stanford.nlp.sempre.StringValue;
import edu.stanford.nlp.sempre.interactive.Item;
import edu.stanford.nlp.sempre.interactive.World;
import fig.basic.Option;

// the world of stacks
public class RobotWorld extends World {
    public static class Options {
	@Option(gloss = "maximum number of cubes to convert")
	public int maxBlocks = 1024 ^ 2;
    }

    public static Options opts = new Options();

    public final static String SELECT = "S";

    public static RobotWorld fromContext(ContextValue context) {
	if (context == null || context.graph == null) {
	    return fromJSON("[[[\"S\"], 3,3,1,\"gray\",false],[[],4,4,1,\"blue\",false]]");
	}
	NaiveKnowledgeGraph graph = (NaiveKnowledgeGraph) context.graph;
	String wallString = ((StringValue) graph.triples.get(0).e1).value;
	return fromJSON(wallString);
    }

    public void base(int x, int y) {
	PEPoint basecube = new PEPoint(x, y, 0, Color.Fake.toString());
	this.allItems = new HashSet<>(this.allItems);
	this.selected = new HashSet<>(this.selected);
	allItems.add(basecube);
	selected.add(basecube);
    }

    public Set<Item> origin() {
	for (Item i : allItems) {
	    PEPoint b = (PEPoint) i;
	    if (b.col == 0 && b.row == 0 && b.height == 0)
		return Sets.newHashSet(b);
	}
	PEPoint basecube = new PEPoint(0, 0, 0, Color.Fake.toString());
	return Sets.newHashSet(basecube);
    }

    @SuppressWarnings("unchecked")
    public RobotWorld(Set<Item> blockset) {
	super();
	this.allItems = blockset;
	this.selected = blockset.stream().filter(b -> ((PEPoint) b).names.contains(SELECT)).collect(Collectors.toSet());
	this.selected.forEach(i -> i.names.remove(SELECT));
    }

    // only use names S to communicate with client, internally it's just select variable
    @Override
    public String toJSON() {
	// selected that's no longer in the world gets nothing
	// allitems.removeIf(c -> ((Block)c).color == CubeColor.Fake &&
	// !this.selected.contains(c));
	// allitems.stream().filter(c -> selected.contains(c)).forEach(i ->
	// i.names.add(SELECT));

	return Json.writeValueAsStringHard(allItems.stream().map(c -> {
		    PEPoint b = ((PEPoint) c).clone();
		    if (selected.contains(b))
			b.names.add("S");
		    return b.toJSON();
		}).collect(Collectors.toList()));
	// return this.worldlist.stream().map(c -> c.toJSON()).reduce("", (o, n) ->
	// o+","+n);
    }

    private static RobotWorld fromJSON(String wallString) {
	@SuppressWarnings("unchecked")
	List<List<Object>> itemstr = Json.readValueHard(wallString, List.class);
	Set<Item> items = itemstr.stream().map(c -> {
		return PEPoint.fromJSONObject(c);
	    }).collect(Collectors.toSet());
	RobotWorld world = new RobotWorld(items);
	return world;
    }

    @Override
    public Set<Item> has(String rel, Set<Object> values) {
	return this.allItems.stream().filter(i -> values.contains(i.get(rel))).collect(Collectors.toSet());
    }

    @Override
    public Set<Object> get(String rel, Set<Item> subset) {
	return subset.stream().map(i -> i.get(rel)).collect(Collectors.toSet());
    }

    @Override
    public void update(String rel, Object value, Set<Item> selected) {
	selected.forEach(i -> i.update(rel, value));
	keyConsistency();
    }

    // if selected no longer in all, make it fake colored and add to all;
    // likewise, if some fake colored block is no longer selected, remove it
    @Override
    public void merge() {
	Sets.difference(selected, allItems).forEach(i -> ((PEPoint) i).color = Color.Fake);
	allItems.removeIf(c -> ((PEPoint) c).color.equals(Color.Fake) && !this.selected.contains(c));
	allItems.addAll(selected);
	if (allItems.size() > opts.maxBlocks) {
	    throw new RuntimeException(String.format("Number of blocks (%d) exceeds the upperlimit %d", allItems.size(), opts.maxBlocks));
	}
    }

    // block world specific actions, overriding move
    public void move(String dir, Set<Item> selected) {
	// allitems.removeAll(selected);
	selected.forEach(b -> ((PEPoint) b).move(Direction.fromString(dir)));
	keyConsistency();
	// allitems.addAll(selected); // this is not overriding
    }

    public void add(String colorstr, String dirstr, Set<Item> selected) {
	Direction dir = Direction.fromString(dirstr);
	Color color = Color.fromString(colorstr);

	if (dir == Direction.None) { // add here
	    selected.forEach(b -> ((PEPoint) b).color = color);
	} else {
	    Set<Item> extremeCubes = extremeCubes(dir, selected);
	    this.allItems.addAll(extremeCubes.stream().map(c -> {
			PEPoint d = ((PEPoint) c).copy(dir);
			d.color = color;
			return d;
		    }).collect(Collectors.toList()));
	}
    }

    // set goal position in coordinate system
    // TODO: CHANGE INTEGER COORDINATES TO DOUBLES AS SOON AS SETTING MADE CONTINUOUS
    public void goal(int x, int y, int z) {
	PEPoint newPEPoint = new PEPoint(x, y, z, "red");
	this.allItems.add(newPEPoint);
    }
    
    // get cubes at extreme positions
    public Set<Item> veryx(String dirstr, Set<Item> selected) {
	Direction dir = Direction.fromString(dirstr);
	switch (dir) {
	case Back:
	    return argmax(c -> c.row, selected);
	case Front:
	    return argmax(c -> -c.row, selected);
	case Left:
	    return argmax(c -> c.col, selected);
	case Right:
	    return argmax(c -> -c.col, selected);
	case Top:
	    return argmax(c -> c.height, selected);
	case Bot:
	    return argmax(c -> -c.height, selected);
	default:
	    throw new RuntimeException("invalid direction");
	}
    }

    // return retrieved from allitems, along with any potential selectors which
    // are empty.
    public Set<Item> adj(String dirstr, Set<Item> selected) {
	Direction dir = Direction.fromString(dirstr);
	Set<Item> selectors = selected.stream().map(c -> {
		PEPoint b = ((PEPoint) c).copy(dir);
		b.color = Color.Fake;
		return b;
	    }).collect(Collectors.toSet());

	this.allItems.addAll(selectors);

	Set<Item> actual = allItems.stream().filter(c -> selectors.contains(c)).collect(Collectors.toSet());

	return actual;
    }

    public static Set<Item> argmax(Function<PEPoint, Integer> f, Set<Item> items) {
	int maxvalue = Integer.MIN_VALUE;
	for (Item i : items) {
	    int cvalue = f.apply((PEPoint) i);
	    if (cvalue > maxvalue)
		maxvalue = cvalue;
	}
	final int maxValue = maxvalue;
	return items.stream().filter(c -> f.apply((PEPoint) c) >= maxValue).collect(Collectors.toSet());
    }

    @Override
    public void noop() {
	keyConsistency();
    }

    // get cubes at the outer locations
    private Set<Item> extremeCubes(Direction dir, Set<Item> selected) {
	Set<Item> realCubes = realBlocks(allItems);
	return selected.stream().map(c -> {
		PEPoint d = (PEPoint) c;
		while (realCubes.contains(d.copy(dir)))
		    d = d.copy(dir);
		return d;
	    }).collect(Collectors.toSet());
    }

    // ensures key coherence on mutations
    private void refreshSet(Set<Item> set) {
	List<Item> s = new LinkedList<>(set);
	set.clear();
	set.addAll(s);
    }

    private void keyConsistency() {
	refreshSet(allItems);
	refreshSet(selected);
	refreshSet(previous);
    }

    private Set<Item> realBlocks(Set<Item> all) {
	return all.stream().filter(b -> !((PEPoint) b).color.equals(Color.Fake)).collect(Collectors.toSet());
    }
}
