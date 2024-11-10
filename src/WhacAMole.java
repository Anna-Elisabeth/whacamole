import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class WhacAMole {
    int boardWidth = 600;
    int boardHeight = 650;

    JFrame frame = new JFrame("Mario's Fabulous Whac-A-Mole Game");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton[] board = new JButton[9];
    ImageIcon moleIcon;
    ImageIcon plantIcon;

    JButton currMoleTile;
    JButton currPlantTile1; // First plant
    JButton currPlantTile2; // Second plant

    JButton restartButton = new JButton("Wanna whac that mole again?");

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer1; // First plant timer
    Timer setPlantTimer2; // Second plant timer
    int score = 0;
    int activePlants = 0; // Track active plants
    Timer delayTimer;

    WhacAMole() {
        restartButton.setFont(new Font("Arial", Font.BOLD, 30));
        restartButton.setPreferredSize(new Dimension(150, 50));
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Ariel", Font.PLAIN, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score:" + Integer.toString(score));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        textPanel.add(restartButton, BorderLayout.SOUTH);
        restartButton.setVisible(false); // Initially hidden

        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        frame.add(boardPanel);

        // Load images
        Image plantImg = new ImageIcon(getClass().getResource("./piranha.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        Image moleImg = new ImageIcon(getClass().getResource("./monty.png")).getImage();
        moleIcon = new ImageIcon(moleImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        // Create and add buttons to the board
        for (int i = 0; i < 9; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);

            tile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton tile = (JButton) e.getSource();
                    if (tile == currMoleTile) {
                        score += 10;
                        textLabel.setText("Score: " + Integer.toString(score));
                        if (score > 50 && activePlants < 2) {
                            addSecondPlant();
                        }
                    } else if (tile == currPlantTile1 || tile == currPlantTile2) {
                        gameOver();
                    }
                }
            });
        }

        // Set up timers for mole and plants
        setMoleTimer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Remove the current mole
                if (currMoleTile != null) {
                    currMoleTile.setIcon(null);
                    currMoleTile = null;
                }

                // Randomly select a new tile for the mole
                int num = random.nextInt(9);
                JButton tile = board[num];

                // Ensure the tile isn't occupied by a plant
                while (tile == currPlantTile1 || tile == currPlantTile2) {
                    num = random.nextInt(9);
                    tile = board[num];
                }

                // Set the new tile for the mole
                currMoleTile = tile;
                currMoleTile.setIcon(moleIcon);
            }
        });

        setPlantTimer1 = new Timer(900, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Remove the current plant
                if (currPlantTile1 != null) {
                    currPlantTile1.setIcon(null);
                    currPlantTile1 = null;
                }

                // Randomly select a new tile for the plant
                int num = random.nextInt(9);
                JButton tile = board[num];

                // Ensure the tile isn't occupied by the mole or the other plant
                while (tile == currMoleTile || tile == currPlantTile2) {
                    num = random.nextInt(9);
                    tile = board[num];
                }

                // Set the new tile for the plant
                currPlantTile1 = tile;
                currPlantTile1.setIcon(plantIcon);
            }
        });

        setPlantTimer2 = new Timer(900, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (activePlants >= 2) {
                    // Remove the current plant
                    if (currPlantTile2 != null) {
                        currPlantTile2.setIcon(null);
                        currPlantTile2 = null;
                    }

                    // Randomly select a new tile for the plant
                    int num = random.nextInt(9);
                    JButton tile = board[num];

                    // Ensure the tile isn't occupied by the mole or the other plant
                    while (tile == currMoleTile || tile == currPlantTile1) {
                        num = random.nextInt(9);
                        tile = board[num];
                    }

                    // Set the new tile for the plant
                    currPlantTile2 = tile;
                    currPlantTile2.setIcon(plantIcon);
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Reset the game
                score = 0;
                activePlants = 0;
                textLabel.setText("Score: 0");

                // Enable all buttons
                for (int i = 0; i < 9; i++) {
                    board[i].setEnabled(true);
                }

                // Reset timers and start the game again
                setMoleTimer.start();
                setPlantTimer1.start();
                setPlantTimer2.start();

                // Hide the restart button
                restartButton.setVisible(false);
            }
        });

        // Start the timers and show the frame
        setMoleTimer.start();
        setPlantTimer1.start();
        setPlantTimer2.start();
        frame.setVisible(true);
    }

    private void addSecondPlant() {
        int num = random.nextInt(9); // 0-8
        JButton tile = board[num];
        while (currPlantTile1 == tile || currPlantTile2 == tile || currMoleTile == tile) {
            num = random.nextInt(9);
            tile = board[num];
        }
        currPlantTile2 = tile;
        currPlantTile2.setIcon(plantIcon);
        activePlants++;
    }

    private void gameOver() {
        textLabel.setText("Game Over: " + Integer.toString(score));
        setMoleTimer.stop();
        setPlantTimer1.stop();
        setPlantTimer2.stop();
        for (int i = 0; i < 9; i++) {
            board[i].setEnabled(false);
        }
        activePlants = 0;

        // Check if delayTimer is null before creating a new one
        if (delayTimer == null) {
            delayTimer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    restartButton.setVisible(true);
                    delayTimer.stop();
                }
            });
        }
        delayTimer.start();
    }
}