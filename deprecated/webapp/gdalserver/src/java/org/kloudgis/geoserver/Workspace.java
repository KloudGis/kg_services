/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

public class Workspace {
    
    public String strName;
    
    public Workspace( String strName ) {
        this.strName = strName;
    }
    
    @Override
    public String toString() {
        return strName;
    }
}