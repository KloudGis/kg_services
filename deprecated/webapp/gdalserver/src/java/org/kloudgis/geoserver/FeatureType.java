/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.util.ArrayList;

public class FeatureType {
    
    public String strName;
    public String strDatastore;
    public String strWorkspace;
    public String strCRS;
    public String strClass;
    public double dNativeMinX;
    public double dNativeMinY;
    public double dNativeMaxX;
    public double dNativeMaxY;
    public double dLatlonMinX;
    public double dLatlonMinY;
    public double dLatlonMaxX;
    public double dLatlonMaxY;
    public String strProjectionPolicy;
    public boolean bEnabled;
    public ArrayList<Attribute> arlAttrs;
}