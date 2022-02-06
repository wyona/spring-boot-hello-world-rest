package org.wyona.webapp.models.katie;

/**
 *
 */
public class Sentence {

    private String text;
    private String domainId;

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

    /**
     *
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     *
     */
    public String getDomainId() {
        return domainId;
    }
}
