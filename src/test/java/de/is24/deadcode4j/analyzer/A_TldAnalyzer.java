package de.is24.deadcode4j.analyzer;

import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public final class A_TldAnalyzer extends AnAnalyzer {

    @Test
    public void shouldParseTldFiles() {
        TldAnalyzer objectUnderTest = new TldAnalyzer();

        objectUnderTest.doAnalysis(codeContext, getFile("taglib.tld"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the TLD file!", codeDependencies.size(), is(1));
        assertThat(getOnlyElement(codeDependencies.values()),
                containsInAnyOrder("TagClass", "TagExtraInfo", "TagLibraryValidator", "TldFunction", "WebAppListener"));
    }

}
