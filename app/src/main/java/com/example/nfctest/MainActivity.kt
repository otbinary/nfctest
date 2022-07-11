package com.example.nfctest

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Build
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var nfcAdapter: NfcAdapter

    private var previousTag: NfcA? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onResume() {
        super.onResume()

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        val intent = Intent(this, javaClass).addFlags(FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, flags)
        val techLists = arrayOf(arrayOf(IsoDep::class.java.name), arrayOf(NfcA::class.java.name))

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, techLists)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val newTag: NfcA? = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)?.let {
            NfcA.get(it)
        }
        newTag?.connect()
        val message = "previousTag.isConnected: ${previousTag?.isConnected}\nnewTag.isConnected: ${newTag?.isConnected}"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        previousTag = newTag
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }
}