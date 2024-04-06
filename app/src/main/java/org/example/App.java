package org.example;


import lombok.extern.log4j.Log4j2;
import org.example.ast.Program;
import org.example.parser.AntlrParser;
import org.example.parser.JavaCCParser;
import org.example.visitor.symbols.MainTable;
import org.example.visitor.symbols.SymbolTableVisitor;
import org.example.visitor.types.TypeCheckingVisitor;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

@Log4j2
public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = new FileInputStream("app/src/test/resources/programs/TreeVisitor.java");

        JavaCCParser javaCCParser = new JavaCCParser();
        Optional<Program> output = javaCCParser.getProgram(stream);

        if (output.isPresent()){
            Program program = output.get();

            SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
            program.accept(symbolTableVisitor);

            if (!symbolTableVisitor.getErrors().isEmpty()) {
                log.error("Errors: {}", symbolTableVisitor.getErrors());
                return;
            }

            MainTable mainTable = symbolTableVisitor.getMainTable();
            TypeCheckingVisitor typeCheckingVisitor = new TypeCheckingVisitor(mainTable);

            program.accept(typeCheckingVisitor);

            if (!typeCheckingVisitor.getErrors().isEmpty()) {
                log.error("Errors: {}", typeCheckingVisitor.getErrors());
                return;
            }

            log.info("Program is correct");
        }
    }
}
