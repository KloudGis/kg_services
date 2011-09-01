/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.kloudgis.admin.pojo.Credential;
import org.kloudgis.admin.pojo.Sandbox;
import org.kloudgis.admin.pojo.SignupUser;
import org.kloudgis.admin.pojo.User;
import org.kloudgis.data.pojo.Layer;
import org.kloudgis.data.pojo.PoiFeature;
import org.kloudgis.data.pojo.QuickFeature;

/**
 *
 * @author jeanfelixg
 */
public class Test {
    
    
    public static void main(String[] a) throws IOException{
        Credential cr = new Credential();
        cr.user="foo";
        String pwd = "bar";
        String crypted = LoginFactory.hashString(pwd, "SHA-256");
        cr.pwd=crypted;
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(cr));
        
        String token = LoginFactory.hashString("foo", "SHA-512");
        System.out.println(token);
        SignupUser usr = new SignupUser();
        usr.user = "foo@bar.com";
        usr.pwd = "fcde2b2edba56bf408601fb721fe9b5c338d10ee429ea04fae5511b68fbf8fb9";
        usr.compagny="XYZ";
        usr.location="Montreal";
        System.out.println(mapper.writeValueAsString(usr));
        Sandbox ss = new Sandbox();
        ss.guid = 123L;
        System.out.println(mapper.writeValueAsString(ss));
        User uu = new User();
        uu.guid = 123L;
        System.out.println(mapper.writeValueAsString(uu));
        
        Layer l = new Layer();
        System.out.println(mapper.writeValueAsString(l));
        
        QuickFeature qf = new QuickFeature();
        System.out.println(mapper.writeValueAsString(qf));
        
        PoiFeature poi = new PoiFeature();
        System.out.println(mapper.writeValueAsString(poi));
        Map m = new LinkedHashMap();
        m.put("p1", "v1");
        m.put("p2", "v2");
        Message mm = new Message();
        mm.type = "test";
        mm.content = m;
        System.out.println(mapper.writeValueAsString(mm));
    }
}
