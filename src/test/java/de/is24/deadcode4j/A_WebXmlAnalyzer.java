package de.is24.deadcode4j;

import javassist.ClassPool;
import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public final class A_WebXmlAnalyzer {

    @Test
    public void shouldParseWebXmlFiles() {
        WebXmlAnalyzer objectUnderTest = new WebXmlAnalyzer();

        CodeContext codeContext = new CodeContext(getClass().getClassLoader(), mock(ClassPool.class));
        objectUnderTest.doAnalysis(codeContext, "web.xml");

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(getOnlyElement(codeDependencies.values()),
                containsInAnyOrder("WebAppListener", "WebAppFilter", "WebAppServlet"));
    }

}