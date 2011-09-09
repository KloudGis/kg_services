/*
 * @author corneliu
 */
package org.kloudgis.datasource;

import java.io.File;
import java.util.Set;

public class Datasource {
    
    public String strFileName;
    public String strGeomName;
    public int iGeomType;
    public int iCRS;
    public int iFeatureCount;
    public int iLayerCount;
    public int iColumnCount;
    public long lID;
    public long lFileSize;
    public long lLastModified;
    public double dEnvelopeMinX;
    public double dEnvelopeMinY;
    public double dEnvelopeMaxX;
    public double dEnvelopeMaxY;
    public File file;
    public Set<Long> setCols;
}