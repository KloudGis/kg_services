/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core.persistence.feature;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.kloudgis.core.pojo.feature.Priority;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "PRIORITIES")
public class PriorityDbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String attr_value;
    @Column
    private Integer priority;
    @ManyToOne
    private AttrTypeDbEntity attribute;

    public Long getId() {
        return id;
    }

    public void setValue(String val){
        this.attr_value = val;
    }

    public void setPriority(Integer iPrio){
        priority = iPrio;
    }

   public Priority toPojo() {
        Priority pojo = new Priority();
        pojo.guid = id;
        pojo.attr_value = attr_value;
        pojo.priority = priority;
        pojo.attribute = attribute == null ? null : attribute.getId();
        return pojo;
    }

    public void setAttribute(AttrTypeDbEntity at) {
        attribute = at;
    }

}
