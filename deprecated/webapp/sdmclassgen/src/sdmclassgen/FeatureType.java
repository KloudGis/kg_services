/*
 * @author corneliu
 */
package sdmclassgen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.jdom.Element;

/**
 * This class corresponds to a feature type node. One instance per feature type node.
 */
public class FeatureType {
    
    private String strName;
    private String strTableName;
    private String strFTName;
    private LinkedHashMap<String, Attribute> lhmFields = new LinkedHashMap<String, Attribute>();
    
    /**
     * Constructor for this class. Parses the element received as parameter and builds this object.
     * @param eleFT is the node that will be parsed.
     */
    public FeatureType( Element eleFT ) {
        Element eleDS = eleFT.getChild( "Dataset" );
        strFTName = eleFT.getAttributeValue( "name" );
        strName = Utils.formatClassName( strFTName );
        if( eleDS != null ) {
            strTableName = eleDS.getAttributeValue( "name" );
        } else {
            System.out.println( "FeatureType constructor error: dataset null for feature type: " + strFTName );
        }
        List<Element> lstAttrs = eleFT.getChild( "Schema" ).getChildren( "AttributeType" );
        for( Element eleAttr : lstAttrs ) {
            addField( new Attribute( eleAttr ) );
        }
    }
    
    /**
     * Writes the pojo class in package org.kloudgis.model.pojos
     * @param strPath is the source path (includes org/kloudgis/model)
     * @throws IOException 
     */
    public void writePojo( String strPath ) throws IOException {
        FileWriter wrt = new FileWriter( strPath + "/pojos/" + strName + ".java" );
        wrt.write( "/*Class generated automatically from an sdm file*/\npackage org.kloudgis.model.pojos;\n\npublic class " );
        wrt.write( strName );
        wrt.write( " {\n\n" );
        for( Attribute fld : lhmFields.values() ) {
            String strClass = fld.getJavaClass();
            if( strClass != null ) {
                wrt.write( "\tpublic " );
                wrt.write( strClass );
                wrt.write( " " );
                wrt.write( fld.getName() );
                wrt.write( ";\n" );
                Set<Join> setJoins = fld.getJoins();
                for( Join jn : setJoins ) {
                    if( jn.isOneToMany() ) {
                        if( strFTName.equals( jn.getFT() ) ) {
                            wrt.write( "\tpublic java.util.Set<Long> set" );
                            wrt.write( Utils.formatClassName( jn.getToFT() + "_" + jn.getAttr() + "_" + jn.getToAttr() ) );
                            wrt.write( ";\n" );
                        } else if( strFTName.equals( jn.getToFT() ) ) {
                            wrt.write( "\tpublic long l" );
                            wrt.write( Utils.formatClassName( jn.getToFT() + "_" + jn.getAttr() + "_" + jn.getToAttr() ) );
                            wrt.write( ";\n" );
                        } else {
                            System.out.println( "Class FeatureType, method writePojo() error: orphan join: " + jn.toString() );
                        }
                    }
                }
            }
        }
        wrt.write( "}" );
        wrt.flush();
        wrt.close();
    }
    
    /**
     * Writes the entity class in package org.kloudgis.model.entities
     * @param strPath strPath is the source path (includes org/kloudgis/model)
     * @throws IOException 
     */
    public void writeEntity( String strPath ) throws IOException {
        FileWriter wrt = new FileWriter( strPath + "/entities/" + strName + "DbEntity.java" );
        writeClassHeader( wrt );
        String strID = null;//flag for existing id
        Collection<Attribute> colAttrs = lhmFields.values();
        for( Attribute fld : colAttrs ) {
            String strClass = fld.getClassName();
            if( strClass.equals( "com.space.core.model.feature.attr.IdType" ) ) {
                strID = fld.getName();
                writeIdMember( wrt, fld );
            } else if( strClass.equals( "com.space.core.model.feature.attr.NumericType" ) ) {
                writeNumberMember( wrt, fld );
            } else if( strClass.equals( "com.space.core.model.feature.attr.BooleanType" ) ) {
                writeBooleanMember( wrt, fld );
            } else if( strClass.equals( "com.space.core.model.feature.attr.TextType" ) || 
                    strClass.equals( "com.space.core.model.feature.attr.MemoType" ) || 
                    strClass.equals( "com.space.core.model.feature.attr.HyperlinkType" ) ) {
                writeStringMember( wrt, fld );
            } else if( strClass.equals( "com.space.core.model.feature.attr.TimeStampType" ) ) {
                writeDateMember( wrt, fld );
            } else if( strClass.equals( "com.space.core.model.feature.attr.BinaryType" ) ) {
                writeByteArrayMember( wrt, fld );
            }
            writeJoins( wrt, fld.getJoins(), fld.getAttrName() );
        }
        if( strID == null ) {
            strID = "space_feature_id";
            writeIdMember( wrt, strID, strID );//there must be an id, otherwise it won't compile
        }
        wrt.write( "\n\tpublic Long getID() {\n\t\treturn " );
        wrt.write( strID );
        wrt.write( ";\n\t}\n" );
        writeToPojo( wrt, colAttrs );
        writeMethods( wrt, colAttrs );
        wrt.write( "}" );
        wrt.flush();
        wrt.close();
    }
    
