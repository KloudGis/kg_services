/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

/**
 *
 * @author jeanfelixg
 */
public class LoadLayer extends Layer{
    
    public String  sld;
    public Long  ft_id;
    public String  filter;
    public Boolean  canGroup;
    
    public Double minX, minY,maxX,maxY;
}
