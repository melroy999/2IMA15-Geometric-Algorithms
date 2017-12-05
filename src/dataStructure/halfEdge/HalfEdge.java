package dataStructure.halfEdge;

public class HalfEdge {
    // The vertex this half edge originates from.
    private final Vertex origin;

    // The face to the left of this half edge.
    private Face incidentFace;

    // The half edge that moves in the opposite direction.
    private HalfEdge twin;

    // The next half edge in our cycle.
    private HalfEdge next;

    // The previous half edge in our cycle.
    private HalfEdge previous;

    public HalfEdge(Vertex origin) {
        this.origin = origin;
        origin.setIncidentEdge(this);
    }

    public HalfEdge getTwin() {
        return twin;
    }

    public void setTwin(HalfEdge twin) {
        this.twin = twin;
    }

    public Face getIncidentFace() {
        return incidentFace;
    }

    public HalfEdge setIncidentFace(Face incidentFace) {
        this.incidentFace = incidentFace;
        return this;
    }

    public HalfEdge getNext() {
        return next;
    }

    public void setNext(HalfEdge next) {
        this.next = next;
    }

    public HalfEdge getPrevious() {
        return previous;
    }

    public void setPrevious(HalfEdge previous) {
        this.previous = previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HalfEdge halfEdge = (HalfEdge) o;

        if (origin != null ? !origin.equals(halfEdge.origin) : halfEdge.origin != null) return false;
        if (incidentFace != null ? !incidentFace.equals(halfEdge.incidentFace) : halfEdge.incidentFace != null)
            return false;
        if (twin != null ? !twin.equals(halfEdge.twin) : halfEdge.twin != null) return false;
        if (next != null ? !next.equals(halfEdge.next) : halfEdge.next != null) return false;
        return previous != null ? previous.equals(halfEdge.previous) : halfEdge.previous == null;
    }

    @Override
    public int hashCode() {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (incidentFace != null ? incidentFace.hashCode() : 0);
        result = 31 * result + (twin != null ? twin.hashCode() : 0);
        result = 31 * result + (next != null ? next.hashCode() : 0);
        result = 31 * result + (previous != null ? previous.hashCode() : 0);
        return result;
    }
}
