public class Main {
    public static void main(String[] args) {
        UI ui = new ConsoleUI();
        Board b = new Board(ui);
        HumanPlayer p1 = new HumanPlayer(b, 1);
        HumanPlayer p2 = new HumanPlayer(b, 2);
        while (true) {
            p1.nextMove();
            p2.nextMove();
        }
    }
}
