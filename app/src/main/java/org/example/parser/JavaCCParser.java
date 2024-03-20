package org.example.parser;

import java.io.InputStream;
import java.util.Optional;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.ast.Program;
import org.example.javacc.Parser;

import static org.example.javacc.Parser.*;

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
            ReInit(stream);
            Program();
        } catch (Exception e) {
            log.error("Syntax error", e);
            isOk = false;
        }
        return isOk;
    }
}