    /**
     * @return the feature type name
     */
    public String getFTName() {
        return strFTName;
    }
    
    /**
     * @return the formatted class name for this feature type
     */
    public String getName() {
        return strName;
    }
    
    /**
     * Adds a join object parameter to the proper attribute that belongs to this feature type
     * @param jn 
     */
    public void addJoin( Join jn ) {
        Attribute att = lhmFields.get( jn.getAttr() );//try getting the parent attribute
        if( att == null ) {//if null, try getting the child attribute
            att = lhmFields.get( jn.getToAttr() );
        }
        if( att != null ) {
            att.addJoin( jn );
        } else {
            System.out.println( "Class FeatureType, method addJoin() error: Attribute not found for join:" + jn.toString() );
        }
    }
    
    /**
     * @return the map of attributes. The keys are the the attribute names and the values are the attributes themselves.
     */
    public LinkedHashMap<String, Attribute> getAttrs() {
        return lhmFields;
    }
    
    /**
     * If the attribute is already in the map it will be updated, else it will be added to the map.
     * @param fld is the attribute that will update an existing attribute or will be added to the map.
     */
    private void addField( Attribute fld ) {
        if( fld != null ) {
            Attribute f = lhmFields.get( fld.getAttrName() );
            if( f != null ) {
                f.setID( fld.isID() );
                f.setIndexType( fld.getIndexType() );
                f.setLength( fld.getLength() );
                f.setManyToOne( fld.isManyToOne() );
                f.setOneOneToMany( fld.isOneOneToMany() );
                f.setNullable( fld.isNullable() );
            } else {
                lhmFields.put( fld.getAttrName(), fld );
            }
        }
    }
    
    private void writeClassHeader( FileWriter wrt ) throws IOException {
        wrt.write( "/*Class generated automatically from an sdm file*/\npackage org.kloudgis.model.entities;\n\n" );
        wrt.write( "import org.kloudgis.model.pojos." );
        wrt.write( strName );
        wrt.write( ";\nimport java.io.Serializable;\nimport javax.persistence.Column;\nimport javax.persistence.Entity;\n" );
        wrt.write( "import javax.persistence.GeneratedValue;\nimport javax.persistence.GenerationType;\nimport javax.persistence.Id;\n" );
        wrt.write( "import javax.persistence.SequenceGenerator;\nimport javax.persistence.Table;\n\n@Entity\n@Table( name = \"" );
        if( strTableName != null ) {
            wrt.write( strTableName );
        } else {
            System.out.println( "Class FeatureType, method writeClassHeader() error: table name is null for feature type: " + strFTName );
        }
        wrt.write( "\" )\npublic class " );
        wrt.write( strName );
        wrt.write( "DbEntity implements Serializable {\n\n" );
    }

    private void writeIdMember( FileWriter wrt, Attribute att ) throws IOException {
        writeIdMember( wrt, att.getAttrName(), att.getName() );
    }
    
    private void writeIdMember( FileWriter wrt, String strAttrName, String strName ) throws IOException {
        wrt.write( "\t@SequenceGenerator( name = \"" );
        String strSeqGen = strFTName + "_seq_gen";
        wrt.write( strSeqGen );
        wrt.write( "\", sequenceName = \"" );
        wrt.write( strFTName );
        wrt.write( "_fid_seq\" )\n\t@Id\n\t@GeneratedValue( strategy = GenerationType.AUTO, generator = \"" );
        wrt.write( strSeqGen );
        wrt.write( "\" )" );
        wrt.write( "\n\t@Column( name = \"" );
        wrt.write( strAttrName );
        wrt.write( "\" )\n\tprivate long " );
        wrt.write( strName );
        wrt.write( ";\n" );
    }
    
