/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.feature.pojo;

import javax.persistence.EntityManager;
import org.kloudgis.core.persistence.feature.FeatureDbEntity;
import org.kloudgis.core.pojo.feature.Feature;
import org.kloudgis.persistence.ArpenteurDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class Arpenteur extends Feature{

    public String firstname;
    public String lastname;
    public String postalcode;

    @Override
    public FeatureDbEntity toDbEntity(EntityManager em) {
        ArpenteurDbEntity entity = new ArpenteurDbEntity();
        entity.setId(guid);
        entity.setFirstName(firstname);
        entity.setLastName(lastname);
        entity.setPostalCode(postalcode);
        entity.setGeoFromWKT(newgeowkt);
        return entity;
    }
}

