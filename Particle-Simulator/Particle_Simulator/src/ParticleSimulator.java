import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class ParticleSimulator extends JFrame implements KeyListener {
    private Canvas canvas;
    private JButton switchModeButton;
    private JButton particleByDistanceButton;
    private JButton particleByAngleButton;
    private JButton particleByVelocityButton;

    public static final int FRAME_WIDTH = 1600;
    public static final int FRAME_HEIGHT = 900;

    ParticleSimulator() {
        setTitle("Particle Simulator | FPS: 0");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(FRAME_HEIGHT / 2 - 325, 100, 0, 0));

        canvas = new Canvas();
        canvas.passFrame(this);
        panel.add(canvas);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 300, 250));

        particleByDistanceButton = new JButton("Add Particle (Distance)");
        particleByDistanceButton.addActionListener(e -> {
            ParticleByDistanceInputDialog particleByDistanceDialog = new ParticleByDistanceInputDialog(this);
            particleByDistanceDialog.setVisible(true);
        });
        particleByAngleButton = new JButton("Add Particle (Angle)");
        particleByAngleButton.addActionListener(e -> {
            ParticleByAngleInputDialog particleByAngleDialog = new ParticleByAngleInputDialog(this);
            particleByAngleDialog.setVisible(true);
        });
        particleByVelocityButton = new JButton("Add Particle (Velocity)");
        particleByVelocityButton.addActionListener(e -> {
            ParticleByVelocityInputDialog particleByVelocityDialog = new ParticleByVelocityInputDialog(this);
            particleByVelocityDialog.setVisible(true);
        });

        switchModeButton = new JButton("Switch Mode (Space)");
        switchModeButton.addActionListener(e -> toggleMode());

        buttonPanel.add(particleByDistanceButton);
        buttonPanel.add(particleByAngleButton);
        buttonPanel.add(particleByVelocityButton);
        buttonPanel.add(switchModeButton);

        panel.add(buttonPanel);
        add(panel);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        setVisible(true);

        Timer timer = new Timer(15, e -> {
            canvas.update();
        });
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    private void toggleMode() {
        canvas.toggleExplorerMode();
        
        boolean explorerMode = canvas.isExplorerMode();
        particleByDistanceButton.setEnabled(!explorerMode);
        particleByDistanceButton.setVisible(!explorerMode);
        particleByAngleButton.setEnabled(!explorerMode);
        particleByAngleButton.setVisible(!explorerMode);
        particleByVelocityButton.setEnabled(!explorerMode);
        particleByVelocityButton.setVisible(!explorerMode);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            toggleMode();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public Canvas getCanvas() {
        return canvas;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParticleSimulator());
    }
}

class ParticleByDistanceInputDialog extends JDialog {
    private JTextField particleCountField;
    private JTextField startXField;
    private JTextField startYField;
    private JTextField endXField;
    private JTextField endYField;

    ParticleByDistanceInputDialog(JFrame parent) {
        super(parent, "Particle Input", true);
        setLocationRelativeTo(parent);
        setSize(400, 300);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        particleCountField = createInputField();
        startXField = createInputField();
        startYField = createInputField();
        endXField = createInputField();
        endYField = createInputField();

        addRow(panel, gbc, "Particle Count:", particleCountField);
        addRow(panel, gbc, "Start X:", startXField);
        addRow(panel, gbc, "Start Y:", startYField);
        addRow(panel, gbc, "End X:", endXField);
        addRow(panel, gbc, "End Y:", endYField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // get user input
            int particleCount = Integer.parseInt(particleCountField.getText());
            double startX = Double.parseDouble(startXField.getText());
            double startY = Double.parseDouble(startYField.getText());
            double endX = Double.parseDouble(endXField.getText());
            double endY = Double.parseDouble(endYField.getText());

            // add particles to canvas
            Canvas canvas = ((ParticleSimulator) getParent()).getCanvas();
            canvas.addParticles(particleCount, startX, startY, endX, endY, 45, 80);

            // close the dialog
            setVisible(false);
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        add(panel);
    }

    private JTextField createInputField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 25));
        return textField;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(textField, gbc);
    }
}

class ParticleByAngleInputDialog extends JDialog {
    private JTextField particleCountField;
    private JTextField startAngleField;
    private JTextField endAngleField;

    ParticleByAngleInputDialog(JFrame parent) {
        super(parent, "Particle Input", true);
        setLocationRelativeTo(parent);
        setSize(400, 300);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        particleCountField = createInputField();
        startAngleField = createInputField();
        endAngleField = createInputField();

        addRow(panel, gbc, "Particle Count:", particleCountField);
        addRow(panel, gbc, "Start Angle:", startAngleField);
        addRow(panel, gbc, "End Angle:", endAngleField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // get user input
            int particleCount = Integer.parseInt(particleCountField.getText());
            double startAngle = Double.parseDouble(startAngleField.getText());
            double endAngle = Double.parseDouble(endAngleField.getText());

            // add particles to canvas
            Canvas canvas = ((ParticleSimulator) getParent()).getCanvas();
            canvas.addParticlesByAngle(particleCount, 100, 100, 80, startAngle, endAngle);

            // close the dialog
            setVisible(false);
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        add(panel);
    }

    private JTextField createInputField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 25));
        return textField;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(textField, gbc);
    }
}

class ParticleByVelocityInputDialog extends JDialog {
    private JTextField particleCountField;
    private JTextField startVelocityField;
    private JTextField endVelocityField;

    ParticleByVelocityInputDialog(JFrame parent) {
        super(parent, "Particle Input", true);
        setLocationRelativeTo(parent);
        setSize(400, 300);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        particleCountField = createInputField();
        startVelocityField = createInputField();
        endVelocityField = createInputField();

        addRow(panel, gbc, "Particle Count:", particleCountField);
        addRow(panel, gbc, "Start Velocity:", startVelocityField);
        addRow(panel, gbc, "End Velocity:", endVelocityField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // get user input
            int particleCount = Integer.parseInt(particleCountField.getText());
            double startVelocity= Double.parseDouble(startVelocityField.getText());
            double endVelocity = Double.parseDouble(endVelocityField.getText());

            // add particles to canvas
            Canvas canvas = ((ParticleSimulator) getParent()).getCanvas();
            canvas.addParticlesByVelocity(particleCount, 100, 100, 45, startVelocity, endVelocity);

            // close the dialog
            setVisible(false);
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        add(panel);
    }

    private JTextField createInputField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 25));
        return textField;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String labelText, JTextField textField) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(textField, gbc);
    }
}

