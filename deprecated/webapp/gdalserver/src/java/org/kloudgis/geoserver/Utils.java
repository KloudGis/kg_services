/*
 * @author corneliu
 */
package org.kloudgis.geoserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {

    public static final String URL = "http://192.168.12.36:8080/geoserver210/rest/";

    /**
     * @param ins is the input stream
     * @return a new Document object from the input stream received as parameter
     */
    public static Document getDocument( InputStream ins ) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dcb = dbf.newDocumentBuilder();
            return dcb.parse( ins );
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param strTag is the name of the child that contains the child whose text is being returned
     * @param ele is the parent element
     * @return the text of the first child of the child (name = strTag) of the element passed as parameter
     */
    public static String getTagValue( String strTag, Element ele ) {
        if( strTag != null && strTag.length() > 0 && ele != null ) {
            NodeList lst = ele.getElementsByTagName( strTag );
            if( lst != null && lst.getLength() > 0 ) {
                NodeList ndl = lst.item( 0 ).getChildNodes();
                if( ndl != null && ndl.getLength() > 0 ) {
                    Node nodValue = ndl.item( 0 );
                    if( nodValue != null ) {
                        return nodValue.getNodeValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * parse boolean
     * @param str the string value of the boolean
     * @return the boolean value of the string received as parameter. False if an error occurred.
     */
    public static boolean parseBoolean( String str ) {
        if( str != null ) {
            try {
                return Boolean.parseBoolean( str.toLowerCase() );
            } catch( Exception e ) {}
        }
        return false;
    }

    /**
     * parse double
     * @param str the string value of the double
     * @return -1 if an error occurred, otherwise the double value of the string received as parameter
     */
    public static double parseDouble( String str ) {
        if( str != null ) {
            try {
                return Double.parseDouble( str );
            } catch( Exception e ) {}
        }
        return -1;
    }

    public static File zip( File file ) {
        String strZipFile = file.getAbsolutePath() + ".zip";
        byte[] buf = new byte[2048];
        try {
            ZipOutputStream out = new ZipOutputStream( new FileOutputStream( strZipFile ) );
            ArrayList<File> arlFiles = getAllFiles( file );
            for( File f : arlFiles ) {
                FileInputStream in = new FileInputStream( f );
                out.putNextEntry( new ZipEntry( f.getName() ) );
                int len;
                while( ( len = in.read( buf ) ) > 0 ) {
                    out.write( buf, 0, len );
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
        return new File( strZipFile );
    }

    private static ArrayList<File> getAllFiles( File file ) {
        ArrayList<File> arlFiles = new ArrayList<File>();
        String strName = file.getName();
        if( strName.toLowerCase().endsWith( ".shp" ) ) {
            File[] files = file.getParentFile().listFiles();
            strName = strName.substring( 0, strName.lastIndexOf( "." ) + 1 );
            for( File f : files ) {
                if( f.getName().startsWith( strName ) ) {
                    arlFiles.add( f );
                }
            }
        } else {
            arlFiles.add( file );
        }
        return arlFiles;
    }
}