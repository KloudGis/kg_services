/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core.persistence.feature;

import com.vividsolutions.jts.geom.Geometry;
import org.kloudgis.core.pojo.feature.Feature;

/**
 *
 * @author jeanfelixg
 */
public abstract class FeatureDbEntity implements Comparable{

    @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb.getClass().equals(this.getClass()))) {
            return false;
        }
        FeatureDbEntity other = (FeatureDbEntity) otherOb;
        return ((getId() == null ? other.getId() == null : getId().equals(other.getId())));
    }

    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().hashCode();
    }

    public abstract String getFeatureTypeName();

    public abstract Feature toPojo();

    public abstract void fromPojo(Feature pojo);

    public abstract Long getId();

    public abstract void setId(Long id);

    @Override
    public int compareTo(Object o){
        if(o instanceof FeatureDbEntity){
           Long id = getId();
           Long oId = ((FeatureDbEntity)o).getId();
           if(id == null && oId == null){
               return 0;
           }else if(id != null && oId != null){
               return id.compareTo(oId);
           }
        }
        return 0;
    }

    public abstract Geometry getGeometry();
}
