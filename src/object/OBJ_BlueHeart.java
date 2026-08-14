package object;
import entity.Entity;
import main.GamePanel;

public class OBJ_BlueHeart extends Entity{
    GamePanel gp;
    public static final String objName="Corazón Azul";

    public OBJ_BlueHeart(GamePanel gp){
        super(gp);
        this.gp=gp;

        type=type_pickUpOnly;
        name=objName;
        down1=setup("/objects/blueheart",gp.tileSize,gp.tileSize);

        setDialogues();
    }

    public void setDialogues(){
        dialogues[0][0]="Recoges una hermosa gema azul.";
        dialogues[0][1]="¡Encontraste el "+name+", el tesoro legendario!";
    }

    public boolean use(Entity entity){
        gp.gameState=gp.cutsceneState;
        gp.csManager.sceneNum=gp.csManager.ending;
        return true;
    }
}