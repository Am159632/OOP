package visuals;

/**
 * Centralised colour constants for item highlighting.
 * Eliminates repeated hex-string literals across action classes.
 */
public final class HighlightColors {

    /** Primary highlight colour – used for query/target items. */
    public static final String PRIMARY = "#007BFF";

    /** Success highlight colour – used for result / positive items. */
    public static final String SUCCESS = "#28A745";

    /** Warning highlight colour – used for connecting lines and secondary emphasis. */
    public static final String WARNING = "#FD7E14";

    /** Danger highlight colour – used for negative / opposite-pole items. */
    public static final String DANGER = "#DC3545";

    private HighlightColors() {}
}
