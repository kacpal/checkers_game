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
        board.set(findBestMove(board.clone(), this.color));
    }

    public Board findBestMove(Board b, int side) {
        ArrayList<Pair> validMoves = generateValidMoves(b, side);
        int bestScore = -1000;
        Board bestBoard = null;

        boolean isKillingTurn = anyKillPossible(b, side);

        for (Pair currentMove : validMoves) {
            Board boardClone = b.clone();

            int move = boardClone.movePawnAI(boardClone.getField(currentMove.first.x, currentMove.first.y),
                    boardClone.getField(currentMove.second.x, currentMove.second.y));
            if (move < 0) {
                System.out.println("[debug] wrong move");
                return null;
            } else {
                //For consecutive kill moves
                if (anyKillPossible(boardClone, side) && isKillingTurn) {
                    boardClone.set(consecutiveMove(boardClone, side));
                }
                boardClone.set(promotePawns(boardClone));

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

            ArrayList<Pair> validMoves;

            if(isKillingTurn) {
                validMoves = generateValidKills(b, side);
            } else {
                validMoves = generateValidMoves(b, side);
            }

            for (Pair currentMove : validMoves) {
                Board boardClone = b.clone();

                int move = boardClone.movePawnAI(boardClone.getField(currentMove.first.x, currentMove.first.y),
                        boardClone.getField(currentMove.second.x, currentMove.second.y));
                if (move < 0) {
                    System.out.println("[debug] wrong move");
                    return 0;
                } else {
                    //For consecutive kill moves
                    if (anyKillPossible(boardClone, side) && isKillingTurn) {
                        boardClone.set(consecutiveMove(boardClone, side));
                    }
                    boardClone.set(promotePawns(boardClone));

                    int currentScore = minimax(boardClone, changeColor(side), depth - 1, !isMax, alpha, beta);

                    bestScore = Math.max(bestScore, currentScore);
                    alpha = Math.max(alpha, bestScore);
                    if (alpha >= beta)
                        break;
                }
            }
        } else {
            bestScore = 1000;

            ArrayList<Pair> validMoves;

            if(isKillingTurn) {
                validMoves = generateValidKills(b, side);
            } else {
                validMoves = generateValidMoves(b, side);
            }

            for (Pair currentMove : validMoves) {
                Board boardClone = b.clone();

                int move = boardClone.movePawnAI(boardClone.getField(currentMove.first.x, currentMove.first.y),
                        boardClone.getField(currentMove.second.x, currentMove.second.y));
                if (move < 0) {
                    System.out.println("[debug] wrong move");
                    return 0;
                } else {
                    //For consecutive kill moves
                    if (anyKillPossible(boardClone, side) && isKillingTurn) {
                        boardClone.set(consecutiveMove(boardClone, side));
                    }
                    boardClone.set(promotePawns(boardClone));

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
        ArrayList<Pair> validKills = generateValidKills(b, side);
        int bestScore = -1000;
        Board bestBoard = null;

        for (Pair currentKill : validKills) {
            Board boardClone = b.clone();

            int move = boardClone.movePawnAI(boardClone.getField(currentKill.first.x, currentKill.first.y),
                    boardClone.getField(currentKill.second.x, currentKill.second.y));
            if (move < 0) {
                System.out.println("[debug] wrong kill");
                return null;
            } else {
                boardClone.set(promotePawns(boardClone));
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

        ArrayList<Pair> validKills = generateValidKills(b, side);
        for (Pair currentKill : validKills) {
            Board boardClone = b.clone();

            int move = boardClone.movePawnAI(boardClone.getField(currentKill.first.x, currentKill.first.y),
                    boardClone.getField(currentKill.second.x, currentKill.second.y));
            if (move < 0) {
                System.out.println("[debug] wrong minimaxConsecutive move");
                return 0;
            } else {
                boardClone.set(promotePawns(boardClone));
                int currentScore = minimaxConsecutive(boardClone, side);
                if (currentScore > bestScore)
                    bestScore = currentScore;
            }
        }
        return bestScore;
    }

    private boolean anyKillPossible(Board board, int color) {
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                Field p = board.getField(x, y);
                if (p.getColor() == color && !board.possibleKills(p).isEmpty())
                    return true;
            }
        }
        return false;
    }

    private boolean anyMovePossible(Board board, int color) {
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                Field p = board.getField(x, y);
                if (p.getColor() == color && !board.possibleMoves(p).isEmpty())
                    return true;
            }
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

    ArrayList<Pair> generateValidKills(Board b, int color) {
        ArrayList<Pair> array = new ArrayList<Pair>();

        if (anyKillPossible(b, color)) {
            for (int x = 0; x < b.size; x++) {
                for (int y = 0; y < b.size; y++) {
                    Field from = b.tab[x][y];
                    if (from.getColor() == color && !b.possibleKills(from).isEmpty()) {
                        for (int i = 0; i < b.possibleKills(from).size(); i++) {
                            Pair pair = new Pair();
                            pair.first = from.clone();
                            pair.second = b.possibleKills(from).get(i);
                            array.add(pair);
                        }
                    }
                }
            }
            return array;
        } else
            return null;
    }

    ArrayList<Pair> generateValidMoves(Board b, int color) {
        ArrayList<Pair> array = new ArrayList<Pair>();

        if (anyKillPossible(b, color)) {
            for (int x = 0; x < b.size; x++) {
                for (int y = 0; y < b.size; y++) {
                    Field from = b.tab[x][y];
                    if (from.getColor() == color && !b.possibleKills(from).isEmpty()) {
                        for (int i = 0; i < b.possibleKills(from).size(); i++) {
                            Pair pair = new Pair();
                            pair.first = from.clone();
                            pair.second = b.possibleKills(from).get(i);
                            array.add(pair);
                        }
                    }
                }
            }
            return array;
        } else if (anyMovePossible(b, color)) {
            for (int x = 0; x < b.size; x++) {
                for (int y = 0; y < b.size; y++) {
                    Field from = b.tab[x][y];
                    if (from.getColor() == color && !b.possibleMoves(from).isEmpty()) {
                        for (int i = 0; i < b.possibleMoves(from).size(); i++) {
                            Pair pair = new Pair();
                            pair.first = from.clone();
                            pair.second = b.possibleMoves(from).get(i);
                            array.add(pair);
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

class Pair {
    Field first;
    Field second;

    @Override
    public String toString() {
        return (first.x+"-"+first.y+" to "+second.x+"-"+second.y);
    }
}