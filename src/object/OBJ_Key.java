package object;
import entity.Entity;
import main.GamePanel;

public class OBJ_Key extends Entity{
    GamePanel gp;

    public OBJ_Key(GamePanel gp){
        super(gp);
        this.gp=gp;

        type=type_consumable;
        name="Llave";
        down1=setup("/objects/key",gp.tileSize,gp.tileSize);
        description="["+name+"]\nPuede abrir una puerta.";
        price=100;
    }

    public boolean use(Entity entity){
        gp.gameState=gp.dialogueState;
        int objIndex=getDetected(entity,gp.obj,"Puerta");
        if(objIndex!=999){
            gp.ui.currentDialogue="Usaste la "+name+" y abriste la puerta.";
            gp.playSE(3);
            gp.obj[gp.currentMap][objIndex]=null;
            return true;
        }else{
            gp.ui.currentDialogue="¿Que intentas hacer?";
            return false;
        }
    }
}