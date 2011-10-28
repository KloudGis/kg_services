/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.List;
import java.util.Map;
import org.kloudgis.core.pojo.Coordinate;

/**
 *
 * @author jeanfelixg
 */
public class Feature {
    
    //guid is the concatenation of ft and fid :  Ex: fid is 123 and ft is toto; guid = toto_123
    public String guid;
    public Long fid;   
    public String ft;
    public Long date;
    public String geo_type;
    public List<Coordinate> coords;
    public Coordinate centroid;
    public Map<String, String>  attrs;
    public String title_attr;
    
    
    public Feature(){}

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature) obj;
        if ((this.guid == null) ? (other.guid != null) : !this.guid.equals(other.guid)) {
            return false;
        }
        return true;
    }
    
    
    
}
