public class ConsoleUI implements UI {
    private String interspace = "\t";

    ConsoleUI() {}

    ConsoleUI(String interspace) {
        this.interspace = interspace;
    }

    @Override
    public void displayBoard(Board board) {
        System.out.print("-" + interspace);
        var tab = board.tab;
        for (int i = 0; i < tab[0].length; i++)
            System.out.print(i + interspace);
        System.out.println();

        for (int y = 0; y < tab.length; y++) {

            // display letters for 'y' coordinate
            System.out.print((char)(y+'a') + interspace);
            for (int x = 0; x < tab[y].length; x++) {
                char c = tab[y][x].content.getCharValue();
                System.out.print(c + interspace);
            }
            System.out.println();
        }
    }

    @Override
    public void displayResults(int result) {
        System.out.println("Game finished. The result is: ");
        if (result == 3) System.out.println("DRAW");
        if (result == 2) System.out.println("PLAYER 2 WINS");
        if (result == 1) System.out.println("PLAYER 1 WINS");
    }

    @Override
    public void displayString(String s) {
        System.out.print(s);
    }

    @Override
    public void displayHello() {
        System.out.println("You started a new game of checkers.");
        System.out.println("Player denoted by 'x' goes first.");
        System.out.println("GLHF!\n");
    }
}
