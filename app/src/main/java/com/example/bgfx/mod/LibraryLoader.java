package com.example.bgfx.mod;

import android.content.Context;
import java.io.*;
import android.widget.Toast;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class LibraryLoader {

    public static boolean load(Context context, String soPath) {
        try {
            File cacheDir = new File(context.getCacheDir(), "libs");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File source = new File(soPath);
            if (!source.exists()) {
                return false;
            }
            File target = new File(
                cacheDir,
                source.getName()
            );
            copyFile(source, target);
            target.setReadable(true, false);
            target.setExecutable(true, false);
            target.setWritable(true, false);
            System.load(target.getAbsolutePath());
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), 1).show();
            Toast.makeText(context, e.getMessage(), 1).show();
            Toast.makeText(context, e.getMessage(), 1).show();
            Toast.makeText(context, e.getMessage(), 1).show();
            /*AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(e.getMessage())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        dia.dismiss();
                    }
                });
            Dialog d = builder.create();
            d.show();*/
            return false;
        }
    }


    private static void copyFile(File src, File dst) throws IOException {
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);

        byte[] buffer = new byte[8192];
        int len;

        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }
}
