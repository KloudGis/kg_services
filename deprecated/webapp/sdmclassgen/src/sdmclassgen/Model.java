/*
 * @author corneliu
 */
package sdmclassgen;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import org.jdom.Element;

/**
 * Class that corresponds to the whole model (the whole .sdm file)
 */
public class Model {
    
    private LinkedHashMap<String, FeatureType> lhmFeatureTypes = new LinkedHashMap<String, FeatureType>();
    
    /**
     * Constructor for this class. Parses the element received as parameter and extracts the list of feature types and their joins
     * @param eleRoot is the root element of the sdm file
     */
    public Model( Element eleRoot ) {
        List<Element> lstFT = eleRoot.getChildren( "FeatureType" );
        for( Element eleFT : lstFT ) {
            FeatureType ftp = new FeatureType( eleFT );
            lhmFeatureTypes.put( ftp.getFTName(), ftp );
        }
        join( lstFT );
    }
    
    /**
     * @return a map of feature type objects. The keys are the feature type names, the values are the feature types themselves.
     */
    public LinkedHashMap<String, FeatureType> getFeatureTypes() {
        return lhmFeatureTypes;
    }
    
    /**
     * Creates the .java files, one for each feature type.
     * @param strProjectPath is the path to the project where the java files will be created.
     * @throws IOException 
     */
    public void writeModel( String strProjectPath ) throws IOException {
        for( FeatureType ftp : lhmFeatureTypes.values() ) {
            ftp.writePojo( strProjectPath );
            ftp.writeEntity( strProjectPath );
        }
    }
    
    private void join( List<Element> lstFT ) {
        for( Element eleFT : lstFT ) {
            String strFTName = eleFT.getAttributeValue( "name" );
            List<Element> lstAttrs = eleFT.getChild( "Schema" ).getChildren( "AttributeType" );
            for( Element eleAttr : lstAttrs ) {
                String strClass = eleAttr.getAttributeValue( "class" );
                String strReverse = eleAttr.getAttributeValue( "reverse" );
                if( strClass != null && strClass.equals( "com.space.core.model.feature.attr.JoinType" ) &&
                        ( strReverse == null || strReverse.equals( "false" ) ) ) {
                    Join jn = new Join( strFTName, eleAttr );
                    if( jn.getAttr() != null && jn.getAttr().length() > 0 && jn.getToAttr() != null && jn.getToAttr().length() > 0 &&
                            jn.getFT() != null && jn.getFT().length() > 0 && jn.getToFT() != null && jn.getToFT().length() > 0 ) {
                        FeatureType ftp = lhmFeatureTypes.get( strFTName  );
                        if( ftp != null ) {//add the join to the parent
                            ftp.addJoin( jn );
                        }
                        FeatureType ftpTo = lhmFeatureTypes.get( jn.getToFT() );
                        if( ftpTo != null ) {//add the same join to the child
                            ftpTo.addJoin( jn );
                        }
                    }
                }
            }
        }
    }
}