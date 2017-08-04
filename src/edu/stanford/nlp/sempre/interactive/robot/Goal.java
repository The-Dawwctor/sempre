package edu.stanford.nlp.sempre.interactive.robot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.collections.Lists;
import org.apache.commons.math3.complex.Quaternion;

import edu.stanford.nlp.sempre.Json;
import edu.stanford.nlp.sempre.interactive.robot.Point;

/* Represents a Potential Energy Point, or a point that
 * can be used to represent a goal or an obstacle to be fed into
 * an operational space controller.
 */
public class Goal extends Point {
    int order;
    
    public Goal() {
        super();
        this.order = 0;
        this.names.add("Goal");
    }

    // used as a key
    public Goal(int x, int y, int z) {
        super(x, y, z);
        this.order = 0;
        this.names.add("Goal");
    }

    public Goal(int id, int x, int y, int z, Quaternion rotate, String color, int order) {
        super(id, x, y, z, rotate, color);
        this.order = order;
        this.names.add("Goal");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object get(String property) {
        Object propval;
        if (property.equals("order"))
            propval = new Integer(this.order);
        else
            propval = super.get(property);
        return propval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(String property, Object value) {
        if (property.equals("order") && value instanceof Integer) {
            this.order = (Integer) value;
        } else {
            super.update(property, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static Goal fromJSONObject(List<Object> props) {
        Point p = Point.fromJSONObject(props);
        Goal retpoint = new Goal();
        retpoint.names = p.names;
        retpoint.id = p.id;
        retpoint.x = p.x;
        retpoint.y = p.y;
        retpoint.z = p.z;
        retpoint.rotate = p.rotate;
        retpoint.color = p.color;
        retpoint.order = ((Integer) props.get(7));
        return retpoint;
    }

    @SuppressWarnings("unchecked")
    public Object toJSON() {
        List<Object> Goal = (List)super.toJSON();
        Goal.add(order);
        return Goal;
    }

    @Override
    public Goal clone() {
        return new Goal(this.id, this.x, this.y, this.z, this.rotate, this.color.toString(), this.order);
    }
}
