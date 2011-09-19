/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.sql.Timestamp;
import org.kloudgis.pojo.Coordinate;

/**
 *
 * @author jeanfelixg
 */
public class Note{

    public Long     guid;
    public String   title;   
    public String   description;
    public Long     author;
    public Coordinate coordinate;
    public Timestamp     date;
    
}
