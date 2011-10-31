/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.Map;

/**
 *
 * @author jeanfelixg
 */
public class LoadFeature {
    
    public Long fid;   
    public String ft;
    public String geo_type;
    public String wkt;
    public Map<String, String>  attrs;
    public String title_attr;
    public Double angle;
    public Double angle_label;

}
