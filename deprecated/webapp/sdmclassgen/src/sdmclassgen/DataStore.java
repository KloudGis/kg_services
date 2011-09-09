/*
 * @author corneliu
 */
package sdmclassgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Class for generating /src/hibernate.cfg.xml needed for database access
 */
public class DataStore {
    
    private String username;
    private String password;
    private String ip;
    private String dbname;
    public static final char[] caAlphabet0 = { 'U', '3', 'a', '!', 'X', 'Y', 'q', 'p', 'O', '6', 'o', 'z', 'B', '[', 'm', 'V',
        '_', 'r', 'Z', '9', 'b', '0', 'f', 'E', '$', 'y', 'W', 'k', 'A', 'l', '#', 'S', 'c', '5', 'd', 'K', 'h', 'I', 'T', 'G', '*', 'N', '8', 'C', '>', '1', 'w', 's',
        '2', 't', 'R', 'i', '@', 'M', 'e', 'D', 'g', '-', 'j', ']', 'Q', 'n', 'F', '%', 'P', 'u', 'H', '?', 'J', 'v', '4', '7', 'L', 'x' };
    public static final char[] caAlphabet1 = { 'Z', 'b', '4', 'f', 'E', '>', '9', 'y', 'W', 'k', '-', 'j', 'Q', 'n', '$', 'F',
        'P', 'u', 'q', 'o', '%', '0', 'O', 'p', 'U', 'a', 'X', '2', 'Y', 'z', 'S', 'm', 'V', '_', 'r', '@', '6', 'M', 'I', 'e', 'A', '3', 'g', 'J', '*', '[', 'v', 'L',
        'x', '1', 'T', 'G', 'N', 'C', 'w', 'D', '#', '7', 'l', '?', 'B', 'd', 's', '8', ']', 'K', 'h', 'H', 'c', '!', 't', 'R', '5', 'i' };
    public static final char[] caAlphabet2 = { 'I', 'M', 'r', '5', 'u', '*', 'v', 'L', 'o', 'f', 'E', 'H', '3', 'x', '[', 'Q',
        'n', 'i', 'y', 'W', '4', 'k', '-', 'e', 'B', 'j', 'F', 'X', ']', 'P', 'K', 'h', '7', 'G', '>', 'N', 'A', '8', 'l', 'c', '%', 'S', '1', 'm', '_', 'U', '!', 't',
        'R', 'Y', 'a', 'q', '9', 'p', 'g', '$', 'O', '#', 'z', 'T', 'D', 'w', '6', 'C', 'd', '?', '2', 's', 'Z', 'b', 'J', '@', '0', 'V' };
    public static final char[] caAlphabet3 = { 'y', 'W', '1', 'o', 'L', 'x', '>', 'T', 'G', 'e', 'D', 'g', 'R', 'S', '3', '#',
        'm', '_', 'I', 'p', '8', '%', 'O', 'r', 'Z', 'j', 'E', 'U', 'X', 'a', '!', 'Y', '6', 'z', '4', 'V', 'k', 'q', 'v', 'w', '-', 'P', '5', '[', 'u', 'H', '0', 'c',
        'd', 'N', 'n', '$', 'F', 'C', 't', '9', 'A', 'l', 'B', 's', 'b', '@', 'K', 'M', 'J', 'h', '7', '2', '?', ']', 'i', 'f', '*', 'Q' };
    private final String[] staDelim = { "j8Z", "Hj9", "gJ3", "j3_", "Zz-", "dw-", "78_", "ZJ9", "jkK", "Iix" };
    
    /**
     * Constructor for this class. Parses the element received as parameter and builds this object.
     * @param eleRoot is the element to be parsed.
     */
    public DataStore( Element eleRoot ) {
        Element eleDB = eleRoot.getChild( "Datastore" );
        Element eleDA = eleDB.getChild( "DataAccess" );
        username = eleDA.getAttributeValue( "user" );
        ip = eleDA.getAttributeValue( "ip" );
        dbname = eleDA.getAttributeValue( "databasename" );
        String strPassword = eleDA.getAttributeValue( "password" );
        password = "";
        for( String s : decrypt( strPassword ) ) {
            password += s;
        }
    }

