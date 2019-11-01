package com.test.chess;

import java.util.ArrayList;

public class Mstc {
    public static int boardWidth = 8;
    public static int boardHeight = 8;
    public static int empty = 0;
    public static int black = 1;
    public static int white = 2;
    public static int draw = -2;
    public static int notEnd = -1;
    public static int _initSimulateDepth = 24;
    public static int _initSimulateTime = 16;
    public static int _initMaxIteration = 4096;
    public static int maxIteration = _initMaxIteration;
    public static int simulateDepth = _initSimulateDepth;
    public static int simulateTime = _initSimulateTime;
    public static Dir[] dirs = new Dir[ 8 ];
    public static int[] map = new int[ 64 ];
    public static int sumCount = 0;
    public static int hitCount = 0;
    public static int maxDepth = 0;

    {
        dirs[ 0 ] = ( new Dir( -1, -1 ) );
        dirs[ 1 ] = ( new Dir( 0, -1 ) );
        dirs[ 2 ] = ( new Dir( 1, -1 ) );
        dirs[ 3 ] = ( new Dir( -1, 0 ) );
        dirs[ 4 ] = ( new Dir( 1, 0 ) );
        dirs[ 5 ] = ( new Dir( -1, 1 ) );
        dirs[ 6 ] = ( new Dir( 0, 1 ) );
        dirs[ 7 ] = ( new Dir( 1, 1 ) );
    }

    public static Integer get( int i, int j, int[] board ) {
        if (i < 0 || j < 0 || i >= Mstc.boardWidth || j >= Mstc.boardWidth) return null;
        return board[ i * Mstc.boardWidth + j ];
    }

    public static void set( int i, int j, int val, int[] board ) {
        int idx = i * Mstc.boardWidth + j;
        board[ idx ] = val;
    }

    public static void moveOn( Move move, int[] board ) {
        int origin = get( move.x0, move.x0, board );
        set( move.y1, move.x1, origin, board );
        set( move.y0, move.x0, Mstc.empty, board );
    }


    public static Move rollOut( ArrayList<Move> array ) {
        if (array.size( ) == 0) return null;
        return array.get( new Double( Math.floor( ( Math.random( ) * array.size( ) ) % array.size( ) ) ).intValue( ) );
    }

    public static int chessCount( int turn, int[] board ) {
        int res = 0;
        for (int e : board) {
            if (e == turn) ++res;
        }
        return res;
    }

    public static boolean hasNeighbour( int i, int j, Dir dir, int[] board ) {
        int turn = get( i, j, board );
        Integer neighbour = get( i + dir.dy, j + dir.dx, board );
        return ( neighbour != null && neighbour == turn );
    }

    public static boolean connected( int turn, int[] board ) {
        for (int i = 0; i < 64; ++i) {
            map[ i ] = -1;
        }

        for (int i = 0; i < Mstc.boardHeight; ++i) {
            for (int j = 0; j < Mstc.boardWidth; ++j) {
                int chess = get( i, j, board );
                // if it's my chess
                if (chess == turn) {
                    Mstc.map[ i * Mstc.boardWidth + j ] = i * Mstc.boardWidth + j;
                    boolean hasNeigh = false;
                    for (int k = 0; k < 4; ++k) {
                        Dir dir = Mstc.dirs[ k ];
                        if (hasNeighbour( i, j, dir, board )) {
                            Mstc.map[ i * Mstc.boardWidth + j ] = ( i + dir.dy ) * Mstc.boardWidth + j + dir.dx;
                            hasNeigh = true;
                            break;
                        }
                    }
                    for (int k = 4; k < Mstc.dirs.length; ++k) {
                        if (hasNeighbour( i, j, Mstc.dirs[ k ], board )) {
                            hasNeigh = true;
                            break;
                        }
                    }
                    // rapid judge
                    if (!hasNeigh) {
                        return false;
                    }
                }
            }
        }
        // check set
        Integer aRoot = null;
        for (int i = 0; i < Mstc.map.length; ++i) {
            int cur = map[ i ];
            if (cur != -1) {
                while (cur != map[ cur ]) {
                    cur = map[ cur ];
                }
                if (aRoot == null) {
                    aRoot = cur;
                } else if (aRoot != cur) {
                    return false;
                }
            }
        }
        return true;
    }

