package com.jenifly.qr_master.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.jenifly.qr_master.R;
import com.jenifly.qr_master.SQLHelper.SQLBaseHelper;
import com.jenifly.qr_master.adapter.ItemAdapter;
import com.jenifly.qr_master.helper.Item;
import com.jenifly.qr_master.helper.JyConstants;
import com.jenifly.qr_master.helper.SimpleItemTouchHelperCallback;
import com.jenifly.qr_master.interfaces.OnItemClickListener;
import com.jenifly.qr_master.interfaces.OnStartDragListener;
import com.jenifly.qr_master.tool.FileTool;
import com.jenifly.qr_master.tool.JySPTool;
import com.jenifly.qr_master.tool.JyTool;
import com.jenifly.qr_master.view.JyToast;
import com.jenifly.qr_master.view.dialog.JyDialogCheck;
import com.jenifly.qr_master.view.dialog.JyDialogSure;
import com.jenifly.qr_master.view.ticker.JyTickerUtils;
import com.jenifly.qr_master.view.ticker.JyTickerView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements OnStartDragListener {

    @BindView(R.id.ticker_scan_count) JyTickerView mRxTickerViewScan;
    @BindView(R.id.ticker_made_count) JyTickerView mRxTickerViewMade;
    @BindView(R.id.qrcodeList) RecyclerView qrcodeList;
    @BindView(R.id.tvCount) TextView tvCount;

    private JyDialogSure jyDialogSure;
    private JyDialogCheck jyDialogCheck;
    private ItemTouchHelper mItemTouchHelper;
    private ItemAdapter itemAdapter;

    private static final char[] NUMBER_LIST = JyTickerUtils.getDefaultNumberList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        JyTool.init(this);
        init();
    }

    private void init(){
        File folder = new File(Environment.getExternalStorageDirectory(),"QRMaster");
        if (!folder.exists()) {
            folder.mkdir();
            FileTool.getCecheFolder();
        }
        JyConstants.sqlBaseHelper = new SQLBaseHelper(this);
        JyConstants.CachePath = FileTool.getCecheFolder().getPath() + File.separator;
        mRxTickerViewMade.setCharacterList(NUMBER_LIST);
        mRxTickerViewScan.setCharacterList(NUMBER_LIST);
        updateMadeCodeCount();
        assert qrcodeList != null;
        qrcodeList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        qrcodeList.setLayoutManager(llm);
        itemAdapter = new ItemAdapter(this, this, tvCount);
        qrcodeList.setAdapter(itemAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(itemAdapter,this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(qrcodeList);
        jyDialogSure = new JyDialogSure(this);
        jyDialogCheck = new JyDialogCheck(this);
        itemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final Item item) {
                final File file = new File(JyConstants.CachePath + item.getItemName());
                if (file != null && file.exists() && file.isFile()) {
                    final  Uri u = Uri.fromFile(file);
                    jyDialogCheck.getLogoView().setImageURI(u);
                    jyDialogCheck.setContent("内容："+ item.getItemContent());
                    jyDialogCheck.getShareView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jyDialogCheck.cancel();
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/png");
                            intent.putExtra(Intent.EXTRA_STREAM, u);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(intent, "二维码小工具"));
                        }
                    });
                    jyDialogCheck.getExortView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileTool.copyFile(JyConstants.CachePath + File.separator + item.getItemName(),
                                    FileTool.getPublicFolder().getPath() + File.separator + item.getItemContent().substring(0,4) + ".png");
                            JyToast.success("二维码已导出到：" +  FileTool.getPublicFolder().getPath() + "文件夹下。");
                            jyDialogCheck.cancel();
                        }
                    });
                    jyDialogCheck.getCancelView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            jyDialogCheck.cancel();
                        }
                    });
                    jyDialogCheck.show();
                }else JyToast.error("失败！");
            }
        });
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateScanCodeCount();
        updateMadeCodeCount();
        itemAdapter.setItemList(JyConstants.sqlBaseHelper.getDataList());
    }

    private void updateScanCodeCount() {
        mRxTickerViewScan.setText(JySPTool.getInt(getBaseContext(), JyConstants.SP_SCAN_CODE, 0) + "");
    }

    private void updateMadeCodeCount() {
        mRxTickerViewMade.setText(JySPTool.getInt(getBaseContext(), JyConstants.SP_MADE_CODE, 0) + "");
    }

    @OnClick({R.id.ll_qr, R.id.ll_scaner, R.id.ll_about}) void click(View view){
        switch (view.getId()){
            case R.id.ll_qr:
                startActivity( new Intent(MainActivity.this,CreatQRActivity.class));
                break;
            case R.id.ll_scaner:
                startActivity( new Intent(MainActivity.this,ScanerCodeActivity.class));
                break;
            case R.id.ll_about:
                jyDialogSure.getTitleView().setText("关于");
                jyDialogSure.setContent("说明：本工具仅为个人兴趣开发，不做任何商业用途。\n\n感谢：vondear、SumiMakito\n\n" +
                        "1439916120@qq.com\n\nby——Jenifly  2017.10.09");
                jyDialogSure.setSubContent("https://github.com/jenifly/QR_master");
                jyDialogSure.getSureView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jyDialogSure.cancel();
                    }
                });
                jyDialogSure.show();
                break;
        }
    }
}
