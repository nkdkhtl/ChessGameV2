package main;

import java.util.HashMap;

import pieces.Piece;

public class CheckFinder {
	Board board;
	public static int halfMoveClock = 0;
	HashMap<String, Integer> positionHistory = new HashMap<>();
	
	public CheckFinder(Board board) {
		this.board = board;
	}
	
	public static int getHalfMoveClock() { 
		return halfMoveClock;
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
		
		System.out.println("Checking if king is in check at position: (" + kingCol + ", " + kingRow + ")");

		boolean inCheck = checkByRook(move.newCol, move.newRow, king, kingCol, kingRow, 0, 1) // up
			        || checkByRook(move.newCol, move.newRow, king, kingCol, kingRow, 1, 0) // right
			        || checkByRook(move.newCol, move.newRow, king, kingCol, kingRow, 0, -1) // down
			        || checkByRook(move.newCol, move.newRow, king, kingCol, kingRow, -1, 0) // left
			        || checkByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, -1) // up left
			        || checkByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, 1) // down right
			        || checkByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, -1) // up right
			        || checkByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, 1) // down left
			        || checkByKnight(move.newCol, move.newRow, king, kingCol, kingRow)
			        || checkByPawn(move.newCol, move.newRow, king, kingCol, kingRow)
			        || checkByKing(king, kingCol, kingRow);

		 System.out.println("King is in check: " + inCheck);
		 return inCheck;   
		
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

	

	public boolean isCheckmate(Piece king) {
		
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
	
	public boolean isTimeout(boolean isWhite) {
        return isWhite ? board.whiteTimer.isOutOfTime() : board.blackTimer.isOutOfTime();
    }
	
	
	
	//stalemate checking
	public boolean isStalemate(Piece king) {

		if (isTimeout(king.isWhite)) {
			return false;
		}
		
		for (Piece p : board.pieceList) {
			if (board.isSameTeam(p, king)) {
				for (int row = 0; row < board.rows; row++) {
					for (int col = 0; col < board.cols; col++) {
						Movements move = new Movements(board, p, col, row);
						if (board.isValidMove(move)) {
							return false; // If a valid move is found, it's not stalemate.
						}
					}
				}
			}
		}
		return true; // No valid moves and king is not in check -> stalemate.
	}
	
	public boolean isInsufficientMaterial() {
	    int whiteBishops = 0, blackBishops = 0;
	    int whiteKnights = 0, blackKnights = 0;
	    int whitePieces = 0, blackPieces = 0;

	    for (Piece p : board.pieceList) {
	        if (p.type == Type.PAWN || p.type == Type.QUEEN || p.type == Type.ROOK) {
	            return false; // If there's a pawn, queen, or rook, it's not insufficient.
	        }
	        if (p.isWhite) {
	            whitePieces++;
	            if (p.type == Type.BISHOP) whiteBishops++;
	            if (p.type == Type.KNIGHT) whiteKnights++;
	        } else {
	            blackPieces++;
	            if (p.type == Type.BISHOP) blackBishops++;
	            if (p.type == Type.KNIGHT) blackKnights++;
	        }
	    }

	    // King vs King
	    if (whitePieces == 1 && blackPieces == 1) {
	        return true;
	    }

	    // King and single minor piece vs King
	    if ((whitePieces == 2 && (whiteBishops == 1 || whiteKnights == 1) && blackPieces == 1) ||
	        (blackPieces == 2 && (blackBishops == 1 || blackKnights == 1) && whitePieces == 1)) {
	        return true;
	    }

	    // King and two knights vs King (special case, often a draw)
	    if ((whitePieces == 3 && whiteKnights == 2 && blackPieces == 1) ||
	        (blackPieces == 3 && blackKnights == 2 && whitePieces == 1)) {
	        return true;
	    }

	    return false;
	}

	
	public void updatehalfMoveClock(Movements move) {
	    Piece targetPiece = board.getPiece(move.newCol, move.newRow);
	    
	    // Reset the counter if a pawn moves or a piece is captured
	    if (move.piece.type == Type.PAWN || (targetPiece != null && !board.isSameTeam(move.piece, targetPiece))) {
	        halfMoveClock = 0; 
	    } else {
	        halfMoveClock++;
	    }
	}
	
	
	
	public boolean isFiftyMoveRule() {
		return halfMoveClock >= 100;
	}
	
	public void recordPosition(String fen) {
	    positionHistory.put(fen, positionHistory.getOrDefault(fen, 0) + 1);
	}

    public boolean isThreefoldRepetition() {
        for (int count : positionHistory.values()) {
            if (count >= 3) {
                return true;
            }
        }
        return false;
    }

    public void removePosition(String fen) {
        if (positionHistory.containsKey(fen)) {
            int count = positionHistory.get(fen);
            if (count > 1) {
                positionHistory.put(fen, count - 1);
            } else {
                positionHistory.remove(fen);
            }
        }
    }

}
