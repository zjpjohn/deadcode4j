package de.is24.deadcode4j.analyzer;

import org.junit.Test;

import java.util.Map;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public final class A_SimpleXmlAnalyzer extends AnAnalyzer {

    @Test
    public void usesTheSpecifiedDependerIdToReportDependencies() {
        final String dependerId = "junit";
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer(dependerId, ".xml", null) {
            {
                registerClassElement("elementWithClass");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(codeDependencies.keySet(), contains(dependerId));
    }

    @Test
    public void ignoresFilesWithNonMatchingRootElement() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", "acme") {
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/empty.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should NOT have analyzed the XML file!", codeDependencies.size(), is(0));
    }

    @Test
    public void reportsTheClassFoundForTheRegisteredElement() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", null) {
            {
                registerClassElement("elementWithClass");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(getOnlyElement(codeDependencies.values()), contains("de.is24.deadcode4j.ClassInElement"));
    }

    @Test
    public void reportsTheClassFound_ForTheRegisteredElement_HavingASpecificAttributeValue() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", null) {
            {
                registerClassElement("restrictedElement").withAttributeValue("locked", "false");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(concat(codeDependencies.values()), containsInAnyOrder("de.is24.deadcode4j.UnlockedClassInElement"));
    }

    @Test
    public void reportsTheClassFoundForTheRegisteredAttribute() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", null) {
            {
                registerClassAttribute("element", "attributeWithClass");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(getOnlyElement(codeDependencies.values()), contains("de.is24.deadcode4j.ClassInAttribute"));
    }

    @Test
    public void reportsTheClassFound_ForTheRegisteredAttribute_HavingASpecificAttributeValue() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", null) {
            {
                registerClassAttribute("restrictedElement", "attributeWithClass").withAttributeValue("locked", "false");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(concat(codeDependencies.values()), containsInAnyOrder("de.is24.deadcode4j.UnlockedClassInAttribute"));
    }

    @Test
    public void reportsTheClassesFound_ForTheRegisteredElementAndForTheRegisteredAttribute_BothHavingASpecificAttributeValue() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", null) {
            {
                registerClassElement("restrictedElement").withAttributeValue("locked", "false");
                registerClassAttribute("restrictedElement", "attributeWithClass").withAttributeValue("locked", "false");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(concat(codeDependencies.values()), containsInAnyOrder(
                "de.is24.deadcode4j.UnlockedClassInAttribute",
                "de.is24.deadcode4j.UnlockedClassInElement"));
    }

    @Test
    public void reportsMultipleFoundClasses() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", null) {
            {
                registerClassElement("anotherElementWithClass");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(getOnlyElement(codeDependencies.values()),
                containsInAnyOrder("de.is24.deadcode4j.FirstClassInAnotherElement",
                        "de.is24.deadcode4j.SecondClassInAnotherElement")
        );
    }

    @Test
    public void reportsClassesForDifferentRegistrations() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", null) {
            {
                registerClassElement("elementWithClass");
                registerClassElement("anotherElementWithClass");
                registerClassAttribute("element", "attributeWithClass");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/some.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(getOnlyElement(codeDependencies.values()),
                containsInAnyOrder("de.is24.deadcode4j.ClassInElement", "de.is24.deadcode4j.FirstClassInAnotherElement",
                        "de.is24.deadcode4j.SecondClassInAnotherElement", "de.is24.deadcode4j.ClassInAttribute")
        );
    }

    @Test
    public void parsesXmlFilesUsingNamespacePrefixes() {
        SimpleXmlAnalyzer objectUnderTest = new SimpleXmlAnalyzer("junit", ".xml", "root") {
            {
                registerClassElement("elementWithClass");
                registerClassElement("anotherElementWithClass");
                registerClassAttribute("element", "attributeWithClass");
            }
        };

        objectUnderTest.doAnalysis(codeContext, getFile("de/is24/deadcode4j/analyzer/prefixed.xml"));

        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat("Should have analyzed the XML file!", codeDependencies.size(), is(1));
        assertThat(getOnlyElement(codeDependencies.values()),
                containsInAnyOrder("de.is24.deadcode4j.ClassInElement", "de.is24.deadcode4j.FirstClassInAnotherElement",
                        "de.is24.deadcode4j.SecondClassInAnotherElement", "de.is24.deadcode4j.ClassInAttribute")
        );
    }

}
