package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.test.data.FragmentItem;
import com.wtz.tools.utils.data_transfer_format.XmlDemo;
import com.wtz.tools.utils.event.RxBus;
import com.wtz.tools.utils.event.RxBusFlowable;
import com.wtz.tools.utils.event.RxBusRelay;
import com.wtz.tools.utils.network.OkhttpWebSocket;

import org.w3c.dom.Text;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class CommonUtilFragment extends Fragment {
    private static final String TAG = CommonUtilFragment.class.getSimpleName();
    
    private String TEST_WEB_SOCKET_URL = "ws://echo.websocket.org";
    private OkhttpWebSocket mWebSocket;

    private TextView mWebsocketMsgView;
    private EditText mWebsocketMsgEdit;

    private Disposable mDisposableMsg;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mDisposableMsg = RxBus.registerOnMainThread(OkhttpWebSocket.WebsocketMsg.class, new Consumer<OkhttpWebSocket.WebsocketMsg>() {
            @Override
            public void accept(OkhttpWebSocket.WebsocketMsg websocketMsg) throws Exception {
                if (mWebsocketMsgView != null && websocketMsg != null) {
                    mWebsocketMsgView.setText(websocketMsg.content);
                }
            }
        });

        // 需要在子线程里做
        mWebSocket = new OkhttpWebSocket(getActivity(), TEST_WEB_SOCKET_URL);
        mWebSocket.connect();

        // RxBus test
        RxBus.getInstance().send(new FragmentItem("haha", "haha-class"));
        RxBusFlowable.getInstance().send(new FragmentItem("haha", "haha-class"));
        RxBusRelay.getInstance().send(new FragmentItem("haha", "haha-class"));

        // xml parse test
        XmlDemo.test(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_common_util, container, false);
        
        mWebsocketMsgView = view.findViewById(R.id.tv_received_websocket);
        mWebsocketMsgEdit = view.findViewById(R.id.et_send_websocket);
        view.findViewById(R.id.btn_send_websocket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mWebsocketMsgEdit.getText().toString();
                if (TextUtils.isEmpty(content)) return;
                mWebSocket.send(content);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        RxBus.unregister(mDisposableMsg);
        mWebSocket.destroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
