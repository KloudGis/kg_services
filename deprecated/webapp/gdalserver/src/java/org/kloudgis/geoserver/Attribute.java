/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

public class Attribute {
    
    public String strName;
    public String strClass;
    public boolean bNillable;
    
    public String toString() {
        return strName + " - " + strClass;
    }
} 