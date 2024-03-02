package com.alex.edudus.utility;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.afpensdk.pen.penmsg.JsonTag.INT_BATT_VAL;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.afpensdk.pen.DPenCtrl;
import com.afpensdk.pen.PaperSize;
import com.afpensdk.pen.penmsg.IAFPenDotListener;
import com.afpensdk.pen.penmsg.IAFPenMsgListener;
import com.afpensdk.pen.penmsg.JsonTag;
import com.afpensdk.pen.penmsg.PenMsg;
import com.afpensdk.pen.penmsg.PenMsgType;
import com.alex.edudus.AppController;
import com.alex.edudus.ui.Activity.DeviceListActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PenClientCtrl implements IAFPenMsgListener//,IAFPenOfflineDataListener
{
    public static PenClientCtrl myInstance;

	private DPenCtrl iPenCtrl;

	private Context context;

    public String lastTryConnectAddr;
    public String lastTryConnectName;
    public int lastDotsCount;

    public int paper_w;
    public int paper_h;

    public void setPaperSizeByKey(String key,int defw, int defh){
        int w=3496,h=4961;
        if(key.equals("A4")){
            w = 4970;
            h = 7040;
        }else if(key.equals("A5")){
            w = 3496;
            h = 4961;
        }else if(key.equals("A6")){
            w = 2480;
            h = 3496;
        }else if(key.equals("B4")){
            w = 6070;
            h = 8598;

        }else if(key.equals("B5")){
            w = 4299;
            h = 6070;
        }else if(key.equals("vPaperSetting")){
            w = 4299;
            h = 6070;
        }else{
            w = (int)(defw/25.4f*600);
            h = (int)(defh/25.4f*600);
        }
        PenClientCtrl.getInstance(AppController.context).paper_h = h;
        PenClientCtrl.getInstance(AppController.context).paper_w = w;

        ArrayList<PaperSize> paperSizeList = new ArrayList<>();
//        for(int i=0;i<4;i++){
//            PaperSize pa = new PaperSize();
//            pa.width = w;
//            pa.height = h;
//            pa.pageFrom = 1+10*i;
//            pa.pageTo = 10+10*i;
//            pa.bookNum = 2+i;

        PaperSize pa = new PaperSize ();
        pa.width = w;
        pa.height = h;
        pa.pageFrom = 1;
        pa.pageTo = 1000000;
        pa.bookNum = 1;

        PaperSize pa1 = new PaperSize ();
        pa1.width = w;
        pa1.height = h;
        pa1.pageFrom = 65605;
        pa1.pageTo = 65605;
        pa1.bookNum = 2;
        pa1.flipmode = 1;

        PaperSize pa2 = new PaperSize ();
        pa2.width = w;
        pa2.height = h;
        pa2.pageFrom = 65606;
        pa2.pageTo = 1000000;
        pa2.bookNum = 3;
        paperSizeList.add(pa);
//        paperSizeList.add(pa1);
//        paperSizeList.add(pa2);



        iPenCtrl.SetPaperSize(paperSizeList);
//        iPenCtrl.SetPaperSizeMillimeter();

    }
	private PenClientCtrl(Context context )
	{
		this.context = context;

		iPenCtrl = DPenCtrl.getInstance();
		iPenCtrl.setListener( this );
        ArrayList<PaperSize> paperSizeList = new ArrayList<>();
//        PaperSize pA6 = new PaperSize ();
//        pA6.width = 7016;
//        pA6.height = 9921;
//        pA6.pageFrom = 1;
//        pA6.pageTo = 30;
//        pA6.bookNum = 3;

//        PaperSize pA3 = new PaperSize ();
//        pA3.width = 3496;
//        pA3.height = 4961;
//        pA3.pageFrom = 1;
//        pA3.pageTo = 65535;
//        pA3.bookNum = 1;
//        iPenCtrl.
//        ArrayList<PaperSize> paperSizeList = new ArrayList<>();
//        PaperSize pA5 = new PaperSize();
//        pA5.width = 3496;
//        pA5.height = 4961;
//        pA5.pageFrom = 1;
//        pA5.pageTo = 300;
//        pA5.bookNum = 1;
//
//        PaperSize pA4 = new PaperSize ();
//        pA4.width = 4960;
//        pA4.height = 7040;
//        pA4.pageFrom = 301;
//        pA4.pageTo = 600;
//        pA4.bookNum = 2;
//
//        PaperSize pA6 = new PaperSize ();
//        pA6.width = 2480;
//        pA6.height = 3496;
//        pA6.pageFrom = 601;
//        pA6.pageTo = 900;
//        pA6.bookNum = 3;
//
//        PaperSize pA3 = new PaperSize ();
//        pA3.width = 7016;
//        pA3.height = 9921;
//        pA3.pageFrom = 901;
//        pA3.pageTo = 1200;
//        pA3.bookNum = 4;
//
//
//        PaperSize pa = new PaperSize ();
//        pa.width = 3496;
//        pa.height = 4961;
//        pa.pageFrom = 1201;
//        pa.pageTo = 1000000;
//        pa.bookNum = 5;
//        paperSizeList.add(pa);
//        paperSizeList.add(pA3);
//        paperSizeList.add(pA6);
//        paperSizeList.add(pA4);
//        paperSizeList.add(pA5);
//        iPenCtrl.SetPaperSize(paperSizeList);
        setContext(context);

//        this.iPenCtrl.setDotDListener(mIAFPenDotListener);
	}

    public static synchronized PenClientCtrl getInstance(Context context )
	{
		if ( myInstance == null )
		{
			myInstance = new PenClientCtrl( context );
		}

		return myInstance;
	}

    public boolean connect( DeviceListActivity.PenInfo mPenInfo)
	{
        lastTryConnectName = mPenInfo.PenName;
        lastTryConnectAddr = mPenInfo.PenAddress;
		Boolean b = iPenCtrl.connect( mPenInfo.PenAddress );
		return b;
	}

    public void disconnect()
	{
		iPenCtrl.disconnect();
	}


	@Override
	public void onReceiveMessage(PenMsg penMsg )
	{
		switch ( penMsg.penMsgType )
		{
            case PenMsgType.FIND_DEVICE: {
                JSONObject obj = penMsg.getContentByJSONObject();

                try {
                    String penAddress = obj.getString(JsonTag.STRING_PEN_MAC_ADDRESS);
                    String penName;

                    if(obj.has(JsonTag.STRING_DEVICE_NAME))
                        penName = obj.getString(JsonTag.STRING_DEVICE_NAME);
                    else
                        penName = "NULL";

                    int rssi;
                    if(obj.has(JsonTag.STRING_DEVICE_RSSI))
                        rssi = obj.getInt(JsonTag.STRING_DEVICE_RSSI);
                    else
                        rssi = -100;
//                    LogUtil.e("afble find : pen name="+penName + " addr="+penAddress);
//                    boolean res = connect(penName, penAddress);
//                    LogUtil.e("connetct r="+String.valueOf(res));

                    Intent intent = new Intent(Const.Broadcast.ACTION_FIND_DEVICE);
                    intent.putExtra(Const.Broadcast.PEN_RSSI, rssi);
                    intent.putExtra(Const.Broadcast.PEN_ADDRESS, penAddress);
                    intent.putExtra(Const.Broadcast.PEN_NAME, penName);
                    context.sendBroadcast(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            }
            case PenMsgType.PEN_CONNECTION_TRY:
            case PenMsgType.PEN_CONNECTION_SUCCESS:
            case PenMsgType.PEN_DISCONNECTED:
            case PenMsgType.PEN_CONNECTING:
            case PenMsgType.PEN_DISCONNECTING:
            case PenMsgType.PEN_CONNECTION_TIMEOUT:
            case PenMsgType.PEN_TRANSFER_TIMEOUT:
            case PenMsgType.PEN_CONNECTION_FAILURE:{
                Log.e("PenMsgType=",""+penMsg.penMsgType);
                Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                context.sendBroadcast(intent);
                break;
            } case PenMsgType.PEN_FW_VER: {
                JSONObject obj = penMsg.getContentByJSONObject();
                try {

                    Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                    intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                    intent.putExtra(JsonTag.STRING_PEN_FW_VERSION, obj.getString(JsonTag.STRING_PEN_FW_VERSION));
                    context.sendBroadcast(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }case PenMsgType.PEN_CUR_BATT: {
                JSONObject obj = penMsg.getContentByJSONObject();
                try {

                    Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                    intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                    int val = obj.getInt(INT_BATT_VAL);
                    int ischarging = 0; //0未充電 1充電中 2已充飽
                    int v = val;
                    if((val&0xFF00) == 0x1000){
                        ischarging = 1;
                        v = (val&0x00FF);
                    }else if((val&0xFF00) == 0x1100){
                        ischarging = 2;
                        v = (val&0x00FF);
                    }else if(val==32767){
                        ischarging = 1;
                    }
                    intent.putExtra(INT_BATT_VAL, v);
                    intent.putExtra("INT_CHARGE_STATUS", ischarging);
                    context.sendBroadcast(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }case PenMsgType.PEN_CUR_MEMOFFSET: {
                JSONObject obj = penMsg.getContentByJSONObject();
                try {
                    Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                    intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                    intent.putExtra(JsonTag.INT_DOTS_MEMORY_OFFSET, obj.getInt(JsonTag.INT_DOTS_MEMORY_OFFSET));
                    context.sendBroadcast(intent);
                    lastDotsCount = obj.getInt(JsonTag.INT_DOTS_MEMORY_OFFSET);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }case PenMsgType.PEN_FLASH_USED_AMOUNT: {
                JSONObject obj = penMsg.getContentByJSONObject();
                try {
                    Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                    intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                    intent.putExtra(JsonTag.LONG_FLASH_USED, obj.getInt(JsonTag.LONG_FLASH_USED));
                    context.sendBroadcast(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }case PenMsgType.PEN_DELETE_OFFLINEDATA_FINISHED: {
                Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                context.sendBroadcast(intent);
                break;
            }
            case PenMsgType.PEN_REQ_OFFLINESECTIONINFO:{
                ClipData myClip;
                myClip = ClipData.newPlainText("text", penMsg.getContent());
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) AppController.getInstance().getSystemService(CLIPBOARD_SERVICE);
                if(myClipboard!=null)
                    myClipboard.setPrimaryClip(myClip);

                Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                context.sendBroadcast(intent);
            }break;
            case PenMsgType.PEN_SET_OFFLINESECTIONINFO:{
                Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                intent.putExtra(Const.Broadcast.MESSAGE_TYPE, penMsg.penMsgType);
                context.sendBroadcast(intent);
            }break;
		}

	}

	public void setDotListener(IAFPenDotListener mIAFPenDotListener){ this.iPenCtrl.setDotListener(mIAFPenDotListener);}
	public String getConnectDevice()
	{
		return this.iPenCtrl.getConnectedDevice();
	}

	public DPenCtrl getIPenCtrl()
	{
		return this.iPenCtrl;
	}

    public void setContext(Context context)
    {
        iPenCtrl.setContext( context );
    }
    public void requestOfflineDataInfo() {
        iPenCtrl.requestOfflineDataInfo();
    }
    public int btStartForPeripheralsList(){return iPenCtrl.btStartForPeripheralsList(context); }
    public void btStopSearchPeripheralsList(){iPenCtrl.btStopSearchPeripheralsList();}
    public void requestFWVer(){iPenCtrl.requestFWVer();}
    public void requestBatInfo(){iPenCtrl.requestBatInfo();}

//    @Override
//    public void offlineDataDidReceivePenData(List<AFDot> dots, JSONObject info) {
//        /* example 0~5348
//         E/afsdk: {"readCnt":3200,"readedCnt":3200,"totalCnt":5348}
//         E/afsdk: {"readCnt":2148,"readedCnt":5348,"totalCnt":5348}
//        * */
//        LogUtil.e(info.toString());
//        for (int i=0;i<dots.size();i++){
//            //handle dot
//            AFDot dot = dots.get(i);
////            LogUtil.e(String.format("X=%d Y=%d t=%d p=%d",dot.X,dot.Y,dot.type,dot.page));
//        }
//    }
}
