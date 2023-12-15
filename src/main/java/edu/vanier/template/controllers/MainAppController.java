/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.template.controllers;

import edu.vanier.template.models.Sprite;
import edu.vanier.template.models.Sprite.SpriteType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCode.SHIFT;
import javafx.scene.input.KeyEvent;
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
    private Sprite explosionSprite;
    private Scene scene;
    private int numOfEnemies;
    private int currentLevel = 1;
    private AudioClip bulletAudio;
    private Image player;
    private Image enemy;
    private long lastNanoTime = System.nanoTime();
    private List<String> input;
    AnimationTimer animation;
    private int health = 3;
    Text txtLevel;
    Text txtLives;
    Text txtNumOfEnemies;

    private final AudioClip gamewon = new AudioClip(MainAppController.class.getResource("/sounds/round_end.wav").toExternalForm());
    private final AudioClip gameOver = new AudioClip(MainAppController.class.getResource("/sounds/GameOver.wav").toExternalForm());
    private final AudioClip explosionAudio = new AudioClip(MainAppController.class.getResource("/sounds/8bit_bomb_explosion.wav").toExternalForm());
    
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

        input = new ArrayList<>();
        scene.setOnKeyPressed((KeyEvent e) -> {
            String code = e.getCode().toString();
            if (!input.contains(code)) {
                input.add(code);
            }if(e.getCode() == SPACE){
                shoot(spaceShip);
            }if(e.getCode() == SHIFT){
                changeBullet();
            }
        });

        scene.setOnKeyReleased((KeyEvent e) -> {
            String code = e.getCode().toString();
            input.remove(code);
        });
    }

    private void createContent() {
        pane.setPrefSize(800, 800);
        //-- Create the game loop.
        animation = new AnimationTimer() {
            @Override
            public void handle(long now) {    
                double time = (now - lastNanoTime) / 100;
                lastNanoTime = now;
                
                spaceShip.setVelocity(0, 0);
                spaceShip.setVelocity(0, 0);
                if(input.contains("D")){
                    spaceShip.addVelocity(-0.5, 0);
                    spaceShip.moveRight();
                }
                if(input.contains("A")){
                    spaceShip.addVelocity(0.5,0);
                    spaceShip.moveLeft();
                }
                if(input.contains("W")){
                    spaceShip.addVelocity(0, -100);
                    spaceShip.moveUp();
                }
                if(input.contains("S")){
                    spaceShip.addVelocity(0, 100);
                    spaceShip.moveDown();
                }

                spaceShip.updatePlayer(time);
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
            System.out.println(e.getMessage());
            
        }
        return null;
       
    }

    private void update() {
        elapsedTime += 0.016;
        
        sprites().forEach(sprite -> {
            switch (sprite.getType()) {

                case ENEMY_BULLET -> {
                    sprite.enemyBulletMoveDown();
                    

                    if (sprite.getBoundsInParent().intersects(spaceShip.getBoundsInParent())) {
                        sprite.setDead(true);
                        spaceShip.decreaseHealth();
                        health--;
                        txtLives.setText("Lives: " + health);
                        playerHit();
                        explosionAudio.play();

                        if(spaceShip.getHealth() == 0){
                            spaceShip.setDead(true);
                            pane.getChildren().remove(spaceShip);
                            checkGameOver();
                        }
                        
                        showEnemies();
                        
                    }
                }
                

                case PLAYER_BULLET -> {
                    sprite.playerBulletMoveUp();
                    try{
                    sprites().stream().filter(e -> e.getType().equals(SpriteType.ENEMY)).forEach(enemy -> {
                        if (sprite.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemyHit(enemy);
                            explosionAudio.play();
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
                            sprite.moveEnemyLeft();
                        }else{
                            sprite.moveEnemyRight();
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
                    txtNumOfEnemies.setText("Enemies: " + numOfEnemies);
                }
            }
            return sprite.isDead();
            }catch(Exception e){
                return false;
            }
        });
        
        if(currentLevel == 1 || currentLevel == 2){
        if(numOfEnemies == 0){
            try{
            pane.getChildren().removeAll(spaceShip,txtLives, txtNumOfEnemies);
            health = 3;
            changelevel(currentLevel + 1);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
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
            gameOverText.setX(370);
            gameOverText.setY(300);
            pane.getChildren().add(gameOverText);
            animation.stop();
            gameOver.play();
        }
    }
    private void checkGameWon(){
        if(currentLevel == 3){
            if(numOfEnemies == 0){
            animation.stop();
            Text txtGameWon = new Text("Victory!!");
            txtGameWon.setFont(Font.font(50));
            txtGameWon.setFill(Color.BLUE);
            txtGameWon.setX(370);
            txtGameWon.setY(300);
            pane.getChildren().add(txtGameWon);
            gamewon.play();
            }
        }
    }
    
    private void levelOne(){
        enemy = new Image("/images/enemyGreen3.png");
        player = new Image("/images/playerShip1_blue.png");
        bulletAudio = new AudioClip(MainAppController.class.getResource("/sounds/laser5.wav").toExternalForm());
        currentLevel = 1;
        numOfEnemies = 15;
        int rows = 3;
        int columns = 5;
        int invaderWidth = 30;
        int invaderHeight = 30;
        
         for(int i = 0; i < rows;i++ ){
            for(int j = 0; j < columns; j++){
                int x = 250 + j * (invaderWidth + 50);
                int y = 150 + i*(invaderHeight + 50);
                
                 invader = new Sprite(x, y, invaderWidth, invaderHeight, SpriteType.ENEMY, Color.TRANSPARENT);
                invader.setFill(new ImagePattern(enemy));
                
                pane.getChildren().add(invader);
                
            }
        }
         
        spaceShip = new Sprite(400, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
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
                int x = 250 + j * (invaderWidth + 50);
                int y = 150 + i*(invaderHeight + 50);
                
                invader = new Sprite(x, y, invaderWidth, invaderHeight, SpriteType.ENEMY, Color.TRANSPARENT);
                invader.setFill(new ImagePattern(enemy));
                
                pane.getChildren().add(invader);
                
            }
        }
        spaceShip = new Sprite(400, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
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
                int x = 250 + j * (invaderWidth + 50);
                int y = 150 + i*(invaderHeight + 50);
                
                invader = new Sprite(x, y, invaderWidth, invaderHeight, SpriteType.ENEMY, Color.TRANSPARENT);
                invader.setFill(new ImagePattern(enemy));
                
                pane.getChildren().add(invader);
                
            }
        }
        spaceShip = new Sprite(400, 750, 40, 40, SpriteType.PLAYER, Color.BLUE);
        spaceShip.setFill(new ImagePattern(player));
        pane.getChildren().add(spaceShip);
    
    }
    private void changelevel(int currentLevel){
        try{
        showLives();
        showEnemies();
       
       
        switch(currentLevel){
            case 1 ->{
                levelOne();
            }
            case 2 -> {
                levelTwo();
                txtLevel.setText("Level 2");
                txtNumOfEnemies.setText("Enemies: 18");

                }
            case 3 -> {
                levelThree();
                txtLevel.setText("Level 3");
                txtNumOfEnemies.setText("Enemies: 20");
            }
        }
        
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
         pane.getChildren().addAll(txtLives,txtNumOfEnemies);
        currentLevel++;
    }
    
    private void changeBullet(){
        if(bulletColor == blueLaser){
            bulletColor = redLaser;
        }else if(bulletColor == redLaser){
            bulletColor = blueLaser;
        }
        
        

    }
    
    
    private void playerHit(){
         explosionSprite = new Sprite((int) spaceShip.getTranslateX(),
                (int) spaceShip.getTranslateY()-10,30,30, 
                SpriteType.EXPLOSION, Color.TRANSPARENT);
        explosionSprite.setFill(new ImagePattern(explosionEffect));
        pane.getChildren().add(explosionSprite);
        
        //removing effect
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), explosionSprite);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> pane.getChildren().remove(explosionSprite));
        fadeOut.play();
        
    }
    
    private void enemyHit(Sprite enemy){
         explosionSprite = new Sprite((int) enemy.getTranslateX(),
                 (int) enemy.getTranslateY() + 10, 30,30, SpriteType.EXPLOSION, Color.TRANSPARENT);
        
         explosionSprite.setFill(new ImagePattern(explosionEffect));
         pane.getChildren().add(explosionSprite);
         
        //removing effect
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), explosionSprite);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> pane.getChildren().remove(explosionSprite));
        fadeOut.play();
         
    }
    
    private void showEnemies(){
        txtNumOfEnemies = new Text();
        txtNumOfEnemies.setFont(Font.font(20));
        txtNumOfEnemies.setFill(Color.RED);
        txtNumOfEnemies.setX(50);
        txtNumOfEnemies.setY(40);
        switch(currentLevel){
            case 1 -> txtNumOfEnemies.setText("Enemies: 15");
            case 2 -> txtNumOfEnemies.setText("Enemies: 18");
            case 3 -> txtNumOfEnemies.setText("Enemies: 20");
        }
    }
    private void showLives(){
        txtLives = new Text();
        txtLives.setFont(Font.font(20));
        txtLives.setFill(Color.RED);
        txtLives.setX(50);
        txtLives.setY(60);
        switch (health) {
            case 3 -> txtLives.setText("Lives: 3");
            case 2 -> txtLives.setText("Lives: 2");
            case 1 -> txtLives.setText("Lives: 1");
            case 0 -> txtLives.setText("Lives: 0");
            
        }
    }
    private void showLevel(){
        txtLevel = new Text("Level " + currentLevel);
        txtLevel.setFont(Font.font(35));
        txtLevel.setFill(Color.WHITE);
        txtLevel.setX(400);
        txtLevel.setY(60);
        pane.getChildren().add(txtLevel);
    }


    }
