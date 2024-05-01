package org.example;


import lombok.extern.log4j.Log4j2;
import org.example.mija.MijaCompiler;
import org.example.parser.AntlrParser;
import org.example.visitor.types.TypeCheckingVisitor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Log4j2
public class App {
    public static void main(String[] args) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("app/src/test/resources/programs/Factorial.java");

        MijaCompiler compiler = MijaCompiler.builder()
                .parser(new AntlrParser())
                .semanticAnalysis(new TypeCheckingVisitor())
                .build();

        compiler.compile(inputStream, System.out);
    }
}