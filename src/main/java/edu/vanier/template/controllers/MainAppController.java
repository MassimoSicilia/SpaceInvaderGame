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
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.D;
import static javafx.scene.input.KeyCode.W;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCode.R;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;


import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Hassimo
 */
public class MainAppController {
    @FXML
    private Pane pane;
    private double elapsedTime = 0;
    private Sprite spaceShip;
    private Sprite invader;
    private Sprite bullet;
    private Scene scene;
    private int numOfEnemies;
    private int currentLevel = 1;
    AnimationTimer animation;

    public static AudioClip explosion = new AudioClip(MainAppController.class.getResource("/sounds/8bit_bomb_explosion.wav").toExternalForm());
    public static AudioClip gamewon = new AudioClip(MainAppController.class.getResource("/sounds/round_end.wav").toExternalForm());
    public static AudioClip gameOver = new AudioClip(MainAppController.class.getResource("/sounds/GameOver.wav").toExternalForm());

    Image enemyLevelOne = new Image("/images/enemyGreen3.png");
    Image enemyBlack = new Image("images/enemyBlack1.png");
    Image enemyRed = new Image("images/enemyRed2.png");
    Image playerLevelOne = new Image("/images/playerShip1_blue.png");
    Image playerOrange = new Image("/images/playerShip3_orange.png");
    Image playerRed = new Image("/images/playerShip3_red.png");
    Image bulletBlue = new Image("/images/laserBlue01.png");
    Image bulletRed = new Image("/images/laserRed01.png");
    Image explosionEffect = new Image("/images/explosion00.png");
    Image enemyLaser = new Image("/images/laserGreen10.png");
    
    private final Paint redLaser = new ImagePattern(bulletRed);
    private final Paint blueLaser = new ImagePattern(bulletBlue);
    private Paint bulletColor = blueLaser;

    
    
  
    
