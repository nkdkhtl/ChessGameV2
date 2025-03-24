package bot;
import java.util.ArrayList;
import java.util.List;

import main.Board;
import main.Movements;
import pieces.Piece;

public abstract class Bot {
    protected final Board board;
    public boolean isWhiteBot;
    public boolean isHardMode;

    public Bot(Board board, boolean isHardMode, boolean isWhiteBot) {
        this.board = board;
        this.isHardMode = isHardMode;
        this.isWhiteBot = isWhiteBot;
    }

    public List<Movements> getAllLegalMoves(Board virtualBoard) {
        List<Movements> legalMoves = new ArrayList<>();
        for (Piece piece : board.pieceList) {
            if (piece.isWhite == isWhiteBot) {
                for (int col = 0; col < 8; col++) {
                    for (int row = 0; row < 8; row++) {
                        if (piece.isValidMovement(col, row) && !piece.isCollide(col, row)) {
                            Movements move = new Movements(virtualBoard, piece, col, row);
                            if (!virtualBoard.checkFinder.isKingInCheck(move) && virtualBoard.isValidMove(move)) {
                                legalMoves.add(move);
                                System.out.println("Added legal move: " + piece.type + " to (" + col + ", " + row + ")");
                            } else {
                                System.out.println("Invalid move: " + piece.type + " to (" + col + ", " + row + ")");
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Total legal moves: " + legalMoves.size());
        return legalMoves;
    }

    public abstract Movements getMove();
}