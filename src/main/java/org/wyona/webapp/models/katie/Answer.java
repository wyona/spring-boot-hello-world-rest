package org.wyona.webapp.models.katie;

/**
 *
 */
public class Answer {

    private String uuid;
    private String answer;

    /**
     *
     */
    public Answer() {
    }

    /**
     * @param uuid UUID of answer, e.g. "01b40185-ea12-4ae0-8df7-b335ad0d8817"
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return UUID of answer, e.g. "01b40185-ea12-4ae0-8df7-b335ad0d8817"
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param answer Answer
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return answer, e.g. "<p>My answer to your question ...</p>"
     */
    public String getAnswer() {
        return answer;
    }
}
