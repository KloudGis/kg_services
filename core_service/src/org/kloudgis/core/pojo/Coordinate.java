/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo;

/**
 *
 * @author jeanfelixg
 */
public class Coordinate {

    public Double x,y;
    
    public Coordinate(){}
    
    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public com.vividsolutions.jts.geom.Coordinate toJTS(){
        return new com.vividsolutions.jts.geom.Coordinate(x, y);
    }
}
