package me.no_ip.glfernando.edisontest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by fernando on 2/19/15.
 */
public class GpioFragment extends Fragment implements View.OnTouchListener {
    ImageButton gpio;
    private EdisonComm comm;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout;
        layout = inflater.inflate(R.layout.fragment_gpio, container, false);
        gpio = (ImageButton) layout.findViewById(R.id.gpioButton);
        gpio.setOnTouchListener(this);

        return layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (EdisonComm) getActivity();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG", "Down");
                comm.sendData("1");
                break;
            case MotionEvent.ACTION_UP:
                comm.sendData("0");
        }
        return true;
    }
}
