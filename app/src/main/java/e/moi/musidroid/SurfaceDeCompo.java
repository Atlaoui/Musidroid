package e.moi.musidroid;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Note;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.MidiFile2I013;


public class SurfaceDeCompo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String PART = "EXTRA_PART";
    private TheApplication app;
    private SeekBar seekBar;
    private int tempo;
    private int octave;
    private Spinner spinnerInstrument;
    private TextView textInstrument;
    private TextView textOctave;

    /* Pour connaitre si la seek bar augmante ou déscent*/
    private int lastSeekbarVal =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_de_compo);

        /*Ont adapte le nombre de temps en fontion de la position de l'apareille*/
        if(getResources().getDisplayMetrics().widthPixels>getResources().getDisplayMetrics().heightPixels){
            Surface.setNbtemps(20);
        }else//comment adapter le nombre de pixel en fonction de l'écrant
            Surface.setNbtemps(8);

        /* Ont recuper ce que l'on a sauvgarder */
        Intent intent = getIntent();

        /*Ont intialise l'application*/
        app= (TheApplication) (this.getApplication());

        /*Ont recuper le tempo*/
        if (intent != null){
            if (intent.hasExtra("EXTRA_TEMPO")){ // vérifie qu'une valeur est associée à la clé “EXTRA_TEMPO”
                tempo = intent.getIntExtra("EXTRA_TEMPO",4);
                app.setTempo(tempo);
            }
        }

        /*Intialisation du spinner et lui associe les valeur du enume d'instrument*/
        spinnerInstrument = findViewById(R.id.InstrumentSpinner);
        spinnerInstrument.setOnItemSelectedListener(this);
        List<InstrumentName> list = Arrays.asList(InstrumentName.values());
        ArrayAdapter<InstrumentName> dataAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerInstrument.setAdapter(dataAdapter);
        spinnerInstrument.setSelection(0);


        /*Intialisation de la seekbar et de l'octave */
        seekBar = findViewById(R.id.seekBarTemps);
        seekBarTemps();
        octave=4;
        /*Ont rend la surface view transparante*/
        SurfaceView sfvTrack = findViewById(R.id.SurfaceView);
        sfvTrack.setZOrderOnTop(true);    // necessary
        SurfaceHolder sfhTrackHolder = sfvTrack.getHolder();
        sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);


        /* Initialisaztion des text view*/
        textInstrument= findViewById(R.id.TextViewInstrumen);
        textOctave =findViewById(R.id.TextViewOctave);
        textOctave.setText(" Octave "+String.valueOf(octave));
        textInstrument.setText("Num instru "+String.valueOf(app.getPartition().getNum_instrum()+1));

    }

  public void OnClickPlay(View v){
      MediaPlayer.OnPreparedListener nPrepared = new MediaPlayer.OnPreparedListener(){
          public void onPrepared(MediaPlayer playerM) {
          }
      };
      MediaPlayer nPlayer = new MediaPlayer();
      nPlayer.setOnPreparedListener(nPrepared);
      try{
          File f = new File(app.getFilesDir(),"tmp.mid");
          MidiFile2I013.write(f, app.getPartition());
          nPlayer.setDataSource((f).getPath());
          nPlayer.prepare();
          if(!nPlayer.isPlaying()){
              nPlayer.start();
          }
      }
      catch(Exception e) {
        System.err.println(e.getMessage());
      }
  }

    public void onClickBack(View v){
        this.finish();
    }

    /* Gestion de l'octave */
    public void onClickSetOctaveUp(View v){
        if (octave==9) return;
        octave++;
        app.getPartition().getInstrucour(app.getPartition().getInstrumentc()).setOctave(octave);
        updateTextOctave();
    }

    public void onClickSetOctaveDown(View v){
        if (octave==1) return;
        octave--;
        app.getPartition().getInstrucour(app.getPartition().getInstrumentc()).setOctave(octave);
        updateTextOctave();
    }


    /* Gestion de l'ajoue de plusieur fois le meme instrument */
    private void onAjouteInstru(){
        int pos=app.getPartition().getInstrumentc()+(app.getPartition().getNum_instrum()*(app.getPartition().getNbInstrument()+1));
        if(app.getPartition().getModelcour(pos)==null && app.getPartition().getInstrucour(pos)==null) {
            app.getPartition().addpartmodel(InstrumentName.values()[app.getPartition().getInstrumentc()], octave);
        }

    }

    public void AjouterInstrument(View v){
        app.getPartition().setNum_instrum(1);
        onAjouteInstru();
        ((Surface) findViewById(R.id.SurfaceView)).reDraw();
        updateTextInstrument();
    }

    public void RevenirInstrument(View v){
        app.getPartition().setNum_instrum(-1);
        if (app.getPartition().getNum_instrum()<=0)
            app.getPartition().resetNum_instrum();
        else
        ((Surface) findViewById(R.id.SurfaceView)).reDraw();
        updateTextInstrument();
    }

    /* Recupération de l'instrument courant */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        /* Ont teste si la partition existe deja sinon ont la crée une nouvelle */
        app.getPartition().resetNum_instrum();
        if(app.getPartition().getModelcour(i)==null && app.getPartition().getInstrucour(i)==null) {
            app.getPartition().addpartmodel(InstrumentName.values()[i], octave);
        }

        /* En ajuste les index et update la surfaceView */
        app.getPartition().setInstrumentc(i);
        updateTextInstrument();
        updateTextOctave();
        app.getPartition().setSeekbartemps(0);
        ((Surface) findViewById(R.id.SurfaceView)).reDraw();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    /* Gestion des temps en fonction de la seekbar */
    public void seekBarTemps(){
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        /* Ont recuper le progress de la seekBar et update la surfaceView */
                        app.getPartition().set_Seekbar_temps_pm(progress-lastSeekbarVal);
                        lastSeekbarVal=progress;
                        ((Surface) findViewById(R.id.SurfaceView)).reDraw();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
    }
    public void onClickSave(View view){
        String part = app.getPartition().toXML();

        Intent playIntent = new Intent(this, SaveActivity.class);
        playIntent.putExtra(PART,part);
        startActivity(playIntent);

    }

    /*Gestion des Edit text*/
    private void updateTextInstrument(){
        textInstrument.setText("Num instru "+String.valueOf(app.getPartition().getNum_instrum()+1));
    }

    private void updateTextOctave(){
        textOctave.setText(" Octave "+String.valueOf(octave));
    }


    /* Suprimer une seul partition */
    public void onClickRemove(View v){//il faut encor geré tout les cas relative l'ip des model
        if (app.getPartition().getNum_instrum()>0) {
            app.getPartition().removePartModel(/*app.getPartition().getInstrumentc()*/);
            app.getPartition().resetNum_instrum();
            updateTextInstrument();
            updateTextOctave();
            ((Surface) findViewById(R.id.SurfaceView)).reDraw();
        }
    }

    /* free */
    public void onRestAll(View v){
        app.getPartition().getParts().clear();
        app.getPartition().getListpos().clear();
        app.getPartition().addpartmodel(InstrumentName.values()[0],octave);
        app.getPartition().setInstrumentc(0);
        spinnerInstrument.setSelection(0);
        ((Surface) findViewById(R.id.SurfaceView)).reDraw();
        updateTextInstrument();
        updateTextOctave();
    }


  }