package net.vinnen.sensorjackeba;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by Julius on 24.07.2018.
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
        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
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
