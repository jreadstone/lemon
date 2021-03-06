package com.mossle.auth.domain;

// Generated by Hibernate Tools
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Access .
 * 
 * @author Lingo
 */
@Entity
@Table(name = "AUTH_ACCESS")
public class Access implements java.io.Serializable {
    private static final long serialVersionUID = 0L;

    /** null. */
    private Long id;

    /** null. */
    private Perm perm;

    /** null. */
    private String type;

    /** null. */
    private String value;

    /** null. */
    private Integer priority;

    /** null. */
    private String descn;

    /** null. */
    private Long globalId;

    /** null. */
    private Long localId;

    public Access() {
    }

    public Access(Perm perm, String type, String value, Integer priority,
            String descn, Long globalId, Long localId) {
        this.perm = perm;
        this.type = type;
        this.value = value;
        this.priority = priority;
        this.descn = descn;
        this.globalId = globalId;
        this.localId = localId;
    }

    /** @return null. */
    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    /**
     * @param id
     *            null.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return null. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERM_ID")
    public Perm getPerm() {
        return this.perm;
    }

    /**
     * @param perm
     *            null.
     */
    public void setPerm(Perm perm) {
        this.perm = perm;
    }

    /** @return null. */
    @Column(name = "TYPE", length = 50)
    public String getType() {
        return this.type;
    }

    /**
     * @param type
     *            null.
     */
    public void setType(String type) {
        this.type = type;
    }

    /** @return null. */
    @Column(name = "VALUE", length = 200)
    public String getValue() {
        return this.value;
    }

    /**
     * @param value
     *            null.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /** @return null. */
    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return this.priority;
    }

    /**
     * @param priority
     *            null.
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /** @return null. */
    @Column(name = "DESCN", length = 200)
    public String getDescn() {
        return this.descn;
    }

    /**
     * @param descn
     *            null.
     */
    public void setDescn(String descn) {
        this.descn = descn;
    }

    /** @return null. */
    @Column(name = "GLOBAL_ID")
    public Long getGlobalId() {
        return this.globalId;
    }

    /**
     * @param globalId
     *            null.
     */
    public void setGlobalId(Long globalId) {
        this.globalId = globalId;
    }

    /** @return null. */
    @Column(name = "LOCAL_ID")
    public Long getLocalId() {
        return this.localId;
    }

    /**
     * @param localId
     *            null.
     */
    public void setLocalId(Long localId) {
        this.localId = localId;
    }
}
