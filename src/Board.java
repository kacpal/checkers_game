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

    Board(UI ui, int size, int pawnRows) {
        this.ui = ui;
        this.size = size;
        this.pawnRows = pawnRows;
        this.tab = new Field[size][size];
        this.gameState = new GameState();
        this.initializeFields();
    }

    void initializeFields() {
        Field field;
        for (int y = 0; y < tab.length; y++) {
            for (int x = 0; x < tab[y].length; x++) {
                field = tab[y][x] = new Field(x, y);
                // Set field's color, according to it's position on the board
                field.inferStartingContent(size, pawnRows);
                if (field.getColor() == Pawn.BLACK) {
                    gameState.playerA.add(field);
                } else if (field.getColor() == Pawn.WHITE) {
                    gameState.playerB.add(field);
                }
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
    }

    GameState getGameState() {
        return gameState;
    }

    boolean areOfOppositeColor(Field a, Field b) {
        return ((a.getColor() == Pawn.BLACK && b.getColor() == Pawn.WHITE)
                || (a.getColor() == Pawn.WHITE && b.getColor() == Pawn.BLACK));
    }

    int[] directionTowards(Field from, Field to) {
        int dx = Integer.signum(to.x - from.x);
        int dy = Integer.signum(to.y - from.y);
        return new int[]{dx, dy};
    }

    Field stepTowards(Field from, Field to, int steps) {
        int[] m = directionTowards(from, to);
        return getField(from.x + (steps * m[0]), from.y + (steps * m[1]));
    }

    Field stepTowards(Field from, Field to) {
        return stepTowards(from, to, 1);
    }

    void moveField(Field from, Field to) {
        to.content = from.content;
        to.promotePawn();
        from.setEmpty();
    }

    int movePawn(Field from, Field to) {
        int result = -1;
        if (!from.isEmpty() && (possibleMoves(from).contains(to) || possibleKills(from).contains(to))) {
            // move is valid
            result = 0;
            gameState.updateHistory(from, to);
            // step over every field until we reach the target
            Field step;
            do {
                step = stepTowards(from, to);
                if (!step.isEmpty()) {
                    // we happen to kill
                    result += 1;
                    gameState.updateHistory(step, null);
                }
                moveField(from, step);
                from = step;
            } while (step != to);
        }
        return result;
    }

    /*
    Finds all proper moves for provided field with normal pawn.
    */
    ArrayList<Field> possiblePawnMoves(Field from) {
        ArrayList<Field> result = new ArrayList<Field>();
        Field to;
        int[][] modifiers;

        // pawns move in one direction only
        // these modifiers indicate diagonals along which they can move
        if (from.getColor() == Pawn.BLACK)
            modifiers = new int[][]{{1, 1}, {-1, 1}};
        else if (from.getColor() == Pawn.WHITE)
            modifiers = new int[][]{{1, -1}, {-1, -1}};
        else
            modifiers = new int[][]{};

        for (int[] m : modifiers) {
            to = getField(from.x+m[0], from.y+m[1]);
            if (to != null) {
                // if field 'to' not empty, check whether kill is possible
                 if(to.isEmpty()) result.add(to);
                 else if(areOfOppositeColor(from, to)) {
                     to = stepTowards(from, to, 2);
                     if (to != null && to.isEmpty()) result.add(to);
                 }
            }
        }
        return result;
    }

    /*
    Finds all proper moves for provided field with queen pawn.
     */
    ArrayList<Field> possibleQueenMoves(Field from) {
        ArrayList<Field> r = new ArrayList<Field>();
        Field to;

        // these modifiers indicate diagonals along which we can move
        int[][] modifiers = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] m : modifiers) {
            to = from;
            while (true) {
                to = getField(to.x + m[0], to.y + m[1]);
                if (to != null) {
                    // if field 'to' not empty, check whether kill is possible
                    if (to.isEmpty()) {
                        r.add(to);
                        continue;
                    } else if (to.getColor() != from.getColor()) {
                        // step over b and check whether we land on a valid field
                        to = getField(to.x + m[0], to.y + m[1]);
                        if (to != null && to.isEmpty()) r.add(to);
                    }
                }
                break;
            }
        }
        return r;
    }

    /*
    Finds all proper moves for provided field with pawn.
    Gives proper output for both, queen and normal pawn.
    */
    ArrayList<Field> possibleMoves(Field from) {
        from.promotePawn();
        if (from.isQueen()) return possibleQueenMoves(from);
        else return possiblePawnMoves(from);
    }

    /*
    Finds all proper killing moves for provided field with normal pawn.
    Parameter previous is necessary in order to prevent pawn from jumping
    over pawn, that has been killed in previous step. We won't be checking
    moves in this direction. If there was no previous step, we provide null.
    */
    ArrayList<Field> possiblePawnKills(Field from, Field previous) {
        ArrayList<Field> r = new ArrayList<Field>();
        Field to;

        // modifiers indicate diagonals along which pawns can kill
        int[][] modifiers = new int[][]{{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        // there might be a forbidden modifier (fm) indicating a diagonal we came from
        int[] fm = null;
        if (previous != null) fm = directionTowards(from, previous);

        for (int[] m : modifiers) {
            // check whether move in this direction is forbidden
            if (m == fm) continue;
            to = getField(from.x+m[0], from.y+m[1]);
            if (to != null && !to.isEmpty() && areOfOppositeColor(from, to)) {
                to = stepTowards(from, to, 2);
                    if (to != null && to.isEmpty()) r.add(to);
            }
        }
        return r;
    }

    /*
    Finds all proper killing moves for provided field with queen pawn.
    Parameter previous is necessary in order to prevent pawn from jumping
    over pawn, that has been killed in previous step. We won't be checking
    moves in this direction. If there was no previous step, we provide null.
     */
    ArrayList<Field> possibleQueenKills(Field from, Field previous) {
        ArrayList<Field> result = new ArrayList<Field>();
        Field to;

        // these modifiers indicate diagonals along which we can kill
        int[][] modifiers = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        // there might be a forbidden modifier (fm) indicating a diagonal we came from
        int[] fm = null;
        if (previous != null) fm = directionTowards(from, previous);

        for (int[] m : modifiers) {
            // check whether move in this direction is forbidden
            if (m == fm) continue;
            to = from;
            while (true) {
                to = getField(to.x + m[0], to.y + m[1]);
                if (to != null) {
                    if (to.isEmpty()) continue;
                    else if (to.getColor() != from.getColor()) {
                        // step over b and check whether we land on a valid field
                        to = getField(to.x + m[0], to.y + m[1]);
                        if (to != null && to.isEmpty()) result.add(to);
                    }
                }
                break;
            }
        }
        return result;
    }

    /*
    Finds all proper kill moves for provided field with pawn.
    Gives proper output for both, queen and normal pawn.
    */
    ArrayList<Field> possibleKills(Field from, Field previous) {
        from.promotePawn();
        if (from.isQueen()) return possibleQueenKills(from, previous);
        else return possiblePawnKills(from, previous);
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
                if (f.getColor() == Pawn.BLACK) {
                    newGameState.playerA.add(f);
                } else if (f.getColor() == Pawn.WHITE) {
                    newGameState.playerB.add(f);
                }
            }
        }

        Board newBoard = new Board();
        newBoard.ui = newUI;
        newBoard.size = this.size;
        newBoard.pawnRows = this.pawnRows;
        newBoard.gameState = newGameState;
        newBoard.tab = newTable;

        return newBoard;
    }
}
