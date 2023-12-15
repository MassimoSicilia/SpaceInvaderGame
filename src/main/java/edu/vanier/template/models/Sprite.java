/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.template.models;

import edu.vanier.template.controllers.MainAppController;
import javafx.animation.Interpolatable;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Hassimo
 */
public class Sprite extends Rectangle{
    private boolean dead = false;
    private final SpriteType type;
    private int health = 3;
    private double velocityX;
    private double velocityY;
    private double positionX;
    private double positionY;
    public Timeline movement;
    
    
    public Sprite(int x, int y, int w, int h, SpriteType type, Color color){
        super(w,h,color);
        this.type = type;
        setTranslateX(x);
        setTranslateY(y);
        
        initializeMovement();
    }
    private void initializeMovement(){
        movement = new Timeline();
        movement.setCycleCount(Timeline.INDEFINITE);
    }
     public void moveLeft() {
         
         setTranslateX(getTranslateX() - 2);
     }
   

    public void moveRight() {
        setTranslateX(getTranslateX() + 2);
    }
    public void moveEnemyLeft(){
        movement.stop();
        movement.getKeyFrames().clear();
        KeyFrame kf = new KeyFrame(Duration.seconds(1), 
        new KeyValue(translateXProperty(), getTranslateX() - 20)
        );
        
        movement.getKeyFrames().addAll(kf);
        movement.setAutoReverse(true);
        movement.setCycleCount(Timeline.INDEFINITE);
        movement.play();
        
    }
    public void moveEnemyRight(){
        movement.stop();
        movement.getKeyFrames().clear();
        KeyFrame kf = new KeyFrame(Duration.seconds(1), 
        new KeyValue(translateXProperty(), getTranslateX() + 20)
        );
        
        movement.getKeyFrames().add(kf);
        movement.setAutoReverse(true);
        movement.setCycleCount(Timeline.INDEFINITE);
        movement.play();
        
    }
    

    public void moveUp() {
        setTranslateY(getTranslateY() - 2);
   }

    public void moveDown() {
        setTranslateY(getTranslateY() + 2);
    }
    
    
    public void enemyBulletMoveDown() {
        setTranslateY(getTranslateY() + 3);
    }
    public void playerBulletMoveUp() {
        setTranslateY(getTranslateY() - 3);
    }

    public boolean isDead() {
        return dead;
    }

    public SpriteType getType() {
        return type;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
    public int getHealth(){
        return this.health;
    }
    public void decreaseHealth(){
        this.health--;
    }
    
    public void setVelocity(double x, double y){
        velocityX = x;
        velocityY = y;
    }
    public void addVelocity( double x, double y){
        velocityX += x;
        velocityY += y;
    }
    public void updatePlayer(double time){
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    
    public enum SpriteType {
       ENEMY_BULLET,
       PLAYER_BULLET,
       PLAYER,
       ENEMY,
       EXPLOSION;
    }
   
}



