/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

/**
 *
 * @author jeanfelixg
 */
public class SearchCategory {
    
    public String guid;
    public String categoryLabel;
    public String search;
    public Integer count;
    
    public SearchCategory(){}
    
    public SearchCategory(String id, String catLabel, String search, Integer count){
        this.guid = id;
        this.categoryLabel = catLabel;
        this.search = search;
        this.count = count;
    }
}
