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

    ArrayList<Field> myPawns(Pawn color) {
        if (color == Pawn.BLACK)
            return playerA;
        else if (color == Pawn.WHITE)
            return playerB;
        else
            return null;
    }

    void updateHistory(Field from, Field to) {
        history.add(new FieldPair(from, to));
        // passing t as null means the piece was removed from the board (killed)
        if (to != null) {
            // only one player owns a piece occupying given field, here we infer which one
            // and append target destination field to the list of their controlled pieces
            if (playerA.contains(from))
                playerA.add(to);
            else if (playerB.contains(from))
                playerB.add(to);
        }
        playerA.remove(from);
        playerB.remove(from);
    }

    void surrender(Pawn color) {
        if (color == Pawn.BLACK)
            playerA.clear();
        else if (color == Pawn.WHITE)
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
