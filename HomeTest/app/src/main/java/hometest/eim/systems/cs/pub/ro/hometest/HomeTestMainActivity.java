package hometest.eim.systems.cs.pub.ro.hometest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class HomeTestMainActivity extends AppCompatActivity {

    EditText addrTxt;
    EditText portTxt;
    EditText cityTxt;
    EditText infoTxt;
    Button startServer;
    Button connectToServer;
    TextView responseView;

    ServerThread serverThread;


    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (startServer.equals((Button)view)) {
                serverThread = new ServerThread();
                serverThread.startServer();
            }

            if (connectToServer.equals((Button)view)) {
                new ClientAsyncTask(addrTxt.getText().toString(), portTxt.getText().toString(),
                        cityTxt.getText().toString(), infoTxt.getText().toString(), responseView).execute();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_test_main);

        this.addrTxt = (EditText) findViewById(R.id.addr);
        this.portTxt = (EditText) findViewById(R.id.port);
        this.cityTxt = (EditText) findViewById(R.id.city);
        this.infoTxt = (EditText) findViewById(R.id.info);
        this.responseView = (TextView) findViewById(R.id.response);

        this.startServer = (Button) findViewById(R.id.button1);
        this.connectToServer = (Button) findViewById(R.id.button2);
        startServer.setOnClickListener(buttonClickListener);
        connectToServer.setOnClickListener(buttonClickListener);
    }


}
