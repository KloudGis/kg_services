/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeanfelixg
 */
public class Cluster {
    
    public Long guid;
    public Double lat;
    public Double lon;
    
    public List features = new ArrayList();
}
