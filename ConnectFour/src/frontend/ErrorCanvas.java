package frontend;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class ErrorCanvas extends Canvas {
	
	// Margins
	protected static final double HEADER_LEFT_MARGIN = 0.15; // Percentage of window width
	protected static final double MESSAGE_LEFT_MARGIN = 0.2; // Percentage of window width
	protected static final double VERTICAL_SPACING = 0.05; // Percentage of window height
	protected static final double BOTTOM_MARGIN = 0.1; // Percentage of window height

	// Background
	protected static final Color BACKGROUND_TOP_COLOR = GameCanvas.BACKGROUND_TOP_COLOR;
	protected static final Color BACKGROUND_BOTTOM_COLOR = GameCanvas.BACKGROUND_BOTTOM_COLOR;
	protected static final Paint BACKGROUND_GRADIENT = new GradientPaint(0, 0, BACKGROUND_TOP_COLOR, 0, WindowFrame.HEIGHT, BACKGROUND_BOTTOM_COLOR);
	
	// Header
	protected static final Color HEADER_COLOR = Color.RED;
	protected static final int HEADER_FONT_SIZE = 75;
	protected static final int ADJUSTED_HEADER_FONT_SIZE = adjustWidth(HEADER_FONT_SIZE);
	protected static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, ADJUSTED_HEADER_FONT_SIZE);
	
	// Message
	protected static final Color MESSAGE_COLOR = GameCanvas.GRID_BORDER_COLOR;
	protected static final int MESSAGE_FONT_SIZE = 60;
	protected static final int ADJUSTED_MESSAGE_FONT_SIZE = adjustWidth(MESSAGE_FONT_SIZE);
	protected static final Font MESSAGE_FONT = new Font("SansSerif", Font.PLAIN, ADJUSTED_MESSAGE_FONT_SIZE);
	
	// Hint
	protected static final String HINT_TEXT = "Click anywhere to dismiss";
	protected static final Color HINT_COLOR = GameCanvas.ARROW_COLOR;
	protected static final int HINT_FONT_SIZE = 40;
	protected static final int ADJUSTED_HINT_FONT_SIZE = adjustWidth(HINT_FONT_SIZE);
	protected static final Font HINT_FONT = new Font("SansSerif", Font.BOLD, ADJUSTED_HINT_FONT_SIZE);
	
	private static final long serialVersionUID = 7743405339333788671L;
	
	private ErrorLogic el;
	
	public ErrorCanvas(ErrorLogic el) {
		this.el = el;
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBackground(g2);
		showHeader(g2);
		showErrorMessage(g2);
		showHint(g2);
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setPaint(BACKGROUND_GRADIENT);
		Rectangle bg = new Rectangle(0, 0, WindowFrame.WIDTH, WindowFrame.HEIGHT);
		g2.fill(bg);
	}

	private void showHeader(Graphics2D g2) {
		g2.setPaint(HEADER_COLOR);
		g2.setFont(HEADER_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (int) (HEADER_LEFT_MARGIN * WindowFrame.WIDTH);
		int y = (int) (fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2 - VERTICAL_SPACING * WindowFrame.HEIGHT / 2);
		g2.drawString(this.el.getErrorHeader() + ":", x, y);
	}
	
	private void showErrorMessage(Graphics2D g2) {
		g2.setPaint(MESSAGE_COLOR);
		g2.setFont(MESSAGE_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (int) (MESSAGE_LEFT_MARGIN * WindowFrame.WIDTH);
		int y = (int) (fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2 + VERTICAL_SPACING * WindowFrame.HEIGHT / 2);
		g2.drawString(this.el.getErrorMessage(), x, y);
	}
	
	private void showHint(Graphics2D g2) {
		g2.setPaint(HINT_COLOR);
		g2.setFont(HINT_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (WindowFrame.WIDTH - fm.stringWidth(HINT_TEXT)) / 2;
		int y = (int) ((1 - BOTTOM_MARGIN) * WindowFrame.HEIGHT);
		g2.drawString(HINT_TEXT, x, y);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		this.el.dismissError();
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

}
