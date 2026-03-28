package com.EpicJava;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Main extends Application {

    int TILES_NUMBER = 10;
    int TILES_SIZE = 65;
    int speed = 1000;
    int score;
    int loops;
    Color BACKGROUND = Color.GRAY;
    Color SNEK = Color.ORANGE;
    Color COOKIE = Color.RED;
    Rectangle[][] board;
    List<Rectangle> snek;
    Rectangle cookie;

    GridPane root;
    Stage stage;
    Timeline timeline;
    enum Direction {
        UP, RIGHT, DOWN, LEFT
    }
    Queue directionQueue = new Queue();
    Direction previousDirection;

    int headX;
    int headY;

    Text scoreText = new Text("0");
    Slider speedometer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        initGame();

        primaryStage.setTitle("Snek");
        primaryStage.show();
    }

    public void initGame() {
        root = new GridPane();
        Scene scene = new Scene(root, 800, 800);
        root.setAlignment(Pos.CENTER);
        root.setVgap(3);
        root.setHgap(3);
        stage.setScene(scene);
        board = new Rectangle[TILES_NUMBER][TILES_NUMBER];
        for (int y = 0; y < TILES_NUMBER; y++) {
            for (int x = 0; x < TILES_NUMBER; x++) {
                Rectangle tile = new Rectangle(TILES_SIZE, TILES_SIZE, BACKGROUND);
                board[x][y] = tile;
                root.add(tile, y + 1, x + 2);
            }
        }
        snek = new ArrayList<>();
        snek.add(board[TILES_NUMBER / 2][TILES_NUMBER / 2]);
        headX = TILES_NUMBER/2;
        headY = TILES_NUMBER/2;
        snek.add(board[TILES_NUMBER / 2][(TILES_NUMBER / 2) + 1]);
        snek.add(board[TILES_NUMBER / 2][(TILES_NUMBER / 2) + 2]);
        for (int i = 0; i < snek.size(); i++) {
            snek.get(i).setFill(SNEK);
        }
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W) {
                directionQueue.enQueue(Direction.UP);
            }
            if (event.getCode() == KeyCode.S) {
                directionQueue.enQueue(Direction.DOWN);
            }
            if (event.getCode() == KeyCode.D) {
                directionQueue.enQueue(Direction.RIGHT);
            }
            if (event.getCode() == KeyCode.A) {
                directionQueue.enQueue(Direction.LEFT);
            }
            if (event.getCode() == KeyCode.Q) {
                speedometer.setValue( speedometer.getValue() - 1);
            }
            if (event.getCode() == KeyCode.E) {
                speedometer.setValue( speedometer.getValue() + 1);
            }
        });

        score = 0;
        speed = 1000;
        previousDirection = Direction.UP;
        setCookie();

        Text predkosc = new Text("Speed: ");
        predkosc.setFont(Font.font(16));
        root.add(predkosc, 0, 0, 2, 1);

        speedometer = new Slider(1, 6, 1);
        speedometer.setMajorTickUnit(1);
        speedometer.setMinorTickCount(0);
        speedometer.setShowTickMarks(true);
        speedometer.setShowTickLabels(true);
        speedometer.setSnapToTicks(true);
        root.add(speedometer, 2, 0, 4, 2);

        Text wynik = new Text("Score: ");
        wynik.setFont(Font.font(16));
        root.add(wynik, 7, 0);

        scoreText.setFont(Font.font(16));
        root.add(scoreText, 8, 0);

        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            loops++;
            double value = speedometer.getValue();
            if (value == 1) {
                speed = 1000;
            } else if (value == 2) {
                speed = 800;
            } else if (value == 3) {
                speed = 600;
            } else if (value == 4) {
                speed = 400;
            } else if (value == 5) {
                speed = 200;
            } else if (value == 6) {
                speed = 100;
            }
            if ( speed == 100 || (speed == 200 && loops % 2 == 0) || (speed == 400 && loops % 4 == 0) ||
                    (speed == 600 && loops % 6 == 0) || (speed == 800 && loops % 8 == 0) ||
                    (speed == 1000 && loops % 10 == 0) ) {
                move();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void move() {
        if (!directionQueue.isEmpty()) {
            Direction current = directionQueue.deQueue();
            if (previousDirection == Direction.UP && current != Direction.DOWN ||
                    previousDirection == Direction.RIGHT && current != Direction.LEFT ||
                    previousDirection == Direction.DOWN && current != Direction.UP ||
                    previousDirection == Direction.LEFT && current != Direction.RIGHT) {
                previousDirection = current;
            }
        }
        if (previousDirection == Direction.UP) {
            headY--;
        } else if (previousDirection == Direction.DOWN) {
            headY++;
        } else if (previousDirection == Direction.RIGHT) {
            headX++;
        } else if (previousDirection == Direction.LEFT) {
            headX--;
        }
        if (headY < 0 || headX < 0 || headY>= 10 || headX >= 10) {
            timeline.stop();
            gameOver();
            return;
        }
        if (board[headY][headX].getFill() == SNEK) {
            timeline.stop();
            gameOver();
        } else if (board[ headY ][headX].getFill() == COOKIE) {
            score = score + (int) speedometer.getValue();
            scoreText.setText(score + "");
            board[headY][headX].setFill(SNEK);
            snek.add(0, board[headY][headX]);
            setCookie();
        } else {
            board[headY][headX].setFill(SNEK);
            snek.add(0, board[headY][headX]);
            snek.get( snek.size() - 1).setFill(BACKGROUND);
            snek.remove(snek.size() - 1);
        }
    }

    private void congratulation() {
        GridPane congratulation = new GridPane();
        congratulation.setAlignment(Pos.CENTER);
        congratulation.setHgap(7);
        congratulation.setVgap(2);
        Scene scene = new Scene(congratulation, 250 , 200);
        Stage congratStage = new Stage();
        congratStage.setScene(scene);
        congratStage.setTitle("Congratulation");
        congratStage.show();

        Text text = new Text("You have won!");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font(14));
        congratulation.add(text, 0, 0, 2, 1);

        Text scoreText = new Text("Score: " + score);
        scoreText.setTextAlignment(TextAlignment.CENTER);
        scoreText.setFont(Font.font(14));
        congratulation.add(scoreText, 0, 1, 2, 1);

        Button restart = new Button("Restart");
        restart.setFont(Font.font(14));
        congratulation.add(restart, 0, 2);
        restart.setOnAction(event -> {
            congratStage.close();
            snek.clear();
            scoreText.setText("0");
            initGame();
        });

        Button exit = new Button("Exit");
        exit.setFont(Font.font(16));
        congratulation.add(exit, 1, 2);
        exit.setOnAction(event -> {
            System.exit(0);
        });
    }

    private void gameOver() {
        GridPane gameOverRoot = new GridPane();
        gameOverRoot.setAlignment(Pos.CENTER);
        gameOverRoot.setHgap(7);
        gameOverRoot.setVgap(2);
        Scene scene = new Scene(gameOverRoot, 250, 200);
        Stage gameOverStage = new Stage();
        gameOverStage.setScene(scene);
        gameOverStage.show();

        Text text = new Text("Game over");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font(14));
        gameOverRoot.add(text, 0, 0, 2, 1);

        Text scoree = new Text("Score: " + score);
        scoree.setTextAlignment(TextAlignment.CENTER);
        scoree.setFont(Font.font(14));
        gameOverRoot.add(scoree, 0, 1, 2, 1);

        Button restart = new Button("Restart");
        restart.setFont(Font.font(14));
        gameOverRoot.add(restart, 0, 2);
        restart.setOnAction(event -> {
            gameOverStage.close();
            snek.clear();
            scoreText.setText("0");
            initGame();
        });

        Button exit = new Button("Exit");
        exit.setFont(Font.font(14));
        gameOverRoot.add(exit, 1, 2);
        exit.setOnAction(event -> {
            System.exit(0);
        });

    }

    private void setCookie() {
        if (snek.size() < Math.pow(TILES_NUMBER, 2)) {
            int x;
            int y;
            do {
                x = (int) (Math.random() * TILES_NUMBER);
                y = (int) (Math.random() * TILES_NUMBER);
            } while (board[x][y].getFill() != BACKGROUND);
            cookie = board[x][y];
            board[x][y].setFill(COOKIE);
        } else {
            timeline.stop();
            congratulation();
        }
    }
}