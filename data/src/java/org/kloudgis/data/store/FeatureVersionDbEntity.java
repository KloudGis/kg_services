/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.kloudgis.data.pojo.Feature;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "features_version")
public class FeatureVersionDbEntity extends AbstractFeatureDbEntity{
    @Column
    private String      version_event;
    @Column 
    private Long        version_user;
    @Column
    private Timestamp   version_date;
    
    @Column 
    private Integer     version_number;

    /**
     * @return the version_event
     */
    public String getVersionEvent() {
        return version_event;
    }

    /**
     * @param version_event the version_event to set
     */
    public void setVersionEvent(String version_event) {
        this.version_event = version_event;
    }

    /**
     * @return the version_user
     */
    public Long getVersionUser() {
        return version_user;
    }

    /**
     * @param version_user the version_user to set
     */
    public void setVersionUser(Long version_user) {
        this.version_user = version_user;
    }

    /**
     * @return the version_date
     */
    public Timestamp getVersionDate() {
        return version_date;
    }

    /**
     * @param version_date the version_date to set
     */
    public void setVersionDate(Timestamp version_date) {
        this.version_date = version_date;
    }

    /**
     * @return the version_number
     */
    public Integer getVersionNumber() {
        return version_number;
    }

    /**
     * @param version_number the version_number to set
     */
    public void setVersionNumber(Integer version_number) {
        this.version_number = version_number;
    }
    
    
    
}
