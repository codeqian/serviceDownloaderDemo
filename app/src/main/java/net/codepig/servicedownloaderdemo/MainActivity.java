package net.codepig.servicedownloaderdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private String _url="http://www.boosj.com/apk/boosjDance.apk";
    private EditText urlText;
    private Button goBtn,cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goBtn=(Button) findViewById(R.id.goBtn);
        cancelBtn=(Button) findViewById(R.id.cancelBtn);
        urlText=(EditText) findViewById(R.id.urlText);
        urlText.setText(_url);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _url=urlText.getText().toString();
                //start download
                start_service();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancel downloading
            }
        });
    }

    public void start_service(){
        Intent intent=new Intent(this,DownLoadService.class);
        intent.putExtra("download_url",_url);
        startService(intent);
    }
}
