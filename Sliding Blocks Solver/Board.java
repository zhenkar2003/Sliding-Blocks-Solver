import java.util.*;

import static java.lang.System.exit;
import static java.lang.System.setSecurityManager;

public class Board implements Comparable<Board>, Cloneable {
    // should have multiple blocks in itself , contain the, some way
    // should have a reference to previous board
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;
    static Board goal = null;

    private Block blockMoved; // reference to block that was last moved
    private coordinates size; // size of the overall board
    private Board parent; // previous Board in configuration
    private coordinates blockMovedDir; // indicates which direction the last block was moved in
    private HashMap<Integer, Block> allBlocks; // a collection of all the blocks that have a unique number
    private HashSet<Block> blocks; // Hash set of blocks coming in from solver
    private HashSet<coordinates> emptySpaces;
    private int priority;

    public int getPrior() {
        return priority;
    }

    public Board getParent() {
        return parent;
    }

    public coordinates getBlockMovedDir() {
        return blockMovedDir;
    }

    public Block getBlockMoved() {
        return blockMoved;
    }

    public HashSet<coordinates> getEmptySpaces() {
        return emptySpaces;
    }

    public HashMap<Integer, Block> getAllBlocks() {
        return allBlocks;
    }

    public HashSet<Block> getBlocks() {
        return blocks;
    }

    public coordinates getSize() {
        return size;
    }

    /**
     * general constructor of board. Allocates all needed information in memory and
     * copies over from hashSets to private variable hashSets.
     * 
     * @param configuration the given hashSet of blocks that represent the blocks in
     *                      the board
     * @param empty         the given hashSet of coordinates that represent all the
     *                      empty spaces in the board
     * @param size          represents the size of the board
     * @throws Exception
     */
    public Board(HashSet<Block> configuration, HashSet<coordinates> empty, coordinates size) throws Exception {

        blocks = new HashSet<>();
        allBlocks = new HashMap<>();
        emptySpaces = new HashSet<>();
        Iterator<Block> it1 = configuration.iterator();
        this.size = new coordinates(size);
        while (it1.hasNext()) {
            Block next = it1.next();
            blocks.add(next);
            System.out.println("The hashCode of " + next.getMyNumber() + " is " + next.hashCode());
            allBlocks.put(next.hashCode(), next);
            
        }
        for (int i = 0; i < this.size.getRow(); i++) {
            for (int j = 0; j < this.size.getCol(); j++) {
                emptySpaces.add(new coordinates(i, j));
            }
        }
        Iterator<Block> it2 = blocks.iterator();
        while (it2.hasNext()) {
            Block b = it2.next();
            b.addCoordinates(emptySpaces);
        }

        
    }

