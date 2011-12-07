
package org.kloudgis.core.pojo;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author jeanfelixg
 */
public class Message {

    private static ObjectMapper mapper = new ObjectMapper();

    public Map    content;
    public String type;
    public String author;
    public Long   dateMillis;
    
    public Message() {
    }

    public Message(Map content, String type, String author) {
        this.content = content;
        this.type = type;
        this.author = author;
        this.dateMillis = Calendar.getInstance().getTimeInMillis();
    }
    
    public String toJSON() throws IOException{
        return mapper.writeValueAsString(this);
    }
    
    public static Message fromJSON(String json) throws IOException {
        return mapper.readValue(json, Message.class);
    }
    
    @Override
    public String toString(){
        try {
            return toJSON();
        } catch (IOException ex) {
            return "json error ???";
        }
    }
    
}
