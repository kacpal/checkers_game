public enum Pawn {
    EMPTY(0, '.'),
    BLACK(1, 'x'),
    WHITE(2, 'o'),
    BLACK_QUEEN(5, 'X'),
    WHITE_QUEEN(6, 'O'),
    FILLER(-1, ' ');

    private final int intValue;
    private final char charValue;

    Pawn (final int i, final char c) {
        intValue = i;
        charValue = c;
    }

    public int getIntValue() {
        return intValue;
    }

    public char getCharValue() {
        return charValue;
    }

    Pawn promote() {
        if (this == BLACK) return BLACK_QUEEN;
        else if (this == WHITE) return WHITE_QUEEN;
        else return EMPTY;
    }
}