    //-1 for not end, 0 for black wins, 1 for white wins; if draw, regard as fail and return enemy item
    public static int gameCondition( int turn, int[] board ) {
        int enemyTurn = enemyPlayer( turn );
        int count = chessCount( turn, board );
        int enemyCount = chessCount( enemyTurn, board );
        return enemyCount == 1 ? turn
                : count == 1 ? enemyTurn
                : connected( turn, board ) ? turn
                : connected( enemyTurn, board ) ? enemyTurn
                : Mstc.notEnd;//not end
    }

    public static int enemyPlayer( int turn ) {
        return turn == black ? white : black;
    }

    int turn;

    public Mstc( int turn ) {
        this.turn = turn;
    }

    Move makeMove( int[] board ) {
        //assume there is available moves
        Node root = new Node( null, this.turn, null, board.clone( ) );
        Mstc.hitCount = 0;
        Mstc.maxDepth = 0;
        Mstc.sumCount = 0;

        for (int i = 0; i < maxIteration; ++i) {
            root.search( );
        }
        Mstc.adjust( );
        Node next = root.maxChild( );
        Move move = next.producedMove;
        return move;
    }

    public static void adjust( ) {
//        simulateDepth = maxDepth;
//        simulateTime = _initSimulateDepth * _initSimulateTime / simulateDepth;
//        maxIteration = _initMaxIteration * _initSimulateDepth / simulateDepth;
    }

    public static ArrayList<Move> allChessMoves( int turn, int[] board ) {
        ArrayList<Move> moves = new ArrayList<>( );
        for (int i = 0; i < Mstc.boardHeight; ++i) {
            for (int j = 0; j < Mstc.boardWidth; ++j) {
                //if its bot's chess
                if (Mstc.get( i, j, board ) == turn) {
                    Mstc.chessMoves( i, j, moves, board );
                }
            }
        }
        return moves;
    }

    static int[] nums = new int[ 4 ];

    public static void chessMoves( int i, int j, ArrayList<Move> moves, int[] board ) {
        int turn = get( i, j, board );
        int enemyTurn = enemyPlayer( turn );
        for (int k = 0; k < 4; ++k) {
            Dir dir = dirs[ k ];
            nums[ k ] = cheessNums( i, j, dir, board );
        }
        for (int k = 0; k < dirs.length; ++k) {
            Dir dir = dirs[ k ];
            int chessNum = nums[ k >= 4 ? 7 - k : k ];
            boolean canMove = true;
            Integer nextPos = -1;
            for (int step = 1; step <= chessNum; ++step) {
                nextPos = get( i + dir.dy * step, j + dir.dx * step, board );
                //out of bound or blocked by enemy
                if (nextPos == null || ( nextPos == enemyTurn && step != chessNum )) {
                    canMove = false;
                    break;
                }
            }
            if (canMove && nextPos != turn) {
                moves.add( new Move( j, i, j + dir.dx * chessNum, i + dir.dy * chessNum ) );
            }
        }
    }

    public static int cheessNums( int i, int j, Dir dir, int[] board ) {
        int count = -1;
        int x = j;
        int y = i;
        Integer nextPos;
        while (( nextPos = get( y, x, board ) ) != null) {
            y += dir.dy;
            x += dir.dx;
            if (nextPos != empty) ++count;
        }
        x = j;
        y = i;
        while (( nextPos = get( y, x, board ) ) != null) {
            y -= dir.dy;
            x -= dir.dx;
            if (nextPos != empty) ++count;
        }
        return count;
    }
}
