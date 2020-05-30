package io.github.rainbyte.nanoxrbunits;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Locale;
import java.util.function.Consumer;

public class MainActivity extends Activity {

    public static final String DONATION_ADDRESS = "nano_1tmt1n4is9cks3xi3eroobjosezj96aaden1pnmdngnqy7kb5nhn9koiy58s";

    private EditText NanoEdit;
    private EditText mNanoEdit;
    private EditText uNanoEdit;
    private EditText fNanoEdit;
    private EditText rawEdit;

    private static void addAfterTextChanged(EditText edit, Consumer<Editable> afterTextChanged) {
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable editable) {
                afterTextChanged.accept(editable);
            }
        });
    }

    private static InputFilter inputFilter(NanoUnit unit) {
        String partL = String.format(Locale.ENGLISH, "\\d{0,%d}", unit.getIntegers());
        String partR = unit.getDecimals() > 0
                ? String.format(Locale.ENGLISH, "(\\.\\d{0,%d})?", unit.getDecimals())
                : "";
        String pattern = partL + partR;

        return (source, start, end, dest, dstart, dend) -> {
            StringBuilder builder = new StringBuilder(dest);
            builder.replace(dstart, dend, source.subSequence(start, end).toString());
            while (builder.length() > 0 && builder.charAt(0) == '0') {
                builder.deleteCharAt(0);
            }
            return builder.toString().matches(pattern) ? null : "";
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NanoEdit = findViewById(R.id.NanoEdit);
        mNanoEdit = findViewById(R.id.mNanoEdit);
        uNanoEdit = findViewById(R.id.uNanoEdit);
        fNanoEdit = findViewById(R.id.fNanoEdit);
        rawEdit = findViewById(R.id.rawEdit);

        ImageButton qrcodeButton = findViewById(R.id.qrcodeButton);
        qrcodeButton.setOnClickListener(v -> {
            Context baseContext = getBaseContext();
            CharSequence clipboardMsgDonation = baseContext.getText(R.string.clipboard_donation);
            CharSequence clipboardMsgCopied = baseContext.getText(R.string.clipboard_copied);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText(clipboardMsgDonation, DONATION_ADDRESS);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(baseContext, clipboardMsgCopied, Toast.LENGTH_SHORT).show();
            }
        });

        NanoEdit.setFilters(new InputFilter[] { inputFilter(NanoUnit.Nano) });
        mNanoEdit.setFilters(new InputFilter[] { inputFilter(NanoUnit.mNano) });
        uNanoEdit.setFilters(new InputFilter[] { inputFilter(NanoUnit.uNano) });
        fNanoEdit.setFilters(new InputFilter[] { inputFilter(NanoUnit.fNano) });
        rawEdit.setFilters(new InputFilter[] { inputFilter(NanoUnit.Raw) });

        addAfterTextChanged(NanoEdit, it -> {
            if (NanoEdit.hasFocus()) {
                update(NanoUnit.Nano, it.toString());
            }
        });
        addAfterTextChanged(mNanoEdit, it -> {
            if (mNanoEdit.hasFocus()) {
                update(NanoUnit.mNano, it.toString());
            }
        });
        addAfterTextChanged(uNanoEdit, it -> {
            if (uNanoEdit.hasFocus()) {
                update(NanoUnit.uNano, it.toString());
            }
        });
        addAfterTextChanged(fNanoEdit, it -> {
            if (fNanoEdit.hasFocus()) {
                update(NanoUnit.fNano, it.toString());
            }
        });
        addAfterTextChanged(rawEdit, it -> {
            if (rawEdit.hasFocus()) {
                update(NanoUnit.Raw, it.toString());
            }
        });

        update(NanoUnit.Nano, "1", false);
    }

    private void update(NanoUnit baseUnit, String input, boolean omitBaseUnit) {
        String raws = baseUnit.toRaws(input);
        for (NanoUnit unit : NanoUnit.values()) {
            if (unit == baseUnit && omitBaseUnit) continue;
            switch (unit) {
                case Nano:
                    NanoEdit.setText(NanoUnit.Nano.fromRaws(raws));
                    break;
                case mNano:
                    mNanoEdit.setText(NanoUnit.mNano.fromRaws(raws));
                    break;
                case uNano:
                    uNanoEdit.setText(NanoUnit.uNano.fromRaws(raws));
                    break;
                case fNano:
                    fNanoEdit.setText(NanoUnit.fNano.fromRaws(raws));
                    break;
                case Raw:
                    rawEdit.setText(NanoUnit.Raw.fromRaws(raws));
                    break;
            }
        }
    }

    private void update(NanoUnit baseUnit, String input) {
        update(baseUnit, input, true);
    }
}
