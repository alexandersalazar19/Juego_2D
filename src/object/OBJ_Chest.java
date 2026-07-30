package object;
import main.GamePanel;
import entity.Entity;

public class OBJ_Chest extends Entity{
    GamePanel gp;
    public static final String objName="Cofre";

    public OBJ_Chest(GamePanel gp){
        super(gp);
        this.gp=gp;

        type=type_obstacle;
        name=objName;
        image=setup("/objects/chest",gp.tileSize,gp.tileSize);
        image2=setup("/objects/chest_opened",gp.tileSize,gp.tileSize);
        down1=image;
        collision=true;

        solidArea.x=4;
        solidArea.y=16;
        solidArea.width=40;
        solidArea.height=32;
        solidAreaDefaultX=solidArea.x;
        solidAreaDefaultY=solidArea.y;
    }

    public void setLoot(Entity loot){
        this.loot=loot;
        setDialogue();
    }

    public void setDialogue(){
        dialogues[0][0]="Abriste el cofre y encontraste "+loot.name+".\n...Pero tu inventario está lleno.";
        dialogues[1][0]="Abriste el cofre y encontraste "+loot.name+".\nObtuviste "+loot.name+".";
        dialogues[2][0]="Ya abriste este cofre.";
    }

    public void interact(){
        if(!opened){
            gp.playSE(3);

            if(!gp.player.canObtainItem(loot)){
                startDialogue(this,0);
            }else{
                startDialogue(this,1);
                down1=image2;
                opened=true;
            }
        }else{
            startDialogue(this,2);
        }
    }
}