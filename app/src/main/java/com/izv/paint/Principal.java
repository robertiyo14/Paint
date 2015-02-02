package com.izv.paint;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;


public class Principal extends Activity {

    Vista v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new Vista(this);
        setContentView(v);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color:
                v.color();
                return true;
            case R.id.circle:
                v.setAccion(2);
                return true;
            case R.id.line:
                v.setAccion(1);
                return true;
            case R.id.hand:
                v.setAccion(0);
                return true;
            case R.id.rectangle:
                v.setAccion(3);
                return true;
            case R.id.eraser:
                v.setAccion(4);
                return true;
            case R.id.save:
                v.save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
