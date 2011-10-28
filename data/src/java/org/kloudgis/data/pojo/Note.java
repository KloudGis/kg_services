/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.List;
import org.kloudgis.core.pojo.Coordinate;

/**
 *
 * @author jeanfelixg
 */
public class Note{

    public Long     guid;
    public String   title;   
    public String   description;
    public Long     author;
    public String   author_descriptor;
    public Coordinate coordinate;
    public Long     date;
    public List<Long> comments;
    
}
