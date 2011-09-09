/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.sql.Date;

/**
 *
 * @author jeanfelixg
 */
public class Note{

    public Long     guid;
    public String   title;   
    public String   description;
    public String   author;
    public Coordinate coordinate;
    public Date     date;
    
}
