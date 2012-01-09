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
public class Featuretype {
    public Long guid;
    public String label;
    public String title_attribute;
    
    public List<Long> attrtypes;
    public String geometry_type;
}