    /**
     * Builds a jdom Document and writes it in hibernate.cfg.xml in src folder
     * @param strPath
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void writeHibernateCfgXml( String strPath ) throws FileNotFoundException, IOException {
        Format frm = Format.getPrettyFormat();
        frm.setEncoding( "UTF-8" );
        XMLOutputter outputter = new XMLOutputter( frm );
        FileOutputStream fos = null;
        File file = new File( strPath + "/src/hibernate.cfg.xml" );
        fos = new FileOutputStream( file );
        Element eleRoot = new Element( "hibernate-configuration" );
        Element eleSessionFactory = new Element( "session-factory" );
        Element eleDialect = new Element( "property" );
        eleDialect.setAttribute( "name", "hibernate.dialect" );
        eleDialect.setText( "org.hibernate.dialect.PostgreSQLDialect" );
        eleSessionFactory.addContent( eleDialect );
        Element eleDriver = new Element( "property" );
        eleDriver.setAttribute( "name", "hibernate.connection.driver_class" );
        eleDriver.setText( "org.postgresql.Driver" );
        eleSessionFactory.addContent( eleDriver );
        Element eleURL = new Element( "property" );
        eleURL.setAttribute( "name", "hibernate.connection.url" );
        eleURL.setText( "jdbc:postgresql://" + ip + ":5432/" + dbname );
        eleSessionFactory.addContent( eleURL );
        Element eleUserName = new Element( "property" );
        eleUserName.setAttribute( "name", "hibernate.connection.username" );
        eleUserName.setText( username );
        eleSessionFactory.addContent( eleUserName );
        Element elePassword = new Element( "property" );
        elePassword.setAttribute( "name", "hibernate.connection.password" );
        elePassword.setText( password );
        eleSessionFactory.addContent( elePassword );
        eleRoot.addContent( eleSessionFactory );
        DocType doctype = new DocType( "hibernate-configuration", 
                "-//Hibernate/Hibernate Configuration DTD 3.0//EN", 
                "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd" );
        outputter.output( new Document( eleRoot, doctype ), fos );
        fos.close();
    }
    
    private String[] decrypt(String strEPW) {
        String[] staRet = null;
        int iSrchMaskAlph = 0x3;
        int iEncrMaskAlph = 0xC;
        if( strEPW != null && strEPW.length() == 37 ) {
            StringBuilder sbfEPW = new StringBuilder();// Encrypted password, unscrambled
            StringBuilder sbfEPWS = new StringBuilder( strEPW );// Encrypted password, scrambled

            // Unscramble
            sbfEPW.append(sbfEPWS.charAt(2));
            sbfEPW.append(sbfEPWS.charAt(18));
            sbfEPW.append(sbfEPWS.charAt(24));
            sbfEPW.append(sbfEPWS.charAt(8));
            sbfEPW.append(sbfEPWS.charAt(23));
            sbfEPW.append(sbfEPWS.charAt(14));
            sbfEPW.append(sbfEPWS.charAt(29));
            sbfEPW.append(sbfEPWS.charAt(34));
            sbfEPW.append(sbfEPWS.charAt(5));
            sbfEPW.append(sbfEPWS.charAt(36));
            sbfEPW.append(sbfEPWS.charAt(15));
            sbfEPW.append(sbfEPWS.charAt(33));
            sbfEPW.append(sbfEPWS.charAt(20));
            sbfEPW.append(sbfEPWS.charAt(9));
            sbfEPW.append(sbfEPWS.charAt(13));
            sbfEPW.append(sbfEPWS.charAt(1));
            sbfEPW.append(sbfEPWS.charAt(22));
            sbfEPW.append(sbfEPWS.charAt(6));
            sbfEPW.append(sbfEPWS.charAt(32));
            sbfEPW.append(sbfEPWS.charAt(28));
            sbfEPW.append(sbfEPWS.charAt(17));
            sbfEPW.append(sbfEPWS.charAt(27));
            sbfEPW.append(sbfEPWS.charAt(3));
            sbfEPW.append(sbfEPWS.charAt(31));
            sbfEPW.append(sbfEPWS.charAt(12));
            sbfEPW.append(sbfEPWS.charAt(21));
            sbfEPW.append(sbfEPWS.charAt(19));
            sbfEPW.append(sbfEPWS.charAt(0));
            sbfEPW.append(sbfEPWS.charAt(35));
            sbfEPW.append(sbfEPWS.charAt(11));
            sbfEPW.append(sbfEPWS.charAt(25));
            sbfEPW.append(sbfEPWS.charAt(30));
            sbfEPW.append(sbfEPWS.charAt(16));
            sbfEPW.append(sbfEPWS.charAt(4));
            sbfEPW.append(sbfEPWS.charAt(10));
            sbfEPW.append(sbfEPWS.charAt(26));
            char cChkSum = sbfEPWS.charAt(7);
            int iSrchAlph = cChkSum & iSrchMaskAlph;
            int iEncrAlph = ( cChkSum & iEncrMaskAlph ) >> 2;
            if( iSrchAlph == iEncrAlph ) {
                iEncrAlph++;
                if( iEncrAlph == 4 ) {
                    iEncrAlph = 0;
                }
            }
            char[] caAlphabetSrch = caAlphabet0;
            switch( iSrchAlph ) {
                case 0:
                    caAlphabetSrch = caAlphabet0;
                    break;
                case 1:
                    caAlphabetSrch = caAlphabet1;
                    break;
                case 2:
                    caAlphabetSrch = caAlphabet2;
                    break;
                case 3:
                    caAlphabetSrch = caAlphabet3;
            }
            char[] caAlphabetEncr = caAlphabet0;
            switch( iEncrAlph ) {
                case 0:
                    caAlphabetEncr = caAlphabet0;
                    break;
                case 1:
                    caAlphabetEncr = caAlphabet1;
                    break;
                case 2:
                    caAlphabetEncr = caAlphabet2;
                    break;
                case 3:
                    caAlphabetEncr = caAlphabet3;
            }
            StringBuilder sbfDPW = new StringBuilder();// Decrypted password
            for( int i = 0; i < sbfEPW.length(); i++ ) {
                for( int j = 0; j < caAlphabetEncr.length; j++ ) {
                    if( sbfEPW.charAt(i) == caAlphabetEncr[j] ) {
                        sbfDPW.append( caAlphabetSrch[j] );
                        break;
                    }
                }
            }
            char cChkSumCalc = getCheckSum( sbfDPW );
            if( cChkSum == cChkSumCalc ) {
                String strPW = sbfDPW.substring( 0, 30 );
                StringBuilder sbfPW = new StringBuilder( strPW );
                int iX = -1;
                for( int i = 0; i < staDelim.length; i++ ) {
                    iX = sbfPW.indexOf( staDelim[i] );
                    if( iX != -1 ) {
                        strPW = sbfPW.substring( 0, iX );
                        break;
                    }
                }
                if ( iX == -1 ) {
                    if( sbfPW.charAt(sbfPW.length() - 1 ) == '_' ) {
                        sbfPW.deleteCharAt( sbfPW.length() - 1 );
                    }
                    if( sbfPW.charAt( sbfPW.length() - 1 ) == '_' ) {
                        sbfPW.deleteCharAt( sbfPW.length() - 1 );
                    }
                    strPW = sbfPW.toString();
                }
                String strDate = sbfDPW.substring( 30 );
                if( strDate.length() == 6 ) {
                    String strDD = strDate.substring( 4 );
                    int iDD;
                    try {
                        iDD = Integer.parseInt( strDD );
                    } catch( NumberFormatException e ) {
                        iDD = -1;
                    }
                    if( iDD > 0 ) {
                        staRet = new String[2];
                        staRet[0] = strPW;
                        if( iDD > 31 ) {
                            staRet[1] = "";
                        } else {
                            staRet[1] = strDate;
                        }
                    }
                }
            }
        }
        return staRet;
    }
    
    private char getCheckSum( StringBuilder sbfIn ) {
        char cRet = '0';
        int iAcc = 0;
        for( int i = 0; i < sbfIn.length(); i++ ) {
            for( int j = 0; j < caAlphabet3.length; j++ ) {
                if( sbfIn.charAt( i ) == caAlphabet3[j] ) {
                    iAcc += j;
                    break;
                }
            }
        }
        iAcc %= caAlphabet3.length;
        cRet = caAlphabet2[iAcc];
        return cRet;
    }
}