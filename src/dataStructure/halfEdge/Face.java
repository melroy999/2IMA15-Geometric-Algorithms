package dataStructure.halfEdge;

import java.util.ArrayList;
import java.util.List;

public class Face {
    // Here, we have one half edge that is part of the cycle around the face.
    private HalfEdge outerComponent;

    // Half edges of the cycles that are inside of this face.
    private final List<HalfEdge> innerComponents = new ArrayList<>();

    /**
     * Set the outer component of this face, which is a half edge that is part of the cycle around this face.
     * @param outerComponent The half-edge that is part of the outer cycle.
     */
    public void setOuterComponent(HalfEdge outerComponent) {
        this.outerComponent = outerComponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Face face = (Face) o;

        if (outerComponent != null ? !outerComponent.equals(face.outerComponent) : face.outerComponent != null)
            return false;
        return innerComponents != null ? innerComponents.equals(face.innerComponents) : face.innerComponents == null;
    }

    @Override
    public int hashCode() {
        int result = outerComponent != null ? outerComponent.hashCode() : 0;
        result = 31 * result + (innerComponents != null ? innerComponents.hashCode() : 0);
        return result;
    }
}
