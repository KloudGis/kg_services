/*
 * @author corneliu
 */
package sdmclassgen;

import org.jdom.Element;

/**
 * Class for mapping the joins. One instance per join.
 */
public class Join {
    
    private String strFT;//parent
    private String strAttr;//parent
    private String strToFT;//child
    private String strToAttr;//child
    private boolean bOneToMany;//[false = one to one] [true = one to many]
    
    /**
     * Constructor for this class. Parses the element received as parameter and builds this object.
     * @param strFT is the parent feature type
     * @param eleJoin is the element to be parsed.
     */
    public Join( String strFT, Element eleJoin ) {
        this.strFT = strFT;
        this.strAttr = eleJoin.getAttributeValue( "joinColumn" );
        this.strToFT = eleJoin.getAttributeValue( "joinFeatureType" );
        this.strToAttr = eleJoin.getAttributeValue( "joinFeatureTypeColumn" );
        this.bOneToMany = parseInt( eleJoin.getAttributeValue( "maxCardinality" ) ) != 1;
    }
    
    /**
     * @return parent feature type
     */
    public String getFT() {
        return strFT;
    }
    
    /**
     * @return parent attribute
     */
    public String getAttr() {
        return strAttr;
    }
    
    /**
     * @return child feature type
     */
    public String getToFT() {
        return strToFT;
    }
    
    /**
     * @return child attribute
     */
    public String getToAttr() {
        return strToAttr;
    }
    
    /**
     * @return true if one to many, false if one to one
     */
    public boolean isOneToMany() {
        return bOneToMany;
    }
    
    private int parseInt( String str ) {
        if( str != null ) {
            try {
                return Integer.parseInt( str );
            } catch( Exception e ) {}
        }
        return 0;
    }

    @Override
    public boolean equals( Object obj ) {
        if( obj instanceof Join ) {
            Join jn = ( Join )obj;
            return strFT.equals( jn.strFT ) && strAttr.equals( jn.strAttr ) && strToFT.equals( jn.strToFT ) && 
                    strToAttr.equals( jn.strToAttr ) && bOneToMany == jn.bOneToMany;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[ft = " + strFT + "][attr = " + strAttr + "][toft = " + strToFT + "][toattr = " + strToAttr + "][otm = " + bOneToMany + "]";
    }
}