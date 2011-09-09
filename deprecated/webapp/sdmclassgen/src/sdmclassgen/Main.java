/*
 * @author corneliu
 */
package sdmclassgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Main class. Creates a Main class for feedback purposes and manages the project generation process and the feedback display.
 */
public class Main {
    
    private static String strSDMPath;
    private static String strSDSPath;
    private static String strProjectPath;
    private static String strFeatureType;//feedback display (default random)
    private static int iLimit = -1;//number of rows to be displayed (default all rows)
    
    /**
     * Constructor for this class.
     */
    public Main() {}
    
    /**
     * Constructor for this class.
     * @param strProjectPath is the path for the new project
     * @param strSDMPath is the path of the sdm file
     */
    public Main( String strProjectPath, String strSDMPath ) {
        Main.strSDMPath = strSDMPath;
        Main.strProjectPath = strProjectPath;
    }
    
    /**
     * Constructor for this class.
     * @param strProjectPath is the path for the new project
     * @param strSDMPath is the path of the sdm file
     * @param strSDSPath is the path for the sds file
     * @param strFeatureType is the name of the feature type that will be used for feedback
     * @param iLimit is the number of rows to be displayed
     */
    public Main( String strProjectPath, String strSDMPath, String strSDSPath, String strFeatureType, int iLimit ) {
        this( strProjectPath, strSDMPath );
        Main.strSDSPath = strSDSPath;
        Main.strFeatureType = strFeatureType;
        Main.iLimit = iLimit;
    }
    
    public static void main( String[] args ) throws ClassNotFoundException, MalformedURLException, NoSuchMethodException, InstantiationException, 
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, JDOMException, IOException, InterruptedException {
        int iLength = args.length;
        for( int i = 0; i < iLength; i++ ) {
            if( args[i].equals( "-p" ) && i + 1 < iLength ) {
                strProjectPath = args[i + 1];
            } else if( args[i].equals( "-m" ) && i + 1 < iLength ) {
                strSDMPath = args[i + 1];
            } else if( args[i].equals( "-d" ) && i + 1 < iLength ) {
                strSDSPath = args[i + 1];
            } else if( args[i].equals( "-f" ) && i + 1 < iLength ) {
                strFeatureType = args[i + 1];
            } else if( args[i].equals( "-l" ) && i + 1 < iLength ) {
                iLimit = Integer.parseInt( args[i + 1] );
            }
        }
        Scanner scn = new Scanner( System.in );
        while( strProjectPath == null ) {
            System.out.print( "Please enter the project path: " );
            strProjectPath = scn.nextLine();
        }
        while( strSDMPath == null ) {
            System.out.print( "Please enter the model file path: " );
            strSDMPath = scn.nextLine();
        }
        if( strSDSPath == null ) {
            System.out.print( "Please enter the datastore file path for feedback, otherwise leave empty: " );
            strSDSPath = scn.nextLine();
        }
        Main main = new Main();
        main.generateProject();
        main.displayResultSet();
    }

    /**
     * Creates the new project.
     * @throws JDOMException
     * @throws IOException 
     */
    public void generateProject() throws JDOMException, IOException, InterruptedException {
        SAXBuilder sax = new SAXBuilder();
        if( strSDMPath.endsWith( "/" ) ) {//make sure that path does not end with a slash
            strSDMPath = strSDMPath.substring( 0, strSDMPath.lastIndexOf( "/" ) );
        }
        Element eleMdl = ( ( Document )sax.build( new File( strSDMPath ) ) ).getRootElement();
        Model mdl = new Model( eleMdl );
        if( strProjectPath.endsWith( "/" ) ) {//make sure that path does not end with a slash
            strProjectPath = strProjectPath.substring( 0, strProjectPath.lastIndexOf( "/" ) );
        }
        new File( strProjectPath + "/src/org/kloudgis/model/pojos/" ).mkdirs();//make sure that path exists
        new File( strProjectPath + "/src/org/kloudgis/model/entities/" ).mkdirs();//make sure that path exists
        mdl.writeModel( strProjectPath + "/src/org/kloudgis/model" );//write the classes (pojos and entities)
        LinkedHashMap<String, FeatureType> lhmFT = mdl.getFeatureTypes();
        String[] sarFT = lhmFT.keySet().toArray( new String[lhmFT.size()] );
        if( strFeatureType == null ) {//if the feature type is null select a random one
            strFeatureType = getRandomFT( sarFT );
        }
        generateMain( lhmFT );//create a Main class in the new project so we can query the db
        if( strSDSPath != null && strSDSPath.length() > 0 ) {
            if( strSDSPath.endsWith( "/" ) ) {//make sure that path does not end with a slash
                strSDSPath = strSDSPath.substring( 0, strSDSPath.lastIndexOf( "/" ) );
            }
            Element eleDS = ( ( Document )sax.build( new File( strSDSPath ) ) ).getRootElement();
            DataStore dst = new DataStore( eleDS );
            dst.writeHibernateCfgXml( strProjectPath );//write the db config xml that contains the connection parameters
        }
        Utils.copyFile( new File( strProjectPath + "/src/hibernate.cfg.xml" ), "src/hibernate.cfg.xml" );//copy the db config to this project to enable it to query the db
        new Ant().writeAntXML( strProjectPath );//write the ant script
        File folderLibs = new File( "gen_libs" );
        File[] farJars = folderLibs.listFiles();
        for( File fileJar : farJars ) {//copy the jars to the lib folder of the new project so it can compile
            Utils.copyFile( fileJar, strProjectPath + "/lib/" + fileJar.getName() );
        }
        Process prc = Runtime.getRuntime().exec( "ant clean compile jar", null, new File( strProjectPath ) );
        prc.waitFor();
    }
    
