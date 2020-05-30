package io.github.rainbyte.nanoxrbunits

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

const val DONATION_ADDRESS = "nano_1tmt1n4is9cks3xi3eroobjosezj96aaden1pnmdngnqy7kb5nhn9koiy58s"

fun EditText.doAfterTextChanged(afterTextChanged: (Editable?) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable)
        }
    })
}

class MainActivity : Activity() {

    private val NanoUnit.pattern: String
        get() = """\d{0,${integers}}""" + if (decimals > 0) """(\.\d{0,${decimals}})?""" else ""

    private fun NanoUnit.inputFilter() = InputFilter { source, start, end, dest, dstart, dend ->
        val regex = Regex(pattern)
        val builder = StringBuilder(dest)
        builder.replace(dstart, dend, source.subSequence(start, end).toString())
        val result = builder.dropWhile { it == '0' }
        if (source != null && regex.matches(result)) null else ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qrcodeButton.setOnClickListener {
            val clipboardMsgDonation = baseContext.getText(R.string.clipboard_donation)
            val clipboardMsgCopied = baseContext.getText(R.string.clipboard_copied)
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(clipboardMsgDonation, DONATION_ADDRESS)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(baseContext, clipboardMsgCopied, Toast.LENGTH_SHORT).show()
        }

        NanoEdit.filters = arrayOf(NanoUnit.Nano.inputFilter())
        mNanoEdit.filters = arrayOf(NanoUnit.mNano.inputFilter())
        uNanoEdit.filters = arrayOf(NanoUnit.uNano.inputFilter())
        rawEdit.filters = arrayOf(NanoUnit.Raw.inputFilter())

        NanoEdit.doAfterTextChanged {
            if (NanoEdit.hasFocus()) {
                update(NanoUnit.Nano, it.toString())
            }
        }
        mNanoEdit.doAfterTextChanged {
            if (mNanoEdit.hasFocus()) {
                update(NanoUnit.mNano, it.toString())
            }
        }
        uNanoEdit.doAfterTextChanged {
            if (uNanoEdit.hasFocus()) {
                update(NanoUnit.uNano, it.toString())
            }
        }
        fNanoEdit.doAfterTextChanged {
            if (fNanoEdit.hasFocus()) {
                update(NanoUnit.fNano, it.toString())
            }
        }
        rawEdit.doAfterTextChanged {
            if (rawEdit.hasFocus()) {
                update(NanoUnit.Raw, it.toString())
            }
        }

        update(NanoUnit.Nano, "1", false)
    }

    fun update(baseUnit: NanoUnit, input: String, omitBaseUnit: Boolean = true) {
        val raws = baseUnit.toRaws(input)
        for (unit in NanoUnit.values()) {
            if (unit == baseUnit && omitBaseUnit) continue
            when (unit) {
                NanoUnit.Nano -> NanoEdit.setText(NanoUnit.Nano.fromRaws(raws))
                NanoUnit.mNano -> mNanoEdit.setText(NanoUnit.mNano.fromRaws(raws))
                NanoUnit.uNano -> uNanoEdit.setText(NanoUnit.uNano.fromRaws(raws))
                NanoUnit.fNano -> fNanoEdit.setText(NanoUnit.fNano.fromRaws(raws))
                NanoUnit.Raw -> rawEdit.setText(NanoUnit.Raw.fromRaws(raws))
            }
        }
    }
}
