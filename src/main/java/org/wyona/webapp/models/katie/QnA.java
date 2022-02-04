package org.wyona.webapp.models.katie;

/**
 *
 */
public class QnA {

    private String domainId;

    /**
     *
     */
    public QnA() {
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
