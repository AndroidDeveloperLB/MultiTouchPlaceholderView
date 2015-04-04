package com.lb.multi_touch_placeholder_view;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class BitmapDisplayActivity extends android.app.Activity
  {
  public static Bitmap _bitmap;

  @Override
  protected void onCreate(final android.os.Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bitmap_display);
    ImageView imageView=(ImageView)findViewById(R.id.imageView);
    imageView.setImageBitmap(_bitmap);
    }
  }
