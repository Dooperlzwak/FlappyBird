import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private int birdY = 200;  // Adjust initial Y position for smaller window
    private int birdVelocity = 0;  // Initial velocity of the bird
    private final int GRAVITY = 1;  // Gravity constant
    private final int FLAP_STRENGTH = -12;  // Strength of the bird's flap
    private final int BIRD_X = 100;  // X position of the bird
    private final int BIRD_WIDTH = 30;  // Width of the bird
    private final int BIRD_HEIGHT = 30;  // Height of the bird

    private Timer timer;
    private ArrayList<Pipe> pipes;
    private final int PIPE_WIDTH = 50;
    private final int PIPE_GAP = 100;
    private final int PIPE_SPEED = 5;
    private int score = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private BufferedImage birdImage;

    public GamePanel() {
        try {
            birdImage = ImageIO.read(new File("kisspng-314xelampaposs-profile-5c892a6a432231.390023621552493162275-removebg-preview.png"));  // Load the bird image
            birdImage = scaleImage(birdImage, BIRD_WIDTH, BIRD_HEIGHT);  // Scale the bird image to the desired size
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer = new Timer(20, this);  // Create a timer that calls actionPerformed every 20ms
        timer.start();  // Start the timer
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        pipes = new ArrayList<>();
        spawnPipe();
    }

    private BufferedImage scaleImage(BufferedImage originalImage, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, originalImage.getType());
        Graphics g = scaledImage.getGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return scaledImage;
    }

    private void resetGame() {
        birdY = 200;
        birdVelocity = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        gameStarted = false;
        spawnPipe();
    }

    private void spawnPipe() {
        Random rand = new Random();
        int maxPipeHeight = getHeight() - PIPE_GAP - 10;  // Ensure there's always a minimum gap at the bottom
        int pipeHeight = rand.nextInt(Math.max(1, maxPipeHeight));
        pipes.add(new Pipe(getWidth(), 0, PIPE_WIDTH, pipeHeight));  // Top pipe
        pipes.add(new Pipe(getWidth(), pipeHeight + PIPE_GAP, PIPE_WIDTH, getHeight() - pipeHeight - PIPE_GAP));  // Bottom pipe
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, getWidth(), getHeight());  // Draw the background

        // Draw the bird
        if (birdImage != null) {
            g.drawImage(birdImage, BIRD_X, birdY, BIRD_WIDTH, BIRD_HEIGHT, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(BIRD_X, birdY, BIRD_WIDTH, BIRD_HEIGHT);  // Fallback in case the image fails to load
        }

        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }

        if (!gameStarted) {
            g.setColor(Color.BLACK);
            g.drawString("Press SPACE to start", getWidth() / 2 - 50, getHeight() / 2);
        }

        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over", getWidth() / 2 - 30, getHeight() / 2);
            g.drawString("Press SPACE to restart", getWidth() / 2 - 60, getHeight() / 2 + 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || !gameStarted) {
            return;
        }

        birdVelocity += GRAVITY;  // Apply gravity to the bird's velocity
        birdY += birdVelocity;  // Update the bird's position

        if (birdY > getHeight() - BIRD_HEIGHT) {  // If the bird hits the ground
            birdY = getHeight() - BIRD_HEIGHT;
            gameOver = true;
        }

        if (birdY < 0) {  // If the bird hits the top of the window
            birdY = 0;
            gameOver = true;
        }

        Iterator<Pipe> iterator = pipes.iterator();
        boolean needToSpawnPipe = false;
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.move(PIPE_SPEED);

            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
            }

            if (!pipe.isPassed() && pipe.getX() + pipe.getWidth() < BIRD_X) {
                score++;
                pipe.setPassed(true);
                if (score % 2 == 0) {  // Every pair of pipes passed, spawn a new pair
                    needToSpawnPipe = true;
                }
            }

            // Check for collision with the pipes
            if (pipe.getX() < BIRD_X + BIRD_WIDTH && pipe.getX() + pipe.getWidth() > BIRD_X) {
                if ((pipe.getY() == 0 && birdY < pipe.getHeight()) || (pipe.getY() > 0 && birdY + BIRD_HEIGHT > pipe.getY())) {
                    gameOver = true;
                }
            }
        }

        if (needToSpawnPipe) {
            spawnPipe();
        }

        repaint();  // Repaint the game panel
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame();  // Reset the game if it's over
            } else if (!gameStarted) {
                gameStarted = true;
            }
            if (!gameOver) {
                birdVelocity = FLAP_STRENGTH;  // Make the bird flap
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}