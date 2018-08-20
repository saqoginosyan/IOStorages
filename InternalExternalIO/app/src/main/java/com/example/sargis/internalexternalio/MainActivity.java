package com.example.sargis.internalexternalio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final int CODE = 10;
    private EditText fileName;
    private EditText fileContent;
    private String path;
    private String data;
    private RadioButton externalSelect;
    private RadioButton internalSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button saveButton = findViewById(R.id.save_button);
        final Button readButton = findViewById(R.id.read_button);
        internalSelect = findViewById(R.id.internal_storage);
        externalSelect = findViewById(R.id.external_storage);
        fileName = findViewById(R.id.file_name);
        fileContent = findViewById(R.id.file_content);

        permission();
        saveOnClick(saveButton);
        readOnClick(readButton);
    }

    private String initPath() {
        if (internalSelect.isChecked()) {
            path = getFilesDir().getAbsolutePath() + "/" + fileName.getText().toString();
        } else if (externalSelect.isChecked()) {
            path = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                    "/" + fileName.getText().toString();
        }
        return path;
    }

    private void saveOnClick(final Button saveButton) {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void readOnClick(final Button readButton) {
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    readFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void writeFile() throws IOException {
        if (initPath() != null && !fileName.getText().toString().isEmpty()) {
            final File fileW = new File(initPath());
            final FileOutputStream fileOutputStream = new FileOutputStream(fileW);
            fileOutputStream.write(fileContent.getText().toString().getBytes());
            fileOutputStream.close();
        } else {
            Toast.makeText(MainActivity.this, "Please enter file name and select storage", Toast.LENGTH_LONG).show();
        }
    }

    public void readFile() throws IOException {
        if (initPath() != null && !fileName.getText().toString().isEmpty()) {
            final File fileR = new File(initPath());
            if (fileR.isFile()) {
                final FileInputStream fileInputStream = new FileInputStream(fileR);
                final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
                String strLine;
                while ((strLine = bufferedReader.readLine()) != null) {
                    data = strLine;
                }
                dataInputStream.close();
                fileContent.setText(data);
            }
        } else {
            Toast.makeText(MainActivity.this, "NO SUCH FILE", Toast.LENGTH_LONG).show();
        }
    }

    private void permission() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE:
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    permission();
                }
        }
    }
}