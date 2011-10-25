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
    
    public Long guid;
    public String category;
    public String categoryLabel;
    public String search;
    public Integer count;
    
    public SearchCategory(){}
    
    public SearchCategory(long id, String cat, String catLabel, String search, Integer count){
        this.guid = id;
        this.category = cat;
        this.categoryLabel = catLabel;
        this.search = search;
        this.count = count;
    }
}