    private void writeBooleanMember( FileWriter wrt, Attribute att ) throws IOException {
        wrt.write( "\t@Column( name = \"" );
        wrt.write( att.getAttrName() );
        wrt.write( "\", nullable = " );
        wrt.write( att.isNullable() ? "true" : "false" );
        wrt.write( " )\n\tprivate boolean " );
        wrt.write( att.getName() );
        wrt.write( ";\n" );
    }
    
    private void writeStringMember( FileWriter wrt, Attribute att ) throws IOException {
        wrt.write( "\t@Column( name = \"" );
        wrt.write( att.getAttrName() );
        wrt.write( "\"" );
        byte bIndex = att.getIndexType();
        if( bIndex == Attribute.INDEX_UNIQUE ) {
            wrt.write( ", unique = true" );
        } else if( bIndex == Attribute.INDEX_DUPLICATE ) {
            wrt.write( ", unique = false" );
        }
        wrt.write( ", nullable = " );
        wrt.write( att.isNullable() ? "true" : "false" );
        wrt.write( ", length = " + att.getLength() );
        wrt.write( " )\n\tprivate String " );
        wrt.write( att.getName() );
        wrt.write( ";\n" );
    }
    
    private void writeNumberMember( FileWriter wrt, Attribute att ) throws IOException {
        wrt.write( "\t@Column( name = \"" );
        wrt.write( att.getAttrName() );
        wrt.write( "\"" );
        byte bIndex = att.getIndexType();
        if( bIndex == Attribute.INDEX_UNIQUE ) {
            wrt.write( ", unique = true" );
        } else if( bIndex == Attribute.INDEX_DUPLICATE ) {
            wrt.write( ", unique = false" );
        }
        wrt.write( ", nullable = " );
        wrt.write( att.isNullable() ? "true" : "false" );
        wrt.write( " )\n\tprivate " );
        wrt.write( att.getJavaClass() );
        wrt.write( " " );
        wrt.write( att.getName() );
        wrt.write( ";\n" );
    }
    
    private void writeDateMember( FileWriter wrt, Attribute att ) throws IOException {
        wrt.write( "\t@Column( name = \"" );
        wrt.write( att.getAttrName() );
        wrt.write( "\"" );
        byte bIndex = att.getIndexType();
        if( bIndex == Attribute.INDEX_UNIQUE ) {
            wrt.write( ", unique = true" );
        } else if( bIndex == Attribute.INDEX_DUPLICATE ) {
            wrt.write( ", unique = false" );
        }
        wrt.write( ", nullable = " );
        wrt.write( att.isNullable() ? "true" : "false" );
        wrt.write( " )\n\t@javax.persistence.Temporal( javax.persistence.TemporalType.DATE )\n\tprivate java.util.Date " );
        wrt.write( att.getName() );
        wrt.write( ";\n" );
    }
    
    private void writeByteArrayMember( FileWriter wrt, Attribute att ) throws IOException {
        wrt.write( "\t@Column( name = \"" );
        wrt.write( att.getAttrName() );
        wrt.write( "\"" );
        byte bIndex = att.getIndexType();
        if( bIndex == Attribute.INDEX_UNIQUE ) {
            wrt.write( ", unique = true" );
        } else if( bIndex == Attribute.INDEX_DUPLICATE ) {
            wrt.write( ", unique = false" );
        }
        wrt.write( ", nullable = " );
        wrt.write( att.isNullable() ? "true" : "false" );
        wrt.write( " )\n\tprivate byte[] " );
        wrt.write( att.getName() );
        wrt.write( ";\n" );
    }

    private void writeMethods( FileWriter wrt, Collection<Attribute> colAttrs ) throws IOException {
        for( Attribute att : colAttrs ) {
            String strFieldName = att.getName();
            String strClass = att.getJavaClass();
            if( strFieldName != null && strClass != null ) {
                wrt.write( "\n\tpublic " );
                wrt.write( strClass );
                wrt.write( " get" );
                wrt.write( strFieldName );
                wrt.write( "() {\n\t\treturn " );
                wrt.write( strFieldName );
                wrt.write( ";\n\t}\n\n\tpublic void set" );
                wrt.write( strFieldName );
                wrt.write( "( " );
                wrt.write( strClass );
                wrt.write( " arg ) {\n\t\tthis." );
                wrt.write( strFieldName );
                wrt.write( " = arg;\n\t}\n" );
            }
        }
    }
    
