package com.example.calleridfinal;

import static android.Manifest.permission.READ_CALL_LOG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.identity.client.*;
import com.microsoft.identity.client.exception.MsalException;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String[] SCOPES = {"Files.Read","Mail.Read"};
    /* Azure AD v2 Configs */
    final static String AUTHORITY = "https://login.microsoftonline.com/common";
    final static String MSSCONTACTS_URL="https://apimd365.azure-api.net/api/contacts";
    final static  String EMAILS_URL="https://graph.microsoft.com/v1.0/me/messages/?$select=from,bodyPreview,subject,receivedDateTime";
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    //webapi app
    //Secret id: 1dbdbd67-400a-4dd5-852d-641173ff19b3
    //Value: PJy8Q~K3ao1ZhdNCuPCgUWopVRaz0Z3bT8CaxaGQ
    private String authToken;
    private static final String TAG = MainActivity.class.getSimpleName();
    ImageButton refreshContacts;
    /* UI & Debugging Variables */
    Button signInButton;
    Button signOutButton;
    JSONArray emailResponse;
    TextView logTextView;
    TextView currentUserTextView;
    static HashMap<String,String> contactsByPhone;
    ArrayList<String> contactsForListView;
    ListView displayContacts;
    LinearLayout contactsLayout;
    ArrayAdapter adapterForContactsList;
//    ProgressDialog progressDialog;
    Button displayCallLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE)){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);

            }else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }else{

        }
        int readContactsPermissionLog =
                ContextCompat.checkSelfPermission(this, READ_CALL_LOG);
        if(readContactsPermissionLog != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{READ_CALL_LOG},2); }
//        progressDialog=new ProgressDialog(this);
//
//        progressDialog.setMessage("Loading your informations!");
//        progressDialog.show();
        initializeUI();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                progressDialog.hide();
//            }
//        },3000);
    }






    //When app comes to the foreground, load existing account to determine if user is signed in
    private void loadAccount() {
        if (mSingleAccountApp == null) {
            return;
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                updateUI(activeAccount);
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    performOperationOnSignOut();
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                displayError(exception);
            }
        });
    }
    private void initializeUI(){
        signInButton = findViewById(R.id.signIn);
        signOutButton = findViewById(R.id.clearCache);
        refreshContacts=findViewById(R.id.refreshContacts);
        refreshContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSingleAccountApp == null){
                    return;
                }
                mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
            }
        });
        displayCallLog=findViewById(R.id.displayCallLog);
        displayCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CallLogScreen.class);
                intent.putExtra("token",authToken);
                startActivity(intent);
            }
        });
        logTextView = findViewById(R.id.txt_log);
        currentUserTextView = findViewById(R.id.current_user);
        contactsLayout=findViewById(R.id.contactsSection);
        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(),
                R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {

                        mSingleAccountApp = application;
                        loadAccount();
                        mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
                    }
                    @Override
                    public void onError(MsalException exception) {
                        displayError(exception);
                    }
                });
        //Sign in user
        signInButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (mSingleAccountApp == null) {
                    return;
                }
                mSingleAccountApp.signIn(MainActivity.this, null, SCOPES, getAuthInteractiveCallback());
            }
        });

        //Sign out user
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSingleAccountApp == null){
                    return;
                }
                mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
                    @Override
                    public void onSignOut() {
                        updateUI(null);
                        performOperationOnSignOut();

                    }
                    @Override
                    public void onError(@NonNull MsalException exception){
                        displayError(exception);
                    }
                });
            }
        });

        //Interactive
//        callGraphApiInteractiveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSingleAccountApp == null) {
//                    return;
//                }
//                mSingleAccountApp.acquireToken(MainActivity.this, SCOPES, getAuthInteractiveCallback());
//            }
//        });

        //Silent
