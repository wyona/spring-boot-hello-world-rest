package org.wyona.webapp.models.katie;

/**
 *
 */
public class QnA {

    private String uuid;
    private String question;
    private String answer;

    /**
     *
     */
    public QnA() {
    }

    /**
     *
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     *
     */
    public String getAnswer() {
        return answer;
    }

    /**
     *
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     *
     */
    public String getQuestion() {
        return question;
    }

    /**
     *
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     *
     */
    public String getUuid() {
        return uuid;
    }
}
