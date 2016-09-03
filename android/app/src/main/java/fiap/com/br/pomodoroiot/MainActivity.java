package fiap.com.br.pomodoroiot;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 202;
    private static final String TAG = "PomodoroIoT";
    private static int Selected_Device = -1;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter btAdapter;
    private OutputStream out;
    private boolean running = false;
    ProgressDialog progress;

    private Switch switchControl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null) {
            if (btAdapter.isEnabled() == false) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(this, "Seu dispositivo não suporta bluetooth. Sorry =/",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        final List<String> items = new ArrayList<String>();
        final List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        switchControl = (Switch) findViewById(R.id.swtPomodoro);
        switchControl.setRotation(-90);

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                items.add(device.getName());
                devices.add(device);
            }
        }

        CharSequence[] itemsCS = new CharSequence[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemsCS[i] = items.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione o dispositivo:");
        builder.setSingleChoiceItems(itemsCS, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Selected_Device = item;
                dialog.dismiss();

                progress = ProgressDialog.show(MainActivity.this, "Carregando...",
                        "Carregando", true);

                BluetoothDevice device = devices.get(item);
                BluetoothSocket socket = null;

                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                    socket.connect();

                    out = socket.getOutputStream();
                    out.write("1".getBytes());

                    progress.dismiss();
                    running = true;
                    switchControl.setChecked(true);
                    //switchControl.setText("Desligar PomodorIoT");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();

        switchControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    if (out != null && running == false) {
                        try {
                            out.write("1".getBytes());
                            running = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //switchControl.setText("Desligar PomodorIoT");
                } else {
                    if (out != null && running == true) {
                        try {
                            out.write("0".getBytes());
                            running = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //switchControl.setText("Ligar PomodorIoT");
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(out != null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void applyStyle(CharSequence switchTxtOn, CharSequence switchTxtOff){
        Spannable styleText = new SpannableString(switchTxtOn);
        StyleSpan style = new StyleSpan(Typeface.BOLD);
        styleText.setSpan(style, 0, switchTxtOn.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        styleText.setSpan(new ForegroundColorSpan(Color.GREEN), 0, switchTxtOn.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        switchControl.setTextOn(styleText);

        styleText = new SpannableString(switchTxtOff);
        styleText.setSpan(style, 0, switchTxtOff.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        styleText.setSpan(new ForegroundColorSpan(Color.RED), 0, switchTxtOff.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        switchControl.setTextOff(styleText);
    }

    public void togglestatehandler(View v){
        Switch switchbtn = (Switch)v;
        boolean isChecked = switchbtn.isChecked();

        if(isChecked){
            Toast.makeText(this, "INICIALIZADO", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "PARADO", Toast.LENGTH_SHORT).show();
        }

    }
}


