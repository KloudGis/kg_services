/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

/**
 *
 * @author jeanfelixg
 */
public class Attribute {
    public String key;
    public String value;
    
    public Attribute(){}
    
    public Attribute(String key, String value){
        this.key = key;
        this.value = value;
    }
}