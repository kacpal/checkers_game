import java.util.ArrayList;

public class ComputerPlayer implements Player {
    private Pawn color;
    private int depth = 5;
    Board board;
    UI ui;

    ComputerPlayer(Board board, Pawn color, UI ui) {
        this.board = board;
        this.color = color;
        this.ui = ui;
    }

    private boolean anyKillPossible(Board board, Pawn color) {
        for (Field p : board.getGameState().myPawns(color)) {
            if (!board.possibleKills(p).isEmpty())
                return true;
        }
        return false;
    }

    ArrayList<FieldPair> generateValidKills(Board board, Pawn color) {
        ArrayList<FieldPair> result = new ArrayList<FieldPair>();
        for (Field from : board.getGameState().myPawns(color)) {
            for (Field to : board.possibleKills(from))
                result.add(new FieldPair(from, to));
        }
        return result;
    }

    ArrayList<FieldPair> generateValidMoves(Board board, Pawn color) {
        ArrayList<FieldPair> result = generateValidKills(board, color);

        // since killing is always obligatory, there's no need to search for non-killing
        // moves if we already have killing moves in our result array
        if (!result.isEmpty()) return result;
        for (Field from : board.getGameState().myPawns(color)) {
            for (Field to : board.possibleMoves(from))
                result.add(new FieldPair(from, to));
        }
        return result;
    }

    /*
    Evaluates the board score, basing on number of pawns.
    The weights are optimized to make algorithm gameplay less "kamikaze".
     */
    int evaluate(Board board) {
        int result = 0;
        int black = 0, white = 0, black_queen = 0, white_queen = 0;
        int white_weight = 4, white_queen_weight = 10, black_weight = 6, black_queen_weight = 10;

        for (int i = 0; i < board.size; i++) {
            for (int j = 0; j < board.size; j++) {
                Field field = board.getField(j, i);

                switch (field.getColor().getIntValue()) {
                    case 1: white++; break;
                    case 2: black++; break;
                    case 5: white_queen++; break;
                    case 6: black_queen++; break;
                    default: break;
                }
            }
        }

        if (color == Pawn.BLACK)
            result = white*white_weight + white_queen*white_queen_weight - black*black_weight - black_queen*black_queen_weight;
        else
            result = black*black_weight + black_queen*black_queen_weight - white*white_weight - white_queen*white_queen_weight;

        return result;
    }

    Pawn changeColor(Pawn color) {
        if (color == Pawn.BLACK)
            return Pawn.WHITE;
        else return Pawn.BLACK;
    }

    /*
    Equivalent class of minimax, but for multiple jumps (kills) in one move.
    It differs because for every consecutive move, side is not being changed.
     */
    int minimaxConsecutive(Board board, Field from) {
        from = board.getField(from.x, from.y);
        if (board.possibleKills(from).isEmpty()) {
            return evaluate(board);
        }

        int bestScore = -1000;

        for (Field kill : board.possibleKills(from)) {
            FieldPair currentKill = new FieldPair(from, kill);
            Board boardClone = board.clone();

            int move = boardClone.movePawn(boardClone.getField(currentKill.first.x, currentKill.first.y),
                                           boardClone.getField(currentKill.second.x, currentKill.second.y));

            if (move < 0) {
                System.err.println("minimaxConsecutive: invalid move, wrong kill");
                System.err.println(currentKill);
            } else {
                int currentScore = minimaxConsecutive(boardClone, kill);
                if (currentScore > bestScore)
                    bestScore = currentScore;
            }
        }
        return bestScore;
    }