    private void writeJoins( FileWriter wrt, Set<Join> setJoins, String strAttrName ) throws IOException {
        for( Join jn : setJoins ) {
            if( jn.getFT().equals( strFTName ) && jn.getAttr().equals( strAttrName ) ) {
                String strJoinClass = Utils.formatClassName( jn.getToFT() );
                if( jn.isOneToMany() ) {
                    wrt.write( "\t@javax.persistence.OneToMany( mappedBy = \"" );
                    wrt.write( Utils.formatAttrName( jn.getFT() + "_" + jn.getAttr() + "_" + jn.getToAttr() ) );
                    wrt.write( "\" )\n\tprivate java.util.Set<" );
                    wrt.write( strJoinClass );
                    wrt.write( "DbEntity> set" ); 
                    wrt.write( Utils.formatClassName( jn.getToFT() + "_" + jn.getAttr() + "_" + jn.getToAttr() ) );
                    wrt.write( ";\n" );
                } else {
                    wrt.write( "\t@javax.persistence.OneToOne\n\tprivate " );
                    wrt.write( strJoinClass );
                    wrt.write( "DbEntity " );
                    wrt.write( Utils.formatAttrName( jn.getToFT() + "_" + jn.getAttr() + "_" + jn.getToAttr() ) );
                    wrt.write( ";\n" );
                }
            }
            if( jn.isOneToMany() && jn.getToFT().equals( strFTName ) && jn.getToAttr().equals( strAttrName ) ) {
                wrt.write( "\t@javax.persistence.ManyToOne\n\tprivate " );
                wrt.write( Utils.formatClassName( jn.getFT() ) );
                wrt.write( "DbEntity " );
                wrt.write( Utils.formatAttrName( jn.getFT() + "_" + jn.getAttr() + "_" + jn.getToAttr() ) );
                wrt.write( ";\n" );
            }
        }
    }
    
    private void writeToPojo( FileWriter wrt, Collection<Attribute> colAttrs ) throws IOException {
        wrt.write( "\n\tpublic " );
        wrt.write( strName );
        wrt.write( " toPojo() {\n\t\t" );
        wrt.write( strName );
        wrt.write( " pojo = new " );
        wrt.write( strName );
        wrt.write( "();\n" );
        for( Attribute att : colAttrs ) {
            String strFieldName = att.getName();
            String strClass = att.getJavaClass();
            if( strName != null && strClass != null ) {
                wrt.write( "\t\tpojo." );
                wrt.write( strFieldName );
                wrt.write( " = " );
                wrt.write( strFieldName );
                wrt.write( ";\n" );
                Set<Join> setJoins = att.getJoins();
                for( Join jn : setJoins ) {
                    if( jn.isOneToMany() ) {
                        String strFT = jn.getFT();
                        String strToFT = jn.getToFT();
                        if( strFTName.equals( strFT ) ) {
                            String strSet = Utils.formatClassName( strToFT + "_" + jn.getAttr() + "_" + jn.getToAttr() );
                            wrt.write( "\t\tjava.util.Set<Long> lhs" );
                            wrt.write( strSet );
                            wrt.write( " = new java.util.LinkedHashSet<Long>();\n\t\tif( set" );
                            wrt.write( strSet );
                            wrt.write( " != null ) {\n\t\t\tfor( " );
                            wrt.write( Utils.formatClassName( strToFT ) );
                            wrt.write( "DbEntity cle : set" );
                            wrt.write( strSet );
                            wrt.write( " ) {\n\t\t\t\tlhs" );
                            wrt.write( strSet );
                            wrt.write( ".add( cle.getID() );\n\t\t\t}\n\t\t}\n\t\tpojo.set" );
                            wrt.write( strSet );
                            wrt.write( " = lhs" );
                            wrt.write( strSet );
                            wrt.write( ";\n" );
                        } else if( strFTName.equals( jn.getToFT() ) ) {
                            String strJoinField = Utils.formatAttrName( jn.getFT() + "_" + jn.getAttr() + "_" + jn.getToAttr() );
                            wrt.write( "\t\tpojo.l" );
                            wrt.write( Utils.formatClassName( strToFT + "_" + jn.getAttr() + "_" + jn.getToAttr() ) );
                            wrt.write( " = " );
                            wrt.write( strJoinField );
                            wrt.write( " == null ? ( long )-1 : " );
                            wrt.write( strJoinField );
                            wrt.write( ".getID();\n" );
                        }
                    }
                }
            }
        }
        wrt.write( "\t\treturn pojo;\n\t}\n" );
    }
}