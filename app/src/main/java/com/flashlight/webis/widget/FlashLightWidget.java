package com.flashlight.webis.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import com.flashlight.webis.R;

/**
 * Created by Hien on 7/12/2016.
 */
public class FlashLightWidget extends AppWidgetProvider {
    public static boolean lightOn = false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if(lightOn) {
            // Flash light active
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            views.setImageViewBitmap(R.id.button_switch_widget, bitmap);

            // Create the intent, set the action
            Intent underlyingIntent = new Intent(context, FlashlightIntentService.class);
            underlyingIntent.setAction(FlashlightIntentService.ACTION_TURN_OFF);

            // Create the pending intent
            PendingIntent turnOffIntent = PendingIntent.getService(context, 1,
                    underlyingIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.button_switch_widget, turnOffIntent);

        } else {
            // Flash light off
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.widget_off);
            views.setImageViewBitmap(R.id.button_switch_widget, bitmap);

            // Create the intent, set the action
            Intent underlyingIntent = new Intent(context, FlashlightIntentService.class);
            underlyingIntent.setAction(FlashlightIntentService.ACTION_TURN_ON);

            // Create the pending intent
            PendingIntent turnOnIntent = PendingIntent.getService(context, 1,
                    underlyingIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.button_switch_widget, turnOnIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}



