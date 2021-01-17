import java.io.*;

public class Field implements Serializable {
    int content = 0;
    int x, y;

    private int promotionColor = 0;

    Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getContent() {
        return content;
    }

    static boolean allowed(int x, int y) {
        return ((x % 2 == 1 && y % 2 == 0) || (x % 2 == 0 && y % 2 == 1));
    }

    void setEmpty() {
        content = 0;
    }

    boolean isEmpty() {
        return (content == 0);
    }

    boolean isQueen() {
        return ((content & 4) > 0);
    }

    int getColor() {
        // 01 is White, 10 is Black
        return (content & 3);
    }

    boolean allowed() {
        return allowed(x, y);
    }

    private void setPromotionField(int boardsize) {
        if (y == 0) promotionColor = 2;
        else if (y == (boardsize - 1)) promotionColor = 1;
        else promotionColor = 0;
    }

    int getPromotionColor() {
        return promotionColor;
    }

    void promotePawn() {
        if (getColor() == getPromotionColor())
            content |= 4;
    }

    void inferStartingContent(int boardsize, int pawnRows) {
        setPromotionField(boardsize);
        if (allowed(x, y)) {
            if (y > boardsize - pawnRows - 1) content = 2;
            else if (y < pawnRows) content = 1;
            else content = 0;
        } else {
            content = -1;
        }
    }

    boolean canReach(int x, int y) {
        x = Math.abs(x - this.x);
        if (x != Math.abs(y - this.y))
            return false;
        else if (x > 1)
            return isQueen();
        else
            return (x == 1);
    }

    boolean canReach(Field f) {
        return canReach(f.x, f.y);
    }

    @Override
    public Field clone() {
        Field newField = new Field(x, y);
        newField.content = this.getContent();
        return newField;
    }
}
