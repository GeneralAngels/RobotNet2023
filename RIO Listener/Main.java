package org.example;

public class Main {
    public static void main(String[] args) {
        Listener l = new Listener();
        System.out.println("constructed");
        l.run();
    }
}