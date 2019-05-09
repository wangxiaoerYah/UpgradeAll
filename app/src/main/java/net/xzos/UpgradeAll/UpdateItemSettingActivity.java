package net.xzos.UpgradeAll;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

public class UpdateItemSettingActivity extends AppCompatActivity {

    private static final String TAG = "UpdateItemSetting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item_setting);
        // 以下是按键事件
        Button versionCheckButton = findViewById(R.id.versionCheckTextButton);
        versionCheckButton.setOnClickListener(v -> {
            // 版本检查设置
            JSONObject versionCheckerJsonObject = getVersionChecker();
            VersionChecker versionChecker = new VersionChecker(versionCheckerJsonObject);
            String appVersion = versionChecker.getVersion();
            if (appVersion != null) {
                Toast.makeText(UpdateItemSettingActivity.this, "version: " + appVersion, Toast.LENGTH_SHORT).show();
            }
        });
        ImageView helpImageView = findViewById(R.id.helpImageView);
        helpImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("")); // TODO：帮助文档网址
            intent = Intent.createChooser(intent, "请选择浏览器查看帮助文档");
            startActivity(intent);
        });
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            EditText editName = findViewById(R.id.editName);
            EditText editUrl = findViewById(R.id.editUrl);
            String name = editName.getText().toString();
            String url = editUrl.getText().toString();
            if (url.substring(url.length() - 1).equals("/")) {
                url = url.substring(0, url.length() - 1);
                // 判断url是否多余 /
            }
            Spinner apiSpinner = findViewById(R.id.api_spinner);
            String api = apiSpinner.getSelectedItem().toString();
            JSONObject versionChecker = getVersionChecker();
            boolean addRepoSuccess = addRepoDatabase(name, api, url, versionChecker);
            if (addRepoSuccess) {
                Intent intent = new Intent(UpdateItemSettingActivity.this, MainActivity.class);
                startActivity(intent);
                // 跳转主页面
            } else {
                Toast.makeText(UpdateItemSettingActivity.this, "什么？数据库添加失败！", Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getVersionChecker() {
        // 获取versionChecker
        JSONObject versionChecker = new JSONObject();
        Spinner versionCheckSpinner = findViewById(R.id.versionCheckSpinner);
        EditText editVersionCheckText = findViewById(R.id.editVersionCheckText);
        EditText editVersionCheckRegular = findViewById(R.id.editVersionCheckRegular);
        String versionCheckerText = editVersionCheckText.getText().toString();
        String versionCheckerApi = versionCheckSpinner.getSelectedItem().toString();
        String versionCheckerRegular = editVersionCheckRegular.getText().toString();
        Log.d(TAG, "getVersionChecker:  " + versionCheckerRegular);
        switch (versionCheckerApi) {
            case "APP 版本":
                versionCheckerApi = "APP";
                break;
            case "Magisk 模块":
                versionCheckerApi = "Magisk";
                break;
        }
        try {
            versionChecker.put("api", versionCheckerApi);
            versionChecker.put("text", versionCheckerText);
            versionChecker.put("regular", versionCheckerRegular);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return versionChecker;
    }

    boolean addRepoDatabase(String name, String api, String url, JSONObject versionChecker) {
        // TODO: 可被忽略的参数
        if (api.length() != 0 && url.length() != 0) {
            String repo = "";
            String api_url = "";
            switch (api.toLowerCase()) {
                case "github":
                    String[] apiUrlStringList = GithubApi.getApiUrl(url);
                    api_url = apiUrlStringList[0];
                    repo = apiUrlStringList[1];
                    break;
            }
            if (name.length() == 0) {
                name = repo;
                // 如果未自定义名称，则使用仓库名
            }
            RepoDatabase repoDatabase = new RepoDatabase();
            // 开启数据库
            LitePal.deleteAll(RepoDatabase.class, "api_url = ?", api_url);
            // 删除所有数据库重复项
            repoDatabase.setName(name);
            repoDatabase.setApi(api);
            repoDatabase.setUrl(url);
            repoDatabase.setApiUrl(api_url);
            repoDatabase.setVersionChecker(versionChecker);
            repoDatabase.save();
            // 将数据存入 RepoDatabase 数据库
            return true;
        }
        return false;
    }
}