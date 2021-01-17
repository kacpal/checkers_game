import java.util.ArrayList;

public class ComputerPlayer implements Player {
    private int color;
    private int depth = 5;
    Board board;

    ComputerPlayer(Board b, int c) {
        board = b;
        color = c;
    }

    public void nextMove() {
        // I don't like this... maybe we can pass a simple move to the board?
        // That way it would be easier to save it in History eventually
        board.setTab(findBestMove(board.clone(), this.color));
    }

    public Board findBestMove(Board b, int side) {
        ArrayList<FieldPair> validMoves = generateValidMoves(b, side);
        int bestScore = -1000;
        Board bestBoard = null;

        boolean isKillingTurn = anyKillPossible(b, side);

        for (FieldPair currentMove : validMoves) {
            // we are actually choosing one of these moves
            Board boardClone = b.clone();

            int move = boardClone.movePawn(boardClone.getField(currentMove.first.x, currentMove.first.y),
                                             boardClone.getField(currentMove.second.x, currentMove.second.y));
            if (move < 0) {
                System.out.println("[debug] wrong move");
                return null;
            } else {
                //For consecutive kill moves
                if (anyKillPossible(boardClone, side) && isKillingTurn) {
                    boardClone.setTab(consecutiveMove(boardClone, side));
                }
                boardClone.setTab(promotePawns(boardClone));

                int currentScore = minimax(boardClone, changeColor(side), depth, false, -1000, 1000);
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestBoard = boardClone;
                }
            }
        }
        return bestBoard;
    }

    int minimax(Board b, int side, int depth, Boolean isMax, int alpha, int beta) {
        if (depth < 1) {
            return evaluate(b);
        }

        int bestScore;

        boolean isKillingTurn = anyKillPossible(b, side);

        if (isMax) {
            bestScore = -1000;

            ArrayList<FieldPair> validMoves;

            if(isKillingTurn) {
                validMoves = generateValidKills(b, side);
            } else {
                validMoves = generateValidMoves(b, side);
            }

            for (FieldPair currentMove : validMoves) {
                Board boardClone = b.clone();

                int move = boardClone.movePawn(boardClone.getField(currentMove.first.x, currentMove.first.y),
                        boardClone.getField(currentMove.second.x, currentMove.second.y));
                if (move < 0) {
                    System.out.println("[debug] wrong move");
                    return 0;
                } else {
                    //For consecutive kill moves
                    if (anyKillPossible(boardClone, side) && isKillingTurn) {
                        boardClone.setTab(consecutiveMove(boardClone, side));
                    }
                    boardClone.setTab(promotePawns(boardClone));

                    int currentScore = minimax(boardClone, changeColor(side), depth - 1, !isMax, alpha, beta);

                    bestScore = Math.max(bestScore, currentScore);
                    alpha = Math.max(alpha, bestScore);
                    if (alpha >= beta)
                        break;
                }
            }
        } else {
            bestScore = 1000;

            ArrayList<FieldPair> validMoves;

            if(isKillingTurn) {
                validMoves = generateValidKills(b, side);
            } else {
                validMoves = generateValidMoves(b, side);
            }

            for (FieldPair currentMove : validMoves) {
                Board boardClone = b.clone();

                int move = boardClone.movePawn(boardClone.getField(currentMove.first.x, currentMove.first.y),
                        boardClone.getField(currentMove.second.x, currentMove.second.y));
                if (move < 0) {
                    System.out.println("[debug] wrong move");
                    return 0;
                } else {
                    //For consecutive kill moves
                    if (anyKillPossible(boardClone, side) && isKillingTurn) {
                        boardClone.setTab(consecutiveMove(boardClone, side));
                    }
                    boardClone.setTab(promotePawns(boardClone));

                    int currentScore = minimax(boardClone, changeColor(side), depth - 1, !isMax, alpha, beta);

                    bestScore = Math.min(bestScore, currentScore);
                    alpha = Math.min(alpha, bestScore);
                    if (alpha >= beta)
                        break;
                }
            }
        }
        return bestScore;
    }

    Board promotePawns(Board b) {
        for (int i=0; i < b.size; i++) {
            if (b.getField(i, 0).getContent() == 2) {
                b.getField(i, 0).content = 6;
            }
        }
        for (int i=0; i < b.size; i++) {
            if (b.getField(i, b.size-1).getContent() == 1) {
                b.getField(i, b.size - 1).content = 5;
            }
        }
        return b;
    }

    Board consecutiveMove(Board b, int side) {
        ArrayList<FieldPair> validKills = generateValidKills(b, side);
        int bestScore = -1000;
        Board bestBoard = null;

        for (FieldPair currentKill : validKills) {
            Board boardClone = b.clone();

            int move = boardClone.movePawn(boardClone.getField(currentKill.first.x, currentKill.first.y),
                                             boardClone.getField(currentKill.second.x, currentKill.second.y));
            if (move < 0) {
                System.out.println("[debug] wrong kill");
                return null;
            } else {
                boardClone.setTab(promotePawns(boardClone));
                int currentScore = minimaxConsecutive(boardClone, side);
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestBoard = boardClone;
                }
            }
        }
        return bestBoard;
    }

    int minimaxConsecutive(Board b, int side) {
        if (!anyKillPossible(b, side)) {
            return evaluate(b);
        }

        int bestScore = -1000;

        ArrayList<FieldPair> validKills = generateValidKills(b, side);
        for (FieldPair currentKill : validKills) {
            Board boardClone = b.clone();

            int move = boardClone.movePawn(boardClone.getField(currentKill.first.x, currentKill.first.y),
                    boardClone.getField(currentKill.second.x, currentKill.second.y));
            if (move < 0) {
                System.out.println("[debug] wrong minimaxConsecutive move");
                return 0;
            } else {
                boardClone.setTab(promotePawns(boardClone));
                int currentScore = minimaxConsecutive(boardClone, side);
                if (currentScore > bestScore)
                    bestScore = currentScore;
            }
        }
        return bestScore;
    }

    private boolean anyKillPossible(Board board, int color) {
        for (Field p : board.getGameState().myPawns(color)) {
            if (!board.possibleKills(p).isEmpty())
                return true;
        }
        return false;
    }

    private boolean anyMovePossible(Board board, int color) {
        for (Field p : board.getGameState().myPawns(color)) {
            if (!board.possibleMoves(p).isEmpty())
                return true;
        }
        return false;
    }

    int evaluate(Board b) {
        int result = 0;
        int black = 0, white = 0, black_queen = 0, white_queen = 0;
        int white_weight = 4, white_queen_weight = 10, black_weight = 6, black_queen_weight = 10;

        for (int i = 0; i < b.size; i++) {
            for (int j = 0; j < b.size; j++) {
                Field field = b.getField(j, i);

                switch (field.getColor()) {
                    case 1: white++; break;
                    case 2: black++; break;
                    case 5: white_queen++; break;
                    case 6: black_queen++; break;
                    default: break;
                }
            }
        }

        if (color == 1)
            result = white*white_weight + white_queen*white_queen_weight - black*black_weight - black_queen*black_queen_weight;
        else
            result = black*black_weight + black_queen*black_queen_weight - white*white_weight - white_queen*white_queen_weight;

        return result;
    }

    ArrayList<FieldPair> generateValidKills(Board b, int color) {
        ArrayList<FieldPair> array = new ArrayList<FieldPair>();

        if (anyKillPossible(b, color)) {
            for (int x = 0; x < b.size; x++) {
                for (int y = 0; y < b.size; y++) {
                    Field from = b.tab[x][y];
                    if (from.getColor() == color && !b.possibleKills(from).isEmpty()) {
                        for (int i = 0; i < b.possibleKills(from).size(); i++) {
                            FieldPair fieldPair = new FieldPair();
                            fieldPair.first = from.clone();
                            fieldPair.second = b.possibleKills(from).get(i);
                            array.add(fieldPair);
                        }
                    }
                }
            }
            return array;
        } else
            return null;
    }

    ArrayList<FieldPair> generateValidMoves(Board b, int color) {
        ArrayList<FieldPair> array = new ArrayList<FieldPair>();

        // check if any of our pawns can kill
        if (anyKillPossible(b, color)) {
            for (int x = 0; x < b.size; x++) {
                for (int y = 0; y < b.size; y++) {
                    Field from = b.tab[x][y];
                    if (from.getColor() == color && !b.possibleKills(from).isEmpty()) {
                        for (int i = 0; i < b.possibleKills(from).size(); i++) {
                            FieldPair fieldPair = new FieldPair();
                            fieldPair.first = from.clone();
                            fieldPair.second = b.possibleKills(from).get(i);
                            array.add(fieldPair);
                        }
                    }
                }
            }
            return array;
        } else if (anyMovePossible(b, color)) {
            // if no kills are possible, check all other possible moves
            for (int x = 0; x < b.size; x++) {
                for (int y = 0; y < b.size; y++) {
                    Field from = b.tab[x][y];
                    if (from.getColor() == color && !b.possibleMoves(from).isEmpty()) {
                        for (int i = 0; i < b.possibleMoves(from).size(); i++) {
                            FieldPair fieldPair = new FieldPair();
                            fieldPair.first = from.clone();
                            fieldPair.second = b.possibleMoves(from).get(i);
                            array.add(fieldPair);
                        }
                    }
                }
            }
            return array;
        } else
            return null;
    }

    int changeColor(int col) {
        if(col == 1)
            return 2;
        else return 1;
    }
}
