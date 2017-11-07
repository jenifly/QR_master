package com.jenifly.qr_master.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jenifly.qr_master.R;
import com.jenifly.qr_master.tool.JyTextTool;

import static com.jenifly.qr_master.tool.JyConstTool.REGEX_URL;


/**
 * Created by vondear on 2016/7/19.
 * Mainly used for confirmation and cancel.
 */
public class JyDialogSure extends JyDialog {

    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvSure;

    public JyDialogSure(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public JyDialogSure(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public JyDialogSure(Context context) {
        super(context);
        initView();
    }

    public JyDialogSure(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    public TextView getTitleView() {
        return mTvTitle;
    }

    public TextView getSureView() {
        return mTvSure;
    }

    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }

    public TextView getContentView() {
        return mTvContent;
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setSure(String content) {
        mTvSure.setText(content);
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

    public void setSubContent(String str){
        TextView textView = findViewById(R.id.tv_subcontent);
        if (Patterns.WEB_URL.matcher(str).matches()) {
            // 响应点击事件的话必须设置以下属性
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(JyTextTool.getBuilder("").setBold().append(str).setUrl(str).create());//当内容为网址的时候，内容变为可点击
        } else {
            textView.setText(str);
        }
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sure, null);
        mTvSure = (TextView) dialogView.findViewById(R.id.tv_sure);
        mTvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        mTvTitle.setTextIsSelectable(true);
        mTvContent = (TextView) dialogView.findViewById(R.id.tv_content);
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        setContentView(dialogView);
    }

}
