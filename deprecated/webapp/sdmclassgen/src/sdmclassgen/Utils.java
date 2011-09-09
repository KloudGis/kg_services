/*
 * @author corneliu
 */
package sdmclassgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class for util methods.
 */
public class Utils {
    
    /**
     * Generates a class name that is compliant with java coding rules
     * @param strName is the original unformatted string
     * @return the formatted string
     */
    public static String formatClassName( String strName ) {
        if( strName != null && strName.length() > 0 ) {
            strName = strName.replace( " ", "_" );//eliminate gaps
            String[] staName = strName.toLowerCase().split( "_" );
            strName = "";
            for( String str : staName ) {
                if( str.length() > 0 ) {
                    strName += str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );
                }
            }
        }
        return strName;
    }
    
    /**
     * Generates a field name that is compliant with java coding rules
     * @param strName is the original unformatted string
     * @return the formatted string
     */
    public static String formatAttrName( String strName ) {
        if( strName != null && strName.length() > 0 ) {
            strName = strName.replace( " ", "_" );//eliminate gaps
            String[] staName = strName.toLowerCase().split( "_" );
            strName = "";
            boolean bUpper = false;
            for( String str : staName ) {
                if( bUpper ) {
                    if( str.length() > 0 ) {
                        strName += str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );
                    }
                } else {
                    strName += str;
                    bUpper = true;
                }
            }
        }
        return strName;
    }
    
    /**
     * Copies a file to the new location.
     * @param source is the source file
     * @param target is the path where the file will be copied
     * @throws IOException 
     */
    public static void copyFile( File source, String target ) throws IOException {
        if( source != null && target != null && source.exists() ) {
            new File( target ).getParentFile().mkdirs();
            FileInputStream fis = new FileInputStream( source );
            FileOutputStream fos = new FileOutputStream( target );
            byte[] barBuffer = new byte[4096];
            int iBytesRead;
            while( ( iBytesRead = fis.read( barBuffer ) ) != -1 ) {
                fos.write( barBuffer, 0, iBytesRead );
            }
            fis.close();
            fos.close();
        }
    }
}