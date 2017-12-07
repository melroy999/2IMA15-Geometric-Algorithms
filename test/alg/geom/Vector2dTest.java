package alg.structure.geom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2dTest {
    @Test
    void angle90Degrees() {
        Vector2d v1 = new Vector2d(0, 5);
        Vector2d v2 = new Vector2d(-5, 0);
        Assertions.assertEquals(90, Math.toDegrees(v1.angle(v2)));
    }

    @Test
    void angleMinus90Degrees() {
        Vector2d v1 = new Vector2d(0, 5);
        Vector2d v2 = new Vector2d(5, 0);
        Assertions.assertEquals(-90, Math.toDegrees(v1.angle(v2)));
    }

    @Test
    void angle45Degrees() {
        Vector2d v1 = new Vector2d(0, 5);
        Vector2d v2 = new Vector2d(-5, 5);
        Assertions.assertEquals(45, Math.toDegrees(v1.angle(v2)));
    }

}