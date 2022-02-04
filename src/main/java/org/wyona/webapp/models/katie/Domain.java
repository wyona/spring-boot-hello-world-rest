package org.wyona.webapp.models.katie;

/**
 *
 */
public class Domain {

    private String id;
    private String name;

    /**
     *
     */
    public Domain() {
    }

    /**
     * @param name Katie domain name, e.g. "Wyona FAQ"
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Katie domain name, e.g. "Wyona FAQ"
     */
    public String getName() {
        return name;
    }

    /**
     * @param id Katie domain Id, e.g. "01b40185-ea12-4ae0-8df7-b335ad0d8817"
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Katie domain Id, e.g. "01b40185-ea12-4ae0-8df7-b335ad0d8817"
     */
    public String getId() {
        return id;
    }
}
