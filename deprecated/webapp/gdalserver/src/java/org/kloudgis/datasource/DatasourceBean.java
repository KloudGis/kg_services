/*
 * @author corneliu
 */
package org.kloudgis.datasource;

import java.io.File;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import org.kloudgis.gdal.Envelope;
import org.kloudgis.gdal.Parser;
import org.kloudgis.gdal.schema.AttrType;
import org.kloudgis.gdal.schema.RealListType;
import org.kloudgis.gdal.schema.RealType;
import org.kloudgis.gdal.schema.Schema;
import org.kloudgis.gdal.schema.StringListType;
import org.kloudgis.gdal.schema.StringType;
import org.kloudgis.geoserver.Utils;

@Path( "/protected/datasource" )
@Produces( { "application/json" } )
public class DatasourceBean {

    @POST
    @Produces( { "application/json" } )
    public Datasource addDatasource( String strPath ) throws WebApplicationException {
        Datasource ftr = null;
        if( strPath != null ) {
            File file = new File( strPath );
            if( file.exists() ) {
                EntityManager emg = Persistence.createEntityManagerFactory( "gdalserver" ).createEntityManager();
                DatasourceDbEntity dse = persistDatasourceEntity( file, emg );
                if( dse != null ) {
                    ftr = dse.toPojo();
                }
                emg.close();
            }
        }
        return ftr;
    }

    @GET
    @Produces ( { "application/json" } )
    public Datasource getDatasource( Long lID ) {
        return ( ( DatasourceDbEntity )Persistence.createEntityManagerFactory( "gdalserver" ).createEntityManager().find( DatasourceDbEntity.class, lID ) ).toPojo();
    }

    private DatasourceDbEntity persistDatasourceEntity( File file, EntityManager em ) {
        Parser prs = new Parser( file.getAbsolutePath() );
        String strErrors = prs.getErrorMessages();
        if( strErrors == null ) {
            em.getTransaction().begin();
            DatasourceDbEntity dse = new DatasourceDbEntity();
            dse.setFileName( file.getName() );
            dse.setCRS( prs.getCRS() );
            Schema scm = prs.getSchema();
            if( scm != null ) {
                dse.setColumnCount( scm.getAttrCount() );
            }
            dse.setFeatureCount( prs.getFeatureCount() );
            dse.setFileSize( prs.getFileSize() );
            dse.setGeomName( prs.getGeomName() );
            dse.setGeomType( prs.getGeomType() );
            dse.setLastModified( prs.getLastModified() );
            dse.setLayerCount( prs.getLayerCount() );
            Envelope env = prs.getExtent();
            if( env != null ) {
                dse.setMinX( env.getLowX() );
                dse.setMinY( env.getLowY() );
                dse.setMaxX( env.getHighX() );
                dse.setMaxY( env.getHighY() );
            }
            File zip = Utils.zip( file );
            dse.setDataFile( zip );
            em.persist( dse );
            persistColumnsEntities( prs, em, dse );
            em.getTransaction().commit();
            zip.delete();
            return dse;
        }
        return null;
    }

    private void persistColumnsEntities( Parser prs, EntityManager em, DatasourceDbEntity dse ) {
        String strErrors = prs.getErrorMessages();
        if( strErrors == null ) {
            Schema scm = prs.getSchema();
            if( scm != null ) {
                Set<AttrType> setAttrs = scm.getAttrTypes();
                if( setAttrs != null ) {
                    for( AttrType att : setAttrs ) {
                        ColumnsDbEntity cle = new ColumnsDbEntity();
                        cle.setName( att.getName() );
                        cle.setType( att.getClass().getName() );
                        if( att instanceof RealType ) {
                            cle.setPrecision( ( ( RealType )att ).getPrecision() );
                        } else if( att instanceof RealListType ) {
                            cle.setPrecision( ( ( RealListType )att ).getPrecision() );
                        } else if( att instanceof StringType ) {
                            cle.setLength( ( ( StringType )att ).getWidth() );
                            cle.setJustify( ( ( StringType )att ).getJustify() );
                        } else if( att instanceof StringListType ) {
                            cle.setLength( ( ( StringListType )att ).getWidth() );
                            cle.setJustify( ( ( StringListType )att ).getJustify() );
                        }
                        cle.setDatasource( dse );
                        em.persist( cle );
                    }
                }
            }
        }
    }
}