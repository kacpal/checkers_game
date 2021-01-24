import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private Scanner scanner = new Scanner(System.in);
    private int color;
    Board board;
    UI ui;

    HumanPlayer(Board b, int c, UI ui) {
        board = b;
        color = c;
        this.ui = ui;
    }

    class Surrender extends Exception {}

    Field parseInput(String prompt) throws Surrender {
        if (prompt != null) ui.displayString(prompt);
        String line = scanner.nextLine().toUpperCase().strip();
        if (line.equals("SURRENDER")) throw new Surrender();
        int y = 0, x = 0;
        char c;
        for (int i = 0; i < line.length(); i++) {
            c = line.charAt(i);
            if (Character.isLetter(c)) {
                y = c - 'A';
            } else if (Character.isDigit(c)) {
                x = 10 * x + Integer.parseInt(String.valueOf(c));
            }
        }
        Field f = board.getField(x, y);
        if (f == null) throw new InputMismatchException("invalid coordinates");
        return f;
    }

    void handleSurrender() {
        board.getGameState().surrender(color);
    }

    private boolean anyKillPossible() {
        for (Field p : board.getGameState().myPawns(color)) {
            if (!board.possibleKills(p).isEmpty())
                return true;
        }
        return false;
    }

    void consecutiveMove(Field f) {
        // we are making a consecutive move, that means we killed in the previous move
        // and we will do consecutive kills for as long as we can
        Field t;
        // the board changed, so we have to display it once again
        board.display();
        while (true) {
            try {
                t = parseInput("to> ");
                if (t == f) throw new InputMismatchException("target destination is a place you are moving from");
                if (!board.possibleKills(f).contains(t)) throw new InputMismatchException("you must kill");
            } catch (Surrender e) {
                handleSurrender();
                return;
            } catch (InputMismatchException e) {
                ui.displayString("Your move is invalid: " + e.getMessage() + ".\n");
                continue;
            }

            int move = board.movePawn(f, t);
            if (move < 0){
                // the move was invalid
                ui.displayString("Invalid move. Try again...\n");
                // retry
                continue;
            } else if (move > 0 && !board.possibleKills(t, f).isEmpty()) {
                // the move was a kill and more kills are possible
                f = t;
                continue;
            } else {
                // the move was valid and there are no more kills possible
                // end round
                break;
            }
        }
    }

    @Override
    public void nextMove() {
        Field f, t;
        if (anyKillPossible()) ui.displayString("Killing this turn is possible.\n");
        while (true) {
            try {
                f = parseInput("from> ");
                if (f.getColor() != color) throw new InputMismatchException("not your pawn");
                t = parseInput("to> ");
                // enforce killing each turn if possible
                if (anyKillPossible() && !board.possibleKills(f).contains(t)) throw new InputMismatchException("you must kill");
            } catch (Surrender e) {
                handleSurrender();
                return;
            } catch (InputMismatchException e) {
                ui.displayString("Your move is invalid: " + e.getMessage() + ".\n");
                continue;
            }

            // make a proper move
            int move = board.movePawn(f, t);
            if (move < 0){
                // the move was invalid
                ui.displayString("Invalid move. Try again...\n");
                // retry
                continue;
            } else if (move > 0 && !board.possibleKills(t, f).isEmpty()) {
                // the move was a kill and more kills are possible
                consecutiveMove(t);
                // end round
                break;
            } else {
                // the move was valid, end round
                break;
            }
        }
    }
}