//        callGraphApiSilentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSingleAccountApp == null){
//                    return;
//                }
//                mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
//            }
//        });
    }
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAG, "Successfully authenticated");
                /* Update UI */
                updateUI(authenticationResult.getAccount());
                /* call graph */
                callGraphAPI(authenticationResult);
                callGraphApiForEmail(authenticationResult);

                getContactsSales();
                authToken=authenticationResult.getAccessToken();

            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                displayError(exception);
            }
            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }
    private SilentAuthenticationCallback getAuthSilentCallback() {
        return new SilentAuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d(TAG, "Successfully authenticated");
                callGraphAPI(authenticationResult);
                callGraphApiForEmail(authenticationResult);

                getContactsSales();
                authToken=authenticationResult.getAccessToken();
            }
            @Override
            public void onError(MsalException exception) {
                Log.d(TAG, "Authentication failed: " + exception.toString());
                displayError(exception);
            }
        };
    }
    private void getContactsSales(){


        RequestQueue queue= Volley.newRequestQueue(this);
        JSONObject parameters=new JSONObject();
        try{
            parameters.put("key","value");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, MSSCONTACTS_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                contactsByPhone=new HashMap<String,String>();
                displayContacts=findViewById(R.id.contactVolley);
                contactsForListView=new ArrayList<>();
                adapterForContactsList=new ArrayAdapter(getApplicationContext(),R.layout.list_item_style,contactsForListView);
                displayContacts.setAdapter(adapterForContactsList);
                displayContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String contact=contactsForListView.get(position);
                        String number=contact.split("\n")[1].split(":")[1].trim();
                        String contactInfoNAE=contactsByPhone.get(number);
                        String name=contactInfoNAE.split(":")[0];
                        String address=contactInfoNAE.split(":")[1];
                        String email=contactInfoNAE.split(":")[2];
                        System.out.println("Email from hash: "+email);
                        Intent intent=new Intent(MainActivity.this,SavedContactInfoScreen.class);
                        intent.putExtra("phoneNumber",number);
                        intent.putExtra("name",name);
                        intent.putExtra("email",email);
                        intent.putExtra("address",address);
                        HashMap<String,String> emails=new HashMap<>();
                        ArrayList<String> ids=new ArrayList<>();
                        String dateReceived,bodyPreview,subject,objId;
                        JSONArray valuesEmails=emailResponse;
                        int i=0;
                        int j;
                        if(valuesEmails!=null){
                            for(j=0;j<valuesEmails.length() || j<100;j++){
                                try {
                                    JSONObject mailInfo=valuesEmails.optJSONObject(j);
                                    System.out.println("+++mailInfo:"+mailInfo+"\nvaluesEmails"+valuesEmails);
                                    JSONObject from=mailInfo.optJSONObject("from");
                                    System.out.println("+++from"+from);
                                    JSONObject emailAddressObj=from.optJSONObject("emailAddress");
                                    String emailAddress=emailAddressObj.optString("address");
                                    if(emailAddress.equals(email)){
                                        dateReceived=mailInfo.optString("receivedDateTime");
                                        bodyPreview=mailInfo.optString("bodyPreview");
                                        subject=mailInfo.optString("subject");
                                        objId=mailInfo.getString("id");
                                        emails.put(objId,dateReceived+":%"+subject+":%"+bodyPreview);
                                        System.out.println("Item in hash:"+emails.get(objId));
                                        ids.add(objId);
                                        i++;

                                    }
                                    if (i == 2) {
                                        break;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    System.out.println("Counter: "+i);
                                    break;
                                }

                        }
                        intent.putExtra("emails",emails);
                        intent.putExtra("ids",ids);
                        startActivity(intent);
                    }else{
                            if (mSingleAccountApp == null){
                                return;
                            }
                            mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
                        }
                    }
                });
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Success");
                JSONArray values = response.optJSONArray("value");
                JSONObject contactObject;

                //mobilephone , fullname , addressComposite, emailaddress1
                String mobilephone,fullname,addressComposite,emailaddress1,contactId;
                for(int i = 0;i<values.length();i++){
                    contactObject=values.optJSONObject(i);
                    mobilephone=contactObject.optString("mobilephone");
                    fullname=contactObject.optString("fullname");
                    addressComposite=contactObject.optString("address1_composite");
                    emailaddress1=contactObject.optString("emailaddress1");
                    contactId=contactObject.optString("contactid");

                    contactsForListView.add("Contact Name: "+fullname+"\n"+"Number: "+mobilephone);
                    contactsByPhone.put(mobilephone,fullname+":"+addressComposite+":"+emailaddress1+":"+contactId);

                }
                System.out.println("+++++++++++++++++++++HashMap Content"+contactsByPhone.toString()+" "+contactsByPhone.get("423-555-0123"));
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error "+error.networkResponse);


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
//                headers.put("Content-Type","application/json");
//                headers.put("Authorization","Bearer "+authenticationResult.getAccessToken());
//                headers.put("Accept","application/json");
//                headers.put("OData-MaxVersion","4.0");
//                headers.put("OData-Version","4.0");
//                headers.put("If-None-Match","null");
                headers.put("Host","apimd365.azure-api.net");
                headers.put("Ocp-Apim-Subscription-Key","ff217ee6bbf74c54972a77cf853a7436");
                headers.put("Ocp-Apim-Trace","true");
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000*2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
    private void callGraphApiForEmail(IAuthenticationResult authenticationResult){
        final String accessToken = authenticationResult.getAccessToken();

        RequestQueue queue= Volley.newRequestQueue(this);
        JSONObject parameters=new JSONObject();
        try{
            parameters.put("key","value");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, EMAILS_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("+++++++Success: ");
                emailResponse=response.optJSONArray("value");
                System.out.println(emailResponse);

            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();


                headers.put("Authorization","Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                300,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
    private void callGraphAPI(IAuthenticationResult authenticationResult) {

        final String accessToken = authenticationResult.getAccessToken();

        IGraphServiceClient graphClient =
                GraphServiceClient
                        .builder()
                        .authenticationProvider(new IAuthenticationProvider() {
                            @Override
                            public void authenticateRequest(IHttpRequest request) {
                                Log.d(TAG, "Authenticating request," + request.getRequestUrl());
                                request.addHeader("Authorization", "Bearer " + accessToken);}
                        })
                        .buildClient();
        graphClient
                .me()
                .drive()
                .buildRequest()
                .get(new ICallback<Drive>() {
                    @Override
                    public void success(final Drive drive) {
                        Log.d(TAG, "Found Drive " + drive.id);
                        displayGraphResult(drive.getRawObject());
                    }

                    @Override
                    public void failure(ClientException ex) {
                        displayError(ex);
                    }
                });
    }
    private void updateUI(@Nullable final IAccount account) {
        if (account != null) {
            signInButton.setEnabled(false);
            signOutButton.setEnabled(true);
            currentUserTextView.setText("User: "+account.getUsername());
            contactsLayout.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {displayCallLog.setVisibility(View.VISIBLE);
                }
                }, 3000);
        } else {
            signInButton.setEnabled(true);
            signOutButton.setEnabled(false);
            displayCallLog.setVisibility(View.GONE);
            contactsLayout.setVisibility(View.GONE);
            currentUserTextView.setText("");
            logTextView.setText("Please Sign in to Display your informations!");
            logTextView.setText("");
        }
    }
    private void displayError(@NonNull final Exception exception) {
//        logTextView.setText(exception.toString());
    }
    private void displayGraphResult(@NonNull final JsonObject graphResponse) {
        System.out.println(graphResponse);
        JsonObject obj1=graphResponse.getAsJsonObject("owner");
        JsonObject obj2=obj1.getAsJsonObject("user");

        String name=obj2.get("displayName").toString();
        logTextView.setText("Hello "+name.replaceAll("\"",""));


    }
    private void performOperationOnSignOut() {
        final String signOutText = "Signed Out.";
        currentUserTextView.setText("");
        Toast.makeText(getApplicationContext(), signOutText, Toast.LENGTH_SHORT)
                .show();
    }
    //Phone call
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED){

                    }else{
                        Toast.makeText(this,"Permission denied!",Toast.LENGTH_SHORT).show();
                    }
                    return ;
                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}