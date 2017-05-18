package hometest.eim.systems.cs.pub.ro.hometest;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Adrian on 5/16/2017.
 */

public class ServerThread extends Thread {

    private boolean isRunning;
    private ArrayList<Tuple> savedInfo = new ArrayList<Tuple>();

    private ServerSocket serverSocket;



    public void startServer() {
        isRunning = true;
        start();
        Log.v(Constants.TAG, "startServer() method was invoked");
    }

    public void stopServer() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());

        }
        Log.v(Constants.TAG, "stopServer() method was invoked");
    }

    @Override
    public void run() {

        HttpClient httpClient;
        String pageSourceCode;
        String cityFromClient = null;
        String infoFromClient = null;
        String internetURL = "http://www.wunderground.com/cgi-bin/findweather/getForecast";

        try {
            serverSocket = new ServerSocket(Constants.PORT);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    BufferedReader br = Utilities.getReader(socket);
                    cityFromClient = br.readLine().trim();
                    infoFromClient = br.readLine().trim();

                    httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(internetURL + "?query=" + cityFromClient);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    pageSourceCode = httpClient.execute(httpGet, responseHandler);

                    String responseForClient = "";
                    if (infoFromClient.equals("all")) {
//
                        Document document = Jsoup.parse(pageSourceCode);
                        Element htmlTag = document.child(0);

                        Element press = htmlTag.getElementsByAttributeValue("data-variable", "pressure").first();
                        responseForClient += " pressure " + press.getElementsByAttributeValue("class", "wx-value").first().ownText();

                        Element temp = htmlTag.getElementsByAttributeValue("data-variable", "temperature").first();
                        responseForClient += " temperature " + temp.getElementsByAttributeValue("class", "wx-value").first().ownText();

                        Element cond = htmlTag.getElementsByAttributeValue("data-variable", "condition").first();
                        responseForClient += " condition " + cond.getElementsByAttributeValue("class", "wx-value").first().ownText();

                        Element wind = htmlTag.getElementsByAttributeValue("data-variable", "wind_speed").first();
                        responseForClient += " wind " + wind.getElementsByAttributeValue("class", "wx-value").first().ownText();

                        responseForClient += getHumidity(htmlTag);


                        savedInfo.add(new Tuple(cityFromClient,"humidity",
                                hum.getElementsByAttributeValue("class", "wx-value").first().ownText()));
                    }

                    PrintWriter pw = Utilities.getWriter(socket);
                    pw.write(responseForClient + "\n");
                    Log.d(Constants.TAG, "Sending from server: " + responseForClient);
                    pw.flush();
                    socket.close();
                }
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        }

    }

    public String searchInSavedInfo(ArrayList<Tuple> savedI, String city, String info) {
        for (Tuple t : si) {
            if (t.getFirst().equals(city) &&  t.getSecond().equals(info)) {
                return t.getThird();
            }
        }
        return null;
    }
    public String getHumidity(Element htmlTag) {
        Element hum = htmlTag.getElementsByAttributeValue("data-variable", "humidity").first();
        return " humidity " + hum.getElementsByAttributeValue("class", "wx-value").first().ownText();
    }

    public String parseAndGetValue(String toFind, String pageSrcCode) {
        int idx, i;
        String ret, aux;

        idx = pageSrcCode.indexOf(toFind);
        if (idx == -1) {
            return "Eroare";
        }

        aux = pageSrcCode.substring(idx + toFind.length());

        i = 0;
        ret = "";
        while (i < aux.length() && aux.charAt(i) != '>' && aux.charAt(i) != '<') {
            ret += aux.charAt(i);
            i++;
        }

        return ret.trim();
    }
}

class Tuple {
    private String first;
    private String second;
    private String third;

    public Tuple(String first, String second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String getFirst() {
        return this.first;
    }
    public String getSecond() {
        return this.second;
    }
    public String getThird() {
        return this.third;
    }
}