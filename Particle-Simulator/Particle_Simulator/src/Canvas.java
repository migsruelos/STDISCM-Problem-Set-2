import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Particle {
    double x, y; // position
    double angle; // angle in degrees
    double velocity; // velocity in pixels per second

    Particle(double x, double y, double angle, double velocity) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.velocity = velocity;
    }

    void move(double deltaTime) {
        // update particle position
        double newX = x + velocity * Math.cos(Math.toRadians(angle)) * deltaTime;
        double newY = y + velocity * Math.sin(Math.toRadians(angle)) * deltaTime;
        x = newX;
        y = newY;

        // check collision on borders
        if (newX < 0 || newX > 1260) {
            angle = 180 - angle;
        }
        if (newY < 0 || newY > 680) {
            angle = -angle;
        }
    }
}

class Canvas extends JPanel implements KeyListener{
    private List<Particle> particles;
    private List<Particle> explorerParticles;
    private boolean explorerMode = false;
    private Particle explorerSprite;

    private BufferedImage spriteImage;

    private int frameCount = 0;
    private int fps;
    private long lastFPSTime = System.currentTimeMillis();

    Canvas() {
        particles = new ArrayList<>();
        explorerParticles = new ArrayList<>();
        setPreferredSize(new Dimension(1280, 720));
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        // Load the sprite image
        try {
            spriteImage = ImageIO.read(new File("sprite/sprite.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::calculateFPS, 0, 500, TimeUnit.MILLISECONDS);
    }

    boolean isExplorerMode() {
        return explorerMode;
    }

    void toggleExplorerMode() {
        explorerMode = !explorerMode;
        if (explorerMode) {
            explorerSprite = new Particle(640, 360, 0, 0);
            explorerParticles.clear();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (explorerMode) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    explorerSprite.velocity = 80;
                    explorerSprite.angle = 0;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    explorerSprite.velocity = 80;
                    explorerSprite.angle = 180;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    explorerSprite.velocity = 80;
                    explorerSprite.angle = 270;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    explorerSprite.velocity = 80;
                    explorerSprite.angle = 90;
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (explorerMode) {
            explorerSprite.velocity = 0;
        }
    }


    private void updateFPS() {
        fps = frameCount * 2;
        frameCount = 0;
        lastFPSTime = System.currentTimeMillis();
        repaint();
    }

    private int calculateFPS() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastFPSTime;

        frameCount++;

        // update FPS every 0.5 seconds
        if (elapsedTime >= 500) {
            updateFPS();
        }

        return fps;
    }

    void addParticles(int n, double startX, double startY, double endX, double endY,
                      double initialAngle, double velocity) {
        if (!explorerMode) { // Only add particles in developer mode
            for (int i = 0; i < n; i++) {
                double randomX = startX + Math.random() * (endX - startX);
                double randomY = startY + Math.random() * (endY - startY);
                particles.add(new Particle(randomX, randomY, initialAngle, velocity));
            }
        }
    }

    void addParticlesByAngle(int n, double startX, double startY, double velocity, double startAngle, double endAngle) {
        if (!explorerMode) { // Only add particles in developer mode
            for (int i = 0; i < n; i++) {
                double randomAngle = startY + Math.random() * (endAngle - startAngle);
                particles.add(new Particle(startX, startY, randomAngle, velocity));
            }
        }
    }

    void addParticlesByVelocity(int n, double startX, double startY, double angle, double startVelocity, double endVelocity) {
        if (!explorerMode) { // Only add particles in developer mode
            for (int i = 0; i < n; i++) {
                double randomVelocity = startX + Math.random() * (endVelocity - startVelocity);
                particles.add(new Particle(startX, startY, angle, randomVelocity));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image offscreen = createImage(getWidth(), getHeight());
        Graphics offscreenGraphics = offscreen.getGraphics();

        if (explorerMode) {
            // Render explorer mode
            renderExplorerMode(offscreenGraphics);
        } else {
            // Render developer mode
            renderDeveloperMode(offscreenGraphics);
        }

        offscreenGraphics.setColor(Color.BLACK);
        offscreenGraphics.drawString("FPS: " + calculateFPS(), 10, 20);

        g.drawImage(offscreen, 0, 0, this);
    }

    private void renderDeveloperMode(Graphics offscreenGraphics) {
        offscreenGraphics.setColor(Color.GREEN);
        for (Particle particle : particles) {
            offscreenGraphics.fillOval((int) particle.x - 5, (int) particle.y - 5, 10, 10);
        }

        offscreenGraphics.setColor(Color.BLACK);
        offscreenGraphics.drawString("FPS: " + fps, 10, 20);
    }

    private void renderExplorerMode(Graphics offscreenGraphics) {
        // Render particles from developer mode
        for (Particle particle : particles) {
            int offsetX = (int) (particle.x - explorerSprite.x) + getWidth() / 2;
            int offsetY = (int) (particle.y - explorerSprite.y) + getHeight() / 2;

            if (Math.abs(offsetX) <= getWidth() / 2 && Math.abs(offsetY) <= getHeight() / 2) {
                offscreenGraphics.fillOval(getWidth() / 2 + offsetX - 5, getHeight() / 2 + offsetY - 5, 10, 10);
            }
        }

        // Render particles from explorer mode
        for (Particle particle : explorerParticles) {
            int offsetX = (int) (particle.x - explorerSprite.x) + getWidth() / 2;
            int offsetY = (int) (particle.y - explorerSprite.y) + getHeight() / 2;

            if (Math.abs(offsetX) <= getWidth() / 2 && Math.abs(offsetY) <= getHeight() / 2) {
                offscreenGraphics.fillOval(getWidth() / 2 + offsetX - 5, getHeight() / 2 + offsetY - 5, 10, 10);
            }
        }

        // Render the explorer sprite using PNG image
        if (spriteImage != null) {
            int spriteX = getWidth() / 2 - spriteImage.getWidth() / 2;
            int spriteY = getHeight() / 2 - spriteImage.getHeight() / 2;
            offscreenGraphics.drawImage(spriteImage, spriteX, spriteY, null);
        } else {
            // Fallback to rendering a red circle if sprite image is not loaded
            offscreenGraphics.setColor(Color.RED);
            offscreenGraphics.fillOval(getWidth() / 2 - 5, getHeight() / 2 - 5, 10, 10);
        }
    }

    void update() {
        calculateFPS();
        double deltaTime = 0.05;

        // minimum target FPS: 60
        long targetFrameTime = 1000 / 60;
        long currentTime = System.currentTimeMillis();

        // submit rendering tasks for particles
        particles.forEach(particle -> particle.move(deltaTime));
        repaint();

        // calculates the time taken for the update and rendering tasks
        long elapsedTime = System.currentTimeMillis() - currentTime;

        // sleep to maintain a consistent frame rate
        long sleepTime = Math.max(0, targetFrameTime - elapsedTime);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
