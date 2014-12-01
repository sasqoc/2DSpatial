package pl.sasqoc.game;

/**
 *
 * @author amadela
 */
public enum FunctionMode {

    SIMPLE_ALL("Brute Force Simply All(AABB)"), SPATIAL_CELLS("Broad phase Spatial Hashing(Cells)"), SPATIAL("Broad+Mid phase, Spatial Hashing + AABB"),
    SIMPLE_ALL_ALL("Brute Force Simply AllxAll(AABBxAABB)"), SPATIAL_ALL_ALL("Spatial Hashing AllxAll");

    private final String name;

    FunctionMode(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
