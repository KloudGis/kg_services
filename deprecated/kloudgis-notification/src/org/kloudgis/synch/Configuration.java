/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.synch;

/**
 *
 * @author jeanfelixg
 */
public class Configuration {
    
    public Double version;
    public String kloudgis_server;
    
    public String getServerURL(){
        return kloudgis_server;
    }
    
    @Override
    public String toString(){
        return "v=" + version + ", server=" + kloudgis_server;
    }
}
