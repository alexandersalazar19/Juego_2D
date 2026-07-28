package data;
import entity.Entity;
import main.GamePanel;
import object.*;

import java.io.*;

public class SaveLoad{
    GamePanel gp;

    public SaveLoad(GamePanel gp){
        this.gp=gp;
    }

    public Entity getObject(String itemName){
        Entity obj=null;
        switch(itemName){
            case "Hacha": obj=new OBJ_Axe(gp); break;
            case "Botas": obj=new OBJ_Boots(gp); break;
            case "Llave": obj=new OBJ_Key(gp); break;
            case "Linterna": obj=new OBJ_Lantern(gp); break;
            case "Poción Roja": obj=new OBJ_Potion_Red(gp); break;
            case "Escudo Azul": obj=new OBJ_Shield_Blue(gp); break;
            case "Escudo de Madera": obj=new OBJ_Shield_Wood(gp); break;
            case "Espada Normal": obj=new OBJ_Sword_Normal(gp); break;
            case "Tienda de Acampar": obj=new OBJ_Tent(gp); break;
            case "Puerta": obj=new OBJ_Door(gp); break;
            case "Cofre": obj=new OBJ_Chest(gp); break;
            case "Corazón": obj=new OBJ_Heart(gp); break;
            case "Cristal de Maná": obj=new OBJ_ManaCrystal(gp); break;

            default: System.out.println("Objeto desconocido: "+itemName);
        }
        return obj;
    }

    public void save(){
        try{
            ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File("save.dat")));

            //stats del jugador
            DataStorage ds=new DataStorage();
            ds.level=gp.player.level;
            ds.maxLife=gp.player.maxLife;
            ds.life=gp.player.life;
            ds.maxMana=gp.player.maxMana;
            ds.mana=gp.player.mana;
            ds.strength=gp.player.strength;
            ds.dexterity=gp.player.dexterity;
            ds.exp=gp.player.exp;
            ds.nextLevelExp=gp.player.nextLevelExp;
            ds.coin=gp.player.coin;

            //inventario del jugador
            for(int i=0;i<gp.player.inventory.size();i++){
                ds.itemNames.add(gp.player.inventory.get(i).name);
                ds.itemAmounts.add(gp.player.inventory.get(i).amount);
            }
            //items equipados del jugador
            ds.currentWeaponSlot=gp.player.getCurrentWeaponSlot();
            ds.currentShieldSlot=gp.player.getCurrentShieldSlot();

            //objetos en el mapa
            ds.mapObjectNames=new String[gp.maxMap][gp.obj[1].length];
            ds.mapObjectWorldX=new int[gp.maxMap][gp.obj[1].length];
            ds.mapObjectWorldY=new int[gp.maxMap][gp.obj[1].length];
            ds.mapObjectLootNames=new String[gp.maxMap][gp.obj[1].length];
            ds.mapObjectOpened=new boolean[gp.maxMap][gp.obj[1].length];

            for(int mapNum=0;mapNum<gp.maxMap;mapNum++){
                for(int i=0;i<gp.obj[1].length;i++){
                    if(gp.obj[mapNum][i]==null){
                        ds.mapObjectNames[mapNum][i]="NA";
                    }else{
                        ds.mapObjectNames[mapNum][i]=gp.obj[mapNum][i].name;
                        ds.mapObjectWorldX[mapNum][i]=gp.obj[mapNum][i].worldX;
                        ds.mapObjectWorldY[mapNum][i]=gp.obj[mapNum][i].worldY;
                        if(gp.obj[mapNum][i].loot!=null) ds.mapObjectLootNames[mapNum][i]=gp.obj[mapNum][i].loot.name;
                        ds.mapObjectOpened[mapNum][i]=gp.obj[mapNum][i].opened;
                    }
                }
            }

            oos.writeObject(ds);
        }catch(Exception e){
            System.out.println("Error al guardar datos");
        }
    }

    public void load(){
        try{
            ObjectInputStream ois=new ObjectInputStream(new FileInputStream(new File("save.dat")));

            //stats del jugador
            DataStorage ds=(DataStorage)ois.readObject();
            gp.player.level=ds.level;
            gp.player.maxLife=ds.maxLife;
            gp.player.life=ds.life;
            gp.player.maxMana=ds.maxMana;
            gp.player.mana=ds.mana;
            gp.player.strength=ds.strength;
            gp.player.dexterity=ds.dexterity;
            gp.player.exp=ds.exp;
            gp.player.nextLevelExp=ds.nextLevelExp;
            gp.player.coin=ds.coin;

            //inventario del jugador
            gp.player.inventory.clear();
            for(int i=0;i<ds.itemNames.size();i++){
                gp.player.inventory.add(getObject(ds.itemNames.get(i)));
                gp.player.inventory.get(i).amount=ds.itemAmounts.get(i);
            }
            //items equipados del jugador
            gp.player.currentWeapon=gp.player.inventory.get(ds.currentWeaponSlot);
            gp.player.currentShield=gp.player.inventory.get(ds.currentShieldSlot);
            gp.player.getAttack();
            gp.player.getDefense();
            gp.player.getAttackImage();

            //objetos en el mapa
            for(int mapNum=0;mapNum<gp.maxMap;mapNum++){
                for(int i=0;i<gp.obj[1].length;i++){
                    if(ds.mapObjectNames[mapNum][i].equals("NA")){
                        gp.obj[mapNum][i]=null;
                    }else{
                        gp.obj[mapNum][i]=getObject(ds.mapObjectNames[mapNum][i]);
                        gp.obj[mapNum][i].worldX=ds.mapObjectWorldX[mapNum][i];
                        gp.obj[mapNum][i].worldY=ds.mapObjectWorldY[mapNum][i];
                        if(ds.mapObjectLootNames[mapNum][i]!=null) gp.obj[mapNum][i].loot=getObject(ds.mapObjectLootNames[mapNum][i]);
                        gp.obj[mapNum][i].opened=ds.mapObjectOpened[mapNum][i];
                        if(gp.obj[mapNum][i].opened) gp.obj[mapNum][i].down1=gp.obj[mapNum][i].image2;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error al cargar datos");
        }
    }
}