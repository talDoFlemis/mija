package org.example.parser;

import java.io.InputStream;
import java.util.Optional;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.ast.Program;
import org.example.javacc.Parser;
import org.example.ast.Type;


@NoArgsConstructor
@Log4j2
public class JavaCCParser implements ParserStrategy {
    private final Parser parser = new Parser(InputStream.nullInputStream());

    public Optional<Program> getProgram(InputStream stream) {
        return Optional.empty();
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
}
