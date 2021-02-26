package e.moi.musidroid;

import android.app.Application;

import e.moi.musidroid.Utilitaire.PartitionModel;
import l2i013.musidroid.util.InstrumentName;

/**
 * Created by Moi on 24/02/2018.
 */

public class TheApplication extends Application {

    private PartitionModel partition;

    @Override
    public void onCreate() {
        super.onCreate();
        partition = new PartitionModel(4);
        partition.addpartmodel(InstrumentName.values()[0],4);
    }


    public PartitionModel getPartition() {
        return partition;
    }

    public void setTempo(int t){
        partition.setTempo(t);
    }


}