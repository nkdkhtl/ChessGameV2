package bot;

import java.util.List;
import java.util.Random;

import main.Board;
import main.Movements;

public class EasyBot extends Bot {
	private static final Random random = new Random();

    public EasyBot(Board board, boolean isHardMode, boolean isWhiteBot) {
        super(board, isHardMode, isWhiteBot);
    }

    public Movements getRandomMove() {
        List<Movements> legalMoves = getAllLegalMoves(board.deepCopy());
        if (legalMoves.isEmpty()) {
            return null;
        }
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }

    @Override
    public Movements getMove() {
        return getRandomMove();
    }
}
