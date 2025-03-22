package main;

import pieces.Piece;

public class Movements {
	public int oldCol;
	public int oldRow;
	public int newCol;
	public int newRow;
	public Piece piece;
	Piece capture;
	
	public Movements(Board board,Piece piece,int newCol,int newRow) {

		this.oldRow = piece.row;
		this.oldCol = piece.col;
		this.newCol = newCol;
		this.newRow = newRow;
		
		this.piece = piece;
		this.capture = board.getPiece(newCol, newRow);
		
	}
	
	private static String getEnPassantSquare(Board board) {
	    Movements lastMove = board.getLastMove();
	    if (lastMove != null && lastMove.piece.type == Type.PAWN) {
	        if (Math.abs(lastMove.oldRow - lastMove.newRow) == 2) {
	            int targetRow = (lastMove.oldRow + lastMove.newRow) / 2;
	            return "" + (char) ('a' + lastMove.newCol) + (8 - targetRow);
	        }
	    }
	    return "-";
	}
	
	private static String getCastlingRights(Board board) {
	    String rights = "";
	    if (board.whiteKingSideCastle) rights += "K";
	    if (board.whiteQueenSideCastle) rights += "Q";
	    if (board.blackKingSideCastle) rights += "k";
	    if (board.blackQueenSideCastle) rights += "q";
	    return rights;
	}
	
	private static String getFENChar(Piece piece) {
	    char symbol = ' ';
	    switch (piece.type) {
	        case PAWN: symbol = 'p'; break;
	        case KNIGHT: symbol = 'n'; break;
	        case BISHOP: symbol = 'b'; break;
	        case ROOK: symbol = 'r'; break;
	        case QUEEN: symbol = 'q'; break;
	        case KING: symbol = 'k'; break;
	    }
	    return piece.isWhite ? Character.toUpperCase(symbol) + "" : symbol + "";
	}
	
	
	public static String generateFEN(Board board) {
		StringBuilder fen = new StringBuilder("");
		
		// 1. Piece Placement
	    for (int row = 0; row < 8; row++) {
	        int emptyCount = 0;
	        for (int col = 0; col < 8; col++) {
	            Piece piece = board.getPiece(col, row);
	            if (piece == null) {
	                emptyCount++;
	            } else {
	                if (emptyCount > 0) {
	                    fen.append(emptyCount);
	                    emptyCount = 0;
	                }
	                fen.append(getFENChar(piece));
	            }
	        }
	        if (emptyCount > 0) fen.append(emptyCount);
	        if (row < 7) fen.append("/");
	        
	    }
	    
	    // 2. Active Color
	    fen.append(" ").append(board.isWhiteToMove ? "w" : "b");

	    // 3. Castling Rights
	    String castling = getCastlingRights(board);
	    fen.append(" ").append(castling.isEmpty() ? "-" : castling);

	    // 4. En Passant Target
	    String enPassant = getEnPassantSquare(board);
	    fen.append(" ").append(enPassant);

	    // 5. Halfmove Clock (for 50-move rule)
	    fen.append(" ").append(CheckFinder.getHalfMoveClock());

	    // 6. Fullmove Number
	    fen.append(" ").append(board.getFullmoveNumber());

	    return fen.toString();
	}
}
