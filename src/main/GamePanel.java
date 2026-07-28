package main;
import ai.PathFinder;
import entity.Entity;
import entity.Player;
import environment.EnvironmentManager;
import tile.Map;
import tile.TileManager;
import tile_interactive.InteractiveTile;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable{
    //CONFIGURACIONES DE PANTALLA
    final int originalTileSize=16;
    final int scale=3;
    public final int tileSize=originalTileSize*scale; //para resoluciones grandes

    public final int maxScreenCol=20;
    public final int maxScreenRow=12;
    public final int screenWidth=tileSize*maxScreenCol; //960 pixeles
    public final int screenHeight=tileSize*maxScreenRow; //576 pixeles

    //CONFIGURACIONES DEL MUNDO
    public final int maxWorldCol=50;
    public final int maxWorldRow=50;
    public final int maxMap=10;
    public int currentMap=0;

    //PARA PANTALLA COMPLETO
    int screenWidth2=screenWidth;
    int screenHeight2=screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean fullScreenOn=false;

    //FPS
    int FPS=60;

    //SISTEMA
    public TileManager tileM=new TileManager(this);
    public KeyHandler keyH=new KeyHandler(this);
    Sound music=new Sound();
    Sound se=new Sound();
    public CollisionChecker cChecker=new CollisionChecker(this);
    public AssetSetter aSetter=new AssetSetter(this);
    public UI ui=new UI(this);
    public EventHandler eHandler=new EventHandler(this);
    Config config=new Config(this);
    public PathFinder pFinder=new PathFinder(this);
    EnvironmentManager eManager=new EnvironmentManager(this);
    Map map=new Map(this);
    Thread gameThread;

    //ENTIDADES Y JUGADOR
    public Player player=new Player(this,keyH);
    public Entity obj[][]=new Entity[maxMap][20];
    public Entity npc[][]=new Entity[maxMap][10];
    public Entity monster[][]=new Entity[maxMap][20];
    public InteractiveTile iTile[][]=new InteractiveTile[maxMap][50];
    public Entity projectile[][]=new Entity[maxMap][20];
    public ArrayList<Entity> particleList=new ArrayList<>();
    ArrayList<Entity> entityList=new ArrayList<>();

    //ESTADOS DE JUEGO
    public int gameState;
    public int titleState=0;
    public final int playState=1;
    public final int pauseState=2;
    public final int dialogueState=3;
    public final int characterState=4;
    public final int optionsState=5;
    public final int gameOverState=6;
    public final int transitionState=7;
    public final int tradeState=8;
    public final int sleepState=9;
    public final int mapState=10;

    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true); //mejor renderizado de juego
        this.addKeyListener(keyH);
        this.setFocusable(true); //para que este pendiente de las teclas
    }

    public void setUpGame(){
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setMonster();
        aSetter.setInteractiveTile();
        eManager.setup();
        gameState=titleState;

        tempScreen=new BufferedImage(screenWidth,screenHeight,BufferedImage.TYPE_INT_ARGB);
        g2=(Graphics2D)tempScreen.getGraphics();

        if(fullScreenOn) setFullScreen();
    }

    public void resetGame(boolean restart){
        player.setDefaultPositions();
        player.restoreStatus();
        aSetter.setNPC();
        aSetter.setMonster();

        if(restart){
            player.setDefaultValues();
            aSetter.setObject();
            aSetter.setInteractiveTile();
            eManager.lighting.resetDay();
        }
    }

    public void setFullScreen(){
        //OBTENER EL TAMAÑO DE LA PANTALLA DEL DISPOSITIVO
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd=ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        //OBTENER MEDIDAS DE LA PANTALLA COMPLETA
        screenWidth2=Main.window.getWidth();
        screenHeight2=Main.window.getHeight();
    }

    public void startGameThread(){
        gameThread=new Thread(this);
        gameThread.start();
    }

    //METODO DELTA/ACCUMULATOR
    @Override
    public void run(){
        double drawInterval=1000000000/FPS;
        double delta=0;
        long lastTime=System.nanoTime();
        long currentTime;
        long timer=0;
        long drawCount=0;

        while(gameThread!=null){
            currentTime=System.nanoTime();
            delta+=(currentTime-lastTime)/drawInterval;
            timer+=(currentTime-lastTime);
            lastTime=currentTime;

            if(delta>=1){
                update();
                drawToTempScreen(); //dibujar todo a la imagen temporal
                drawToScreen(); //dibujar la imagen a la pantalla
                delta--;
                drawCount++;
            }
            if(timer>=1000000000){
                drawCount=0;
                timer=0;
            }
        }
    }

    public void update(){
        if(gameState==playState){
            //JUGADOR
            player.update();

            //NPC
            for(int i=0;i<npc[1].length;i++){
                if(npc[currentMap][i]!=null){
                    npc[currentMap][i].update();
                }
            }

            //MOUNSTRUOS
            for(int i=0;i<monster[1].length;i++){
                if(monster[currentMap][i]!=null){
                    if(monster[currentMap][i].alive && !monster[currentMap][i].dying){
                        monster[currentMap][i].update();
                    }
                    if(!monster[currentMap][i].alive){
                        monster[currentMap][i].checkDrop();
                        monster[currentMap][i]=null;
                    }
                }
            }

            //PROYECTILES
            for(int i=0;i<projectile[1].length;i++){
                if(projectile[currentMap][i]!=null){
                    if(projectile[currentMap][i].alive){
                        projectile[currentMap][i].update();
                    }
                    if(!projectile[currentMap][i].alive){
                        projectile[currentMap][i]=null;
                    }
                }
            }

            //PARTICULAS
            for(int i=0;i<particleList.size();i++){
                if(particleList.get(i)!=null){
                    if(particleList.get(i).alive){
                        particleList.get(i).update();
                    }
                    if(!particleList.get(i).alive){
                        particleList.remove(i);
                    }
                }
            }

            //BLOQUES CON INTERACCION
            for(int i=0;i<iTile[1].length;i++){
                if(iTile[currentMap][i]!=null){
                    iTile[currentMap][i].update();
                }
            }
            eManager.update();
        }
        if(gameState==pauseState){
            //nada por ahora
        }
    }

    public void drawToTempScreen(){
        //DEBUG
        long drawStart=0;
        if(keyH.showDebugText) drawStart=System.nanoTime();

        //PANTALLA DE TITULO
        if(gameState==titleState){
            ui.draw(g2);
        }else if(gameState==mapState){
            map.drawFullMapScreen(g2);
        }else{
            //BLOQUES
            tileM.draw(g2);

            //BLOQUES CON INTERACCION
            for(int i=0;i<iTile[1].length;i++){
                if(iTile[currentMap][i]!=null){
                    iTile[currentMap][i].draw(g2);
                }
            }

            //AGREGAR TODO A LA LISTA DE ENTIDADES
            //jugador
            entityList.add(player);

            //npcs
            for(int i=0;i<npc[1].length;i++){
                if(npc[currentMap][i]!=null){
                    entityList.add(npc[currentMap][i]);
                }
            }

            //objetos
            for(int i=0;i<obj[1].length;i++){
                if(obj[currentMap][i]!=null){
                    entityList.add(obj[currentMap][i]);
                }
            }

            //mounstruos
            for(int i=0;i<monster[1].length;i++){
                if(monster[currentMap][i]!=null){
                    entityList.add(monster[currentMap][i]);
                }
            }

            //proyectiles
            for(int i=0;i<projectile[1].length;i++){
                if(projectile[currentMap][i]!=null){
                    entityList.add(projectile[currentMap][i]);
                }
            }

            //particulas
            for(int i=0;i<particleList.size();i++){
                if(particleList.get(i)!=null){
                    entityList.add(particleList.get(i));
                }
            }

            //ORDENAR LA LISTA DE ENTIDADES
            Collections.sort(entityList,new Comparator<Entity>(){
                @Override
                public int compare(Entity e1, Entity e2){
                    int result=Integer.compare(e1.worldY,e2.worldY);
                    return result;
                }
            });

            //DIBUJAR TODAS LAS ENTIDADES DE LA LISTA
            for(int i=0;i<entityList.size();i++){
                entityList.get(i).draw(g2);
            }

            //VACIAR LA LISTA DE ENTIDADES
            entityList.clear();

            //AMBIENTE
            eManager.draw(g2);

            //MINI MAPA
            map.drawMiniMap(g2);

            //UI
            ui.draw(g2);
        }

        //DEBUG
        if(keyH.showDebugText){
            long drawEnd=System.nanoTime();
            long passed=drawEnd-drawStart;

            g2.setFont(new Font("Arial",Font.PLAIN,20));
            g2.setColor(Color.WHITE);
            int x=10;
            int y=400;
            int lineHeight=20;

            g2.drawString("WorldX: "+player.worldX,x,y); y+=lineHeight;
            g2.drawString("WorldY: "+player.worldY,x,y); y+=lineHeight;
            g2.drawString("Col: "+(player.worldX+player.solidArea.x)/tileSize,x,y); y+=lineHeight;
            g2.drawString("Row: "+(player.worldY+player.solidArea.y)/tileSize,x,y); y+=lineHeight;
            g2.drawString("Tiempo de dibujado: "+passed,x,y);
        }
    }

    public void drawToScreen(){
        Graphics g=getGraphics();
        g.drawImage(tempScreen,0,0,screenWidth2,screenHeight2,null);
        g.dispose();
    }

    public void playMusic(int i){
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic(){
        music.stop();
    }

    public void playSE(int i){
        se.setFile(i);
        se.play();
    }
}