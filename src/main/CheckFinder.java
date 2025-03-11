package main;

import pieces.Piece;

public class CheckFinder {
	Board board;
	private int fiftyMoveCounter = 0;
	
	public CheckFinder(Board board) {
		this.board = board;
	}
	
	public boolean isTimeout(boolean isWhite) {
	    if (isWhite) {
	        return board.whiteTimer.isOutOfTime();
	    } else {
	        return board.blackTimer.isOutOfTime();
	    }
	}

	
	public boolean isKingInCheck(Movements move) {
		Piece king = board.findKing(move.piece.isWhite);
		assert king != null;
		
		int kingCol = king.col;
		int kingRow = king.row;
		
		if (board.selectedPiece != null && board.selectedPiece.type == Type.KING) {
			kingCol = move.newCol;
			kingRow = move.newRow;
		}
		
		return checkByRook(move.newCol,move.newRow,king,kingCol,kingRow,0,1) ||  // up
			   checkByRook(move.newCol,move.newRow,king,kingCol,kingRow,1,0) ||  //right
			   checkByRook(move.newCol,move.newRow,king,kingCol,kingRow,0,-1) || //down
			   checkByRook(move.newCol,move.newRow,king,kingCol,kingRow,-1,0) || //left
			   
			   checkByBishop(move.newCol,move.newRow,king,kingCol,kingRow,-1,-1) || //up left
			   checkByBishop(move.newCol,move.newRow,king,kingCol,kingRow,1,1)   || //down right
			   checkByBishop(move.newCol,move.newRow,king,kingCol,kingRow,1,-1)  || //up right
			   checkByBishop(move.newCol,move.newRow,king,kingCol,kingRow,-1,1)  || // down left
			   
			   checkByKnight(move.newCol,move.newRow,king,kingCol,kingRow) ||
			   checkByPawn(move.newCol,move.newRow,king,kingCol,kingRow) ||
			   checkByKing(king,kingCol,kingRow);
	}
	
	private boolean checkByRook(int col,int row,Piece king, int kingCol,int kingRow,int colVal,int rowVal) {
		for (int i = 1;i<8;i++) {
			if (kingCol + (i*colVal) == col && kingRow + (i*rowVal) == row) {
				break;
			}
			Piece p = board.getPiece(kingCol + (i*colVal), kingRow + (i*rowVal));
			if (p != null && p != board.selectedPiece) {
				if (!board.isSameTeam(p,king) && (p.type == Type.ROOK || p.type == Type.QUEEN)) {
					return true;
				}
				break;
			}
		}
		
		return false;
		
	}
	
	private boolean checkByBishop(int col,int row,Piece king, int kingCol,int kingRow,int colVal,int rowVal) {
		for (int i = 1;i<8;i++) {
			if (kingCol - (i*colVal) == col && kingRow - (i*rowVal) == row) {
				break;
			}
			Piece p = board.getPiece(kingCol - (i*colVal), kingRow - (i*rowVal));
			if (p != null && p != board.selectedPiece) {
				if (!board.isSameTeam(p,king) && (p.type == Type.BISHOP || p.type == Type.QUEEN)) {
					return true;
				}
				break;
			}
		}
		
		return false;
	}
	
	private boolean findKnight(Piece p,Piece king,int col,int row) {
		return p != null && !board.isSameTeam(p, king) && p.type == Type.KNIGHT && !(p.col == col && p.row == row);
	}
	
	private boolean checkByKnight(int col,int row,Piece king, int kingCol,int kingRow) {
		return  findKnight(board.getPiece(kingCol-1, kingRow-2),king,col,row) ||
				findKnight(board.getPiece(kingCol+1, kingRow-2),king,col,row) || 
				findKnight(board.getPiece(kingCol+2, kingRow-1),king,col,row) ||
				findKnight(board.getPiece(kingCol+2, kingRow+1),king,col,row) ||
				findKnight(board.getPiece(kingCol+1, kingRow+2),king,col,row) ||
				findKnight(board.getPiece(kingCol-1, kingRow+2),king,col,row) ||
				findKnight(board.getPiece(kingCol-2, kingRow+1),king,col,row) ||
				findKnight(board.getPiece(kingCol-2, kingRow-1),king,col,row);
	}

	private boolean findPawn(Piece p,Piece king,int col,int row) {
		return p != null && !board.isSameTeam(p, king) && p.type == Type.PAWN;
	}
	
	private boolean checkByPawn(int col,int row, Piece king,int kingCol,int kingRow) {
		int color = king.isWhite ? -1 : 1;
		return  findPawn(board.getPiece(kingCol+1, kingRow + color),king,col,row) ||
				findPawn(board.getPiece(kingCol-1,kingRow + color),king,col,row);
	}
	
	
	private boolean findKing(Piece p,Piece king) {
		return p != null && !board.isSameTeam(p, king) && p.type == Type.KING;

	}
	
