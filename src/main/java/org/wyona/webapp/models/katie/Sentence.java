package org.wyona.webapp.models.katie;

/**
 *
 */
public class Sentence {

    private String text;

    /**
     *
     */
    public Sentence() {
    }

    /**
     * @param text Text, e.g. "What time is it?"
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return text, e.g. "What time is it?"
     */
    public String getText() {
        return text;
    }
}
