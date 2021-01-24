import java.util.ArrayList;

public class GameState {
    ArrayList<FieldPair> history;
    ArrayList<Field> playerA;
    ArrayList<Field> playerB;

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
        // passing t as null means the piece was removed from the board (killed)
        if (t != null) {
            // only one player owns a piece occupying given field, here we infer which one
            // and append target destination field to the list of their controlled pieces
            if (playerA.contains(f))
                playerA.add(t);
            else if (playerB.contains(f))
                playerB.add(t);
        }
        playerA.remove(f);
        playerB.remove(f);
    }

    void surrender(int color) {
        if (color == 1)
            playerA.clear();
        else if (color == 2)
            playerB.clear();
    }

    int gameover() {
        if (playerA.isEmpty() && playerB.isEmpty())
            return 3;
        if (playerA.isEmpty())
            return 2;
        if (playerB.isEmpty())
            return 1;
        return 0;
    }
}
