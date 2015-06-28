package com.lb.multi_touch_placeholder_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MultiTouchContainer extends RelativeLayout
  {
  public MultiTouchContainer(final Context context)
    {
    super(context);
    }

  public MultiTouchContainer(final Context context,final android.util.AttributeSet attrs)
    {
    super(context,attrs);
    }

  public MultiTouchContainer(final Context context,final android.util.AttributeSet attrs,final int defStyleAttr)
    {
    super(context,attrs,defStyleAttr);
    }

  @android.annotation.TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
  public MultiTouchContainer(final Context context,final android.util.AttributeSet attrs,final int defStyleAttr,final int defStyleRes)
    {
    super(context,attrs,defStyleAttr,defStyleRes);
    }

  @Override
  protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec)
    {
    super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    int width=getMeasuredWidth(), height=getMeasuredHeight();
    int size=Math.min(width,height);
    setMeasuredDimension(size,size);
    }

  protected void prepareDrawingOfChildToBitmap(View view,int bitmapWidth,int bitmapHeight,int layoutWidth,int layoutHeight)
    {
    view.setAlpha(1);
    if(view instanceof EditText)
      {
      //make the EditText look like a normal TextView
      EditText editText=(EditText)view;
      editText.setTextColor(editText.getCurrentTextColor());
      editText.setCursorVisible(false);
      editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,bitmapHeight*editText.getTextSize()/layoutHeight);
      editText.setBackgroundColor(Color.TRANSPARENT);
      }
    if(view instanceof MultiTouchImageView)
      {
      MultiTouchImageView multiTouchImageView=(MultiTouchImageView)view;
      multiTouchImageView.setTranslate(bitmapWidth*multiTouchImageView.getTranslateX()/layoutWidth,bitmapHeight*multiTouchImageView.getTranslateY()/layoutHeight);
      multiTouchImageView.setScaleFactor(bitmapWidth*multiTouchImageView.getScaleFactorX()/layoutWidth,bitmapHeight*multiTouchImageView.getScaleFactorY()/layoutHeight);
      }
    final LayoutParams layoutParams=(LayoutParams)view.getLayoutParams();
    layoutParams.leftMargin=bitmapWidth*layoutParams.leftMargin/layoutWidth;
    layoutParams.topMargin=bitmapHeight*layoutParams.topMargin/layoutHeight;
    layoutParams.rightMargin=bitmapWidth*layoutParams.rightMargin/layoutWidth;
    layoutParams.bottomMargin=bitmapHeight*layoutParams.bottomMargin/layoutHeight;
    int currentWidth=view.getId()==android.R.id.background?view.getWidth():layoutParams.width;
    layoutParams.width=currentWidth<=0?currentWidth:bitmapWidth*currentWidth/layoutWidth;
    int currentHeight=view.getId()==android.R.id.background?view.getHeight():layoutParams.height;
    layoutParams.height=currentHeight<=0?currentHeight:bitmapHeight*currentHeight/layoutHeight;
    view.setLayoutParams(layoutParams);
    }

  public Bitmap finishDrawing(final Bitmap bitmap)
    {
    Canvas canvas=new Canvas(bitmap);
    int childCount=getChildCount();
    final int layoutWidth=getWidth(), layoutHeight=getHeight();
    final int bitmapWidth=bitmap.getWidth(), bitmapHeight=bitmap.getHeight();
    ViewGroup parent=(ViewGroup)getParent();
    parent.removeView(this);
    for(int i=0;i<childCount;++i)
      {
      final View view=getChildAt(i);
      prepareDrawingOfChildToBitmap(view,bitmapWidth,bitmapHeight,layoutWidth,layoutHeight);
      }
    int measureWidth=MeasureSpec.makeMeasureSpec(bitmapWidth,MeasureSpec.EXACTLY);
    int measuredHeight=MeasureSpec.makeMeasureSpec(bitmapHeight,MeasureSpec.EXACTLY);
    measure(measureWidth,measuredHeight);
    layout(0,0,bitmapWidth,bitmapHeight);
    draw(canvas);
    return bitmap;
    }

  }

