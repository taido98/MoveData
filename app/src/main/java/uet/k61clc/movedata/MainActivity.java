package uet.k61clc.movedata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.nearby.connection.Strategy.P2P_POINT_TO_POINT;

public class MainActivity extends AppCompatActivity {
    private Button mBtnWifi;
    private Button mBtnDiscovery;
    private RecyclerView mRcvDevice;
    WifiManager wifiManager;
    private WifiP2pManager mWifiP2pManager;
    private  WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private List<WifiP2pDevice> mList = new ArrayList<WifiP2pDevice>();
    private String[] mDeviceNameArray;
    private WifiP2pDevice[] mDeviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAction();
    }

    private void initAction() {
        mBtnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    mBtnWifi.setText("ON");
                }else{
                    wifiManager.setWifiEnabled(true);
                    mBtnWifi.setText("OFF");
                }
            }
        });
        mBtnDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i) {

                    }
                });
            }
        });
    }

    private void initView() {
        mBtnWifi = findViewById(R.id.btn_wifi);
        mBtnDiscovery = findViewById(R.id.btn_discovery);
        mRcvDevice = findViewById(R.id.rcv_device);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this,getMainLooper(),null);
        mReceiver = new WifiDirectBroadcastReceiver(mWifiP2pManager,mChannel,this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(mList)) {
                mList.clear();
                mList.addAll(peerList.getDeviceList());
                mDeviceNameArray = new String[peerList.getDeviceList().size()];
                mDeviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index =0;
                for(WifiP2pDevice device : peerList.getDeviceList()){
                    mDeviceNameArray[index] = device.deviceName;
                    mDeviceArray[index]=device;
                    index++;
                }
            }
            if(mList.size()==0) Toast.makeText(getApplicationContext(), "No Device", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
