

package org.kloudgis.core.pojo.feature;

import java.util.List;

/**
 *
 * @author jeanfelixg
 */
public class FeatureType {

    public Integer guid;
    public String name;
    public String class_name;
    public String table_name;
    public String label;
    public List<Long> attrtypes;
    public Boolean hasGeometry;
    public Boolean searchable;
    public Boolean selectionable;
    public Boolean hasChart;
    public String idAttribute;
    public String prioritySelectionAttr;
      

}
