/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kloudgis.data.store;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;
import org.postgresql.util.Base64;


/**
 *
 * @author jeanfelixg
 */
public class PictureEntity implements Serializable {

    private byte[] imageBytes;
    
    public PictureEntity(){}
    
    public PictureEntity(String base64){
        this.setAsBase64(base64);
    }

    public BufferedImage getImage() throws IOException {
        if (imageBytes == null) {
            return null;
        }
        InputStream in = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(in);
    }

    public void setImage(BufferedImage image) throws IOException {
        if (image == null) {
            imageBytes = null;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG" /* for instance */, out);
            imageBytes = out.toByteArray();
        }
    }
    
    public String getAsBase64(){
        if(imageBytes == null){
            return null;
        }
        return Base64.encodeBytes(imageBytes);
    }
    
    public final void setAsBase64(String base64Image){
        if(base64Image == null){
            imageBytes = null;
        }
        imageBytes = Base64.decode(base64Image);
    }
}
