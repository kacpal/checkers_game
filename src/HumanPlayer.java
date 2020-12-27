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
        // TODO: only allow consecutive moves if the kill is possible
        Field t;
        board.display();
        while (true) {
            System.out.print("to> ");
            t = inputToField(scanner.nextLine());
            if (t == f) break;
            int move = board.movePawn(f, t);
            if (move < 0)
                System.out.println("Invalid move. Try again...");
            else if (move > 0)
                f = t;
            else
                break;
        }
    }

    @Override
    public void nextMove() {
        Field f, t;
        board.display();
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
            else if (move > 0) {
                consecutiveMove(t);
                break;
            } else
                break;
        }
    }
}
