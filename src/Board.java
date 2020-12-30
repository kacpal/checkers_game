import java.util.ArrayList;

public class Board {
    Field[][] tab;
    int size = 10;
    int pawnRows = 4;
    UI ui;

    Board(UI ui) {
        tab = new Field[size][size];
        this.ui = ui;
        this.initializeFields();
    }

    void initializeFields() {
        for (int y = 0; y < tab.length; y++) {
            for (int x = 0; x < tab[y].length; x++) {
                tab[y][x] = new Field(x, y);
                tab[y][x].inferStartingContent(size, pawnRows);
            }
        }
    }

    void display() {
        ui.displayBoard(this);
    }

    Field getField(int x, int y) {
        if (y < 0 || x < 0 || x >= size || y >= size)
            return null;
        return tab[y][x];
    };

    int[] directionTowards(Field f, Field t) {
        int dx = Integer.signum(t.x - f.x);
        int dy = Integer.signum(t.y - f.y);
        int[] r = {dx, dy};
        return r;
    }

    Field stepTowards(Field f, Field t, int steps) {
        int[] m = directionTowards(f, t);
        return getField(f.x + (steps * m[0]), f.y + (steps * m[1]));
    }

    Field stepTowards(Field f, Field t) {
        return stepTowards(f, t, 1);
    }

    void moveField(Field f, Field t) {
        t.content = f.content;
        t.promotePawn();
        f.setEmpty();
    }

    int movePawn(Field a, Field b) {
        // this is also suitable as moveQueen
        int r = -1;
        if (!a.isEmpty() && possibleMoves(a).contains(b)) {
            r = 0;
            Field c;
            do {
                c = stepTowards(a, b);
                if (!c.isEmpty()) r += 1;
                moveField(a, c);
                a = c;
            } while (c != b);
        }
        return r;
    }

    ArrayList<Field> possiblePawnMoves(Field a) {
        ArrayList<Field> r = new ArrayList<Field>();
        Field b;
        int[][] modifiers;

        if (a.getColor() == 1)
            modifiers = new int[][]{{1, 1}, {-1, 1}};
        else
            modifiers = new int[][]{{1, -1}, {-1, -1}};

        for (int[] m : modifiers) {
            b = getField(a.x+m[0], a.y+m[1]);
            if (b != null) {
                 if(b.isEmpty()) r.add(b);
                 else if(b.getColor() != a.getColor()) {
                     // we can reuse b here, because it's not needed outside of this if statement anymore
                     b = stepTowards(a, b, 2);
                     if (b != null && b.isEmpty()) r.add(b);
                 }
            }
        }
        return r;
    }

    ArrayList<Field> possibleQueenMoves(Field a) {
        ArrayList<Field> r = new ArrayList<Field>();
        Field b;
        int[][] modifiers = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] m : modifiers) {
            b = a;
            while (true) {
                b = getField(b.x + m[0], b.y + m[1]);
                if (b != null) {
                    if (b.isEmpty()) {
                        r.add(b);
                        // this loop kinda feels like a goto
                        continue;
                    } else if (b.getColor() != a.getColor()) {
                        // step over b and check whether we land on a valid field
                        b = getField(b.x + m[0], b.y + m[1]);
                        if (b != null && b.isEmpty()) r.add(b);
                    }
                }
                break;
            }
        }
        return r;
    }

    ArrayList<Field> possibleMoves(Field a) {
        a.promotePawn(); // redundant, but jic this hasn't been done yet
        if (a.isQueen()) return possibleQueenMoves(a);
        else return possiblePawnMoves(a);
    }

    ArrayList<Field> possiblePawnKills(Field a, Field p) {
        // p stands for previous, we won't be checking for kills in that direction
        ArrayList<Field> r = new ArrayList<Field>();
        Field b;
        int[][] modifiers;

        if (a.getColor() == 1)
            modifiers = new int[][]{{1, 1}, {-1, 1}};
        else
            modifiers = new int[][]{{1, -1}, {-1, -1}};

        int[] fm = null; // forbidden modifier
        if (p != null) fm = directionTowards(a, p);
        for (int[] m : modifiers) {
            if (m == fm) continue;
            b = getField(a.x+m[0], a.y+m[1]);
            if (b != null && !b.isEmpty() && b.getColor() != a.getColor()) {
                    b = stepTowards(a, b, 2);
                    if (b != null && b.isEmpty()) r.add(b);
            }
        }
        return r;
    }

    ArrayList<Field> possibleQueenKills(Field a, Field p) {
        ArrayList<Field> r = new ArrayList<Field>();
        Field b;
        int[][] modifiers = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int[] fm = null; // forbidden modifier
        if (p != null) fm = directionTowards(a, p);
        for (int[] m : modifiers) {
            if (m == fm) continue;
            b = a;
            while (true) {
                b = getField(b.x + m[0], b.y + m[1]);
                if (b != null) {
                    if (b.isEmpty()) continue;
                    else if (b.getColor() != a.getColor()) {
                        // step over b and check whether we land on a valid field
                        b = getField(b.x + m[0], b.y + m[1]);
                        if (b != null && b.isEmpty()) r.add(b);
                    }
                }
                break;
            }
        }
        return r;
    }

    ArrayList<Field> possibleKills(Field a, Field p) {
        a.promotePawn(); // redundant, but jic this hasn't been done yet
        if (a.isQueen()) return possibleQueenKills(a, p);
        else return possiblePawnKills(a, p);
    }

    ArrayList<Field> possibleKills(Field a) {
        return possibleKills(a, null);
    }
}
