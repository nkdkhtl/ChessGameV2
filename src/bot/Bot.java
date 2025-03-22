package bot;
import java.util.ArrayList;
import java.util.List;

import main.Board;
import main.Movements;
import pieces.Piece;

public class Bot {
    Board board;
    public boolean isWhiteBot;
    public boolean isHardMode;

    public Bot(Board board, boolean isHardMode, boolean isWhiteBot) {
        this.board = board;
        this.isHardMode = isHardMode;
        this.isWhiteBot = isWhiteBot;
    }

    public List<Movements> getAllLegalMoves() {
    	List<Movements> legalMoves = new ArrayList<>();
        for (Piece piece : board.pieceList) {
            if (piece.isWhite == isWhiteBot) {
                for (int col = 0; col < 8; col++) {
                    for (int row = 0; row < 8; row++) {
                        if (piece.isValidMovement(col, row) && !piece.isCollide(col, row)) {
                            Movements move = new Movements(board, piece, col, row);
                            if (!board.checkFinder.isKingInCheck(move) 
                            		&& board.isValidMove(move)) {
                            	legalMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
		return legalMoves;
     
    }

    public Movements getMove() {
        return null;
    }
}