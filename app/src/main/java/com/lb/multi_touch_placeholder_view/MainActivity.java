package com.lb.multi_touch_placeholder_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
  {

  private MultiTouchImageView mMultiTouchImageView;

  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final MultiTouchContainer multiTouchContainer=(MultiTouchContainer)findViewById(R.id.container);
    mMultiTouchImageView=new MultiTouchImageView(this);
    final Bitmap movingBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.moving_image);
    final Bitmap background=decodeMutableBitmapFromResourceId(this,R.drawable.background);
    final ImageView backgroundImageView=(ImageView)findViewById(android.R.id.background);
    final int bgBitmapWidth=background.getWidth(), bgBitmapHeight=background.getHeight();

//    backgroundImageView.setAlpha(0.5f);
    backgroundImageView.setImageBitmap(background);
    multiTouchContainer.setOnTouchListener(new OnTouchListener()
    {
    @Override
    public boolean onTouch(final View v,final MotionEvent event)
      {
      return mMultiTouchImageView.onTouch(event);
      }
    });
    runJustBeforeBeingDrawn(multiTouchContainer,new Runnable()
    {
    @Override
    public void run()
      {
      final int bgViewWidth=multiTouchContainer.getWidth(), bgViewHeight=multiTouchContainer.getHeight();
      // handle the moving imageView
      mMultiTouchImageView.setMovingBitmap(movingBitmap);
      mMultiTouchImageView.setEnableGestures(true);
      mMultiTouchImageView.setOnClickListener(new OnClickListener()
      {
      @Override
      public void onClick(final View v)
        {
        Toast.makeText(MainActivity.this,"click",Toast.LENGTH_SHORT).show();
        }
      });
      mMultiTouchImageView.setClickable(false);
      //this is where you wish to put the boundaries of the moving imageView, in relation to the bitmap:
      final Rect boundingBox=new Rect(bgBitmapWidth/4,bgBitmapHeight/4,bgBitmapWidth*3/4,bgBitmapHeight*3/4);
      RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(bgViewWidth*boundingBox.width()/bgBitmapWidth,bgViewHeight*boundingBox.height()/bgBitmapHeight);
      layoutParams.leftMargin=bgViewWidth*boundingBox.left/bgBitmapWidth;
      layoutParams.topMargin=bgViewHeight*boundingBox.top/bgBitmapHeight;
      multiTouchContainer.addView(mMultiTouchImageView,0,layoutParams);
      findViewById(R.id.viewOutputBitmapButton).setOnClickListener(new View.OnClickListener()
      {
      @Override
      public void onClick(final View v)
        {
        Bitmap bmp2=Bitmap.createBitmap(bgBitmapWidth,bgBitmapHeight,Config.ARGB_8888);
        final Bitmap outputBitmap=multiTouchContainer.finishDrawing(bmp2);
        BitmapDisplayActivity._bitmap=outputBitmap;
        startActivity(new android.content.Intent(MainActivity.this,BitmapDisplayActivity.class));
        finish();
        }
      });
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

  /**
   * This method helps to retrieve the ui component size after it was create during the onCreate method
   *
   * @param view     - the view to get it's size
   * @param runnable
   */
  public static void runJustBeforeBeingDrawn(final View view,final Runnable runnable)
    {
    final OnPreDrawListener preDrawListener=new OnPreDrawListener()
    {
    @Override
    public boolean onPreDraw()
      {
      view.getViewTreeObserver().removeOnPreDrawListener(this);
      runnable.run();
      return true;
      }
    };
    view.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }
  }