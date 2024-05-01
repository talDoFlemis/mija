package org.example.parser;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.ast.*;
import org.example.javacc.Parser;
import org.example.mija.LexicalOrSemanticAnalysisException;
import org.example.mija.ParserStrategy;

import java.io.InputStream;
import java.util.Optional;


@NoArgsConstructor
@Log4j2
public class JavaCCParser implements ParserStrategy {
    private final Parser parser = new Parser(InputStream.nullInputStream());

    public Optional<Program> getProgram(InputStream stream) {
        log.debug("Parsing program");

        try {
            parser.ReInit(stream);
            return Optional.of(parser.Program());
        } catch (Exception e) {
            log.error("Error parsing program", e);
            return Optional.empty();
        }
    }

    public Program getProgramOrThrow(InputStream stream) throws LexicalOrSemanticAnalysisException {
        log.debug("Parsing program");

        parser.ReInit(stream);
        try {
            return parser.Program();
        } catch (Exception e) {
            throw new LexicalOrSemanticAnalysisException("Error parsing program", e);
        }
    }

    public boolean isSyntaxOk(InputStream stream) {
        log.debug("Checking syntax");

        boolean isOk = true;
        try {
            parser.ReInit(stream);
            parser.Program();
        } catch (Exception e) {
            log.error("Syntax error", e);
            isOk = false;
        }
        return isOk;
    }

    public Type getType(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.Type();
    }

    public Formal getFormal(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.Formal();
    }

    public FormalList getFormalList(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.FormalList();
    }

    public VarDecl getVarDecl(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.VarDecl();
    }

    public Expression getExpression(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.Expression();
    }

    public Statement getStatement(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.Statement();
    }

    public MethodDecl getMethodDecl(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.MethodDecl();
    }

    public ClassDecl getClassDecl(InputStream stream) throws org.example.javacc.ParseException {
        parser.ReInit(stream);
        return parser.ClassDecl();
    }
}
