package com.jenifly.qr_master.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jenifly.qr_master.R;
import com.jenifly.qr_master.helper.JyConstants;
import com.jenifly.qr_master.tool.JySPTool;
import com.jenifly.qr_master.tool.JyTextTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vondear on 2016/7/19.
 * Mainly used for confirmation and cancel.
 */
public class JyDialogSetting extends JyDialog {

    @BindView(R.id.size) EditText etSize;
    @BindView(R.id.colorLight) EditText etColorLight;
    @BindView(R.id.colorDark) EditText etColorDark;
    @BindView(R.id.margin) EditText etMargin;
    @BindView(R.id.dotScale) EditText etDotScale;
    @BindView(R.id.binarizeThreshold) EditText etBinarizeThreshold;
    @BindView(R.id.logoMargin) EditText etLogoMargin;
    @BindView(R.id.logoScale) EditText etLogoScale;
    @BindView(R.id.logoRadius) EditText etLogoCornerRadius;
    @BindView(R.id.whiteMargin) CheckBox ckbWhiteMargin;
    @BindView(R.id.autoColor) CheckBox ckbAutoColor;
    @BindView(R.id.binarize) CheckBox ckbBinarize;
    @BindView(R.id.rounded) CheckBox ckbRoundedDataDots;
    @BindView(R.id.tv_ok) TextView tv_ok;
    @BindView(R.id.tv_cancel) TextView tv_cancel;

    public JyDialogSetting(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public JyDialogSetting(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public JyDialogSetting(Context context) {
        super(context);
        initView();
    }

    public JyDialogSetting(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_setting, null);
        setContentView(dialogView);
        ButterKnife.bind(this);
        etSize.setText(JySPTool.getInt(getContext(), JyConstants.SP_QR_SIZE, 800) + "");
        etMargin.setText(JySPTool.getInt(getContext(), JyConstants.SP_QR_MARGIN, 20) + "");
        etDotScale.setText(JySPTool.getFloat(getContext(), JyConstants.SP_QR_DOTSCALE, 0.3f) + "");
        etColorDark.setText(JySPTool.getString(getContext(), JyConstants.SP_QR_COLORDARK, "深色"));
        etColorLight.setText(JySPTool.getString(getContext(), JyConstants.SP_QR_COLORLIGHT, "浅色"));
        ckbWhiteMargin.setChecked(JySPTool.getInt(getContext(), JyConstants.SP_QR_WHITEMARGIN, 1) == 1 ? true : false);
        ckbAutoColor.setChecked(JySPTool.getInt(getContext(), JyConstants.SP_QR_AUTOCOLOR, 1) == 1 ? true : false);
        ckbBinarize.setChecked(JySPTool.getInt(getContext(), JyConstants.SP_QR_BINARIZE, 0) == 1 ? true : false);
        etBinarizeThreshold.setText(JySPTool.getInt(getContext(), JyConstants.SP_QR_BINARIZETHRESHOLD, 0) + "");
        ckbRoundedDataDots.setChecked(JySPTool.getInt(getContext(), JyConstants.SP_QR_ROUNDEDDATADOTS, 1) == 1 ? true : false);
        etLogoMargin.setText(JySPTool.getInt(getContext(), JyConstants.SP_QR_LOGOMARGIN, 10) + "");
        etLogoCornerRadius.setText(JySPTool.getInt(getContext(), JyConstants.SP_QR_LOGOCORNERRADIUS, 8) + "");
        etLogoScale.setText(JySPTool.getFloat(getContext(), JyConstants.SP_QR_LOGOSCALE, 10)+"");
        ckbAutoColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etColorLight.setEnabled(!isChecked);
                etColorDark.setEnabled(!isChecked);
            }
        });

        ckbBinarize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etBinarizeThreshold.setEnabled(isChecked);
            }
        });
    }

    @OnClick({R.id.tv_ok,R.id.tv_cancel}) void click(View view){
        switch (view.getId()){
            case R.id.tv_ok:
                JySPTool.putInt(getContext(), JyConstants.SP_QR_SIZE,
                        etSize.getText().length() == 0 ? 800 : Integer.parseInt(etSize.getText().toString()));
                JySPTool.putInt(getContext(), JyConstants.SP_QR_MARGIN,
                        etMargin.getText().length() == 0 ? 20 : Integer.parseInt(etMargin.getText().toString()));
                JySPTool.putInt(getContext(), JyConstants.SP_QR_BINARIZETHRESHOLD,
                        etBinarizeThreshold.getText().length() == 0 ? 128 : Integer.parseInt(etBinarizeThreshold.getText().toString()));
                JySPTool.putInt(getContext(), JyConstants.SP_QR_LOGOMARGIN,
                        etLogoMargin.getText().length() == 0 ? 10 : Integer.parseInt(etLogoMargin.getText().toString()));
                JySPTool.putInt(getContext(), JyConstants.SP_QR_LOGOCORNERRADIUS,
                        etLogoCornerRadius.getText().length() == 0 ? 8 : Integer.parseInt(etLogoCornerRadius.getText().toString()));
                JySPTool.putFloat(getContext(), JyConstants.SP_QR_LOGOSCALE,
                        etLogoScale.getText().length() == 0 ? 10 : Float.parseFloat(etLogoScale.getText().toString()));
                JySPTool.putFloat(getContext(), JyConstants.SP_QR_DOTSCALE,
                        etDotScale.getText().length() == 0 ? 0.3f : Float.parseFloat(etDotScale.getText().toString()));
                JySPTool.putInt(getContext(), JyConstants.SP_QR_AUTOCOLOR,
                        ckbAutoColor.isChecked() ? 1 : 0);
                JySPTool.putInt(getContext(), JyConstants.SP_QR_WHITEMARGIN,
                        ckbWhiteMargin.isChecked() ? 1 : 0);
                JySPTool.putInt(getContext(), JyConstants.SP_QR_BINARIZE,
                        ckbBinarize.isChecked() ? 1 : 0);
                JySPTool.putInt(getContext(), JyConstants.SP_QR_ROUNDEDDATADOTS,
                        ckbRoundedDataDots.isChecked() ? 1 : 0);
                JySPTool.putString(getContext(), JyConstants.SP_QR_COLORDARK,
                        ckbAutoColor.isChecked() ? "0" : etColorDark.getText().toString());
                JySPTool.putString(getContext(), JyConstants.SP_QR_COLORLIGHT,
                        ckbAutoColor.isChecked() ? "1" : etColorLight.getText().toString());
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
