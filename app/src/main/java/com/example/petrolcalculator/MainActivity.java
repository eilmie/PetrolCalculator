package com.example.petrolcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerPetrolType;
    private EditText etPricePerLiter;
    private EditText etFuelUsage;
    private LinearLayout layoutBudiMadani;
    private RadioGroup rgBudiMadani;
    private RadioButton rbYes, rbNo;
    private Button btnCalculate, btnReset;
    private CardView cardResult;

    private TextView tvTotalCost;
    private TextView tvBudiRebate;
    private TextView tvTotalSaving;
    private TextView tvFinalPayable;
    private TextView tvRebateLabel;
    private TextView tvSavingLabel;
    private TextView tvFinalLabel;
    private TextView tvEligibilityNote;

    private static final double SUBSIDY_RATE = 1.99;
    private boolean isEditingPrice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init views
        spinnerPetrolType  = findViewById(R.id.spinnerPetrolType);
        etPricePerLiter    = findViewById(R.id.etPricePerLiter);
        etFuelUsage        = findViewById(R.id.etFuelUsage);
        layoutBudiMadani   = findViewById(R.id.layoutBudiMadani);
        rgBudiMadani       = findViewById(R.id.rgBudiMadani);
        rbYes              = findViewById(R.id.rbYes);
        rbNo               = findViewById(R.id.rbNo);
        btnCalculate       = findViewById(R.id.btnCalculate);
        btnReset           = findViewById(R.id.btnReset);
        cardResult         = findViewById(R.id.cardResult);
        tvTotalCost        = findViewById(R.id.tvTotalCost);
        tvBudiRebate       = findViewById(R.id.tvBudiRebate);
        tvTotalSaving      = findViewById(R.id.tvTotalSaving);
        tvFinalPayable     = findViewById(R.id.tvFinalPayable);
        tvRebateLabel      = findViewById(R.id.tvRebateLabel);
        tvSavingLabel      = findViewById(R.id.tvSavingLabel);
        tvFinalLabel       = findViewById(R.id.tvFinalLabel);
        tvEligibilityNote  = findViewById(R.id.tvEligibilityNote);

        // Petrol type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.petrol_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetrolType.setAdapter(adapter);

        cardResult.setVisibility(View.GONE);

        // FIX 1: Show BUDI MADANI question only when RON95 is selected
        spinnerPetrolType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("RON95")) {
                    layoutBudiMadani.setVisibility(View.VISIBLE);
                } else {
                    layoutBudiMadani.setVisibility(View.GONE);
                    rgBudiMadani.clearCheck();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // FIX 2: Bank-style price input — always shows 2 decimal places
        etPricePerLiter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditingPrice) return;
                isEditingPrice = true;

                String input = s.toString().replaceAll("[^0-9]", ""); // digits only
                if (input.isEmpty()) {
                    etPricePerLiter.setText("");
                } else {
                    // treat input as cents, divide by 100
                    long cents = Long.parseLong(input);
                    if (cents > 99999) cents = 99999; // cap at 999.99
                    String formatted = String.format("%.2f", cents / 100.0);
                    etPricePerLiter.setText(formatted);
                    etPricePerLiter.setSelection(formatted.length());
                }

                isEditingPrice = false;
            }
        });

        btnCalculate.setOnClickListener(v -> calculate());
        btnReset.setOnClickListener(v -> reset());
    }

    private void calculate() {
        String priceStr = etPricePerLiter.getText().toString().trim();
        String usageStr = etFuelUsage.getText().toString().trim();

        if (priceStr.isEmpty() || priceStr.equals("0.00")) {
            etPricePerLiter.setError("Please enter petrol price");
            etPricePerLiter.requestFocus();
            return;
        }
        if (usageStr.isEmpty()) {
            etFuelUsage.setError("Please enter fuel usage");
            etFuelUsage.requestFocus();
            return;
        }

        String petrolType = spinnerPetrolType.getSelectedItem().toString();
        boolean isRON95   = petrolType.equals("RON95");

        // Only validate BUDI eligibility for RON95
        if (isRON95 && rgBudiMadani.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select BUDI MADANI eligibility", Toast.LENGTH_SHORT).show();
            return;
        }

        double pricePerLiter;
        double fuelUsage;
        try {
            pricePerLiter = Double.parseDouble(priceStr);
            fuelUsage     = Double.parseDouble(usageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pricePerLiter <= 0 || fuelUsage <= 0) {
            Toast.makeText(this, "Values must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isEligible = rbYes.isChecked();

        // Step 1: Total petrol cost
        double totalCost = fuelUsage * pricePerLiter;

        // Step 2 & 3: BUDI rebate (RON95 + eligible only)
        double budiRebate  = 0;
        double totalSaving = 0;
        double finalPayable;

        if (isRON95 && isEligible) {
            budiRebate   = fuelUsage * SUBSIDY_RATE;
            totalSaving  = totalCost - budiRebate;
            finalPayable = totalSaving;
        } else {
            finalPayable = totalCost;
        }

        // Display results
        tvTotalCost.setText(String.format("RM %.2f", totalCost));

        if (isRON95 && isEligible) {
            tvRebateLabel.setVisibility(View.VISIBLE);
            tvBudiRebate.setVisibility(View.VISIBLE);
            tvSavingLabel.setVisibility(View.VISIBLE);
            tvTotalSaving.setVisibility(View.VISIBLE);
            tvFinalLabel.setVisibility(View.VISIBLE);
            tvFinalPayable.setVisibility(View.VISIBLE);
            tvEligibilityNote.setVisibility(View.GONE);

            // FIX 3: minus symbol on rebate so user knows it's a discount
            tvBudiRebate.setText(String.format("- RM %.2f", budiRebate));
            tvTotalSaving.setText(String.format("RM %.2f", totalSaving));
            tvFinalPayable.setText(String.format("RM %.2f", finalPayable));
        } else {
            tvRebateLabel.setVisibility(View.GONE);
            tvBudiRebate.setVisibility(View.GONE);
            tvSavingLabel.setVisibility(View.GONE);
            tvTotalSaving.setVisibility(View.GONE);
            tvFinalLabel.setVisibility(View.GONE);
            tvFinalPayable.setVisibility(View.GONE);

            if (isRON95) {
                tvEligibilityNote.setText("Not eligible for BUDI MADANI rebate.");
            } else {
                tvEligibilityNote.setText("BUDI MADANI rebate applies to RON95 only.");
            }
            tvEligibilityNote.setVisibility(View.VISIBLE);
        }

        cardResult.setVisibility(View.VISIBLE);

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void reset() {
        etPricePerLiter.setText("");
        etFuelUsage.setText("");
        rgBudiMadani.clearCheck();
        spinnerPetrolType.setSelection(0);
        layoutBudiMadani.setVisibility(View.VISIBLE); // RON95 is default selection
        cardResult.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            reset();
            return true;
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}