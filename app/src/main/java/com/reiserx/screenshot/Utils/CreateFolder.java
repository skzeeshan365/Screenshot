package com.reiserx.screenshot.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.reiserx.screenshot.Interfaces.FolderCreation;
import com.reiserx.screenshot.R;

public class CreateFolder {
    Context context;
    String Path;

    public CreateFolder(Context context, String path) {
        this.context = context;
        Path = path;
    }

    public void create(FolderCreation listener) {
        // Create a layout inflater to inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.single_edittext_dialog, null);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        EditText editText = customView.findViewById(R.id.edittext);

        builder.setPositiveButton("save", (dialogInterface, i) -> {
            if (editText.getText().toString().trim().equals(""))
                Toast.makeText(context, "Please enter a folder name", Toast.LENGTH_SHORT).show();
            else {
                SaveBitmap.createDirectoryInDCIM(editText.getText().toString());
                listener.onDismiss(true);
                Toast.makeText(context, "Folder created successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("cancel", null);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}
