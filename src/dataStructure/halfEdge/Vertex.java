package dataStructure.halfEdge;

public class Vertex {
    // The coordinates of the vertex in the plane.
    private final int x, y;

    // One of the half edges originating from this vertex.
    private HalfEdge incidentEdge;

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HalfEdge getIncidentEdge() {
        return incidentEdge;
    }

    public void setIncidentEdge(HalfEdge incidentEdge) {
        this.incidentEdge = incidentEdge;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex) {
            Vertex p = (Vertex) obj;
            return x == p.x && y == p.y;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(x);
        bits ^= java.lang.Double.doubleToLongBits(y) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }
}
