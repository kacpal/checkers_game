# Checkers game
Checkers game written in Java with implementation of minimax based artifficial intelligence. It uses standard international/polish variation of the game, although there are many configurable settings. 

## Options
### Gameplay
Default setting: player vs computer game, with computer doing the first move.\
``--first`` Player makes the first move, instead of computer.\
``--ai-only`` Watch the game of computer vs computer.\
``--players-only`` Play player vs player game. If selected, above options will be ignored.
### Difficulty
Default setting: medium (search depth 4)\
``--hard`` Sets the algorithm search depth to 5 (may impact the performance).\
``--easy`` Sets the algorithm search depth to 3.
### Board
Default setting: 10x10 board with 4 rows of pawns.\
``--board-8`` 8x8 board with 3 rows of pawns.\
``--board-12`` 12x12 board with 5 rows of pawns (may impact the performance).
### Other
``--spaces`` Use spaces instead of tabulators for drawing the board in console. It's useful when tabulators are too wide. 