package com.github.tvbox.osc.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.base.baseActivity;
import com.github.tvbox.osc.bean.IJKCode;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.ui.activity.HomeActivity;
import com.github.tvbox.osc.ui.activity.SettingActivity;
import com.github.tvbox.osc.ui.adapter.ApiHistoryDialogAdapter;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.dialog.AboutDialog;
import com.github.tvbox.osc.ui.dialog.ApiDialog;
import com.github.tvbox.osc.ui.dialog.ApiHistoryDialog;
import com.github.tvbox.osc.ui.dialog.BackupDialog;
import com.github.tvbox.osc.ui.dialog.SearchRemoteTvDialog;
import com.github.tvbox.osc.ui.dialog.SelectDialog;
import com.github.tvbox.osc.ui.dialog.UpdateDialog;
import com.github.tvbox.osc.ui.dialog.XWalkInitDialog;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.HistoryHelper;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;
import okhttp3.HttpUrl;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class ModelSettingFragment extends BaseLazyFragment {
    public static boolean reloadA;
    private TextView tvDebugOpen;
    private TextView tvMediaCodec;
    private TextView tvParseWebView;
    private TextView tvPlay;
    private TextView tvRender;
    private TextView tvScale;
    private TextView tvApi;
    private TextView tvHomeApi;
    private TextView tvHomeShow;
    private TextView tvHomeImg;
    private TextView tvHomeLive;
    private TextView tvDns;
    private TextView tvXianc;
    private TextView tvHomeRec;
    private TextView tvHistoryNum;
    private TextView tvSearchView;
    private TextView tvShowPreviewText;
    private TextView tvShowWallpaperIndexText;
    private TextView tvPicsize;
    private TextView tvFastSearchText;
    private TextView tvIjkCachePlay;
    private TextView tvTheme;
    private TextView tvRemote;
    private TextView tvVersion;
    private TextView tvPwet;
    private TextView tvPtst;
    private TextView tvPlayback;

    private TextView mForbidDialogText;

    public static ModelSettingFragment newInstance() {
        return new ModelSettingFragment().setArguments();
    }


    public ModelSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
            return R.layout.fragment_model;
    }
    @Override
    protected void init() {
        tvFastSearchText = findViewById(R.id.showFastSearchText);
        tvFastSearchText.setText(Hawk.get(HawkConfig.EPISODE_GROUPING, false) ? "开启" : "关闭");
        tvShowPreviewText = findViewById(R.id.showPreviewText);
        tvShowPreviewText.setText(Hawk.get(HawkConfig.SHOW_PREVIEW, true) ? "开启" : "关闭");
        tvPicsize = findViewById(R.id.tvPicsize);
        tvRemote = findViewById(R.id.remoteTv);
        tvPicsize.setText(Hawk.get(HawkConfig.PIC_SIZE, false) ? "横向" : "纵向");
        tvDebugOpen = findViewById(R.id.tvDebugOpen);
        tvParseWebView = findViewById(R.id.tvParseWebView);
        tvMediaCodec = findViewById(R.id.tvMediaCodec);
        tvPlay = findViewById(R.id.tvPlay);
        tvRender = findViewById(R.id.tvRenderType);
        tvScale = findViewById(R.id.tvScaleType);
        tvApi = findViewById(R.id.tvApi);
        tvHomeApi = findViewById(R.id.tvHomeApi);
        tvDns = findViewById(R.id.tvDns);
        tvXianc = findViewById(R.id.tvXianc);
        tvHomeRec = findViewById(R.id.tvHomeRec);
        tvHistoryNum = findViewById(R.id.tvHistoryNum);
        tvSearchView = findViewById(R.id.tvSearchView);
        tvVersion = findViewById(R.id.tvVersion);
        tvHomeLive = findViewById(R.id.tvHomeLive);
        tvXianc.setText(HistoryHelper.getHistoryNumNamex(Hawk.get(HawkConfig.XIANC_NUM, 0)));
        tvHomeLive.setText(Hawk.get(HawkConfig.HOME_LIVE, false) ? "开" : "关");
        tvPtst = findViewById(R.id.tvPtst);
        tvPtst.setText(Hawk.get(HawkConfig.HOME_PTST_TUMM, false) ? "开" : "关");
        tvRemote.setText(Hawk.get(HawkConfig.FILTER_LOCATION, false) ? "顶部" : "底部");
        tvPwet = findViewById(R.id.tvPwet);
        tvPwet.setText(Hawk.get(HawkConfig.HOME_PWET_TUMM, false) ? "开" : "关");
        tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "已打开" : "已关闭");
        tvPlayback = findViewById(R.id.tvPlayback);
        tvMediaCodec.setText(Hawk.get(HawkConfig.IJK_CODEC, ""));
        tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "已打开" : "已关闭");
        tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
        tvApi.setText(Hawk.get(HawkConfig.API_URL, ""));
        findViewById(R.id.forbidJarDialog).setOnClickListener(v -> {
            Hawk.put(HawkConfig.FORBID_JAR_DIALOG, !Hawk.get(HawkConfig.FORBID_JAR_DIALOG, false));
            mForbidDialogText.setText(Hawk.get(HawkConfig.FORBID_JAR_DIALOG, false) ? " 开" : "关");
        });
        mForbidDialogText = findViewById(R.id.forbidJarDialogText);
        mForbidDialogText.setText(Hawk.get(HawkConfig.FORBID_JAR_DIALOG, false) ? "开" : "关");
        findViewById(R.id.llIjkCachePlay).setOnClickListener(v -> {
            Hawk.put(HawkConfig.PLAYBACK_PLAYS, !Hawk.get(HawkConfig.PLAYBACK_PLAYS, false));
            tvIjkCachePlay.setText(Hawk.get(HawkConfig.PLAYBACK_PLAYS, false) ? " 开" : "关");
        });
        tvIjkCachePlay = findViewById(R.id.tvIjkCachePlay);
        tvIjkCachePlay.setText(Hawk.get(HawkConfig.PLAYBACK_PLAYS, false) ? "开" : "关");
        tvHomeShow = findViewById(R.id.tvHomeShow);
        tvHomeShow.setText(Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false) ? "数据源" : "LOGO");
        tvHomeImg = findViewById(R.id.tvHomeImg);
        tvHomeImg.setText(getimgView(Hawk.get(HawkConfig.HOME_LSTIMG, 0)));
        tvDns.setText(OkGoHelper.dnsHttpsList.get(Hawk.get(HawkConfig.DOH_URL, 0)));
        tvHomeRec.setText(getHomeRecName(Hawk.get(HawkConfig.HOME_REC, 0)));
        tvHistoryNum.setText(HistoryHelper.getHistoryNumName(Hawk.get(HawkConfig.HISTORY_NUM, 0)));
        tvSearchView.setText(getSearchView(Hawk.get(HawkConfig.SEARCH_VIEW, 0)));
        tvHomeApi.setText(ApiConfig.get().getHomeSourceBean().getName());
        tvTheme = findViewById(R.id.tvTheme);
        tvTheme.setText(getThemeView(Hawk.get(HawkConfig.THEME_SELECT, 0)));
        tvScale.setText(PlayerHelper.getScaleName(Hawk.get(HawkConfig.PLAY_SCALE, 0)));
        tvPlay.setText(PlayerHelper.getPlayerName(Hawk.get(HawkConfig.PLAY_TYPE, 0)));
        tvRender.setText(PlayerHelper.getRenderName(Hawk.get(HawkConfig.PLAY_RENDER, 0)));
        tvPlayback.setText(Hawk.get(HawkConfig.PLAYBACK_PLAY, false) ? "开" : "关");
        findViewById(R.id.llHomeApi).requestFocus();
        findViewById(R.id.llDebug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.DEBUG_OPEN, !Hawk.get(HawkConfig.DEBUG_OPEN, false));
                tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "已打开" : "已关闭");
            }
        });
        LinearLayout strs[] = {findViewById(R.id.llApi), findViewById(R.id.llHomeApi), findViewById(R.id.llHomeRec),
                findViewById(R.id.llHomeLive), findViewById(R.id.forbidJarDialog), findViewById(R.id.llHomeImg), findViewById(R.id.llSearchView),
                findViewById(R.id.llPlay), findViewById(R.id.llHomeShow), findViewById(R.id.llvodst),
                findViewById(R.id.llvodet), findViewById(R.id.llRender), findViewById(R.id.llMediaCodec), findViewById(R.id.llPicsize),
                findViewById(R.id.llDns), findViewById(R.id.llParseWebVew), findViewById(R.id.llTheme),
                findViewById(R.id.llSearchTv), findViewById(R.id.llHistoryNum), findViewById(R.id.llWp), findViewById(R.id.llWpRecovery),
                findViewById(R.id.llScale), findViewById(R.id.llIjkCachePlay), findViewById(R.id.llClearCache),
                findViewById(R.id.llBackup), findViewById(R.id.llplayback), findViewById(R.id.llAbout),findViewById(R.id.llXianc), findViewById(R.id.llDadeup), findViewById(R.id.showFastSearch), findViewById(R.id.showPreview)};
        baseActivity.drawableLinearLayout(strs, 1);
        findViewById(R.id.llParseWebVew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                boolean useSystem = !Hawk.get(HawkConfig.PARSE_WEBVIEW, true);
                Hawk.put(HawkConfig.PARSE_WEBVIEW, useSystem);
                tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
                if (!useSystem) {
                    Toast.makeText(mContext, "注意: XWalkView只适用于部分低Android版本，Android5.0以上推荐使用系统自带", Toast.LENGTH_LONG).show();
                    XWalkInitDialog dialog = new XWalkInitDialog(mContext);
                    dialog.setOnListener(new XWalkInitDialog.OnListener() {
                        @Override
                        public void onchange() {
                        }
                    });
                    dialog.show();
                }
            }
        });

        findViewById(R.id.llBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                BackupDialog dialog = new BackupDialog(mActivity);
                dialog.show();
            }
        });

        findViewById(R.id.llAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                 AboutDialog dialog = new AboutDialog(mActivity);
                 dialog.show();
            }
        });
        findViewById(R.id.llDadeup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                UpdateDialog.checkUpdate(mActivity, false);
            }
        });
        tvVersion.setText(DefaultConfig.getAppVersionName(mActivity));
        findViewById(R.id.llWp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (!ApiConfig.get().wallpaper.isEmpty())
                    OkGo.<File>get(ApiConfig.get().wallpaper).execute(new FileCallback(requireActivity().getFilesDir().getAbsolutePath(), "wp") {
                        @Override
                        public void onSuccess(Response<File> response) {
                            ((baseActivity) requireActivity()).changeWallpaper(true);
                        }

                        @Override
                        public void onError(Response<File> response) {
                            super.onError(response);
                        }

                        @Override
                        public void downloadProgress(Progress progress) {
                            super.downloadProgress(progress);
                        }
                    });
            }
        });
        findViewById(R.id.llWpRecovery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                File wp = new File(requireActivity().getFilesDir().getAbsolutePath() + "/wp");
                if (wp.exists())
                    wp.delete();
                ((baseActivity) requireActivity()).changeWallpaper(true);
            }
        });
        findViewById(R.id.llHomeApi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                //List<SourceBean> sites = ApiConfig.get().getSourceBeanList();
                List<SourceBean> sites = new ArrayList<>();
                for (SourceBean sb : ApiConfig.get().getSourceBeanList()) {
                    if (sb.getHide() == 0) sites.add(sb);
                }
                if (sites.size() > 0) {
                    SelectDialog<SourceBean> dialog = new SelectDialog<>(mActivity);
                    TvRecyclerView tvRecyclerView = dialog.findViewById(R.id.list);
                    int spanCount;
                    spanCount = (int)Math.floor(sites.size()/10);
                    spanCount = Math.min(spanCount, 2);
                    tvRecyclerView.setLayoutManager(new V7GridLayoutManager(dialog.getContext(), spanCount+1));
                    ConstraintLayout cl_root = dialog.findViewById(R.id.cl_root);
                    ViewGroup.LayoutParams clp = cl_root.getLayoutParams();
                    clp.width = AutoSizeUtils.mm2px(dialog.getContext(), 300+160*spanCount);
                    dialog.setTip("请选择数据源");
                    dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<SourceBean>() {
                        @Override
                        public void click(SourceBean value, int pos) {
                            ApiConfig.get().setSourceBean(value);
                            tvHomeApi.setText(ApiConfig.get().getHomeSourceBean().getName());

                            Intent intent =new Intent(mContext, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("useCache", true);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                        @Override
                        public String getDisplay(SourceBean val) {
                            return val.getName();
                        }
                    }, new DiffUtil.ItemCallback<SourceBean>() {
                        @Override
                        public boolean areItemsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) {
                            return oldItem == newItem;
                        }

                        @Override
                        public boolean areContentsTheSame(@NonNull @NotNull SourceBean oldItem, @NonNull @NotNull SourceBean newItem) {
                            return oldItem.getKey().equals(newItem.getKey());
                        }
                    }, sites, sites.indexOf(ApiConfig.get().getHomeSourceBean()));
                    dialog.show();
                }
            }
        });
        findViewById(R.id.llHomeShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_SHOW_SOURCE, !Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false));
                tvHomeShow.setText(Hawk.get(HawkConfig.HOME_SHOW_SOURCE, true) ? "数据源" : "LOGO");
            }
        });
        findViewById(R.id.llHomeLive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_LIVE, !Hawk.get(HawkConfig.HOME_LIVE, false));
                tvHomeLive.setText(Hawk.get(HawkConfig.HOME_LIVE, false) ? "开" : "关");
            }
        });

        findViewById(R.id.llDns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int dohUrl = Hawk.get(HawkConfig.DOH_URL, 0);

                SelectDialog<String> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择安全DNS");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<String>() {
                    @Override
                    public void click(String value, int pos) {
                        tvDns.setText(OkGoHelper.dnsHttpsList.get(pos));
                        Hawk.put(HawkConfig.DOH_URL, pos);
                        String url = OkGoHelper.getDohUrl(pos);
                        OkGoHelper.dnsOverHttps.setUrl(url.isEmpty() ? null : HttpUrl.get(url));
                        IjkMediaPlayer.toggleDotPort(pos > 0);
                    }

                    @Override
                    public String getDisplay(String val) {
                        return val;
                    }
                }, new DiffUtil.ItemCallback<String>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                        return oldItem.equals(newItem);
                    }
                }, OkGoHelper.dnsHttpsList, dohUrl);
                dialog.show();
            }
        });
        findViewById(R.id.llApi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                ApiDialog dialog = new ApiDialog(mActivity);
                EventBus.getDefault().register(dialog);
                dialog.setOnListener(new ApiDialog.OnListener() {
                    @Override
                    public void onchange(String api) {
                        Hawk.put(HawkConfig.API_URL, api);
                        tvApi.setText(api);
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((baseActivity) mActivity).hideSysBar();
                        EventBus.getDefault().unregister(dialog);
                    }
                });
                dialog.show();
            }
        });



        findViewById(R.id.llMediaCodec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<IJKCode> ijkCodes = ApiConfig.get().getIjkCodes();
                if (ijkCodes == null || ijkCodes.size() == 0)
                    return;
                FastClickCheckUtil.check(v);

                int defaultPos = 0;
                String ijkSel = Hawk.get(HawkConfig.IJK_CODEC, "");
                for (int j = 0; j < ijkCodes.size(); j++) {
                    if (ijkSel.equals(ijkCodes.get(j).getName())) {
                        defaultPos = j;
                        break;
                    }
                }

                SelectDialog<IJKCode> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择IJK解码");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<IJKCode>() {
                    @Override
                    public void click(IJKCode value, int pos) {
                        value.selected(true);
                        tvMediaCodec.setText(value.getName());
                    }

                    @Override
                    public String getDisplay(IJKCode val) {
                        return val.getName();
                    }
                }, new DiffUtil.ItemCallback<IJKCode>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                        return oldItem == newItem;
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                        return oldItem.getName().equals(newItem.getName());
                    }
                }, ijkCodes, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llScale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_SCALE, 0);
                ArrayList<Integer> players = new ArrayList<>();
                players.add(0);
                players.add(1);
                players.add(2);
                players.add(3);
                players.add(4);
                players.add(5);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认画面缩放");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_SCALE, value);
                        tvScale.setText(PlayerHelper.getScaleName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getScaleName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, players, defaultPos);
                dialog.show();
            }
        });

        findViewById(R.id.llHomeImg).setOnClickListener(new View.OnClickListener() {
            private final int chkTheme = Hawk.get(HawkConfig.HOME_LSTIMG, 0);

            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HOME_LSTIMG, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择略缩图默认大小");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HOME_LSTIMG, value);
                        tvHomeImg.setText(getimgView(value));
                    }
                    @Override
                    public String getDisplay(Integer val) {
                        return getimgView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (chkTheme != Hawk.get(HawkConfig.HOME_LSTIMG, 0)) {
                            reloadActivity();
                        }
                    }
                });
                dialog.show();
            }
        });

        findViewById(R.id.llPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int playerType = Hawk.get(HawkConfig.PLAY_TYPE, 0);
                int defaultPos = 0;
                ArrayList<Integer> players = PlayerHelper.getExistPlayerTypes();
                ArrayList<Integer> renders = new ArrayList<>();
                for(int p = 0; p<players.size(); p++) {
                    renders.add(p);
                    if (players.get(p) == playerType) {
                        defaultPos = p;
                    }
                }
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认播放器");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Integer thisPlayerType = players.get(pos);
                        Hawk.put(HawkConfig.PLAY_TYPE, thisPlayerType);
                        tvPlay.setText(PlayerHelper.getPlayerName(thisPlayerType));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        Integer playerType = players.get(val);
                        return PlayerHelper.getPlayerName(playerType);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, renders, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llRender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_RENDER, 0);
                ArrayList<Integer> renders = new ArrayList<>();
                renders.add(0);
                renders.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认渲染方式");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_RENDER, value);
                        tvRender.setText(PlayerHelper.getRenderName(value));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getRenderName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, renders, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llHomeRec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HOME_REC, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择列表数据");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HOME_REC, value);
                        tvHomeRec.setText(getHomeRecName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getHomeRecName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llSearchView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.SEARCH_VIEW, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择搜索视图");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.SEARCH_VIEW, value);
                        tvSearchView.setText(getSearchView(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getSearchView(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });
        SettingActivity.callback = new SettingActivity.DevModeCallback() {
            @Override
            public void onChange() {
                findViewById(R.id.llDebug).setVisibility(View.VISIBLE);
            }
        };

        findViewById(R.id.showPreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.SHOW_PREVIEW, !Hawk.get(HawkConfig.SHOW_PREVIEW, true));
                tvShowPreviewText.setText(Hawk.get(HawkConfig.SHOW_PREVIEW, true) ? "开启" : "关闭");
            }
        });
        findViewById(R.id.llPicsize).setOnClickListener(new View.OnClickListener() {
            private final Boolean chkPicsize = Hawk.get(HawkConfig.PIC_SIZE, false);
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.PIC_SIZE, !Hawk.get(HawkConfig.PIC_SIZE, false));
                tvPicsize.setText(Hawk.get(HawkConfig.PIC_SIZE, false) ? "横向" : "纵向");
                if (chkPicsize != Hawk.get(HawkConfig.PIC_SIZE, false)) {
                    reloadActivity();
                }
            }
        });
        findViewById(R.id.llHistoryNum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HISTORY_NUM, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("保留历史记录数量");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HISTORY_NUM, value);
                        tvHistoryNum.setText(HistoryHelper.getHistoryNumName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return HistoryHelper.getHistoryNumName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });

        findViewById(R.id.llXianc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.XIANC_NUM, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                types.add(3);
                types.add(4);
                types.add(5);
                types.add(6);
                types.add(7);
                types.add(8);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("搜索线程");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.XIANC_NUM, value);
                        tvXianc.setText(HistoryHelper.getHistoryNumNamex(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return HistoryHelper.getHistoryNumNamex(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });//episode_grouping EPISODE_GROUPING
        findViewById(R.id.showFastSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.EPISODE_GROUPING, !Hawk.get(HawkConfig.EPISODE_GROUPING, false));
                tvFastSearchText.setText(Hawk.get(HawkConfig.EPISODE_GROUPING, false) ? "开启" : "关闭");
            }
        });

        findViewById(R.id.llvodst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_PTST_TUMM, !Hawk.get(HawkConfig.HOME_PTST_TUMM, false));
                tvPtst.setText(Hawk.get(HawkConfig.HOME_PTST_TUMM, false) ? "开" : "关");
            }
        });

        findViewById(R.id.llvodet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_PWET_TUMM, !Hawk.get(HawkConfig.HOME_PWET_TUMM, false));
                tvPwet.setText(Hawk.get(HawkConfig.HOME_PWET_TUMM, false) ? "开" : "关");
            }
        });
        findViewById(R.id.llplayback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.PLAYBACK_PLAY, !Hawk.get(HawkConfig.PLAYBACK_PLAY, false));
                tvPlayback.setText(Hawk.get(HawkConfig.PLAYBACK_PLAY, false) ? "开" : "关");
            }
        });
            findViewById(R.id.llSearchTv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FastClickCheckUtil.check(v);
                    Hawk.put(HawkConfig.FILTER_LOCATION, !Hawk.get(HawkConfig.FILTER_LOCATION, false));
                    tvRemote.setText(Hawk.get(HawkConfig.FILTER_LOCATION, false) ? "顶部" : "底部");
                }
            });
        //findViewById(R.id.llIjkCachePlay).setOnClickListener((this::onClickIjkCachePlay));
       // findViewById(R.id.llClearCache).setOnClickListener((this::onClickClearCache));
        findViewById(R.id.llClearCache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> history = Hawk.get(HawkConfig.API_HISTORY, new ArrayList<String>());
                if (history.isEmpty())
                    return;
                String current = Hawk.get(HawkConfig.API_URL, "");
                int idx = 0;
                if (history.contains(current))
                    idx = history.indexOf(current);
                ApiHistoryDialog dialog = new ApiHistoryDialog(mActivity);
                dialog.setTip("历史配置列表");
                dialog.setAdapter(new ApiHistoryDialogAdapter.SelectDialogInterface() {
                    @Override
                    public void click(String value) {
                        Hawk.put(HawkConfig.API_URL, value);
                        Hawk.put(HawkConfig.LIVE_API_URL, value);
                        ArrayList<String> history = Hawk.get(HawkConfig.LIVE_API_HISTORY, new ArrayList<String>());
                        if (!history.contains(value)) {
                            history.add(0, value);
                        }
                        if (history.size() > 30) {
                            history.remove(30);
                        }
                        Hawk.put(HawkConfig.LIVE_API_HISTORY, history);
                        tvApi.setText(value);
                    }

                    @Override
                    public void del(String value, ArrayList<String> data) {
                        Hawk.put(HawkConfig.API_HISTORY, data);
                    }
                }, history, idx);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        restartApp();
                    }
                });
                dialog.show();
            }
        });
        // Select App Theme Color -------------------------------------
        findViewById(R.id.llTheme).setOnClickListener(new View.OnClickListener() {
            private final int chkTheme = Hawk.get(HawkConfig.THEME_SELECT, 0);

            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.THEME_SELECT, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                types.add(3);
                types.add(4);
                types.add(5);
                types.add(6);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择主题");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.THEME_SELECT, value);
                        tvTheme.setText(getThemeView(value));
                        reloadA = true;
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return getThemeView(val);
                    }

                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (chkTheme != Hawk.get(HawkConfig.THEME_SELECT, 0)) {
                            reloadActivity();
                        }
                    }
                });
                dialog.show();
            }
        });

    }

    private void onClickIjkCachePlay(View v) {
        FastClickCheckUtil.check(v);
        Hawk.put(HawkConfig.IJK_CACHE_PLAY, !Hawk.get(HawkConfig.IJK_CACHE_PLAY, false));
        tvIjkCachePlay.setText(Hawk.get(HawkConfig.IJK_CACHE_PLAY, false) ? "开启" : "关闭");
    }

    private void onClickClearCache(View v) {
        FastClickCheckUtil.check(v);
        String cachePath = FileUtils.getCachePath();
        File cacheDir = new File(cachePath);
        if (!cacheDir.exists()) return;
        new Thread(() -> {
            try {
                FileUtils.cleanDirectory(cacheDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Toast.makeText(getContext(), "缓存已清空", Toast.LENGTH_LONG).show();
    }


    public static SearchRemoteTvDialog loadingSearchRemoteTvDialog;
    public static List<String> remoteTvHostList;
    public static boolean foundRemoteTv;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SettingActivity.callback = null;
    }

    String getHomeRecName(int type) {
        if (type == 1) {
            return "站点推荐";
        } else {
            return "豆瓣热播";
        }
    }

    String getSearchView(int type) {
        if (type == 0) {
            return "文字列表";
        } else {
            return "缩略图";
        }
    }

    String getimgView(int type) {
        if (type == 0) {
            return "大";
        } else if (type == 1) {
            return "小";
        } else {
            return "中";
        }
    }

    String getThemeView(int type) {
        if (type == 0) {
            return "玫瑰";
        } else if (type == 1) {
            return "海藻";
        } else if (type == 2) {
            return "百事";
        } else if (type == 3) {
            return "鸣人";
        } else if (type == 4) {
            return "樱花";
        } else if (type == 5) {
            return "蝶恋";
        } else {
            return "玻璃";
        }
    }

    void reloadActivity() {
        if (AppManager.getInstance().isActivity()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("useCache", true);
            baseActivity currActivity = (baseActivity) AppManager.getInstance().currentActivity();
            currActivity.jumpActivity(HomeActivity.class, bundle);
        }
    }
    private void restartApp() {
        Context context = getContext();
        if (context != null) {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
                System.exit(0);
            }
        }
    }
}