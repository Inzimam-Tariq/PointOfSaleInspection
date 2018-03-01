package web2design.epos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import web2design.epos.R;

import web2design.epos.Adapters.QuestionsAdapter;
import web2design.epos.models.DataInfo;
import web2design.epos.models.OptionsInfo;
import web2design.epos.models.QuestionsInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements HttpResponseCallback {

    private ProgressDialog progressDialog;
    private DataInfo dataInfo;
    private List<DataInfo> dataList;
    private QuestionsInfo questionsInfo;
    private ArrayList<QuestionsInfo> questionsList;
    private OptionsInfo optionsInfo;
    private List<OptionsInfo> optionsList;

//    private QuestionsListAdapter questionsListAdapter;
    private QuestionsAdapter questionsAdapter;

//    private ListView listView;
    private RecyclerView recyclerView;
    private TextView cat_name, nextBtn;
//    private ImageView cat_image;
    private RelativeLayout container;
    private ProgressBar loading;

    private List<String> questionsDataList, answersDataList, valuesDataList;
    private String questionIds, optionIds, options;

    private int i, j, k;


    private String id,subId,subName, typeName;
    private String dataToSend = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUtils();
        initViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Questions");
        progressDialog.setMessage("Please wait...");

        dataList = new ArrayList<>();
        questionsList = new ArrayList<>();
        optionsList = new ArrayList<>();

        questionsAdapter = new QuestionsAdapter(this, questionsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(questionsAdapter);

        if (AppConstants.totalLength< AppConstants.subCatInfoList.size()){

            id = AppConstants.subCatInfoList.get(AppConstants.totalLength).getCategory_id();
            cat_name.setText(AppConstants.subCatInfoList.get(AppConstants.totalLength).getCategory_title());
            getFormData(id);
        }
    }

    @Override
    public void onBackPressed() {
        //
    }

    private void initViews() {

        cat_name = findViewById(R.id.catName);
        nextBtn = findViewById(R.id.nextBtn);

//        listView = (ListView) findViewById(R.id.questionList);
        recyclerView = findViewById(R.id.questionsRecyclerView);

        container = findViewById(R.id.container);
        container.setVisibility(View.GONE);
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
    }

    private void getFormData(String id) {
        progressDialog.show();
        httpService.getFormData(id, this);
    }

    @Override
    public void onCompleteHttpResponse(String response, String requestUrl) {
        if (response == null) {
            utils.showToast("No Response....!");
            return;
        } else {
            try {
                loading.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);

                Log.e("response: ",response);
                JSONObject jsonObject = new JSONObject(response);

                    JSONArray questionArray = jsonObject.getJSONArray("questions");
                    for (j = 0; j < questionArray.length(); j++) {
                        JSONObject questionData = questionArray.getJSONObject(j);
                        questionsInfo = new QuestionsInfo();
                        questionsInfo.setQuestion_id(questionData.optString("question_id"));
                        questionsInfo.setTemplate_id(questionData.optString("template_id"));
                        questionsInfo.setQuestion_text(questionData.optString("question_text"));
                        questionsInfo.setQuestion_type(questionData.optString("question_type"));

                        optionsList.clear();

                        JSONArray optionsArray = questionData.getJSONArray("options");
                        for (k = 0; k < optionsArray.length(); k++) {
                            JSONObject optionsData = optionsArray.getJSONObject(k);
                            optionsInfo = new OptionsInfo();
                            optionsInfo.setQuestion_number(String.valueOf(i));
                            optionsInfo.setOption_id(optionsData.optString("option_id"));
                            optionsInfo.setOption_text(optionsData.optString("option_text"));
                            optionsInfo.setOption_value(optionsData.optString("option_value"));

                            optionsList.add(optionsInfo);
                        }
                        questionsInfo.setOptions(optionsList);
                        questionsInfo.setOptions_size(String.valueOf(k));
                        questionsList.add(questionsInfo);
                        k = 0;

                        questionsAdapter.notifyDataSetChanged();

                    }

                progressDialog.dismiss();

//                questionsAdapter.notifyDataSetChanged();

                if (AppConstants.totalLength+1 == AppConstants.subCatInfoList.size()){

                    nextBtn.setText("Finish");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickNext(View view) {

        if (!AppConstants.selected.isEmpty()) {
            if (AppConstants.selected.size() == questionsList.size()) {

                JSONArray questionsArray = new JSONArray(AppConstants.selected);
                questionIds = questionsArray.toString();
                JSONArray answersIdsArray = new JSONArray(AppConstants.answers_id);
                optionIds = answersIdsArray.toString();
                JSONArray answersArray = new JSONArray(AppConstants.answers);
                options = answersArray.toString();

                JSONObject object = new JSONObject();
                try {
                    object.put("questions", new JSONArray(AppConstants.selected));
                    object.put("answers", new JSONArray(AppConstants.answers_id));
                    object.put("values",new JSONArray(AppConstants.answers));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dataToSend = object.toString();
                Log.e("Question Ids", AppConstants.selected.toString());
                Log.e("Option Ids", AppConstants.answers_id.toString());
                Log.e("Options", AppConstants.answers.toString());
                Log.e("Data To Send", dataToSend);


                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIcon(R.drawable.skafs_logo);
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                httpService.saveAnswers(dataToSend, new HttpResponseCallback() {
                    @Override
                    public void onCompleteHttpResponse(String response, String requestUrl) {

                        if (response == null) {
                            utils.showToast("No Response....!");
                            return;
                        } else {
                            try {
                                Log.e("Response", response);
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {

                                    if (AppConstants.totalLength + 1 == AppConstants.subCatInfoList.size()) {
                                        AppConstants.totalLength = 0;
                                        utils.showToast("Survey completed successfully....");
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setTitle("All forms submitted successfully.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        utils.startNewActivity(TakePicturesActivity.class, null, true);
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    } else {
                                        AppConstants.totalLength = AppConstants.totalLength + 1;
                                        utils.startNewActivity(MainActivity.class, null, true);
                                    }
                                } else {

                                    utils.showToast("Server Error...!");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {

                Log.e("Selected Size", String.valueOf(AppConstants.selected.size()));
                Log.e("Questions Size", String.valueOf(questionsList.size()));
                utils.showToast("All fields required.....!");
            }
        }
    }
}
