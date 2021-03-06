package com.mossle.auth.domain;

// Generated by Hibernate Tools
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Resc .
 * 
 * @author Lingo
 */
@Entity
@Table(name = "AUTH_RESC")
public class Resc implements java.io.Serializable {
    private static final long serialVersionUID = 0L;

    /** null. */
    private Long id;

    /** null. */
    private String name;

    /** null. */
    private String descn;

    /** null. */
    private Long globalId;

    /** null. */
    private Long localId;

    /** . */
    private Set<Perm> perms = new HashSet<Perm>(0);

    public Resc() {
    }

    public Resc(String name, String descn, Long globalId, Long localId,
            Set<Perm> perms) {
        this.name = name;
        this.descn = descn;
        this.globalId = globalId;
        this.localId = localId;
        this.perms = perms;
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
    @Column(name = "NAME", length = 50)
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            null.
     */
    public void setName(String name) {
        this.name = name;
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

    /** @return . */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resc")
    public Set<Perm> getPerms() {
        return this.perms;
    }

    /**
     * @param perms
     *            .
     */
    public void setPerms(Set<Perm> perms) {
        this.perms = perms;
    }
}
