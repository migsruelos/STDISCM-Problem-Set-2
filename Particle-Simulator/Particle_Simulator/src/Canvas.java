import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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

class Canvas extends JPanel {
    private List<Particle> particles;

    private int frameCount = 0;
    private int fps;
    private long lastFPSTime = System.currentTimeMillis();

    Canvas() {
        particles = new ArrayList<>();
        setPreferredSize(new Dimension(1280, 720));

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::calculateFPS, 0, 500, TimeUnit.MILLISECONDS);
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
        for (int i = 0; i < n; i++) {
            double randomX = startX + Math.random() * (endX - startX);
            double randomY = startY + Math.random() * (endY - startY);
            particles.add(new Particle(randomX, randomY, initialAngle, velocity));
        }
    }

    void addParticlesByAngle(int n, double startX, double startY, double velocity, double startAngle, double endAngle) {
        for (int i = 0; i < n; i++) {
            double randomAngle = startY + Math.random() * (endAngle - startAngle);
            particles.add(new Particle(startX, startY, randomAngle, velocity));
        }
    }

    void addParticlesByVelocity(int n, double startX, double startY, double angle, double startVelocity, double endVelocity) {
        for (int i = 0; i < n; i++) {
            double randomVelocity = startX + Math.random() * (endVelocity - startVelocity);
            particles.add(new Particle(startX, startY, angle, randomVelocity));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image offscreen = createImage(getWidth(), getHeight());
        Graphics offscreenGraphics = offscreen.getGraphics();

        offscreenGraphics.setColor(Color.GREEN);
        for (Particle particle : particles) {
            offscreenGraphics.fillOval((int) particle.x - 5, (int) particle.y - 5, 10, 10);
        }

        offscreenGraphics.setColor(Color.BLACK);
        offscreenGraphics.drawString("FPS: " + calculateFPS(), 10, 20);

        g.drawImage(offscreen, 0, 0, this);
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
