
package com.example.tombstonetussle;

public class DrawingModel {

    // This method checks if the point (x,y) is inside any of the body parts.
    public boolean isInsideHumanFigure(double x, double y) {
        boolean insideHead = (x - 250) * (x - 250) + (y - 75) * (y - 75) <= 25 * 25;
        boolean insideBody = x >= 240 && x <= 260 && y >= 100 && y <= 200;
        boolean insideLeftLeg = x >= 240 && x <= 250 && y >= 200 && y <= 300;
        boolean insideRightLeg = x >= 250 && x <= 260 && y >= 200 && y <= 300;
        boolean insideLeftArm = x >= 210 && x <= 240 && y >= 110 && y <= 120;
        boolean insideRightArm = x >= 260 && x <= 290 && y >= 110 && y <= 120;

        return insideHead || insideBody || insideLeftLeg || insideRightLeg || insideLeftArm || insideRightArm;
    }
}
