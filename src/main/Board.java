package main;
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
import utils.StyledButton;

public class Board extends JPanel {
	public static final int SQUARE_SIZE = 80;
	public static final int BOARD_RADIUS = 40;
	int cols = 8;
	int rows = 8;
	public String initFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	public ArrayList<Piece> pieceList = new ArrayList<>();
		
	public Piece selectedPiece;
	
	private Stack<String> moveHistory = new Stack<>();
	
	ArrayList<Point> validMoves = new ArrayList<>();
	
	public MouseInput input = new MouseInput(this);
	
	public CheckFinder checkFinder = new CheckFinder(this);
	
	
	public ChessTimer whiteTimer;
	public ChessTimer blackTimer;
	
	//state
	protected boolean isWhiteToMove = true;
	
	private Bot bot;
    private boolean isBotPlaying = false;
    private boolean isHardMode = false;
    private boolean botMakingMove = false;
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
	
	private StyledButton homeButton;
	private StyledButton exitButton;
	public Board() {
		this.setOpaque(true);
		this.setBackground(Color.RED);
		this.setPreferredSize(new Dimension(cols * SQUARE_SIZE, rows * SQUARE_SIZE));
	    this.setLayout(null);  // No layout manager
	    this.addMouseListener(input);
	    this.addMouseMotionListener(input);
	    this.setOpaque(false);
	    timerPanel = new TimerPanel();
	    timerPanel.setBounds(720, 50, 160, 100);
	    this.add(timerPanel);
	    
	    whiteTimer = new ChessTimer(this,timerPanel, true, 1);
	    blackTimer = new ChessTimer(this,timerPanel, false, 1);
		
	    addPieces(initFEN);
	    
        homeButton = new StyledButton("Return to Menu", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
        exitButton = new StyledButton("Exit", new Color(60, 60, 60), new Color(80, 80, 80), Color.WHITE);
	    
	    homeButton.setBounds(720, 200, 160, 40);
        exitButton.setBounds(720, 250, 160, 40);
	    
        homeButton.addActionListener(_ -> {
            System.out.println("Return to Menu Clicked!");
            Main.showMenu();
        });
        exitButton.addActionListener(_ -> {
            System.out.println("Exit Clicked!");
            System.exit(0);
        });
        
        this.add(homeButton);
        this.add(exitButton);
        
	    
        
	    gameOverPanel = new GameOverPanel(this,"");
	    gameOverPanel.setBounds(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE);
        gameOverPanel.setVisible(false); 
        this.add(gameOverPanel);
        this.setComponentZOrder(gameOverPanel, 0);
        
        
        
	}
	
	
	public void enableBot(boolean isBotPlaying, boolean isHardMode, boolean isWhiteBot) {
	    this.isBotPlaying = true;
	    if (isHardMode) {
	        this.bot = new HardBot(this, true, isWhiteBot);
	    } else {
	        this.bot = new EasyBot(this, false, isWhiteBot);
	    }
	}
	
	
	public Piece getPiece(int col,int row) {
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

	public void makeBotMove() {
        if (bot == null || isGameOver) return;
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
            	if (!isPromoting) {
            		Movements move = bot.getMove();
                    if (move != null) {
                        makeMove(move);
                    }
            	}
            }
        }, 500); // Add delay for realism
    }
	
	public void makeMove(Movements move) {
		if (isGameOver || isPromoting) return;
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
	        blackTimer.start();  // Start Black's timer after White moves
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
        checkFinder.recordPosition(Movements.generateFEN(this));
        
        
        updateGame();
        
        selectedPiece = null;
        validMoves.clear();
        repaint();
        
        if (isBotPlaying && isWhiteToMove == bot.isWhiteBot) {
        	makeBotMove();
        }
        
	}
	
	public void undoMove() {
	    if (!moveHistory.isEmpty()) {
	        String lastFEN = moveHistory.pop();
	        checkFinder.removePosition(lastFEN);

	        // Restore the previous board state
	        String previousFEN = moveHistory.isEmpty() ? initFEN : moveHistory.peek();
	        addPieces(previousFEN);

	        if (lastMove != null) {
	            // Restore captured piece (if any)
	            if (lastMove.capture != null) {
	                pieceList.add(lastMove.capture);
	            }

	            // Restore special moves
	            if (lastMove.piece.type == Type.KING && Math.abs(lastMove.oldCol - lastMove.newCol) == 2) {
	                // Undo castling
	                if (lastMove.newCol == 6) { // King-side castle
	                    Piece rook = getPiece(5, lastMove.piece.row);
	                    if (rook != null) rook.col = 7;
	                } else if (lastMove.newCol == 2) { // Queen-side castle
	                    Piece rook = getPiece(3, lastMove.piece.row);
	                    if (rook != null) rook.col = 0;
	                }
	            }

	            if (lastMove.piece.type == Type.PAWN && lastMove.newCol != lastMove.oldCol && lastMove.capture == null) {
	                // Undo en passant
	                int rowOffset = lastMove.piece.isWhite ? -1 : 1;
	                Piece enPassantPawn = new Pawn(this, !lastMove.piece.isWhite, lastMove.newCol, lastMove.newRow + rowOffset);
	                pieceList.add(enPassantPawn);
	            }
	        }

	        isWhiteToMove = !isWhiteToMove;
	        updateGame();
	        selectedPiece = null;
	        validMoves.clear();
	        repaint();
	    }
	}
	

	
	public void setLastMove(Movements move) {
	    this.lastMove = move;
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
				rook = getPiece(7,move.piece.row);
				rook.col = 5;
				whiteKingSideCastle = false;
	            blackKingSideCastle = false;
			} else {
				rook = getPiece(0,move.piece.row);
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
		//enpassant
		int colorIdx = move.piece.isWhite? 1 : -1;
		
		if (getSquareNum(move.newCol,move.newRow) == enPassantSquare) {
			move.capture = getPiece(move.newCol,move.newRow + colorIdx);
			
		}
		
		if (Math.abs(move.piece.row - move.newRow) == 2) {
			enPassantSquare = getSquareNum(move.newCol,move.newRow + colorIdx);
		} else {
			enPassantSquare = -1;
		}
		
		//promotion
		//white == 0
		//black == 7
		colorIdx = move.piece.isWhite? 0:7;
		if (move.newRow == colorIdx) {
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
	    SwingUtilities.invokeLater(() -> {
	        PromotionPanel panel = new PromotionPanel((JFrame) SwingUtilities.getWindowAncestor(this), move.piece.isWhite);
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

	
	public void capture(Piece piece) {
		pieceList.remove(piece);
	}
	
	public boolean isSameTeam(Piece p1,Piece p2) {
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
		
		if (isGameOver || move.piece.isWhite != isWhiteToMove) return false;
        if (hasPiece(move.newCol, move.newRow) && isSameTeam(move.piece, getPiece(move.newCol, move.newRow))) return false;
        if (!move.piece.isValidMovement(move.newCol, move.newRow) || move.piece.isCollide(move.newCol, move.newRow)) return false;
        return !checkFinder.isKingInCheck(move);
	}
	
	public int getSquareNum(int col,int row) {
		return row*rows + col;
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
	    if (checkFinder.isCheckmate(king)) {
	    	if (checkFinder.isKingInCheck(new Movements(this,king,king.col,king.row))) {
	    		String result = isWhiteToMove ? "Black" : "White";
	    		endGame(result + " Wins!","Checkmate");
	    	} 
	    }
	    
	    if (checkFinder.isTimeout(isWhiteToMove)) {
	    	String result = isWhiteToMove ? "Black" : "White";
	    	endGame(result +" Wins!","Timeout");
	    }
	    
	    if (checkFinder.isInsufficientMaterial()) {
	    	endGame("Stalemate!","Insufficient Material");
	    }
	    
	    if (checkFinder.isThreefoldRepetition()) {
	        endGame("Stalemate!","Threefold Repetition");
	    }
	    
	    if (checkFinder.isFiftyMoveRule()) {
	    	endGame("Stalemate!","50 Move Rule");
	    }
	    
	    if (checkFinder.isStalemate(king)) {
	    	if (!checkFinder.isKingInCheck(new Movements(this,king,king.col,king.row))) {
	    		endGame("Stalemate!","No legal move");
	    	} 
	    }
	    
	    
	    
	}

	private void endGame(String message,String reason) {
	    isGameOver = true;
	    whiteTimer.stop(); 
        blackTimer.stop();
	    input.disableMouseInput();
	    gameOverPanel.showResult(message,reason);
	    return;
	}
    
    public void resetBoard() {
        pieceList.clear();
        addPieces(initFEN);
        selectedPiece = null;
        validMoves.clear();
        enPassantSquare = -1;
        isWhiteToMove = true;
        isGameOver = false;
        
        
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
        g2d.setClip(new RoundRectangle2D.Float(0, 0, cols * SQUARE_SIZE, rows * SQUARE_SIZE, BOARD_RADIUS, BOARD_RADIUS));
		
        
        //draw the chess board
		for (int i = 0;i<rows;i++) {
			for (int j = 0;j<cols;j++) {
				g2d.setColor((i+j)%2==0 ? new Color(235,236,208) : new Color(119,149,86));
				g2d.fillRect(j*SQUARE_SIZE, i*SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);
			}
		}
		//when piece is selected
		if (selectedPiece != null) {
			for (int i = 0;i<rows;i++) {
				for (int j = 0;j<cols;j++) {
					//highlight selected piece
					g2d.setColor(new Color(255, 255, 51,10));
					g2d.fillRect(selectedPiece.col * SQUARE_SIZE, selectedPiece.row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
					
					//show the valid move of selected piece
					if (isValidMove(new Movements(this,selectedPiece,j,i))) {
						g2d.setColor(new Color(0, 0, 0, 50));
						g2d.fillOval(j*SQUARE_SIZE+SQUARE_SIZE/3, i*SQUARE_SIZE+SQUARE_SIZE/3,SQUARE_SIZE/3,SQUARE_SIZE/3);						
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
