package data;
import java.io.Serializable;
import java.util.ArrayList;

public class DataStorage implements Serializable{
    //stats del jugador
    int level;
    int maxLife;
    int life;
    int maxMana;
    int mana;
    int strength;
    int dexterity;
    int exp;
    int nextLevelExp;
    int coin;

    //inventario del jugador
    ArrayList<String> itemNames=new ArrayList<>();
    ArrayList<Integer> itemAmounts=new ArrayList<>();
    int currentWeaponSlot;
    int currentShieldSlot;

    //objetos en el mapa
    String mapObjectNames[][];
    int mapObjectWorldX[][];
    int mapObjectWorldY[][];
    String mapObjectLootNames[][];
    boolean mapObjectOpened[][];
}