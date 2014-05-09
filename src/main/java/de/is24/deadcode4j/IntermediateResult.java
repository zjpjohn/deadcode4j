package de.is24.deadcode4j;

/**
 * If an <code>IntermediateResult</code> is put into a {@link CodeContext#getCache() code context's cache},
 * it will be made available to the code context of those modules depending on the module the result belongs to.
 *
 * @since 1.6
 */
public interface IntermediateResult {

    /**
     * If a module depends on both module <i>A</i> and <i>B</i>, each providing an intermediate result for the same key,
     * the results will be merged by calling this method on <i>A</i>'s result using <i>B</i>'s result as a parameter.</br>
     * Note that if collisions occur, <i>A</i>'s results should usually be preferred as <i>A</i> is listed higher in the
     * class path.</p>
     * <b>It is important that neither result is modified by this call.</b>
     *
     * @param sibling the <code>IntermediateResult</code> to merge with
     * @return a new <code>IntermediateResult</code> instance
     * @since 1.6
     */
    IntermediateResult mergeSibling(IntermediateResult sibling);

    /**
     * If a module depends on module <i>A</i> which in turn depends on module <i>B</i>, both providing an intermediate
     * result for the same key, the results will be merged by calling this method on <i>A</i>'s result using <i>B</i>'s
     * result as a parameter.</br>
     * Note that if collisions occur, <i>A</i>'s results should be preferred as <i>A</i> is listed higher in the class
     * path.</p>
     * <b>It is important that neither result is modified by this call.</b>
     *
     * @param parent the <code>IntermediateResult</code> to merge with
     * @return a new <code>IntermediateResult</code> instance
     * @since 1.6
     */
    IntermediateResult mergeParent(IntermediateResult parent);

}
