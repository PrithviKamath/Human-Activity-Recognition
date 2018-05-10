package co.harshadpardeshi.humanactivityrecognition;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final int N_SAMPLES = 128;
    private static List<Float> accX;
    private static List<Float> accY;
    private static List<Float> accZ;
    private static List<Float> groX;
    private static List<Float> groY;
    private static List<Float> groZ;
    private TextView downstairsTextView;

    private Long lastAccTimer = 0L;
    private Long lastGyroTimer = 0L;
    private Long startTime = 0L;

    private TextView layingTextView;
    private TextView sittingTextView;
    private TextView standingTextView;
    private TextView upstairsTextView;
    private TextView walkingTextView;
    private TextView xAxisTextView;
    private TextView yAxisTextView;
    private TextView zAxisTextView;
    private TextView gyroXAxisTextView;
    private TextView gyroYAxisTextView;
    private TextView gyroZAxisTextView;

    private TextView numberOfReadingTextView;
    private TextView timeTextView;

    private TextToSpeech textToSpeech;
    private float[] results;
    private TensorFlowClassifier classifier;

    private String[] labels = {"Downstairs", "Upstairs", "Walking", "Sitting", "Laying", "Standing"};

    String res = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accX = new ArrayList<>();
        accY = new ArrayList<>();
        accZ = new ArrayList<>();

        groX = new ArrayList<>();
        groY = new ArrayList<>();
        groZ = new ArrayList<>();


        downstairsTextView = findViewById(R.id.textViewDownstairsValue);
        layingTextView = findViewById(R.id.textViewLayingValue);
        sittingTextView = findViewById(R.id.textViewStandingValue);
        standingTextView = findViewById(R.id.textViewSittingValue);
        upstairsTextView = findViewById(R.id.textViewUpstairsValue);
        walkingTextView = findViewById(R.id.textViewWalkingValue);
        xAxisTextView = findViewById(R.id.textViewXAxis);
        yAxisTextView = findViewById(R.id.textViewYAxis);
        zAxisTextView = findViewById(R.id.textViewZAxis);
        gyroXAxisTextView = findViewById(R.id.textViewXGyro);
        gyroYAxisTextView = findViewById(R.id.textViewYGyro);
        gyroZAxisTextView = findViewById(R.id.textViewZGyro);
        numberOfReadingTextView = findViewById(R.id.textViewNumberOfReadingsValue);
        timeTextView  = findViewById(R.id.textViewTimeDiffValue);

        classifier = new TensorFlowClassifier(getApplicationContext());

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
    }

    @Override
    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (results == null || results.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }
                res = labels[idx];
                textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));

            }
        }, 2000, 5000);
    }

    protected void sendToUI(String result){

    }
    protected void onPause() {
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            // Accelerometer Sensor
            if(lastAccTimer == 0 && accX.size() < N_SAMPLES){
                lastAccTimer =  currentTime;
                startTime = currentTime;
                accX.add(event.values[0]);
                accY.add(event.values[1]);
                accZ.add(event.values[2]);
                xAxisTextView.setText(Float.toString(round(event.values[0], 2)));
                yAxisTextView.setText(Float.toString(round(event.values[1], 2)));
                zAxisTextView.setText(Float.toString(round(event.values[2], 2)));
            }else{

                long timeDifference = currentTime - lastAccTimer;
                if(timeDifference >= 20 && accX.size() < N_SAMPLES){
                    lastAccTimer =  currentTime;
                    accX.add(event.values[0]);
                    accY.add(event.values[1]);
                    accZ.add(event.values[2]);
                    xAxisTextView.setText(Float.toString(round(event.values[0], 2)));
                    yAxisTextView.setText(Float.toString(round(event.values[1], 2)));
                    zAxisTextView.setText(Float.toString(round(event.values[2], 2)));
                }
            }

        }else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            if(lastGyroTimer == 0 && groX.size() < N_SAMPLES){
                lastGyroTimer = currentTime;
                startTime = currentTime;
                groX.add(event.values[0]);
                groY.add(event.values[1]);
                groZ.add(event.values[2]);
                gyroXAxisTextView.setText(Float.toString(round(event.values[0], 2)));
                gyroYAxisTextView.setText(Float.toString(round(event.values[1], 2)));
                gyroZAxisTextView.setText(Float.toString(round(event.values[2], 2)));
            }else{
                long timeDifference = currentTime -lastGyroTimer;
                if(timeDifference >= 20 && groX.size() < N_SAMPLES){
                    lastGyroTimer = currentTime;
                    groX.add(event.values[0]);
                    groY.add(event.values[1]);
                    groZ.add(event.values[2]);
                    gyroXAxisTextView.setText(Float.toString(round(event.values[0], 2)));
                    gyroYAxisTextView.setText(Float.toString(round(event.values[1], 2)));
                    gyroZAxisTextView.setText(Float.toString(round(event.values[2], 2)));
                }
            }

        }
        activityPrediction(currentTime);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private void activityPrediction(long eventTime) {
        if (accX.size() == N_SAMPLES && accY.size() == N_SAMPLES && accZ.size() == N_SAMPLES
                && groX.size() == N_SAMPLES && groY.size() == N_SAMPLES && groZ.size() == N_SAMPLES ) {
            List<Float> data = new ArrayList<>();
            data.addAll(groX);
            data.addAll(groY);
            data.addAll(groZ);
            data.addAll(accX);
            data.addAll(accY);
            data.addAll(accZ);


            results = classifier.predictProbabilities(toFloatArray(data));

//            downstairsTextView.setText(Float.toString(round(results[0], 2)));
//            joggingTextView.setText(Float.toString(round(results[1], 2)));
//            sittingTextView.setText(Float.toString(round(results[2], 2)));
//            standingTextView.setText(Float.toString(round(results[3], 2)));
//            upstairsTextView.setText(Float.toString(round(results[4], 2)));
//            walkingTextView.setText(Float.toString(round(results[5], 2)));

            downstairsTextView.setText(Float.toString(round(results[2], 2)));
            layingTextView.setText(Float.toString(round(results[5], 2)));
            sittingTextView.setText(Float.toString(round(results[3], 2)));
            standingTextView.setText(Float.toString(round(results[4], 2)));
            upstairsTextView.setText(Float.toString(round(results[1], 2)));
            walkingTextView.setText(Float.toString(round(results[0], 2)));

            Date date = new Date(eventTime - startTime);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            String dateFormatted = formatter.format(date);

            numberOfReadingTextView.setText(Integer.toString(accX.size()));
            timeTextView.setText(dateFormatted);

            //invalidate();
            accX.clear();
            accY.clear();
            accZ.clear();
            groX.clear();
            groY.clear();
            groZ.clear();

            startTime = 0L;
            lastAccTimer = 0L;
            lastGyroTimer = 0L;
            new SendData().execute();
        }
    }

    class SendData extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb=null;
            BufferedReader reader=null;
            String serverResponse=null;
            try {

                URL url = new URL("http://88885ecc4.ngrok.io/login?param="+res);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.connect();
                int statusCode = connection.getResponseCode();
                Toast.makeText(getApplicationContext(),"Data Sent",Toast.LENGTH_SHORT);
                if (statusCode == 200) {
                    sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                }
                connection.disconnect();
                if (sb!=null)
                    serverResponse=sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Error Sending Data",Toast.LENGTH_SHORT);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return serverResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //All your UI operation can be performed here
            System.out.println(s);
        }
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
