/**
 * Background class
 * Patrick Zhang
 * 1/21/2023
 */
import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Background{
    private BufferedImage background;
    Background(int stage){
    try{
    background = ImageIO.read(new File("Game Misc Sprites/battlecatsback"+stage+".png")); // add different enemt towers 
    }catch(IOException ex){}
    }
    public void draw(Graphics g){
         g.drawImage(background, 0, -70, null);          
    }
}