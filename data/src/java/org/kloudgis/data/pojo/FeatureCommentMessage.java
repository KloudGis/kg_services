/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.kloudgis.core.pojo.Message;

/**
 *
 * @author jeanfelixg
 */
public class FeatureCommentMessage extends Message{
    public static String ADD = "add";
    public static String DELETE = "delete";
    public static String UPDATE = "update";
    
    
    public FeatureCommentMessage(){}
    
    public FeatureCommentMessage(Long id, String feature_id, String mType, String author){
        Map map = new HashMap();
        map.put("id", id);
        map.put("feature_id", feature_id);
        map.put("modif_type", mType);
        this.content = map;
        this.type = "feature_comment";
        this.author = author;
        this.dateMillis = Calendar.getInstance().getTimeInMillis();
    }
}
