package web2design.epos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import web2design.epos.R;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements HttpResponseCallback {

    private EditText emailET, passwordET;
    private String email, password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUtils();
        initViews();

        progressDialog = new ProgressDialog(this);
    }

    private void initViews() {

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
    }

    public void onCLickLogin(View view) {

        if (utils.isNetworkConnected()){

            email = emailET.getText().toString();
            password = passwordET.getText().toString();

            validateFields(email,password);
        } else {

            utils.showAlertDialoge();
        }
    }

    private void validateFields(String email, String password) {

        if (email.isEmpty()){

            emailET.setError("Required");
            emailET.requestFocus();
        } else if (!utils.validEmail(email)){

            emailET.setError("Invalid email...!");
            emailET.requestFocus();
        } else if(password.isEmpty()){

            passwordET.setError("Required");
            passwordET.requestFocus();
        } else {

            progressDialog.setTitle("Loggin In");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            httpService.authenticateUser(email,password,this);
        }
    }

    @Override
    public void onCompleteHttpResponse(String response, String requestUrl) {

        progressDialog.dismiss();

        Log.e("Response", response);
        if (response == null) {
            utils.showToast("No Response....!");
            return;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(response);

                if(jsonObject.getBoolean("success")){

                    JSONObject pharmacist = jsonObject.getJSONObject("pharmacist");

                    String pharmacist_id = pharmacist.optString("pharmacist_id"); //": "1",
                    String imei = pharmacist.optString("imei"); //": "357805023984942",
                    String email = pharmacist.optString("email"); //: "wajid@skafs.com",
                    String password = pharmacist.optString("password"); //: "ff41f016dec533c7167533deeae3868ee5e66696",
                    String access = pharmacist.optString("access"); //": "0",

                    sharedPrefUtils.saveSharedPrefValue(AppConstants.PHARMACIST_ID,pharmacist_id);
                    sharedPrefUtils.saveSharedPrefValue(AppConstants.PHARMACIST_IMEI,imei);
                    sharedPrefUtils.saveSharedPrefValue(AppConstants.PHARMACIST_EMAIL,email);
                    sharedPrefUtils.saveSharedPrefValue(AppConstants.PHARMACIST_PASSWORD,password);
                    sharedPrefUtils.saveSharedPrefValue(AppConstants.PHARMACIST_ACCESS,access);

                    sharedPrefUtils.saveActivityPrefValue(AppConstants.ALREADY_LOGIN,true);
                    utils.startNewActivity(VerificationActivity.class,null,true);

                } else {
                    utils.showToast(jsonObject.optString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
