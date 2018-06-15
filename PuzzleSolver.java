/*
Markus Realica

The program is designed to solve a NxN-puzzle(if N > 2) using an A* Search
with a manhattan distance heuristic. NxN-puzzles are interpreted in a
single one-dimensional array of integers.



Manhattan Distance Heuristic: The main heuristic used in this program is the
Manhattan Distance heuristic which totals up every tiles distance from the goal state. This heurstic is much better than calculating each misplaced tile because it estimates an accurate value from a goal state, unlike the misplaced tiles which could lead you to a goal state, but spends more time mixing the tiles.
The Manhattan Distance comes from a problem in Manhattan,New York where taxi drivers had to find the best path to arrive at a certain coordinate. This is perfect for the 15-puzzle because we could assume each tile has a coordinate x and y which can be found by taking the position in the array and dividing by the number of rows to get the x or taking the position and moduling by the number of columns to get the y.

*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
State Object that contains the puzzle information, current paths taken, level,
and pointers to other State Objects.
Constructor takes in an array and copies the information within the object's
array.
*/
class State{
  int p[];
  int rows;
  int columns;
  int p_size;
  int empty_position;
  int Level = 0;
  String Commands = "";

  State UpMove;
  State DownMove;
  State LeftMove;
  State RightMove;
  State BackPointer;

  public State(int[] list, int x, int y)
  {
    p_size = x * y;
    rows = x;
    columns = y;
    this.p = new int[p_size];
    for(int i = 0; i < p_size; i++){
      this.p[i] = list[i];
      if(this.p[i] == 0)
        this.empty_position = i;
    }
  }
}


/*
The Operation class contains functions used to modify or retrieve
information from the State Object. All functions within this class
need an argument State.
*/
class Operations{

  /*
  The function takes a given State and outputs the puzzle(in 4x4 format),
  Soultion Path and Length of Solution.
  */
  public void Display_Puzzle(State t)
  {
    for(int i = 0;i < t.p_size;i++)
    {
      System.out.print(String.format("%-2s %s", t.p[i], " "));
      if((i+1) % t.rows == 0)
        System.out.println("\n");
    }
    if(t.Commands != "")
      System.out.println("Path taken: "+ t.Commands + "\n");

    System.out.println("Length of Solution: " + t.Level + "\n");
  }


  /*
  The function takes a given State and returns a heurstic value.
  The heurstic used in this function is the Manhattan Distance approach,
  where the distance each tile is from its correct location is added together
  to equal the heurstic value.
  */
  public int Get_Heurstic(State t){

    int x, y;
    int cost = 0;

    for(int i = 0;i < t.p_size;i++){
      x = (t.p[i]-1)/t.rows;
      y = (t.p[i]-1)%t.columns;

      if(x != i/t.rows && t.p[i] != 0){
        cost = cost + Math.abs(x - (i/t.rows));
      }
      if(y != i%t.columns && t.p[i] != 0){
        cost = cost + Math.abs(y - (i%t.columns));
      }

    }
    return cost*10;
  }

  /*
  The function takes a given State and inspects each child for a valid move.

  If a move is valid the child is created and passed the parents information +
  the modified puzzle.

  Else the child stays null and no information is passed on.
  */
  public void Check_Kids(State t, int x, int y){
    if(IsLeftValid(t))
    {
      t.LeftMove = new State(t.p,t.rows,t.columns);
      t.LeftMove.Commands = t.Commands + "L";
      t.LeftMove.Level = t.Level + 1;
      t.LeftMove.BackPointer = t;

      int temp = t.LeftMove.p[t.LeftMove.empty_position];
      t.LeftMove.p[t.LeftMove.empty_position] = t.LeftMove.p[t.LeftMove.empty_position - 1];
      t.LeftMove.p[t.LeftMove.empty_position - 1] = temp;
      t.LeftMove.empty_position = t.LeftMove.empty_position - 1;
    }

    if(IsRightValid(t))
    {
      t.RightMove = new State(t.p,t.rows,t.columns);
      t.RightMove.Commands = t.Commands + "R";
      t.RightMove.Level = t.Level + 1;
      t.RightMove.BackPointer = t;

      int temp = t.RightMove.p[t.RightMove.empty_position];
      t.RightMove.p[t.RightMove.empty_position] = t.RightMove.p[t.RightMove.empty_position + 1];
      t.RightMove.p[t.RightMove.empty_position + 1] = temp;
      t.RightMove.empty_position = t.RightMove.empty_position + 1;
    }

    if(IsUpValid(t))
    {
      t.UpMove = new State(t.p,t.rows,t.columns);
      t.UpMove.Commands = t.Commands + "U";
      t.UpMove.Level = t.Level + 1;
      t.UpMove.BackPointer = t;

      int temp = t.UpMove.p[t.UpMove.empty_position];
      t.UpMove.p[t.UpMove.empty_position] = t.UpMove.p[t.UpMove.empty_position - t.rows];
      t.UpMove.p[t.UpMove.empty_position - t.rows] = temp;
      t.UpMove.empty_position = t.UpMove.empty_position - t.rows;
    }

    if(IsDownValid(t))
    {
      t.DownMove = new State(t.p,t.rows,t.columns);
      t.DownMove.Commands = t.Commands + "D";
      t.DownMove.Level = t.Level + 1;
      t.DownMove.BackPointer = t;

      int temp = t.DownMove.p[t.DownMove.empty_position];
      t.DownMove.p[t.DownMove.empty_position] = t.DownMove.p[t.DownMove.empty_position + t.rows];
      t.DownMove.p[t.DownMove.empty_position + t.rows] = temp;
      t.DownMove.empty_position = t.DownMove.empty_position + t.rows;
    }
  }

