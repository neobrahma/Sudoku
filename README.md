# Sudoku

In 2020, I was playing a lot to Puzzle Page to resolve Sudoku grids.

At the same period, I discovered the new layout for animation, the MotionLayout.

So I create this app to try and play with the motionLayout.

And with my imagination, I try a nice UX to avoid to move your thumb everywhere on the screen to select a square.

So I create a "touchpad". It's a square which contains 9 squares, with this order :


|1 | 2 | 3|
|---|---|---|
|4 | 5 | 6|
|7 | 8 | 9|

And the touchpad has 3 levels.

First level : Sudoku grid is composed by 9 big squares, 
Second level : a big square is composed by 9 square
Third level : a square is composed by 9 values

| First Level                                      | Second Level|  Third Level| 
|---------------------------------------------------|--------------------------------------------------|---------|
| ![sudoku_1](https://user-images.githubusercontent.com/96651172/225755714-de0a4c85-6443-4674-89cf-2607b160da8c.png) | ![sudoku_2](https://user-images.githubusercontent.com/96651172/225755974-6e6d4ab0-a142-4460-9e2e-c4453557cb45.png) | ![sudoku_3](https://user-images.githubusercontent.com/96651172/225756013-152e3e9a-5813-45c1-92d1-17c0bb725db7.png) |
| it's the default state, when you start the application | when you click on the fifth box, it will select fifth big square on the grid, and display the same informatinons | when you click on the first box, it will select the first box in the big square, and you can select the value to write |


After you selected a value on the third level, you can click on the background to go back to the second level, and click again on the background to go back on the first level.

And you will see a different animation depending on the box you click.
