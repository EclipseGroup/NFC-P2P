package com.eclipsegroup.nfc_p2p;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private NfcAdapter nfcAdapter;
    private EditText txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        txtContent = (EditText) findViewById(R.id.txtContent);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is not available, closing the app", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        nfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        nfcAdapter.enableForegroundDispatch(this, PendingIntent.getActivity(this, 0, intent, 0)
                , new IntentFilter[]{}, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /* Questo metodo è attivato quando la activity riceve un nuovo intent */
        super.onNewIntent(intent);

        setIntent(intent);
    }

    void processIntent(Intent intent) {
        /* In caso di messaggio ricevuto questo viene processato da questo metodo */

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage msg = (NdefMessage) rawMsgs[0];

        /* Aggiornamento EditText a schermo con il testo ricevuto */
        txtContent.setText(new String(msg.getRecords()[0].getPayload()));
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        /* L' NdefMessage del return di questo metodo viene spedito al dispositivo vicino */
        /* Il metodo si attiva in automatico quando l'utente attiva l'Android Beam */

        String text = txtContent.getText().toString(); /* Lettura testo dalla EditText a schermo */
        String strType = "application/vnd.com.dorel.eclipsegroup.nfcp2p"; /* impostazione mimeType */
        byte[] type = strType.getBytes(Charset.forName("US-ASCII"));

        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, type, new byte[0],
                text.getBytes(Charset.forName("US-ASCII")));
        NdefRecord[] ndefRecords = new NdefRecord[]{ ndefRecord };

        return new NdefMessage(ndefRecords);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}