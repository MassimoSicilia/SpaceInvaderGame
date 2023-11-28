/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.vanier.template.models;

import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Hassimo
 */
public class Sprite extends Rectangle{
    private boolean dead = false;
    private final SpriteType type;
    private int health = 3;
    
    public Sprite(int x, int y, int w, int h, SpriteType type, Color color){
        super(w,h,color);
        this.type = type;
        setTranslateX(x);
        setTranslateY(y);
    }
     public void moveLeft() {
        setTranslateX(getTranslateX() - 5);
    }

    public void moveRight() {
        setTranslateX(getTranslateX() + 5);
    }

    public void moveUp() {
        setTranslateY(getTranslateY() - 5);
    }

    public void moveDown() {
        setTranslateY(getTranslateY() + 5);
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
    
    public enum SpriteType {
       ENEMY_BULLET,
       PLAYER_BULLET,
       PLAYER,
       ENEMY,
    }
   
}



