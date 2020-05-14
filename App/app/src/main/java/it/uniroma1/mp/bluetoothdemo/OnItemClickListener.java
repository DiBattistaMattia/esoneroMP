package it.uniroma1.mp.bluetoothdemo;

import android.view.View;
import android.widget.AdapterView;

public interface OnItemClickListener extends AdapterView.OnItemClickListener {

    void onItemClick(AdapterView<?> parent, View view, int position,
                     long id);

    void OnClick(String deviceAddress);
}
