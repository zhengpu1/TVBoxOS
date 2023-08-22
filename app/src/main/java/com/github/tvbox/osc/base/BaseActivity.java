package com.github.tvbox.osc.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.PermissionChecker;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.HawkConfig;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.orhanobut.hawk.Hawk;

import java.io.File;

import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.internal.CustomAdapt;
import me.jessyan.autosize.utils.AutoSizeUtils;
import xyz.doikki.videoplayer.util.CutoutUtil;

/**
 * @author pj567
 * @date :2020/12/17
 * &#064;description:
 */
public abstract class BaseActivity extends AppCompatActivity implements CustomAdapt {
    protected static Context mContext;
    private LoadService mLoadService;


    private static float screenRatio = -100.0f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            if (screenRatio < 0) {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;
                int screenHeight = dm.heightPixels;
                screenRatio = (float) Math.max(screenWidth, screenHeight) / (float) Math.min(screenWidth, screenHeight);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }

        // takagen99 : Set Theme Color
        if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 0) {
            setTheme(R.style.NetfxTheme);
        } else if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 1) {
            setTheme(R.style.DoraeTheme);
        } else if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 2) {
            setTheme(R.style.PepsiTheme);
        } else if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 3) {
            setTheme(R.style.NarutoTheme);
        } else if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 4) {
            setTheme(R.style.MinionTheme);
        } else if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 5) {
            setTheme(R.style.YagamiTheme);
        } else {
            setTheme(R.style.SakuraTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        mContext = this;
        CutoutUtil.adaptCutoutAboveAndroidP(mContext, true);//设置刘海
        AppManager.getInstance().addActivity(this);
        init();
        setScreenOn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSysBar();
        changeWallpaper(false);
    }

    public static void LParams(@NonNull FrameLayout RIDD, int w, int h) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) RIDD.getLayoutParams();
        params.width = AutoSizeUtils.mm2px(mContext,w);
        params.height = AutoSizeUtils.mm2px(mContext,h);
        RIDD.setLayoutParams(params);
    }

    public static void LParamsa(@NonNull LinearLayout RIDD, int w, int h) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) RIDD.getLayoutParams();
        params.width = AutoSizeUtils.mm2px(mContext,w);
        params.height = AutoSizeUtils.mm2px(mContext,h);
        RIDD.setLayoutParams(params);
    }

    public static void colorTextView(TextView str, @NonNull LinearLayout esdrs) {
        esdrs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    str.setTextColor(mContext.getResources().getColor(R.color.color_FFF5E8DC));
                } else {
                    str.setTextColor(mContext.getResources().getColor(R.color.color_80FFFFFF));
                }
            }
        });
    }

    public static void drawableTextView(@NonNull TextView[] str, int esdrs) {
        for (TextView textView : str) {
            textView.setBackground(createDrawableSelector(mContext, esdrs));
        }
    }

    public static void drawableLinearLayout(@NonNull LinearLayout[] strs, int esdrs) {
        for (LinearLayout str : strs) {
            str.setBackground(createDrawableSelector(mContext, esdrs));
        }
    }
    public static void drawableLinearLayouts(@NonNull LinearLayout strs, int esdrs) {
            strs.setBackground(createDrawableSelector(mContext, esdrs));
    }
    public static void drawableTextViews(@NonNull TextView strs, int esdrs) {
        strs.setBackground(createDrawableSelector(mContext, esdrs));
    }

    public static void drawableFrameLayout(@NonNull FrameLayout[] strs, int esdrs) {
        for (FrameLayout str : strs) {
            str.setBackground(createDrawableSelector(mContext, esdrs));
        }
    }

    public static void drawableConstraintLayout(@NonNull ConstraintLayout[] strs, int esdrs) {
        for (ConstraintLayout str : strs) {
            str.setBackground(createDrawableSelector(mContext, esdrs));
        }
    }

    public static void drawableImageView(@NonNull ImageView[] strs, int esdrs) {
        for (ImageView str : strs) {
            str.setBackground(createDrawableSelector(mContext, esdrs));
        }
    }
    @NonNull
    public static Drawable createDrawableSelector(Context context, int bg) {
        StateListDrawable stateDrawable = new StateListDrawable();
        GradientDrawable normalDrawable = new GradientDrawable();
        GradientDrawable pressedDrawable = new GradientDrawable();
        GradientDrawable selectedDrawable = new GradientDrawable();

        int[][] states = new int[4][];
        states[0] = new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[3] = new int[]{android.R.attr.state_enabled, android.R.attr.state_selected}; // disabled state
        states[2] = new int[]{android.R.attr.state_enabled};
        int[] colors = { getColorByAttrId(context, R.attr.color_theme_70), getColorByAttrId(context, R.attr.color_theme_80), getColorByAttrId(context, R.attr.color_theme_70) };
        // 为各种状态下的 drawable 设置 attr 颜色值
        if (bg == 1) {
            pressedDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
            pressedDrawable.setColors(colors);
            if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 6) {
                normalDrawable.setColor(getColorByAttrId(context, R.attr.color_theme_20));
                normalDrawable.setStroke(1, mContext.getResources().getColor(R.color.color_33FFFFFF));
            } else {
                normalDrawable.setColor(mContext.getResources().getColor(R.color.color_32364E_40));
            }
            pressedDrawable.setStroke(2, getColorByAttrId(context, R.attr.color_theme_h));
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
        } else
        if (bg == 2) {
            normalDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_0));
            pressedDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
            pressedDrawable.setColors(colors);
            pressedDrawable.setStroke(2, getColorByAttrId(context, R.attr.color_theme_h));
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
        } else if (bg == 3) {
            int[] colorss = { getColorByAttrId(context, R.attr.color_theme_80), getColorByAttrId(context, R.attr.color_theme_70) };
            if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 6) {
                normalDrawable.setGradientCenter((float)0.5, (float)0.5);
                normalDrawable.setGradientRadius(AutoSizeUtils.mm2px(mContext,130));
                normalDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                normalDrawable.setColors(colorss);
                normalDrawable.setStroke(1, mContext.getResources().getColor(R.color.color_33FFFFFF));
            } else {
                normalDrawable.setColor(mContext.getResources().getColor(R.color.color_32364E_80));
            }
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
            pressedDrawable.setGradientCenter((float)0.5, (float)0.5);
            pressedDrawable.setGradientRadius(AutoSizeUtils.mm2px(mContext,100));
            pressedDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            selectedDrawable.setStroke(2, getColorByAttrId(context, R.attr.color_theme_h));
            selectedDrawable.setColors(colorss);
            selectedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
            pressedDrawable.setColors(colorss);
            pressedDrawable.setStroke(2, getColorByAttrId(context, R.attr.color_theme_h));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
        } else if (bg == 4) {
            Bitmap bitmap;
            if (globalWp == null) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.app_bg);
            } else {
                BitmapDrawable bd = globalWp;
                bitmap = bd.getBitmap();
            }
            return new BitmapDrawable(getRoundBitmapByShader(bitmap,1000,720,20,4));
        } else  if (bg == 5) {
            pressedDrawable.setOrientation(GradientDrawable.Orientation.TR_BL);
            normalDrawable.setColor(mContext.getResources().getColor(R.color.color_32364E_80));
            pressedDrawable.setColors(colors);
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
            pressedDrawable.setStroke(2, getColorByAttrId(context, R.attr.color_theme_h));
        } else if (bg == 6) {
            normalDrawable.setColor(getColorByAttrId(context, R.attr.color_theme_40));
            pressedDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
            pressedDrawable.setColors(colors);
            normalDrawable.setStroke(1, mContext.getResources().getColor(R.color.color_33FFFFFF));
            pressedDrawable.setStroke(2, getColorByAttrId(context, R.attr.color_theme_h));
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,2));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,2));
        } else if (bg == 7) {
            normalDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_0));
            pressedDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
            pressedDrawable.setColors(colors);
            pressedDrawable.setStroke(2, getColorByAttrId(context, R.attr.color_theme_h));
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,10));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,10));
        } else if (bg == 8) {
            normalDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_0));
            pressedDrawable.setColor(getColorByAttrId(context, R.attr.color_theme_40));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,50));
        } else if (bg == 9) {
            normalDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_0));
            pressedDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_s));
            selectedDrawable.setStroke(3, getColorByAttrId(context, R.attr.color_theme_h));
            selectedDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_s));
            pressedDrawable.setStroke(3, getColorByAttrId(context, R.attr.color_theme_h));
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
            selectedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,5));
        } else if (bg == 10) {
            normalDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_0));
            pressedDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_s));
            selectedDrawable.setStroke(3, getColorByAttrId(context, R.attr.color_theme_h));
            selectedDrawable.setColor(mContext.getResources().getColor(R.color.color_theme_s));
            pressedDrawable.setStroke(3, getColorByAttrId(context, R.attr.color_theme_h));
            normalDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,8));
            selectedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,8));
            pressedDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext,8));
        } else if (bg == 11) {
            Bitmap bitmap;
            if (globalWp == null) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.app_bg);
            } else {
                BitmapDrawable bd = globalWp;
                bitmap = bd.getBitmap();
            }
            return new BitmapDrawable(getRoundBitmapByShader(bitmap,1280,720,0,0));
        }

        stateDrawable.addState(states[0], pressedDrawable);
        stateDrawable.addState(states[1], pressedDrawable);
        stateDrawable.addState(states[3], selectedDrawable);
        stateDrawable.addState(states[2], normalDrawable);

        return stateDrawable;
    }

    @ColorInt
    public static int getColorByAttrId(Context context, @AttrRes int attrIdForColor) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrIdForColor, typedValue, true);
        return typedValue.data;
    }
    public int getThemeColor() {
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.themeColor);
        int themeColor = a.getColor(R.styleable.themeColor_color_theme, 0);
        return themeColor;
    }

    public static Bitmap getRoundBitmapByShader(Bitmap bitmap, int outWidth, int outHeight, int radius, int boarder) {
        if (bitmap == null) {
            return null;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float widthScale = outWidth * 1f / width;
        float heightScale = outHeight * 1f / height;
        Matrix matrix = new Matrix();
        matrix.setScale(widthScale, heightScale);
        Bitmap desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(desBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
        canvas.drawRoundRect(rect, radius, radius, paint);
        if (boarder  > 0) {
            Paint boarderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            boarderPaint.setColor(mContext.getResources().getColor(R.color.color_33FFFFFF));
            boarderPaint.setStyle(Paint.Style.STROKE);
            boarderPaint.setStrokeWidth(boarder);
            canvas.drawRoundRect(rect, radius, radius, boarderPaint);
        }
        return desBitmap;
    }

    public void hideSysBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
            uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    public void hideSystemUI(boolean shownavbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            uiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiVisibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            uiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            uiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            if (!shownavbar) {
                uiVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                uiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
            getWindow().getDecorView().setSystemUiVisibility(uiVisibility);
        }
    }

    @Override
    public Resources getResources() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfCustomAdapt(super.getResources(), this);
        }
        return super.getResources();
    }

    public boolean hasPermission(String permission) {
        boolean has = true;
        try {
            has = PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return has;
    }

    protected abstract int getLayoutResID();

    protected abstract void init();

    protected void setLoadSir(View view) {
        if (mLoadService == null) {
            mLoadService = LoadSir.getDefault().register(view, new Callback.OnReloadListener() {
                @Override
                public void onReload(View v) {
                }
            });
        }
    }
    protected void showLoading() {
        if (mLoadService != null) {
            mLoadService.showCallback(LoadingCallback.class);
        }
    }

    protected void showEmpty() {
        if (null != mLoadService) {
            mLoadService.showCallback(EmptyCallback.class);
        }
    }

    protected void showSuccess() {
        if (null != mLoadService) {
            mLoadService.showSuccess();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    public void jumpActivity(Class<? extends BaseActivity> clazz) {
        Intent intent = new Intent(mContext, clazz);
        startActivity(intent);
    }

    public void jumpActivity(Class<? extends BaseActivity> clazz, Bundle bundle) {
        Intent intent = new Intent(mContext, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public float getSizeInDp() {
        return isBaseOnWidth() ? 1280 : 720;
    }

    @Override
    public boolean isBaseOnWidth() {
        return !(screenRatio >= 4.0f);
    }

    public boolean supportsPiPMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public boolean supportsTouch() {
        return getPackageManager().hasSystemFeature("android.hardware.touchscreen");
    }

    public void setScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected static BitmapDrawable globalWp = null;

    public void changeWallpaper(boolean force) {
        if (!force && globalWp != null)
            getWindow().setBackgroundDrawable(globalWp);
        try {
            File wp = new File(getFilesDir().getAbsolutePath() + "/wp");
            if (wp.exists()) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(wp.getAbsolutePath(), opts);
                // 从Options中获取图片的分辨率
                int imageHeight = opts.outHeight;
                int imageWidth = opts.outWidth;
                int picHeight = 720;
                int picWidth = 1080;
                int scaleX = imageWidth / picWidth;
                int scaleY = imageHeight / picHeight;
                int scale = 1;
                if (scaleX > scaleY && scaleY >= 1) {
                    scale = scaleX;
                }
                if (scaleX < scaleY && scaleX >= 1) {
                    scale = scaleY;
                }
                opts.inJustDecodeBounds = false;
                // 采样率
                opts.inSampleSize = scale;
                globalWp = new BitmapDrawable(BitmapFactory.decodeFile(wp.getAbsolutePath(), opts));
            } else {
                globalWp = null;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            globalWp = null;
        }
        if (globalWp != null) {
            getWindow().setBackgroundDrawable(globalWp);
        } else {
            getWindow().setBackgroundDrawableResource(R.drawable.princess_bg);
        }

    }
}