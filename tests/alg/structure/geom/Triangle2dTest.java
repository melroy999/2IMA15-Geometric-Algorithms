package alg.structure.geom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Triangle2dTest {
    @Test
    void containsInside() {
        // Setup
        Point2d p1 = new Point2d(0, 0);
        Point2d p2 = new Point2d(3, 0);
        Point2d p3 = new Point2d(0, 3);
        Point2d p = new Point2d(1, 1);
        Triangle2d triangle = new Triangle2d(p1, p2, p3);
        Triangle2d triangle2 = new Triangle2d(p1, p3, p2);

        // Test
        Triangle2d.Location l = triangle.contains(p);
        Triangle2d.Location l2 = triangle2.contains(p);

        // Assert
        Assertions.assertEquals(Triangle2d.Location.INSIDE, l);
        Assertions.assertEquals(Triangle2d.Location.INSIDE, l2);
    }

    @Test
    void containsBorder() {
        // Setup
        Point2d p1 = new Point2d(0, 0);
        Point2d p2 = new Point2d(2, 0);
        Point2d p3 = new Point2d(0, 2);
        Point2d p = new Point2d(1, 1);
        Triangle2d triangle = new Triangle2d(p1, p3, p2);

        // Test
        Triangle2d.Location l = triangle.contains(p);

        // Assert
        Assertions.assertEquals(Triangle2d.Location.BORDER, l);
    }

    @Test
    void containsOutside() {
        // Setup
        Point2d p1 = new Point2d(0, 0);
        Point2d p2 = new Point2d(2, 0);
        Point2d p3 = new Point2d(0, 2);
        Point2d p = new Point2d(-5, 5);
        Triangle2d triangle = new Triangle2d(p1, p3, p2);

        // Test
        Triangle2d.Location l = triangle.contains(p);

        // Assert
        Assertions.assertEquals(Triangle2d.Location.OUTSIDE, l);
    }
}