package com.izv.paint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by rober on 21/01/2015.
 */

public class Vista extends View implements ColorPicker.OnColorChangedListener {

    private int accion;
    private Paint pincel;
    private Bitmap mapaDeBits;
    private Canvas lienzoFondo;
    private float x0 = 0, y0 = 0, x1 = 0, y1 = 0;
    private Path rectaPoligonal = new Path();
    private int alto, ancho;
    private double radio = 0;
    private int grosor = 8;
    private int color = Color.BLACK;
    private Context c;

    public Vista(Context context) {
        super(context);
        pincel = new Paint();
        pincel.setStrokeWidth(grosor);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setAntiAlias(true);
        c = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mapaDeBits, 0, 0, null);
        switch (accion) {
            case 0://A mano alzada
                pincel.setColor(color);
                pincel.setStrokeWidth(grosor);
                canvas.drawPath(rectaPoligonal, pincel);
                break;
            case 1://Linea recta
                pincel.setColor(color);
                pincel.setStrokeWidth(grosor);
                canvas.drawLine(x0, y0, x1, y1, pincel);
                break;
            case 2://Circulo
                pincel.setColor(color);
                pincel.setStrokeWidth(grosor);
                canvas.drawCircle(x0, y0, (float) radio, pincel);
                break;
            case 3://Rectangulo
                pincel.setColor(color);
                pincel.setStrokeWidth(grosor);
                canvas.drawRect(x0, y0, x1, y1, pincel);
                break;
            case 4://Borrar
                pincel.setColor(Color.WHITE);
                pincel.setStrokeWidth(50);
                canvas.drawPath(rectaPoligonal, pincel);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (accion) {
                    case 4:
                    case 0://A mano alzada
                        x0 = x1 = event.getX();
                        y0 = y1 = event.getY();
                        rectaPoligonal.reset();
                        rectaPoligonal.moveTo(x0, y0);
                        break;
                    case 1://Linea recta
                        x0 = x;
                        y0 = y;
                        break;
                    case 2://Circulo
                        x0 = x;
                        y0 = y;
                        break;
                    case 3://Rectangulo
                        x0 = x;
                        y0 = y;
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (accion) {
                    case 4:
                    case 0://A mano alzada
                        rectaPoligonal.quadTo(x1, y1, (x + x1) / 2, (y + y1) / 2);
                        x1 = x;
                        y1 = y;
                        x0 = x1;
                        y0 = y1;
                        lienzoFondo.drawLine(x0, y0, x1, y1, pincel);
                        invalidate();
                        break;
                    case 1://Linea recta
                        x1 = x;
                        y1 = y;
                        //lienzoFondo.drawLine(x0,y0,x1,y1,pincel);
                        invalidate();
                        break;
                    case 2://Circulo
                        x1 = x;
                        y1 = y;
                        radio = Math.sqrt(Math.pow((x1 - x0), 2) + Math.pow((y1 - y0), 2));
                        //lienzoFondo.drawCircle(x0,y0,(float)radio,pincel);
                        invalidate();
                        break;
                    case 3://Rectangulo
                        x1 = x;
                        y1 = y;
                        invalidate();
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (accion) {
                    case 4:
                    case 0://A mano alzada
                        x1 = x;
                        y1 = y;
                        lienzoFondo.drawPath(rectaPoligonal, pincel);
                        x0 = y0 = x1 = y1 = -1;
                        break;
                    case 1://Linea recta
                        lienzoFondo.drawLine(x0, y0, x1, y1, pincel);
                        break;
                    case 2://Circulo
                        x1 = x;
                        y1 = y;
                        radio = Math.sqrt(Math.pow((x0 - x1), 2) + Math.pow((y0 - y1), 2));
                        lienzoFondo.drawCircle(x0, y0, (float) radio, pincel);
                        break;
                    case 3://Rectangulo
                        x1 = x;
                        y1 = y;
                        lienzoFondo.drawRect(x0, y0, x1, y1, pincel);
                        break;
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mapaDeBits = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(mapaDeBits);
        alto = h;
        ancho = w;
    }

    public void color() {
        new ColorPicker(this.getContext(), Vista.this, Color.BLACK).show();
    }


    @Override
    public void colorChanged(int color) {
        //pincel.setColor(color);
        this.color = color;
    }

    public void setAccion(int accion) {
        this.accion = accion;
    }

    class Recta {
        public float x0, y0, xi, yi;

        Recta(float x0, float y0, float xi, float yi) {
            this.x0 = x0;
            this.y0 = y0;
            this.xi = xi;
            this.yi = yi;
        }
    }

    public void save() {
        Bitmap imagen = getBitmap(lienzoFondo);
        File carpeta = new File(Environment.getExternalStorageDirectory().getPath());
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String formatteDate = df.format(date);
        String nombre = "PAINT_" + formatteDate+".png";
        try {
            File archivo = new File(carpeta, nombre);
            FileOutputStream fos = new FileOutputStream(archivo);
            imagen.compress(Bitmap.CompressFormat.PNG, 0, fos);
            addFoto2Gallery(archivo);
            Toast.makeText(c,"Dibujo guardado",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addFoto2Gallery(File f) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(f);
        intent.setData(uri);
        c.sendBroadcast(intent);
    }

    public static Bitmap getBitmap(Canvas canvas) {
        try {
            java.lang.reflect.Field field = Canvas.class.getDeclaredField("mBitmap");
            field.setAccessible(true);
            return (Bitmap)field.get(canvas);
        }
        catch (Throwable t) {
            return null;
        }
    }

}
