package org.example;

import org.example.parser.Example;
import org.example.parser.ParseException;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) throws ParseException {
        var kk = new Example();
        System.out.println(new App().getGreeting());
    }
}
