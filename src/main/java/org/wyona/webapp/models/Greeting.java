package org.wyona.webapp.models;

/**
 *
 */
public class Greeting {

    private String name;

    /**
     *
     */
    public Greeting(String name) {
        this.name  = name;
    }

    /**
     *
     */
    public String getGreeting() {
        return "Hello " + name;
    }
}
