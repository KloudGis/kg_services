/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.kloudgis.data.pojo.Feature;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "features")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Indexed
public class FeatureDbEntity extends AbstractFeatureDbEntity {

    //private id - Do not expose it.
    @SequenceGenerator(name = "feature_seq_gen", sequenceName = "feature_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "feature_seq_gen")
    @DocumentId
    private Long system_id;
    //**********************************
    //  JOIN on itself
    //**********************************
    @ManyToMany
    @JoinTable(name = "joins",
    joinColumns = {
        @JoinColumn(name = "src_feature", referencedColumnName = "system_id")},
    inverseJoinColumns = {
        @JoinColumn(name = "dest_feature", referencedColumnName = "system_id")})
    private List<FeatureDbEntity> joins;
    //**********************************
    //  Reverse JOIN on itself
    //**********************************
    @ManyToMany
    @JoinTable(name = "joins",
    joinColumns = {
        @JoinColumn(name = "dest_feature", referencedColumnName = "system_id")},
    inverseJoinColumns = {
        @JoinColumn(name = "src_feature", referencedColumnName = "system_id")})
    private List<FeatureDbEntity> reverse_joins;
    //**********************************
    //  JOIN on feature Comments
    //**********************************
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL)
    @IndexedEmbedded
    private List<FeatureCommentDbEntity> comments;

    public String getGuid() {
        return buildGuid(fid, ft_id);
    }

    public void addJoin(FeatureDbEntity f2) {
        if (joins == null) {
            joins = new ArrayList();
        }
        this.joins.add(f2);
    }

    public List<FeatureDbEntity> getJoins() {
        return joins;
    }

    public List<FeatureDbEntity> getReserveJoins() {
        return reverse_joins;
    }

    public Feature toPojo() {
        Feature pojo = new Feature();
        pojo.guid = buildGuid(this.fid, this.ft_id);
        super.toPojo(pojo);
        //Joins
        List<FeatureDbEntity> lstJoins = this.getJoins();
        if (lstJoins != null && !lstJoins.isEmpty()) {
            List<String> lstGuid = new ArrayList(lstJoins.size());
            for (FeatureDbEntity fJoin : lstJoins) {
                lstGuid.add(buildGuid(fJoin.fid, fJoin.ft_id));
            }
            pojo.joins = lstGuid;
        }
        //reverse Joins
        lstJoins = this.getReserveJoins();
        if (lstJoins != null && !lstJoins.isEmpty()) {
            List<String> lstGuid = new ArrayList(lstJoins.size());
            for (FeatureDbEntity fJoin : lstJoins) {
                lstGuid.add(buildGuid(fJoin.fid, fJoin.ft_id));
            }
            pojo.reverse_joins = lstGuid;
        }
        return pojo;
    }

    public void fromPojo(Feature pojo, HibernateEntityManager em) {
        super.fromPojo(pojo);
        //Joins
        List<String> lstNew = pojo.joins == null ? new ArrayList() : pojo.joins;
        List<FeatureDbEntity> lstJoinsActual = this.getJoins();
        Map<String, FeatureDbEntity> mapGuidActual = new HashMap();
        if (lstJoinsActual != null) {
            for (FeatureDbEntity fJoin : lstJoinsActual) {
                mapGuidActual.put(buildGuid(fJoin.fid, fJoin.ft_id), fJoin);
            }
        }
        //add new joins
        if (lstNew.size() > 0 && lstJoinsActual == null) {
            this.joins = new ArrayList();
            lstJoinsActual = getJoins();
        }
        for (String guid : lstNew) {
            if (!mapGuidActual.containsKey(guid)) {
                try {
                    FeatureDbEntity f = findByGuid(guid, em);
                    lstJoinsActual.add(f);
                } catch (EntityNotFoundException e) {
                    System.out.println("couldnt add " + guid + " as join because it not found!");
                }
            }
        }
        //remove joins not relevant
        for (String guid : mapGuidActual.keySet()) {
            if (!lstNew.contains(guid)) {
                lstJoinsActual.remove(mapGuidActual.get(guid));
            }
        }
    }

    public static FeatureDbEntity findByGuid(String guid, HibernateEntityManager em) {
        //add it
        String[] split = guid.split("_");
        if (split.length == 2) {
            List<FeatureDbEntity> lst = em.getSession().createCriteria(FeatureDbEntity.class).
                    add(Restrictions.eq("fid", Long.valueOf(split[0]))).
                    add(Restrictions.eq("ft_id", Long.valueOf(split[1]))).list();
            if (lst.isEmpty()) {
                return null;
            }
            return lst.get(0);
        }
        return null;
    }

    public static String buildGuid(Long fid, Long ft_id) {
        return fid + "_" + ft_id;
    }
}
