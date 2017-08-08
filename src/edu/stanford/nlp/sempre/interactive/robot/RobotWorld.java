package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.apache.commons.math3.complex.Quaternion;
import redis.clients.jedis.Jedis;

import edu.stanford.nlp.sempre.ContextValue;
import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.NaiveKnowledgeGraph;
import edu.stanford.nlp.sempre.StringValue;
import edu.stanford.nlp.sempre.interactive.Item;
import edu.stanford.nlp.sempre.interactive.World;
import fig.basic.Option;

/* Contains a world made up of a set of points with properties that
 * can be specified and fed into an operational space controller
 * for a robot.
 */
public class RobotWorld extends World {
    public final static String SELECT = "S";
    private int currentID;
    private int currentOrder;
    private Jedis jedis;

    // Returns next unused ID
    private int incrementAndGetID() {
        currentID++;
        return currentID;
    }

    // Returns next unused order
    private int incrementAndGetOrder() {
        currentOrder++;
        return currentOrder;
    }

    // Finds highest ID and order in world; next highest unused
    private void setStartNums() {
        for (Item p : allItems) {
            int id = ((Point) p).id;
            if (id > currentID) {
                currentID = id;
            }
            if (p instanceof Goal) {
                int order = ((Goal) p).order;
                if (order > currentOrder) {
                    currentOrder = order;
                }
            }
        }
    }

    public static RobotWorld fromContext(ContextValue context) {
        if (context == null || context.graph == null) {
            return fromJSON("[]");
        }
        NaiveKnowledgeGraph graph = (NaiveKnowledgeGraph) context.graph;
        String wallString = ((StringValue) graph.triples.get(0).e1).value;
        return fromJSON(wallString);
    }

    public Set<Item> origin() {
        for (Item i : allItems) {
            Point b = (Point) i;
            if (b.x == 0 && b.y == 0 && b.z == 0)
                return Sets.newHashSet(b);
        }
        Point basepoint = new Point(incrementAndGetID(), 0, 0, 0, Quaternion.ZERO, Color.Fake.toString());
        return Sets.newHashSet(basepoint);
    }

    @SuppressWarnings("unchecked")
    public RobotWorld(Set<Item> pointset) {
        super();
        this.allItems = pointset;
        this.selected = pointset.stream().filter(b -> ((Point) b).names.contains(SELECT)).collect(Collectors.toSet());
        this.selected.forEach(i -> i.names.remove(SELECT));
        this.jedis = new Jedis("localhost", 6379);
        this.jedis.select(1);
        setStartNums();
    }

    // Communicates state of the world to web client with JSON and redis client
    // only use names S to communicate with client, internally it's just select variable
    @Override
    public String toJSON() {
        // JSON code
        return Json.writeValueAsStringHard(allItems.stream().map(c -> {
            Point b = (Point) c;
            if (selected.contains(b)) {
                b.names.add(SELECT);
            }
            return b.toJSON();
        }).collect(Collectors.toList()));
    }

    private static RobotWorld fromJSON(String wallString) {
        @SuppressWarnings("unchecked")
        List<List<Object>> itemstr = Json.readValueHard(wallString, List.class);
        Set<Item> items = itemstr.stream().map(c -> {
            List<String> types = (List) c.get(0);
            if (types.contains("Goal")) {
                return Goal.fromJSONObject(c);
            } else if (types.contains("Obstacle")) {
                return Obstacle.fromJSONObject(c);
            } else if (types.contains("OpPoint")) {
                return OpPoint.fromJSONObject(c);
            } else {
                return Point.fromJSONObject(c);
            }
        }).collect(Collectors.toSet());
        return new RobotWorld(items);
    }

