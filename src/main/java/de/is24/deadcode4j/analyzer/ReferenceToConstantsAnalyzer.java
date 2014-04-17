package de.is24.deadcode4j.analyzer;

import de.is24.deadcode4j.CodeContext;
import japa.parser.JavaParser;
import japa.parser.ast.*;
import japa.parser.ast.body.*;
import japa.parser.ast.comments.BlockComment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.comments.LineComment;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.type.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class ReferenceToConstantsAnalyzer extends AnalyzerAdapter {

    private final Collection<Analysis> resultsNeedingPostProcessing = newArrayList();

    @Override
    public void doAnalysis(@Nonnull CodeContext codeContext, @Nonnull File file) {
        if (file.getName().endsWith(".java")) {
            logger.debug("Analyzing Java file [{}]...", file);
            analyzeJavaFile(codeContext, file);
        }
    }

    @Override
    public void finishAnalysis(@Nonnull CodeContext codeContext) {
        Collection<String> analyzedClasses = codeContext.getAnalyzedCode().getAnalyzedClasses();
        for (Analysis analysis : resultsNeedingPostProcessing) {
            for (Map.Entry<String, String> referenceToPackageType : analysis.referencesToPackageType.entrySet()) {
                String dependee = referenceToPackageType.getValue();
                if (analyzedClasses.contains(dependee)) {
                    codeContext.addDependencies(referenceToPackageType.getKey(), dependee);
                }
            }
        }
    }

    private void analyzeJavaFile(final CodeContext codeContext, File file) {
        CompilationUnit compilationUnit;
        try {
            compilationUnit = JavaParser.parse(file, null, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse [" + file + "]!", e);
        }

        System.out.println(compilationUnit);
        Analysis result = compilationUnit.accept(new CompilationUnitVisitor(codeContext), null);
        if (result.needsPostProcessing()) {
            this.resultsNeedingPostProcessing.add(result);
        }
    }

    private static class CompilationUnitVisitor extends GenericVisitorAdapter<Analysis, Void> {

        private final CodeContext codeContext;
        private final Map<String, String> imports = new HashMap<String, String>();
        private final Queue<String> typeNames = new LinkedList<String>();
        private final Set<String> innerTypes = new HashSet<String>();
        private final Map<String, String> referenceToInnerOrPackageType = new HashMap<String, String>();
        private String typeName;
        private String packageName;
        private int depth = 0;

        public CompilationUnitVisitor(CodeContext codeContext) {
            this.codeContext = codeContext;
        }

        @Override
        public Analysis visit(AnnotationDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(AnnotationMemberDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ArrayAccessExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ArrayCreationExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ArrayInitializerExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(AssertStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(BinaryExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(BlockComment n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(BlockStmt n, Void arg) {
            print(n, n.getStmts());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(BooleanLiteralExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(BreakStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(CastExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(CatchClause n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(CharLiteralExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ClassExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ClassOrInterfaceDeclaration n, Void arg) {
            print(n, n.getName());
            this.typeNames.add(n.getName());
            if (this.typeNames.size() == 1) {
                this.typeName = n.getName();
            } else {
                this.innerTypes.add(n.getName());
            }
            depth++;
            super.visit(n, arg);
            depth--;
            this.typeNames.remove(n.getName());
            return null;
        }

        @Override
        public Analysis visit(ClassOrInterfaceType n, Void arg) {
            print(n, n.getName() + "/" + n.getScope() + "/" + n.getTypeArgs());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(CompilationUnit n, Void arg) {
            print(n, "");
            depth++;
            super.visit(n, arg);
            depth--;
            resolveInnerTypeReferences();
            return new Analysis(this.packageName, this.referenceToInnerOrPackageType);
        }

        @Override
        public Analysis visit(ConditionalExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ConstructorDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ContinueStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(DoStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(DoubleLiteralExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(EmptyMemberDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(EmptyStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(EmptyTypeDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(EnclosedExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(EnumConstantDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(EnumDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ExplicitConstructorInvocationStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ExpressionStmt n, Void arg) {
            print(n, n.getExpression());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(FieldAccessExpr n, Void arg) {
            print(n, n.getScope() + "." + n.getField() + "/" + n.getFieldExpr() + "/" + n.getTypeArgs());
            if (FieldAccessExpr.class.isInstance(n.getScope())) {
                codeContext.addDependencies(buildTypeName(), n.getScope().toString());
            } else if (NameExpr.class.isInstance(n.getScope())) {
                String typeName = NameExpr.class.cast(n.getScope()).getName();
                String referencedType = this.imports.get(typeName);
                if (referencedType != null) {
                    codeContext.addDependencies(buildTypeName(), referencedType);
                } else {
                    this.referenceToInnerOrPackageType.put(buildTypeName(), typeName);
                }
            } else {
                depth++;
                super.visit(n, arg);
                depth--;
            }
            return null;
        }

        @Override
        public Analysis visit(FieldDeclaration n, Void arg) {
            print(n, n.getType() + "/" + n.getVariables());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(ForeachStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ForStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(IfStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ImportDeclaration n, Void arg) {
            print(n, n.getName() + "/static: " + n.isStatic() + "/asterisk: " + n.isAsterisk());
            if (!n.isStatic()) {
                this.imports.put(n.getName().getName(), n.getName().toString());
            } else {
                depth++;
                super.visit(n, arg);
                depth--;
            }
            return null;
        }

        @Override
        public Analysis visit(InitializerDeclaration n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(InstanceOfExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(IntegerLiteralExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(IntegerLiteralMinValueExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(JavadocComment n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(LabeledStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(LineComment n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(LongLiteralExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(LongLiteralMinValueExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(MarkerAnnotationExpr n, Void arg) {
            print(n, n.getName());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(MemberValuePair n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(MethodCallExpr n, Void arg) {
            print(n, n.getName());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(MethodDeclaration n, Void arg) {
            print(n, n.getName() + ":" + n.getType());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(NameExpr n, Void arg) {
            print(n, n.getName());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(NormalAnnotationExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(NullLiteralExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ObjectCreationExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(PackageDeclaration n, Void arg) {
            print(n, n.getName() + "/" + n.getAnnotations());
            this.packageName = n.getName().toString();
            return null;
        }

        @Override
        public Analysis visit(Parameter n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(MultiTypeParameter n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(PrimitiveType n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(QualifiedNameExpr n, Void arg) {
            print(n, n.getQualifier() + "/" + n.getName());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(ReferenceType n, Void arg) {
            print(n, n.getType());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(ReturnStmt n, Void arg) {
            print(n, n.getExpr());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(SingleMemberAnnotationExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(StringLiteralExpr n, Void arg) {
            print(n, n.getValue());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(SuperExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(SwitchEntryStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(SwitchStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(SynchronizedStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ThisExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(ThrowStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(TryStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(TypeDeclarationStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(TypeParameter n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(UnaryExpr n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(VariableDeclarationExpr n, Void arg) {
            print(n, n.getVars() + ":" + n.getType());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(VariableDeclarator n, Void arg) {
            print(n, n.getId() + "/" + n.getInit());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(VariableDeclaratorId n, Void arg) {
            print(n, n.getName() + "/" + n.getArrayCount());
            depth++;
            super.visit(n, arg);
            depth--;
            return null;
        }

        @Override
        public Analysis visit(VoidType n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(WhileStmt n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(WildcardType n, Void arg) {
            print(n, null);
            return null;
        }

        @Override
        public Analysis visit(AssignExpr n, Void arg) {
            print(n, n.getValue());
            return null;
        }

        private String buildTypeName() {
            StringBuilder buffy = new StringBuilder(this.packageName);
            boolean rootType = true;
            for (String typeName : this.typeNames) {
                if (rootType) {
                    buffy.append(".");
                    rootType = false;
                } else {
                    buffy.append("$");
                }
                buffy.append(typeName);
            }

            return buffy.toString();
        }

        private void resolveInnerTypeReferences() {
            Iterator<Map.Entry<String, String>> namedReferences = this.referenceToInnerOrPackageType.entrySet().iterator();
            while (namedReferences.hasNext()) {
                Map.Entry<String, String> referenceToInnerOrPackageType = namedReferences.next();
                if (this.innerTypes.contains(referenceToInnerOrPackageType.getValue())) {
                    codeContext.addDependencies(referenceToInnerOrPackageType.getKey(),
                            this.packageName + "." + this.typeName + "$" + referenceToInnerOrPackageType.getValue());
                    namedReferences.remove();
                }
            }
        }

        private void print(Node node, Object content) {
            StringBuilder buffy = new StringBuilder(16);
            for (int blanks = depth * 2; blanks-- > 0; ) {
                buffy.append(" ");
            }
            System.out.println(buffy + node.getClass().getSimpleName() + " [" + content + "]@"
                    + node.getBeginLine() + "." + node.getBeginColumn() + ": " + node.getData());
        }

    }

    private static class Analysis {

        public final Map<String, String> referencesToPackageType;

        public Analysis(String packageName, Map<String, String> referencesToNamedType) {
            this.referencesToPackageType = newHashMap();
            for (Map.Entry<String, String> referenceToPackageType : referencesToNamedType.entrySet()) {
                this.referencesToPackageType.put(referenceToPackageType.getKey(),
                        packageName + "." + referenceToPackageType.getValue());
            }
        }

        public boolean needsPostProcessing() {
            return !this.referencesToPackageType.isEmpty();
        }

    }

}
