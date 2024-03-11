package org.example.parser;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.example.antlr.MiniJavaLexer;
import org.example.antlr.MiniJavaParser;

import java.io.InputStream;
import java.util.Optional;

@Log4j2
@NoArgsConstructor
public class AntlrParser implements ParserStrategy {
    public Optional<Void> getProgram(InputStream stream) {
        return Optional.empty();
    }

    public boolean isSyntaxOk(InputStream stream) {
        log.info("Checking syntax");
        boolean status = true;

        try {
            var charStream = CharStreams.fromStream(stream);
            var lexer = new MiniJavaLexer(charStream);
            lexer.removeErrorListeners();
            lexer.addErrorListener(AntlrParserExceptionListener.INSTANCE);

            var tokens = new CommonTokenStream(lexer);

            var parser = new MiniJavaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(AntlrParserExceptionListener.INSTANCE);

            parser.program();
        } catch (ParseCancellationException e) {
            log.error("Syntax error", e);
            status = false;
        } catch (Exception e) {
            log.error("Error", e);
            status = false;
        }

        return status;
    }
}
