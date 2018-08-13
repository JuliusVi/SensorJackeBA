package net.vinnen.sensorjackeba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import net.vinnen.sensorjackeba.thread.ConnectThread;

/**
 * Created by Julius on 24.07.2018.
 */

/**
 * This class handels the download dialog window, for when a file is downloaded from the esp
 */
public class DownloadDialog {
    public final Context context;
    public final ConnectThread thread;
    public String m_Text;
    public DownloadDialog(Context con, ConnectThread t) {
        context = con;
        thread=t;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Download File");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                thread.downloadFile(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
