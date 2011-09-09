/*
 * @author corneliu
 */
package sdmclassgen;

import java.util.HashSet;
import java.util.Set;
import org.jdom.Element;

/**
 * Class that corresponds to the AttrType node. One instance per node.
 */
public class Attribute {
    
    public static final byte INDEX_NULL = 0;
    public static final byte INDEX_UNIQUE = 1;
    public static final byte INDEX_DUPLICATE = 2;
    private String strName;
    private String strClass;
    private String strJavaClass;
    private String strAttrName;
    private boolean bID;
    private boolean bManyToOne;
    private boolean bOneOneToMany;
    private boolean bNullable;
    private long lLength;
    private byte bDecimal;
    private byte bIndexType = INDEX_NULL;
    private Set<Join> setJoins = new HashSet<Join>();
    
    /**
     * Constructor for this class. Parses the element received as parameter and builds this object.
     * @param eleAttr is the element to be parsed.
     */
    public Attribute( Element eleAttr ) {
        strClass = eleAttr.getAttributeValue( "class" );
        strAttrName = eleAttr.getAttributeValue( "name" );
        strName = Utils.formatAttrName( strAttrName );
        String strDecimal = eleAttr.getAttributeValue( "decimal" );
        if( strDecimal != null && strDecimal.length() > 0 ) {
            bDecimal = Byte.parseByte( strDecimal );
        }
        String strLength = eleAttr.getAttributeValue( "length" );
        if( strLength != null && strLength.length() > 0 ) {
            lLength = Long.parseLong( strLength );
        }
        strJavaClass = javaClass();
        bID = strClass.equals( "com.space.core.model.feature.attr.IdType" );
        bNullable = !Boolean.parseBoolean( eleAttr.getAttributeValue( "mandatory" ) );
        String strIndex = eleAttr.getAttributeValue( "index" );
        if( strIndex != null ) {
            if( strIndex.equals( "unique" ) ) {
                bIndexType = INDEX_UNIQUE;
            } else if( strIndex.equals( "duplicate" ) ) {
                bIndexType = INDEX_DUPLICATE;
            }
        }
    }

    public String getName() {
        return strName;
    }
    
    public String getClassName() {
        return strClass;
    }
    
    public String getJavaClass() {
        return strJavaClass;
    }
    
    public String getAttrName() {
        return strAttrName;
    }
    
    public boolean isID() {
        return bID;
    }
    
    public boolean isManyToOne() {
        return bManyToOne;
    }
    
    public boolean isOneOneToMany() {
        return bOneOneToMany;
    }
    
    public boolean isNullable() {
        return bNullable;
    }
    
    public long getLength() {
        return lLength;
    }
    
    public byte getIndexType() {
        return bIndexType;
    }
    
    public Set<Join> getJoins() {
        return setJoins;
    }
    
    public void setName( String strName ) {
        this.strName = strName;
    }
    
    public void setClassName( String strClass ) {
        this.strClass = strClass;
    }
    
    public void setJavaClass( String strJavaClass ) {
        this.strJavaClass = strJavaClass;
    }

    public void setAttrName( String strAttrName ) {
        this.strAttrName = strAttrName;
    }
    
    public void setID( boolean bID ) {
        this.bID = bID;
    }
    
    public void setManyToOne( boolean bManyToOne ) {
        this.bManyToOne = bManyToOne;
    }
    
    public void setOneOneToMany( boolean bOneOneToMany ) {
        this.bOneOneToMany = bOneOneToMany;
    }
    
    public void setNullable( boolean bNullable ) {
        this.bNullable = bNullable;
    }
    
    public void setLength( long lLength ) {
        this.lLength = lLength;
    }
    
    public void setIndexType( byte bIndexType ) {
        this.bIndexType = bIndexType;
    }
    
    public void addJoin( Join jn ) {
        setJoins.add( jn );
    }
    
    private String javaClass() {
        if( strClass.equals( "com.space.core.model.feature.attr.IdType" ) ) {
            return "long";
        } else if( strClass.equals( "com.space.core.model.feature.attr.NumericType" ) ) {
            if( bDecimal <= 0 ) {
                if( lLength < 10 ) {
                    return "int";
                } else {
                    return "long";
                }
            } else {
                if( bDecimal < 10 ) {
                    return "float";
                } else {
                    return "double";
                }
            }
            
        } else if( strClass.equals( "com.space.core.model.feature.attr.BooleanType" ) ) {
            return "boolean";
        } else if( strClass.equals( "com.space.core.model.feature.attr.TextType" ) || 
                strClass.equals( "com.space.core.model.feature.attr.MemoType" ) || 
                strClass.equals( "com.space.core.model.feature.attr.HyperlinkType" ) ) {
            return "String";
        } else if( strClass.equals( "com.space.core.model.feature.attr.TimeStampType" ) ) {
            return "java.util.Date";
        } else if( strClass.equals( "com.space.core.model.feature.attr.BinaryType" ) ) {
            return "byte[]";
        }
        return null;
    }
}