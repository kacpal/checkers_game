import java.util.ArrayList;

public class Board {
    Field[][] tab;
    int size = 10;
    int pawnRows = 4;

    UI ui;
    GameState gameState;

    Board() {}

    Board(UI ui) {
        this.ui = ui;
        this.tab = new Field[size][size];
        this.gameState = new GameState();
        this.initializeFields();
    }

    void initializeFields() {
        Field f;
        for (int y = 0; y < tab.length; y++) {
            for (int x = 0; x < tab[y].length; x++) {
                f = tab[y][x] = new Field(x, y);
                f.inferStartingContent(size, pawnRows);
                if (f.getColor() == 1) {
                    gameState.playerA.add(f);
                } else if (f.getColor() == 2) {
                    gameState.playerB.add(f);
                }
            }
        }
    }

    void setTab(Board newBoard) {
        // TODO: delete this method
        this.tab = newBoard.tab;
    }

    void display() {
        ui.displayBoard(this);
    }

    Field getField(int x, int y) {
        if (y < 0 || x < 0 || x >= size || y >= size)
            return null;
        return tab[y][x];
    }

    GameState getGameState() {
        return gameState;
    }

    boolean areOfOppositeColor(Field a, Field b) {
        return ((a.getColor() == 1 && b.getColor() == 2) || (a.getColor() == 2 && b.getColor() == 1));
    }

    int[] directionTowards(Field f, Field t) {
        int dx = Integer.signum(t.x - f.x);
        int dy = Integer.signum(t.y - f.y);
        return new int[]{dx, dy};
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
        // TODO: simplify this condition
        if (!a.isEmpty() && (possibleMoves(a).contains(b) || possibleKills(a).contains(b))) {
            // move is valid
            r = 0;
            gameState.updateHistory(a, b);
            // step over every field until we reach the target
            Field c;
            do {
                c = stepTowards(a, b);
                if (!c.isEmpty()) {
                    // we happen to kill
                    r += 1;
                    gameState.updateHistory(c, null);
                }
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

        // pawns move in one direction only
        // these modifiers indicate diagonals along which they can move
        if (a.getColor() == 1)
            modifiers = new int[][]{{1, 1}, {-1, 1}};
        else if (a.getColor() == 2)
            modifiers = new int[][]{{1, -1}, {-1, -1}};
        else
            modifiers = new int[][]{};

        for (int[] m : modifiers) {
            b = getField(a.x+m[0], a.y+m[1]);
            if (b != null) {
                 if(b.isEmpty()) r.add(b);
                 else if(areOfOppositeColor(a, b)) {
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

        // these modifiers indicate diagonals along which we can move
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

        // modifiers indicate diagonals along which pawns can kill
        int[][] modifiers = new int[][]{{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        // there might be a forbidden modifier (fm) indicating a diagonal we came from
        int[] fm = null;
        if (p != null) fm = directionTowards(a, p);

        for (int[] m : modifiers) {
            if (m == fm) continue;
            b = getField(a.x+m[0], a.y+m[1]);
            if (b != null && !b.isEmpty() && areOfOppositeColor(a, b)) {
                    b = stepTowards(a, b, 2);
                    if (b != null && b.isEmpty()) r.add(b);
            }
        }
        return r;
    }

    ArrayList<Field> possibleQueenKills(Field a, Field p) {
        ArrayList<Field> r = new ArrayList<Field>();
        Field b;

        // these modifiers indicate diagonals along which we can kill
        int[][] modifiers = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        // there might be a forbidden modifier (fm) indicating a diagonal we came from
        int[] fm = null;
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
        // p stands for previous, we won't be checking for kills in that direction
        a.promotePawn(); // redundant, but jic this hasn't been done yet
        if (a.isQueen()) return possibleQueenKills(a, p);
        else return possiblePawnKills(a, p);
    }

    ArrayList<Field> possibleKills(Field a) {
        return possibleKills(a, null);
    }

    @Override
    public Board clone() {
        UI newUI = new ConsoleUI();
        GameState newGameState = new GameState();
        Field[][] newTable = new Field[size][size];

        Field f;
        for (int y = 0; y < tab.length; y++) {
            for (int x = 0; x < tab[y].length; x++) {
                f = newTable[y][x] = getField(x, y).clone();
                if (f.getColor() == 1) {
                    newGameState.playerA.add(f);
                } else if (f.getColor() == 2) {
                    newGameState.playerB.add(f);
                }
            }
        }

        Board newBoard = new Board();
        newBoard.ui = newUI;
        newBoard.gameState = newGameState;
        newBoard.tab = newTable;

        return newBoard;
    }
}
