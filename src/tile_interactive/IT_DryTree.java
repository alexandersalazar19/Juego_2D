package tile_interactive;
import entity.Entity;
import main.GamePanel;

import java.awt.*;

public class IT_DryTree extends InteractiveTile{
    GamePanel gp;

    public IT_DryTree(GamePanel gp,int col,int row){
        super(gp,col,row);
        this.gp=gp;
        this.worldX=gp.tileSize*col;
        this.worldY=gp.tileSize*row;

        down1=setup("/tiles_interactive/drytree",gp.tileSize,gp.tileSize);
        desctructible=true;
        life=3;
    }

    public boolean isCorrectItem(Entity entity){
        return entity.currentWeapon.type==type_axe;
    }

    public void playSE(){gp.playSE(11);}

    public InteractiveTile getDestroyedForm(){InteractiveTile tile=new IT_Trunk(gp,worldX/gp.tileSize,worldY/gp.tileSize); return tile;}

    public Color getParticleColor(){Color color=new Color(65,50,30); return color;}

    public int getParticleSize(){int size=6; return size;} //6 pixeles

    public int getParticleSpeed(){int speed=1; return speed;}

    public int getParticleMaxLife(){int maxLife=20; return maxLife;}
}