	private boolean checkByKing(Piece king,int kingCol,int kingRow) {
		return 	findKing(board.getPiece(kingCol-1, kingRow-1), king) ||
				findKing(board.getPiece(kingCol+1, kingRow-1), king) ||
				findKing(board.getPiece(kingCol, kingRow-1), king) ||
				findKing(board.getPiece(kingCol-1, kingRow), king) ||
				findKing(board.getPiece(kingCol+1, kingRow), king) ||
				findKing(board.getPiece(kingCol-1, kingRow+1), king) ||
				findKing(board.getPiece(kingCol+1, kingRow+1), king) ||
				findKing(board.getPiece(kingCol, kingRow+1), king);
				
	}
	
	private boolean isFiftyMoveRule() {
	    return fiftyMoveCounter >= 50;
	}

	// Call this in `Board` class when a move is made
	public void updateFiftyMoveCounter(Movements move) {
	    if (move.piece.type == Type.PAWN || board.getPiece(move.newCol, move.newRow) != null) {
	        fiftyMoveCounter = 0; // Reset if pawn moves or capture happens
	    } else {
	        fiftyMoveCounter++;
	    }
	}
	
	private Piece findBishop(boolean isWhite) {
		for (Piece p : board.pieceList) {
			if (p.type == Type.BISHOP && p.isWhite == isWhite) {
				return p;
			}
		}
		return null;
	}
	
	
	
	public boolean isInsufficientMaterial() {
	    int whitePieces = 0, blackPieces = 0;
	    int whiteKnights = 0, whiteBishops = 0, blackKnights = 0, blackBishops = 0;

	    for (Piece p : board.pieceList) {
	        if (p.type == Type.KING) continue; // Ignore kings
	        
	        if (p.isWhite) {
	            whitePieces++;
	            if (p.type == Type.KNIGHT) whiteKnights++;
	            if (p.type == Type.BISHOP) whiteBishops++;
	        } else {
	            blackPieces++;
	            if (p.type == Type.KNIGHT) blackKnights++;
	            if (p.type == Type.BISHOP) blackBishops++;
	        }
	    }

	    // King vs King
	    if (whitePieces == 0 && blackPieces == 0) return true;

	    // King vs King + single bishop/knight
	    if ((whitePieces == 1 && (whiteKnights == 1 || whiteBishops == 1)) && blackPieces == 0) return true;
	    if ((blackPieces == 1 && (blackKnights == 1 || blackBishops == 1)) && whitePieces == 0) return true;

	    // King + Bishop vs King + Bishop (same color bishops)
	    if (whitePieces == 1 && blackPieces == 1 && whiteBishops == 1 && blackBishops == 1) {
	        Piece whiteBishop = findBishop(true);  
	        Piece blackBishop = findBishop(false);
	        if (whiteBishop != null && blackBishop != null && whiteBishop.getSquareColor() == blackBishop.getSquareColor()) {
	            return true;  // Only draw if bishops are on the same color
	        }
	    }

	    return false;
	}
	
	public boolean isStalemate(boolean isWhite) {
	    
	    if (isInsufficientMaterial() || isFiftyMoveRule()) {
	        return true; // Draw by insufficient material or 50-move rule
	    }
	    
	    Piece king = board.findKing(isWhite);
	    
	    // If king is in check, it's not stalemate
	    if (isKingInCheck(new Movements(board, king, king.col, king.row))) {
	        return false; // King is in check, so it's NOT stalemate
	    }


	    // Check if any valid move exists
	    for (Piece p : board.pieceList) {
	        if (board.isSameTeam(p, king)) { 
	            for (int row = 0; row < board.rows; row++) {
	                for (int col = 0; col < board.cols; col++) {
	                    Movements move = new Movements(board, p, col, row);
	                    if (board.isValidMove(move) && !isKingInCheck(move)) {
	                        return false; // If a valid move exists and doesn't put the king in check, it's not stalemate
	                    }
	                }
	            }
	        }
	    }
	    
	    
	    return true;
	}
	
	public boolean isGameOver(Piece king) {
		
		if (isTimeout(king.isWhite)) {
	        return true;
	    }
		
		for (Piece p : board.pieceList) {
			if (board.isSameTeam(p, king)) {
				board.selectedPiece = p == king ? king : null;
				
				for (int row = 0;row < board.rows;row++) {
					for (int col = 0;col < board.cols;col++) {
						Movements move = new Movements(board,p,col,row);
						if (board.isValidMove(move)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}
