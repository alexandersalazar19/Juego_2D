package object;
import main.GamePanel;
import entity.Entity;

public class OBJ_Heart extends Entity{
    GamePanel gp;
    public static final String objName="Corazón";

    public OBJ_Heart(GamePanel gp){
        super(gp);
        this.gp=gp;

        type=type_pickUpOnly;
        name=objName;
        value=2;
        down1=setup("/objects/heart_full",gp.tileSize,gp.tileSize);
        image=setup("/objects/heart_full",gp.tileSize,gp.tileSize);
        image2=setup("/objects/heart_half",gp.tileSize,gp.tileSize);
        image3=setup("/objects/heart_blank",gp.tileSize,gp.tileSize);
    }

    public boolean use(Entity entity){
        gp.playSE(2);
        gp.ui.addMessage("Vida +"+value);
        entity.life+=value;
        return true;
    }
}