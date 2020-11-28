package com.example.booktracker.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.boundary.CaptureAct;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ScanActivity extends AppCompatActivity {
    private ScanActivity ref = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanCode();
    }

    /**
     * scanCode instantiate intent integrator and initiate the scan
     * https://github.com/zxing/zxing/wiki/Scanning-Via-Intent
     * @author Andrew Wood <awood@ualberta.ca>
     */
    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);

        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    /**
     * onActivityResult will handle the result of the initiateScan
     * https://github.com/zxing/zxing/wiki/Scanning-Via-Intent
     * @author Andrew Wood <awood@ualberta.ca>
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scan Result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanCode();
                    }
                }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getCallingActivity() != null){
                            //==========code to pass data back to parent activity======
                            Intent data = new Intent();
                            data.setData(Uri.parse(result.getContents()));
                            setResult(RESULT_OK,data);
                            //=========================================================
                            finish();
                        } else {
                            Intent intent = new Intent(ref,ViewBookActivity.class);
                            intent.putExtra(EXTRA_MESSAGE,result.getContents());
                            startActivity(intent);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
