package frontend;

import java.awt.BasicStroke;
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
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

public class WaitCanvas extends Canvas {

	// Margins
	protected static final double LEFT_MARGIN = 0.15; // Percentage of window width
	protected static final double RIGHT_MARGIN = 0.2;
	protected static final double VERTICAL_SPACING = 0.05; // Percentage of window height

	// Background
	protected static final Color BACKGROUND_TOP_COLOR = GameCanvas.BACKGROUND_TOP_COLOR;
	protected static final Color BACKGROUND_BOTTOM_COLOR = GameCanvas.BACKGROUND_BOTTOM_COLOR;
	protected static final Paint BACKGROUND_GRADIENT = new GradientPaint(0, 0, BACKGROUND_TOP_COLOR, 0, WindowFrame.HEIGHT, BACKGROUND_BOTTOM_COLOR);
	
	// Text
	protected static final String TEXT = "Waiting for other players...";
	protected static final Color TEXT_COLOR = GameCanvas.GRID_COLOR;
	protected static final int TEXT_FONT_SIZE = 50;
	protected static final int ADJUSTED_TEXT_FONT_SIZE = adjustWidth(TEXT_FONT_SIZE);
	protected static final Font TEXT_FONT = new Font("SansSerif", Font.BOLD, ADJUSTED_TEXT_FONT_SIZE);
	
	// Time
	protected static final Color TIME_COLOR = GameCanvas.GRID_BORDER_COLOR;
	protected static final int TIME_FONT_SIZE = 100;
	protected static final int ADJUSTED_TIME_FONT_SIZE = adjustWidth(TIME_FONT_SIZE);
	protected static final Font TIME_FONT = new Font("SansSerif", Font.BOLD, ADJUSTED_TIME_FONT_SIZE);
	
	// Outer spinner
	protected static final Color OUTER_SPINNER_COLOR = GameCanvas.TOKEN1_COLOR;
	protected static final int OUTER_SPINNER_DIAMETER = 200;
	protected static final int ADJUSTED_OUTER_SPINNER_DIAMETER = adjustWidth(OUTER_SPINNER_DIAMETER);
	protected static final int OUTER_SPINNER_ARC_ANGLE = 270;
	protected static final int OUTER_SPINNER_THICKNESS = 10;
	protected static final int ADJUSTED_OUTER_SPINNER_THICKNESS = adjustWidth(OUTER_SPINNER_THICKNESS);
	protected static final BasicStroke OUTER_SPINNER_STROKE = new BasicStroke(ADJUSTED_OUTER_SPINNER_THICKNESS);
	
	// Inner spinner
	protected static final Color INNER_SPINNER_COLOR = GameCanvas.TOKEN2_COLOR;
	protected static final int INNER_SPINNER_DIAMETER = 150;
	protected static final int ADJUSTED_INNER_SPINNER_DIAMETER = adjustWidth(INNER_SPINNER_DIAMETER);
	protected static final int INNER_SPINNER_ARC_ANGLE = 270;
	protected static final int INNER_SPINNER_THICKNESS = 10;
	protected static final int ADJUSTED_INNER_SPINNER_THICKNESS = adjustWidth(INNER_SPINNER_THICKNESS);
	protected static final BasicStroke INNER_SPINNER_STROKE = new BasicStroke(ADJUSTED_INNER_SPINNER_THICKNESS);

	
	private Timer timer;
	private int secondsWaited, fractionsOfASecondWaited;
	
	@SuppressWarnings("unused")
	private WaitLogic wl;
	
	private static final long serialVersionUID = 4562861486690573984L;
	
	public WaitCanvas(WaitLogic wl) {
		super();
		this.secondsWaited = 0;
		this.fractionsOfASecondWaited = 0;
		this.timer = new Timer(1000 / FPS, this);
		this.wl = wl;
		timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBackground(g2);
		showText(g2);
		showTime(g2);
		showOuterSpinner(g2);
		showInnerSpinner(g2);
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setPaint(BACKGROUND_GRADIENT);
		Rectangle bg = new Rectangle(0, 0, WindowFrame.WIDTH, WindowFrame.HEIGHT);
		g2.fill(bg);
	}

	private void showText(Graphics2D g2) {
		g2.setPaint(TEXT_COLOR);
		g2.setFont(TEXT_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (int) (LEFT_MARGIN * WindowFrame.WIDTH);
		int y = (int) (fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2 - VERTICAL_SPACING * WindowFrame.HEIGHT / 2);
		g2.drawString(TEXT, x, y);
	}
	
	private void showTime(Graphics2D g2) {
		String str = formatTime(this.secondsWaited);
		g2.setPaint(TIME_COLOR);
		g2.setFont(TIME_FONT);
		FontMetrics fm = g2.getFontMetrics();
		int x = (int) (LEFT_MARGIN * WindowFrame.WIDTH);
		int y = (int) (fm.getAscent() + (WindowFrame.HEIGHT - (fm.getAscent() + fm.getDescent())) / 2 + VERTICAL_SPACING * WindowFrame.HEIGHT / 2);
		g2.drawString(str, x, y);
	}
	
	private void showOuterSpinner(Graphics2D g2) {
		g2.setPaint(OUTER_SPINNER_COLOR);
		g2.setStroke(OUTER_SPINNER_STROKE);
		int x = (int) ((1 - RIGHT_MARGIN) * WindowFrame.WIDTH - ADJUSTED_OUTER_SPINNER_DIAMETER);
		int y = (WindowFrame.HEIGHT - ADJUSTED_OUTER_SPINNER_DIAMETER) / 2;
		g2.drawArc(x, y, ADJUSTED_OUTER_SPINNER_DIAMETER, ADJUSTED_OUTER_SPINNER_DIAMETER, -this.fractionsOfASecondWaited * 360 / FPS, OUTER_SPINNER_ARC_ANGLE);
	}
	
	private void showInnerSpinner(Graphics2D g2) {
		g2.setPaint(INNER_SPINNER_COLOR);
		g2.setStroke(INNER_SPINNER_STROKE);
		int x = (int) ((1 - RIGHT_MARGIN) * WindowFrame.WIDTH - ADJUSTED_INNER_SPINNER_DIAMETER - (ADJUSTED_OUTER_SPINNER_DIAMETER - ADJUSTED_INNER_SPINNER_DIAMETER) / 2);
		int y = (WindowFrame.HEIGHT - ADJUSTED_INNER_SPINNER_DIAMETER) / 2;
		g2.drawArc(x, y, ADJUSTED_INNER_SPINNER_DIAMETER, ADJUSTED_INNER_SPINNER_DIAMETER, this.fractionsOfASecondWaited * 360 / FPS, INNER_SPINNER_ARC_ANGLE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.fractionsOfASecondWaited = (this.fractionsOfASecondWaited + 1) % FPS;
		if (this.fractionsOfASecondWaited == 0) {
			this.secondsWaited++;
		}
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
	
	public static String formatTime(int seconds) {
		String mins = seconds / 60 + "";
		String secs = seconds % 60 + "";
		
		while (secs.length() < 2)
			secs = "0" + secs;
		
		return mins + ":" + secs;
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

}