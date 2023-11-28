/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.template.controllers;

import edu.vanier.template.models.Sprite;
import edu.vanier.template.models.Sprite.SpriteType;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.D;
import static javafx.scene.input.KeyCode.W;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.SPACE;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Hassimo
 */
public class MainAppController {
    @FXML
    private Pane pane;
    private double elapsedTime = 0;
    private Sprite spaceShip;
    private Scene scene;
    AnimationTimer animation;
   

    Image enemy1 = new Image("/images/enemyGreen2.png");
    Image player = new Image("/images/playerShip1_blue.png");
    Image bullet = new Image("/images/laserBlue01.png");
    
    @FXML
    public void initialize() {
        spaceShip = new Sprite(300, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(player));
        pane.setBackground(new Background(new BackgroundImage(new Image("/images/starfield_alpha.png"), 
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT)));

    }
    public void initGameComponents() {
        createContent();
        this.scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A -> spaceShip.moveLeft();
                case D -> spaceShip.moveRight();
                case SPACE -> shoot(spaceShip);
                case W -> spaceShip.moveUp();
                case S -> spaceShip.moveDown();
 
            }
        });
    }

    private void createContent() {
        pane.setPrefSize(600, 800);
        pane.getChildren().add(spaceShip);
        //-- Create the game loop.
        animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        animation.start();
        nextLevel(); 
        
    }

    private void nextLevel() {
        for (int i = 0; i < 5; i++) {
            Sprite invader = new Sprite(20 + i * 100, 150, 30, 30, SpriteType.ENEMY, Color.RED);
            invader.setFill(new ImagePattern(enemy1));

            pane.getChildren().add(invader);
        }
    }

    private List<Sprite> sprites() {
        return pane.getChildren().stream().map(n -> (Sprite) n).collect(Collectors.toList());
    }

    private void update() {
        elapsedTime += 0.016;

        sprites().forEach(sprite -> {
            switch (sprite.getType()) {

                case ENEMY_BULLET -> {
                    sprite.moveDown();

                    if (sprite.getBoundsInParent().intersects(spaceShip.getBoundsInParent())) {
                        sprite.setDead(true);
                        spaceShip.decreaseHealth();
                        if(spaceShip.getHealth() == 0){
                            spaceShip.setDead(true);
                            checkGameOver();
                        }
                    }
                }

                case PLAYER_BULLET -> {
                    sprite.moveUp();

                    sprites().stream().filter(e -> e.getType().equals(SpriteType.ENEMY)).forEach(enemy -> {
                        if (sprite.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.setDead(true);
                            sprite.setDead(true);
                        }
                    });
                }

                case ENEMY -> {
                    if (elapsedTime > 2) {
                        if (Math.random() < 0.3) {
                            shoot(sprite);
                        }
                    }
                }
            }
        });

        pane.getChildren().removeIf(n -> {
            try{
            Sprite sprite = (Sprite) n;
            return sprite.isDead();
            }catch(Exception e){
                return false;
            }
        });

        if (elapsedTime > 2) {
            elapsedTime = 0;
        }
    }

    private void shoot(Sprite who) {
        SpriteType whoType = who.getType();
        if (whoType.equals(SpriteType.PLAYER)) whoType = SpriteType.PLAYER_BULLET;
        else if (whoType.equals(SpriteType.ENEMY)) whoType = SpriteType.ENEMY_BULLET;
        Sprite s = new Sprite((int) who.getTranslateX() + 20, 
                (int) who.getTranslateY(), 5, 20, whoType, 
                Color.LIGHTYELLOW);
        s.setFill(new ImagePattern(bullet));
        pane.getChildren().add(s);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }    

    public void stopAnimation() {
        if (animation != null) {
            animation.stop();
        }
    }
    public void checkGameOver(){
        if(spaceShip.isDead()){
            Text gameOverText = new Text("Game Over");
            gameOverText.setFont(Font.font(50));
            gameOverText.setFill(Color.RED);
            gameOverText.setX(200);
            gameOverText.setY(300);
            pane.getChildren().add(gameOverText);
            animation.stop();
        }
    }

    
    
}
