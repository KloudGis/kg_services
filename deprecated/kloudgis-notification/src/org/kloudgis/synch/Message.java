/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.synch;

/**
 *
 * @author jeanfelixg
 */
public class Message {
    
    public Message(){}
    
    public Message(String content, String type){
        this.content = content;
        this.type = type;
    }
    
    public String content;
    
    public String type;
}
