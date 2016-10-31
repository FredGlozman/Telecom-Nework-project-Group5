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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

public class InsultCanvas extends Canvas {
	
	protected static final int CURSOR_FRAME_COUNT = 15;
	
	// Margins
	protected static final double INSULT_LEFT_MARGIN = 0.05; // Percentage of window width
	protected static final double VERTICAL_SPACING = 0.05; // Percentage of window height
	protected static final double TOP_MARGIN = 0.15;  // Percentage of window height
	protected static final double BOTTOM_MARGIN = 0.1; // Percentage of window height
	protected static final double RIGHT_MARGIN = 0.1; // Percentage of window height

	// Background
	protected static final Color BACKGROUND_TOP_COLOR = GameCanvas.BACKGROUND_TOP_COLOR;
	protected static final Color BACKGROUND_BOTTOM_COLOR = GameCanvas.BACKGROUND_BOTTOM_COLOR;
	protected static final Paint BACKGROUND_GRADIENT = new GradientPaint(0, 0, BACKGROUND_TOP_COLOR, 0, WindowFrame.HEIGHT, BACKGROUND_BOTTOM_COLOR);
	
	// Header
	protected static final Color HEADER_COLOR = GameCanvas.GRID_COLOR;
	protected static final int HEADER_FONT_SIZE = 60;
	protected static final int ADJUSTED_HEADER_FONT_SIZE = adjustWidth(HEADER_FONT_SIZE);
	protected static final Font HEADER_FONT = new Font("SansSerif", Font.PLAIN, ADJUSTED_HEADER_FONT_SIZE);
	
	// Insult
	protected static final Color INSULT_COLOR = GameCanvas.GRID_BORDER_COLOR;
	protected static final int INSULT_FONT_SIZE = 60;
	protected static final int ADJUSTED_INSULT_FONT_SIZE = adjustWidth(INSULT_FONT_SIZE);
	protected static final Font INSULT_FONT = new Font("SansSerif", Font.PLAIN, ADJUSTED_INSULT_FONT_SIZE);
	
	// Hint
	protected static final String HINT_TEXT = "Hit Enter to send";
	protected static final Color HINT_COLOR = GameCanvas.ARROW_COLOR;
	protected static final int HINT_FONT_SIZE = 40;
	protected static final int ADJUSTED_HINT_FONT_SIZE = adjustWidth(HINT_FONT_SIZE);
	protected static final Font HINT_FONT = new Font("SansSerif", Font.BOLD, ADJUSTED_HINT_FONT_SIZE);
	
	// Timer
	protected static final int TIMER_DIAMETER = 75;
	protected static final int ADJUSTED_TIMER_DIAMETER = adjustWidth(TIMER_DIAMETER);
	protected static final Color TIMER_COLOR = GameCanvas.ARROW_COLOR;

	
	private static final long serialVersionUID = 7743405339333788671L;

	private Timer timer;
	
	private InsultLogic il;
	
	private boolean winner;
	
	private int frame;
	private boolean showCursor;
	
	private int timeLeft;
	
	public InsultCanvas(InsultLogic il) {
		super();
		this.timer = new Timer(1000 / FPS, this);
		this.frame = 0;
		this.showCursor = false;
		this.il = il;
		this.winner = this.il.isWinner();
		timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBackground(g2);
		showHeader(g2);
		displayInsult(g2);
		if (this.winner) {
			showHint(g2);
			drawTimeLeft(g2);
		} else if (this.il.getPhase() == 2) {
			drawTimeLeft(g2);
		}
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setPaint(BACKGROUND_GRADIENT);
		Rectangle bg = new Rectangle(0, 0, WindowFrame.WIDTH, WindowFrame.HEIGHT);
		g2.fill(bg);
	}

	private void showHeader(Graphics2D g2) {
		String str = this.il.getHeaderText();
		g2.setPaint(HEADER_COLOR);
		g2.setFont(HEADER_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (WindowFrame.WIDTH - fm.stringWidth(str)) / 2;
		int y = (int) (fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2 - VERTICAL_SPACING * WindowFrame.HEIGHT / 2);
		g2.drawString(str, x, y);
	}
	
	private void displayInsult(Graphics2D g2) {
		String str = this.il.getInsult();
		g2.setPaint(INSULT_COLOR);
		g2.setFont(INSULT_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (WindowFrame.WIDTH - fm.stringWidth(str)) / 2;
		int y = (int) (fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2 + VERTICAL_SPACING * WindowFrame.HEIGHT / 2);
		g2.drawString(str + ((!this.il.insultAtMaxLength() && this.winner && this.showCursor) ? "_" : ""), x, y);
	}
	
	private void showHint(Graphics2D g2) {
		g2.setPaint(HINT_COLOR);
		g2.setFont(HINT_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (WindowFrame.WIDTH - fm.stringWidth(HINT_TEXT)) / 2;
		int y = (int) ((1 - BOTTOM_MARGIN) * WindowFrame.HEIGHT);
		g2.drawString(HINT_TEXT, x, y);
		
	}
	
	private void drawTimeLeft(Graphics2D g2) {
		double ratioOfTimeLeft = (double) this.timeLeft / this.il.getTimeSet();
		int x = (int) ((1 - RIGHT_MARGIN) * WindowFrame.WIDTH) - ADJUSTED_TIMER_DIAMETER;
		int y = (int) (TOP_MARGIN * WindowFrame.HEIGHT) - ADJUSTED_TIMER_DIAMETER;
				
		g2.setPaint(TIMER_COLOR);
		g2.fillArc(x, y, ADJUSTED_TIMER_DIAMETER, ADJUSTED_TIMER_DIAMETER, 90, (int) -(ratioOfTimeLeft * 360));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (++frame == CURSOR_FRAME_COUNT) {
			frame = 0;
			showCursor = !showCursor;
		}
		
		this.timeLeft = this.il.updateTimer();
		
		repaint();		
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!this.winner)
			return;
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_ALT:
			case KeyEvent.VK_DELETE:
			case KeyEvent.VK_UNDEFINED:
				break;
			case KeyEvent.VK_BACK_SPACE:
				this.il.popFromInsult();
				break;
			case KeyEvent.VK_ESCAPE:
				this.il.clearInsult();
				break;
			case KeyEvent.VK_ENTER:
				this.il.sendInsult();
				break;
			default:
				char c;
				if ((c = e.getKeyChar()) != 65535)
					this.il.appendToInsult(c);				
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	public void stop() {
		this.timer.stop();
	}
	
	public void resume() {
		this.timer.start();
	}
	
	@Override
	public void cleanUp() {
		super.cleanUp();
		stop();
	}
}
