package com.test.chess;

import java.util.ArrayList;

class Move {

    public int y1;
    public int x0;
    public int y0;
    public int x1;

    public Move( int x0, int y0, int x1, int y1 ) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }
}

class Dir {
    public int dx, dy;

    Dir( int dx, int dy ) {
        this.dx = dx;
        this.dy = dy;
    }
}


public class Node {
    public int A = 0; //black wins
    public int B = 0;
    public ArrayList<Move> totalMoves;
    public ArrayList<Move> remainMoves;
    public ArrayList<Node> child = new ArrayList<>( );
    public int turn;
    public int[] board;
    public Node parent;
    public Move producedMove;

    //board should be a copy
    public Node( Node parent, int turn, Move producedMove, int[] board ) {
        this.parent = parent;
        this.turn = turn;
        this.board = board;
        this.producedMove = producedMove;
        this.remainMoves = Mstc.allChessMoves( turn, board );
        this.totalMoves = new ArrayList<>( this.remainMoves );
    }

    boolean search( ) {
        // if game not ends here, and can still search down
        Node expParNode = this.select( );// select parent node
        // if no existed node can expand a new node
        if (expParNode == null) {
            return false;
        }
        Node expNode = expParNode.expand( ); // expand a child node
        for (int i = 0; i < Mstc.simulateTime; i++) {
            expNode.simulate( );
        }
        return true;
    }

    Node select( ) {
        if (this.totalMoves.size( ) == 0) return null;
        else return this.remainMoves.size( ) > 0 ? this : this.maxChild( ).select( );
    }

    Node expand( ) {
        if (Mstc.gameCondition( this.turn, this.board ) != Mstc.notEnd) return this;

        Move move = this.remainMoves.get( 0 );
        this.remainMoves.remove( 0 );

        int enemyTurn = Mstc.enemyPlayer( this.turn );
        int[] nextBoard = this.board.clone( );
        Mstc.moveOn( move, nextBoard );
        Node child = new Node( this, enemyTurn, move, nextBoard );
        this.child.add( child );
        return child;
    }

    void simulate( ) {
        int[] board = this.board.clone( );
        int turn = this.turn;
        int gameRes = -1;
        int count = 0;
        while (count < Mstc.simulateDepth && ( gameRes = Mstc.gameCondition( turn, board ) ) == Mstc.notEnd) {
            Move move = Mstc.rollOut( Mstc.allChessMoves( turn, board ) );
            if (move != null) {
                Mstc.moveOn( move, board );
            }
            turn = Mstc.enemyPlayer( turn ); // flip
            ++count;
        }
        if (count > Mstc.maxDepth) Mstc.maxDepth = count;
        this.backPropagation( gameRes );
    }

    void backPropagation( int gameRes ) {
        // console.log(`[backPropagation]:${gameRes}`)
        Node node = this;
        if (gameRes != Mstc.notEnd) ++Mstc.hitCount;
        do {
            if (gameRes == Mstc.black) {
                ++node.A;
            }
            // console.log(gameRes)
            ++node.B;
            node = node.parent;
        } while (node != null);
    }

    double weight( Node child ) {
        return Math.pow( Math.log( this.B ) / child.B, 0.5 ) + ( this.turn == Mstc.black ? 1.0 * child.A / child.B : 1 - 1.0 * child.A / child.B );
    }

    Node maxChild( ) {
        double maxWeight = -1;
        Node maxChild = null;
        for (Node child : this.child) {
            double childWeight = this.weight( child );
            if (maxChild == null || childWeight > maxWeight) {
                maxWeight = childWeight;
                maxChild = child;
            }
        }
        return maxChild;
    }
}