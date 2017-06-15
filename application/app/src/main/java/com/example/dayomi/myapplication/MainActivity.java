package com.example.dayomi.myapplication;

import android.app.*;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private ImageButton mCallApiButton;
    private Button mCancleApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    //DBHelper dbHelper = null;
    public MessageDBManager mDBManager = null;
    //public WatchingThread wThread = null;
    private TimerTask mTask = null;
    private Timer mTimer = null;
    private boolean isAlarmRunning = false;
    private int nowProgress = 0;

    private static final String PREF_ACCOUNT_NAME = "notify mail";
    //private static final String[] SCOPES = { GmailScopes.GMAIL_LABELS };
    private static final String[ ] SCOPES = { GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_COMPOSE,
            GmailScopes.GMAIL_INSERT, GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_READONLY, GmailScopes.MAIL_GOOGLE_COM };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);*/
        setContentView(R.layout.activity_main);

        mCallApiButton = (ImageButton)findViewById(R.id.mCallApiButton);
        mOutputText = (TextView)findViewById(R.id.mOutputText);

        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mCallApiButton.setImageResource(R.drawable.radio);
                // ------------------------------------------------------ 여기 getResultsFromApi()//
                //getResultsFromApi();
                //WatchingThread wThread = new WatchingThread();
                //wThread.run();
                if(isAlarmRunning == false) {
                    isAlarmRunning = true;
                    mTask = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("timer run 실행됌");
                                    getResultsFromApi();
                                }
                            });
                        }
                    };
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(mTask, 0, 3000);
                }
                else{
                    mCallApiButton.setImageResource(R.drawable.mail);
                    mOutputText.setText(
                             "버튼을 클릭하여 \n 실시간 알림을 시작하세요");
                    isAlarmRunning = false;
                    mTimer.cancel();
                    System.out.println("timer run 중지됌");
                }
                // ------------------------------------------------------ 여기 getResultsFromApi()//
                mCallApiButton.setEnabled(true);
            }
        });
        //activityLayout.addView(mCallApiButton);

        /*
        mCancleApiButton = new Button(this);
        mCancleApiButton.setText("STOP CALL API");
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  if(mTimer!=null)
                                                    mTimer.cancel();
                                              }
                                          });
        activityLayout.addView(mCancleApiButton);*/

        /*mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());*/
        mOutputText.setText(
                "버튼을 클릭하여 \n 실시간 알림을 시작하세요");
        //activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Gmail API ...");

        //setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
    getApplicationContext(), Arrays.asList(SCOPES))
            .setBackOff(new ExponentialBackOff());


    // DB 관리
    //dbHelper = new DBHelper(getApplicationContext(), "MESSAGEMANAGEMENT.db", null, 1);
    //dbHelper.updateSize(0);

    mDBManager = MessageDBManager.getInstance(this);
        if(mDBManager.getProfilesCount()==0){
            mDBManager.insert(0);
        }
    //mDBManager.insert(0);

}

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     * ------------- 구글 플레이 서비스 가 설치 되어있는지 미리 확인하기 -----------
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                //return getDataFromApi();
                //---------------------------------------------------------------------------------------  MessageList 를 받아와서 검사 후 DB 업데이트합니다 -----//
                getMessagesSizeAndSaveData(listMessagesMatchingQuery(mService, "me",
                      "from:notifymail.comsoshk@gmail.com is:unread"));
                return null;
                //---------------------------------------------------------------------------------------  MessageList 를 받아와서 검사 후 DB 업데이트합니다 -----//
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of Gmail labels attached to the specified account.
         * @return List of Strings labels.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get the labels in the user's account.
            String user = "me";
            List<String> labels = new ArrayList<String>();
            ListLabelsResponse listResponse =
                    mService.users().labels().list(user).execute();
            for (Label label : listResponse.getLabels()) {
                labels.add(label.getName());
            }
            return labels;
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("...실시간 알림 켜짐...");
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                //mOutputText.setText("No results");
            } else {
                output.add(0, "Data retrieved using the Gmail API:");
                mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }

        // --------------------------------------------------------------------------------------------- //
        // 특정 유저로부터 온 메세지 검색 ( 스레드로 3초마다 갱신되야할 기능 )
        // 메세지 검색 + 갯수 확인 + DB 에 저장 -> DB 리스너로 푸시 알림
        // --------------------------------------------------------------------------------------------- //
        public List<Message> listMessagesMatchingQuery(Gmail service, String userId,
                                                              String query) throws IOException {

            ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
            // from:someuser@example.com rfc822msgid: is:unread < QUERY ( someuser@example.com 에게 온 메세지를 리스트해서 보여준다 )

            List<Message> messages = new ArrayList<Message>();

            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = service.users().messages().list(userId).setQ(query)
                            .setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }

            /*
            if(messages.size() != 0) {
                for (Message message : messages) {
                    System.out.println(message.toPrettyString());
                }
            }*/

            return messages;
        }


        public void getMessagesSizeAndSaveData(List<Message> messages){
            String returnMessage = null;
            int messageSize = messages.size();
            //System.out.println("메일 갯수는 " + messageSize + "개 입니다 / DB안의 메일 갯수는 "+ mDBManager.getSize() + "개입니다");
            if(mDBManager.getSize() < messageSize){
                System.out.println("새로운 관리자로부터의 메일이 있습니다");
                /*runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "새로운 관리자로부터의 메일이 있습니다", Toast.LENGTH_LONG).show();
                    }
                });*/
                NotifyMail();
            }
            else{
                System.out.println("새로운 관리자로부터의 메일이 아직 없습니다");
                returnMessage = "정상 작동 중";
            }
            if(mDBManager.getRowSize() > 100){
                mDBManager.cleanDB();
            }
            System.out.println("DB row 갯수 : " + mDBManager.getRowSize());
            mDBManager.insert(messageSize);
            System.out.println("현재 안읽은 메일의 갯수는 " + mDBManager.getSize() + "개");
            //return returnMessage;
        }

        public void NotifyMail(){
            Resources res = getResources();

            Intent notificationIntent = new Intent(MainActivity.this, NotificationMail.class);
            notificationIntent.putExtra("web","https://mail.google.com/mail/u/0/?tab=wm#inbox");
            PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this , 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);

            builder.setContentTitle("위험 로그가 발생하였습니다")
                    .setContentText("관리자로부터의 메일을 확인하세요")
                    .setTicker("위험로그가 발생하였습니다")
                    .setSmallIcon(R.drawable.warningicon)
                    .setLargeIcon(BitmapFactory.decodeResource(res,R.drawable.warningicon))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_ALL);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                builder.setCategory(Notification.CATEGORY_MESSAGE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);
            }

            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1234,builder.build());
        }
    }
}