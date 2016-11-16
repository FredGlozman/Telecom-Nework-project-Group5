package frontend;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

/**
 * Game view: displays the grid, the falling token, an arrow indicator, the turn timer, and potentially text indicating whether the player won or lost.
 * Detects mouse input to make plays, and is updated upon receiving network information.
 */
public class GameCanvas extends Canvas {

	private static final long serialVersionUID = -7366085140697800431L;
	
	// Animation speed
	protected static final int ANIMATION_SPEED = 80;
	protected static final int ADJUSTED_ANIMATION_SPEED = adjustHeight(ANIMATION_SPEED);
	
	// Positioning
	// Grid
	protected static final double TOP_PADDING = 0.03;
	protected static final double BOTTOM_PADDING = 0.028;
	protected static final double LEFT_RIGHT_MARGIN = 0.15; 
	protected static final double TOP_BOTTOM_MARGIN = -(3.0 * WindowFrame.WIDTH)/(7.0 * WindowFrame.HEIGHT) * (1 - 2 * LEFT_RIGHT_MARGIN) + 0.5; // computed to keep 7:6 ratio
	protected static final double GRID_LEFT = LEFT_RIGHT_MARGIN * WindowFrame.WIDTH;
	protected static final double GRID_TOP = TOP_BOTTOM_MARGIN * WindowFrame.HEIGHT;
	protected static final double GRID_WIDTH = (1 - 2 * LEFT_RIGHT_MARGIN) * WindowFrame.WIDTH;
	protected static final double GRID_HEIGHT = (1 - 2 * TOP_BOTTOM_MARGIN) * WindowFrame.HEIGHT;
	protected static final double COLUMN_WIDTH = GRID_WIDTH / 7;
	protected static final double ROW_HEIGHT = GRID_HEIGHT / 6;
	// Holes
	protected static final double HOLE_MARGIN = 0.1; // percentage of hole diameter in all directions (each, not cumulative)
	protected static final double HOLE_DIAMETER = (1 - 2 * LEFT_RIGHT_MARGIN) * WindowFrame.WIDTH / 7.0 * (1 - 2 * HOLE_MARGIN); //computed
	// Arrow indicator
	protected static final double ARROW_Y_POSITION = (int) ((TOP_BOTTOM_MARGIN - TOP_PADDING) * WindowFrame.HEIGHT - 2 * ROW_HEIGHT / 3); // computed
	// Timer
	protected static final double TIMER_SPACING = 0.05;
	protected static final double LEFT_RIGHT_PADDING = 0.02; // percentage of window (e.g. if 0.1, then 10% of the window to the left, and 10% to the right will be left blank)
	protected static final int TIMER_HEIGHT = 25;
	protected static final int ADJUSTED_TIMER_HEIGHT = adjustHeight(TIMER_HEIGHT);
	
	// Colors
	protected static final Color GRID_COLOR = new Color(0.65f, 0.75f, 0.95f);
	protected static final Color GRID_BORDER_COLOR = new Color(0.9f, 0.95f, 1.0f);
	protected static final Color BACKGROUND_TOP_COLOR = new Color(0.2f, 0.18f, 0.18f);
	protected static final Color BACKGROUND_BOTTOM_COLOR = new Color(0.25f, 0.22f, 0.22f);
	protected static final Paint BACKGROUND_GRADIENT = new GradientPaint(0, 0, BACKGROUND_TOP_COLOR, 0, WindowFrame.HEIGHT, BACKGROUND_BOTTOM_COLOR);
	protected static final Color TOKEN1_COLOR = Color.RED;
	protected static final Color TOKEN2_COLOR = Color.YELLOW;
	protected static final Color ARROW_COLOR = new Color(0.5f, 0.5f, 0.5f);
	protected static final Color WIN_LOSE_TEXT_BORDER_COLOR = GRID_BORDER_COLOR;
	
	// Misc
	protected static final int GRID_STROKE_THICKNESS = 6;
	protected static final int ADJUSTED_GRID_STROKE_THICKNESS = adjustWidth(GRID_STROKE_THICKNESS);
	protected static final BasicStroke GRID_STROKE = new BasicStroke(ADJUSTED_GRID_STROKE_THICKNESS);
	protected static final int WIN_LOSE_TEXT_BORDER_THICKNESS = 4;
	protected static final int ADJUSTED_WIN_LOSE_TEXT_BORDER_THICKNESS = adjustWidth(WIN_LOSE_TEXT_BORDER_THICKNESS);
	
	// Text
	protected static final int FONT_SIZE = 200;
	protected static final int ADJUSTED_FONT_SIZE = adjustWidth(FONT_SIZE);
	protected static final Font FONT = new Font("SansSerif", Font.BOLD, ADJUSTED_FONT_SIZE);
	
	private Timer timer;
	
	private int timeLeft;
	
	private GameLogic gl;
	private Area grid;
	private int[][] positions;
	
