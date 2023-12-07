/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.template.controllers;

import edu.vanier.template.models.Sprite;
import edu.vanier.template.models.Sprite.SpriteType;
import java.util.Collection;
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
    private AudioClip bulletAudio;
    private Image player;
    private Image enemy;
    AnimationTimer animation;
    Text txtLevel;

    public static AudioClip gamewon = new AudioClip(MainAppController.class.getResource("/sounds/round_end.wav").toExternalForm());
    public static AudioClip gameOver = new AudioClip(MainAppController.class.getResource("/sounds/GameOver.wav").toExternalForm());

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
        showLevel();
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
            return pane.getChildren().stream().filter(n -> n instanceof 
                    Sprite).map(n -> (Sprite) n).
                    collect(Collectors.toList());
        }catch(Exception e){
            System.out.println(e.getMessage());;
            
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
                            shoot(sprite);
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
            pane.getChildren().removeAll(spaceShip);
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

        bulletAudio.play();
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
        enemy = new Image("/images/enemyGreen3.png");
        player = new Image("/images/playerShip1_blue.png");
        bulletAudio = new AudioClip(MainAppController.class.getResource("/sounds/8bit_bomb_explosion.wav").toExternalForm());
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
                invader.setFill(new ImagePattern(enemy));
                
                pane.getChildren().add(invader);
                
            }
        }
         
        spaceShip = new Sprite(300, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(player));
        
        pane.getChildren().add(spaceShip);
        
        
    }
    private void levelTwo(){
        enemy = new Image("images/enemyBlack1.png");
        player = new Image("/images/playerShip3_red.png");
        bulletAudio = new AudioClip(MainAppController.class.getResource("/sounds/laser1.wav").toExternalForm());
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
                invader.setFill(new ImagePattern(enemy));
                
                pane.getChildren().add(invader);
                
            }
        }
        spaceShip = new Sprite(300, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(player));
        pane.getChildren().add(spaceShip);
        

    }
    
    private void levelThree(){
        enemy = new Image("images/enemyRed2.png");
        player = new Image("/images/playerShip3_orange.png");
        bulletAudio = new AudioClip(MainAppController.class.getResource("/sounds/laser13.wav").toExternalForm());
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
                invader.setFill(new ImagePattern(enemy));
                
                pane.getChildren().add(invader);
                
            }
        }
        spaceShip = new Sprite(300, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(player));
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
            Text txtGameWon = new Text("Victory!!");
            txtGameWon.setFont(Font.font(50));
            txtGameWon.setFill(Color.BLUE);
            txtGameWon.setX(200);
            txtGameWon.setY(300);
            pane.getChildren().add(txtGameWon);
            gamewon.play();
            }
        }
    }
    
    private void  showLevel(){
        txtLevel = new Text("Level: " + currentLevel);
        txtLevel.setFont(Font.font(25));
        txtLevel.setFill(Color.WHITE);
        txtLevel.setX(250);
        txtLevel.setY(75);
        pane.getChildren().add(txtLevel);
    }

    }
