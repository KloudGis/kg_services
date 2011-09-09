/*
 * @author corneliu
 */
package sdmclassgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Class for generating the ant script for building the newly generated project.
 */
public class Ant {
    
    /**
     * Creates the ant script for building the newly generated project.
     */
    public void writeAntXML( String strPath ) throws FileNotFoundException, IOException {
        Format frm = Format.getPrettyFormat();
        frm.setEncoding( "UTF-8" );
        XMLOutputter outputter = new XMLOutputter( frm );
        FileOutputStream fos = null;
        File file = new File( strPath + "/build.xml" );
        fos = new FileOutputStream( file );
        Element eleRoot = new Element( "project" );
        Element eleClean = new Element( "target" );
        eleClean.setAttribute( "name", "clean" );
        Element eleDelBld = new Element( "delete" );
        eleDelBld.setAttribute( "dir", "build" );
        eleClean.addContent( eleDelBld );
        Element eleDelDist = new Element( "delete" );
        eleDelDist.setAttribute( "dir", "dist" );
        eleClean.addContent( eleDelDist );
        eleRoot.addContent( eleClean );
        Element eleCompile = new Element( "target" );
        eleCompile.setAttribute( "name", "compile" );
        Element eleCompMkDir = new Element( "mkdir" );
        eleCompMkDir.setAttribute( "dir", "build/classes" );
        eleCompile.addContent( eleCompMkDir );
        Element eleJavac = new Element( "javac" );
        eleJavac.setAttribute( "srcdir", "src" );
        eleJavac.setAttribute( "destdir", "build/classes" );
        Element eleClasspath = new Element( "classpath" );
        Element eleFileset = new Element( "fileset" );
        eleFileset.setAttribute( "dir", "lib" );
        Element eleInclude = new Element( "include" );
        eleInclude.setAttribute( "name", "*.jar" );
        eleFileset.addContent( eleInclude );
        eleClasspath.addContent( eleFileset );
        eleJavac.addContent( eleClasspath );
        eleCompile.addContent( eleJavac );
        eleRoot.addContent( eleCompile );
        Element eleTargetJar = new Element( "target" );
        eleTargetJar.setAttribute( "name", "jar" );
        Element eleJarMkDir = new Element( "mkdir" );
        eleJarMkDir.setAttribute( "dir", "dist" );
        eleTargetJar.addContent( eleJarMkDir );
        Element eleJar = new Element( "jar" );
        eleJar.setAttribute( "destfile", "dist/" + new File( strPath ).getName() + ".jar" );
        eleJar.setAttribute( "basedir", "build/classes" );
        Element eleManifest = new Element( "manifest" );
        Element eleAttribute = new Element( "attribute" );
        eleAttribute.setAttribute( "name", "Main-Class" );
        eleAttribute.setAttribute( "value", "org.kloudgis.model.Main" );
        eleManifest.addContent( eleAttribute );
        eleJar.addContent( eleManifest );
        eleTargetJar.addContent( eleJar );
        eleRoot.addContent( eleTargetJar );
        outputter.output( new Document( eleRoot ), fos );
        fos.close();
    }
}