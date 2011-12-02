/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo.space;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jeanfelixg
 */
public class TransactionSummary {
    
    public String featuretype;
    public Long feature_id;
    
    public TransactionSummary(){}
    
    public TransactionSummary(String sFt, Long fid){
        this.featuretype = sFt;
        this.feature_id = fid;
    }

    public Map toMap() {
        Map map = new HashMap();
        map.put("featuretype", this.featuretype);
        map.put("feature_id", this.feature_id);
        return map;
    }
}
