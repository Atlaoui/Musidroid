package e.moi.musidroid;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import e.moi.musidroid.Utilitaire.PartitionModel;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.NoteName;

public class PlayMusique extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    private MediaPlayer mediaPlayer;
    private List<String> ListSongs;
    private Spinner spinnerSongs;
    private int musique_cour=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_musique);
        ListSongs = new ArrayList<>();
        mediaPlayer= new MediaPlayer();//juste pour que ca ne crash pas

        Button buttonPlay = findViewById(R.id.play);
        Button buttonPause = findViewById(R.id.pause);
        buttonPlay.setOnClickListener(buttonPlayOnClickListener);
        buttonPause.setOnClickListener(buttonPauseOnClickListener);


        /*Ont récuper les nom des fichier sauvgarder*/
        String[] songs = fileList();
        ListSongs = new ArrayList<>();

        for (int i = 0; i < songs.length; i++) {
            String nom = songs[i];
            if (nom.contains(".xml"))
                ListSongs.add(songs[i]);
        }


        spinnerSongs =findViewById(R.id.SongsSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ListSongs);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSongs.setAdapter(spinnerArrayAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        /*Ont recuper la note a charger */
        musique_cour=i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    private Document readXMLFile(String fname) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(openFileInput(fname));
        } catch (Exception ex) {
            return null;
        }
    }

    /* the doc est toujour null  */
    public void onClick_play_SONGS(View v){
        PartitionModel part;
        int ip;

        Document theDoc= readXMLFile(ListSongs.get(musique_cour));
        if( theDoc != null) {
            part = new PartitionModel(Integer.parseInt(theDoc.getDocumentElement().getAttribute("Partition_tempo")));
            NodeList Linstru = theDoc.getChildNodes();
            NodeList LNotes;
            for (int i = 0; i < Linstru.getLength(); i++) {
                ip = Integer.parseInt(Linstru.item(i).getAttributes().getNamedItem("InstrumentPart_Name").getNodeValue());
                part.addpartmodel(InstrumentName.values()[ip], Integer.parseInt(Linstru.item(i).getAttributes().getNamedItem("Octave").getNodeValue()));
                LNotes = Linstru.item(i).getChildNodes();
                for (int j = 0; j < LNotes.getLength(); j++) {
                    part.getInstrucour(ip).addNote(Integer.parseInt(LNotes.item(j).getAttributes().getNamedItem("Note_instant").getNodeValue()), NoteName.values()[Integer.parseInt(LNotes.item(j).getAttributes().getNamedItem("name").getNodeValue())], Integer.parseInt(LNotes.item(j).getAttributes().getNamedItem("duree").getNodeValue()));
                }
            }
        }
    }



    Button.OnClickListener buttonPlayOnClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                Toast.makeText(PlayMusique.this, "mediaPlayer.start()",Toast.LENGTH_LONG).show();
            }
        }
    };

    Button.OnClickListener buttonPauseOnClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                Toast.makeText(PlayMusique.this,"mediaPlayer.pause()",Toast.LENGTH_LONG).show();
            }
        }
    };


    public void onClickExit(View v){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        this.finish();
    }


}
