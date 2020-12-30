import java.util.Scanner;

public class HumanPlayer implements Player {
    private Scanner scanner = new Scanner(System.in);
    private int color;
    Board board;

    HumanPlayer(Board b, int c) {
        board = b;
        color = c;
    }

    static Field inputToCoordinates(String s) {
        int y = 0+s.charAt(0)-'a';
        int x = Integer.parseInt(String.valueOf(s.charAt(1)));
        Field p = new Field(x, y);
        return p;
    }

    Field inputToField(String s) {
        Field p = inputToCoordinates(s);
        return board.getField(p.x, p.y);
    }

    void consecutiveMove(Field f) {
        Field t;
        board.display();
        while (true) {
            System.out.print("to> ");
            t = inputToField(scanner.nextLine());
            if (t == f) break;
            if (!board.possibleKills(f).contains(t)) {
                System.out.println("Invalid move. You have to kill!");
            }
            int move = board.movePawn(f, t);
            if (move < 0)
                System.out.println("Invalid move. Try again...");
            else if (move > 0 && !board.possibleKills(t, f).isEmpty())
                f = t;
            else
                break;
        }
    }

    private boolean anyKillPossible() {
        for (int x = 0; x < board.size; x++) {
            for (int y = 0; y < board.size; y++) {
                Field p = board.getField(x, y);
                if (p.getColor() == color && !board.possibleKills(p).isEmpty())
                    return true;
            }
        }
        return false;
    }

    @Override
    public void nextMove() {
        Field f, t;
        board.display();
        if (anyKillPossible()) {
            System.out.println("Killing this turn is possible.");
        }
        while (true) {
            System.out.print("from> ");
            f = inputToField(scanner.nextLine());
            if (f.getColor() != color) {
                System.out.println("This is not your pawn.");
                continue;
            }
            System.out.print("to> ");
            t = inputToField(scanner.nextLine());
            int move = board.movePawn(f, t);
            if (move < 0)
                System.out.println("Invalid move. Try again...");
            else if (move > 0 && !board.possibleKills(t, f).isEmpty()) {
                consecutiveMove(t);
                break;
            } else
                break;
        }
    }
}
