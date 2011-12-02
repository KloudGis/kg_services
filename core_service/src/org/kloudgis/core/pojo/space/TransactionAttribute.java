/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.space;

/**
 *
 * @author jeanfelixg
 */
public class TransactionAttribute {
    
    public String attribute;
    public String original_value;
    public String modified_value;
    
    public TransactionAttribute(){}
    
    public TransactionAttribute(String attr, String ori, String modif){
        this.attribute = attr;
        this.original_value = ori;
        this.modified_value = modif;
    }
    
    public String toString(){
        return "A=" + attribute + ", O=" + original_value + ", M=" + modified_value;
    }
}
