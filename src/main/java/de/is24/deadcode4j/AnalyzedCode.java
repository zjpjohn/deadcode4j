package de.is24.deadcode4j;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

/**
 * The <code>AnalyzedCode</code> provides access to the code repositories and other convenient tools.
 *
 * @since 1.0.0
 */
public class AnalyzedCode {
    private final Collection<String> analyzedClasses;
    private final Map<String, ? extends Iterable<String>> codeDependencies;

    public AnalyzedCode(@Nonnull Collection<String> analyzedClasses, @Nonnull Map<String, ? extends Iterable<String>> codeDependencies) {
        this.analyzedClasses = analyzedClasses;
        this.codeDependencies = codeDependencies;
    }

    @Nonnull
    public Collection<String> getAnalyzedClasses() {
        return analyzedClasses;
    }

    @Nonnull
    public Map<String, ? extends Iterable<String>> getCodeDependencies() {
        return codeDependencies;
    }

}
