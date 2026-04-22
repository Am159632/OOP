package core;

/**
 * Constants for the named spaces loaded into the system.
 * Centralises the magic strings "FULL" and "PCA" that were
 * previously duplicated across multiple packages.
 */
public final class SpaceNames {

    /** The full-dimensional word-embedding space. */
    public static final String FULL = "FULL";

    /** The PCA-reduced projection space. */
    public static final String PCA = "PCA";

    private SpaceNames() {}
}
