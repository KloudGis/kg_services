/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.core.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jeanfelixg
 */
@XmlRootElement
public class ErrorMessage {
    
    private String message;

    private String type;

    public ErrorMessage() {
    }

    public ErrorMessage(String mess, String type) {
        this.message = mess;
        this.type = type;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    @XmlElement
    public String getType() {
        return type;
    }
}
