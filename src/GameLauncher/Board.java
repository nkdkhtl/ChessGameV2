package GameLauncher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bot.Bot;
import bot.EasyBot;
import bot.HardBot;
import panels.GameOverPanel;
import panels.PromotionPanel;
import panels.TimerPanel;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;
import utils.ChessTimer;
import utils.SoundManager;

public class Board extends JPanel {
	/**
	 * 
	 */
	private final String boardId = java.util.UUID.randomUUID().toString();
	public static final int SQUARE_SIZE = 80;
	public static final int BOARD_RADIUS = 40;
	public boolean isResign = false;
	public boolean isDraw = false;

	public int cols = 8;
	public int rows = 8;
	public Color primary = new Color(235, 236, 208);
	public Color secondary = new Color(119, 149, 86);

	public String initFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	public ArrayList<Piece> pieceList = new ArrayList<>();

	public Piece selectedPiece;

	protected Stack<String> moveHistory = new Stack<>();

	public ArrayList<Point> validMoves = new ArrayList<>();

	public MouseInput input = new MouseInput(this);

	public CheckFinder checkFinder = new CheckFinder(this);

	public ChessTimer whiteTimer;
	public ChessTimer blackTimer;
	public static int duration = 10;

	// state
	public boolean isWhiteToMove = true;

	private Bot bot;
	private boolean isBotPlaying = false;
	private boolean isWhiteBot;
	private boolean isPromoting = false;
	public boolean isGameOver = false;

	private boolean firstMoveMade = false;

	protected boolean whiteKingSideCastle = true;
	protected boolean whiteQueenSideCastle = true;
	protected boolean blackKingSideCastle = true;
	protected boolean blackQueenSideCastle = true;

	private int fullMoveNumber = 1;

	public int enPassantSquare = -1;

	private GameOverPanel gameOverPanel;
	private TimerPanel timerPanel;
	private Movements lastMove;


	public Board() {
		System.out.println("Board instance created: " + boardId);
		this.setOpaque(true);
		this.setBackground(Color.RED);
		this.setPreferredSize(new Dimension(cols * SQUARE_SIZE, rows * SQUARE_SIZE));
		this.setLayout(null); // No layout manager
		this.addMouseListener(input);
		this.addMouseMotionListener(input);
		this.setOpaque(false);
		timerPanel = new TimerPanel(duration);
		timerPanel.setBounds(700, 50, 160, 100);
		this.add(timerPanel);

		whiteTimer = new ChessTimer(this, timerPanel, true, duration);
		blackTimer = new ChessTimer(this, timerPanel, false, duration);

		addPieces(initFEN);
		


		gameOverPanel = new GameOverPanel(this, "");
		gameOverPanel.setBounds(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE);
		gameOverPanel.setVisible(false);
		this.add(gameOverPanel);
		this.setComponentZOrder(gameOverPanel, 0);

	}

	public Board deepCopy() {
		Board copy = new Board();
		copy.isGameOver = this.isGameOver;
		copy.isPromoting = this.isPromoting;
		copy.isWhiteToMove = this.isWhiteToMove;
		copy.fullMoveNumber = this.fullMoveNumber;
		copy.enPassantSquare = this.enPassantSquare;
		copy.whiteKingSideCastle = this.whiteKingSideCastle;
		copy.whiteQueenSideCastle = this.whiteQueenSideCastle;
		copy.blackKingSideCastle = this.blackKingSideCastle;
		copy.blackQueenSideCastle = this.blackQueenSideCastle;
		copy.isBotPlaying = this.isBotPlaying;
		copy.pieceList = new ArrayList<>();
		for (Piece piece : this.pieceList) {
			copy.pieceList.add(piece.deepCopy(copy));
		}

		copy.checkFinder = this.checkFinder.deepCopy(copy);

		return copy;
	}

	public static void setDuration(int duration) {
		Board.duration = duration;
	}
	public Stack<String> getMoveHistory() {
		return moveHistory;
	}
	
	public void setIsResign(boolean b) {
		isResign = b;
	}
	public void setIsDraw(boolean b) {
		isDraw = b;
	}

	public void enableBot(boolean isBotPlaying, boolean isHardMode, boolean isWhiteBot) {
        this.isBotPlaying = isBotPlaying;
        this.isWhiteBot = isWhiteBot;
        System.out.println("enableBot called: isBotPlaying = " + this.isBotPlaying); // Debug log
        if (isHardMode) {
            this.bot = new HardBot(this, true, isWhiteBot);
        } else {
            this.bot = new EasyBot(this, false, isWhiteBot);
        }
    }

