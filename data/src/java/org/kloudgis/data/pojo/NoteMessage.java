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
public class NoteMessage extends Message{
    public static String ADD = "add";
    public static String DELETE = "delete";
    public static String UPDATE = "update";
    
    
    public NoteMessage(){}
    
    public NoteMessage(Long id, String mType, String author){
        Map map = new HashMap();
        map.put("id", id);
        map.put("modif_type", mType);
        this.content = map;
        this.type = "note";
        this.author = author;
        this.dateMillis = Calendar.getInstance().getTimeInMillis();
    }
}
