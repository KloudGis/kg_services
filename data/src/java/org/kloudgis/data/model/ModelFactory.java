/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.data.store.FeatureTypeDbEntity;
import org.kloudgis.data.store.LayerDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class ModelFactory {

    public static Map<Long, FeatureTypeDbEntity> getFeatureTypes(HibernateEntityManager em) {
        List<FeatureTypeDbEntity> arrFts = em.getSession().createCriteria(FeatureTypeDbEntity.class).list();
        Map<Long, FeatureTypeDbEntity> mapFt = new HashMap();
        for(FeatureTypeDbEntity ft : arrFts){
            mapFt.put(ft.getId(), ft);
        }
        return mapFt;
    }

    public static LayerDbEntity getMainLayer(HibernateEntityManager em) {
        List<LayerDbEntity> arrL = em.getSession().createCriteria(LayerDbEntity.class).add(Restrictions.eq("is_group", true)).list();
        if(arrL != null && arrL.size() > 0){
            return arrL.get(0);
        }
        return null;
    }
    
    
}
