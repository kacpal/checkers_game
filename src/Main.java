public class Main {
    public static void main(String[] args) {
        boolean playerGoesFirst = (args.length > 0 && args[0].equals("--first"));
        boolean ai = (args.length > 0 && args[0].equals("--aiOnly"));

        UI ui = new ConsoleUI();
        Board board = new Board(ui);

        Player a, b;

        if (playerGoesFirst) {
            a = new HumanPlayer(board, Pawn.BLACK, ui);
            b = new ComputerPlayer(board, Pawn.WHITE, ui);
        } else if (ai) {
            a = new ComputerPlayer(board, Pawn.BLACK, ui);
            b = new ComputerPlayer(board, Pawn.WHITE, ui);
        } else {
            a = new ComputerPlayer(board, Pawn.BLACK, ui);
            b = new HumanPlayer(board, Pawn.WHITE, ui);
        }

        ui.displayHello();

        // main game loop
        while (board.gameState.gameover() == 0) {
                board.display();
                a.nextMove();
                board.display();
                b.nextMove();
        }

        board.display();
        ui.displayResults(board.gameState.gameover());

        return;
    }
}
