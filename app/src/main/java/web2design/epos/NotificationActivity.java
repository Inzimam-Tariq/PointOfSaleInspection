package web2design.epos;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import web2design.epos.R;

public class NotificationActivity extends BaseActivity {

    private TextView notifTitle, notifMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initUtils();
        initViews();

        if (sharedPrefUtils.getSharedPrefValue(AppConstants.NOTIF_TITLE)!=null){

            notifTitle.setText(sharedPrefUtils.getSharedPrefValue(AppConstants.NOTIF_TITLE));
            notifMsg.setText(sharedPrefUtils.getSharedPrefValue(AppConstants.NOTIF_MSG));
        } else {
            notifTitle.setText("No Notification Available...!");
        }
    }

    private void initViews() {

        notifTitle = findViewById(R.id.notifTitle);
        notifMsg = findViewById(R.id.notifMsg);
    }

    public void onClickBack(View view) {

        utils.startNewActivity(LicenceDetailsActivity.class,null,true);
    }

    @Override
    public void onBackPressed() {
        utils.startNewActivity(LicenceDetailsActivity.class,null,true);
    }
}
