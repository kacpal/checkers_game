import java.io.*;

public class Field implements Serializable {
    Pawn content = Pawn.EMPTY;
    int boardsize;
    int x, y;

    Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pawn getContent() {
        return content;
    }

    /*
    Checks if position of the pawn is legal on current filed, basing on coordinate's parity.
     */
    static boolean allowed(int x, int y) {
        return ((x % 2 == 1 && y % 2 == 0) || (x % 2 == 0 && y % 2 == 1));
    }

    void setEmpty() {
        content = Pawn.EMPTY;
    }

    boolean isEmpty() {
        return (content == Pawn.EMPTY);
    }

    boolean isQueen() {
        return (content == Pawn.BLACK_QUEEN || content == Pawn.WHITE_QUEEN);
    }

    Pawn getColor() {
        if (content == Pawn.BLACK || content == Pawn.BLACK_QUEEN)
            return Pawn.BLACK;
        else if (content == Pawn.WHITE || content == Pawn.WHITE_QUEEN)
            return Pawn.WHITE;
        else return Pawn.EMPTY;
    }

    boolean allowed() {
        return allowed(x, y);
    }

    /*
    Checks if pawn is on opposed border field, so it can promote.
     */
    private boolean canPromote() {
        if (y == 0 && content == Pawn.WHITE)
            return true;
        else if (y == boardsize - 1 && content == Pawn.BLACK)
            return true;
        else return false;
    }

    void promotePawn() {
        if (content.promote() != Pawn.EMPTY && canPromote())
            content = content.promote();
    }

    /*
    Initial method for placing the pawns.
     */
    void inferStartingContent(int boardsize, int pawnRows) {
        this.boardsize = boardsize;
        if (allowed(x, y)) {
            if (y > boardsize - pawnRows - 1) content = Pawn.WHITE;
            else if (y < pawnRows) content = Pawn.BLACK;
            else content = Pawn.EMPTY;
        } else {
            content = Pawn.FILLER;
        }
    }

    @Override
    public Field clone() {
        Field newField = new Field(x, y);
        newField.content = this.getContent();
        return newField;
    }
}
