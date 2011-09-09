/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.kloudgis.core.pojo;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.kloudgis.core.persistence.feature.AttrTypeDbEntity;
import org.kloudgis.core.persistence.security.GroupDbEntity;
import org.kloudgis.core.persistence.security.PrivilegeDbEntity;
import org.kloudgis.core.persistence.security.UserDbEntity;
import org.kloudgis.core.persistence.security.UserRoleDbEntity;
import org.kloudgis.core.pojo.feature.AttrType;
import org.kloudgis.core.pojo.security.Group;
import org.kloudgis.core.pojo.security.Privilege;
import org.kloudgis.core.pojo.security.User;
import org.kloudgis.core.pojo.security.UserRole;

/**
 *
 * @author jeanfelixg
 */
public class PojoFactory {

    public static List<User> toUserEntities(List<UserDbEntity> members) {
        List<User> lstU = new ArrayList();
        if(members != null){
            for(UserDbEntity uDb : members){
                lstU.add(uDb.toPojo());
            }
        }
        return lstU;
    }

    public static List<UserDbEntity> toUserDbEntities(List<User> members, EntityManager em) {
        List<UserDbEntity> lstU = new ArrayList();
        if(members != null){
            for(User uDb : members){
                lstU.add(uDb.toDbEntity(em));
            }
        }
        return lstU;
    }

    public static List<Group> toGroupEntities(List<GroupDbEntity> groups) {
        List<Group> lstG = new ArrayList();
        if(groups != null){
            for(GroupDbEntity gDb : groups){
                System.out.println("Parent=" + gDb.getParentGroup());
                lstG.add(gDb.toPojo());
            }
        }
        return lstG;
    }


    public static List<Privilege> toPrivilegeEntities(List<PrivilegeDbEntity> privs) {
        List<Privilege> lstP = new ArrayList();
        if(privs != null){
            for(PrivilegeDbEntity pDb : privs){
                lstP.add(pDb.toPojo());
            }
        }
        return lstP;
    }

    public static List<PrivilegeDbEntity> toPrivilegeDbEntities(List<Privilege> privs, EntityManager em) {
        List<PrivilegeDbEntity> lstP = new ArrayList();
        if(privs != null){
            for(Privilege pDb : privs){
                lstP.add(pDb.toDbEntity(em));
            }
        }
        return lstP;
    }

    public static List<AttrType> toAttrType(List<AttrTypeDbEntity> lstC) {
        List<AttrType> listColumns = new ArrayList(lstC.size());
        for(AttrTypeDbEntity cDb : lstC){
            listColumns.add(cDb.toPojo());
        }
        return listColumns;
    }

    public static List<UserRole> toRoleEntities(List<UserRoleDbEntity> lstDb) {
        List<UserRole> lstP = new ArrayList();
        if(lstDb != null){
            for(UserRoleDbEntity pDb : lstDb){
                lstP.add(pDb.toPojo());
            }
        }
        return lstP;
    }
       
}
