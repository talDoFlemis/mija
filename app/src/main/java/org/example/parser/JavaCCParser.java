package org.example.parser;

import java.io.InputStream;
import java.util.Optional;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.ast.*;
import org.example.javacc.Parser;


@NoArgsConstructor
@Log4j2
public class JavaCCParser implements ParserStrategy {
    private final Parser parser = new Parser(InputStream.nullInputStream());

    public Optional<Program> getProgram(InputStream stream) {
        log.info("Parsing program");

        try {
            parser.ReInit(stream);
            return Optional.of(parser.Program());
        } catch (Exception e) {
            log.error("Error parsing program", e);
            return Optional.empty();
        }
    }

    public boolean isSyntaxOk(InputStream stream) {
        log.info("Checking syntax");

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
