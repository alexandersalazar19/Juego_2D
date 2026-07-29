package object;
import main.GamePanel;
import entity.Entity;

public class OBJ_Door extends Entity{
    GamePanel gp;

    public OBJ_Door(GamePanel gp){
        super(gp);
        this.gp=gp;

        type=type_obstacle;
        name="Puerta";
        down1=setup("/objects/door",gp.tileSize,gp.tileSize);
        collision=true;

        solidArea.x=0;
        solidArea.y=16;
        solidArea.width=48;
        solidArea.height=32;
        solidAreaDefaultX=solidArea.x;
        solidAreaDefaultY=solidArea.y;

        setDialogue();
    }

    public void setDialogue(){
        dialogues[0][0]="Necesitas una llave para abrir esta puerta.";
    }

    public void interact(){
        startDialogue(this,0);
    }
}
