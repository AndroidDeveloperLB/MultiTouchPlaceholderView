package com.lb.multi_touch_placeholder_view;
import android.content.Context;
import android.view.View;

public class MultiTouchContainer extends android.widget.FrameLayout
  {
  private MultiTouchView _multiTouchView;

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
  protected void onAttachedToWindow()
    {
    super.onAttachedToWindow();
    for(int i=0;i<getChildCount();++i)
      {
      View v=getChildAt(i);
      if(v instanceof MultiTouchView)
        {
        _multiTouchView=(MultiTouchView)v;
        break;
        }
      if(_multiTouchView==null)
        throw new IllegalStateException("needs a "+MultiTouchView.class.getName()+" instance inside this view");
      }
    }

  @Override
  public boolean onTouchEvent(final android.view.MotionEvent event)
    {
    return _multiTouchView.ontTouchFromContainer(event);
    }
  }
