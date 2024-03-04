package org.example;


import org.example.javacc.ParseException;
import org.example.javacc.Parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws ParseException {
        String s = """
                    class Factorial{
                    public static void main(String[] a){
                        System.out.println(!7);
                    }
                }
                """;
        InputStream stream = new ByteArrayInputStream(s.getBytes());
        new Parser(stream);
        Parser.Program();
    }
}
