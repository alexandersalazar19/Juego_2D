package environment;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Lighting{
    GamePanel gp;
    BufferedImage darknessFilter;

    public Lighting(GamePanel gp){
        this.gp=gp;
        setLightSource();
    }

    public void setLightSource(){
        //Crear imagen del filtro
        darknessFilter=new BufferedImage(gp.screenWidth,gp.screenHeight,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2=(Graphics2D)darknessFilter.getGraphics();

        if(gp.player.currentLight==null) g2.setColor(new Color(0,0,0,0.98F));
        else{
            //Obtener el centro del aro de luz
            int centerX=gp.player.screenX+(gp.tileSize)/2;
            int centerY=gp.player.screenY+(gp.tileSize)/2;

            //Crear efecto de degradado cerca del circulo
            Color color[]=new Color[12];
            float fraction[]=new float[12];

            color[0]=new Color(0,0,0,0.1F);
            color[1]=new Color(0,0,0,0.42F);
            color[2]=new Color(0,0,0,0.52F);
            color[3]=new Color(0,0,0,0.61F);
            color[4]=new Color(0,0,0,0.69F);
            color[5]=new Color(0,0,0,0.76F);
            color[6]=new Color(0,0,0,0.82F);
            color[7]=new Color(0,0,0,0.87F);
            color[8]=new Color(0,0,0,0.91F);
            color[9]=new Color(0,0,0,0.94F);
            color[10]=new Color(0,0,0,0.96F);
            color[11]=new Color(0,0,0,0.98F);

            fraction[0]=0F;
            fraction[1]=0.4F;
            fraction[2]=0.5F;
            fraction[3]=0.6F;
            fraction[4]=0.65F;
            fraction[5]=0.7F;
            fraction[6]=0.75F;
            fraction[7]=0.8F;
            fraction[8]=0.85F;
            fraction[9]=0.9F;
            fraction[10]=0.95F;
            fraction[11]=1F;

            //Crear el degradado
            RadialGradientPaint gPaint=new RadialGradientPaint(centerX,centerY,gp.player.currentLight.lightRadius,fraction,color);

            //Pasar los datos del degradado a g2
            g2.setPaint(gPaint);
        }
        g2.fillRect(0,0,gp.screenWidth,gp.screenHeight);

        g2.dispose();
    }

    public void update(){
        if(gp.player.lightUpdated){
            setLightSource();
            gp.player.lightUpdated=false;
        }
    }

    public void draw(Graphics2D g2){
        g2.drawImage(darknessFilter,0,0,null);
    }
}