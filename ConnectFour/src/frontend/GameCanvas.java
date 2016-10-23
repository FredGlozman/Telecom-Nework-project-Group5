package frontend;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

public class GameCanvas extends Canvas {

	private static final long serialVersionUID = -7366085140697800431L;
	
	// Animation speed
	protected static final int ANIMATION_SPEED = 80;
	protected static final int ADJUSTED_ANIMATION_SPEED = (ANIMATION_SPEED * WindowFrame.HEIGHT) / WindowFrame.DEFAULT_WIDTH_HEIGHT;
	
	// Positioning
	// Grid
	protected static final double LEFT_RIGHT_PADDING = 0.02; // percentage of window (e.g. if 0.1, then 10% of the window to the left, and 10% to the right will be left blank)
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
	
	// Colors
	protected static final Color GRID_COLOR = new Color(0.65f, 0.75f, 0.95f);
	protected static final Color GRID_BORDER_COLOR = new Color(0.9f, 0.95f, 1.0f);
	protected static final Color BACKGROUND_TOP_COLOR = new Color(0.2f, 0.18f, 0.18f);
	protected static final Color BACKGROUND_BOTTOM_COLOR = new Color(0.25f, 0.22f, 0.22f);
	protected static final Paint BACKGROUND_GRADIENT = new GradientPaint(0, 0, BACKGROUND_TOP_COLOR, 0, WindowFrame.HEIGHT, BACKGROUND_BOTTOM_COLOR);
	protected static final Color TOKEN1_COLOR = Color.RED;
	protected static final Color TOKEN2_COLOR = Color.YELLOW;
	protected static final Color ARROW_COLOR = new Color(0.5f, 0.5f, 0.5f);
	
	// Misc
	protected static final int GRID_STROKE_THICKNESS = 6;
	protected static final int ADJUSTED_GRID_STROKE_THICKNESS = (GRID_STROKE_THICKNESS * WindowFrame.WIDTH) / WindowFrame.DEFAULT_WIDTH_HEIGHT;
	protected static final BasicStroke GRID_STROKE = new BasicStroke(ADJUSTED_GRID_STROKE_THICKNESS);
	
	//Strings
	protected static final String WINNING_STRING = "YOU WIN!";
	protected static final String DRAW_STRING = "DRAW!";
	protected static final String LOSING_STRING = "YOU LOSE!";
	protected static final int FONT_SIZE = 200;
	protected static final int ADJUSTED_FONT_SIZE = (FONT_SIZE * WindowFrame.WIDTH) / WindowFrame.DEFAULT_WIDTH_HEIGHT;
	protected static final Font FONT = new Font("SansSerif", Font.BOLD, ADJUSTED_FONT_SIZE);
	
	private Timer timer;
	
	private GameLogic gl;
	private Area grid;
	private int[][] positions;
	
	private int selectedColumn;
	
	private FallingToken fallingToken;
	
	public GameCanvas(GameLogic gl) {
		this.selectedColumn = -1;
		this.gl = gl;
		this.positions = this.gl.getPositions();
		this.timer = new Timer(1000 / FPS, this);
		createGrid();
		addMouseMotionListener(this);
		addMouseListener(this);
		this.timer.start();
	}
	
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBackground(g2);
		drawTokens(g2);
		drawFallingToken(g2);
		drawGrid(g2);
		if (gl.getWinner() == 0)
			drawArrowIndicator(g2);
		else
			drawWinLose(g2);
	}

	private void drawBackground(Graphics2D g2) {
		g2.setPaint(BACKGROUND_GRADIENT);
		Rectangle bg = new Rectangle(0, 0, WindowFrame.WIDTH, WindowFrame.HEIGHT);
		g2.fill(bg);
	}

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
	
	private void drawFallingToken(Graphics2D g2) {
		if (this.fallingToken == null)
			return;
		
		g2.setPaint(getTokenColor(this.fallingToken.getType()));
		g2.fill(this.fallingToken.getRepresentation());
	}

	private void drawGrid(Graphics2D g2) {
		g2.setPaint(GRID_COLOR);
		g2.fill(this.grid);
		g2.setStroke(GRID_STROKE);
		g2.setPaint(GRID_BORDER_COLOR);
		g2.draw(this.grid);
	}
	
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
	
	private void drawWinLose(Graphics2D g2) {
		String str;		
		if (this.gl.isWinner())
			str = WINNING_STRING;
		else if (this.gl.isDraw())
			str = DRAW_STRING;
		else
			str = LOSING_STRING;
		
		g2.setPaint(BACKGROUND_GRADIENT);
		g2.setFont(FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (WindowFrame.WIDTH - fm.stringWidth(str)) / 2;
		int y = fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2;
		g2.drawString(str, x, y);
	}

	protected static Color getTokenColor(int type) {
		switch (type) {
			case 0:
				return null;
			case 1:
				return TOKEN1_COLOR;
			case 2:
				return TOKEN2_COLOR;
			default:
				return null; // error
		}
	}

	protected static Ellipse2D.Double getEllipseAtCoords(int i, int j) {
		return new Ellipse2D.Double(
				GRID_LEFT + i * COLUMN_WIDTH + HOLE_DIAMETER * HOLE_MARGIN,
				GRID_TOP + j * ROW_HEIGHT + HOLE_DIAMETER * HOLE_MARGIN,
				HOLE_DIAMETER,
				HOLE_DIAMETER);
	}

	public void drop(int column, int row, int type) {
		this.fallingToken = new FallingToken(column, row, type);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if (gl.getWinner() != 0) {
			gl.rematch();
			return;
		}
		
		if (this.gl.isUserTurn() && this.fallingToken == null && this.selectedColumn != -1)
			gl.placeToken(selectedColumn);
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.gl.getWinner() != 0) {
			timer.stop();
		} else if (this.fallingToken != null && this.fallingToken.update()) {
			this.gl.insertToken(this.fallingToken.getColumn(), this.fallingToken.getRow(), this.fallingToken.getType());
			this.fallingToken = null;
		}
		
		repaint();
	}
	
}