    /**
     * iterates through the blocks of the goal board and checks to see if the
     * current board contains every single block of the goal board
     * 
     * @param goalBoard it is the final configuration board , the goal board
     * @return true if the current board equals goal board , false otherwise
     */
    public boolean isSolved(Board goalBoard) {
        Iterator<Block> goal = goalBoard.blocks.iterator();

        while (goal.hasNext()) {
            if (!blocks.contains(goal.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param prev      reference to the previous board
     * @param moved     reference to the newly moved board
     * @param direction reference to the direction that the last block was moved
     */
    // sets info to this if was not there , only fires if it wasnt previously set
    public void previousInstance(Board prev, Block moved, coordinates direction) {
        if (blockMovedDir == null && parent == null && blockMoved == null) {
            parent = prev;
            blockMoved = moved;
            blockMovedDir = new coordinates(direction);
        }
    }

    /**
     * find the block and take the desired destination and move the block in that
     * direction.update empty spaces and blocks if movement is successful. validate
     * all positions and destination position before moving
     * 
     * @param findMe      this is the block that will be used in the moving process
     * @param destination this is the destination coordinate where we want to move
     *                    our block
     * @return new board if movement is successful , null otherwise
     * @throws Exception
     */
    public Board moveOneBlock(Block b, coordinates destination) throws Exception {
        // Iterator<Block> it = blocks.iterator();
         boolean success = false;
         Board clone = clone();
         Block findMe = null;
         Iterator<Block> it = blocks.iterator();
         while (it.hasNext()) {
             Block next = it.next();
             if (next.equals(b)) {
                 findMe = b.clone();
             }
         }
         //Block findMe = clone.getBlock(b.getSize().getRow(), b.getSize().getCol() , b.getUpperLeft(),b.hashCode());
         if (findMe != null) 
         success = true;
        //  clone.parent = this;
        // System.out.println("success is: " + success);

        // while (it.hasNext()) {
        //     if (it.next().equals(findMe)) {
        //         System.out.println("I reached here111");
        //         success = true;
        //     }
        // }
        if (!success) {
            
            return null;
        }
       
        if (!destination.isValidPosition() || destination.getRow() > size.getRow()
                || destination.getCol() > size.getCol()) {
            return null;
        }
        coordinates oneUp = new coordinates(findMe.oneUp());

        if (oneUp.equals(destination)) {
            success = findMe.moveBlock(UP, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }
        coordinates oneRight = new coordinates(findMe.oneRight());
        if (oneRight.equals(destination)) {
            success = findMe.moveBlock(RIGHT, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }
        coordinates oneDown = new coordinates(findMe.oneDown());
        if (oneDown.equals(destination)) {
            success = findMe.moveBlock(DOWN, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }
        coordinates oneLeft = new coordinates(findMe.oneLeft());
        if (oneLeft.equals(destination)) {
            success = findMe.moveBlock(LEFT, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }
        if(success){
            clone.parent = this;
            clone.blockMoved = findMe;
            clone.blockMovedDir = destination;
            return clone;
        }
        return null;
    }

    /**
     * uses the info of the block and looks for the Block in the Board. MyNumber
     * only used , the rest are not used
     * 
     * @param height   however many the block has
     * @param width    however many columns the block has
     * @param row      represents the upperLeft coordinate's row of the block.
     * @param col      represents the upperLeft coordinate's column of the block
     * @param myNumber the number that the block was assigned in the HashMap
     * @return the block if its is found , null otherwise
     * @throws Exception
     */
    public Block getBlock(int height, int width, int row, int col, int myNumber) throws Exception {
        // Iterator<Block> it = this.blocks.iterator();
        // while (it.hasNext()) {
        //     Block found = it.next();
        //     if (found.getMyNumber() == myNumber) {
        //         return found;
        //     }
        // }
        Block b = this.allBlocks.get(myNumber);
        return b == null ? null : b;
    }

    /**
     * uses the info of the block and looks for the Block in the Board
     * 
     * @param height   however many the block has
     * @param width    however many columns the block has
     * @param pos      represents the upperLeft coordinates of the block.
     * @param myNumber the number that the block was assigned in the HashMap
     * @return the block if its is found , null otherwise
     * @throws Exception
     */
    public Block getBlock(int height, int width, coordinates pos, int myNumber) throws Exception {
        return getBlock(height, width, pos.getRow(), pos.getCol(), myNumber);
    }
    

    public String toString() {
        StringBuilder sb = new StringBuilder("Printing the Hash set of blocks:\n");
        Iterator it = blocks.iterator();
        while (it.hasNext()) {
            sb.append(it.next() + "\n");
        }
        sb.append("\nPrinting the empty coordinates\n");
        Iterator it1 = emptySpaces.iterator();
        while (it1.hasNext()) {
            sb.append(it1.next() + "\n");
        }
        Iterator it2 = allBlocks.keySet().iterator();
        while (it2.hasNext()) {
            int n = (int) it2.next();
            sb.append("number: " + n + ":\n" + allBlocks.get(n));
            sb.append("\n\n");
        } 
        sb.append("\n");
        return sb.toString();
    }

    // used for debugging , print board
    /**
     * print the board in a double for loop and a 2 dimensional array. Each block
     * coordinate will print a number if there is an empty space , print null. Use
     * Block's number that was assigned to print in each box of the array
     */
    public void printBoard() {
        String[][] board = new String[size.getRow()][size.getCol()];
        Iterator iterator = this.blocks.iterator();

        while (iterator.hasNext()) {
            Block currentBlock = (Block) iterator.next();
            int currentNumber = currentBlock.getMyNumber();

            for (int i = currentBlock.getUpperLeft().getRow(); i < currentBlock.getBottomRight().getRow(); i++) {
                for (int j = currentBlock.getUpperLeft().getCol(); j < currentBlock.getBottomRight().getCol(); j++) {
                    board[i][j] = currentNumber + "";
                    if (currentNumber % 10 == 1 && currentNumber % 100 != 11)
                        board[i][j] += "st";
                    else if (currentNumber % 10 == 2)
                        board[i][j] += "nd";
                    else if (currentNumber % 10 == 3)
                        board[i][j] += "rd";
                    else
                        board[i][j] += "th";
                }
            }
        }
        for (int i = 0; i < this.size.getRow(); i++) {
            for (int j = 0; j < this.size.getCol(); j++) {
                System.out.printf("%8S", "[" + board[i][j] + "] ");

            }
            System.out.println();
        }
    }

    // we have to check if boards are the same. we check the size and we check if
    // all the blocks in there are in the
    // same spots

    /**
     * equals compares every single Hashable and potentially dynamic part of 2
     * Boards to ensure that the two are identical if the method returns true.
     * 
     * @param other is a Board that will be compared with this Board
     * @return true if this Board is equal to other Board , false otherwise
     */
    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Board b = (Board) other;
        if (blockMoved != null && size.equals(b.size)&& priority == b.priority) {
            if (blocks.size() != b.blocks.size() || emptySpaces.size() != b.emptySpaces.size()
                    || allBlocks.size() != b.allBlocks.size()) {
                return false;
            }
            return blocks.containsAll(b.blocks) && emptySpaces.containsAll(b.emptySpaces)
                    && allBlocks.equals(b.allBlocks) && hashCode() == b.hashCode();
        }
        return false;
    }

    /**
     * makes sure to copy every single detail over to a new Board with memory
     * allocated in a new address.
     * 
     * @return a Board that is identical to this Board
     * @throws Exception
     */
    public Board deepClone() throws Exception {
        HashSet<Block> b = new HashSet<>();
            Iterator it0 = blocks.iterator();

            while (it0.hasNext()) {
                Block next = (Block)it0.next();
                b.add(new Block(new coordinates(next.getSize()), new coordinates(next.getUpperLeft()), next.getMyNumber(), new coordinates(next.getMyBoardSize())));
            }

            HashSet<coordinates> e = new HashSet<>();
            Iterator it1 = emptySpaces.iterator();
            while (it1.hasNext()) {
                coordinates next =(coordinates) it1.next();
                e.add(new coordinates(next));
            }

            Iterator<Integer> it2 = allBlocks.keySet().iterator();
            while (it2.hasNext()) {
                Integer in = it2.next();
                Block next = allBlocks.get(in);
            }
            return new Board(b, e, new coordinates(size));
    }

    /**
     * HashCode is the multiplication of all the HashCodes of the Blocks in this
     * Board
     * 
     * @return HashCode of this Block
     */
    @Override
    public int hashCode() {
        Iterator<Block> it0 = blocks.iterator();
        int hashCode = 1;
        while (it0.hasNext()) {
            hashCode *= it0.next().hashCode();
        }
        return hashCode;
    }

    /**
     * if priority isn't 0 to begin with , update it then return it
     * 
     * @return the cost/priority from current Board to goal Board
     */
    public int getPriority() {
        if (this.priority != 0) {
            this.getCost(goal);
        }
        return this.priority;
    }

    /**
     * this method is necessary in order to be able to sort Boards in the priority
     * queue based on their priority
     * 
     * @param other Board that is being compared with this
     * @return the difference between the priorities
     */
    public int compareTo(Board other) {
        return this.getPriority() - other.getPriority();
    }

    // the lower priority the better

    

    /**
     *
     * @param goal
     */
    public void getCost(Board goal) {
        if (this.priority != 0)
            System.out.println("somethings wrong , priority was not 0");
        int temp = 0;
        LinkedList<Block> checkMe = new LinkedList<>(); // changes maybe needed
        for (Block current : this.blocks) {
            checkMe.add(current);
        }

        for (Block current : goal.getBlocks()) {
            if (this.blocks.contains(current))
                checkMe.remove(current);
            else
                temp += this.Cost(checkMe, current);
        }
        priority = temp;
    }

    /**
     *
     * @param checkMe
     * @param other
     * @return
     */
    private int Cost(LinkedList<Block> checkMe, Block other) {
        int returnMe = Integer.MAX_VALUE;
        int currentMin;

        Block closest = null;
        for (Block b : checkMe) {
            if (b.getSize().getRow() != other.getSize().getRow() || b.getSize().getCol() != other.getSize().getCol())
                continue;

            currentMin = other.getUpperLeft().manhattanDist(b.getUpperLeft(), other.getUpperLeft());

            if (currentMin < returnMe) {
                returnMe = currentMin;
                closest = b;
            }
        }
        if (returnMe == Integer.MAX_VALUE)
            System.out.println("returning max value , goal board doesnt exist");
        checkMe.remove(closest);
        return returnMe;
    }

    public Board clone() throws CloneNotSupportedException {
        try {
            Board copy = (Board) super.clone();
            //copy.blockMoved = (Block) blockMoved.clone();
            copy.size = (coordinates) size.clone();
            //copy.parent = (Board) parent.clone();
            //copy.blockMovedDir = (coordinates) blockMovedDir.clone();
            HashSet<Block> b = new HashSet<>();
            Iterator it0 = blocks.iterator();
            while (it0.hasNext()) {
                b.add(((Block) it0.next()).clone());
            }
            copy.blocks = b;

            HashSet<coordinates> e = new HashSet<>();
            Iterator it1 = emptySpaces.iterator();
            while (it1.hasNext()) {
                e.add(((coordinates) it1.next()).clone());
            }
            copy.emptySpaces = e;

            HashMap<Integer, Block> h = new HashMap<>();
            Iterator<Integer> it2 = allBlocks.keySet().iterator();
            while (it2.hasNext()) {
                Integer in = it2.next();
                Block next = allBlocks.get(in);
                h.put(in, next.clone());
            }
            copy.allBlocks = h;

            return copy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }



    // public static void main(String[] args) throws Exception {
    //     HashSet<Block> blocks = new HashSet<>();
    //     HashSet<coordinates> empty = new HashSet<>();
    //     coordinates size = new coordinates(7, 7);
        
    //     Block b = new Block(new coordinates(2, 1), new coordinates(4, 0), 1, size);
    //     Block up = new Block(new coordinates(1, 1), new coordinates(3, 0), 2, size);
    //     Block right = new Block(new coordinates(2, 1), new coordinates(4, 2), 3, size);

    //     blocks.add(b);
    //     blocks.add(up);
    //     blocks.add(right);

    //     coordinates upperLeft = b.getUpperLeft();
    //     coordinates upUpperLeft = up.getUpperLeft();
    //     coordinates rightUpperLeft = right.getUpperLeft();
    //     coordinates bottomRight = b.getBottomRight();
    //     coordinates upBottomRight = up.getBottomRight();
    //     coordinates rightBottomRight = right.getBottomRight();

    //     for (int i = 0; i < size.getRow(); i++) {
    //         for (int j = 0; j < size.getCol(); j++) {
    //             empty.add(new coordinates(i, j));
    //         }
    //     }

        // b.addCoordinates(empty);
        // up.addCoordinates(empty);
        // right.addCoordinates(empty);

        // Board board = new Board(blocks, empty, size);

        // b.addCoordinates(board.emptySpaces);
        // up.addCoordinates(board.emptySpaces);
        // right.addCoordinates(board.emptySpaces);

        // System.out.println(board.hashCode());

        // board.printBoard();
        // System.out.println();
        // System.out.println("After moving 0");
        // Board board1 = board.moveOneBlock(right, new coordinates(5, 2));
        // if(board1 == null)
        // System.out.println("board is null");
        // board1.printBoard();
        // System.out.println();
        // System.out.println("After moving 1");
        // Board board2 = board1.moveOneBlock(b, new coordinates(5, 0));
        // board2.printBoard();
        // System.out.println();
        // System.out.println("After moving 2");
        // Board board3 = board2.moveOneBlock(up, new coordinates(3, 1));
        // board3.printBoard();
        // System.out.println();
        // System.out.println("After moving 3");
        // Iterator<Block> it = blocks.iterator();
        // while (it.hasNext()) {
        //     Block found = it.next();
        //     System.out.println(found.getUpperLeft());
        //     if (found.getUpperLeft().equals(new coordinates(3, 1))) {
        //         up = found.deepClone();
        //     }
        // }
        // Board board4 = board3.moveOneBlock(up, new coordinates(4, 1));
        // board4.printBoard();
        // System.out.println();
 //   }
}