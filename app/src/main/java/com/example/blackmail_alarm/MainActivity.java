package com.example.blackmail_alarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Open secret data" menu option
            case R.id.action_open_repository:
                Intent intent = new Intent(MainActivity.this,SecretRepository.class);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete all alarms" menu option
            case R.id.action_delete_all_alarms:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
