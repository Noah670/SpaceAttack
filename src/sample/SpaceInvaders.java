package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Noah670
 */
public class SpaceInvaders extends Application {

    private Pane root = new Pane();

    private double time = 0;

    //private ImageView imageView;

    //Image playerImage = new Image("player.png");


    private Sprite player = new Sprite(275, 700, 45, 45, "player", Color.GREEN); // set player attributes and size

    public SpaceInvaders() throws FileNotFoundException {
    }


    private Parent createContent() {

        //Media level1 = new Media("level.wav");
        //MediaPlayer playMusic = new MediaPlayer(level1);

        //MediaView mediaView = new MediaView(playMusic);

        // Add sound to the scene
        //Group root = new Group(mediaView);
        //Scene scene = new Scene(root, 500, 200);

        root.setPrefSize(600, 800); // sets the window size

        root.getChildren().add(player);


        //playMusic.play();


        // timer for smoother player animation
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
            }
        };
        timer.start();

        nextEnemy(); // call another enemy after the player defeats them


        return root;
    }

    private List<Sprite> sprites() {
        return root.getChildren().stream().map(n -> (Sprite) n).collect(Collectors.toList());
    }

    private void update() {

        time += 0.016;

        sprites().forEach(s -> {
            switch (s.type) {

                case "enemyAmmo":
                    s.moveDown();

                    if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
                        player.dead = true;
                        s.dead = true;
                    }
                    break;

                case "playerAmmo":
                    s.moveUp();

                    sprites().stream().filter(e -> e.type.equals("enemy")).forEach(enemy -> {
                        if (s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.dead = true;
                            s.dead = true;
                        }
                    });

                    break;

                // controls the behaviour of the enemies shooting at the player
                case "enemy":
                    if (time > 2) {
                        if (Math.random() < 0.3) {
                            shoot(s);
                        }
                    }

                    break;

            }
        });

        root.getChildren().removeIf(n -> {
            Sprite s = (Sprite) n;
            return s.dead;
        });

        if (time > 2) {
            time = 0;
        }

    }

    private void nextEnemy() {
        // 5 enemies
        for (int i = 0; i < 5; i++) {
            // SpaceEnemy sprite
            Sprite s = new Sprite(90 + i * 100, 150, 30, 30, "enemy", Color.DARKRED);

            root.getChildren().add(s);
        }
    }

    private void shoot(Sprite you) {
        Sprite s = new Sprite((int) you.getTranslateX() + 20, (int) you.getTranslateY(), 5, 20, you.type + "Ammo", Color.YELLOW);

        root.getChildren().add(s);

    }

    MediaPlayer playMusic;

    private void playAudio() {
        AudioClip audio = new AudioClip("file:src/sample/level.wav");
        audio.play();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());

        stage.setTitle("Space Attack");

        playAudio();

        // Add sound to the scene
        //Group root = new Group(mediaView);
        stage.centerOnScreen();


        // add background image
        String image = SpaceInvaders.class.getResource("/sample/marioSpace.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch;");


        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A:
                    player.moveLeft();
                    break;
                case D:
                    player.moveRight();
                    break;
                case SPACE:
                    shoot(player);
                    break;

            }
        });

        stage.setScene(scene);
        stage.show();
    }

    private static class Sprite extends Rectangle {
        boolean dead = false;
        final String type;


        Sprite(int x, int y, int w, int h, String type, Color color) {
            super(w, h, color);
            this.type = type;

            setTranslateX(x);
            setTranslateY(y);
        }

        void moveLeft() {
            setTranslateX(getTranslateX() - 20); // move player to the left
        }

        void moveRight() {
            setTranslateX(getTranslateX() + 20); // move player to the right
        }

        void moveUp() {
            setTranslateY(getTranslateY() - 5);
        }

        void moveDown() {
            setTranslateY(getTranslateY() + 5);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}