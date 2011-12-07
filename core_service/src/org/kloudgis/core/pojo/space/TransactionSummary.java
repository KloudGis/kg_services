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
    
    public Long ft_id;
    public Long feature_id;
    public int  trx_type;
    
    public TransactionSummary(){}
    
    public TransactionSummary(Long sFt, Long fid, int trx_t){
        this.ft_id = sFt;
        this.feature_id = fid;
        this.trx_type = trx_t;
    }

    public Map toMap() {
        Map map = new HashMap();
        map.put("ft_id", this.ft_id);
        map.put("feature_id", this.feature_id);
        map.put("trx_type", this.trx_type);
        return map;
    }
}
