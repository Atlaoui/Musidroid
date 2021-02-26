package e.moi.musidroid.Utilitaire;

import java.util.ArrayList;

import e.moi.musidroid.Utilitaire.Model;
import e.moi.musidroid.Utilitaire.Position;
import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Partition;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.NoteName;

/***
 * Created by Moi on 24/03/2018.
 */

public class PartitionModel extends Partition {
    private ArrayList<Model> listpos;
    private int seekbartemps;
    private int instrucourant;

    /* le nombre d'instrument du enum pour calculer le décalage */
    private final int nbInstrument=InstrumentName.values().length;
    private int num_instrum;

    public PartitionModel(int t) {
        super(t);
        listpos = new ArrayList<>();
        seekbartemps=0;
        instrucourant =0;

         /* De base in n y a pas d'instrument ajouter */
         num_instrum=0;

    }

    public void addpartmodel(InstrumentName n,int o) {
        if (o > 10 || o < 0) {
            return;
        }
        int b;
        b=super.addPart(n, o);
        if (b!=-1)
            this.listpos.add(new Model(n.getNum()+(num_instrum*(nbInstrument+1)),n.getNum()));
    }

    /*Verifier les temps*/
    public void addRemove(int x, int y ,int t) {
        InstrumentPart p = getInstrucour(instrucourant);
        Model m = getModelcour(p.getInstrumentNum());
        /* Ont test si d'abor en a pas cliquer sur une note que l'ont veux enlever */
        for(int i=0 ;i<m.getArray().size();i++){
            if(m.getArray().get(i).getX()-seekbartemps==x && m.getArray().get(i).getY()==y){

                    p.removeNote(x-m.getArray().get(i).getPosInel()+seekbartemps, NoteName.ofNum(y));

                if (m.getArray().get(i).getDuration()==0)
                    m.getArray().remove(i);
                else
                    m.RemoveAll(m.getArray().get(i).getX(),m.getArray().get(i).getY(),m.getArray().get(i).getPosInel(),m.getArray().get(i).getDuration());
                return;
            }
        }

        /* Ont teste si ont peux ajouter la note sur cette intervalle de temps a faire sinoin en remouve la note qui si trouve */
        for(int i=0 ;i<m.getArray().size();i++)
            if(m.getArray().get(i).getX()-seekbartemps>=x && m.getArray().get(i).getX()-seekbartemps<=x+t && m.getArray().get(i).getY()==y)
                return;

        /* Si elle n'est pas présente dans l'array liste de possition ont ajoute les position et la note */
        if (t>0) {
            int ofset = 0;
            for (int i = 0; i <= t; i++) {/* Si c'est la 1er position ajouter */
                m.getArray().add(new Position(x + seekbartemps + i, y, ofset++,t));
            }
            p.addNote(x+seekbartemps,NoteName.ofNum(y),t+1);
        }else{
            m.getArray().add(new Position(x + seekbartemps,y,t,t));
            p.addNote(x+seekbartemps,NoteName.ofNum(y),1);
        }

    }

     public Model getModelcour(int instrumentc) {
        for (Model m: listpos) {
            if (m.getIp()==instrumentc+(num_instrum*(nbInstrument+1))){
                return  m;
            }
        }
        return null;
    }

    public InstrumentPart getInstrucour(int i){
         for (int index =0;index<listpos.size();index++)
             if (listpos.get(index).getIp()==i+(num_instrum*(nbInstrument+1)))
                 return this.getParts().get(index);

         return null;
    }

    public void removePartModel(){
        InstrumentPart p = getInstrucour(instrucourant);
        Model m = getModelcour(p.getInstrumentNum());
        m.getArray().clear();
        p.getNotes().clear();
    }

    /*pas  Fonctionelle mais elle censé réajusté les ip de la classe model mit pour retrouvé l'instrument de facon a ce que ce soit
     * complétement dynamique */

    public void removePartModel(int i){
        InstrumentPart p = getInstrucour(instrucourant);
        Model m2 = getModelcour(p.getInstrumentNum());
        super.getParts().remove(p);
        this.listpos.remove(m2);
        for (Model m: listpos ) {
            if(m.getNum_instrum()==i && m.getIp()>i){
                m.setIp(-nbInstrument);
            }
        }

    }


     public String toXML(){
        String part="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?> \n<Partition_tempo=\""+super.getTempo()+"\">";
         for (int i=0;i<getParts().size();i++){
             part+="\n\t<InstrumentPart_Name=\""+i+"\" Octave=\"\""+getParts().get(i).getOctave()+"\" Instrument=\""+getParts().get(i).getInstrument()+"\">";
          for (int j=0;j<getParts().get(i).getNotes().size();j++){
                 part+="\n\t\t"+"<Note_instant=\""+getParts().get(i).getNotes().get(j).getInstant()+"\" name=\""+getParts().get(i).getNotes().get(j).getName()+"\" duree=\""+getParts().get(i).getNotes().get(j).getDuration()+"\"*/>";
            }
            part+="\n\t</InstrumentPart>";
        }
        part+="\n</Partition>";

         return part;
     }





    /*Fonction de supression et de remise en ordre des instance pas encor totalment fonctionelle*/
    public void remove_Part_Model(int i){
        if (listpos.size()>1)
            for (int index =0;index<listpos.size();index++)
                if (listpos.get(index).getIp()==i+(num_instrum*(nbInstrument+1))){
                    this.removePart(index);
                    int ipcour=i+(num_instrum*(nbInstrument+1));
                    for (int j=0;j<listpos.size();j++)//En remet en place les indice de façpn a ce que en est pas des creux dans les affichage
                        if (listpos.get(index).getIp()==ipcour && ipcour>nbInstrument){
                            listpos.get(index).setIp(-(nbInstrument+1));
                            ipcour+=nbInstrument+1;
                        }
                    listpos.remove(index);
                    return;
                }
    }



    public int getInstrumentc() {
        return instrucourant;
    }

    public void setInstrumentc(int instrumentc) {
        this.instrucourant = instrumentc;
    }

    public void setSeekbartemps(int seekbartemps) {
        this.seekbartemps = seekbartemps;
    }

    public void set_Seekbar_temps_pm(int i){this.seekbartemps+=i;}

    public int getSeekbartemps() {return seekbartemps;}

    public ArrayList<Model> getListpos() {
        return listpos;
    }

    public  int getNbInstrument() {
        return nbInstrument;
    }

    public int getNum_instrum() {
        return num_instrum;
    }

    public void setNum_instrum(int num_instrum) {
        this.num_instrum += num_instrum;
    }

    public void resetNum_instrum(){
        this.num_instrum=0;
    }
}

