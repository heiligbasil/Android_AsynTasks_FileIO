package com.lambdaschool.android_async_task_file_io;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileIO {
    private Context context;

    public FileIO(Context context) {
        this.context = context;
    }

    public String readFile(String fileName) {
        File fileToRead = new File(getStorageDirectory(), fileName);
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(fileToRead);
            int next = fileReader.read();
            while (next != -1) {
                stringBuilder.append((char) next);
                next = fileReader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    public String readFileBuffered(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = fileName.endsWith(MainActivity.ASSET_EXTENSION) ? context.getAssets().open(fileName) : new FileInputStream(new File(context.getCacheDir(), fileName).getPath());

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String lineByLine = bufferedReader.readLine();

            do {
                stringBuilder.append(lineByLine);
                lineByLine = bufferedReader.readLine();
            } while (lineByLine != null);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    public Byte[] readFileBytes(String fileName) {
        FileInputStream fileInputStream = null;
        ArrayList<Byte> byteArrayList = new ArrayList<>();

        try {
            File fileToRead = new File(getStorageDirectory(), fileName);
            fileInputStream = new FileInputStream(fileToRead.getPath());
            byte byteToRead;

            do {
                byteToRead = (byte) fileInputStream.read();
                byteArrayList.add(byteToRead);
            } while (byteToRead != -1);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        final Byte[] data = new Byte[byteArrayList.size()];
        byteArrayList.toArray(data);
        return data;
    }

    public void writeFile(String fileName, String textToWrite) {
        FileWriter fileWriter = null;

        try {
            File fileToWrite = File.createTempFile(fileName, null, getStorageDirectory());
            fileWriter = new FileWriter(fileToWrite);
            fileWriter.write(textToWrite);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeFile(String fileName, byte[] byteArray) {
        FileOutputStream fileOutputStream = null;

        try {
            File fileToWrite = new File(getStorageDirectory(), fileName);
            String fileToWritePath = fileToWrite.getPath();
            fileOutputStream = new FileOutputStream(fileToWritePath);
            fileOutputStream.write(byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeFile(String fileName, Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            bitmap.recycle();

            File fileToWrite = new File(getStorageDirectory(), fileName);
            String fileToWritePath = fileToWrite.getPath();
            fileOutputStream = new FileOutputStream(fileToWritePath);
            fileOutputStream.write(byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> getFileList() {
        ArrayList<String> filesStringArrayList = new ArrayList<>();
        String[] filesStringArray = getStorageDirectory().list();

        for (String name : filesStringArray) {
            if (name.contains(MainActivity.ASSET_EXTENSION)) {
                filesStringArrayList.add(name);
            }
        }
        return filesStringArrayList;
    }

    private File getStorageDirectory() {
        // get cache directory
        return context.getCacheDir();

        // get Internal directory
        //return context.getFilesDir();

        // get External Directory
        /*if(isExternalStorageWritable()) {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "subDir");
            if(!directory.mkdirs()) {
                // didn't create
            }
            return directory;
        } else {
            return context.getCacheDir();
        }*/
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
