package org.example;


import lombok.extern.log4j.Log4j2;
import org.example.parser.AntlrParser;
import org.example.parser.JavaCCParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Log4j2
public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        String s = """
                    class Factorial{
                    public static void main(String[] a){
                        System.out.println(3<5 && false);
                        System.out.println(a + b * c + d * e - f);
                        System.out.println(3 + 4 * 5 && 3 * 1 + 4 * 5);
                        System.out.println(3 * (4 + 5));
                    }
                }
                """;
        InputStream stream = new ByteArrayInputStream(s.getBytes());

        JavaCCParser javaCCParser = new JavaCCParser();
        log.info("Syntax is ok for JavaCC: {}", javaCCParser.isSyntaxOk(stream) ? "Yes" : "No");

        stream = new ByteArrayInputStream(s.getBytes());
        AntlrParser antlrParser = new AntlrParser();
        log.info("Syntax is ok for Antlr: {}", antlrParser.isSyntaxOk(stream) ? "Yes" : "No");

        stream = new ByteArrayInputStream(s.getBytes());
        var testeAntlr = antlrParser.getProgram(stream);


        stream = new ByteArrayInputStream(s.getBytes());
        var testeJavaCC = javaCCParser.getProgram(stream);

        log.info("<<<<<<<<<<<<<<< ANTLR <<<<<<<<<<<<<<<");
        testeAntlr.ifPresent(log::info);
        log.info("<<<<<<<<<<<<<<< JAVACC <<<<<<<<<<<<<<<");
        testeJavaCC.ifPresent(log::info);


    }
}
