/*
 * @author corneliu
 */
package org.kloudgis.datasource;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

@Entity
@Table( name = "datasource" )
public class DatasourceDbEntity implements Serializable {

    @SequenceGenerator(name = "ds_seq_gen", sequenceName = "datasource_fid_seq")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "ds_seq_gen")
    private Long lID;
    @Column( length = 250 )
    @Index( name = "ds_name_ix" )
    private String strFileName;
    @Column
    private String strGeomName;
    @Column
    private int iGeomType;
    @Column
    private int iCRS;
    @Column
    private int iFeatureCount;
    @Column
    private int iLayerCount;
    @Column
    private int iColumnCount;
    @Column
    private long lFileSize;
    @Column
    private long lLastModified;
    @Column
    private double dEnvelopeMinX;
    @Column
    private double dEnvelopeMinY;
    @Column
    private double dEnvelopeMaxX;
    @Column
    private double dEnvelopeMaxY;
    @Column
    @Type( type = "org.kloudgis.datasource.StreamingBinaryArrayFileType" )
    private File file;
    @OneToMany( mappedBy = "dts" )
    private Set<ColumnsDbEntity> setCols;
    
    public Datasource toPojo() {
        Datasource pojo = new Datasource();
        pojo.lID = lID;
        pojo.strFileName = strFileName;
        pojo.strGeomName = strGeomName;
        pojo.iGeomType = iGeomType;
        pojo.iCRS = iCRS;
        pojo.iFeatureCount = iFeatureCount;
        pojo.iLayerCount = iLayerCount;
        pojo.iColumnCount = iColumnCount;
        pojo.lFileSize = lFileSize;
        pojo.lLastModified = lLastModified;
        pojo.dEnvelopeMinX = dEnvelopeMinX;
        pojo.dEnvelopeMinY = dEnvelopeMinY;
        pojo.dEnvelopeMaxX = dEnvelopeMaxX;
        pojo.dEnvelopeMaxY = dEnvelopeMaxY;
        pojo.file = file;
        Set<Long> setColumns = new LinkedHashSet<Long>();
        if( setCols != null ) {
            for( ColumnsDbEntity cle : setCols ) {
                setColumns.add( cle.getID() );
            }
        }
        pojo.setCols = setColumns;
        return pojo;
    }
    
    public void setFileName( String strFileName ) {
        this.strFileName = strFileName;
    }
    
    public void setGeomName( String strGeomName ) {
        this.strGeomName = strGeomName;
    }
    
    public void setGeomType( int iGeomType ) {
        this.iGeomType = iGeomType;
    }
    
    public void setCRS( int iCRS ) {
        this.iCRS = iCRS;
    }
    
    public void setFeatureCount( int iFeatureCount ) {
        this.iFeatureCount = iFeatureCount;
    }
    
    public void setLayerCount( int iLayerCount ) {
        this.iLayerCount = iLayerCount;
    }
    
    public void setColumnCount( int iColumnCount ) {
        this.iColumnCount = iColumnCount;
    }
    
    public void setFileSize( long lFileSize ) {
        this.lFileSize = lFileSize;
    }
    
    public void setLastModified( long lLastModified ) {
        this.lLastModified = lLastModified;
    }
    
    public void setMinX( double dEnvelopeMinX ) {
        this.dEnvelopeMinX = dEnvelopeMinX;
    }
    
    public void setMinY( double dEnvelopeMinY ) {
        this.dEnvelopeMinY = dEnvelopeMinY;
    }
    
    public void setMaxX( double dEnvelopeMaxX ) {
        this.dEnvelopeMaxX = dEnvelopeMaxX;
    }
    
    public void setMaxY( double dEnvelopeMaxY ) {
        this.dEnvelopeMaxY = dEnvelopeMaxY;
    }
    
    public void setDataFile( File file ) {
        this.file = file;
    }
    
    public void setColumns( Set<ColumnsDbEntity> setCols ) {
        this.setCols = setCols;
    }
    
    public Set<ColumnsDbEntity> getColumns() {
        return setCols;
    }

    public Long getID() {
        return lID;
    }

    public void setID( Long lID ) {
        this.lID = lID;
    }
}