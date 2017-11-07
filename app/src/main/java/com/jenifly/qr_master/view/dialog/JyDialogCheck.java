package com.jenifly.qr_master.view.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jenifly.qr_master.R;
import com.jenifly.qr_master.tool.JyTextTool;


/**
 * Created by vondear on 2016/7/19.
 * Mainly used for confirmation and cancel.
 */
public class JyDialogCheck extends JyDialog {

    private ImageView mIvLogo;
    private TextView mTvContent;
    private TextView tv_share, tv_export, tv_cancel;

    public JyDialogCheck(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public JyDialogCheck(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public JyDialogCheck(Context context) {
        super(context);
        initView();
    }

    public JyDialogCheck(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    public ImageView getLogoView() {
        return mIvLogo;
    }

    public TextView getShareView() {
        return tv_share;
    }

    public TextView getExortView() {
        return tv_export;
    }

    public TextView getCancelView() {
        return tv_cancel;
    }

    public TextView getContentView() {
        return mTvContent;
    }

    public void setLogo(Drawable drawable) {
        mIvLogo.setImageDrawable(drawable);
    }

    public void setContent(String str) {
        if (Patterns.WEB_URL.matcher(str).matches()) {
            // 响应点击事件的话必须设置以下属性
            mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
            mTvContent.setText(JyTextTool.getBuilder("").setBold().append(str).setUrl(str).create());//当内容为网址的时候，内容变为可点击
        } else {
            mTvContent.setText(str);
        }
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_check, null);
        tv_share = dialogView.findViewById(R.id.tv_share);
        tv_export = dialogView.findViewById(R.id.tv_export);
        tv_cancel = dialogView.findViewById(R.id.tv_cancel);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        mIvLogo = dialogView.findViewById(R.id.iv_logo);
        setContentView(dialogView);
    }

}
