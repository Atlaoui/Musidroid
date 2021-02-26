package e.moi.musidroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import e.moi.musidroid.Utilitaire.PartitionModel;
import e.moi.musidroid.Utilitaire.Position;

import static java.lang.Math.min;

/**
 * Created by Moi on 27/02/2018.
 */

public class Surface extends SurfaceView implements SurfaceHolder.Callback{
    private TheApplication app;

    /* les dimention voulu de notre surface de composition */
    private static final int nbnotes = 12;
    private static int nbtemps =8;

    /* Pour stocker les dimantion du canvas */
    private int longeur;
    private int largeur;

    /* Pour stocker les valeur précedante du onToucheEvents */
    private int PrevTemp;
    private int PrevNote;

    /* Variable pour géré les déplacement sur la surface view et update
    * temps des notes */
    private static int decal=0;

    private boolean MOVE_RIGHT_TRIGER=false;
    private boolean MOVE_RIGHT=false;
    private boolean MOVE_LEFT=false;



    public Surface(Context context) {
        super(context);
        getHolder().addCallback(this);
        app = (TheApplication) (context.getApplicationContext());

    }

    public Surface(Context context, AttributeSet attrs) {
        super(context, attrs);
       getHolder().addCallback(this);
       app = (TheApplication) (context.getApplicationContext());
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        longeur=height;
        largeur=width;
        reDraw();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
    @SuppressLint("WrongCall")
    void reDraw() {
        Canvas c = getHolder().lockCanvas();
        if (c != null) {
            this.onDraw(c);
            getHolder().unlockCanvasAndPost(c);
        }
    }

   @Override
    public void onDraw(Canvas c) {
        /* Ont recuper la partition */
        PartitionModel m = app.getPartition();
        /* Ont recuper la valeur de la seekbar */
        int seekB = app.getPartition().getSeekbartemps();
        int instrumentcourant = app.getPartition().getInstrumentc();
        Paint p = new Paint();
        Paint p2= new Paint();
        p2.setColor(Color.BLACK);
        int cpt;
        p.setColor(Color.BLUE);
        for (int i = longeur/nbnotes/2; i < longeur; i += longeur / nbnotes) {
            cpt=1;
            for (int j = largeur/nbtemps /2; j < largeur; j += largeur / nbtemps) {
                c.drawCircle(j, i, min(longeur/nbnotes /2,largeur/nbtemps/2), p);
                c.drawText(String.valueOf(cpt+seekB),j,i,p2);
                cpt++;
            }
        }
       p.setColor(Color.rgb((int)(Math.random()*256),255,(int)(Math.random()*256)));
       ArrayList<Position> xys = m.getModelcour(instrumentcourant).getArray();
        for (int i = 0; i < xys.size(); i++) {
            /* Ont Verifie que la pos est dans la fenetre d'affichage  et on fait le min du pas en fonction de la largeur et de la longeur */
           if(xys.get(i).getX()>=seekB && xys.get(i).getX() <= largeur+seekB)
                c.drawCircle(((xys.get(i).getX()-seekB)*((float)(largeur/nbtemps))+largeur/nbtemps/2), ((xys.get(i).getY())*(float)(longeur/nbnotes)+longeur/nbnotes/2), min(longeur/nbnotes /2,largeur/nbtemps/2), p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /* Ont recup l'action*/
        float x =  event.getX();
        float y =  event.getY();
        int action = event.getAction();

        int seekB=app.getPartition().getSeekbartemps();

        /* Ont discretise */
        int temps = (int) (x / (float)(largeur / nbtemps));
        int note = (int) (y / (float) (longeur/ nbnotes));

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                /* Ont recuper la position de départ du doigt  */
                if (MOVE_LEFT)
                    app.getPartition().set_Seekbar_temps_pm(-1);
                if(MOVE_RIGHT){
                    app.getPartition().set_Seekbar_temps_pm(1);
                    decal++;
                }
                else {
                    PrevTemp = temps;
                    PrevNote = note;
                }
                return true;
            }
            case MotionEvent.ACTION_UP:{
                /* Ont recuper la position d'arriver du doigt */
                MOVE_RIGHT=false;
                MOVE_LEFT=false;
                MOVE_RIGHT_TRIGER=false;
                int t=temps-PrevTemp;

                /* Ont teste que ont a pas decaler ver le bas ou ver le haut */
                if(note == PrevNote)
                    /* Ont teste que en va pas de droite a gauche */
                    if(t>0) {
                        app.getPartition().addRemove(PrevTemp-decal, PrevNote, t+decal);
                    }else if(t==0)
                        app.getPartition().addRemove(PrevTemp, PrevNote, t);
                /* Ont update la surface view */
                reDraw();
                decal=0;
                return true;
            }
            case MotionEvent.ACTION_MOVE:{
                //On gere les condition du pseudo scroll ver la droite
                if(MOVE_RIGHT){
                    decal++;
                    app.getPartition().set_Seekbar_temps_pm(1);
                    reDraw();
                }else if(temps==nbtemps-1 && MOVE_RIGHT_TRIGER){
                    MOVE_RIGHT=true;
                }
                if(temps==nbtemps-2)
                    MOVE_RIGHT_TRIGER=true;


                //Ont gere les condition du pseudo scroll ver la gauche
                if(MOVE_LEFT && seekB>1){
                    app.getPartition().set_Seekbar_temps_pm(-1);
                    reDraw();
                }

                if(temps==1 && temps-PrevTemp<0){
                    app.getPartition().set_Seekbar_temps_pm(-1);
                   if(seekB>1) {
                       reDraw();
                       MOVE_LEFT=true;
                   }
                   else
                       app.getPartition().setSeekbartemps(0);
                }

                return true;
            }
            default:
                return false;
        }
    }

    /* Pour changer le nombre de temps afficher en fonction de la position du téléphone */
    public static void setNbtemps(int nbtemps) {
        Surface.nbtemps = nbtemps;
    }
}
