package hometest.eim.systems.cs.pub.ro.hometest;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Adrian on 5/17/2017.
 */

public class ClientAsyncTask extends AsyncTask<String, String, Void> {

    String serverAddr;
    int port;
    String city;
    String infoNeeded;
    TextView responseView;

    public ClientAsyncTask(String serverAddr, String port, String city, String infoNeeded, TextView responseView) {
        this.serverAddr = serverAddr;
        this.port = Integer.parseInt(port);
        this.city = city;
        this.infoNeeded = infoNeeded;
        this.responseView = responseView;
    }

    @Override
    protected Void doInBackground(String... params) {

        Socket socket;
        String response = "";
        try {
            socket = new Socket(serverAddr, port);
            PrintWriter clientPr = Utilities.getWriter(socket);
            clientPr.write(city+"\n");
            clientPr.write(infoNeeded+"\n");
            clientPr.flush();

            BufferedReader clientBr = Utilities.getReader(socket);
            //while (!socket.isClosed()) {
                response += clientBr.readLine();
            //}

            this.publishProgress(response);
            socket.close();

        } catch (Exception e) {e.printStackTrace();}

        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(String... progress) {
        responseView.setText(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result) {}
}
