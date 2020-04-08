package ru.gosarcho.finder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ConnectTask extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    private ProgressDialog dialog;
    private AlertDialog alertDialog;
    private Context context;
    private boolean isFailed;

    ConnectTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isFailed = false;
        dialog = new ProgressDialog(context);
        dialog.setMessage("Подключение");
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        URLConnection connection;
        BufferedReader reader;
        try {
            URL url = new URL(params[0]);
            connection = url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
            isFailed = true;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog.dismiss();
        if (!isFailed) {
            delegate.processFinish(result);
        } else {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Ошибка");
            alertDialog.setMessage(result);
            alertDialog.show();
        }
    }
}
