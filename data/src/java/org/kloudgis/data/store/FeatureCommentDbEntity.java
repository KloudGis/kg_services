/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.kloudgis.data.pojo.FeatureComment;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "feature_comments")
public class FeatureCommentDbEntity extends AbstractCommentDbEntity {
    
    @SequenceGenerator(name = "feature_comments_seq_gen", sequenceName = "feature_comments_seq")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="feature_comments_seq_gen")
    @DocumentId
    @Id
    private Long id; 
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne
    @ContainedIn
    private FeatureDbEntity feature;
    
    public FeatureComment toPojo(){
        FeatureComment pojo = new FeatureComment();
        super.toPojo(pojo);
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
