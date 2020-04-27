package com.lambdaschool.android_async_task_file_io;

import android.content.Context;
import android.os.AsyncTask;
import androidx.core.content.res.TypedArrayUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final String EMPTY_STRING = "";
    public static final String ASSET_EXTENSION = ".txt";
    public static final String DEFAULT_CIPHER = "default.txt";
    EditText editTextShifts;
    TextView textViewCipher;
    ProgressBar progressBar;
    Button buttonDecrypt;
    Button buttonWrite;
    AsyncTask cipher;


    @Override
    protected void onStop() {
        super.onStop();
        cipher.cancel(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;
        textViewCipher = findViewById(R.id.text_view_cipher);

        String[] assetStringArray;
        ArrayList<String> assetArrayList = new ArrayList<>();
        try {
            assetArrayList.addAll(Arrays.asList(getAssets().list(EMPTY_STRING)));
            assetArrayList.addAll(Arrays.asList(getCacheDir().list()));
            assetArrayList.add(0, DEFAULT_CIPHER);
            ArrayList<String> assetsToRemove = new ArrayList<>();

            for (String asset : assetArrayList) {
                if (!asset.contains(ASSET_EXTENSION)) {
                    assetsToRemove.add(asset);
                }
            }
            assetArrayList.removeAll(assetsToRemove);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, assetArrayList);
        stringArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        final Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(stringArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String assetText = null;
                String itemSelected = parent.getItemAtPosition(position).toString();

                if (itemSelected.equals(DEFAULT_CIPHER)) {
                    assetText = getString(R.string.cipher);
                } else {
                    FileIO fileIO = new FileIO(context);
                    assetText = fileIO.readFileBuffered(itemSelected);
                }
                textViewCipher.setText(assetText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        editTextShifts = findViewById(R.id.edit_text_shifts);

        progressBar = findViewById(R.id.progress_bar);

        buttonDecrypt = findViewById(R.id.button_decrypt);
        buttonDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToDecrypt = textViewCipher.getText().toString();
                String numberOfShifts = editTextShifts.getText().toString();
                cipher = (new Cipher()).execute(textToDecrypt, numberOfShifts);
            }
        });

        buttonWrite = findViewById(R.id.button_write);
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileIO fileIO = new FileIO(context);
                fileIO.writeFile(spinner.getSelectedItem().toString(), textViewCipher.getText().toString());
            }
        });
    }

    public class Cipher extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonDecrypt.setEnabled(false);
            buttonWrite.setEnabled(false);
            progressBar.setMax(textViewCipher.getText().length());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder shiftedText = new StringBuilder();

            for (int i = 0; i < strings[0].length(); i++) {
                int oldValueOfChar = (int) strings[0].charAt(i);
                int newValueOfChar = oldValueOfChar;
                int numberOfShifts = Integer.parseInt(strings[1]);
                if ((oldValueOfChar >= 65 && oldValueOfChar <= 90) || (oldValueOfChar >= 97 && oldValueOfChar <= 122)) {
                    for (int j = 1; j <= numberOfShifts; j++) {
                        newValueOfChar += 1;
                        if (newValueOfChar > 90 && newValueOfChar < 97) {
                            newValueOfChar = 65;
                        } else if (newValueOfChar > 122) {
                            newValueOfChar = 97;
                        }
                    }
                }
                shiftedText.append(Character.toString((char) newValueOfChar));
                publishProgress(i);

                if (isCancelled()) {
                    break;
                }
            }
            return shiftedText.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String shiftedText) {
            super.onPostExecute(shiftedText);
            textViewCipher.setText(shiftedText);
            progressBar.setVisibility(View.GONE);
            buttonDecrypt.setEnabled(true);
            buttonWrite.setEnabled(true);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            progressBar.setVisibility(View.GONE);
            buttonDecrypt.setEnabled(true);
            buttonWrite.setEnabled(true);
        }
    }
}
