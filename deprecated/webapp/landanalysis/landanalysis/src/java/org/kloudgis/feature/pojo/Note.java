/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.feature.pojo;

import javax.persistence.EntityManager;
import org.kloudgis.core.persistence.feature.FeatureDbEntity;
import org.kloudgis.core.pojo.feature.Feature;
import org.kloudgis.persistence.NoteDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class Note extends Feature{

    public String title;
    public String description;

    @Override
    public FeatureDbEntity toDbEntity(EntityManager em) {
        NoteDbEntity entity = new NoteDbEntity();
        entity.setId(guid);
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setGeoFromWKT(newgeowkt);
        return entity;
    }
}
