
package org.kloudgis.core.pojo.feature;

import javax.persistence.EntityManager;
import org.kloudgis.core.persistence.feature.FeatureDbEntity;

/**
 *
 * @author jeanfelixg
 */

public abstract class Feature {

    public Long guid;
    public String boundswkt;
    public String newgeowkt;

    public Feature(){}

    public void setFeatureId(Long id){
        this.guid = id;
    }

    public abstract FeatureDbEntity toDbEntity(EntityManager em);

}
