package main;
import java.io.*;

public class Config{
    GamePanel gp;

    public Config(GamePanel gp){
        this.gp=gp;
    }

    public void saveConfig(){
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter("config.txt"));

            //PANTALLA COMPLETA
            if(gp.fullScreenOn) bw.write("On");
            if(!gp.fullScreenOn) bw.write("Off");
            bw.newLine();

            //VOLUMEN MUSICA
            bw.write(String.valueOf(gp.music.volumeScale));
            bw.newLine();

            //VOLUMEN EFECTOS DE SONIDO
            bw.write(String.valueOf(gp.se.volumeScale));
            bw.newLine();

            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        try{
            BufferedReader br=new BufferedReader(new FileReader("config.txt"));

            String s=br.readLine();

            //PANTALLA COMPLETA
            if(s.equals("On")) gp.fullScreenOn=true;
            if(s.equals("Off")) gp.fullScreenOn=false;

            //VOLUMEN MUSICA
            s=br.readLine();
            gp.music.volumeScale=Integer.parseInt(s);

            //VOLUMEN EFECTOS DE SONIDO
            s=br.readLine();
            gp.se.volumeScale=Integer.parseInt(s);

            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}