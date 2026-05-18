package com.github.tvbox.osc.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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
import java.lang.ref.WeakReference;

import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.internal.CustomAdapt;
import me.jessyan.autosize.utils.AutoSizeUtils;
import xyz.doikki.videoplayer.util.CutoutUtil;

public abstract class baseActivity extends AppCompatActivity implements CustomAdapt {
    protected Context mContext;
    private LoadService mLoadService;

    private static float screenRatio = -100.0f;
    private static WeakReference<BitmapDrawable> globalWp = null;
    public static  int roundRadius = getCornerRadius();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = this;
        initScreenRatio();
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());

        CutoutUtil.adaptCutoutAboveAndroidP(mContext, true);
        AppManager.getInstance().addActivity(this);
        init();
        setScreenOn();
    }

    private void initScreenRatio() {
        if (screenRatio < 0) {
            try {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;
                int screenHeight = dm.heightPixels;
                screenRatio = (float) Math.max(screenWidth, screenHeight) / Math.min(screenWidth, screenHeight);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private void initTheme() {
        int theme = Hawk.get(HawkConfig.THEME_SELECT, 0);
        switch (theme) {
            case 1:
                setTheme(R.style.DoraeTheme);
                break;
            case 2:
                setTheme(R.style.PepsiTheme);
                break;
            case 3:
                setTheme(R.style.NarutoTheme);
                break;
            case 4:
                setTheme(R.style.MinionTheme);
                break;
            case 5:
                setTheme(R.style.YagamiTheme);
                break;
            case 6:
                setTheme(R.style.SakuraTheme);
                break;
            default:
                setTheme(R.style.NetfxTheme);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSysBar();
        changeWallpaper(false);
    }

    public static void LParams(@NonNull FrameLayout view, int w, int h) {
        if (view == null || view.getContext() == null) return;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = AutoSizeUtils.mm2px(view.getContext(), w);
        params.height = AutoSizeUtils.mm2px(view.getContext(), h);
        view.setLayoutParams(params);
    }

    public static void colorTextView(final TextView textView, @NonNull final LinearLayout layout) {
        if (textView == null || layout == null || textView.getContext() == null) return;
        final Context context = textView.getContext();
        layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textView.setTextColor(context.getResources().getColor(R.color.color_FFF5E8DC));
                } else {
                    textView.setTextColor(context.getResources().getColor(R.color.color_80FFFFFF));
                }
            }
        });
    }
    private static int getCornerRadius() {
        return 10;
    }
    public static void drawableTextView(@NonNull TextView[] str, int bg) {
        for (TextView item : str) setBackground(item, bg);
    }
    public static void drawableTextView(@NonNull TextView str, int bg) {
        setBackground(str, bg);
    }
    public static void drawableLinearLayout(@NonNull LinearLayout[] strs, int bg) {
        for (LinearLayout item : strs) setBackground(item, bg);
    }
    public static void drawableLinearLayout(@NonNull LinearLayout strs, int bg) {
        setBackground(strs, bg);
    }
    public static void drawableLinearLayouts(@NonNull LinearLayout strs, int bg) {
        setBackground(strs, bg);
    }

    public static void drawableTextViews(@NonNull TextView strs, int bg) {
        setBackground(strs, bg);
    }

    public static void drawableFrameLayout(@NonNull FrameLayout[] strs, int bg) {
        for (FrameLayout item : strs) setBackground(item, bg);
    }

    public static void drawableConstraintLayout(@NonNull ConstraintLayout[] strs, int bg) {
        for (ConstraintLayout item : strs) setBackground(item, bg);
    }

    public static void drawableImageView(@NonNull ImageView[] strs, int bg) {
        for (ImageView item : strs) setBackground(item, bg);
    }

    private static void setBackground(View view, int bg) {
        if (view == null || view.getContext() == null) return;
        view.setBackground(createDrawableSelector(view.getContext(), bg));
    }
    public static void setBackgroundrs(View tvSeries, Context mContext) {

        int color70 = getColorByAttrId(mContext, R.attr.color_theme_70);
        int color80 = getColorByAttrId(mContext, R.attr.color_theme_80);
        int color71 = getColorByAttrId(mContext, R.attr.color_theme_70);
        int colorH = getColorByAttrId(mContext, R.attr.color_theme_h);
        int stroke2 = AutoSizeUtils.mm2px(mContext, 2);
        int radius = AutoSizeUtils.mm2px(mContext, 4); // 圆角，可自己改
        int pL = tvSeries.getPaddingLeft();
        int pT = tvSeries.getPaddingTop();
        int pR = tvSeries.getPaddingRight();
        int pB = tvSeries.getPaddingBottom();
        // ============= 选中：设置渐变背景 + 边框 =============
        GradientDrawable pressed = new GradientDrawable();
        pressed.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        pressed.setColors(new int[]{color70, color80,color71});
        pressed.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        pressed.setStroke(stroke2, colorH);
        pressed.setCornerRadius(radius);
        tvSeries.setBackground(pressed);
        // 设置背景后立刻恢复 Padding —— 间距不会丢！
        tvSeries.setBackground(pressed);
        tvSeries.setPadding(pL, pT, pR, pB);
    }
    // ====================== 你要的 e() 方法 已完美加入 ======================
    public static Drawable e(Context context) {
        int r = AutoSizeUtils.mm2px(context, 5);
        int strokeSize = AutoSizeUtils.mm2px(context, 1.5f);

        // 第一层 底色
        GradientDrawable back = new GradientDrawable();
        back.setCornerRadius(r);
        back.setColor(getColorByAttrId(context, R.attr.color_theme_h));

        // 第二层 渐变 【API16 正确渐变写法】
        GradientDrawable gradient = new GradientDrawable();
        gradient.setCornerRadius(r);
        // 低版本不能用构造器传颜色数组，分开写
        gradient.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        int[] colors = new int[]{
                getColorByAttrId(context, R.attr.color_theme_70),
                getColorByAttrId(context, R.attr.color_theme_80),
                getColorByAttrId(context, R.attr.color_theme_70)
        };

            gradient.setColors(colors);
        gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);


        // 第三层 边框
        GradientDrawable stroke = new GradientDrawable();
        stroke.setCornerRadius(r);
        stroke.setStroke(strokeSize, context.getResources().getColor(R.color.color_theme_s));

        // 三层 LayerDrawable 【API16 完全支持】
        LayerDrawable layer = new LayerDrawable(new Drawable[]{back, gradient, stroke});
        // 你原来的内边距 一行不改
        layer.setLayerInset(1, 2, 2, 2, 2);
        layer.setLayerInset(2, 3, 3, 3, 3);

        return layer;
    }
    // ====================================================================

    @NonNull
    public static Drawable createDrawableSelector(Context context, int bg) {
        StateListDrawable stateDrawable = new StateListDrawable();
        GradientDrawable normal = new GradientDrawable();
        GradientDrawable pressed = new GradientDrawable();
        GradientDrawable selected = new GradientDrawable();

        int color70 = getColorByAttrId(context, R.attr.color_theme_70);
        int color80 = getColorByAttrId(context, R.attr.color_theme_80);
        int color71 = getColorByAttrId(context, R.attr.color_theme_70);
        int[] colorBoth = new int[]{color70, color80, color71};
        int colorH = getColorByAttrId(context, R.attr.color_theme_h);
        int stroke2 = AutoSizeUtils.mm2px(context, 2);
        int radius5 = AutoSizeUtils.mm2px(context, 5);
       switch (bg) {
            case 1:
                pressed.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                pressed.setColors(colorBoth);
                pressed.setStroke(stroke2, colorH);
                pressed.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                normal.setColor(context.getResources().getColor(R.color.color_32364E_40));
                if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 6) {
                    normal.setColor(getColorByAttrId(context, R.attr.color_theme_20));
                    normal.setStroke(1, context.getResources().getColor(R.color.color_33FFFFFF));
                }
                break;
            case 2:
                normal.setColor(context.getResources().getColor(R.color.color_theme_0));
                pressed.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                pressed.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                pressed.setColors(colorBoth);
                pressed.setStroke(stroke2, colorH);
                break;
            case 3:
                    pressed.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                    pressed.setColors(colorBoth);
                    pressed.setStroke(stroke2, colorH);
                    pressed.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                    normal.setColor(context.getResources().getColor(R.color.color_32364E_80));
                if (Hawk.get(HawkConfig.THEME_SELECT, 0) == 6) {
                    normal.setColor(getColorByAttrId(context, R.attr.color_theme_20));
                    normal.setStroke(1, context.getResources().getColor(R.color.color_33FFFFFF));
                }
                break;
            case 4:
                return new BitmapDrawable(getRoundBitmap(context, 1000, 720, 20, 4));
            case 5:
                pressed.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                pressed.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                normal.setColor(context.getResources().getColor(R.color.color_32364E_80));
                pressed.setColors(colorBoth);
                pressed.setStroke(stroke2, colorH);
                break;
            case 6:
                normal.setColor(getColorByAttrId(context, R.attr.color_theme_40));
                normal.setStroke(1, context.getResources().getColor(R.color.color_33FFFFFF));
                pressed.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
                pressed.setColors(colorBoth);
                pressed.setStroke(stroke2, colorH);
                normal.setCornerRadius(AutoSizeUtils.mm2px(context, 2));
                pressed.setCornerRadius(AutoSizeUtils.mm2px(context, 2));
                break;
            case 7:
                normal.setColor(context.getResources().getColor(R.color.color_theme_0));
                pressed.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                pressed.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                pressed.setColors(colorBoth);
                pressed.setStroke(stroke2, colorH);
                normal.setCornerRadius(AutoSizeUtils.mm2px(context, 10));
                pressed.setCornerRadius(AutoSizeUtils.mm2px(context, 10));
                break;
            case 8:
                normal.setColor(context.getResources().getColor(R.color.color_theme_0));
                pressed.setColor(getColorByAttrId(context, R.attr.color_theme_40));
                pressed.setCornerRadius(AutoSizeUtils.mm2px(context, 50));
                break;
            case 9:
                normal.setColor(context.getResources().getColor(R.color.color_theme_0));
                pressed.setColor(context.getResources().getColor(R.color.color_theme_s));
                selected.setStroke(3, colorH);
                selected.setColor(context.getResources().getColor(R.color.color_theme_s));
                pressed.setStroke(3, colorH);
                break;
            case 10:
                normal.setColor(context.getResources().getColor(R.color.color_theme_0));
                pressed.setColor(context.getResources().getColor(R.color.color_theme_s));
                selected.setStroke(3, colorH);
                selected.setColor(context.getResources().getColor(R.color.color_theme_s));
                pressed.setStroke(3, colorH);
                normal.setCornerRadius(AutoSizeUtils.mm2px(context, 8));
                selected.setCornerRadius(AutoSizeUtils.mm2px(context, 8));
                pressed.setCornerRadius(AutoSizeUtils.mm2px(context, 8));
                break;
            case 11:
                return new BitmapDrawable(getRoundBitmap(context, 1280, 720, 0, 0));
        }

        if (bg != 6 && bg != 8 && bg != 10) {
            normal.setCornerRadius(radius5);
            pressed.setCornerRadius(radius5);
            selected.setCornerRadius(radius5);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            if (bg == 1 || bg == 2 || bg == 3 || bg == 5 || bg == 7) {
                Drawable highlight = e(context);
                stateDrawable.addState(new int[]{android.R.attr.state_pressed}, highlight);
                stateDrawable.addState(new int[]{android.R.attr.state_focused}, highlight);
                stateDrawable.addState(new int[]{android.R.attr.state_selected}, highlight);
                stateDrawable.addState(new int[]{}, normal);
                return stateDrawable;
            }
        }
        stateDrawable.addState(new int[]{android.R.attr.state_pressed}, pressed);
        stateDrawable.addState(new int[]{android.R.attr.state_focused}, pressed);
        stateDrawable.addState(new int[]{android.R.attr.state_selected}, selected);
        stateDrawable.addState(new int[]{android.R.attr.state_enabled}, normal);

        return stateDrawable;
    }

    private static Bitmap getRoundBitmap(Context context, int outW, int outH, int radius, int border) {
        Bitmap bitmap;
        if (globalWp == null || globalWp.get() == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_bg);
        } else {
            bitmap = globalWp.get().getBitmap();
        }
        return getRoundBitmapByShader(bitmap, outW, outH, radius, border, context);
    }

    public static Bitmap getRoundBitmapByShader(Bitmap bitmap, int outWidth, int outHeight, int radius, int boarder, Context context) {
        if (bitmap == null || bitmap.isRecycled()) return null;

        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, true);
        Bitmap result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        RectF rect = new RectF(boarder, boarder, outWidth - boarder, outHeight - boarder);
        canvas.drawRoundRect(rect, radius, radius, paint);

        if (boarder > 0) {
            Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setColor(context.getResources().getColor(R.color.color_33FFFFFF));
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(boarder);
            canvas.drawRoundRect(rect, radius, radius, borderPaint);
        }

        if (scaled != bitmap) scaled.recycle();
        return result;
    }

    @ColorInt
    public static int getColorByAttrId(Context context, @AttrRes int attrId) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(attrId, tv, true);
        return tv.data;
    }

    public void hideSysBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    public void hideSystemUI(boolean shownavbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            if (!shownavbar) {
                visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
            getWindow().getDecorView().setSystemUiVisibility(visibility);
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
        try {
            return PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

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
        if (mLoadService != null) mLoadService.showCallback(LoadingCallback.class);
    }

    protected void showEmpty() {
        if (mLoadService != null) mLoadService.showCallback(EmptyCallback.class);
    }

    protected void showSuccess() {
        if (mLoadService != null) mLoadService.showSuccess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    public void jumpActivity(Class<? extends baseActivity> clazz) {
        startActivity(new Intent(this, clazz));
    }

    public void jumpActivity(Class<? extends baseActivity> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public float getSizeInDp() {
        return isBaseOnWidth() ? 1280 : 720;
    }

    @Override
    public boolean isBaseOnWidth() {
        return screenRatio < 4.0f;
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

    public void changeWallpaper(boolean force) {
        if (!force && globalWp != null && globalWp.get() != null) {
            getWindow().setBackgroundDrawable(globalWp.get());
            return;
        }
        try {
            File wp = new File(getFilesDir().getAbsolutePath() + "/wp");
            if (wp.exists()) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(wp.getAbsolutePath(), opts);

                int scale = 1;
                int w = opts.outWidth;
                int h = opts.outHeight;
                if (w > 1080 || h > 720) {
                    scale = Math.max(w / 1080, h / 720);
                }
                opts.inJustDecodeBounds = false;
                opts.inSampleSize = scale;
                Bitmap bitmap = BitmapFactory.decodeFile(wp.getAbsolutePath(), opts);
                globalWp = new WeakReference<>(new BitmapDrawable(getResources(), bitmap));
            } else {
                globalWp = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            globalWp = null;
        }

        if (globalWp != null && globalWp.get() != null) {
            getWindow().setBackgroundDrawable(globalWp.get());
        } else {
            getWindow().setBackgroundDrawableResource(R.drawable.princess_bg);
        }
    }

    protected abstract int getLayoutResID();

    protected abstract void init();
}