/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.persistence.feature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Table;
import org.kloudgis.core.pojo.feature.FeatureType;
import org.kloudgis.core.persistence.security.GroupDbEntity;

/**
 *
 * @author jeanfelixg
 */
@Entity
@Table(name = "FEATURETYPES")
@NamedQueries({
@NamedQuery(name = "FeatureType.findAll", query = "SELECT c FROM FeatureTypeDbEntity c order by c.label")})
public class FeatureTypeDbEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String name;
    @Column
    private String class_name;
    @Column
    private String table_name;
    @Column
    private String label;
    @Column
    private Boolean hasGeometry;
    @Column
    private Boolean searchable;
    @Column
    private Boolean selectionable;
    @Column
    private Boolean hasChart;
    @Column
    private String idAttribute;
    @Column
    private String prioritySelectionAttribute;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setTableName(String table) {
        this.table_name = table;
    }

    public void setIdAttr(String attr){
        this.idAttribute = attr;
    }

    public void setHasGeometry(Boolean bHas){
        this.hasGeometry = bHas;
    }

    public void setSearchable(Boolean search){
        this.searchable = search;
    }

    public void setSelectionable(Boolean select){
        this.selectionable = select;
    }

    public void setHasChart(Boolean bHas){
        this.hasChart = bHas;
    }

    public void setPrioritySelectionAttribute(String attr){
        this.prioritySelectionAttribute = attr;
    }

    public String getPrioritySelectionAttribute(){
        return prioritySelectionAttribute;
    }

    public List<AttrTypeDbEntity> getAttrTypes(GroupDbEntity group, EntityManager em){
        List<GroupDbEntity> grpInheritance = group.getGroupInheritance();
        LinkedHashMap<String, AttrTypeDbEntity> mapColumnsFound = new LinkedHashMap();
        for(int i=grpInheritance.size()-1; i >= 0; i--){
            GroupDbEntity grp = grpInheritance.get(i);
            Query query = em.createQuery("SELECT c FROM AttrTypeDbEntity c WHERE (c.group_id = :group OR c.group_id is null ) AND c.featuretype_id = :ft");
            query = query.setParameter("group", grp.getId()).setParameter("ft", this.getId());
            List<AttrTypeDbEntity> listColumns = query.getResultList();
            for(AttrTypeDbEntity col : listColumns){
                mapColumnsFound.put(col.getName(), col);
            }
        }
        ArrayList<AttrTypeDbEntity> arrlCols = new ArrayList(mapColumnsFound.values());
        Collections.sort(arrlCols, new Comparator<AttrTypeDbEntity>(){

            public int compare(AttrTypeDbEntity o1, AttrTypeDbEntity o2) {
                Integer i1 = o1.getOrder();
                Integer i2 = o2.getOrder();
                if(i1 == null){
                    i1 = 0;
                }
                if(i2 == null){
                    i2 = 0;
                }
                return i1.compareTo(i2);
            }

        });
        return arrlCols;
    }

    public void addAttrType(AttrTypeDbEntity at) {
        at.setFeatureType(this);
    }

    @Override
    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof FeatureTypeDbEntity)) {
            return false;
        }
        FeatureTypeDbEntity other = (FeatureTypeDbEntity) otherOb;
        return ((id == null ? other.id == null : id.equals(other.id)));
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public FeatureType toPojo(GroupDbEntity group, EntityManager em){
        FeatureType pojo = new FeatureType();
        pojo.guid = getId();
        pojo.name = getName();
        pojo.class_name = class_name;
        pojo.table_name = table_name;
        pojo.label = getLabel();
        pojo.hasGeometry = hasGeometry;
        pojo.searchable = searchable;
        pojo.selectionable = selectionable;
        pojo.hasChart = hasChart;
        pojo.idAttribute = idAttribute;
        pojo.prioritySelectionAttr = prioritySelectionAttribute;
        List<AttrTypeDbEntity> lstAttrs = getAttrTypes(group, em);
        List<Long> lstAtIds = new ArrayList();
        for(AttrTypeDbEntity at : lstAttrs){
            lstAtIds.add(at.getId());
        }
        pojo.attrtypes = lstAtIds;
        return pojo;
    }

    public void setClassName(String classN) {
        this.class_name = classN;
    }

}
