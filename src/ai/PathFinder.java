package ai;
import main.GamePanel;
import java.util.ArrayList;

public class PathFinder{
    GamePanel gp;
    Node[][] node;
    ArrayList<Node> openList=new ArrayList<>();
    public ArrayList<Node> pathList=new ArrayList<>();
    Node startNode,goalNode,currentNode;
    boolean goalReached=false;
    int step=0;

    public PathFinder(GamePanel gp){
        this.gp=gp;
    }

    public void instantiateNodes(){
        node=new Node[gp.maxWorldCol][gp.maxWorldRow];
        int col=0;
        int row=0;

        while(col<gp.maxWorldCol && row<gp.maxWorldRow){
            node[col][row]=new Node(col,row);
            col++;
            if(col==gp.maxWorldCol){
                col=0;
                row++;
            }
        }
    }

    public void resetNodes(){
        int col=0;
        int row=0;

        while(col<gp.maxWorldCol && row<gp.maxWorldRow){
            node[col][row].open=false;
            node[col][row].checked=false;
            node[col][row].solid=false;

            col++;
            if(col==gp.maxWorldCol){
                col=0;
                row++;
            }
        }
        openList.clear();
        pathList.clear();
        goalReached=false;
        step=0;
    }

    public void setNodes(int startCol,int startRow,int goalCol,int goalRow){
        resetNodes();

        startNode=node[startCol][startRow];
        currentNode=startNode;
        goalNode=node[goalCol][goalRow];
        openList.add(currentNode);

        int row=0;
        int col=0;
        while(col<gp.maxWorldCol && row<gp.maxWorldRow){
            //BUSCAR BLOQUES SOLIDOS
            //bloques
            int tileNum=gp.tileM.mapTileNum[gp.currentMap][col][row];
            if(gp.tileM.tile[tileNum].collision){
                node[col][row].solid=true;
            }
            //bloques interactivos
            for(int i=0;i<gp.iTile[1].length;i++){
                if(gp.iTile[gp.currentMap][i]!=null && gp.iTile[gp.currentMap][i].desctructible){
                    int itCol=gp.iTile[gp.currentMap][i].worldX/gp.tileSize;
                    int itRow=gp.iTile[gp.currentMap][i].worldY/gp.tileSize;
                    node[itCol][itRow].solid=true;
                }
            }

            //SETEAR COSTOS
            
        }
    }
}