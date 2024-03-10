package org.example;


import lombok.extern.log4j.Log4j2;
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
                        System.out.println(!7);
                    }
                }
                """;
        InputStream stream = new ByteArrayInputStream(s.getBytes());
        JavaCCParser javaCCParser = new JavaCCParser();

        log.info("Syntax is ok for JavaCC: {}", javaCCParser.isSyntaxOk(stream) ? "Yes" : "No");
    }
}
