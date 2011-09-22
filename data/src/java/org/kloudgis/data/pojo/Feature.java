/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;

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
    public List<Attribute>  attrs;
    
}
