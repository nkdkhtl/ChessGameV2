package main;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import pieces.Piece;

public class MouseInput extends MouseAdapter {
	
	Board board;
	
	public MouseInput(Board board) {
		this.board = board;
	}
	
	public void enableMouseInput() {
        board.addMouseListener(this);
        board.addMouseMotionListener(this);
    }

    public void disableMouseInput() {
        board.removeMouseListener(this);
        board.removeMouseMotionListener(this);
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int col = e.getX() / Board.SQUARE_SIZE;
		int row = e.getY() / Board.SQUARE_SIZE; 
	
		Piece pieceXY = board.getPiece(col,row);
		
		if (pieceXY != null) { 
            // If a piece is selected, highlight its valid moves
            board.selectedPiece = pieceXY;
            board.validMoves.clear();

            for (int i = 0; i < board.rows; i++) {
                for (int j = 0; j < board.cols; j++) {
                    if (board.isValidMove(new Movements(board, pieceXY, j, i))) {
                        board.validMoves.add(new Point(j, i));
                    }
                }
            }
        } else {
            // If clicked on empty space, deselect the piece
            board.selectedPiece = null;
            board.validMoves.clear();
        }
		
		board.repaint();
	
	}
	@Override 
	public void mousePressed(MouseEvent e) {
		int col = e.getX() / Board.SQUARE_SIZE;
		int row = e.getY() / Board.SQUARE_SIZE; 
	
		Piece pieceXY = board.getPiece(col,row);
		
		if (pieceXY != null) { 
            // If a piece is selected, highlight its valid moves
            board.selectedPiece = pieceXY;
            board.validMoves.clear();

            for (int i = 0; i < board.rows; i++) {
                for (int j = 0; j < board.cols; j++) {
                    if (board.isValidMove(new Movements(board, pieceXY, j, i))) {
                        board.validMoves.add(new Point(j, i));
                    }
                }
            }
        } else {
            // If clicked on empty space, deselect the piece
            board.selectedPiece = null;
            board.validMoves.clear();
        }
		
		board.repaint();
	}
	
	
	@Override 
	public void mouseDragged(MouseEvent e) {
		if (board.selectedPiece != null) {
			board.selectedPiece.xPos = e.getX() - Board.SQUARE_SIZE/2; 
			board.selectedPiece.yPos = e.getY() - Board.SQUARE_SIZE/2; 
			
			board.validMoves.clear();
			board.repaint();
		}
	}
	
	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int col = e.getX() / Board.SQUARE_SIZE;
		int row = e.getY() / Board.SQUARE_SIZE;
		
		if (board.selectedPiece != null) {
			Movements move = new Movements(board,board.selectedPiece,col,row);
		
			if (board.isValidMove(move)) {
				board.makeMove(move);
			} else {
				board.selectedPiece.xPos = board.selectedPiece.col * Board.SQUARE_SIZE;
				board.selectedPiece.yPos = board.selectedPiece.row * Board.SQUARE_SIZE;
			}
		}
		
	    board.validMoves.clear();
		board.selectedPiece = null;
		board.repaint();
	}
	
 }
