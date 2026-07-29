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
        stackable=true;

        setDialogue();
    }

    public void setDialogue(){
        dialogues[0][0]="Usaste la "+name+" y abriste la puerta.";
        dialogues[1][0]="¿Que intentas hacer?";
    }

    public boolean use(Entity entity){
        int objIndex=getDetected(entity,gp.obj,"Puerta");
        if(objIndex!=999){
            startDialogue(this,0);
            gp.playSE(3);
            gp.obj[gp.currentMap][objIndex]=null;
            return true;
        }else{
            startDialogue(this,1);
            return false;
        }
    }
}