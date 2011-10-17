/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.model;

/**
 *
 * @author jeanfelixg
 */
public class LayerFilter {
    
    public String operator;
    //if value predicate
    public String attribute;
    public String value;
    //if composed predicate
    public LayerFilter leftFilter;
    public LayerFilter rightFilter;
 
}
