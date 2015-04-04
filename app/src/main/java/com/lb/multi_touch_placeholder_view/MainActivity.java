package com.lb.multi_touch_placeholder_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity
  {
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final MultiTouchContainer multiTouchContainer=(MultiTouchContainer)findViewById(R.id.container);
    final MultiTouchView multiTouchView=(MultiTouchView)findViewById(R.id.multiTouchView);
    final Bitmap movingBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.moving_image);
    final Bitmap background=decodeMutableBitmapFromResourceId(this,R.drawable.background);
    multiTouchView.setBitmaps(background,movingBitmap);
    float scaleFactor=1f;
    multiTouchView.setScaleFactor(scaleFactor);
    multiTouchView.setTranslate(background.getWidth()/2,background.getHeight()/2);
    findViewById(R.id.viewOutputBitmapButton).setOnClickListener(new View.OnClickListener()
    {
    @Override
    public void onClick(final View v)
      {
      Bitmap outputBitmap=multiTouchView.finishDrawing();
      BitmapDisplayActivity._bitmap=background;
      startActivity(new android.content.Intent(MainActivity.this,BitmapDisplayActivity.class));
      finish();
      }
    });
    }

  public static Bitmap decodeMutableBitmapFromResourceId(final Context context,final int bitmapResId)
    {
    final Options bitmapOptions=new Options();
    if(VERSION.SDK_INT>=VERSION_CODES.HONEYCOMB)
      bitmapOptions.inMutable=true;
    Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),bitmapResId,bitmapOptions);
    return bitmap;
    }

  @Override
  public boolean onCreateOptionsMenu(final android.view.Menu menu)
    {
    getMenuInflater().inflate(R.menu.activity_main,menu);
    return super.onCreateOptionsMenu(menu);
    }

  @SuppressWarnings("deprecation")
  @Override
  public boolean onOptionsItemSelected(final android.view.MenuItem item)
    {
    String url=null;
    switch(item.getItemId())
      {
      case R.id.menuItem_all_my_apps:
        url="https://play.google.com/store/apps/developer?id=AndroidDeveloperLB";
        break;
      case R.id.menuItem_all_my_repositories:
        url="https://github.com/AndroidDeveloperLB";
        break;
      case R.id.menuItem_current_repository_website:
        url="https://github.com/AndroidDeveloperLB/MultiTouchPlaceholderView";
        break;
      }
    if(url==null)
      return true;
    final android.content.Intent intent=new android.content.Intent(android.content.Intent.ACTION_VIEW,android.net.Uri.parse(url));
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY|android.content.Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK|android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    startActivity(intent);
    return true;
    }
  }