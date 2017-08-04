package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.collections.Lists;
import org.apache.commons.math3.complex.Quaternion;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

/* Represents an obstacle in the world.
 */
public class Obstacle extends Point {
    public Obstacle() {
        super();
        this.names.add("Obstacle");
    }

    // used as a key
    public Obstacle(int x, int y, int z) {
        super(x, y, z);
        this.names.add("Obstacle");
    }

    public Obstacle(int id, int x, int y, int z, Quaternion rotate, String color) {
        super(id, x, y, z, rotate, color);
        this.names.add("Obstacle");
    }

    public Obstacle(Point p) {
        this(p.id, p.x, p.y, p.z, p.rotate, p.color.toString());
        this.names.add("Obstacle");
    }

    @SuppressWarnings("unchecked")
    public static Obstacle fromJSONObject(List<Object> props) {
        Point p = Point.fromJSONObject(props);
        Obstacle retpoint = new Obstacle();
        retpoint.names = p.names;
        retpoint.id = p.id;
        retpoint.x = p.x;
        retpoint.y = p.y;
        retpoint.z = p.z;
        retpoint.rotate = p.rotate;
        retpoint.color = p.color;
        return retpoint;
    }

    @Override
    public Obstacle clone() {
        return new Obstacle(this.id, this.x, this.y, this.z, this.rotate, this.color.toString());
    }
}
