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
public class NoteCommentMessage extends Message{
    public static String ADD = "add";
    public static String DELETE = "delete";
    public static String UPDATE = "update";
    
    
    public NoteCommentMessage(){}
    
    public NoteCommentMessage(Long id, Long noteId, String mType, String author){
        Map map = new HashMap();
        map.put("id", id);
        map.put("note_id", noteId);
        map.put("modif_type", mType);
        this.content = map;
        this.type = "note_comment";
        this.author = author;
        this.dateMillis = Calendar.getInstance().getTimeInMillis();
    }
}
