import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private static final String TITLE = "Hide My Cucumber 🥒";
    private GamePanel gamePanel;
    private ProgressBar progressBar;

    public GameWindow() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressBar = new ProgressBar();
        gamePanel = new GamePanel(progressBar);

        add(progressBar, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        setIconImage(createIcon());

        setVisible(true);

        gamePanel.requestFocusInWindow();
    }

    private Image createIcon() {
        int size = 32;
        java.awt.image.BufferedImage icon = new java.awt.image.BufferedImage(
                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(50, 205, 50));
        g2d.fillOval(4, 10, 24, 12);
        g2d.setColor(new Color(34, 139, 34));
        g2d.drawOval(4, 10, 24, 12);

        g2d.dispose();
        return icon;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public static class ProgressBar extends JPanel {
        private int progress;
        private int totalCucumbers;
        private int collectedCucumbers;
        private Color barColor;
        private Color backgroundColor;

        public ProgressBar() {
            this.progress = 0;
            this.totalCucumbers = 5;
            this.collectedCucumbers = 0;
            this.barColor = new Color(50, 205, 50);
            this.backgroundColor = new Color(50, 50, 50);
            setPreferredSize(new Dimension(800, 45));
            setBackground(new Color(30, 30, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int barX = 150;
            int barY = 10;
            int barWidth = 500;
            int barHeight = 25;

            drawSmallCucumber(g2d, 20, 15);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Progress:", 55, 28);

            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);

            int fillWidth = (int) ((progress / 100.0) * barWidth);
            if (fillWidth > 0) {
                GradientPaint gradient = new GradientPaint(
                        barX, barY, new Color(34, 139, 34),
                        barX, barY + barHeight, barColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(barX, barY, fillWidth, barHeight, 10, 10);
            }

            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(barX, barY, barWidth, barHeight, 10, 10);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String text = collectedCucumbers + " / " + totalCucumbers + " (" + progress + "%)";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = barX + (barWidth - fm.stringWidth(text)) / 2;
            g2d.drawString(text, textX, barY + 18);

            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.drawString("WASD: Move | R: Restart", 670, 28);
        }

        private void drawSmallCucumber(Graphics2D g2d, int x, int y) {

            g2d.setColor(new Color(34, 139, 34));
            g2d.fillOval(x, y, 28, 12);

            g2d.setColor(new Color(50, 180, 50));
            g2d.fillOval(x + 4, y + 3, 20, 6);

            g2d.setColor(new Color(34, 139, 34));
            for (int i = 0; i < 3; i++) {
                g2d.fillOval(x + 5 + i * 7, y + 1, 3, 3);
            }

            g2d.setColor(new Color(20, 80, 20));
            g2d.drawOval(x, y, 28, 12);
        }

        public void setProgress(int collectedCucumbers, int totalCucumbers) {
            this.collectedCucumbers = collectedCucumbers;
            this.totalCucumbers = totalCucumbers;
            this.progress = (collectedCucumbers * 100) / totalCucumbers;
            repaint();
        }

        public void reset() {
            this.progress = 0;
            this.collectedCucumbers = 0;
            repaint();
        }
    }
}
