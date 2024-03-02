package com.alex.edudus.ui.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afpensdk.pen.penmsg.PenMsgType;
import com.alex.edudus.R;
import com.alex.edudus.utility.Const;
import com.alex.edudus.utility.LogUtils;
import com.alex.edudus.utility.PenClientCtrl;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceListActivity extends AppCompatActivity {

    ArrayList<PenInfo> temp = new  ArrayList<>();

    boolean bConnecting;
    Timer scanTimer;
    public class PenInfo {
        public String PenName;
        public String PenAddress;
        public int rssi;
    }
    RecyclerView mRecyclerView;
    PenInfoAdapter mPenInfoAdapter;
    String lastTryConnect;
    ProgressDialog pd;
    Context _context;
    TextView tvStatus;
    ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        penClientCtrl = PenClientCtrl.getInstance( getApplicationContext() );
        // Setup the window
        _context = this;
        setContentView( R.layout.activity_device_list );
        tvStatus = findViewById(R.id.statusstr);
        btnBack = findViewById(R.id.btnBack);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*
        // 默认系统的divider
dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
// 自定义图片drawable分的divider
dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, getResources().getDrawable(R.drawable.ic_launcher));
// 自定义无高宽的drawable的divider - 垂直列表
dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, new ColorDrawable(Color.parseColor("#ff00ff")));
dividerItemDecoration.setHeight(1);
// 自定义无高宽的drawable的divider - 水平列表
dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST, new ColorDrawable(Color.parseColor("#ff00ff")));
dividerItemDecoration.setWidth(1);
// 自定义带边距且无高宽的drawable的divider（以上面InsetDrawable为例子）
// 这个地方也可以在drawable的xml文件设置size指定宽高，效果一样
dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST, getResources().getDrawable(R.drawable.list_divider));
dividerItemDecoration.setWidth(DisplayLess.$dp2px(16) + 1);
        * */
        String blestatus = "Bluetooth ON/OFF : ";
        String gpspermission = "GPS Permission YES/NO : ";
        String gpsstatus = "GPS ON/OFF : ";
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            blestatus += "ERROR";
        } else if (!mBluetoothAdapter.isEnabled()) {
            blestatus += "OFF";
        } else {
            blestatus += "ON";
        }
        int gpsPermissionCheck = ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION );
        if(gpsPermissionCheck == PackageManager.PERMISSION_DENIED){
            gpspermission += "NO";
        }else{
            gpspermission += "YES";
        }

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            gpsstatus += "OFF";
        }else{
            gpsstatus += "ON";
        }

        tvStatus.setText("Bluetooth Scanning"+"\n"+
                blestatus +"\n" +
                gpspermission+"\n"+
                gpsstatus+"\n\n"
        );


//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, new ColorDrawable(Color.parseColor("#696969")));
//        dividerItemDecoration.setHeight(1);
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mPenInfoAdapter = new PenInfoAdapter(temp);
        mPenInfoAdapter.listener = new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                PenClientCtrl.getInstance(getApplicationContext()).connect(temp.get(position));
                bConnecting = true;
                lastTryConnect = temp.get(position).PenAddress;
                pd = new ProgressDialog(_context);
                pd.setMessage("connecting");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.show();
            }
        };

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // Find and set up the ListView for newly discovered devices.

//        View emptyview = getLayoutInflater().inflate(R.layout.emptyview_searching, null);
//        mPenInfoAdapter.setEmptyView(emptyview);
        mRecyclerView.setAdapter(mPenInfoAdapter);


        IntentFilter filter = new IntentFilter( Const.Broadcast.ACTION_FIND_DEVICE );
        filter.addAction(Const.Broadcast.ACTION_PEN_MESSAGE);
        registerReceiver( mReceiver, filter );
        scanTimer = new Timer();
/*      scanTimer.schedule(new TimerTask() {
          @Override
           public void run() {
               temp.clear();
                PenClientCtrl.getInstance(getApplicationContext()).btStopSearchPeripheralsList();
                PenClientCtrl.getInstance(getApplicationContext()).btStartForPeripheralsList();
            }
        }, 5000);*/
        PenClientCtrl.getInstance(getApplicationContext()).btStopSearchPeripheralsList();
        PenClientCtrl.getInstance(getApplicationContext()).btStartForPeripheralsList();
        bConnecting = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

//        unregisterReceiver( mReceiver );

    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
        if(scanTimer!=null) {
            scanTimer.cancel();
        }
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if ( Const.Broadcast.ACTION_FIND_DEVICE.equals( action ) )
            {
                String penAddress = intent.getStringExtra( Const.Broadcast.PEN_ADDRESS );
                String penName = intent.getStringExtra( Const.Broadcast.PEN_NAME );
                int rssi = intent.getIntExtra( Const.Broadcast.PEN_RSSI, 0 );
                PenInfo mPenInfo = new PenInfo();
                mPenInfo.PenAddress = penAddress;
                mPenInfo.PenName = penName;
                mPenInfo.rssi = rssi;
                temp.add(0, mPenInfo);
                mPenInfoAdapter.notifyDataSetChanged();

            }
            else if( Const.Broadcast.ACTION_PEN_MESSAGE.equals( action ) ){
                int penMsgType = intent.getIntExtra( Const.Broadcast.MESSAGE_TYPE, 0 );
                if(penMsgType == PenMsgType.PEN_CONNECTION_SUCCESS){
//                    SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences( AppController.getInstance().getApplicationContext() ).edit();
//                    edit.putString("APP_LAST_CONNECT_ADDR",lastTryConnect);
//                    edit.apply();
                    if(pd!=null)
                        pd.dismiss();
                    finish();
                }else if(penMsgType == PenMsgType.PEN_CONNECTION_FAILURE){

                    if(pd!=null)
                        pd.dismiss();
                    LogUtils.ToastMessage( DeviceListActivity.this, "Connect fail please retry",true );

                }
            }
        }
    };
    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position);
    }
    class PenInfoAdapter extends RecyclerView.Adapter<PenInfoAdapter.ViewHolder> {
        private List<PenInfo> mData;
        public RecyclerViewClickListener listener;
        PenInfoAdapter(List<PenInfo> data) {
            mData = data;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            // 宣告元件
            private TextView txtItem;

            ViewHolder(View itemView) {
                super(itemView);
                txtItem = (TextView) itemView.findViewById(R.id.tvName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                listener.recyclerViewListClicked(v, this.getLayoutPosition());
            }
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 連結項目布局檔list_item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scan_peninfo_rvitem, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // 設置txtItem要顯示的內容
            holder.txtItem.setText(mData.get(position).PenName + " " + mData.get(position).rssi + "dBm");
        }
        @Override
        public int getItemCount() {
            return mData.size();
        }

    }
}