	private int selectedColumn;
	
	private Color userColor, opponentColor;
	
	private FallingToken fallingToken;
	
	/**
	 * Constructor: sets up a link with the game logic (controller), initializes the appropriate UI colors, creates the UI representation
	 * of the grid, and launches the game timer.
	 * @param gl Game controller.
	 */
	public GameCanvas(GameLogic gl) {
		super();
		this.gl = gl;
		this.positions = this.gl.getPositions();
		this.selectedColumn = -1;
		this.userColor = this.gl.getUserColor() == 1 ? TOKEN1_COLOR : TOKEN2_COLOR;
		this.opponentColor = this.gl.getOpponentColor() == 1 ? TOKEN1_COLOR : TOKEN2_COLOR;
		this.timer = new Timer(1000 / FPS, this);
		createGrid();
		this.timer.start();
	}
	
	/**
	 * Create the UI representation of the game grid, swiss cheese style - i.e. a rectangle with holes in it.
	 */
	private void createGrid() {
		Rectangle gridBackground = new Rectangle(
				(int) (GRID_LEFT - (LEFT_RIGHT_PADDING * WindowFrame.WIDTH)),
				(int) (GRID_TOP - (TOP_PADDING * WindowFrame.HEIGHT)),
				(int) (GRID_WIDTH + 2 * (LEFT_RIGHT_PADDING * WindowFrame.WIDTH)),
				(int) (GRID_HEIGHT + 2 * (BOTTOM_PADDING * WindowFrame.HEIGHT)));
		
		this.grid = new Area(gridBackground);
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				this.grid.subtract(new Area(getEllipseAtCoords(i, j)));
			}
		}
	}
	/**
	 * Update the graphics, show every component.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBackground(g2);
		drawTokens(g2);
		drawFallingToken(g2);
		drawGrid(g2);
		if (gl.getWinner() == 0) {
			drawArrowIndicator(g2);
			drawTimeLeft(g2);
		}
		else {
			drawWinLose(g2);
		}
	}

	/**
	 * Draw the background - a dark grey gradient.
	 * @param g2 Graphics.
	 */
	private void drawBackground(Graphics2D g2) {
		g2.setPaint(BACKGROUND_GRADIENT);
		Rectangle bg = new Rectangle(0, 0, WindowFrame.WIDTH, WindowFrame.HEIGHT);
		g2.fill(bg);
	}

	/**
	 * Draw all placed tokens.
	 * @param g2 Graphics.
	 */
	private void drawTokens(Graphics2D g2) {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				if (positions[i][j] == 1 || positions[i][j] == 2) {
					g2.setPaint(getTokenColor(positions[i][j]));
					g2.fill(getEllipseAtCoords(i, j));
				}
			}
		}
	}
	
	/**
	 * Draw falling token at current position if there is one.
	 * @param g2 Graphics.
	 */
	private void drawFallingToken(Graphics2D g2) {
		if (this.fallingToken == null)
			return;
		
		g2.setPaint(getTokenColor(this.fallingToken.getType()));
		g2.fill(this.fallingToken.getRepresentation());
	}

	/**
	 * Draw grid - this is done after the tokens and falling tokens so that they appear behind it.
	 * @param g2 Graphics.
	 */
	private void drawGrid(Graphics2D g2) {
		g2.setPaint(GRID_COLOR);
		g2.fill(this.grid);
		g2.setStroke(GRID_STROKE);
		g2.setPaint(GRID_BORDER_COLOR);
		g2.draw(this.grid);
	}
	
	/**
	 * Draw arrow indicator on player's turn depending on the mouse's position -
	 * this is the hint on top showing where the token will fall if the player makes the move.
	 * @param g2 Graphics.
	 */
	private void drawArrowIndicator(Graphics2D g2) {
		if (!this.gl.isUserTurn() || this.selectedColumn == -1 || this.fallingToken != null)
			return;
		
		double xPosition = LEFT_RIGHT_MARGIN * WindowFrame.WIDTH + this.selectedColumn * COLUMN_WIDTH;
		double yPosition = ARROW_Y_POSITION;
		
		Polygon arrow = new Polygon(
				new int[] {(int) xPosition, (int) (xPosition + COLUMN_WIDTH), (int) (xPosition + COLUMN_WIDTH / 2)},
				new int[] {(int) yPosition, (int) yPosition, (int) (yPosition + ROW_HEIGHT / 2)},
				3);
		
		g2.setPaint(ARROW_COLOR);
		g2.fill(arrow);
	}
	
	/**
	 * Draw the timer bar at the bottom - its colour is that of the player whose turn it is and it gradually shrinks in width.
	 * @param g2 Graphics.
	 */
	private void drawTimeLeft(Graphics2D g2) {
		double ratioOfTimeLeft = (double) this.timeLeft / (GameLogic.TURN_TIME * FPS);
		int x = (int) (LEFT_RIGHT_MARGIN * WindowFrame.WIDTH);
		int y = (int) ((1 - TOP_BOTTOM_MARGIN + TIMER_SPACING) * WindowFrame.HEIGHT);
		int width = (int) ((1 - 2 * LEFT_RIGHT_MARGIN) * WindowFrame.WIDTH);
		int height = ADJUSTED_TIMER_HEIGHT;
		
		x += (width * (1 - ratioOfTimeLeft)) / 2;
		width -= width * (1 - ratioOfTimeLeft);
		
		g2.setPaint(this.gl.isUserTurn() ? this.userColor : this.opponentColor);
		g2.fillRect(x, y, width, height);
	}
	
	/**
	 * If the game is over, draw the string telling the player if they are the victor, the loser, or in a draw.
	 * @param g2 Graphics.
	 */
	private void drawWinLose(Graphics2D g2) {
		String str;		
		if (this.gl.isWinner())
			str = GameLogic.WINNING_STRING;
		else if (this.gl.isDraw())
			str = GameLogic.DRAW_STRING;
		else
			str = GameLogic.LOSING_STRING;
		
		g2.setFont(FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (WindowFrame.WIDTH - fm.stringWidth(str)) / 2;
		int y = fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2;

		g2.setPaint(WIN_LOSE_TEXT_BORDER_COLOR);
		for (int borderLayerX = -ADJUSTED_WIN_LOSE_TEXT_BORDER_THICKNESS; borderLayerX <= ADJUSTED_WIN_LOSE_TEXT_BORDER_THICKNESS; borderLayerX++) {
			for (int borderLayerY = -ADJUSTED_WIN_LOSE_TEXT_BORDER_THICKNESS; borderLayerY <= ADJUSTED_WIN_LOSE_TEXT_BORDER_THICKNESS; borderLayerY++) {
				g2.drawString(str, x + borderLayerX, y + borderLayerY);
			}
		}
		
		g2.setPaint(BACKGROUND_GRADIENT);
		g2.drawString(str, x, y);
	}

	/**
	 * Convert an integer to a token color.
	 * @param type Integer input.
	 * @return Color of token.
	 */
	protected static Color getTokenColor(int type) {
		switch (type) {
			case 0:
				return null;
			case 1:
				return TOKEN1_COLOR;
			case 2:
				return TOKEN2_COLOR;
			default:
				throw new RuntimeException("Invalid token type.");
		}
	}

	/**
	 * Create an ellipse representation of a token given its coordinates within the grid.
	 * @param i Column index (range: 0-6 incl.)
	 * @param j Row index (range: 0-5 incl.)
	 * @return Ellipse represnetation.
	 */
	protected static Ellipse2D.Double getEllipseAtCoords(int i, int j) {
		return new Ellipse2D.Double(
				GRID_LEFT + i * COLUMN_WIDTH + HOLE_DIAMETER * HOLE_MARGIN,
				GRID_TOP + j * ROW_HEIGHT + HOLE_DIAMETER * HOLE_MARGIN,
				HOLE_DIAMETER,
				HOLE_DIAMETER);
	}

	/**
	 * Launch the falling token UI.
	 * @param column Column in which the token is being dropped.
	 * @param row Row where the token will land.
	 * @param type Color of the token.
	 */
	public void drop(int column, int row, int type) {
		this.fallingToken = new FallingToken(column, row, type);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	/**
	 * On mouse press, if the game is over, go to the insult view; else if it's the user's turn, drop the token at the mouse's position.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (gl.getWinner() != 0) {
			gl.exit();
			return;
		}
		
		if (this.gl.isUserTurn() && this.fallingToken == null && this.selectedColumn != -1)
			gl.placeToken(selectedColumn);
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	/**
	 * Update the arrow indicator's position on mouse move.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (gl.getWinner() != 0)
			return;
		
		int x = e.getX();
		this.selectedColumn = -1;
		
		int sep = (int) (LEFT_RIGHT_MARGIN * WindowFrame.WIDTH);
		
		for (int sc = -1; sc < 7; sc++, sep += COLUMN_WIDTH) {
			if (x < sep) {
				if (sc != -1 && this.positions[sc][0] != 0)
					this.selectedColumn = -1;
				else
					this.selectedColumn = sc;
				break;
			}
		}
		
	}

	/**
	 * At every frame, update the timer and all animations. If the game is over, stop the timer.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.timeLeft = this.gl.updateTimer();
		
		if (this.gl.getWinner() != 0) {
			timer.stop();
		} else if (this.fallingToken != null && this.fallingToken.update()) {
			this.gl.insertToken(this.fallingToken.getColumn(), this.fallingToken.getRow(), this.fallingToken.getType());
			this.fallingToken = null;
		}
		
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	/**
	 * Clean up the view and stop the timer.
	 */
	@Override
	public void cleanUp() {
		super.cleanUp();
		this.timer.stop();
	}
	
}
