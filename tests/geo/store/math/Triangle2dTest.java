package geo.store.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Triangle2dTest {
    @Test
    public void counterClockwiseExceptionTriangle2dTest() {
        assertThrows(Triangle2d.ClockwiseException.class,
                ()-> new Triangle2d(new Point2d(0, 0), new Point2d(0, 1), new Point2d(1, 0)));
    }

    @Test
    public void collinearExceptionTriangle2dTest() {
        assertThrows(Triangle2d.CollinearPointsException.class,
                ()-> new Triangle2d(new Point2d(1, 5), new Point2d(5, 5), new Point2d(4, 5)));
    }

    @Test
    public void circumCenterTest1a() {
        Triangle2d t = new Triangle2d(new Point2d(0, 0), new Point2d(1, 0), new Point2d(0, 1));
        Point2d v = t.getCircumCenter();

        Assertions.assertEquals(0.5d, v.x, 10e-6);
        Assertions.assertEquals(0.5d , v.y, 10e-6);
    }

    @Test
    public void circumCenterTest1b() {
        Triangle2d t = new Triangle2d(new Point2d(0, 0), new Point2d(10e9, 0), new Point2d(0, 10e9));
        Point2d v = t.getCircumCenter();

        Assertions.assertEquals(0.5d * 10e9, v.x, 10e-6);
        Assertions.assertEquals(0.5d * 10e9, v.y, 10e-6);
    }

    @Test
    public void circumCenterTest2() {
        Triangle2d t = new Triangle2d(new Point2d(9, 0), new Point2d(15, 0), new Point2d(6, 6));
        Point2d v = t.getCircumCenter();

        Assertions.assertEquals(12d, v.x, 10e-6);
        Assertions.assertEquals(5.25 , v.y, 10e-6);
    }

    @Test
    public void circumCenterTest3() {
        Triangle2d t = new Triangle2d(new Point2d(1, 3), new Point2d(7, 5), new Point2d(5, 5));
        Point2d v = t.getCircumCenter();

        Assertions.assertEquals(6d, v.x, 10e-6);
        Assertions.assertEquals(-2d , v.y, 10e-6);
    }

    @Test
    public void inCircumCircleTest1a() {
        Triangle2d t = new Triangle2d(new Point2d(0, 0), new Point2d(1, 0), new Point2d(0, 1));
        Assertions.assertTrue(t.isInCircumCircle(new Point2d(0.5, 0.5)));
        Assertions.assertFalse(t.isInCircumCircle(new Point2d(1, 0)));
    }

    @Test
    public void inCircumCircleTest1b() {
        Triangle2d t = new Triangle2d(new Point2d(0, 0), new Point2d(10e9, 0), new Point2d(0, 10e9));
        Assertions.assertTrue(t.isInCircumCircle(new Point2d(0.5, 0.5)));
        Assertions.assertTrue(t.isInCircumCircle(new Point2d(0.5, 0.5)));
        Assertions.assertFalse(t.isInCircumCircle(new Point2d(10e9, 0)));
        Assertions.assertFalse(t.isInCircumCircle(new Point2d(10e9, 10e9)));
        Assertions.assertEquals(Math.sqrt(Math.pow(10e9d, 2) + Math.pow(10e9d, 2)) / 2, t.circumRadius, 10e-6);
    }
}