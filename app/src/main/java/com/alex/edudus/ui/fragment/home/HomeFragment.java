package com.alex.edudus.ui.fragment.home;

import static com.afpensdk.pen.penmsg.AFPenCommManStartStatus.AFPenCommManStartStatusIsConnected;
import static com.afpensdk.pen.penmsg.AFPenCommManStartStatus.AFPenCommManStartStatusNeedLocationPermission;
import static com.afpensdk.pen.penmsg.AFPenCommManStartStatus.AFPenCommManStartStatusPowerOff;
import static com.afpensdk.pen.penmsg.AFPenCommManStartStatus.AFPenCommManStartStatusSucceed;
import static com.afpensdk.pen.penmsg.AFPenCommManStartStatus.AFPenCommManStartStatusUnknown;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.afpensdk.pen.AFOTAUpdaterStatus;
import com.afpensdk.pen.DPenCtrl;
import com.afpensdk.pen.IAFPenReport;
import com.afpensdk.pen.penmsg.IAFPenDotListener;
import com.afpensdk.pen.penmsg.IAFPenOfflineDataListener;
import com.afpensdk.pen.penmsg.JsonTag;
import com.afpensdk.pen.penmsg.PenGripStyle;
import com.afpensdk.pen.penmsg.PenMsgType;
import com.afpensdk.structure.AFDot;
import com.afpensdk.util.LogUtil;
import com.alex.edudus.AppController;
import com.alex.edudus.R;
import com.alex.edudus.databinding.FragmentHomeBinding;
import com.alex.edudus.BaseFragment;
import com.alex.edudus.databinding.FragmentNotificationsBinding;
import com.alex.edudus.utility.AFStroke;
import com.alex.edudus.utility.CanvasFrame;
import com.alex.edudus.utility.Const;
import com.alex.edudus.utility.Glo;
import com.alex.edudus.utility.LogUtils;
import com.alex.edudus.utility.PenClientCtrl;
import com.alex.edudus.utility.SignatureView;
import com.alex.edudus.utility.TouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HomeFragment extends BaseFragment  implements IAFPenOfflineDataListener {

    private static final String TAG = "HomeFragment";
    private Queue<AFDot> mDotQueueForBroadcast = null;
    private DotConsumerForBroadcastThread mBroadcastThread = null;
    Toast toast;

    CanvasFrame canvasFrame;
    SignatureView mStrokeView;
    Context mContext;
    private Toolbar mNormalToolbar;
    ArrayList<AFDot> cacheOfflineDots;
    boolean bClearLog;
    PenGripStyle lastAngleState = PenGripStyle.PenGripStyleNormal;

    String lastMessage = "";

    ArrayList<AFStroke> lastDot;

    String StringForAngle(PenGripStyle angle){
        String res = "";
        switch (angle){
            case PenGripStyleApprox00_10:
                Log.d(TAG, "StringForAngle: ");
                res = "傾斜＜10°";
                break;
            case PenGripStyleNormal:
                res = "筆桿正常";
                break;
            case PenGripStyleNA:
                res = "筆桿反握";
                break;
            case PenGripStyleApprox10_20:
                res = "傾斜10°~20°";
                break;
            case PenGripStyleApprox15_25:
                res = "傾斜15°~25°";
                break;
            case PenGripStyleApprox20_30:
                res = "傾斜20°~30°";
                break;
            case PenGripStyleApprox25_35:
                res = "傾斜25°~35°";
                break;
            case PenGripStyleApprox30_40:
                res = "傾斜30°~40°";
                break;
            case PenGripStyleApprox35_45:
                res = "傾斜35°~45°";
                break;
            case PenGripStyleApprox40_50:
                res = "傾斜40°~50°";
                break;
        }
        return res;
    }

  /*  @Override public boolean onCreateOptionsMenu(Menu menu) {
        //TODO getMenuInflater().inflate(R.menu.topmenu, menu);
        getMenuInflater().inflate(R.menu.topmenu_basic, menu);
       *//* if(mNormalToolbar.getMenu().findItem(R.id.action_item2)!=null) {
            mNormalToolbar.getMenu().findItem(R.id.action_item2).setEnabled(false);
        }*//*

        return true;
    }*/

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
      /*  NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);*/

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvLog.setClickable(false);

        binding.freeDrawLayout.post(new Runnable() {
            public void run() {
                canvasFrame = new CanvasFrame(getContext());
                mStrokeView = canvasFrame.bDrawl;
                Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.notebk1420);
                canvasFrame.bDrawl.setSignatureBitmap(bmp);

                binding.freeDrawLayout.addView(canvasFrame);
                canvasFrame.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener()
                        {
                            @Override
                            public void onGlobalLayout()
                            {
                                canvasFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                binding.freeDrawLayout.setOnTouchListener(new TouchListener(canvasFrame, binding.freeDrawLayout.getWidth(),binding.freeDrawLayout.getHeight(),canvasFrame.getMeasuredWidth(),canvasFrame.getMeasuredHeight()));

                            }
                        });
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mContext = getContext();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String s = pref.getString("PaperSizeSetting","A5");
        PenClientCtrl.getInstance(mContext).setPaperSizeByKey(s,0,0);

        h = new MyOTAHandler(getActivity());
        DPenCtrl.getInstance().setUpdaterListener(new IAFPenReport() {
            @Override
            public void onStatus(@AFOTAUpdaterStatus int status, int progress) {
                Message m = Message.obtain(h);
                m.arg1 = status;
                m.arg2 = progress;
                h.sendMessage(m);

                Log.d("IAFPenReport","status="+status + " p="+progress);
            }
        });
        if(s.equals("Custom")){
            String sw = pref.getString("custom_width","3496");
            String sh = pref.getString("custom_height","4960");
            int customw;
            int customh;
            try{
                customw = Integer.parseInt(sw);
                customh = Integer.parseInt(sh);
            }catch (Exception e){
                customw = Const.VAR.DEFAULT_PAPER_A5W;
                customh = Const.VAR.DEFAULT_PAPER_A5H;
            }
            PenClientCtrl.getInstance(mContext).setPaperSizeByKey("Custom",customw,customh);
        }

//        boolean bEnableJitter = pref.getBoolean()
        // TODO mNormalToolbar.setOnMenuItemClickListener(

        chkPermissions();
//        PenCtrlInit();

    }

    MyOTAHandler h;
    static class MyOTAHandler extends Handler {
        WeakReference<Activity> mActivityReference;

        ProgressDialog pD;
        androidx.appcompat.app.AlertDialog aD;
        MyOTAHandler(Activity activity) {
            mActivityReference= new WeakReference<Activity>(activity);
            pD = new ProgressDialog(activity);
            pD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            aD = new androidx.appcompat.app.AlertDialog.Builder(activity)

                    .create();
        }
        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivityReference.get();
            int status = msg.arg1;
            int progress = msg.arg2;
            if(status == AFOTAUpdaterStatus.WriteOTAReady){
                DPenCtrl.getInstance().requestOTAWriteStart();
            }else if(status == AFOTAUpdaterStatus.WriteOTAReport){
                if(!pD.isShowing())
                    pD.show();
                pD.setTitle("writing ota " + progress+"%");
                pD.setProgress(progress);
            }else if(status == AFOTAUpdaterStatus.WriteOTADone){
                pD.dismiss();
                aD.setTitle("ota write done");
                aD.dismiss();
                aD.show();
            }else if(status == AFOTAUpdaterStatus.FWWriteReport){
                aD.dismiss();
                pD.setTitle("update fw");
                pD.setProgress(progress);
                if(!pD.isShowing())
                    pD.show();
                if(progress==100) {
                    pD.dismiss();
                    aD.setTitle("fw update done");
                    aD.show();
                }

            }else if(status == AFOTAUpdaterStatus.StatusUnknown){
                pD.dismiss();
                aD.setTitle("update fw StartUpdateFWFailed");
                aD.show();
            }else if(status == AFOTAUpdaterStatus.WokringModeChanged){
                pD.dismiss();
                aD.setTitle("changed workmode");
                aD.show();
            }else{
                pD.dismiss();
                aD.setTitle("err="+status);
                aD.show();
            }
        }
    }

    void updateInfo(){
        if(DPenCtrl.getInstance().getPenStatus()==4){

//            int batval = Glo.getInstance().batLvNormalize(lastFetchedBatLv);
            binding.tvInfo.setText(
                    DPenCtrl.getInstance().getConnectedDevice() + "\n\n" +
                            DPenCtrl.getInstance().getFWVer()+ "\n\n" +
                            StringForAngle(lastAngleState)
//                            DPenCtrl.getInstance().GetFlashCapacity() + " (bytes)\n\n" +
//                            "battery= " +(batval==0?"":batval+"%")
            );
        }else{
            
            binding.tvInfo.setText("");
        }
    }

    private void chkPermissions ()
    {
        if( Build.VERSION.SDK_INT >= 23)
        {
            int gpsPermissionCheck = ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION );
            int scanPermission = ContextCompat.checkSelfPermission( mContext, Manifest.permission.BLUETOOTH_SCAN );
            int connectPermission = ContextCompat.checkSelfPermission( mContext, Manifest.permission.BLUETOOTH_CONNECT );
            final int writeExternalPermissionCheck = ContextCompat.checkSelfPermission( mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE );
//
            if(Build.VERSION.SDK_INT >= 31){
                ArrayList<String> permissions = new ArrayList<String>();
                if(scanPermission == PackageManager.PERMISSION_DENIED)
                    permissions.add( Manifest.permission.BLUETOOTH_SCAN );
                if(connectPermission == PackageManager.PERMISSION_DENIED)
                    permissions.add( Manifest.permission.BLUETOOTH_CONNECT );
                if(permissions.size() > 0){
                    requestPermissions( permissions.toArray( new String[permissions.size()] ), Const.REQ_ANDROID12_BLUETOOTH );
                }else{
                    PenCtrlInit();
                }
            }else{
                if(gpsPermissionCheck == PackageManager.PERMISSION_DENIED || writeExternalPermissionCheck == PackageManager.PERMISSION_DENIED)
                {
                    ArrayList<String> permissions = new ArrayList<String>();
                    if(gpsPermissionCheck == PackageManager.PERMISSION_DENIED)
                        permissions.add( Manifest.permission.ACCESS_FINE_LOCATION );
                    if(writeExternalPermissionCheck == PackageManager.PERMISSION_DENIED)
                        permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
                    requestPermissions( permissions.toArray( new String[permissions.size()] ),  Const.REQ_GPS_EXTERNAL_PERMISSION );
                }else{
                    PenCtrlInit();
                }
            }

        }else{
            PenCtrlInit();
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, String[] permissions, int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==  Const.REQ_GPS_EXTERNAL_PERMISSION) {
            boolean bGrantedExternal = false;
            boolean bGrantedGPS = false;
            int gpsPermissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
            final int writeExternalPermissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (gpsPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                bGrantedGPS = true;
            }
            if (writeExternalPermissionCheck == PackageManager.PERMISSION_GRANTED)
                bGrantedExternal = true;

            if (!bGrantedExternal || !bGrantedGPS) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Permission Check");
                builder.setMessage("PERMISSION_DENIED");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
//                builder.setCancelable( false );
                builder.create().show();
            } else {
                PenCtrlInit();
            }
        } else if (requestCode ==  Const.REQ_ANDROID12_BLUETOOTH) {

        }
    }

    void PenCtrlInit(){
//        DPenCtrl.setDotDListener(iafPenDotDListener);
        PenClientCtrl.getInstance(mContext).setDotListener( mPenReceiveDotListener );
        Log.e("main", DPenCtrl.getInstance().getVersion());
        int result = PenClientCtrl.getInstance( mContext ).btStartForPeripheralsList();
        switch (result){
            case AFPenCommManStartStatusSucceed:
                break;
            case AFPenCommManStartStatusIsConnected:
                Log.e("main", "already connected");
                break;
            case AFPenCommManStartStatusPowerOff:
                Log.e("main", "bluetooth maybe closed");
                break;
            case AFPenCommManStartStatusUnknown:
                Log.e("main", "other error");
                break;
            case AFPenCommManStartStatusNeedLocationPermission:
                Log.e("main", "no location permission");
                break;
            default:
                break;
        }
        DPenCtrl.getInstance().setOffLineDataListener(this);
        mDotQueueForBroadcast = new ConcurrentLinkedQueue<>();

        mBroadcastThread = new DotConsumerForBroadcastThread();
        mBroadcastThread.setDaemon( true );
        mBroadcastThread.start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBroadcastThread != null)
            mBroadcastThread.interrupt();
    }
    @Override
    public void onPause()
    {
        super.onPause();

        getContext().unregisterReceiver(mPenStatusBroadcastReceiver);

    }

    @Override
    public void onResume()
    {
        super.onResume();

        IntentFilter filter = new IntentFilter( Const.Broadcast.ACTION_PEN_MESSAGE );
        filter.addAction( Const.Broadcast.ACTION_PEN_DOT );
        filter.addAction( Const.Broadcast.ACTION_SYNC_PROGRESS );
        getContext().registerReceiver(mPenStatusBroadcastReceiver, filter );

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppController.context);
        boolean sh = pref.getBoolean("EnableDejitter",true);

        if(sh) {
            DPenCtrl.getInstance().enableJitter(true);
        }else{
            DPenCtrl.getInstance().enableJitter(false);
        }
        updateInfo();
    }
    private void handleMsg(int penMsgType, Intent intent)
    {
        String statustxt = "";
        switch ( penMsgType )
        {
            case PenMsgType.PEN_REQ_OFFLINESECTIONINFO:{
                LogUtils.ToastMessage(getContext(),getString(R.string.copy_to_clipboard),true);
            }break;
            case PenMsgType.PEN_SET_OFFLINESECTIONINFO:{
                
                LogUtils.ToastMessage(getContext(),getString(R.string.done),true);
            }break;
            case PenMsgType.PEN_CUR_MEMOFFSET: {
                int penoffset = intent.getIntExtra(JsonTag.INT_DOTS_MEMORY_OFFSET, 0);
                binding.tvInfo.setText(String.valueOf(penoffset));
                break;
            }
            case PenMsgType.PEN_CONNECTION_SUCCESS:
                
                statustxt = getString(R.string.connection_is_successful);
                Log.e(TAG, statustxt);
                LogUtils.ToastMessage(getContext(),statustxt,true);
                break;
            case PenMsgType.PEN_DISCONNECTED:
                statustxt = getString(R.string.disconnect);
                updateInfo();
                Log.e(TAG, statustxt);
                LogUtils.ToastMessage(getContext(),statustxt,true);
                break;
            case PenMsgType.PEN_TRANSFER_TIMEOUT:{
                statustxt ="PEN_TRANSFER_TIMEOUT.";
                Log.e(TAG, statustxt);
                LogUtils.ToastMessage(getContext(),statustxt,true);
                break;
            }
            case PenMsgType.PEN_CONNECTING:{
                statustxt ="PEN_CONNECTING.";
                Log.e(TAG, statustxt);
                LogUtils.ToastMessage(getContext(),statustxt,true);

                break;
            }
            case PenMsgType.PEN_CONNECTION_FAILURE:{
                statustxt ="PEN_CONNECTION_FAILURE.";
                Log.e(TAG, statustxt);
                LogUtils.ToastMessage(getContext(),statustxt,true);
                break;
            }
            case PenMsgType.PEN_DISCONNECTING:{
                statustxt ="PEN_DISCONNECTING";
                Log.e(TAG, statustxt);
                LogUtils.ToastMessage(getContext(),statustxt,true);
                break;
            }
            case PenMsgType.PEN_CONNECTION_TRY: {
                statustxt ="PEN_CONNECTION_TRY.";
                Log.e(TAG, statustxt);
                LogUtils.ToastMessage(getContext(),statustxt,true);
                break;
            }case PenMsgType.PEN_FW_VER: {
            String strFWVer = intent.getStringExtra(JsonTag.STRING_PEN_FW_VERSION);
            LogUtils.ToastMessage(getContext(),statustxt,true);

            break;
        }case PenMsgType.PEN_CUR_BATT: {
            int battlv = intent.getIntExtra(JsonTag.INT_BATT_VAL,0);
            LogUtils.ToastMessage(getContext(),battlv+"",true);
            updateInfo();
            
            break;
        }
        }
    }
    private void handleDot(AFDot dot )
    {
//        String s1 = String.format("X=%d Y=%d type=%d page=%d bn=%d bw=%d bw=%d",dot.X,dot.Y,dot.type,dot.page,dot.book_no,dot.book_width,dot.book_height);
        if(Glo.getInstance().bEnableShowDotLog) {
            String s2 = String.format("X=%d Y=%d t=%d p=%d pr=%d\n", (int)dot.X, (int)dot.Y, dot.type, dot.page, dot.getFieldRv1());
            Log.d(TAG, "handleDot: message s2"+s2);
            if (bClearLog) {
                binding.tvLog.setText("");
                bClearLog = false;
            }

            if (dot.type == 2)
                bClearLog = true;
            binding.tvLog.append(s2);
        }
        mStrokeView.addDot(dot);
    }
    private class OfflineTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            final List<AFDot> dots = (List<AFDot>)objects[0];
            final JSONObject info = (JSONObject)objects[1];

            LogUtil.e(info.toString());

            try {
                String logs = String.format("%d/%d",info.getInt("readedCnt"),info.getInt("totalCnt"));
                Intent intent = new Intent( Const.Broadcast.ACTION_SYNC_PROGRESS);
                intent.putExtra( "toast", logs);
                getContext().sendBroadcast( intent );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //for (int i = 0; i < dots.size(); i++) {
            //handle dot
            //  AFDot dot = dots.get(i);
            //LogUtil.e(String.format("X=%d Y=%d t=%d p=%d",dot.X,dot.Y,dot.type,dot.page));
            //}
            cacheOfflineDots = new ArrayList<>();
            cacheOfflineDots.addAll(dots);
            mStrokeView.addDots(cacheOfflineDots);
            cacheOfflineDots.clear();
            try {
                if (info.getInt("readedCnt") == info.getInt("totalCnt")) {
//test again
//                mStrokeView.strokes.clear();
//                mStrokeView.invalidate();
//                boolean result = DPenCtrl.getInstance().requestOfflineDataWithRange(0,PenClientCtrl.getInstance(mContext).lastDotsCount);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void values) {
            super.onPostExecute(values);
        }

    }


    @Override
    public void offlineDataDidReceivePenData(List<AFDot> dots, JSONObject info) {
        /* example 0~5348
         E/afsdk: {"readCnt":3200,"readedCnt":3200,"totalCnt":5348}
         E/afsdk: {"readCnt":2148,"readedCnt":5348,"totalCnt":5348}
        * */

        OfflineTask task = new OfflineTask();
        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,dots,info);

    }

    private BroadcastReceiver mPenStatusBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent )
        {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: PenAction name" +action);
            if ( Const.Broadcast.ACTION_PEN_MESSAGE.equals( action ) ) {
                int penMsgType = intent.getIntExtra( Const.Broadcast.MESSAGE_TYPE, 0 );
                handleMsg(penMsgType, intent);
            }
            else if ( Const.Broadcast.ACTION_PEN_DOT.equals( action ) ) {
                AFDot dot = intent.getParcelableExtra(Const.Broadcast.EXTRA_DOT );
                handleDot(dot );
            }else if ( Const.Broadcast.ACTION_SYNC_PROGRESS.equals( action ) ) {
                if(toast!=null) {
                    toast.cancel();
                }
                toast = Toast.makeText(mContext, intent.getStringExtra("toast"), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    private void broadcastDot (AFDot dot ) {
        Intent intent = new Intent( Const.Broadcast.ACTION_PEN_DOT);
        intent.putExtra( Const.Broadcast.EXTRA_DOT, dot);
        getContext().sendBroadcast( intent );
    }
    private final IAFPenDotListener mPenReceiveDotListener = new IAFPenDotListener() {
        @Override
        public void onReceiveDot (AFDot dot ) {
            enqueueDotForBroadcast(dot);
        }

        @Override
        public void onReceiveAngle(boolean isReverse, PenGripStyle penGripStyle) {
            if(! lastAngleState.equals(penGripStyle)){
                lastAngleState = penGripStyle;
                updateInfo();
            }
        }

    };

    private void enqueueDotForBroadcast(AFDot dot){
        mDotQueueForBroadcast.offer(dot);
        synchronized (mDotQueueForBroadcast) {
            mDotQueueForBroadcast.notifyAll();
        }
    }


    private final class DotConsumerForBroadcastThread extends Thread {
        @Override
        public void run() {
            setName( this.getClass().getSimpleName() );
            while(true){
                while(!mDotQueueForBroadcast.isEmpty()){
                    AFDot dot  = mDotQueueForBroadcast.poll();
                    if(dot != null){
                        broadcastDot(dot);
                    }
                }
                try{
                    synchronized (mDotQueueForBroadcast) {
                        mDotQueueForBroadcast.wait();
                    }
                } catch (InterruptedException e){
                }
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0x01){
            if(data!=null) {
//                Uri
                Uri uri = data.getData();
                try {

                    ContentResolver r = getContext().getContentResolver();
                    ParcelFileDescriptor mParcelFileDescriptor = r.openFileDescriptor(uri, "r");
                    long fSize = mParcelFileDescriptor.getStatSize();
                    byte[] data1 = new byte[(int) (fSize)];
                    FileDescriptor fd = mParcelFileDescriptor.getFileDescriptor();
                    FileInputStream fileStream = new FileInputStream(fd);
                    fileStream.read(data1);
                    DPenCtrl.getInstance().sendOTAFile(data1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}