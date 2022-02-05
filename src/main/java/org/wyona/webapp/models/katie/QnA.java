package org.wyona.webapp.models.katie;

/**
 *
 */
public class QnA {

    private String domainId;
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

    /**
     * @param id Katie domain Id, e.g. "01b40185-ea12-4ae0-8df7-b335ad0d8817"
     */
    public void setDomainId(String id) {
        this.domainId = id;
    }

    /**
     * @return Katie domain Id, e.g. "01b40185-ea12-4ae0-8df7-b335ad0d8817"
     */
    public String getDomainId() {
        return domainId;
    }
}
