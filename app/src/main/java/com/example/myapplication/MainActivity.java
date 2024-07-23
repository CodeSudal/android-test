package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private View mainLayout;
    private FrameLayout colorBox;
    private TextView colorView;
    private TextView textView;
    private Button buttonChangeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainLayout = findViewById(R.id.main);
        colorBox = findViewById(R.id.colorBox);
        colorView = findViewById(R.id.colorView);
        textView = findViewById(R.id.textView);
        buttonChangeColor = findViewById(R.id.button_change_color);

        Button buttonChangeColor = findViewById(R.id.button_change_color);
        buttonChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = changeBackgroundColor();

                String colorText = String.format("#%06X", (0xFFFFFF & color));
                colorView.setText(colorText);
                colorView.setTextColor(color);
            }
        });

        Button buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity();
            }
        });
    }

    private int changeBackgroundColor() {
        int color = ColorUtil.getRandomColor();
        mainLayout.setBackgroundColor(color);

        return color;
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
    }
}