package geo.util;

import geo.gui.GUI;
import geo.store.math.Point2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clip a polygon, using the Sutherland Hodgman clipping algorithm.
 */
public class SutherlandHodgmanClipping {
    // Get the current screen dimensions.
    private static Dimension bb;

    /**
     * Set the bounding box to use in the clipping.
     *
     * @param bb The bounding box, represented as a Dimensions object.
     */
    public static void setBoundingBox(Dimension bb) {
        SutherlandHodgmanClipping.bb = new Dimension(bb.width + 1, bb.height + 1);
    }

    /**
     * Clip the given polygon, represented by points given in CCW order.
     *
     * @param points The points surrounding the face of the polygon, given in CCW order.
     * @return Points given in CCW that fit inside of the rectangular bounding box.
     */
    public static List<Point2d> clipPolygon(List<Point2d> points) {
        // First, set the current bounding box.
        setBoundingBox(GUI.createAndShow().getGamePanelDimensions());

        // First, get the points in CCW order which we are clipping on.
        List<Point2d> clipPoints = Arrays.asList(
                new Point2d(-1, -1),
                new Point2d(bb.width, -1),
                new Point2d(bb.width, bb.height),
                new Point2d(-1, bb.height));

        // Now, for each pair of clip points forming a clipping edge:
        for(int i = 0; i < clipPoints.size(); i++) {
            // Clear the list of clipped vertices.
            List<Point2d> clipped = new ArrayList<>();

            Point2d cp1 = clipPoints.get(i);
            Point2d cp2 = clipPoints.get((i + 1) % clipPoints.size());

            // Now, iterate over each of the vertices in the polygon...
            for(int j = 0; j < points.size(); j++) {
                Point2d v1 = points.get(j);
                Point2d v2 = points.get((j + 1) % points.size());

                if(isLeftOfClippingLine(v1, cp1, cp2)) {
                    if(isLeftOfClippingLine(v2, cp1, cp2)) {
                        clipped.add(v2);
                    } else {
                        clipped.add(getIntersectionPoint(v1, v2, cp1, cp2));
                    }
                } else {
                    if(isLeftOfClippingLine(v2, cp1, cp2)) {
                        clipped.add(getIntersectionPoint(v1, v2, cp1, cp2));
                        clipped.add(v2);
                    }
                }
            }

            // Continue working with the clipped polygon.
            points.clear();
            points.addAll(clipped);
        }

        // Return the clipped polygon.
        return points;
    }

    /**
     * Check whether the given point is inside of the clipping area.
     *
     * @param p The point to check the location of.
     * @return True if the point is inside the given dimensions, false otherwise.
     */
    private static boolean isLeftOfClippingLine(Point2d p, Point2d cp1, Point2d cp2) {
        // Since we use CCW, the cross product should be smaller or equal to 0.
        return (p.x - cp1.x) * (cp2.y - cp1.y) - (p.y - cp1.y) * (cp2.x - cp1.x) <= 0;
    }

    /**
     * Get the intersection point of the two lines.
     *
     * @param v1 The starting point of the first line.
     * @param v2 The end point of the first line.
     * @param cp1 The starting point of the second line.
     * @param cp2 The end point of the second line.
     * @return An intersection point of the two lines.
     */
    private static Point2d getIntersectionPoint(Point2d v1, Point2d v2, Point2d cp1, Point2d cp2) {
        double delta_vy = v2.y - v1.y;
        double delta_vx = v1.x - v2.x;
        double dot_delta_v = delta_vy * v1.x + delta_vx * v1.y;

        double delta_cpy = cp2.y - cp1.y;
        double delta_cpx = cp1.x - cp2.x;
        double dot_delta_cp = delta_cpy * cp1.x + delta_cpx * cp1.y;

        double det = 1d / (delta_vy * delta_cpx - delta_cpy * delta_vx);
        double x = det * (delta_cpx * dot_delta_v - delta_vx * dot_delta_cp);
        double y = det * (delta_vy * dot_delta_cp - delta_cpy * dot_delta_v);

        // Create a new point with the given coordinates.
        return new Point2d(x, y);
    }
}
