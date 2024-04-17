import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.Integer.parseInt;

public class LifeOfGame extends JFrame {
    int BoardSize;
    int BoardHeight;
    int BoardWidth;
    int CellSize;
    boolean[][] Board;
    myCanvas canvas;
    JLabel evolutionCounterLbl;
    int evolutionCounts = 0;
    Timer simulationTimer;

    public static void main(String[] args) {
        new LifeOfGame().setVisible(true);
    }

    LifeOfGame() {
        init();
    }

    void init() {
        this.setTitle("Life of Game");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });

        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(100, 30));
        startButton.addActionListener(e -> startSimulation());

        JButton nextButton = new JButton("Next");
        nextButton.setPreferredSize(new Dimension(100, 30));
        nextButton.addActionListener(e -> onceSimulation());

        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(100, 30));
        stopButton.addActionListener(e -> stopSimulation());

        JButton resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 30));
        resetButton.addActionListener(e -> resetBoard());

        JButton randomButton = new JButton("Random");
        randomButton.setPreferredSize(new Dimension(100, 30));
        randomButton.addActionListener(e -> randomBoard());

        JTextField BoardSizeTF = new JTextField();
        BoardSizeTF.setColumns(3);
        BoardSizeTF.setFont(new Font("Ubuntu", Font.PLAIN, 20));
        BoardSizeTF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                resetBoard();
                updateBoardSize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resetBoard();
                updateBoardSize();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                resetBoard();
                updateBoardSize();
            }

            private void updateBoardSize() {
                try {
                    BoardSize = parseInt(BoardSizeTF.getText());
                    if (BoardSize <= 10) {
                        BoardSize = 10;
                    }



                } catch (Exception ex) {
                    BoardSize = 10;
                }
                finally {
                    BoardWidth = BoardSize;
                    CellSize = (int)Math.round((double)(canvas.getWidth()) / (double)(BoardWidth));
                    BoardHeight = canvas.getHeight() / CellSize;
                    Board = new boolean[BoardWidth][BoardHeight];
                }
            }
        });

        JLabel BoardSizeLbl = new JLabel("Board Size (>= 10)");
        BoardSizeLbl.setFont(new Font("Ubuntu", Font.PLAIN, 20));

        evolutionCounterLbl = new JLabel("Evolution Counter: " + evolutionCounts);
        evolutionCounterLbl.setFont(new Font("Ubuntu", Font.PLAIN, 20));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(startButton);
        buttonsPanel.add(nextButton);
        buttonsPanel.add(stopButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(randomButton);

        JPanel settingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        settingPanel.add(BoardSizeLbl);
        settingPanel.add(BoardSizeTF);

        JPanel evolutionCounterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        evolutionCounterPanel.add(evolutionCounterLbl);

        JPanel HeaderPanel = new JPanel(new BorderLayout());
        HeaderPanel.add(settingPanel, BorderLayout.WEST);
        HeaderPanel.add(evolutionCounterPanel, BorderLayout.EAST);

        canvas = new myCanvas();
        canvas.setPreferredSize(new Dimension(800, 800));

        this.add(HeaderPanel, BorderLayout.NORTH);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.add(canvas);

        this.pack();
    }

    private class myCanvas extends JComponent {
        public myCanvas() {
            addMouseListener(new CellClickListener());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            //draw cells background
            for (int i = 0; i < BoardWidth; i++) {
                for (int j = 0; j < BoardHeight; j++) {
                    if (Board[i][j]) {
                        g.setColor(Color.ORANGE);
                        g.fillRect(i * CellSize, j * CellSize, CellSize, CellSize);
                    }
                }
            }


            //draw board's cells
            g.setColor(Color.GRAY);
            for (int i = 0; i <= BoardWidth; i++) {
                g.drawLine(i * CellSize, 0, i * CellSize, BoardHeight * CellSize);
            }
            for(int i = 0; i <= BoardHeight; i++){
                g.drawLine(0, i * CellSize, BoardWidth * CellSize, i * CellSize);
            }
        }
    }

    private class CellClickListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            int x = evt.getX() / CellSize;
            int y = evt.getY() / CellSize;

            if(x<BoardWidth && y < BoardHeight){
                Board[x][y] = !Board[x][y];
                canvas.repaint();
            }
        }
    }

    private void startSimulation() {

        if(simulationTimer == null || !simulationTimer.isRunning()){
            simulationTimer = new Timer(333, e -> {
                updateBoard();
                canvas.repaint();
            });
            simulationTimer.start();
        }

    }

    private void stopSimulation() {
        if (simulationTimer != null && simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
    }
    private void onceSimulation() {
        stopSimulation();
        updateBoard();
        canvas.repaint();
    }
    private void resetBoard() {
        stopSimulation();
        evolutionCounts = 0;
        evolutionCounterLbl.setText("Evolution Counter: " + evolutionCounts);
        for (int i = 0; i < BoardWidth; i++) {
            for (int j = 0; j < BoardHeight; j++) {
                Board[i][j] = false;
            }
        }
        canvas.repaint();
    }

    private void randomBoard() {
        stopSimulation();
        resetBoard();
        for (int i = 0; i < BoardWidth; i++) {
            for (int j = 0; j < BoardHeight; j++) {
                int rd = (int) (Math.random() * 3);
                if (rd == 1)
                    Board[i][j] = true;
            }
        }
        canvas.repaint();
    }

    private void updateBoard() {
        evolutionCounts += 1;
        evolutionCounterLbl.setText("Evolution Counter: " + evolutionCounts);
        boolean[][] newBoard = new boolean[BoardWidth][BoardHeight];

        for (int i = 0; i < BoardWidth; i++) {
            for (int j = 0; j < BoardHeight; j++) {
                int neighbors = countNeighbors(i, j);

                if (Board[i][j]) {
                    if (neighbors == 2 || neighbors == 3)
                        newBoard[i][j] = true;
                } else {
                    if (neighbors == 3)
                        newBoard[i][j] = true;
                }
            }
        }

        Board = newBoard;
    }

    private int countNeighbors(int x, int y) {
        int count = 0;

        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                if (!(i == 0 && j == 0)) {
                    int otherX = x + i;
                    int otherY = y + j;

                    if (otherX >= 0 && otherX < BoardWidth && otherY >= 0 && otherY < BoardHeight) {
                        if (Board[otherX][otherY]) {
                            count++;
                        }
                    }
                }
        return count;
    }
}