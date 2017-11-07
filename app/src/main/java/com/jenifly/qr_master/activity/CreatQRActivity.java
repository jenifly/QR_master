package com.jenifly.qr_master.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jenifly.qr_master.R;
import com.jenifly.qr_master.helper.ContentHelper;
import com.jenifly.qr_master.helper.Item;
import com.jenifly.qr_master.helper.JyConstants;
import com.jenifly.qr_master.helper.QRCode;
import com.jenifly.qr_master.tool.FileTool;
import com.jenifly.qr_master.tool.JySPTool;
import com.jenifly.qr_master.view.JyToast;
import com.jenifly.qr_master.view.dialog.JyDialogSetting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatQRActivity extends AppCompatActivity {

    @BindView(R.id.contents) EditText etContents;
    @BindView(R.id.qrcode) ImageView qrCodeImageView;
    @BindView(R.id.backgroundImage) Button btSelectBG;
    @BindView(R.id.removeBackgroundImage) Button btRemoveBackgroundImage;
    @BindView(R.id.logoImage) Button btSelectLogo;
    @BindView(R.id.removeLogoImage) Button btRemoveLogoImage;
    @BindView(R.id.generate) Button btGenerate;

    private final int BKG_IMAGE = 822;
    private final int LOGO_IMAGE = 379;
    private Bitmap backgroundImage = null;
    private AlertDialog progressDialog;
    private boolean generating = false;
    private Bitmap qrBitmap;
    private Bitmap logoImage;
    private String QR_Content;
    private JyDialogSetting jyDialogSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        jyDialogSetting = new JyDialogSetting(this);
        btRemoveBackgroundImage.setEnabled(false);
        btRemoveLogoImage.setEnabled(false);
    }

    @OnClick({R.id.backgroundImage, R.id.removeBackgroundImage, R.id.logoImage, R.id.removeLogoImage,
            R.id.generate, R.id.creatqr_back, R.id.creatqr_menu})void click(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.backgroundImage:
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(intent, BKG_IMAGE);
                break;
            case R.id.removeBackgroundImage:
                backgroundImage = null;
                btSelectBG.setText("选择");
                btRemoveBackgroundImage.setEnabled(false);
                JyToast.success(this, "背景图片已清除", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logoImage:
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(intent, LOGO_IMAGE);
                break;
            case R.id.removeLogoImage:
                logoImage = null;
                btSelectLogo.setText("选择");
                btRemoveLogoImage.setEnabled(false);
                JyToast.success(this, "头像图片已清除", Toast.LENGTH_SHORT).show();
                break;
            case R.id.generate:
                QR_Content = etContents.getText().length() == 0 ? "这是测试文字！" : etContents.getText().toString();
                try {
                    generate();
                    btGenerate.setText("完成");
                    btGenerate.setEnabled(false);
                } catch (Exception e) {
                    JyToast.error(CreatQRActivity.this, "创建错误，请检查你的设置。", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.creatqr_back:
                finish();
                break;
            case R.id.creatqr_menu:
                jyDialogSetting.show();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        acquireStoragePermissions();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void acquireStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                if (requestCode == BKG_IMAGE) {
                    backgroundImage = BitmapFactory.decodeFile(ContentHelper.absolutePathFromUri(this, imageUri));
                    btSelectBG.setText("已选择");
                    btRemoveBackgroundImage.setEnabled(true);
                    JyToast.success(this, "背景图片添加成功", Toast.LENGTH_SHORT).show();
                } else if (requestCode == LOGO_IMAGE) {
                    logoImage = BitmapFactory.decodeFile(ContentHelper.absolutePathFromUri(this, imageUri));
                    JyToast.success(this, "头像图片添加成功", Toast.LENGTH_SHORT).show();
                    btRemoveLogoImage.setEnabled(true);
                    btSelectLogo.setText("已选择");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (requestCode == BKG_IMAGE) {
                    JyToast.error(this, "背景图片添加失败", Toast.LENGTH_SHORT).show();
                } else if (requestCode == LOGO_IMAGE) {
                    JyToast.error(this, "头像图片添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generate() {
        final String contents = QR_Content;
        final int size = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_SIZE, 800);
        final int margin = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_MARGIN, 20);
        final float dotScale = JySPTool.getFloat(getBaseContext(), JyConstants.SP_QR_DOTSCALE, 0.3f);
        final int colorDark = JySPTool.getString(getBaseContext(), JyConstants.SP_QR_COLORDARK, "0") == "0" ?
                Color.BLACK : Color.parseColor(JySPTool.getString(getBaseContext(), JyConstants.SP_QR_COLORDARK, "0"));
        final int colorLight = JySPTool.getString(getBaseContext(), JyConstants.SP_QR_COLORLIGHT, "1") == "1" ?
                Color.WHITE : Color.parseColor(JySPTool.getString(getBaseContext(), JyConstants.SP_QR_COLORLIGHT, "1"));
        final boolean whiteMargin = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_WHITEMARGIN, 1) == 1 ? true : false;
        final boolean autoColor = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_AUTOCOLOR, 1) == 1 ? true : false;
        final boolean binarize = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_BINARIZE, 0) == 1 ? true : false;
        final int binarizeThreshold = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_BINARIZETHRESHOLD, 0);
        final boolean roundedDD = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_ROUNDEDDATADOTS, 1) == 1 ? true : false;
        final int logoMargin = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_LOGOMARGIN, 10);
        final int logoCornerRadius = JySPTool.getInt(getBaseContext(), JyConstants.SP_QR_LOGOCORNERRADIUS, 8);
        final float logoScale = JySPTool.getFloat(getBaseContext(), JyConstants.SP_QR_LOGOSCALE, 10);
        if (generating) return;
        generating = true;
        progressDialog = new ProgressDialog.Builder(this).setMessage("生成中...").setCancelable(false).create();
        progressDialog.show();
        new QRCode.Renderer().contents(contents)
                .size(size).margin(margin).dotScale(dotScale)
                .colorDark(colorDark).colorLight(colorLight)
                .background(backgroundImage).whiteMargin(whiteMargin)
                .autoColor(autoColor).roundedDots(roundedDD)
                .binarize(binarize).binarizeThreshold(binarizeThreshold)
                .logo(logoImage).logoMargin(logoMargin)
                .logoRadius(logoCornerRadius).logoScale(logoScale)
                .renderAsync(new QRCode.Callback() {
                    @Override
                    public void onRendered(QRCode.Renderer renderer, final Bitmap bitmap) {
                        qrBitmap = bitmap;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                qrCodeImageView.setImageBitmap(qrBitmap);
                                saveBitmap(qrBitmap);
                                if (progressDialog != null) progressDialog.dismiss();
                                generating = false;
                            }
                        });
                    }

                    @Override
                    public void onError(QRCode.Renderer renderer, Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog != null) progressDialog.dismiss();
                                generating = false;
                            }
                        });
                    }
                });
    }

    private void saveBitmap(Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            String fileName = String.valueOf(System.currentTimeMillis());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            File outputFile = new File(FileTool.getCecheFolder(), fileName);// + ".png");
            fos = new FileOutputStream(outputFile);
            fos.write(byteArray);
            fos.close();
            JySPTool.putInt(this, JyConstants.SP_MADE_CODE, JySPTool.getInt(this, JyConstants.SP_MADE_CODE, 0) + 1);
            JyConstants.sqlBaseHelper.insertQR(new Item(fileName,QR_Content));
            JyToast.success("二维码创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            JyToast.error("二维码创建失败");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }
}
