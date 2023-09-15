/**
 *CatIcons class
 * Patrick Zhang
 * 1/21/2023
 */
import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
public class CatIcons {
    BufferedImage catIcon;
    private int x=25;
    private int y=700;
    private int unitYDownBoundary=850;
    private int unitYUpBoundary=700;
    CatIcons(int rows){   
        this.x=rows*250;
        try{
                catIcon=ImageIO.read(new File("Game Misc Sprites/caticon"+rows+".png"));
        }catch (IOException ex){}
    }
    public void draw(Graphics g){
            g.drawImage(catIcon, this.x, this.y, null);     
    }
    public int unitSelected(int mouseX, int mouseY){
        //Check and return which unit has been selected
        if (mouseY<=unitYDownBoundary && mouseY>=unitYUpBoundary){
            for( int i=0; i<5; i++){
                if(mouseX>=250*i && mouseX<=250*i+180){
                   return i+1;
                }
            }
        }
        return -1;
    }
}