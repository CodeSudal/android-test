package com.example.myapplication;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private ScaleGestureDetector scaleGestureDetector;
    private float initialRotation = 0;
    private float rotationDegrees = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // AndroidGraphicFactory를 초기화해야 합니다.
        AndroidGraphicFactory.createInstance(this.getApplication());

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);

        setupMapView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 스케일 제스처 감지기 초기화
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                byte currentZoomLevel = mapView.getModel().mapViewPosition.getZoomLevel();
                byte newZoomLevel = (byte) Math.max(0, Math.min(22, currentZoomLevel + (scaleFactor > 1 ? 1 : -1)));
                mapView.getModel().mapViewPosition.setZoomLevel(newZoomLevel);
                return true;
            }
        });

        // 지도 회전을 위한 터치 리스너 설정
        mapView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_POINTER_DOWN:
                            initialRotation = getRotation(event);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentRotation = getRotation(event);
                            rotationDegrees += currentRotation - initialRotation;
                            mapView.setRotation(rotationDegrees);
                            initialRotation = currentRotation;
                            break;
                    }
                    return true;
                } else {
                    scaleGestureDetector.onTouchEvent(event);
                }
                return false;
            }

            private float getRotation(MotionEvent event) {
                double deltaX = (event.getX(0) - event.getX(1));
                double deltaY = (event.getY(0) - event.getY(1));
                return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
            }
        });
    }

    private void setupMapView() {
        // 타일 캐시 설정
        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1.5f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        // MapFile 설정 (mapfile.map는 실제 파일 경로로 변경해야 합니다)
        MapFile mapFile = new MapFile(getMapFileFromAssets());

        // TileRendererLayer 설정
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapFile,
                mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);

        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
        tileRendererLayer.setTextScale(1.2f);  // 텍스트 크기 조정

        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        // 지도 위치 설정 (예: 서울)
        mapView.setCenter(new LatLong(37.5665, 126.9780));
        mapView.setZoomLevel((byte) 10);
    }

    private File getMapFileFromAssets() {
        AssetManager assetManager = getAssets();
        InputStream inputStream;
        File outFile = new File(getFilesDir(), "south-korea.map");

        try {
            inputStream = assetManager.open("south-korea.map");
            FileOutputStream outputStream = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.destroyAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 필요에 따라 현재 상태를 저장
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 필요에 따라 상태를 복원하고 지도 뷰를 다시 설정
        if (mapView.getLayerManager().getLayers().isEmpty()) {
            setupMapView();
        }
    }
}