	public Piece getPiece(int col, int row) {
		for (Piece p : pieceList) {
			if (p.col == col && p.row == row) {
				return p;
			}
		}

		return null;
	}

	public boolean hasPiece(int col, int row) {
		return getPiece(col, row) != null;
	}
	
	public void setLastMove(Movements move) {
		this.lastMove = move;
	}

	public void makeBotMove() {
		if (bot == null || isGameOver)
			return;
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				if (!isPromoting) {

					Movements virtualMove = bot.getMove(); // Get bot move (from virtual board)
					System.out.printf("MOVE: %s (%d, %d) -> (%d, %d) \n", virtualMove.piece.type, virtualMove.oldCol,
							virtualMove.oldRow, virtualMove.newCol, virtualMove.newRow);
					Piece actualPiece = null;
					for (Piece p : pieceList) { // Loop through actual board pieces
						if (p.isWhite == bot.isWhiteBot) {
							if (p.type == virtualMove.piece.type
									&& p.col == virtualMove.oldCol
									&& p.row == virtualMove.oldRow) {
								actualPiece = p; // Found matching piece
							}
						}
					}
					// Find the actual piece in the real board

					if (actualPiece == null)
						return; // Safety check

					// Create an actual move using the correct piece
					Movements actualMove = new Movements(Board.this, actualPiece, virtualMove.newCol,
							virtualMove.newRow);

					// Execute move
					makeMove(actualMove);
				}
			}
		}, 300); // Add delay for realism
	}

	public void makeMove(Movements move) {

        System.out.println("makeMove called: isBotPlaying = " + isBotPlaying); // Debug log
        if (bot == null && isBotPlaying) {
            System.out.println("Bot is not initialized: cannot make a move.");
            return; // Prevent null pointer exception
        }
		setLastMove(move);

		if (move.piece.type == Type.PAWN) {
			movePawn(move);
		} else if (move.piece.type == Type.KING) {
			moveKing(move);
		}
		
		move.piece.col = move.newCol;
		move.piece.row = move.newRow;
		move.piece.xPos = move.newCol * SQUARE_SIZE;
		move.piece.yPos = move.newRow * SQUARE_SIZE;

		move.piece.isFirstMove = false;

		capture(move.capture);

		checkFinder.updatehalfMoveClock(move);

		if (!firstMoveMade) {
			firstMoveMade = true;
			whiteTimer.stop();
			blackTimer.start(); // Start Black's timer after White moves
		} else {
			// Normal timer switching after the first move
			if (isWhiteToMove) {
				whiteTimer.stop();
				blackTimer.start();
			} else {
				blackTimer.stop();
				whiteTimer.start();
			}
		}

		isWhiteToMove = !isWhiteToMove;

		moveHistory.add(Movements.generateFEN(this));

		updateGame();
		SoundManager.playSound("move-self");
		selectedPiece = null;
		validMoves.clear();
		repaint();

		if (isBotPlaying && isWhiteToMove == bot.isWhiteBot) {
			makeBotMove();
		}

	}

	public void undoMove() {
		if (!moveHistory.isEmpty()) {

			// Restore the previous board state using the previous FEN
			String previousFEN = moveHistory.isEmpty() ? initFEN : moveHistory.peek();
			addPieces(previousFEN);

			// Toggle the turn
			isWhiteToMove = !isWhiteToMove;

			// Update the game state
			updateGame();
			selectedPiece = null;
			validMoves.clear();

			// Handle bot move if bot is playing
			if (isBotPlaying && isWhiteToMove == bot.isWhiteBot) {
				makeBotMove();
			}
		}
	}

	

	public Movements getLastMove() {
		return lastMove;
	}

	public void updateFullMoveNumber(boolean isWhite) {
		if (!isWhite) { // Increment only after Black moves
			fullMoveNumber++;
		}
	}

	public int getFullmoveNumber() {
		return fullMoveNumber;
	}

	public void moveKing(Movements move) {
		if (Math.abs(move.piece.col - move.newCol) == 2) {
			Piece rook;
			if (move.piece.col < move.newCol) {
				rook = getPiece(7, move.piece.row);
				rook.col = 5;
				whiteKingSideCastle = false;
				blackKingSideCastle = false;
			} else {
				rook = getPiece(0, move.piece.row);
				rook.col = 3;
				whiteQueenSideCastle = false;
				blackQueenSideCastle = false;
			}
			rook.xPos = rook.col * SQUARE_SIZE;
		}

		// King has moved, disable castling for this color
		if (move.piece.isWhite) {
			whiteKingSideCastle = false;
			whiteQueenSideCastle = false;
		} else {
			blackKingSideCastle = false;
			blackQueenSideCastle = false;
		}

	}

	public void movePawn(Movements move) {
		// enpassant
		int colorIdx = move.piece.isWhite ? 1 : -1;

		if (getSquareNum(move.newCol, move.newRow) == enPassantSquare) {
			SoundManager.playSound("move-self");
			move.capture = getPiece(move.newCol, move.newRow + colorIdx);

		}

		if (Math.abs(move.piece.row - move.newRow) == 2) {
			enPassantSquare = getSquareNum(move.newCol, move.newRow + colorIdx);
		} else {
			enPassantSquare = -1;
		}

		// promotion
		// white == 0
		// black == 7
		colorIdx = move.piece.isWhite ? 0 : 7;
		if (move.newRow == colorIdx && move.piece.type == Type.PAWN) { // Add a check for pawn type
		    promotion(move);
		    return;
		}

		move.piece.col = move.newCol;
		move.piece.row = move.newRow;
		move.piece.xPos = move.newCol * SQUARE_SIZE;
		move.piece.yPos = move.newRow * SQUARE_SIZE;

		move.piece.isFirstMove = false;

		capture(move.capture);

		selectedPiece = null;
		validMoves.clear();
		repaint();
	}

	private void promotion(Movements move) {
	    isPromoting = true;
        System.out.println("promotion called: isBotPlaying = " + isBotPlaying); // Debug log

	    // Check if the move is being made by the bot
	    if (isBotPlaying && isWhiteBot == isWhiteToMove) {    
	        // Automatically promote to Queen for the bot
	        Piece newPiece = new Queen(this, move.piece.isWhite, move.newCol, move.newRow);

	        pieceList.remove(move.piece);
	        pieceList.add(newPiece);
	        capture(move.capture);
	        isPromoting = false;

	        // Continue with the bot's move
	        updateGame();
	        repaint();
	    } else {
	        // Human player promotion logic
	        SwingUtilities.invokeLater(() -> {
	            PromotionPanel panel = new PromotionPanel((JFrame) SwingUtilities.getWindowAncestor(this),
	                    move.piece.isWhite);
	            String choice = panel.getSelectedPiece();

	            Piece newPiece;
	            switch (choice) {
	                case "Rook":
	                    newPiece = new Rook(this, move.piece.isWhite, move.newCol, move.newRow);
	                    break;
	                case "Bishop":
	                    newPiece = new Bishop(this, move.piece.isWhite, move.newCol, move.newRow);
	                    break;
	                case "Knight":
	                    newPiece = new Knight(this, move.piece.isWhite, move.newCol, move.newRow);
	                    break;
	                default:
	                    newPiece = new Queen(this, move.piece.isWhite, move.newCol, move.newRow);
	                    break;
	            }

	            pieceList.remove(move.piece);
	            pieceList.add(newPiece);
	            capture(move.capture);
	            isPromoting = false;
	            makeBotMove();
	            updateGame();
	            repaint();
	        });
	    }
	}

	public void capture(Piece piece) {
		if (piece != null) {
            SoundManager.playSound("capture"); // Play capture sound
            pieceList.remove(piece);
        }
	}

	public boolean isSameTeam(Piece p1, Piece p2) {
		if (p1 == null || p2 == null) {
			return false;
		}
		return p1.isWhite == p2.isWhite;
	}

	Piece findKing(boolean isWhite) {
		for (Piece p : pieceList) {
			if (isWhite == p.isWhite && p.type == Type.KING) {
				return p;
			}
		}
		return null;
	}

	public boolean isValidMove(Movements move) {
		// Check 1: Game state and turn
		if (isGameOver || move.piece.isWhite != isWhiteToMove) {
			return false;
		}

		// Check 2: Same team capture check
		if (hasPiece(move.newCol, move.newRow) && isSameTeam(move.piece, getPiece(move.newCol, move.newRow))) {
			return false;
		}

		// Check 3: Valid movement pattern and collision check
		if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
			return false;
		}
		if (move.piece.isCollide(move.newCol, move.newRow)) {
			return false;
		}
		
		// Check 4: King in check after move
		return !checkFinder.isKingInCheck(move);
	}

	public int getSquareNum(int col, int row) {
		return row * rows + col;
	}

	public void addPieces(String boardFen) {
		pieceList.clear(); // Clear any existing pieces

		String fen = boardFen;
		String[] fenParts = fen.split(" ");
		String boardSetup = fenParts[0]; // The first part of FEN represents the board layout

		String[] rows = boardSetup.split("/");
		for (int rank = 0; rank < 8; rank++) {
			int file = 0; // Column counter
			for (char c : rows[rank].toCharArray()) {
				if (Character.isDigit(c)) {
					file += Character.getNumericValue(c); // Skip empty squares
				} else {
					boolean isWhite = Character.isUpperCase(c);
					switch (Character.toLowerCase(c)) {
						case 'p' -> pieceList.add(new Pawn(this, isWhite, file, rank));
						case 'r' -> pieceList.add(new Rook(this, isWhite, file, rank));
						case 'n' -> pieceList.add(new Knight(this, isWhite, file, rank));
						case 'b' -> pieceList.add(new Bishop(this, isWhite, file, rank));
						case 'q' -> pieceList.add(new Queen(this, isWhite, file, rank));
						case 'k' -> pieceList.add(new King(this, isWhite, file, rank));
					}
					file++; // Move to the next square
				}
			}
		}
	}

	public void updateGame() {
		Piece king = findKing(isWhiteToMove);
		
		
		if (isResign) {
			String result = isWhiteToMove ? "Black" : "White";
			endGame(result + " Wins!", "Opponent resigned");
		}
		
		if (isDraw) {
			endGame("Stalemate!", "Opponent approved");
		}
		
		checkFinder.recordPosition(checkFinder.getBoardState(this));
		if (checkFinder.isCheckmate(king)) {
			if (checkFinder.isKingInCheck(new Movements(this, king, king.col, king.row))) {
				String result = isWhiteToMove ? "Black" : "White";
				endGame(result + " Wins!", "Checkmate");
			}
		}

		if (checkFinder.isTimeout(isWhiteToMove)) {
			String result = isWhiteToMove ? "Black" : "White";
			endGame(result + " Wins!", "Timeout");
		}

		if (checkFinder.isInsufficientMaterial()) {
			endGame("Stalemate!", "Insufficient Material");
		}

		if (checkFinder.isThreefoldRepetition()) {
			endGame("Stalemate!", "Threefold Repetition");
		}

		if (checkFinder.isFiftyMoveRule()) {
			endGame("Stalemate!", "50 Move Rule");
		}

		if (checkFinder.isStalemate(king) && !isResign && !isDraw) {
			if (!checkFinder.isKingInCheck(new Movements(this, king, king.col, king.row))) {
				endGame("Stalemate!", "No legal move");
			}
		}

	}

	private void endGame(String message, String reason) {
		isGameOver = true;
		whiteTimer.stop();
		blackTimer.stop();
		input.disableMouseInput();
		gameOverPanel.showResult(message, reason);
		return;
	}

	public void resetBoard() {
        System.out.println("resetBoard called: isBotPlaying = " + isBotPlaying); // Debug log

		pieceList.clear();
		addPieces(initFEN);
		selectedPiece = null;
		validMoves.clear();
		enPassantSquare = -1;
		checkFinder.positionHistory.clear();
		isWhiteToMove = true;
		isGameOver = false;
		isResign = false;
		isDraw = false;

		whiteTimer.reset(10); // Reset White's timer
		blackTimer.reset(10); // Reset Black's timer

		input.enableMouseInput();
		gameOverPanel.reset();
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Enable anti-aliasing for smoother rendering
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(50, 50, 50));
		g2d.fillRoundRect(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE, BOARD_RADIUS, BOARD_RADIUS);

		// Clip the drawing area to the rounded rectangle to prevent overflow
		g2d.setClip(
				new RoundRectangle2D.Float(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE, BOARD_RADIUS, BOARD_RADIUS));

		// draw the chess board
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				g2d.setColor((i + j) % 2 == 0 ? primary : secondary);
				g2d.fillRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
		// when piece is selected
		if (selectedPiece != null) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					// highlight selected piece
					g2d.setColor(new Color(255, 255, 51, 10));
					g2d.fillRect(selectedPiece.col * SQUARE_SIZE, selectedPiece.row * SQUARE_SIZE, SQUARE_SIZE,
							SQUARE_SIZE);

					// show the valid move of selected piece
					if (isValidMove(new Movements(this, selectedPiece, j, i))) {
						g2d.setColor(new Color(0, 0, 0, 50));
						g2d.fillOval(j * SQUARE_SIZE + SQUARE_SIZE / 3, i * SQUARE_SIZE + SQUARE_SIZE / 3,
								SQUARE_SIZE / 3, SQUARE_SIZE / 3);
					}
				}
			}
		}

		for (Piece p : pieceList) {
			p.draw(g2d);
		}

		g2d.setClip(null);
	}



}