    /*
    Equivalent class of findBestMove, but for multiple jumps (kills) in one move.
     */
    FieldPair findBestConsecutiveMove(Board board, Field from) {
        int bestScore = -1000;
        FieldPair bestMove = null;
        from = board.getField(from.x, from.y);

        for (Field kill : board.possibleKills(from)) {
            FieldPair currentKill = new FieldPair(from, kill);
            Board boardClone = board.clone();

            int move = boardClone.movePawn(boardClone.getField(currentKill.first.x, currentKill.first.y),
                                           boardClone.getField(currentKill.second.x, currentKill.second.y));

            if (move <= 0) {
                System.err.println("findBestConsecutiveMove: invalid move, wrong kill");
                System.err.println(currentKill);
            } else {
                int currentScore = minimaxConsecutive(boardClone, kill);
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestMove = currentKill;
                }
            }
        }
        return bestMove;
    }

    int minimax(Board board, Pawn side, int depth, boolean isMax, int alpha, int beta) {
        if (depth < 1) {
            return evaluate(board);
        }

        int bestScore = (isMax) ? -1000 : 1000;

        boolean isKillingTurn = anyKillPossible(board, side);

        // iterate through every pair with correct "from, to" move
        for (FieldPair currentMove : generateValidMoves(board, side)) {

            // create temporary board to test our moves
            Board boardClone = board.clone();

            // create a new move sequence in case this is a killing move
            int move = boardClone.movePawn(boardClone.getField(currentMove.first.x, currentMove.first.y),
                    boardClone.getField(currentMove.second.x, currentMove.second.y));

            FieldPair kill = new FieldPair(null, boardClone.getField(currentMove.second.x, currentMove.second.y));

            // if this is killing move, continue until there are no more possible consecutive (killing) moves
            while (isKillingTurn && !boardClone.possibleKills(kill.second).isEmpty()) {
                kill = findBestConsecutiveMove(boardClone, kill.second);
                if (kill == null) {
                    System.err.println("Something went terribly wrong with consecutiveMove of ComputerPlayer's minmax.");
                    break;
                }
                boardClone.movePawn(kill.first, kill.second);
            }

            int currentScore = minimax(boardClone, changeColor(side), depth - 1, !isMax, alpha, beta);

            // alpha-beta pruning
            bestScore = (isMax) ? Math.max(bestScore, currentScore) : Math.min(bestScore, currentScore);
            alpha = (isMax) ? Math.max(alpha, bestScore) : Math.min(alpha, bestScore);
            if (alpha >= beta) break;
        }
        return bestScore;
    }

    /*
    Returns list of best found possible move sequence, based on minimax algorithm.
     */
    public ArrayList<FieldPair> findBestMove(Board board, Pawn side) {
        ArrayList<FieldPair> bestMoveSequence = null;
        int bestScore = -1000;

        boolean isKillingTurn = anyKillPossible(board, side);

        // iterate through every pair with correct "from, to" move
        for (FieldPair currentMove : generateValidMoves(board, side)) {

            // create temporary board to test our moves
            Board boardClone = board.clone();

            // create a new move sequence in case this is a killing move
            ArrayList<FieldPair> currentMoveSequence = new ArrayList<FieldPair>();

            int move = boardClone.movePawn(boardClone.getField(currentMove.first.x, currentMove.first.y),
                                           boardClone.getField(currentMove.second.x, currentMove.second.y));
            currentMoveSequence.add(currentMove);

            FieldPair kill = new FieldPair(null, boardClone.getField(currentMove.second.x, currentMove.second.y));

            // if this is killing move, continue until there are no more possible consecutive (killing) moves
            while (isKillingTurn && !boardClone.possibleKills(kill.second).isEmpty()) {
                kill = findBestConsecutiveMove(boardClone, kill.second);
                if (kill == null) {
                    System.err.println("Something went terribly wrong with consecutiveMove of ComputerPlayer.");
                    break;
                }
                boardClone.movePawn(kill.first, kill.second);
                currentMoveSequence.add(new FieldPair(board.getField(kill.first.x, kill.first.y),
                                                      board.getField(kill.second.x, kill.second.y)));
            }

            int currentScore = minimax(boardClone, changeColor(side), depth, false, -1000, 1000);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMoveSequence = currentMoveSequence;
            }
        }
        return bestMoveSequence;
    }

    @Override
    public void nextMove() {
        ArrayList<FieldPair> moves = findBestMove(board, color);
        if (moves == null) board.getGameState().surrender(color);
        else for (FieldPair m : moves) {
            ui.displayString(m + "\n");
            board.movePawn(m.first, m.second);
        }
    }
}
