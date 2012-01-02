/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.List;

/**
 *
 * @author jeanfelixg
 */
public class Attrtype {
    
    public Long guid;
    public String label;
    public String type;
    public String attr_ref;
    public Long featuretype;
    public Integer width;
    public String css_class;
    public Integer render_order;
    public List<Catalog> enum_values;
    
}
