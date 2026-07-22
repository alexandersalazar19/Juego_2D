package object;
import entity.Entity;
import main.GamePanel;

public class OBJ_Potion_Red extends Entity{
    GamePanel gp;

    public OBJ_Potion_Red(GamePanel gp){
        super(gp);
        this.gp=gp;

        type=type_consumable;
        name="Poción Roja";
        value=5;
        down1=setup("/objects/potion_red",gp.tileSize,gp.tileSize);
        description="["+name+"]\nRestaura "+value+" puntos de tu\nvida.";
        price=25;
        stackable=true;
    }

    public boolean use(Entity entity){
        gp.gameState=gp.dialogueState;
        gp.ui.currentDialogue="¡Tomaste una "+name+"!\nHas recuperado "+value+" puntos de tu vida.";
        entity.life+=value;
        gp.playSE(2);
        return true;
    }
}