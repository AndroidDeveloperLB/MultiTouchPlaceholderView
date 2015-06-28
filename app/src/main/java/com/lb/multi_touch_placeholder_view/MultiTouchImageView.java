package com.lb.multi_touch_placeholder_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.almeros.android.multitouch.BaseGestureDetector;
import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;
import com.almeros.android.multitouch.ShoveGestureDetector;

public class MultiTouchImageView extends ImageView
  {
  private final Paint _paint=new Paint();
  private final Matrix _movingBitmapMatrix=new Matrix();
  private float _scaleFactorX=1f, _scaleFactorY=1f, _rotationDegrees=0.f;
  private float _translateX=0.f, _translateY=0.f;
  private int _alpha=255, _movingImageHeight, _movingImageWidth;
  private Bitmap _movingBitmap;
  private float _minScale=0.1f, _maxScale=10f;

  private ScaleGestureDetector _scaleDetector;
  private BaseGestureDetector _rotateDetector, _moveDetector, _shoveDetector;
  private boolean _isScaling;
  private boolean _isRotating;
  private boolean _isMoving;
  private boolean _isShoving;
  private boolean _initialized;
  private boolean mEnableGestures=true;
  private GestureDetectorCompat mGestureDetector;

  public MultiTouchImageView(final Context context)
    {
    super(context);
    }

  public MultiTouchImageView(final Context context,final android.util.AttributeSet attrs)
    {
    super(context,attrs);
    }

  public MultiTouchImageView(final Context context,final android.util.AttributeSet attrs,final int defStyleAttr)
    {
    super(context,attrs,defStyleAttr);
    }

  @android.annotation.TargetApi(android.os.Build.VERSION_CODES.LOLLIPOP)
  public MultiTouchImageView(final Context context,final android.util.AttributeSet attrs,final int defStyleAttr,final int defStyleRes)
    {
    super(context,attrs,defStyleAttr,defStyleRes);
    }

  public void setMinScale(final float minScale)
    {
    if(_minScale==minScale)
      return;
    _minScale=minScale;
    float oldScaleFactorX=_scaleFactorX, oldScaleFactorY=_scaleFactorY;
    _scaleFactorX=Math.max(_minScale,Math.min(_scaleFactorX,_maxScale));
    _scaleFactorY=Math.max(_minScale,Math.min(_scaleFactorY,_maxScale));
    if(oldScaleFactorX!=_scaleFactorX||oldScaleFactorY!=_scaleFactorY)
      onTouch(null);

    }

  public void setMaxScale(final float maxScale)
    {
    if(_maxScale==maxScale)
      return;
    _maxScale=maxScale;
    float oldScaleFactorX=_scaleFactorX, oldScaleFactorY=_scaleFactorY;
    _scaleFactorX=Math.max(_minScale,Math.min(_scaleFactorX,_maxScale));
    _scaleFactorY=Math.max(_minScale,Math.min(_scaleFactorY,_maxScale));
    if(oldScaleFactorX!=_scaleFactorX||oldScaleFactorY!=_scaleFactorY)
      onTouch(null);
    }

//    public void setScaleFactor(final float scaleFactor) {
//        _scaleFactorX = _scaleFactorY = scaleFactor;
//        onTouch(null);
//    }

  public void setScaleFactor(float scaleFactorX,float scaleFactorY)
    {
    _scaleFactorX=Math.max(_minScale,Math.min(scaleFactorX,_maxScale));
    _scaleFactorY=Math.max(_minScale,Math.min(scaleFactorY,_maxScale));
    onTouch(null);
    }

  public float getScaleFactorX()
    {
    return _scaleFactorX;
    }

  public float getScaleFactorY()
    {
    return _scaleFactorY;
    }

  public void setTranslate(float translateX,float translateY)
    {
    _translateX=translateX;
    _translateY=translateY;
    onTouch(null);
    }

  public float getTranslateX()
    {
    return _translateX;
    }

  public float getTranslateY()
    {
    return _translateY;
    }

//    public void setScaleFactor(final float scaleFactor, boolean checkRange) {
//        float oldScaleFactor = _scaleFactor;
//        _scaleFactor = checkRange ? Math.max(_minScale, Math.min(scaleFactor, _maxScale)) : scaleFactor;
//        if (oldScaleFactor != _scaleFactor)
//            onTouch(null);
//    }

  @Override
  protected void onAttachedToWindow()
    {
    super.onAttachedToWindow();
    Context appContext=getContext().getApplicationContext();
    initGestures(appContext);
    }

  public void initGestures(final Context appContext)
    {
    _isMoving=_isRotating=_isScaling=_isShoving=false;
    mGestureDetector=new GestureDetectorCompat(appContext,new SimpleOnGestureListener()
    {
    @Override
    public boolean onSingleTapConfirmed(final MotionEvent e)
      {
      Rect rect=new Rect();
      getHitRect(rect);
      if(rect.contains((int)e.getX(),(int)e.getY()))
        return performClick();
      return false;
      }
    });
    _scaleDetector=new ScaleGestureDetector(appContext,new ScaleGestureDetector.SimpleOnScaleGestureListener()
    {
    @Override
    public boolean onScale(final ScaleGestureDetector detector)
      {
      if(_isShoving||!mEnableGestures)
        return true;
      _scaleFactorX=Math.max(_minScale,Math.min(_scaleFactorX*detector.getScaleFactor(),_maxScale));
      _scaleFactorY=Math.max(_minScale,Math.min(_scaleFactorY*detector.getScaleFactor(),_maxScale));
      _isScaling=true;
      return true;
      }
    });
    _rotateDetector=new RotateGestureDetector(appContext,new RotateGestureDetector.SimpleOnRotateGestureListener()
    {
    @Override
    public boolean onRotate(RotateGestureDetector detector)
      {
      if(_isShoving||!mEnableGestures)
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
      if(_isShoving||_isScaling||!mEnableGestures)
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
      if(!mEnableGestures)
        return true;
      _alpha=Math.min(255,Math.max(0,_alpha+(int)detector.getShovePixelsDelta()));
      _isShoving=true;
      return true;
      }
    });
    }

  public void setRotationDegrees(final float rotationDegrees)
    {
    this._rotationDegrees=rotationDegrees;
    onTouch(null);
    }

  @Override
  protected void onSizeChanged(final int w,final int h,final int oldW,final int oldH)
    {
    super.onSizeChanged(w,h,oldW,oldH);
    //init location and size
    if(!_initialized)
      {
      _initialized=true;
      initCenterCrop(w,h);
//            initTopLeftNoScale();
      onTouch(null);
      }
    }

//    private void initTopLeftNoScale() {
//        _scaleFactorX = _scaleFactorY = 1;
//        _translateX += (_movingImageWidth * _scaleFactorX) / 2;
//        _translateY += (_movingImageHeight * _scaleFactorY) / 2;
//    }

  public void initCenterCrop(int w,int h)
    {
    if(w==0||h==0)
      return;
    _scaleFactorX=_scaleFactorY=(float)Math.max(w,h)/Math.min(_movingImageHeight,_movingImageWidth);
    _translateX=w/2; // because we need : (_movingImageWidth * _scaleFactorX) / 2 + (w - _movingImageWidth * _scaleFactorX) / 2;
    _translateY=h/2;  // because we need : (_movingImageHeight * _scaleFactorY) / 2 + (h - _movingImageHeight * _scaleFactorY) / 2;
    }

  public boolean onTouch(final MotionEvent event)
    {
    if(!_initialized)
      return true;
    if(event!=null)
      {
      requestFocus();
      boolean handledUsingNormalGestureDetector=mGestureDetector.onTouchEvent(event);
      if(!handledUsingNormalGestureDetector)
        {
        _scaleDetector.onTouchEvent(event);
        _rotateDetector.onTouchEvent(event);
        _shoveDetector.onTouchEvent(event);
        _moveDetector.onTouchEvent(event);
        }
      final int action=event.getAction();
      if(action==MotionEvent.ACTION_CANCEL||action==MotionEvent.ACTION_UP)
        _isShoving=false;
      else if(action==MotionEvent.ACTION_DOWN)
        {
        requestFocus();
        final InputMethodManager imm=(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(),0);
        }
      _isMoving=_isRotating=_isScaling=false;
      if(handledUsingNormalGestureDetector)
        return true;
      }
    float scaledImageCenterX=(_movingImageWidth*_scaleFactorX)/2;
    float scaledImageCenterY=(_movingImageHeight*_scaleFactorY)/2;
    _movingBitmapMatrix.reset();
    _movingBitmapMatrix.postScale(_scaleFactorX,_scaleFactorY);
    _movingBitmapMatrix.postRotate(_rotationDegrees,scaledImageCenterX,scaledImageCenterY);
    _movingBitmapMatrix.postTranslate(_translateX-scaledImageCenterX,_translateY-scaledImageCenterY);
    _paint.setAlpha(_alpha);
    invalidate();
    return true;
    }

  /**
   * sets the bitmap to be used for moving,scaling,rotating and alpha changing
   */
  public void setMovingBitmap(Bitmap movingBitmap)
    {
    _movingBitmap=movingBitmap;
    boolean bitmapSizeChanged=false;
    if(_movingImageHeight!=(_movingImageHeight=movingBitmap.getHeight()))
      bitmapSizeChanged=true;
    if(_movingImageWidth!=(_movingImageWidth=movingBitmap.getWidth()))
      bitmapSizeChanged=true;
    if(bitmapSizeChanged)
      if(getWidth()!=0&&getHeight()!=0)
        initCenterCrop(getWidth(),getHeight());
      else onTouch(null);
    }

  @android.annotation.TargetApi(android.os.Build.VERSION_CODES.HONEYCOMB)
  @Override
  protected void onDraw(final Canvas canvas)
    {
    if(_movingBitmap!=null)
      canvas.drawBitmap(_movingBitmap,_movingBitmapMatrix,_paint);
    super.onDraw(canvas);
    }

  public void setEnableGestures(final boolean enableGestures)
    {
    mEnableGestures=enableGestures;
    }
  }
