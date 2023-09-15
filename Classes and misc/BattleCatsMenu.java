/**
 * BattleCatsMenu class
 * Patrick Zhang
 * 1/21/2023
 */
import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
public class BattleCatsMenu{
    private int menuX=385;
    private int menuY=425;
    private int menuPosition=1;
    private int menuStep=180;
    private BufferedImage menu;
    private BufferedImage character;
    BattleCatsMenu(){
        try{
            menu=ImageIO.read(new File("Game Misc Sprites/battlecatsmenu7.png"));
        }catch (IOException ex){}
        try{
            character=ImageIO.read(new File("Game Misc Sprites/Doge1.png"));
        }catch (IOException ex){}
    }
    public void draw(Graphics g){
        g.drawImage(menu, 0, -70, null);    
        g.drawImage(character, menuX,menuY, null); 
    }
    public void moveLeft(){
        if(menuPosition>1){        
            this.menuX-=menuStep;
                menuPosition--;    
        }
    }
    public void moveRight(){
        if(menuPosition<7){
            this.menuX+=menuStep;
            menuPosition++;
        }
    }
    
    //Get the stage number1
    
}