package e.moi.musidroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import e.moi.musidroid.Utilitaire.PartitionModel;
import l2i013.musidroid.util.InstrumentName;
import l2i013.musidroid.util.NoteName;

public class SaveActivity extends AppCompatActivity {

    private String part;
    private EditText name;

    private  ArrayList<String> listnoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("EXTRA_PART")){ // vérifie qu'une valeur est associée à la clé “EXTRA_TEMPO”
                part = intent.getStringExtra("EXTRA_PART");
            }
        }

        name = findViewById(R.id.editText2);
        listnoms=new ArrayList<>();

    }
    public void onSave(View view){
        String nom =name.getText().toString();
        String[] files = fileList();
        for(int i=0;i<files.length;i++)
            if(files[i].contains(".xml"))
                System.out.println(files[i]);
        saveScore(nom);
        listnoms.add(nom);
    }

    private void saveScore(String fname) {
        FileOutputStream oc = null;

        try {
            oc = openFileOutput(fname+".xml", Context.MODE_PRIVATE);
            oc.write(part.getBytes());
            oc.close();
        }
        catch (FileNotFoundException e) { }
        catch (IOException e) {
            e.printStackTrace();
            try { oc.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }

        }
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


    /* Fonction de chargement juste pour teste */
    public void onClick_play_CXML(View v){
        PartitionModel part;
        int ip;

        Document theDoc= readXMLFile(name.getText().toString()+".xml");
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



    public void onClickOut (View view){
        finish();
    }

    }