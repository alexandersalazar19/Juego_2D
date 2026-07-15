package entity;
import main.GamePanel;

import java.awt.*;
import java.util.Random;

public class NPC_OldMan extends Entity{
    public NPC_OldMan(GamePanel gp){
        super(gp);
        direction="down";
        speed=1;

        solidArea=new Rectangle();
        solidArea.x=8;
        solidArea.y=16;
        solidArea.width=32;
        solidArea.height=32;
        solidAreaDefaultX=solidArea.x;
        solidAreaDefaultY=solidArea.y;

        getImage();
        setDialogue();
    }

    public void getImage(){
        up1=setup("/npc/oldman_up_1",gp.tileSize,gp.tileSize);
        up2=setup("/npc/oldman_up_2",gp.tileSize,gp.tileSize);
        down1=setup("/npc/oldman_down_1",gp.tileSize,gp.tileSize);
        down2=setup("/npc/oldman_down_2",gp.tileSize,gp.tileSize);
        left1=setup("/npc/oldman_left_1",gp.tileSize,gp.tileSize);
        left2=setup("/npc/oldman_left_2",gp.tileSize,gp.tileSize);
        right1=setup("/npc/oldman_right_1",gp.tileSize,gp.tileSize);
        right2=setup("/npc/oldman_right_2",gp.tileSize,gp.tileSize);
    }

    public void setDialogue(){
        dialogues[0]="¡Hola!";
        dialogues[1]="¿Así que vienes a esta isla a encontrar\nel tesoro?";
        dialogues[2]="Solía ser un gran mago, pero ahora...\nEstoy un poco viejo para más aventuras.";
        dialogues[3]="Como sea, ¡buena suerte en la tuya!";
    }

    public void setAction(){
        actionLockCounter++;
        if(actionLockCounter==120){
            Random random=new Random();
            int i=random.nextInt(100)+1;
            if(i<=25){
                direction="up";
            }
            if(i>25 && i<=50){
                direction="down";
            }
            if(i>50 && i<=75){
                direction="left";
            }
            if(i>75 && i<=100){
                direction="right";
            }
            actionLockCounter=0;
        }
    }

    public void speak(){
        super.speak();
    }
}