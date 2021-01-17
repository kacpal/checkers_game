public class Main {
    public static void main(String[] args) {
        UI ui = new ConsoleUI();
        Board b = new Board(ui);
        HumanPlayer p1 = new HumanPlayer(b, 1);
        ComputerPlayer p2 = new ComputerPlayer(b, 2);
        while (true) {
            b.display();
            p1.nextMove();
            b.display();
            p2.nextMove();
        }
    }
}
