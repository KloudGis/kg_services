/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.pojo;

import java.util.List;

/**
 *
 * @author jeanfelixg
 */
public class Feature {
    
    //guid is the concatenation of fid and ft_id :  Ex: fid is 123 and ft_id is 1: guid = 123;1
    public String guid;
    public Long fid;  
    public Long ft_id;
    
    public Long date_create;
    public Long user_create;
    public Long date_update;
    public Long user_update;
    
    //list of joined features guid (fid;ft_id)
    public List<String> joins;
    //list of reverse joined features guid (fid;ft_id)
    public List<String> reverse_joins;    
    
    //for geo, uses an object with is the combinaison of coordinate list and geo_type (WKT is not supported)
    public Geometry geo;
    //fallback to wellknown text (from Space)
    public String wkt;
    
    //texts
    public String text1;
    public String text2;
    public String text3;
    public String text4;
    public String text5;
    public String text6;
    public String text7;
    public String text8;
    public String text9;
    public String text10;
    public String text11;
    public String text12;
    public String text13;
    public String text14;
    public String text15;
    public String text16;
    public String text17;
    public String text18;
    public String text19;
    public String text20;
    public String text21;
    public String text22;
    public String text23;
    public String text24;
    public String text25;
    //bool
    public Boolean bool1;
    public Boolean bool2;
    public Boolean bool3;
    public Boolean bool4;
    public Boolean bool5;
    //dates
    public Long   date1;
    public Long   date2;
    public Long   date3;
    //num
    public Long   num1;
    public Long   num2;
    public Long   num3;
    public Long   num4;
    public Long   num5;
    public Long   num6;
    public Long   num7;
    public Long   num8;
    public Long   num9;
    public Long   num10;
    //decim
    public Double decim1;
    public Double decim2;
    public Double decim3;
    public Double decim4;
    public Double decim5;
    public Double decim6;
    public Double decim7;
    public Double decim8;
    public Double decim9;
    public Double decim10;
    //images (base64 string)
    public String img1;
    public String img2;
    
    public Feature(){}

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature) obj;
        if ((this.guid == null) ? (other.guid != null) : !this.guid.equals(other.guid)) {
            return false;
        }
        return true;
    }   
}
