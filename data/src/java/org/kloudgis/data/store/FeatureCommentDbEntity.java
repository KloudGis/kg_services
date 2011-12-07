/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.search.annotations.ContainedIn;
import org.kloudgis.data.pojo.FeatureComment;

@Entity
@Table(name = "feature_comments")
public class FeatureCommentDbEntity extends AbstractCommentDbEntity {
    @ManyToOne
    @ContainedIn
    private FeatureDbEntity feature;
    
    public FeatureComment toPojo(){
        FeatureComment pojo = new FeatureComment();
        
        pojo.feature = feature != null ? feature.getGuid() : null;
        return pojo;
    }


    public void setFeature(FeatureDbEntity fea) {
        this.feature = fea;
    }
    
    public FeatureDbEntity getFeature(){
        return this.feature;
    }

}
