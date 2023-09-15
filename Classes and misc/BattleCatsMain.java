/**
 * BattleCatsMain class
 * Patrick Zhang
 * 1/21/2023
 */
//Import all needed java classes
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class BattleCatsMain{
    //Game Objects and variables
    JFrame gameWindow;
    GamePanel gamePanel;   
    MyKeyListener keyListener; 
    BattleCatsMenu menu=new BattleCatsMenu();
    static Money cash=new Money(Const.STARTING_MONEY);;
    Tower catTower,enemyTower;
    static boolean inMenu=true;
    boolean unitDied=false;
    static int money;
    int stage,deathPosition,counter=0;
    static Map <Integer,String> spawningEnemiesMap = new HashMap<Integer,String>();
    ArrayList<Background> back= new ArrayList<Background>();
    ArrayList<Enemy> enemies= new ArrayList<Enemy>();
    static int[] totalCatPictures=new int[Const.TOTAL_ICONS];
    static LinkedList<CatIcons> catIcons= new LinkedList<CatIcons>();
    static ArrayList<Cats> spawnedCats= new ArrayList<Cats>();
    static long startTime,elapsedTime, unitsAttackTime, newElapsedTime, previousEnemyElapsedTime;
//------------------------------------------------------------------------------
    //instantiate game objects
    BattleCatsMain(){
        gameWindow = new JFrame("Game Window");
        gameWindow.setSize(Const.WIDTH,Const.HEIGHT);
        gameWindow.setResizable(false);        
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel = new GamePanel();
        gameWindow.add(gamePanel);   
        gamePanel.addMouseListener(new MyMouseListener()); 
        keyListener = new MyKeyListener();
        gamePanel.addKeyListener(keyListener);
        gameWindow.setVisible(true);    
    }
//------------------------------------------------------------------------------  
    //Main game loop
    public void run(){
        while (true) {
            gameWindow.repaint();
            if(!inMenu){
                unitsAttackTime=(System.currentTimeMillis()-startTime)/100;
                newElapsedTime=unitsAttackTime/10;
                //Check if time is different
                if(elapsedTime!=newElapsedTime){    
                    money=cash.addMoney(stage);
                    elapsedTime=newElapsedTime;
                    //Add the enemies set to spawn in this stage from the file
                    String [] enemiesToSpawn= spawningEnemiesMap.get(stage+1).split(",");
                    //Add the enemies 
                    if(elapsedTime%Const.SPAWNING_DELAY==0 && counter<=Const.MAX_ENEMY_SPAWNS && !inMenu){
                        enemies.add(new Enemy(Const.UNIT_X+5,Const.UNIT_Y,Const.ENTITY_ATTACK, false,Const.ENTITY_HEALTH,Integer.valueOf(enemiesToSpawn[counter])));
                        counter++;    
                    }
                }
            }else{
                //Fill Cat Icons with icons
                if(catIcons.size()<Const.TOTAL_ICONS){
                    for(int catIconX=0; catIconX<Const.TOTAL_ICONS; catIconX++){
                        catIcons.add(new CatIcons(catIconX));  
                    }
                }
                //Fill Background with backgrounds
                if(back.size()<Const.TOTAL_BACKGROUNDS){
                    for(int backgroundStage=0; backgroundStage<Const.TOTAL_BACKGROUNDS; backgroundStage++){
                        back.add(new Background(backgroundStage));  
                    }
                }
            }
            try  {Thread.sleep(Const.FRAME_PERIOD);} catch(Exception e){}
        }
        
    }  
//------------------------------------------------------------------------------  
    //act upon key events
    public class MyKeyListener implements KeyListener{   
        public void keyPressed(KeyEvent e){
            int key= e.getKeyCode();
            if(inMenu){
                if (key == KeyEvent.VK_LEFT){
                    menu.moveLeft();
                    if(stage>(Const.TOTAL_BACKGROUNDS-7)){
                        stage--;
                    }
                } else if (key == KeyEvent.VK_RIGHT){
                    menu.moveRight();
                    if(stage<Const.TOTAL_BACKGROUNDS-1){
                        stage++;
                    }
                } else if (key == KeyEvent.VK_ENTER){
                    //Enter a stage, create necessary objects
                    inMenu=false;
                    catTower= new Tower();
                    enemyTower= new Tower();
                    startTime=System.currentTimeMillis();
                    long enemyAttackTime=System.currentTimeMillis();
                    enemies.clear();
                    spawnedCats.clear();
                }
            }if (key == KeyEvent.VK_ESCAPE){
                //Exiting a stage
                inMenu=true;
                unitDied=false;
                catTower.resetHealth();
                enemyTower.resetHealth();
                counter=0;
                cash.setMoney(0);
            }
        }
        public void keyReleased(KeyEvent e){ 
        }   
        public void keyTyped(KeyEvent e){
        }    
    }
    //------------------------------------------------------------------------------
    //Act upon mouse events
    static class MyMouseListener implements MouseListener{
        public void mouseClicked(MouseEvent e){
        }  
        
        public void mousePressed(MouseEvent e){
            int mouseX = e.getX();
            int mouseY = e.getY();
            //Check which unit spawned and insert it into the arraylist 
            if(!inMenu && spawnedCats.size()<10){
                int catSpawned=catIcons.get(0).unitSelected(mouseX,mouseY);
                if(catSpawned>=0 && money>=100*catSpawned){
                    cash.subtractMoney(100*catSpawned);
                    money-=100*catSpawned;
                    spawnedCats.add(new Cats(Const.UNIT_X*10,Const.UNIT_Y,Const.ENTITY_ATTACK,false,catSpawned, Const.ENTITY_HEALTH+100,totalCatPictures[catSpawned-1]));
                }
            }
        }
        public void mouseReleased(MouseEvent e){
        }
        public void mouseEntered(MouseEvent e){
        }
        public void mouseExited(MouseEvent e){
        }
        
    } // MyMouseListener class end
    
//------------------------------------------------------------------------------
    public class GamePanel extends JPanel{
        GamePanel(){
            setFocusable(true);
            requestFocusInWindow();
        }
        
        @Override
        public void paintComponent(Graphics g){ 
            super.paintComponent(g); //required
            g.setFont(new Font("Ink Free",Font.BOLD,42));
            g.setColor(Color.RED);
            //While in menu, draw menu
            if(inMenu){
                menu.draw(g);
            } else{
                //Draw background and icons
                back.get(stage).draw(g);
                for(int icons=0; icons<Const.TOTAL_ICONS; icons++){     
                    catIcons.get(icons).draw(g);
                }
                if(catTower.loseTower()){
                    catTower.draw(g);
                }else if( enemyTower.loseTower()){
                    enemyTower.drawWin(g);
                }else{
                    //Draw money and health of towers
                    g.drawString("$"+money,Const.MONEY_X,Const.MONEY_Y);             
                    g.drawString("HP:"+catTower.getHealth(),Const.CAT_TOWER_POSITION,200);
                    g.drawString("HP:"+enemyTower.getHealth(),Const.ENEMY_TOWER_POSITION,230);
                    boolean isDifferentTime=false;
                    //Variable to keep track of time changes
                    if(unitsAttackTime!=previousEnemyElapsedTime){
                        isDifferentTime=true;
                    }else{
                        isDifferentTime=false;
                    }
                    //Draw the death symbol if unit died
                    if(deathPosition>0 && unitDied && spawnedCats.size()>0)
                        spawnedCats.get(0).drawDeath(g,deathPosition);              
                    if(elapsedTime%5==0){
                        deathPosition=Const.DEATH_X;
                    }
                    // ---------------------------------------------------------------------//
                    //SPAWNING CATS                
                    for(int row=0; row<spawnedCats.size(); row++){
                        spawnedCats.get(row).draw(g);
                        //Swap cat positions if a cat is in front of the current first cat
                        if(spawnedCats.get(row).getPosition()<spawnedCats.get(0).getPosition()){
                            Collections.swap(spawnedCats,0,row);
                        }
                        if(spawnedCats.get(row).isAlive()){
                            if(spawnedCats.get(row).checkAttack()){
                                if(unitsAttackTime%2==0 && isDifferentTime){
                                    spawnedCats.get(row).attack();  
                                }
                            }else{
                                //Move left if not attacking
                                spawnedCats.get(row).moveLeft();
                            }
                            //Check if hitting an enemy
                            if(!enemies.isEmpty()){
                                if(spawnedCats.get(row).hitEnemy(enemies.get(0).getPosition())){
                                    if(spawnedCats.get(row).isAttacking() && isDifferentTime){
                                        //Hit the first enemy 
                                        enemies.get(0).getHit(spawnedCats.get(row).getAttack());
                                    }
                                    spawnedCats.get(row).shouldAttack(true);
                                }
                            }else{                           
                                spawnedCats.get(row).shouldAttack(false);
                            }
                            //Hit the enemy tower
                            if(spawnedCats.get(row).getPosition()-spawnedCats.get(row).getAttackDistance()<Const.ENEMY_TOWER_POSITION){
                                if(spawnedCats.get(row).isAttacking() && isDifferentTime)
                                    enemyTower.hitTower(spawnedCats.get(row).getAttack());
                                spawnedCats.get(row).shouldAttack(true);
                            } 
                        }else{
                            //Cat death
                            deathPosition=spawnedCats.get(0).getPosition();
                            spawnedCats.remove(0);
                            unitDied=true;
                            //Set all enemies to not attack
                            for(int i=0; i<enemies.size(); i++){
                                enemies.get(row).shouldAttack(false);
                            }            
                        }
                    }
// ---------------------------------------------------------------------//
                    //ENEMIES  
                    
                    
                    for(int row=0; row<enemies.size(); row++){
                        enemies.get(row).draw(g);
                        //Swaping enemies if one is ahead of the other
                        if(enemies.get(row).getPosition()>enemies.get(0).getPosition()){
                            Collections.swap(enemies,0,row);
                        }
                        if(enemies.get(row).isAlive()){
                            if(enemies.get(row).checkAttack()){
                                if(unitsAttackTime%2==0 && isDifferentTime){
                                    enemies.get(row).attack();  
                                }
                            }else{
                                enemies.get(row).moveRight();
                            }
                            if(!spawnedCats.isEmpty()){
                                if(enemies.get(row).hitCat(spawnedCats.get(0).getPosition())){
                                    if(enemies.get(row).isAttacking() && isDifferentTime){
                                        spawnedCats.get(0).getHit(enemies.get(row).getAttack());
                                    }
                                    enemies.get(row).shouldAttack(true);
                                }
                            }else{                           
                                enemies.get(row).shouldAttack(false);    
                            }
                            //Hitting the cat tower
                            if(enemies.get(row).getPosition()+enemies.get(row).getAttackDistance()>Const.CAT_TOWER_POSITION){
                                if(enemies.get(row).isAttacking() && isDifferentTime)
                                    catTower.hitTower(enemies.get(row).getAttack());
                                enemies.get(row).shouldAttack(true);
                            }
                        }else{
                            //Enemy death
                            deathPosition=enemies.get(0).getPosition();
                            enemies.remove(0);
                            unitDied=true;
                            for(int i=0; i<spawnedCats.size(); i++){
                                spawnedCats.get(i).shouldAttack(false);
                            }
                            counter--;
                        }
                    }
                    previousEnemyElapsedTime=unitsAttackTime;
                }
            }
        }    
    }    
//------------------------------------------------------------------------------
    public static void main(String[] args) throws IOException{
        BattleCatsMain demo = new BattleCatsMain();
        //Read photos and enemies from files
        File allyPictures= new File("totalCatPictures.txt");
        File enemySpawning= new File("EnemySpawns.txt");
        enemySpawning.createNewFile();
        allyPictures.createNewFile();
        Scanner enemySpawningFile=new Scanner(enemySpawning);
        Scanner allyPicturesFile = new Scanner(allyPictures);
        for(int next=1; enemySpawningFile.hasNextLine(); next++){
            spawningEnemiesMap.put(next,enemySpawningFile.nextLine());
        }
        for(int next=0; allyPicturesFile.hasNextLine(); next++){
            totalCatPictures[next]=Integer.valueOf(allyPicturesFile.nextLine());
        }
        
        demo.run();
    }    
}