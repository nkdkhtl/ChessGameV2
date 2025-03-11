package main;

import pieces.Piece;

public class Movements {

	int oldCol;
	int oldRow;
	int newCol;
	int newRow;
	
	Piece piece;
	Piece capture;
	
	public Movements(Board board,Piece piece,int newCol,int newRow) {

		this.oldRow = piece.row;
		this.oldCol = piece.col;
		this.newCol = newCol;
		this.newRow = newRow;
		
		this.piece = piece;
		this.capture = board.getPiece(newCol, newRow);
		
	}
}
