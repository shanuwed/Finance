/*
 * Copyright (C) 2008 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.washington.shan.util;

import edu.washington.shan.Consts;
import edu.washington.shan.R;
import edu.washington.shan.WebviewActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;


public class UIUtilities {
    private UIUtilities() {
    }
    
    public static void browse(Context context, String title, String url) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getResources().getString(R.string.pref_filename), 
                Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("settings_use_external_browser", false)) {
            // start the url in the default browser
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } else {
            // start the url in Webview
            Intent i = new Intent(context, WebviewActivity.class);
            i.putExtra(Consts.WEBVIEW_TITLE, title);
            i.putExtra(Consts.WEBVIEW_URL, url);
            context.startActivity(i);
        }
    }

    public static void showImageToast(Context context, int id, Drawable drawable,
            int inflateId, int textViewId, int imageViewId) {
        final View view = LayoutInflater.from(context).inflate(inflateId, null);
        ((TextView) view.findViewById(textViewId)).setText(id);
        ((ImageView) view.findViewById(imageViewId)).setImageDrawable(drawable);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);

        toast.show();
    }

    public static void showToast(Context context, int id) {
        showToast(context, id, false);
    }

    public static void showToast(Context context, int id, boolean longToast) {
        Toast.makeText(context, id, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void showFormattedImageToast(Context context, int id, Drawable drawable,
            int inflateId, int textViewId, int imageViewId, Object... args) {

        final View view = LayoutInflater.from(context).inflate(inflateId, null);
        ((TextView) view.findViewById(textViewId)).setText(
                String.format(context.getText(id).toString(), args));
        ((ImageView) view.findViewById(imageViewId)).setImageDrawable(drawable);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);

        toast.show();
    }

    public static void showFormattedToast(Context context, int id, Object... args) {
        Toast.makeText(context, String.format(context.getText(id).toString(), args),
                Toast.LENGTH_LONG).show();
    }
}
