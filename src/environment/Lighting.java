package environment;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Lighting{
    GamePanel gp;
    BufferedImage darknessFilter;
    public int dayCounter;
    public float filterAlpha=0F;

    //horas del dia
    public final int day=0;
    public final int dusk=1;
    public final int night=2;
    public final int dawn=3;
    public int dayState=day;

    public Lighting(GamePanel gp){
        this.gp=gp;
        setLightSource();
    }

    public void setLightSource(){
        //Crear imagen del filtro
        darknessFilter=new BufferedImage(gp.screenWidth,gp.screenHeight,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2=(Graphics2D)darknessFilter.getGraphics();

        if(gp.player.currentLight==null) g2.setColor(new Color(0,0,0.1F,0.98F));
        else{
            //Obtener el centro del aro de luz
            int centerX=gp.player.screenX+(gp.tileSize)/2;
            int centerY=gp.player.screenY+(gp.tileSize)/2;

            //Crear efecto de degradado cerca del circulo
            Color color[]=new Color[12];
            float fraction[]=new float[12];

            color[0]=new Color(0,0,0.1F,0.1F);
            color[1]=new Color(0,0,0.1F,0.42F);
            color[2]=new Color(0,0,0.1F,0.52F);
            color[3]=new Color(0,0,0.1F,0.61F);
            color[4]=new Color(0,0,0.1F,0.69F);
            color[5]=new Color(0,0,0.1F,0.76F);
            color[6]=new Color(0,0,0.1F,0.82F);
            color[7]=new Color(0,0,0.1F,0.87F);
            color[8]=new Color(0,0,0.1F,0.91F);
            color[9]=new Color(0,0,0.1F,0.94F);
            color[10]=new Color(0,0,0.1F,0.96F);
            color[11]=new Color(0,0,0.1F,0.98F);

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

    public void resetDay(){
        dayState=day;
        filterAlpha=0F;
    }

    public void update(){
        if(gp.player.lightUpdated){
            setLightSource();
            gp.player.lightUpdated=false;
        }

        //revisar la hora del dia
        if(dayState==day){
            dayCounter++;
            if(dayCounter>600){
                dayState=dusk;
                dayCounter=0;
            }
        }
        if(dayState==dusk){
            filterAlpha+=0.0001F;
            if(filterAlpha>1F){
                filterAlpha=1F;
                dayState=night;
            }
        }
        if(dayState==night){
            dayCounter++;
            if(dayCounter>36000){
                dayState=dawn;
                dayCounter=0;
            }
        }
        if(dayState==dawn){
            filterAlpha-=0.0001F;
            if(filterAlpha<0){
                filterAlpha=0;
                dayState=day;
            }
        }
    }

    public void draw(Graphics2D g2){
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,filterAlpha));
        g2.drawImage(darknessFilter,0,0,null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1F));

        //DEBUG
        String situation="";
        switch(dayState){
            case day: situation="Day"; break;
            case dusk: situation="Dusk"; break;
            case night: situation="Night"; break;
            case dawn: situation="Dawn"; break;
        }
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(50F));
        g2.drawString(situation,800,500);
    }
}