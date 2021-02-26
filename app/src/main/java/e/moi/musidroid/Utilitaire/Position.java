package e.moi.musidroid.Utilitaire;

/**
 * Created by Moi on 24/02/2018.
 */

public class Position {
    private int x;
    private int y;

    /* Dans le cas ou la note a plusieur temps en stock la place de la position en fonction des autre pour  */
    private int posInel;

    /* Ont stock la position de la dernier pour savoir ou s'arreter */
    private int duration;

    public Position(int x, int y, int posInel, int duration) {
        this.x = x; this.y = y; this.duration=duration;
        this.posInel=posInel;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getDuration() {
        return duration;
    }

    public int getPosInel() {
        return posInel;
    }

    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj == this) return true;
        if(this.getClass() != obj.getClass()) return false;
        Position Other = (Position) obj;
        if(Other.getX() == this.getX())
            if(Other.getY()==this.getY())
                if(Other.getDuration()==this.getDuration())
                    if (Other.getPosInel()==this.getPosInel())
                        return true;
        return false;
    }
}