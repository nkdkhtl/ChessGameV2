package bot;
import java.util.ArrayList;
import java.util.List;

import GameLauncher.Board;
import GameLauncher.Movements;
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
        for (Piece piece : virtualBoard.pieceList) {
            if (piece.isWhite == isWhiteBot) {
                for (int row = 0; row < virtualBoard.rows; row++) {
                    for (int col = 0; col < virtualBoard.cols; col++) {
                    	virtualBoard.selectedPiece = piece;
                        Movements move = new Movements(virtualBoard, piece, col, row);
                        if (virtualBoard.isValidMove(move)) {
                             legalMoves.add(move);
                        }
                    }
                }
           }
        }
        return legalMoves;
    }

    public abstract Movements getMove();
}