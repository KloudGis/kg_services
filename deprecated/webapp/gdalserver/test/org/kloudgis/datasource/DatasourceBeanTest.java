/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.datasource;

import org.postgresql.util.PSQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.net.MalformedURLException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author corneliu
 */
public class DatasourceBeanTest {

    /**
     * Test of addDatasource method, of class DatasourceBean.
     */
    @Test
    public void testAddDatasource() throws MalformedURLException, IOException, SQLException, ClassNotFoundException {
        System.out.println("addDatasource");

//        get a direct connection to the test database so we can check if things went right
        Class.forName( "org.postgresql.Driver" );
        Connection conn = DriverManager.getConnection( "jdbc:postgresql://192.168.12.36:5432/la_la_land", "bgjlr", "qwerty" );

//        drop the existing tables so we can start with a clean slate
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement( "drop table columns;" );
            pst.execute();
            pst = conn.prepareStatement( "drop table datasource;" );
            pst.execute();
            pst.close();
        } catch( PSQLException e ) {}

//        get the relative path to the place where the data files for this test are
        String strPath = new File( getClass().getClassLoader().getResource( "org/kloudgis/datasource" ).getFile() ).getAbsolutePath() + "/../../../../../../../res/";

//        insert shape file
        URL url = new URL( "http://localhost:8084/gdalserver/resources/protected/datasource" );
        HttpURLConnection httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        OutputStream ost = httpCon.getOutputStream();
        ost.write( ( strPath + "/cities.shp" ).getBytes() );
        ost.flush();
        int iRet = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        assertEquals( iRet, 200 );

//        test the shape file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='cities.shp';" );
        ResultSet rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 28.1388, 0 );
        assertEquals( rst.getDouble( 3 ), 68.9713, 0 );
        assertEquals( rst.getDouble( 4 ), -21.8522, 0 );
        assertEquals( rst.getDouble( 5 ), 58.5526, 0 );
        assertEquals( rst.getBytes( 6 ).length, 73933, 1 );//35576
        assertEquals( rst.getInt( 7 ), 0 );
        assertEquals( rst.getInt( 8 ), 11 );
        assertEquals( rst.getInt( 9 ), 1267 );
        assertEquals( rst.getInt( 10 ), 1 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 35576 );
        assertEquals( rst.getLong( 13 ), 981014400000l );
        assertEquals( rst.getString( 14 ), "cities.shp" );
        assertEquals( rst.getString( 15 ), "Point" );
        long lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "TYPE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "NATION" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "CNTRYNAME" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "LEVEL" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "NAME" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 10 );
        assertEquals( rst.getString( 5 ), "NAMEPRE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 10 );
        assertEquals( rst.getString( 5 ), "CODE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "PROVINCE" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "PROVNAME" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "UNPROV" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 60 );
        assertEquals( rst.getString( 5 ), "CONURB" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();

//        insert dgn file
        url = new URL( "http://localhost:8084/gdalserver/resources/protected/datasource" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "/2325_integration.dgn" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        assertEquals( iRet, 200 );

//        test the dgn file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='2325_integration.dgn';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 5040476.0779, 0.000001 );
        assertEquals( rst.getDouble( 3 ), 5064226, 0.000001 );
        assertEquals( rst.getDouble( 4 ), 208626.4227, 0.000001 );
        assertEquals( rst.getDouble( 5 ), 229754.9006, 0.000001 );
        assertEquals( rst.getBytes( 6 ).length, 1297824 );//3180544
        assertEquals( rst.getInt( 7 ), -1 );
        assertEquals( rst.getInt( 8 ), 9 );
        assertEquals( rst.getInt( 9 ), 8002 );
        assertEquals( rst.getInt( 10 ), 0 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 3180544 );
        assertEquals( rst.getLong( 13 ), 1248178009000l );
        assertEquals( rst.getString( 14 ), "2325_integration.dgn" );
        assertEquals( rst.getString( 15 ), "Unknown" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Type" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Level" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "GraphicGroup" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "ColorIndex" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Weight" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Style" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "EntityNum" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "MSLink" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Text" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();

//        insert dxf file
        url = new URL( "http://localhost:8084/gdalserver/resources/protected/datasource" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "/LCET2000.dxf" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        assertEquals( iRet, 200 );

//        test the dxf file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='LCET2000.dxf';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 7.31495406149496, 0.000001 );
        assertEquals( rst.getDouble( 3 ), 5148161.91072665, 0.000001 );
        assertEquals( rst.getDouble( 4 ), 5.97605049337149, 0.000001 );
        assertEquals( rst.getDouble( 5 ), 310870.189139075, 0.000001 );
        assertEquals( rst.getBytes( 6 ).length, 3613722 );//22430190
        assertEquals( rst.getInt( 7 ), -1 );
        assertEquals( rst.getInt( 8 ), 6 );
        assertEquals( rst.getInt( 9 ), 18682 );
        assertEquals( rst.getInt( 10 ), 0 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 22430190 );
        assertEquals( rst.getLong( 13 ), 1301343520000l );
        assertEquals( rst.getString( 14 ), "LCET2000.dxf" );
        assertEquals( rst.getString( 15 ), "Unknown" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Layer" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "SubClasses" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "ExtendedEntity" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Linetype" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "EntityHandle" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getString( 5 ), "Text" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();

//        insert gml file
        url = new URL( "http://localhost:8084/gdalserver/resources/protected/datasource" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "lot_occ.gml" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        assertEquals( iRet, 200 );

//        test the gml file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='lot_occ.gml';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 0, 0 );
        assertEquals( rst.getDouble( 3 ), 0, 0 );
        assertEquals( rst.getDouble( 4 ), 0, 0 );
        assertEquals( rst.getDouble( 5 ), 0, 0 );
        assertEquals( rst.getBytes( 6 ).length, 44585 );//359324 );
        assertEquals( rst.getInt( 7 ), -1 );
        assertEquals( rst.getInt( 8 ), 9 );
        assertEquals( rst.getInt( 9 ), 362 );
        assertEquals( rst.getInt( 10 ), 6 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 359324 );
        assertEquals( rst.getLong( 13 ), 1246305154000l );
        assertEquals( rst.getString( 14 ), "lot_occ.gml" );
        assertEquals( rst.getString( 15 ), "MultiPolygon" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "objectid" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "idlotocc" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.IntegerType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 11 );
        assertEquals( rst.getString( 5 ), "nolotmat" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 37 );
        assertEquals( rst.getString( 5 ), "remmat" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 7 );
        assertEquals( rst.getString( 5 ), "matriculem" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "x" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.RealType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "y" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.RealType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "shape_leng" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.RealType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "shape_area" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.RealType" );
        rst.close();
        pst.close();

//        insert kml file
        url = new URL( "http://localhost:8084/gdalserver/resources/protected/datasource" );
        httpCon = ( HttpURLConnection )url.openConnection();
        httpCon.setDoOutput( true );
        httpCon.setRequestMethod( "POST" );
        httpCon.setRequestProperty("Content-type", "application/json");
        ost = httpCon.getOutputStream();
        ost.write( ( strPath + "LOTOCCUPE.kml" ).getBytes() );
        ost.flush();
        iRet = httpCon.getResponseCode();
        ost.close();
        httpCon.disconnect();
        assertEquals( iRet, 200 );

//        test the kml file insertion
        pst = conn.prepareStatement( "select * from datasource where strfilename='LOTOCCUPE.kml';" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getDouble( 2 ), 45.3160540739093, 0.00000001 );
        assertEquals( rst.getDouble( 3 ), 45.3592196152746, 0.00000001 );
        assertEquals( rst.getDouble( 4 ), -72.5514406587745, 0.00000001 );
        assertEquals( rst.getDouble( 5 ), -72.5046028211896, 0.00000001 );
        assertEquals( rst.getBytes( 6 ).length, 688154, 1 );
        assertEquals( rst.getInt( 7 ), 0 );
        assertEquals( rst.getInt( 8 ), 2 );
        assertEquals( rst.getInt( 9 ), 2903 );
        assertEquals( rst.getInt( 10 ), -2147483645 );
        assertEquals( rst.getInt( 11 ), 1 );
        assertEquals( rst.getLong( 12 ), 14479348 );
        assertEquals( rst.getLong( 13 ), 1306241742000l );
        assertEquals( rst.getString( 14 ), "LOTOCCUPE.kml" );
        assertEquals( rst.getString( 15 ), "Unknown" );
        lID = rst.getLong( 1 );
        rst.close();
        pst.close();
        pst = conn.prepareStatement( "select * from columns where dts_lid=" + lID + ";" );
        rst = pst.executeQuery();
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "Name" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        assertTrue( rst.next() );
        assertEquals( rst.getInt( 3 ), 0 );
        assertEquals( rst.getString( 5 ), "Description" );
        assertEquals( rst.getString( 6 ), "org.kloudgis.gdal.schema.StringType" );
        rst.close();
        pst.close();
        conn.close();
    }
}