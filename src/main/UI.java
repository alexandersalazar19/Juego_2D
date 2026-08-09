package main;
import entity.Entity;
import object.OBJ_Coin_Bronze;
import object.OBJ_Heart;
import object.OBJ_ManaCrystal;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UI{
    GamePanel gp;
    Graphics2D g2;
    public Font maruMonica,purisaB;
    BufferedImage heart_full,heart_half,heart_blank,crystal_full,crystal_blank,coin;
    public boolean messageOn=false;
    ArrayList<String> message=new ArrayList<>();
    ArrayList<Integer> messageCounter=new ArrayList<>();
    public boolean gameFinished=false;
    public String currentDialogue="";
    public int commandNum=0;
    public int playerSlotCol=0;
    public int playerSlotRow=0;
    public int npcSlotCol=0;
    public int npcSlotRow=0;
    int subState=0;
    int counter=0;
    public Entity npc;
    int charIndex=0;
    String combinedText="";

    public UI(GamePanel gp){
        this.gp=gp;
        try{
            InputStream is=getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            maruMonica=Font.createFont(Font.TRUETYPE_FONT,is);
            is=getClass().getResourceAsStream("/font/Purisa Bold.ttf");
            purisaB=Font.createFont(Font.TRUETYPE_FONT,is);
        }catch(FontFormatException | IOException e){
            e.printStackTrace();
        }

        //OBJETOS DEL HUD
        Entity heart=new OBJ_Heart(gp);
        heart_full=heart.image;
        heart_half=heart.image2;
        heart_blank=heart.image3;
        Entity crystal=new OBJ_ManaCrystal(gp);
        crystal_full=crystal.image;
        crystal_blank=crystal.image2;
        Entity bronzeCoin=new OBJ_Coin_Bronze(gp);
        coin=bronzeCoin.down1;
    }

    public void addMessage(String text){
        message.add(text);
        messageCounter.add(0);
    }

    public void draw(Graphics2D g2){
        this.g2=g2;
        g2.setFont(maruMonica);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        if(gp.gameState==gp.titleState){
            drawTitleScreen();
        }
        if(gp.gameState==gp.playState){
            drawPlayerLife();
            drawMonsterLife();
            drawMessage();
        }
        if(gp.gameState==gp.pauseState){
            drawPlayerLife();
            drawPauseScreen();
        }
        if(gp.gameState==gp.dialogueState){
            drawDialogueScreen();
        }
        if(gp.gameState==gp.characterState){
            drawCharacterScreen();
            drawInventory(gp.player,true);
        }
        if(gp.gameState==gp.optionsState){
            drawOptionsScreen();
        }
        if(gp.gameState==gp.gameOverState){
            drawGameOverScreen();
        }
        if(gp.gameState==gp.transitionState){
            drawTransition();
        }
        if(gp.gameState==gp.tradeState){
            drawTradeScreen();
        }
        if(gp.gameState==gp.sleepState){
            drawSleepScreen();
        }
    }

    public void drawPlayerLife(){
        int x=gp.tileSize/2;
        int y=gp.tileSize/2;
        int i=0;
        int iconSize=32;
        int manaStartX=(gp.tileSize/2)-5;
        int manaStartY=0;
        //VIDA MAXIMA
        while(i<gp.player.maxLife/2){
            g2.drawImage(heart_blank,x,y,iconSize,iconSize,null);
            i++;
            x+=iconSize;
            manaStartY=y+32;
            if(i%8==0){
                x=gp.tileSize/2;
                y+=iconSize;
            }
        }

        x=gp.tileSize/2;
        y=gp.tileSize/2;
        i=0;
        //VIDA ACTUAL
        while(i<gp.player.life){
            g2.drawImage(heart_half,x,y,iconSize,iconSize,null);
            i++;
            if(i<gp.player.life){
                g2.drawImage(heart_full,x,y,iconSize,iconSize,null);
            }
            i++;
            x+=iconSize;
        }

        //MANA MAXIMO
        x=manaStartX;
        y=manaStartY;
        i=0;
        while(i<gp.player.maxMana){
            g2.drawImage(crystal_blank,x,y,iconSize,iconSize,null);
            i++;
            x+=iconSize-10;
        }
        //MANA ACTUAL
        x=manaStartX;
        y=manaStartY;
        i=0;
        while(i<gp.player.mana){
            g2.drawImage(crystal_full,x,y,iconSize,iconSize,null);
            i++;
            x+=iconSize-10;
        }
    }

    public void drawMessage(){
        int messageX=gp.tileSize;
        int messageY=gp.tileSize*4;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,32F));

        for(int i=0;i<message.size();i++){
            if(message.get(i)!=null){
                g2.setColor(Color.BLACK);
                g2.drawString(message.get(i),messageX+2,messageY+2);
                g2.setColor(Color.WHITE);
                g2.drawString(message.get(i),messageX,messageY);

                int counter=messageCounter.get(i)+1;
                messageCounter.set(i,counter);
                messageY+=50;

                if(messageCounter.get(i)>180){
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }

    public void drawMonsterLife(){
        for(int i=0;i<gp.monster[1].length;i++){
            Entity monster=gp.monster[gp.currentMap][i];
            if(monster!=null && monster.inCamera()){
                if(monster.hpBarOn && !monster.boss){
                    double oneScale=(double)gp.tileSize/monster.maxLife;
                    double hpBarValue=oneScale*monster.life;

                    g2.setColor(new Color(35,35,35));
                    g2.fillRect(monster.getScreenX()-1,monster.getScreenY()-16,gp.tileSize+2,12);

                    g2.setColor(new Color(255,0,30));
                    g2.fillRect(monster.getScreenX(),monster.getScreenY()-15,(int)hpBarValue,10);

                    monster.hpBarCounter++;

                    if(monster.hpBarCounter>600){
                        monster.hpBarCounter=0;
                        monster.hpBarOn=false;
                    }
                }else if(monster.boss){
                    double oneScale=(double)gp.tileSize*8/monster.maxLife;
                    double hpBarValue=oneScale*monster.life;

                    int x=gp.screenWidth/2-gp.tileSize*4;
                    int y=gp.tileSize*10;

                    g2.setColor(new Color(35,35,35));
                    g2.fillRect(x-1,y-1,gp.tileSize*8+2,22);

                    g2.setColor(new Color(255,0,30));
                    g2.fillRect(x,y,(int)hpBarValue,20);

                    g2.setFont(g2.getFont().deriveFont(Font.BOLD,24));
                    g2.setColor(Color.WHITE);
                    g2.drawString(monster.name,x+4,y-10);
                }
            }
        }
    }

    public void drawTitleScreen(){
        g2.setColor(new Color(0,0,0));
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);

        //TITULO
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,96F));
        String text="Juego 2D";
        int x=getXForCenteredText(text);
        int y=gp.tileSize*3;
        //SOMBRA
        g2.setColor(Color.GRAY);
        g2.drawString(text,x+5,y+5);
        //COLOR PRINCIPAL
        g2.setColor(Color.WHITE);
        g2.drawString(text,x,y);

        //IMAGEN DEL JUGADOR
        x=gp.screenWidth/2-(gp.tileSize*2)/2;
        y+=gp.tileSize*2;
        g2.drawImage(gp.player.down1,x,y,gp.tileSize*2,gp.tileSize*2,null);

        //MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));
        text="NUEVO JUEGO";
        x=getXForCenteredText(text);
        y+=gp.tileSize*3.5;
        g2.drawString(text,x,y);
        if(commandNum==0){
            g2.drawString(">",x-gp.tileSize,y);
        }

        text="CARGAR JUEGO";
        x=getXForCenteredText(text);
        y+=gp.tileSize;
        g2.drawString(text,x,y);
        if(commandNum==1){
            g2.drawString(">",x-gp.tileSize,y);
        }

        text="SALIR";
        x=getXForCenteredText(text);
        y+=gp.tileSize;
        g2.drawString(text,x,y);
        if(commandNum==2){
            g2.drawString(">",x-gp.tileSize,y);
        }
    }

    public void drawPauseScreen(){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,80F));
        String text="JUEGO PAUSADO";
        int x=getXForCenteredText(text);
        int y=gp.screenHeight/2;

        g2.drawString(text,x,y);
    }

    public void drawDialogueScreen(){
        int x=gp.tileSize*3;
        int y=gp.tileSize/2;
        int width=gp.screenWidth-(gp.tileSize*6);
        int height=gp.tileSize*4;
        drawSubWindow(x,y,width,height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,32F));
        x+=gp.tileSize;
        y+=gp.tileSize;

        if(npc.dialogues[npc.dialogueSet][npc.dialogueIndex]!=null){
            char characters[]=npc.dialogues[npc.dialogueSet][npc.dialogueIndex].toCharArray();
            if(charIndex<characters.length){
                if(charIndex%5==0) gp.playSE(17);
                String s=String.valueOf(characters[charIndex]);
                combinedText=combinedText+s;
                currentDialogue=combinedText;
                charIndex++;
            }
            if(gp.keyH.enterPressed){
                charIndex=0;
                combinedText="";
                if(gp.gameState==gp.dialogueState || gp.gameState==gp.cutsceneState){
                    npc.dialogueIndex++;
                    gp.keyH.enterPressed=false;
                }
            }
        }else{
            npc.dialogueIndex=0;
            if(gp.gameState==gp.dialogueState) gp.gameState=gp.playState;
            if(gp.gameState==gp.cutsceneState) gp.csManager.scenePhase++;
        }

        for(String line:currentDialogue.split("\n")){
            g2.drawString(line,x,y);
            y+=40;
        }
    }

    public void drawCharacterScreen(){
        //CREAR UN FRAME
        final int frameX=gp.tileSize*2;
        final int frameY=gp.tileSize;
        final int frameWidth=gp.tileSize*5;
        final int frameHeight=gp.tileSize*10;
        drawSubWindow(frameX,frameY,frameWidth,frameHeight);

        //TEXTO
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(32F));

        int textX=frameX+20;
        int textY=frameY+gp.tileSize;
        final int lineHeight=35;

        //NOMBRES
        g2.drawString("Nivel",textX,textY); textY+=lineHeight;
        g2.drawString("Vida",textX,textY); textY+=lineHeight;
        g2.drawString("Mana",textX,textY); textY+=lineHeight;
        g2.drawString("Fuerza",textX,textY); textY+=lineHeight;
        g2.drawString("Resistencia",textX,textY); textY+=lineHeight;
        g2.drawString("Ataque",textX,textY); textY+=lineHeight;
        g2.drawString("Defensa",textX,textY); textY+=lineHeight;
        g2.drawString("Experiencia",textX,textY); textY+=lineHeight;
        g2.drawString("Sig. Nivel",textX,textY); textY+=lineHeight;
        g2.drawString("Monedas",textX,textY); textY+=lineHeight+10;
        g2.drawString("Arma",textX,textY); textY+=lineHeight+15;
        g2.drawString("Escudo",textX,textY);

        //VALORES
        int tailX=(frameX+frameWidth)-30;
        textY=frameY+gp.tileSize;
        String value;

        value=String.valueOf(gp.player.level);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.life+"/"+gp.player.maxLife);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.mana+"/"+gp.player.maxMana);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.strength);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.dexterity);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.attack);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.defense);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.exp);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.nextLevelExp);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        value=String.valueOf(gp.player.coin);
        textX=getXForAlignToRightText(value,tailX);
        g2.drawString(value,textX,textY);
        textY+=lineHeight;

        g2.drawImage(gp.player.currentWeapon.down1,tailX-gp.tileSize,textY-24,null);
        textY+=gp.tileSize;
        g2.drawImage(gp.player.currentShield.down1,tailX-gp.tileSize,textY-24,null);
    }

    public void drawInventory(Entity entity,boolean cursor){
        int frameX=0;
        int frameY=0;
        int frameWidth=0;
        int frameHeight=0;
        int slotCol=0;
        int slotRow=0;

        if(entity==gp.player){
            frameX=gp.tileSize*12;
            frameY=gp.tileSize;
            frameWidth=gp.tileSize*6;
            frameHeight=gp.tileSize*5;
            slotCol=playerSlotCol;
            slotRow=playerSlotRow;
        }else{
            frameX=gp.tileSize*2;
            frameY=gp.tileSize;
            frameWidth=gp.tileSize*6;
            frameHeight=gp.tileSize*5;
            slotCol=npcSlotCol;
            slotRow=npcSlotRow;
        }

        //FRAME
        drawSubWindow(frameX,frameY,frameWidth,frameHeight);

        //ESPACIOS
        final int slotXstart=frameX+20;
        final int slotYstart=frameY+20;
        int slotX=slotXstart;
        int slotY=slotYstart;
        int slotSize=gp.tileSize+3;

        //ITEMS DEL JUGADOR
        for(int i=0;i<entity.inventory.size();i++){
            //CURSOR DE EQUIPAR
            if(entity.inventory.get(i)==entity.currentWeapon || entity.inventory.get(i)==entity.currentShield || entity.inventory.get(i)==entity.currentLight){
                g2.setColor(new Color(240,190,90));
                g2.fillRoundRect(slotX,slotY,gp.tileSize,gp.tileSize,10,10);
            }

            g2.drawImage(entity.inventory.get(i).down1,slotX,slotY,null);

            //DIBUJAR CANTIDAD
            if(entity==gp.player && entity.inventory.get(i).amount>1){
                g2.setFont(g2.getFont().deriveFont(32F));
                int amountX;
                int amountY;
                String s=""+entity.inventory.get(i).amount;
                amountX=getXForAlignToRightText(s,slotX+44);
                amountY=slotY+gp.tileSize;

                //SOMBRA
                g2.setColor(new Color(60,60,60));
                g2.drawString(s,amountX,amountY);
                //NUMERO
                g2.setColor(Color.WHITE);
                g2.drawString(s,amountX-3,amountY-3);
            }

            slotX+=slotSize;
            if(i==4 || i==9 || i==14){
                slotX=slotXstart;
                slotY+=slotSize;
            }
        }

        //CURSOR
        if(cursor){
            int cursorX=slotXstart+(slotSize*slotCol);
            int cursorY=slotYstart+(slotSize*slotRow);
            int cursorWidth=gp.tileSize;
            int cursorHeight=gp.tileSize;

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cursorX,cursorY,cursorWidth,cursorHeight,10,10);

            //DESCRIPCION
            int dFrameX=frameX;
            int dFrameY=frameY+frameHeight;
            int dFrameWidth=frameWidth;
            int dFrameHeight=gp.tileSize*3;

            //TEXTO DE DESCRIPCION
            int textX=dFrameX+20;
            int textY=dFrameY+gp.tileSize;
            g2.setFont(g2.getFont().deriveFont(28F));

            int itemIndex=getItemIndexOnSlot(slotCol,slotRow);
            if(itemIndex<entity.inventory.size()){
                drawSubWindow(dFrameX,dFrameY,dFrameWidth,dFrameHeight);
                for(String line:entity.inventory.get(itemIndex).description.split("\n")){
                    g2.drawString(line,textX,textY);
                    textY+=32;
                }
            }
        }
    }

    public void drawGameOverScreen(){
        g2.setColor(new Color(0,0,0,150));
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,110F));

        text="Moriste";
        //sombra
        g2.setColor(Color.BLACK);
        x=getXForCenteredText(text);
        y=gp.tileSize*4;
        g2.drawString(text,x,y);
        //texto
        g2.setColor(Color.WHITE);
        g2.drawString(text,x-4,y-4);

        //REINTENTAR
        g2.setFont(g2.getFont().deriveFont(50F));
        text="Reintentar";
        x=getXForCenteredText(text);
        y+=gp.tileSize*4;
        g2.drawString(text,x,y);
        if(commandNum==0) g2.drawString(">",x-40,y);

        //VOLVER A LA PANTALLA DE TITULO
        text="Salir";
        x=getXForCenteredText(text);
        y+=55;
        g2.drawString(text,x,y);
        if(commandNum==1) g2.drawString(">",x-40,y);
    }

    public void drawOptionsScreen(){
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(32F));

        //SUB VENTANA
        int frameX=gp.tileSize*6;
        int frameY=gp.tileSize;
        int frameWidth=gp.tileSize*8;
        int frameHeight=gp.tileSize*10;
        drawSubWindow(frameX,frameY,frameWidth,frameHeight);

        switch(subState){
            case 0: options_top(frameX,frameY); break;
            case 1: options_fullScreenNotification(frameX,frameY); break;
            case 2: options_control(frameX,frameY); break;
            case 3: options_endGameConfirmation(frameX,frameY); break;
        }

        gp.keyH.enterPressed=false;
    }

    public void options_top(int frameX,int frameY){
        int textX;
        int textY;

        //TITULO
        String text="Opciones";
        textX=getXForCenteredText(text);
        textY=frameY+gp.tileSize;
        g2.drawString(text,textX,textY);

        //PANTALLA COMPLETA ENCENDIDO/APAGADO
        textX=frameX+gp.tileSize;
        textY+=gp.tileSize*2;
        g2.drawString("Pantalla Comp.",textX,textY);
        if(commandNum==0){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed){
                gp.fullScreenOn=!gp.fullScreenOn;
                subState=1;
            }
        }

        //MUSICA
        textY+=gp.tileSize;
        g2.drawString("Vol. Música",textX,textY);
        if(commandNum==1) g2.drawString(">",textX-25,textY);

        //EFECTOS DE SONIDO
        textY+=gp.tileSize;
        g2.drawString("Vol. Efectos",textX,textY);
        if(commandNum==2) g2.drawString(">",textX-25,textY);

        //CONTROLES
        textY+=gp.tileSize;
        g2.drawString("Controles",textX,textY);
        if(commandNum==3){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed){
                subState=2;
                commandNum=0;
            }
        }

        //TERMINAR JUEGO
        textY+=gp.tileSize;
        g2.drawString("Salir del Juego",textX,textY);
        if(commandNum==4){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed){
                subState=3;
                commandNum=0;
            }
        }

        //VOLVER
        textY+=gp.tileSize*2;
        g2.drawString("Volver",textX,textY);
        if(commandNum==5){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed){
                gp.gameState=gp.playState;
                commandNum=0;
            }
        }

        //CHECK BOX DE PANTALLA COMPLETA
        textX=frameX+(int)(gp.tileSize*4.5);
        textY=frameY+gp.tileSize*2+24;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(textX,textY,24,24);
        if(gp.fullScreenOn) g2.fillRect(textX,textY,24,24);

        //VOLUMEN DE MUSICA
        textY+=gp.tileSize;
        g2.drawRect(textX,textY,120,24);
        int volumeWidth=24*gp.music.volumeScale;
        g2.fillRect(textX,textY,volumeWidth,24);

        //VOLUMEN DE EFECTOS DE SONIDO
        textY+=gp.tileSize;
        g2.drawRect(textX,textY,120,24);
        volumeWidth=24*gp.se.volumeScale;
        g2.fillRect(textX,textY,volumeWidth,24);

        gp.config.saveConfig();
    }

    public void options_fullScreenNotification(int frameX,int frameY){
        int textX=frameX+gp.tileSize;
        int textY=frameY+gp.tileSize*3;

        currentDialogue="Se necesitará reiniciar el\njuego para que el cambio\nse realice.";

        for(String line:currentDialogue.split("\n")){
            g2.drawString(line,textX,textY);
            textY+=40;
        }

        //VOLVER
        textY=frameY+gp.tileSize*9;
        g2.drawString("Volver",textX,textY);
        if(commandNum==0){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed) subState=0;
        }
    }

    public void options_control(int frameX,int frameY){
        int textX;
        int textY;

        //TITULO
        String text="Controles";
        textX=getXForCenteredText(text);
        textY=frameY+gp.tileSize;
        g2.drawString(text,textX,textY);

        textX=frameX+gp.tileSize;
        textY+=gp.tileSize-10;
        g2.drawString("Moverse",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Atacar/Confirmar",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Disparar",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Bloquear",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Inventario",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Mapa",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Mini Mapa",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Pausa",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("Opciones",textX,textY); textY+=gp.tileSize-10;

        textX=frameX+gp.tileSize*6-10;
        textY=frameY+gp.tileSize*2-10;
        g2.drawString("WASD",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("ENTER",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("ESPACIO",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("SHIFT",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("E",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("M",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("I",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("P",textX,textY); textY+=gp.tileSize-10;
        g2.drawString("ESC",textX,textY); textY+=gp.tileSize-10;

        //VOLVER
        textX=frameX+gp.tileSize;
        textY=frameY+gp.tileSize*9;
        g2.drawString("Volver",textX,textY);
        if(commandNum==0){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed){
                subState=0;
                commandNum=3;
            }
        }
    }

    public void options_endGameConfirmation(int frameX,int frameY){
        int textX=frameX+gp.tileSize;
        int textY=frameY+gp.tileSize*3;

        currentDialogue="¿Seguro que quiere salir\ndel juego y volver a la\npantalla de título?";
        for(String line:currentDialogue.split("\n")){
            g2.drawString(line,textX,textY);
            textY+=40;
        }

        //SI
        String text="Sí";
        textX=getXForCenteredText(text);
        textY+=gp.tileSize*3;
        g2.drawString(text,textX,textY);
        if(commandNum==0){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed){
                subState=0;
                gp.gameState=gp.titleState;
                gp.stopMusic();
                gp.resetGame(true);
            }
        }

        //NO
        text="No";
        textX=getXForCenteredText(text);
        textY+=gp.tileSize;
        g2.drawString(text,textX,textY);
        if(commandNum==1){
            g2.drawString(">",textX-25,textY);
            if(gp.keyH.enterPressed){
                subState=0;
                commandNum=4;
            }
        }
    }

    public void drawTransition(){
        counter++;
        g2.setColor(new Color(0,0,0,counter*5));
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);
        if(counter==50){
            counter=0;
            gp.gameState=gp.playState;
            gp.currentMap=gp.eHandler.tempMap;
            gp.player.worldX=gp.tileSize*gp.eHandler.tempCol;
            gp.player.worldY=gp.tileSize*gp.eHandler.tempRow;
            gp.eHandler.previousEventX=gp.player.worldX;
            gp.eHandler.previousEventY=gp.player.worldY;
            gp.changeArea();
        }
    }

    public void drawTradeScreen(){
        switch(subState){
            case 0: trade_select(); break;
            case 1: trade_buy(); break;
            case 2: trade_sell(); break;
        }
        gp.keyH.enterPressed=false;
    }

    public void trade_select(){
        npc.dialogueSet=0;
        drawDialogueScreen();

        //DIBUJAR VENTANA
        int x=gp.tileSize*15;
        int y=gp.tileSize*4;
        int width=(int)(gp.tileSize*3.5);
        int height=(int)(gp.tileSize*3.5);
        drawSubWindow(x,y,width,height);

        //TEXTOS
        x+=gp.tileSize;
        y+=gp.tileSize;
        g2.drawString("Comprar",x,y);
        if(commandNum==0){
            g2.drawString(">",x-24,y);
            if(gp.keyH.enterPressed){
                subState=1;
            }
        }
        y+=gp.tileSize;

        g2.drawString("Vender",x,y);
        if(commandNum==1){
            g2.drawString(">",x-24,y);
            if(gp.keyH.enterPressed){
                subState=2;
            }
        }
        y+=gp.tileSize;

        g2.drawString("Salir",x,y);
        if(commandNum==2){
            g2.drawString(">",x-24,y);
            if(gp.keyH.enterPressed){
                commandNum=0;
                npc.startDialogue(npc,1);
            }
        }
    }

    public void trade_buy(){
        //DIBUJAR INVENTARIOS DEL JUGADOR Y NPC
        drawInventory(gp.player,false);
        drawInventory(npc,true);

        //DIBUJAR VENTANA DE INFO
        int x=gp.tileSize*2;
        int y=gp.tileSize*9;
        int width=gp.tileSize*6;
        int height=gp.tileSize*2;
        drawSubWindow(x,y,width,height);
        g2.drawString("[ESC] Volver",x+24,y+60);

        //DIBUJAR VENTANA DE MONEDAS DEL JUGADOR
        x=gp.tileSize*12;
        y=gp.tileSize*9;
        width=gp.tileSize*6;
        height=gp.tileSize*2;
        drawSubWindow(x,y,width,height);
        g2.drawString("Tus monedas: "+gp.player.coin,x+24,y+60);

        //DIBUJAR VENTANA DE PRECIOS
        int itemIndex=getItemIndexOnSlot(npcSlotCol,npcSlotRow);
        if(itemIndex<npc.inventory.size()){
            x=(int)(gp.tileSize*5.5);
            y=(int)(gp.tileSize*5.5);
            width=(int)(gp.tileSize*2.5);
            height=gp.tileSize;
            drawSubWindow(x,y,width,height);
            g2.drawImage(coin,x+10,y+7,32,32,null);

            int price=npc.inventory.get(itemIndex).price;
            String text=""+price;
            x=getXForAlignToRightText(text,gp.tileSize*8-20);
            g2.drawString(text,x,y+34);

            //COMPRAR UN ITEM
            if(gp.keyH.enterPressed){
                if(npc.inventory.get(itemIndex).price>gp.player.coin){
                    subState=0;
                    npc.startDialogue(npc,2);
                }else{
                    if(gp.player.canObtainItem(npc.inventory.get(itemIndex))){
                        gp.player.coin-=npc.inventory.get(itemIndex).price;
                    }else{
                        subState=0;
                        npc.startDialogue(npc,3);
                    }
                }
            }
        }
    }

    public void trade_sell(){
        drawInventory(gp.player,true);

        int x;
        int y;
        int width;
        int height;

        //DIBUJAR VENTANA DE INFO
        x=gp.tileSize*2;
        y=gp.tileSize*9;
        width=gp.tileSize*6;
        height=gp.tileSize*2;
        drawSubWindow(x,y,width,height);
        g2.drawString("[ESC] Volver",x+24,y+60);

        //DIBUJAR VENTANA DE MONEDAS DEL JUGADOR
        x=gp.tileSize*12;
        y=gp.tileSize*9;
        width=gp.tileSize*6;
        height=gp.tileSize*2;
        drawSubWindow(x,y,width,height);
        g2.drawString("Tus monedas: "+gp.player.coin,x+24,y+60);

        //DIBUJAR VENTANA DE PRECIOS
        int itemIndex=getItemIndexOnSlot(playerSlotCol,playerSlotRow);
        if(itemIndex<gp.player.inventory.size()){
            x=(int)(gp.tileSize*15.5);
            y=(int)(gp.tileSize*5.5);
            width=(int)(gp.tileSize*2.5);
            height=gp.tileSize;
            drawSubWindow(x,y,width,height);
            g2.drawImage(coin,x+10,y+7,32,32,null);

            int price=gp.player.inventory.get(itemIndex).price/2;
            String text=""+price;
            x=getXForAlignToRightText(text,gp.tileSize*18-20);
            g2.drawString(text,x,y+34);

            //VENDER UN ITEM
            if(gp.keyH.enterPressed){
                if(gp.player.inventory.get(itemIndex)==gp.player.currentWeapon || gp.player.inventory.get(itemIndex)==gp.player.currentShield){
                    commandNum=0;
                    subState=0;
                    npc.startDialogue(npc,4);
                }else{
                    if(gp.player.inventory.get(itemIndex).amount>1) gp.player.inventory.get(itemIndex).amount--;
                    else gp.player.inventory.remove(itemIndex);
                    gp.player.coin+=price;
                }
            }
        }
    }

    public void drawSleepScreen(){
        counter++;
        if(counter<120){
            gp.eManager.lighting.filterAlpha+=0.01F;
            if(gp.eManager.lighting.filterAlpha>1F){
                gp.eManager.lighting.filterAlpha=1F;
            }
        }
        if(counter>=120){
            gp.eManager.lighting.filterAlpha-=0.01F;
            if(gp.eManager.lighting.filterAlpha<=0){
                gp.eManager.lighting.filterAlpha=0F;
                counter=0;
                gp.eManager.lighting.dayState=gp.eManager.lighting.day;
                gp.eManager.lighting.dayCounter=0;
                gp.gameState=gp.playState;
                gp.player.getImage();
            }
        }
    }

    public int getItemIndexOnSlot(int slotCol,int slotRow){
        int itemIndex=slotCol+(slotRow*5);
        return itemIndex;
    }

    public void drawSubWindow(int x,int y,int width,int height){
        Color c=new Color(0,0,0,210);
        g2.setColor(c);
        g2.fillRoundRect(x,y,width,height,35,35);

        c=new Color(255,255,255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5,y+5,width-10,height-10,25,25);
    }

    public int getXForCenteredText(String text){
        int length=(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int x=gp.screenWidth/2-length/2;
        return x;
    }

    public int getXForAlignToRightText(String text,int tailX){
        int length=(int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
        int x=tailX-length;
        return x;
    }
}