/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data;

import java.sql.Timestamp;
import java.util.Calendar;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.kloudgis.data.pojo.Feature;
import org.kloudgis.data.store.FeatureVersionDbEntity;
import org.kloudgis.data.store.MemberDbEntity;

/**
 *
 * @author jeanfelixg
 */
public class VersionsFactory {
    
    public static void addVersion(VersionEvent event, Feature featureBeforeUpdate, HibernateEntityManager em, MemberDbEntity member){
         em.getTransaction().begin();
         FeatureVersionDbEntity entity = new FeatureVersionDbEntity();
         entity.fromPojo(featureBeforeUpdate);
         entity.setVersionDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
         entity.setVersionUser(member.getUserId());
         entity.setVersionEvent(event.toString());        
         Object oMax = em.getSession().createCriteria(FeatureVersionDbEntity.class).setProjection(Projections.max("version_number")).uniqueResult();
         if(oMax == null){
             oMax = 1;
         }
         int iMax = ((Number)oMax).intValue();
         entity.setVersionNumber(iMax + 1);
         em.persist(entity);
         em.getTransaction().commit();
    }
}
