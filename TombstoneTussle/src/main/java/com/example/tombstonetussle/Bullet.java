// Package declaration
package com.example.tombstonetussle;

// Class representing a bullet in the game
public class Bullet {
    // Instance variables to store bullet's position, speed, and direction
    private double x;  // Horizontal position of the bullet
    private double y;  // Vertical position of the bullet
    private double speed;  // Speed of the bullet
    private double directionX;  // Horizontal direction of the bullet
    private double directionY;  // Vertical direction of the bullet

    // Constructor to initialize a bullet with position, direction, and speed
    public Bullet(double x, double y, double directionX, double directionY, double speed) {
        this.x = x;
        this.y = y;
        this.directionX = directionX;
        this.directionY = directionY;
        this.speed = speed;
    }

    // Getter methods to retrieve properties of the bullet

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSpeed() {
        return this.speed;
    }

    public double getDirectionX() {
        return directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    // Setter methods to modify properties of the bullet

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

}
