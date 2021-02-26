package e.moi.musidroid.Utilitaire;

import java.util.ArrayList;
import java.util.Iterator;

import e.moi.musidroid.Utilitaire.Position;


/**
 * Created by Moi on 24/02/2018.
 */

 public class Model {
    private ArrayList<Position> xys;
    private final int num_instrum;
    private int ip;
    public Model(int ip,int num_instrum) {
        xys = new ArrayList<>();
        this.ip=ip;
        this.num_instrum=num_instrum;
    }


    /* Enleve la note en ça totaliter */
    public void RemoveAll(int posX,int posY ,int pos,int t){
            Iterator<Position> iterator = xys.iterator();
            while (iterator.hasNext()){
                Position position = iterator.next();
                /*avec cette condition ont s'assure que quelque soit la position que l'ont touche en supprime toutes les position qui apartien au temps de la note*/
                if(position.getX()>=posX-pos && position.getX()<=posX-pos+t && position.getY()==posY){
                    iterator.remove();// remove element if match condition
                }
            }
    }

    public ArrayList<Position> getArray (){
        return xys;
    }

    public int getIp(){
        return ip;
    }

    public void setIp(int ip) {
        this.ip += ip;
    }

    public int getNum_instrum() {
        return num_instrum;
    }
}