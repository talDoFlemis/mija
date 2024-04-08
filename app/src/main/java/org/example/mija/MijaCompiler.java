package org.example.mija;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.example.ast.Program;
import org.example.parser.JavaCCParser;
import org.example.visitor.mermaid.MermaidASTPrinterVisitor;
import org.example.visitor.types.TypeCheckingVisitor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

@AllArgsConstructor
@Builder
@Log4j2
public class MijaCompiler {
    @Builder.Default
    private ParserStrategy parser = new JavaCCParser();
    @Builder.Default
    private SemanticAnalysisStrategy semanticAnalysis = new TypeCheckingVisitor();

    public boolean isLexicalAndSyntacticallyOk(InputStream stream) {
        log.debug("Checking lexical and syntactic analysis");
        if (parser.isSyntaxOk(stream)) {
            log.info("Lexical and syntactic analysis is correct");
            return true;
        } else {
            log.error("Lexical and syntactic analysis is incorrect");
            return false;
        }
    }

    public Program getAbstractSyntaxTreeFromStream(InputStream stream) throws LexicalOrSemanticAnalysisException {
        log.debug("Getting abstract syntax tree from stream");

        try {
            Program program = parser.getProgramOrThrow(stream);
            log.info("Program is lexically and syntactically correct");
            return program;
        } catch (LexicalOrSemanticAnalysisException e) {
            log.error("Program is not lexically or semantically ok");
            throw e;
        }
    }

    public boolean isSemanticallyOk(Program program) {
        log.debug("Checking semantics");
        var ok =  semanticAnalysis.isSemanticsOk(program);

        if (ok) {
            log.info("Semantics is correct");
        } else {
            log.error("Semantics is incorrect");
        }

        return ok;
    }

    public void isSemanticallyOkOrThrow(Program program) throws SemanticAnalysisException {
        log.debug("Checking semantics");
        try {
            semanticAnalysis.isSemanticsOkOrThrow(program);
            log.info("Semantics is correct");
        } catch (SemanticAnalysisException e) {
            log.error("Semantics is incorrect");
            throw e;
        }
    }

    public void compile(InputStream inputStream, OutputStream outputStream) throws LexicalOrSemanticAnalysisException, SemanticAnalysisException {
        // Check lexical and syntactic analysis
        Program program = getAbstractSyntaxTreeFromStream(inputStream);
        isSemanticallyOkOrThrow(program);

        // Intermediate code generation

    }

    public void mermaid(InputStream inputStream, OutputStream outputStream) throws LexicalOrSemanticAnalysisException, SemanticAnalysisException {
        Program program = getAbstractSyntaxTreeFromStream(inputStream);
        isSemanticallyOkOrThrow(program);

        log.info("Generating mermaid file");
        try {
            MermaidASTPrinterVisitor visitor =
                    new MermaidASTPrinterVisitor("app/src/main/java/org/example/visitor/mermaid/teste.md");
            program.accept(visitor);
        } catch (Exception e) {
            log.error("Error while generating mermaid file");
            throw e;
        }
    }
}