    /**
     * Loads the new jar and uses it to query the db and display rows.
     * @throws ClassNotFoundException
     * @throws MalformedURLException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    public void displayResultSet() throws ClassNotFoundException, MalformedURLException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if( strSDSPath != null && strSDSPath.length() > 0 ) {
//            load jar
            URL url = new File( strProjectPath + "/dist/" + new File( strProjectPath ).getName() + ".jar" ).toURI().toURL();
            Class urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod( "addURL", new Class[]{ URL.class } );
            method.setAccessible( true );
            method.invoke( ClassLoader.getSystemClassLoader(), new Object[]{ url } );
//            load class
            Class classToLoad = Class.forName( "org.kloudgis.model.Main" );
            Method mth = classToLoad.getDeclaredMethod( "displayResultSet" );
            Object instance = classToLoad.newInstance();
            mth.invoke( instance );
        }
    }
    
    private void generateMain( LinkedHashMap<String, FeatureType> lhmFT ) throws IOException {
        FileWriter wrt = new FileWriter( strProjectPath + "/src/org/kloudgis/model/Main.java" );
        wrt.write( "/*Class generated automatically*/\npackage org.kloudgis.model;\n\n" );
        wrt.write( "import java.util.List;\nimport org.hibernate.Query;\nimport org.hibernate.Session;\n" );
        wrt.write( "import org.hibernate.Transaction;\nimport org.hibernate.cfg.AnnotationConfiguration;\nimport org.kloudgis.model.entities.*" );
        wrt.write( ";\n\npublic class Main {\n\n\tpublic void displayResultSet() {\n" );
        wrt.write( "\t\tSession newSession = new AnnotationConfiguration().addPackage( \"org.kloudgis.model.entities\" )\n" );
        for( FeatureType ftp : lhmFT.values() ) {
            wrt.write( "\t\t.addAnnotatedClass( " );
            wrt.write( ftp.getName() );
            wrt.write( "DbEntity.class )\n" );
        }
        wrt.write( "\t\t.configure().buildSessionFactory().openSession();\n" );
        wrt.write( "\t\tTransaction newTransaction = newSession.beginTransaction();\n\t\tQuery q = newSession.createQuery( \"select " );
        FeatureType ftp = lhmFT.get( strFeatureType );
        if( ftp != null ) {
            Collection<Attribute> colAttrNames = ftp.getAttrs().values();
            StringBuilder stb = new StringBuilder();
            for( Attribute att : colAttrNames ) {
                if( att.getJavaClass() != null ) {
                    stb.append( att.getName() );
                    stb.append( ", " );
                }
            }
            stb.replace( stb.length() - 2, stb.length(), " " );
            wrt.write( stb.toString() );
            wrt.write( "from " );
            wrt.write( ftp.getName() );
            wrt.write( "DbEntity\" );\n\t\tList<Object[]> lst = q.list();\n\t\t" );
            if( iLimit <= 0 ) {
                wrt.write( "System.out.println( \"Displaying all rows for feature type: " ); 
                wrt.write( strFeatureType  );
                wrt.write( "\" );\n\t\tfor( Object[] oba : lst ) {\n" );
            } else {
                wrt.write( "int iSize = " + iLimit );
                wrt.write( " > lst.size() ? lst.size() : " + iLimit );
                wrt.write( ";\n\t\tSystem.out.println( \"Displaying " );
                wrt.write( iLimit + " row(s) for feature type: " );
                wrt.write( strFeatureType );
                wrt.write( "\" );\n\t\tfor( int i = 0; i < iSize; i++ ) {\n\t\t\tObject[] oba = lst.get( i );\n" );
            }
            wrt.write( "\t\t\tfor( Object obj : oba ) {\n\t\t\t\tif( obj == null ) {\n\t\t\t\t\tSystem.out.print( \"null\\t\" );\n\t\t\t\t}" );
            wrt.write( " else {\n\t\t\t\t\tSystem.out.print( obj.toString() + \"\\t\" );\n\t\t\t\t}" );
            wrt.write( "\n\t\t\t}\n\t\t\tSystem.out.println( \"\" );\n\t\t}\n\t\tnewTransaction.commit();\n\t\tnewSession.close();\n\t}\n}" );
        }
        wrt.flush();
        wrt.close();
    }
    
    private String getRandomFT( String[] sarFT ) {
        return sarFT[ new Random().nextInt( sarFT.length ) ];
    }
}