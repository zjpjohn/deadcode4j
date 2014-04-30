package de.is24.deadcode4j.analyzer;

import de.is24.deadcode4j.Analyzer;
import de.is24.deadcode4j.analyzer.constants.ClassWithInnerClassNamedLikePotentialTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public final class A_ReferenceToConstantsAnalyzer extends AnAnalyzer {
    private static final String FQ_CONSTANTS = "de.is24.deadcode4j.analyzer.constants.Constants";
    private Analyzer objectUnderTest;
    private Set<String> dependers = newHashSet();
    private List<String> dependees = newArrayList();

    @Before
    public void setUp() throws Exception {
        objectUnderTest = new ReferenceToConstantsAnalyzer();
        codeContext.addAnalyzedClass(FQ_CONSTANTS); // make this class known to the context
        codeContext.addAnalyzedClass(FQ_CONSTANTS + ".More");

        dependers.clear();
        dependees.clear();
    }

    @After
    public void assertNoOtherDependenciesExist() {
        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat(codeDependencies.keySet(), equalTo(this.dependers));

        List<String> allReportedClasses = newArrayList(concat(codeDependencies.values()));
        assertThat(allReportedClasses, containsInAnyOrder(this.dependees.toArray()));
    }

    @Test
    public void recognizesDependencyToConstantInExpression() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantInExpression.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingConstantInExpression");
    }

    @Test
    public void recognizesDependencyToConstantInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingConstantInField");
    }

    @Test
    public void recognizesDependencyToConstantInMethod() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingConstantInMethod");
    }

    @Test
    public void recognizesDependencyToFullyQualifiedConstantInExpression() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingFQConstantInExpression.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingFQConstantInExpression");
    }

    @Test
    public void recognizesDependencyToFullyQualifiedConstantInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingFQConstantInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingFQConstantInField");
    }

    @Test
    public void recognizesDependencyToFullyQualifiedConstantInMethod() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingFQConstantInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingFQConstantInMethod");
    }

    @Test
    public void recognizesDependencyToConstantViaStaticImportInExpression() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantViaStaticImportInExpression.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingConstantViaStaticImportInExpression");
    }

    @Test
    public void recognizesDependencyToConstantViaStaticImportInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantViaStaticImportInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingConstantViaStaticImportInField");
    }

    @Test
    public void recognizesDependencyToConstantViaStaticImportInMethod() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantViaStaticImportInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingConstantViaStaticImportInMethod");
    }

    @Test
    public void recognizesDependencyToConstantViaStaticImportInSwitch() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantViaStaticImportInSwitch.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingConstantViaStaticImportInSwitch");
    }

    @Test
    public void recognizesDependencyToConstantViaAsteriskStaticImportInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingConstantViaAsteriskStaticImportInField.java");
        triggerFinishAnalysisEvent();

        // TODO implement
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageViaStaticImportIsOverwrittenByLocalVariable() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingStaticImportForConstantWithSameLocalNameInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingStaticImportForConstantWithSameLocalNameInMethod$InnerClass");
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageViaStaticImportIsOverwrittenByLocalVariableInSuperiorBlock() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingStaticImportForConstantWithSameLocalNameInSuperiorBlocksMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingStaticImportForConstantWithSameLocalNameInSuperiorBlocksMethod$InnerClass");
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageViaStaticImportIsOverwrittenByLocalVariableInStaticInitializer() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingStaticImportForConstantWithSameLocalNameInStaticInitializer.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingStaticImportForConstantWithSameLocalNameInStaticInitializer$InnerClass");
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageViaStaticImportExistsAlthoughInnerClassDefinesInstanceFieldWithSameName() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingStaticImportForConstantWithSameFieldNameDefinedByInnerClassInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassUsingStaticImportForConstantWithSameFieldNameDefinedByInnerClassInMethod");
        triggerFinishAnalysisEvent();
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageViaStaticImportIsOverwrittenByInstanceFieldBeingDeclaredAfterItIsReferenced() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingStaticImportForConstantWithSameFieldNameBeingDeclaredAfterItIsReferencedInMethod.java");
        triggerFinishAnalysisEvent();

        assertNoOtherDependenciesExist();
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageViaStaticImportIsOverwrittenByStaticField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingStaticImportForConstantWithSameStaticFieldNameInMethod.java");
        triggerFinishAnalysisEvent();

        assertNoOtherDependenciesExist();
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageIsOverwrittenByCatchClauseParameter() {
        // no one says you cannot name a variable like an imported class :(
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingImportForConstantWithSameParameterNameInCatchClause.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingImportForConstantWithSameParameterNameInCatchClause$InnerClass");
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageIsOverwrittenByConstructorParameter() {
        // no one says you cannot name a variable like an imported class :(
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingImportForConstantWithSameParameterNameInConstructor.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingImportForConstantWithSameParameterNameInConstructor$InnerClass");
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageIsOverwrittenByLocalVariable() {
        // no one says you cannot name a variable like an imported class :(
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingImportForConstantWithSameLocalNameInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingImportForConstantWithSameLocalNameInMethod$InnerClass");
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageIsOverwrittenByInstanceField() {
        // not allowed by JVM: prefers field all the time; however, the import may be defined
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingImportForConstantWithSameFieldNameInMethod.java");
        triggerFinishAnalysisEvent();

        assertNoOtherDependenciesExist();
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageIsOverwrittenByMethodParameter() {
        // no one says you cannot name a variable like an imported class :(
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingImportForConstantWithSameParameterNameInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingImportForConstantWithSameParameterNameInMethod$InnerClass");
    }

    @Test
    public void recognizesReferenceToConstantOfOtherPackageIsOverwrittenByStaticVariable() {
        // not allowed by JVM: prefers field all the time; however, the import may be defined
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingImportForConstantWithSameStaticFieldNameInMethod.java");
        triggerFinishAnalysisEvent();

        assertNoOtherDependenciesExist();
    }

    @Test
    public void recognizesDependencyToInnerClassInsteadOfPackageClass() {
        // make sure JVM specs don't mess with our assumptions
        assertThat(new ClassWithInnerClassNamedLikePotentialTarget().foo, is("bar"));
        assertThat(new ClassWithInnerClassNamedLikePotentialTarget.InnerClass().foo, is("bar"));

        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassWithInnerClassNamedLikePotentialTarget.java");
        triggerFinishAnalysisEvent();

        assertDependencyExists("de.is24.deadcode4j.analyzer.constants.ClassWithInnerClassNamedLikePotentialTarget",
                "de.is24.deadcode4j.analyzer.constants.ClassWithInnerClassNamedLikePotentialTarget$Constants");
        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.ClassWithInnerClassNamedLikePotentialTarget$AnotherInnerClass");
        assertDependencyExists("de.is24.deadcode4j.analyzer.constants.ClassWithInnerClassNamedLikePotentialTarget$InnerClass",
                "de.is24.deadcode4j.analyzer.constants.ClassWithInnerClassNamedLikePotentialTarget$Constants");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageInExpression() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageInExpression.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageInExpression");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageInField");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageInMethod() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageInMethod");
    }

    @Test
    public void recognizesDependencyToFullyQualifiedConstantOfOtherPackageInExpression() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingFQConstantOfOtherPackageInExpression.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingFQConstantOfOtherPackageInExpression");
    }

    @Test
    public void recognizesDependencyToFullyQualifiedConstantOfOtherPackageInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingFQConstantOfOtherPackageInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingFQConstantOfOtherPackageInField");
    }

    @Test
    public void recognizesDependencyToFullyQualifiedConstantOfOtherPackageInMethod() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingFQConstantOfOtherPackageInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingFQConstantOfOtherPackageInMethod");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageViaAsteriskImportInExpression() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageViaAsteriskImportInExpression.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageViaAsteriskImportInExpression");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageViaAsteriskImportInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageViaAsteriskImportInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageViaAsteriskImportInField");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageViaAsteriskImportInMethod() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageViaAsteriskImportInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageViaAsteriskImportInMethod");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageViaStaticImportInExpression() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageViaStaticImportInExpression.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageViaStaticImportInExpression");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageViaStaticImportInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageViaStaticImportInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageViaStaticImportInField");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageViaStaticImportInMethod() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageViaStaticImportInMethod.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingConstantOfOtherPackageViaStaticImportInMethod");
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageViaAsteriskStaticImportInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingConstantOfOtherPackageViaAsteriskStaticImportInField.java");
        triggerFinishAnalysisEvent();

        // TODO implement
    }

    @Test
    public void recognizesEnumsDependencyToConstantInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/EnumUsingConstantInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.EnumUsingConstantInField");
    }

    @Test
    public void recognizesDependencyToConstantForInnerClassesInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingInnerClassOfConstantInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyExists("de.is24.deadcode4j.analyzer.constants.ClassUsingInnerClassOfConstantInField",
                FQ_CONSTANTS + "$More");
    }

    @Test
    public void recognizesDependencyToConstantForInnerClassViaStaticImportInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingInnerClassOfConstantViaStaticImportInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyExists("de.is24.deadcode4j.analyzer.constants.ClassUsingInnerClassOfConstantViaStaticImportInField",
                FQ_CONSTANTS + "$More");
    }

    @Test
    public void recognizesDependencyToConstantForInnerClassViaAsteriskStaticImportInField() {
    }

    @Test
    public void recognizesDependencyToConstantOfOtherPackageForInnerClassesInField() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingInnerClassOfConstantOfOtherPackageInField.java");
        triggerFinishAnalysisEvent();

        assertDependencyExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingInnerClassOfConstantOfOtherPackageInField",
                FQ_CONSTANTS + "$More");
    }

    @Ignore("not sure if Constants.A_STRING.intern() is actually inlined by the compiler")
    @Test
    public void recognizesDependencyToConstantOfOtherPackageForInnerClassesInMethodCall() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/subpackage/ClassUsingInnerClassOfConstantOfOtherPackageInMethodCall.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.subpackage.ClassUsingInnerClassOfConstantOfOtherPackageInMethodCall");
    }

    @Test
    public void recognizesAnnotationsDependencyToConstantAsDefault() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/AnnotationUsingConstantAsDefault.java");
        triggerFinishAnalysisEvent();

        assertDependencyToConstantsExists("de.is24.deadcode4j.analyzer.constants.AnnotationUsingConstantAsDefault");
        assertDependencyExists("de.is24.deadcode4j.analyzer.constants.AnnotationUsingConstantAsDefault",
                "java.lang.annotation.ElementType");
    }

    @Test
    public void ignoresReferencesToStaticMethods() {
        analyzeFile("../../src/test/java/de/is24/deadcode4j/analyzer/constants/ClassUsingStaticMethodInStaticField.java");
        triggerFinishAnalysisEvent();
    }

    private void analyzeFile(String fileName) {
        objectUnderTest.doAnalysis(codeContext, getFile(fileName));
    }

    private void triggerFinishAnalysisEvent() {
        objectUnderTest.finishAnalysis(codeContext);
    }

    private void assertDependencyExists(String depender, String dependee) {
        Map<String, ? extends Iterable<String>> codeDependencies = codeContext.getAnalyzedCode().getCodeDependencies();
        assertThat(codeDependencies.keySet(), hasItem(depender));

        Iterable<String> allReportedClasses = concat(codeDependencies.values());
        assertThat(allReportedClasses, hasItem(dependee));
        this.dependers.add(depender);
        this.dependees.add(dependee);
    }

    private void assertDependencyToConstantsExists(String depender) {
        assertDependencyExists(depender, FQ_CONSTANTS);
    }

}
