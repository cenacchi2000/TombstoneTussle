package com.example.tombstonetussle;

import javafx.scene.paint.Color;

public class DrawingModel {

    // This method checks if the point (x,y) is inside any of the body parts.
    public boolean isInsideHumanFigure(DrawingView view, double x, double y) {
        int countInside = 0;
        int totalPixels = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Color color = view.getPixelColor(x + i, y + j);
                if (color != null && color.getRed() < 0.9 && color.getGreen() < 0.9 && color.getBlue() < 0.9) {
                    countInside++;
                }
                totalPixels++;
            }
        }

        return (double) countInside / totalPixels < 0.5;  // almeno il 50% dei pixel deve essere all'interno
    }




}
