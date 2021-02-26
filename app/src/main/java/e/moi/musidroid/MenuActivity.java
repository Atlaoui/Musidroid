package e.moi.musidroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MenuActivity extends AppCompatActivity {
    private final String TEMPO = "EXTRA_TEMPO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onClickExit(View v){
        this.finish();
    }


    public void onClickPlayCompo(View v) {

        EditText et = findViewById(R.id.tempsEditText);
        String tempsstr = et.getText().toString();
        int tempo= Integer.parseInt(tempsstr);

        Intent playIntent = new Intent(this, SurfaceDeCompo.class);
        playIntent.putExtra(TEMPO,tempo);
        startActivity(playIntent);
    }
    public void onClickPlayMusiq(View v) {
        Intent playIntent = new Intent(this, PlayMusique.class);
        startActivity(playIntent);
    }
}