    @FXML
    public void initialize() {
        
        changelevel(currentLevel);
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
                case R -> changeBullet();
 
            }
        });
    }

    private void createContent() {
        pane.setPrefSize(600, 800);
        //-- Create the game loop.
        animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        animation.start();
        
    }



    private List<Sprite> sprites() {
        try{
        return pane.getChildren().stream().map(n -> (Sprite) n).collect(Collectors.toList());
        }catch(Exception e){
            e.getMessage();
        }
        return null;
        
    }

    private void update() {
        elapsedTime += 0.016;

        sprites().forEach(sprite -> {
            switch (sprite.getType()) {

                case ENEMY_BULLET -> {
                    sprite.enemyMoveDown();
                    

                    if (sprite.getBoundsInParent().intersects(spaceShip.getBoundsInParent())) {
                        sprite.setDead(true);
                        spaceShip.decreaseHealth();
                        
                        if(spaceShip.getHealth() == 0){
                            spaceShip.setDead(true);
                            pane.getChildren().remove(spaceShip);
                            checkGameOver();
                        }
                    }
                }

                case PLAYER_BULLET -> {
                    sprite.moveUp();
                    try{
                    sprites().stream().filter(e -> e.getType().equals(SpriteType.ENEMY)).forEach(enemy -> {
                        if (sprite.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.setDead(true);
                            sprite.setDead(true);
                            
                        }
                    });
                    }catch(Exception e){
                        e.getMessage();
                    }
                }

                case ENEMY -> {
                    if (elapsedTime > 2) {
                        if (Math.random() < 0.2) {
//                            shoot(sprite);
                        }
                    }
                    if(elapsedTime > 2){
                        if(Math.random() < 0.5){
                            sprite.moveRight();
                        }else{
                            sprite.moveLeft();
                        }
                    }
                }
            }
        });

        pane.getChildren().removeIf(n -> {
            Sprite sprite;
            try{
            sprite = (Sprite) n;
            if(sprite.isDead()){
                if(sprite.getType() == SpriteType.ENEMY){
                    numOfEnemies--;
                }
            }
            return sprite.isDead();
            }catch(Exception e){
                return false;
            }
        });
        if(currentLevel == 1 || currentLevel == 2){
        if(numOfEnemies == 0){
            changelevel(currentLevel + 1);
        }
        }else if(currentLevel == 3){
            if(numOfEnemies == 0){
                checkGameWon();
            }
        }

        if (elapsedTime > 2) {
            elapsedTime = 0;
        }
    }

    private void shoot(Sprite who) {
        SpriteType whoType = who.getType();
        if (whoType.equals(SpriteType.PLAYER)) whoType = SpriteType.PLAYER_BULLET;
        
        
        else if (whoType.equals(SpriteType.ENEMY)) whoType = SpriteType.ENEMY_BULLET;
        bullet = new Sprite((int) who.getTranslateX() + 20, 
                (int) who.getTranslateY(), 5, 20, whoType, 
                Color.TRANSPARENT);
        if(whoType == SpriteType.ENEMY_BULLET){
            bullet.setFill(new ImagePattern(enemyLaser));
                    pane.getChildren().add(bullet);

        }else{
        bullet.setFill(bulletColor);
                pane.getChildren().add(bullet);

        }
        
        //explosion effect for player
        Sprite explosionSprite = new Sprite((int) who.getTranslateX(),
                (int) who.getTranslateY()-10, 30,30, 
                SpriteType.EXPLOSION, Color.TRANSPARENT);
        explosionSprite.setFill(new ImagePattern(explosionEffect));
        pane.getChildren().add(explosionSprite);
        
        
        //removing effect
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5),explosionSprite);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> pane.getChildren().remove(explosionSprite));
        fadeOut.play();

        explosion.play();
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
            gameOver.play();
        }
    }

    
    private void levelOne(){
        currentLevel = 1;
        numOfEnemies = 15;
        int rows = 3;
        int columns = 5;
        int invaderWidth = 30;
        int invaderHeight = 30;
        
         for(int i = 0; i < rows;i++ ){
            for(int j = 0; j < columns; j++){
                int x = 100 + j * (invaderWidth + 50);
                int y = 150 + i*(invaderHeight + 50);
                
                 invader = new Sprite(x, y, invaderWidth, invaderHeight, SpriteType.ENEMY, Color.TRANSPARENT);
                invader.setFill(new ImagePattern(enemyLevelOne));
                
                pane.getChildren().add(invader);
                
            }
        }
         
        spaceShip = new Sprite(300, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(playerLevelOne));
        
        pane.getChildren().add(spaceShip);
        
        
    }
    private void levelTwo(){
        
        currentLevel = 2;
        numOfEnemies = 18;
        int rows = 3;
        int columns = 6;
        int invaderWidth = 30;
        int invaderHeight = 30;
        
         for(int i = 0; i < rows;i++ ){
            for(int j = 0; j <columns; j++){
                int x = 100 + j * (invaderWidth + 50);
                int y = 150 + i*(invaderHeight + 50);
                
                invader = new Sprite(x, y, invaderWidth, invaderHeight, SpriteType.ENEMY, Color.TRANSPARENT);
                invader.setFill(new ImagePattern(enemyBlack));
                
                pane.getChildren().add(invader);
                
            }
        }
        spaceShip = new Sprite(300, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(playerRed));
        pane.getChildren().add(spaceShip);
        

    }
    
    private void levelThree(){
        currentLevel = 3;
        numOfEnemies = 20;
        int rows = 4;
        int columns = 5;
        int invaderWidth = 30;
        int invaderHeight = 30;
        
         for(int i = 0; i < rows;i++ ){
            for(int j = 0; j <columns; j++){
                int x = 100 + j * (invaderWidth + 50);
                int y = 150 + i*(invaderHeight + 50);
                
                invader = new Sprite(x, y, invaderWidth, invaderHeight, SpriteType.ENEMY, Color.TRANSPARENT);
                invader.setFill(new ImagePattern(enemyRed));
                
                pane.getChildren().add(invader);
                
            }
        }
        spaceShip = new Sprite(300, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(playerOrange));
        pane.getChildren().add(spaceShip);
    
    }
    private void changelevel(int currentLevel){
        switch(currentLevel){
            case 1 ->{
                levelOne();
            }
  
            case 2 -> {
                levelTwo();
                
                }
            case 3 -> {
                levelThree();
                
            }
            
        }
        currentLevel++;
        
    
    }
    
    private void changeBullet(){
        if(bulletColor == blueLaser){
            bulletColor = redLaser;
        }else if(bulletColor == redLaser){
            bulletColor = blueLaser;
        }
        
        

    }
    private void checkGameWon(){
        if(currentLevel == 3){
            if(numOfEnemies == 0){
            animation.stop();
            Text gameWonText = new Text("Victory!!");
            gameWonText.setFont(Font.font(50));
            gameWonText.setFill(Color.BLUE);
            gameWonText.setX(200);
            gameWonText.setY(300);
            pane.getChildren().add(gameWonText);
            gamewon.play();
            }
        }
    }

    }
