package de.is24.deadcode4j;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * An <code>Analyzer</code> analyzes code of all flavours: java classes, spring XML files, <tt>web.xml</tt> etc.
 *
 * @since 1.1.0
 */
public interface Analyzer {

    /**
     * Perform an analysis for the specified file.
     * Results must be reported via the capabilities of the {@link CodeContext}.
     *
     * @since 1.1.0
     */
    void doAnalysis(@Nonnull CodeContext codeContext, @Nonnull File fileName);

    /**
     * Indicates that all files have been processed.
     * This method offers <code>Analyzer</code>s the possibility to report dependencies based on the whole project.
     *
     * @since 1.4
     */
    void finishAnalysis(@Nonnull CodeContext codeContext);

}
