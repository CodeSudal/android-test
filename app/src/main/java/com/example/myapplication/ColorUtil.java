package com.example.myapplication;

import android.graphics.Color;
import java.util.Random;

public class ColorUtil {

    public static int getRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static int getComplementaryColor(int color) {
        int alpha = Color.alpha(color);
        int red = 255 - Color.red(color);
        int green = 255 - Color.green(color);
        int blue = 255 - Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
