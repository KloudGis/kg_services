/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.feature;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jeanfelixg
 */
@XmlRootElement
public class QueryResult {

    private List<Feature> lstFeatures;
    private Long count;

    public QueryResult() {
    }

    public QueryResult(List<Feature> lstF, Long count) {
        setFeatures(lstF);
        setCount(count);
    }

    /**
     * @return the lstFeatures
     */
    @XmlElement
    public List<Feature> getFeatures() {
        return lstFeatures;
    }

    /**
     * @param lstFeatures the lstFeatures to set
     */
    public final void setFeatures(List<Feature> lstFeatures) {
        this.lstFeatures = lstFeatures;
    }

    /**
     * @return the count
     */
    @XmlElement
    public Long getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public final void setCount(Long count) {
        this.count = count;
    }

}
