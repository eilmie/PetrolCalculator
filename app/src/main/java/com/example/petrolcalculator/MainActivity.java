package com.example.petrolcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerPetrolType;
    private EditText etPricePerLiter;
    private EditText etFuelUsage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init views
        spinnerPetrolType = findViewById(R.id.spinnerPetrolType);
        etPricePerLiter   = findViewById(R.id.etPricePerLiter);
        etFuelUsage       = findViewById(R.id.etFuelUsage);
        rgBudiMadani      = findViewById(R.id.rgBudiMadani);
        rbYes             = findViewById(R.id.rbYes);
        rbNo              = findViewById(R.id.rbNo);
        btnCalculate      = findViewById(R.id.btnCalculate);
        btnReset          = findViewById(R.id.btnReset);
        cardResult        = findViewById(R.id.cardResult);
        tvTotalCost       = findViewById(R.id.tvTotalCost);
        tvBudiRebate      = findViewById(R.id.tvBudiRebate);
        tvTotalSaving     = findViewById(R.id.tvTotalSaving);
        tvFinalPayable    = findViewById(R.id.tvFinalPayable);
        tvRebateLabel     = findViewById(R.id.tvRebateLabel);
        tvSavingLabel     = findViewById(R.id.tvSavingLabel);
        tvFinalLabel      = findViewById(R.id.tvFinalLabel);
        tvEligibilityNote = findViewById(R.id.tvEligibilityNote);

        // Setup petrol type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.petrol_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetrolType.setAdapter(adapter);

        cardResult.setVisibility(View.GONE);

        btnCalculate.setOnClickListener(v -> calculate());
        btnReset.setOnClickListener(v -> reset());
    }

    private void calculate() {
        String priceStr = etPricePerLiter.getText().toString().trim();
        String usageStr = etFuelUsage.getText().toString().trim();

        if (priceStr.isEmpty()) {
            etPricePerLiter.setError("Please enter petrol price");
            etPricePerLiter.requestFocus();
            return;
        }
        if (usageStr.isEmpty()) {
            etFuelUsage.setError("Please enter fuel usage");
            etFuelUsage.requestFocus();
            return;
        }
        if (rgBudiMadani.getCheckedRadioButtonId() == -1) {
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

        String petrolType  = spinnerPetrolType.getSelectedItem().toString();
        boolean isEligible = rbYes.isChecked();
        boolean isRON95    = petrolType.equals("RON95");

        // Step 1: Total petrol cost
        double totalCost = fuelUsage * pricePerLiter;

        // Step 2 & 3: BUDI rebate (RON95 + eligible only)
        double budiRebate  = 0;
        double totalSaving = 0;
        double finalPayable;

        if (isEligible && isRON95) {
            budiRebate   = fuelUsage * SUBSIDY_RATE;
            totalSaving  = totalCost - budiRebate;
            finalPayable = totalSaving;
        } else {
            finalPayable = totalCost;
        }

        // Show results
        tvTotalCost.setText(String.format("RM %.2f", totalCost));

        if (isEligible && isRON95) {
            tvRebateLabel.setVisibility(View.VISIBLE);
            tvBudiRebate.setVisibility(View.VISIBLE);
            tvSavingLabel.setVisibility(View.VISIBLE);
            tvTotalSaving.setVisibility(View.VISIBLE);
            tvFinalLabel.setVisibility(View.VISIBLE);
            tvFinalPayable.setVisibility(View.VISIBLE);
            tvEligibilityNote.setVisibility(View.GONE);

            tvBudiRebate.setText(String.format("RM %.2f", budiRebate));
            tvTotalSaving.setText(String.format("RM %.2f", totalSaving));
            tvFinalPayable.setText(String.format("RM %.2f", finalPayable));
        } else {
            tvRebateLabel.setVisibility(View.GONE);
            tvBudiRebate.setVisibility(View.GONE);
            tvSavingLabel.setVisibility(View.GONE);
            tvTotalSaving.setVisibility(View.GONE);
            tvFinalLabel.setVisibility(View.GONE);
            tvFinalPayable.setVisibility(View.GONE);

            if (!isEligible) {
                tvEligibilityNote.setText("Not eligible for BUDI MADANI rebate.");
            } else {
                tvEligibilityNote.setText("BUDI MADANI rebate applies to RON95 only.");
            }
            tvEligibilityNote.setVisibility(View.VISIBLE);
        }

        cardResult.setVisibility(View.VISIBLE);

        // Scroll to result
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void reset() {
        etPricePerLiter.setText("");
        etFuelUsage.setText("");
        rgBudiMadani.clearCheck();
        spinnerPetrolType.setSelection(0);
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