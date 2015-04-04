package com.lb.multi_touch_placeholder_view;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.almeros.android.multitouch.BaseGestureDetector;
import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;
import com.almeros.android.multitouch.ShoveGestureDetector;

public class MultiTouchView extends ImageView
  {
  private final Paint _paint=new Paint();
  private final Matrix _movingBitmapMatrix=new Matrix();
  private float _scaleFactor=1f, _rotationDegrees=0.f;
  private float _translateX=0.f, _translateY=0.f;
  private int _alpha=255, _backgroundImageHeight, _backgroundImageWidth;
  private Bitmap _movingBitmap, _background;
  private boolean _isDrawingEnabled=true;
  private float _minScale=0.1f, _maxScale=10f;

  private ScaleGestureDetector _scaleDetector;
  private BaseGestureDetector _rotateDetector, _moveDetector, _shoveDetector;
  private boolean _isScaling;
  private boolean _isRotating;
  private boolean _isMoving;
  private boolean _isShoving;

  public MultiTouchView(final android.content.Context context)
    {
    super(context);
    }

  public MultiTouchView(final android.content.Context context,final android.util.AttributeSet attrs)
    {
    super(context,attrs);
    }

  public MultiTouchView(final android.content.Context context,final android.util.AttributeSet attrs,final int defStyleAttr)
    {
    super(context,attrs,defStyleAttr);
    }

  @android.annotation.TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
  public MultiTouchView(final android.content.Context context,final android.util.AttributeSet attrs,final int defStyleAttr,final int defStyleRes)
    {
    super(context,attrs,defStyleAttr,defStyleRes);
    }

  public void setMinScale(final float minScale)
    {
    _minScale=minScale;
    float oldScaleFactor=_scaleFactor;
    if(oldScaleFactor!=(_scaleFactor=Math.max(_minScale,Math.min(_scaleFactor,_maxScale))))
      ontTouchFromContainer(null);
    }

  public void setMaxScale(final float maxScale)
    {
    _maxScale=maxScale;
    float oldScaleFactor=_scaleFactor;
    if(oldScaleFactor!=(_scaleFactor=Math.max(_minScale,Math.min(_scaleFactor,_maxScale))))
      ontTouchFromContainer(null);
    }

  public void setTranslate(float translateX,float translateY)
    {
    _translateX=translateX;
    _translateY=translateY;
    ontTouchFromContainer(null);
    }

  public void setScaleFactor(final float scaleFactor)
    {
    float oldScaleFactor=_scaleFactor;
    _scaleFactor=Math.max(_minScale,Math.min(scaleFactor,_maxScale));
    if(oldScaleFactor!=_scaleFactor)
      ontTouchFromContainer(null);
    }

  @Override
  protected void onAttachedToWindow()
    {
    super.onAttachedToWindow();
    android.content.Context appContext=getContext().getApplicationContext();
    _scaleDetector=new ScaleGestureDetector(appContext,new ScaleGestureDetector.SimpleOnScaleGestureListener()
    {
    @Override
    public boolean onScale(final ScaleGestureDetector detector)
      {
      if(_isShoving)
        return true;
      _scaleFactor=Math.max(_minScale,Math.min(_scaleFactor*detector.getScaleFactor(),_maxScale));
      _isScaling=true;
      return true;
      }
    });
    _rotateDetector=new RotateGestureDetector(appContext,new RotateGestureDetector.SimpleOnRotateGestureListener()
    {
    @Override
    public boolean onRotate(RotateGestureDetector detector)
      {
      if(_isShoving)
        return true;
      _rotationDegrees-=detector.getRotationDegreesDelta();
      _isRotating=true;
      return true;
      }
    });
    _moveDetector=new MoveGestureDetector(appContext,new MoveGestureDetector.SimpleOnMoveGestureListener()
    {
    @Override
    public boolean onMove(MoveGestureDetector detector)
      {
      if(_isShoving||_isScaling)
        return true;
      android.graphics.PointF d=detector.getFocusDelta();
      _translateX+=d.x;
      _translateY+=d.y;
      _isMoving=true;
      return true;
      }
    });
    _shoveDetector=new ShoveGestureDetector(appContext,new ShoveGestureDetector.SimpleOnShoveGestureListener()
    {
    @Override
    public boolean onShove(ShoveGestureDetector detector)
      {
      _alpha=Math.min(255,Math.max(0,_alpha+(int)detector.getShovePixelsDelta()));
      _isShoving=true;
      android.util.Log.d("AppLog","onShove:"+_isShoving);
      return true;
      }
    });
    }

  public boolean ontTouchFromContainer(MotionEvent event)
    {
    android.util.Log.d("AppLog","_isShoving:"+_isShoving);
    if(event!=null)
      {
      _scaleDetector.onTouchEvent(event);
      _rotateDetector.onTouchEvent(event);
      _shoveDetector.onTouchEvent(event);
      _moveDetector.onTouchEvent(event);
      final int action=event.getAction();
      if(action==android.view.MotionEvent.ACTION_CANCEL||action==android.view.MotionEvent.ACTION_UP)
        _isShoving=false;
      _isMoving=_isRotating=_isScaling=false;
      }
    float scaledImageCenterX=(_backgroundImageWidth*_scaleFactor)/2;
    float scaledImageCenterY=(_backgroundImageHeight*_scaleFactor)/2;
    _movingBitmapMatrix.reset();
    _movingBitmapMatrix.postScale(_scaleFactor,_scaleFactor);
    _movingBitmapMatrix.postRotate(_rotationDegrees,scaledImageCenterX,scaledImageCenterY);
    _movingBitmapMatrix.postTranslate(_translateX-scaledImageCenterX,_translateY-scaledImageCenterY);
    _paint.setAlpha(_alpha);
    invalidate();
    return true;
    }

  public void setBitmaps(Bitmap background,Bitmap movingBitmap)
    {
    if(!background.isMutable())
      throw new IllegalArgumentException("background bitmap must be mutable (to write into it later)");
    _background=background;
    setImageBitmap(background);
    _movingBitmap=movingBitmap;
    _backgroundImageHeight=movingBitmap.getHeight();
    _backgroundImageWidth=movingBitmap.getWidth();
    invalidate();
    }

  public Bitmap finishDrawing()
    {
    _isDrawingEnabled=false;
    Canvas canvas=new android.graphics.Canvas(_background);
    _paint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
    canvas.drawBitmap(_movingBitmap,_movingBitmapMatrix,_paint);
    return _background;
    }

  @android.annotation.TargetApi(android.os.Build.VERSION_CODES.HONEYCOMB)
  @Override
  protected void onDraw(final Canvas canvas)
    {
    if(_isDrawingEnabled&&_movingBitmap!=null)
      canvas.drawBitmap(_movingBitmap,_movingBitmapMatrix,_paint);
    super.onDraw(canvas);
    }
  }