  /*
  The function takes a given State and returns true if moving the blank
  Up is a valid move, else return false
  */
  public boolean IsUpValid(State t)
  {
    if(t.BackPointer != null)
      if(t.BackPointer.empty_position == (t.empty_position - t.rows))
        return false;
    if((t.empty_position - t.rows) < 0 )
      return false;
    return true;
  }

  /*
  The function takes a given State and returns true if moving the blank
  Down is a valid move, else return false
  */
  public boolean IsDownValid(State t)
  {
    if(t.BackPointer != null)
      if(t.BackPointer.empty_position == (t.empty_position + t.rows))
        return false;
    if((t.empty_position + t.rows) >= t.p_size)
      return false;
    return true;
  }

  /*
  The function takes a given State and returns true if moving the blank
  Right is a valid move, else return false
  */
  public boolean IsRightValid(State t)
  {
    if(t.BackPointer != null){
      if(t.BackPointer.empty_position == t.empty_position + 1)
        return false;
      }
    if((t.empty_position + 1) % t.columns == 0 )
      return false;
    return true;
  }

  /*
  The function takes a given State and returns true if moving the blank
  Left is a valid move, else return false
  */
  public boolean IsLeftValid(State t)
  {
    if(t.BackPointer != null)
      if(t.BackPointer.empty_position == t.empty_position - 1)
        return false;
    if(t.empty_position % t.columns == 0)
      return false;
    return true;
  }

  /*
  The function takes a State and prints out each step it took to get to
  that State.
  */
  public void Display_Path(State t)
  {
    char move;
    String p = t.Commands;
    while(t.BackPointer != null)
      t = t.BackPointer;

    while(p.length() != 0)
    {
      move = p.charAt(0);
      p = p.substring(1);

      if(move == 'U')
        t = t.UpMove;
      if(move == 'D')
        t = t.DownMove;
      if(move == 'R')
        t = t.RightMove;
      if(move == 'L')
        t = t.LeftMove;

      Display_Puzzle(t);

    }

  }

}

class PuzzleSolver{
  static public void main(String args[])
    throws java.io.IOException{

      Scanner scan = new Scanner(System.in);

      System.out.print("Enter name of file: ");

      String nameOfFile = scan.nextLine();

      File filename = new File(nameOfFile);

      scan = new Scanner(filename);

      int x = scan.nextInt();
      int y = scan.nextInt();

      int puzzle_array_size = x * y;

      int puzzle_numset[] = new int[puzzle_array_size];

      char seperate = scan.next().charAt(0);
      int counter = 1;
      while(counter <= x * y)
      {
        int input = scan.nextInt();
        puzzle_numset[counter-1] = input;
        counter++;
      }

      State puzzle = new State(puzzle_numset, x, y);
      Operations tool = new Operations();

      System.out.println();
      System.out.println("INITIAL STATE");
      tool.Display_Puzzle(puzzle);

      //List of open State nodes that need to be expanded
      ArrayList<State> openNodes = new ArrayList<State>();
      openNodes.add(puzzle);

      int unevalutatedNodes = 1;
      int evalutatedNodes = 0;
      State current = null;

      //Loop until solution is found
      while(evalutatedNodes < Integer.MAX_VALUE)
      {
        //Use smallest f(n)
        int smallest = Integer.MAX_VALUE;

        //Expand each open State Node
        for(int i = 0;i < openNodes.size();i++)
        {
          State temp = openNodes.get(i);

          //Search for smallest f(n) value and focus on that State.
          if(tool.Get_Heurstic(temp) + temp.Level < smallest)
          {
            smallest = tool.Get_Heurstic(temp) + temp.Level;
            current = temp;
          }
        }

        unevalutatedNodes++;

        //Remove State from list of openNodes
        openNodes.remove(current);

        //Huerstic value is 0 than solution found. Display State information, # of nodes genereated, display solution path.
        if(tool.Get_Heurstic(current) == 0)
        {
          tool.Display_Path(current);
          int totalNodes = unevalutatedNodes + evalutatedNodes;

          System.out.println("# of Open Nodes: "+ unevalutatedNodes);
          System.out.println("# of Closed Nodes: " + evalutatedNodes);
          System.out.println("# of Generated Nodes: " + totalNodes );
          break;
        }

        //If solution not found, then Check the children for a best heurstic value.
        else
        {
          tool.Check_Kids(current, current.rows, current.columns);
          evalutatedNodes++;

          if(current.LeftMove != null){
            openNodes.add(current.LeftMove);
          }
          if(current.RightMove != null){
            openNodes.add(current.RightMove);
          }
          if(current.DownMove != null){
            openNodes.add(current.DownMove);
          }
          if(current.UpMove != null){
            openNodes.add(current.UpMove);

          }
        }

      }

  }
}
