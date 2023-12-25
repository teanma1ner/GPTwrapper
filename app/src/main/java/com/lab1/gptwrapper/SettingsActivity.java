package com.lab1.gptwrapper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    private Button saveButton;
    private EditText textEditAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveButton = findViewById(R.id.seveSettingsButton);
        textEditAPI = findViewById(R.id.editTextAPIKey);

        Context context = this;

        String oldAPIKey = App.getInstance().getOpenAiAPIkey();
        textEditAPI.setText(oldAPIKey);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curStr = textEditAPI.getText().toString();
                if (!curStr.equals("") && !curStr.equals(oldAPIKey)) {
                    App.getInstance().setOpenAiAPIkey(curStr);
                }
                try {
                    if (App.getInstance().getopenAIAPI().connectTest()){
                        finish();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(true)
                                .setMessage("Attention")
                                .setTitle("API key is not valid")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i("AlertDialog", "on positive button click");
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}