    @Override
    public Set<Item> has(String rel, Set<Object> values) {
        return allItems.stream().filter(i -> values.contains(i.get(rel))).collect(Collectors.toSet());
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
    // likewise, if some fake colored point is no longer selected, remove it
    @Override
    public void merge() {
        Sets.difference(selected, allItems).forEach(i -> ((Point) i).color = Color.Fake);
        allItems.removeIf(c -> ((Point) c).color.equals(Color.Fake) && !selected.contains(c));
        allItems.addAll(selected);
    }

    // Move selected elements certain amount in a certain direction
    public void move(String dir, int number, Set<Item> selected) {
        Direction d = Direction.fromString(dir);
        for (Item i : selected) {
            selected.remove(i);
            Point p = (Point) i;
            int newX = p.x;
            int newY = p.y;
            int newZ = p.z;
            switch (d) {
                case Back:
                newX += number;
                break;
                case Front:
                newX -= number;
                break;
                case Left:
                newY += number;
                break;
                case Right:
                newY -= number;
                break;
                case Top:
                newZ += number;
                break;
                case Bot:
                newZ -= number;
                break;
                case None:
                break;
            }
            Goal newGoal = new Goal(incrementAndGetID(), newX, newY, newZ, p.rotate, p.color.toString(), incrementAndGetOrder());
            selected.add(newGoal);
            allItems.add(newGoal);
        }
    }

    public void add(String colorstr, String dirstr, Set<Item> selected) {
        Direction dir = Direction.fromString(dirstr);

        if (dir == Direction.None) { // add here
            selected.forEach(b -> ((Point) b).color = Color.fromString(colorstr));
        } else {
            Set<Item> extremePoints = extremePoints(dir, selected);
            allItems.addAll(extremePoints.stream().map(c -> {
                Point d = ((Point) c).copy(dir);
                return new Point(incrementAndGetID(), d.x, d.y, d.z, d.rotate, colorstr);
            }).collect(Collectors.toList()));
        }
    }

    // Add goal position in coordinate system
    public void add(String colorstr, int x, int y, int z) {
        Point newPoint = new Point(incrementAndGetID(), x, y, z, Quaternion.ZERO, colorstr);
        allItems.add(newPoint);
    }

    // Set obstacle position in coordinate system
    public void block(int x, int y, int z) {
        Obstacle newObstacle = new Obstacle(incrementAndGetID(), x, y, z, Quaternion.ZERO, "red");
        allItems.add(newObstacle);
    }

    // Goto position of certain color
    public void go_to(String colorstr) {
        Point dest = null;
        for (Item c : allItems) {
            Point choice = (Point) c;
            if (choice.color.toString().equals(colorstr)) {
                dest = choice;
            }
        }
        // Going to nonexistent color in world
        if (dest == null) {
            return;
        }
        Goal goal = new Goal(incrementAndGetID(), dest.x, dest.y, dest.z, dest.rotate, dest.color.toString(), incrementAndGetOrder());
        selected.clear();
        selected.add(goal);
        allItems.add(goal);
    }

    // Goto position of specific rigidbody
    public void gotoOptitrack(int number) {
        jedis.publish("nrc-optitrack", str(number));
    }

    // Goto goal block with linear trajectory
    // Takes in color string of destination block
    public void lgoto(String colorstr, Set<Item> selected) {
        Point oldDest = null;
        for (Item c : allItems) {
            Point choice = (Point) c;
            if (choice.color.toString().equals(colorstr)) {
                oldDest = choice;
            }
        }
        // Going to nonexistent color in world
        if (oldDest == null) {
            return;
        }
        final Point dest = oldDest;
        selected.forEach(b -> {
            Point start = (Point) b;
            double increment = 16;
            for (int i = 0; i < increment; i++) {
                double xDiff = start.x + i * (dest.x - start.x) / increment;
                double yDiff = start.y + i * (dest.y - start.y) / increment;
                double zDiff = start.z + i * (dest.z - start.z) / increment;
                Point mid = new Point(0, (int)Math.round(xDiff), (int)Math.round(yDiff), (int)Math.round(zDiff), Quaternion.ZERO, "black");
                allItems.add(mid);
            }
        });
    }

    // Get points at extreme positions
    public Set<Item> veryx(String dirstr, Set<Item> selected) {
        Direction dir = Direction.fromString(dirstr);
        switch (dir) {
            case Back:
            return argmax(c -> c.x, selected);
            case Front:
            return argmax(c -> -c.x, selected);
            case Left:
            return argmax(c -> c.y, selected);
            case Right:
            return argmax(c -> -c.y, selected);
            case Top:
            return argmax(c -> c.z, selected);
            case Bot:
            return argmax(c -> -c.z, selected);
            default:
            throw new RuntimeException("invalid direction");
        }
    }

    // return retrieved from allitems, along with any potential empty selectors
    public Set<Item> adj(String dirstr, Set<Item> selected) {
        Direction dir = Direction.fromString(dirstr);
        Set<Item> selectors = selected.stream().map(c -> {
            Point b = ((Point) c).copy(dir);
            b.color = Color.Fake;
            return b;
        }).collect(Collectors.toSet());

        allItems.addAll(selectors);

        Set<Item> actual = allItems.stream().filter(c -> selectors.contains(c)).collect(Collectors.toSet());

        return actual;
    }

    public static Set<Item> argmax(Function<Point, Integer> f, Set<Item> items) {
        int maxvalue = Integer.MIN_VALUE;
        for (Item i : items) {
            int cvalue = f.apply((Point) i);
            if (cvalue > maxvalue)
                maxvalue = cvalue;
        }
        final int maxValue = maxvalue;
        return items.stream().filter(c -> f.apply((Point) c) >= maxValue).collect(Collectors.toSet());
    }

    public void send() {
        jedis.publish("nrc-world-state", toJSON());
        jedis.publish("nrc-optitrack", "start");
    }

    @Override
    public void noop() {
        keyConsistency();
    }

    // get points at outer locations
    private Set<Item> extremePoints(Direction dir, Set<Item> selected) {
        Set<Item> realPoints = realPoints(allItems);
        return selected.stream().map(c -> {
            Point d = (Point) c;
            while (realPoints.contains(d.copy(dir)))
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

    private Set<Item> realPoints(Set<Item> all) {
        return all.stream().filter(b -> !((Point) b).color.equals(Color.Fake)).collect(Collectors.toSet());
    }
}