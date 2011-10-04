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
    
    public String category;
    public String categoryLabel;
    public String search;
    public Integer count;
    
    public SearchCategory(){}
    
    public SearchCategory(String cat, String catLabel, String search, Integer count){
        this.category = cat;
        this.categoryLabel = catLabel;
        this.search = search;
        this.count = count;
    }
}
