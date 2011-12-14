/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.io.Serializable;

/**
 *
 * @author jeanfelixg
 */
public abstract class AbstractComment implements Serializable{
    
    public Long     guid;
    public String   comment;   
    public Long     author;
    public String   author_descriptor;
    public Long     date_create;
}
