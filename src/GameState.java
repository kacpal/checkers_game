import java.util.ArrayList;

public class GameState {
    ArrayList<FieldPair> history;
    ArrayList<Field> playerA;
    ArrayList<Field> playerB;

    boolean playerAStale = false;
    boolean playerBStale = false;

    GameState() {
        history = new ArrayList<FieldPair>();
        playerA = new ArrayList<Field>();
        playerB = new ArrayList<Field>();
    }

    ArrayList<Field> myPawns(int color) {
        if (color == 1)
            return playerA;
        else if (color == 2)
            return playerB;
        else
            return null;
    }

    void updateHistory(Field f, Field t) {
        history.add(new FieldPair(f, t));
        if (t != null) {
            if (playerA.contains(f))
                playerA.add(t);
            else if (playerB.contains(f))
                playerB.add(t);
        }
        playerA.remove(f);
        playerB.remove(f);
    }

    void setStale(int color) {
        if (color == 1) playerAStale = true;
        else if (color == 2) playerBStale = true;
    }

    void unsetStale(int color) {
        if (color == 1) playerAStale = false;
        else if (color == 2) playerBStale = false;
    }

    void surrender(int color) {
        if (color == 1)
            playerA.clear();
        else if (color == 2)
            playerB.clear();
    }

    int gameover() {
        if (playerA.isEmpty() || (playerAStale && !playerBStale))
            return 2;
        if (playerB.isEmpty() || (playerBStale && !playerAStale))
            return 1;
        if (playerAStale && playerBStale)
            return 3;
        return 0;
    }
}
