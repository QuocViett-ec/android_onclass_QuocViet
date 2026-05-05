package com.example.k234112e_hqv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CalculatorActivity extends AppCompatActivity {
    EditText editFormular;
    Button buttonDel,button_equal,button_1dividex,button_xx,button_sqrtx,button_plus_minus;
    Button buttonPercent, buttonCE, buttonC, button_dot;
    TextView textViewMC,textViewMR,textViewMPlus,textViewMMinus,textViewMS,textViewM;
    View.OnClickListener m_onclick;
    private double accumulator = 0.0;
    private String pendingOperator = null;
    private boolean isNewInput = true;
    private boolean hasError = false;
    private double memoryValue = 0.0;
    private boolean hasMemory = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        addView();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDelete();
            }
        });

        button_equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEquals();
            }
        });

        buttonPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePercent();
            }
        });

        buttonCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClearEntry();
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClearAll();
            }
        });

        button_1dividex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleReciprocal();
            }
        });

        button_xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSquare();
            }
        });

        button_sqrtx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSqrt();
            }
        });

        button_plus_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleToggleSign();
            }
        });

        button_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processInputData(view);
            }
        });

        m_onclick=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.equals(textViewM))
                {
                    //khách hàng nhấn txtM
                }
                else if (view.equals(textViewMMinus))
                {
                    //khách hàng nhấn txtMinus
                }//không dùng dấu == để so sánh vì nó không hiểu so sánh ô nhớ khi dùng ==

                if (view.equals(textViewMC)) {
                    handleMemoryClear();
                } else if (view.equals(textViewMR)) {
                    handleMemoryRecall();
                } else if (view.equals(textViewMPlus)) {
                    handleMemoryAdd();
                } else if (view.equals(textViewMMinus)) {
                    handleMemorySubtract();
                } else if (view.equals(textViewMS)) {
                    handleMemoryStore();
                }
            }
        };
        //m_onclick là biến có khả năng sinh sự kiện (variable as listener)
        //thường dùng để sharing sự kiện (từ 2 view trở lên)
        textViewM.setOnClickListener(m_onclick);
        textViewMMinus.setOnClickListener(m_onclick);
        textViewMR.setOnClickListener(m_onclick);
        textViewMS.setOnClickListener(m_onclick);
        textViewMPlus.setOnClickListener(m_onclick);
        textViewMC.setOnClickListener(m_onclick);
    }

    private void addView() {
        editFormular=findViewById(R.id.editFormular);
        buttonDel=findViewById(R.id.buttonDel);
        button_equal=findViewById(R.id.button_equal);
        buttonPercent = findViewById(R.id.buttonPercent);
        buttonCE = findViewById(R.id.buttonCE);
        buttonC = findViewById(R.id.buttonC);
        button_1dividex = findViewById(R.id.button_1dividex);
        button_xx = findViewById(R.id.button_xx);
        button_sqrtx = findViewById(R.id.button_sqrtx);
        button_plus_minus = findViewById(R.id.button_plus_minus);
        button_dot = findViewById(R.id.button_dot);

        textViewMC=findViewById(R.id.textViewMC);
        textViewMR=findViewById(R.id.textViewMR);
        textViewMPlus=findViewById(R.id.textViewMPlus);
        textViewMMinus=findViewById(R.id.textViewMMinus);
        textViewMS=findViewById(R.id.textViewMS);
        textViewM=findViewById(R.id.textViewM);
    }


    public void processInputData(View view) {
        Button button_clicked = (Button) view;
        String input_value = button_clicked.getText().toString();

        if (hasError) {
            handleClearAll();
        }

        if (isOperator(input_value)) {
            handleOperator(input_value);
            return;
        }

        if (".".equals(input_value)) {
            handleDecimal();
            return;
        }

        // digits
        String old_value = editFormular.getText().toString();
        if (isNewInput || "0".equals(old_value)) {
            editFormular.setText(input_value);
        } else {
            editFormular.setText(old_value + input_value);
        }
        isNewInput = false;
    }

    private boolean isOperator(String value) {
        return "+".equals(value) || "-".equals(value) || "x".equals(value) || ":".equals(value);
    }

    private void handleOperator(String operator) {
        double currentValue = getDisplayValue();

        if (pendingOperator != null && !isNewInput) {
            double result = applyOperator(accumulator, currentValue, pendingOperator);
            if (hasError) {
                return;
            }
            accumulator = result;
            setDisplayValue(result);
        } else if (pendingOperator == null) {
            accumulator = currentValue;
        }

        pendingOperator = operator;
        isNewInput = true;
    }

    private void handleEquals() {
        if (pendingOperator == null) {
            return;
        }
        double currentValue = getDisplayValue();
        double result = applyOperator(accumulator, currentValue, pendingOperator);
        if (hasError) {
            return;
        }
        setDisplayValue(result);
        accumulator = result;
        pendingOperator = null;
        isNewInput = true;
    }

    private void handlePercent() {
        double currentValue = getDisplayValue();
        if (pendingOperator != null) {
            double percentValue = accumulator * currentValue / 100.0;
            setDisplayValue(percentValue);
            isNewInput = false;
        } else {
            setDisplayValue(currentValue / 100.0);
            isNewInput = true;
        }
    }

    private void handleClearEntry() {
        editFormular.setText("0");
        isNewInput = true;
        hasError = false;
    }

    private void handleClearAll() {
        editFormular.setText("0");
        accumulator = 0.0;
        pendingOperator = null;
        isNewInput = true;
        hasError = false;
    }

    private void handleDelete() {
        if (hasError) {
            handleClearAll();
            return;
        }
        String current = editFormular.getText().toString();
        if (current.length() <= 1 || "-".equals(current) || (current.startsWith("-") && current.length() <= 2)) {
            editFormular.setText("0");
            isNewInput = true;
            return;
        }
        editFormular.setText(current.substring(0, current.length() - 1));
        isNewInput = false;
    }

    private void handleReciprocal() {
        double value = getDisplayValue();
        if (value == 0.0) {
            showError("Cannot divide by zero");
            return;
        }
        setDisplayValue(1.0 / value);
        isNewInput = true;
    }

    private void handleSquare() {
        double value = getDisplayValue();
        setDisplayValue(value * value);
        isNewInput = true;
    }

    private void handleSqrt() {
        double value = getDisplayValue();
        if (value < 0.0) {
            showError("Invalid input");
            return;
        }
        setDisplayValue(Math.sqrt(value));
        isNewInput = true;
    }

    private void handleToggleSign() {
        String current = editFormular.getText().toString();
        if ("0".equals(current)) {
            return;
        }
        if (current.startsWith("-")) {
            editFormular.setText(current.substring(1));
        } else {
            editFormular.setText("-" + current);
        }
        isNewInput = false;
    }

    private void handleDecimal() {
        String current = editFormular.getText().toString();
        if (isNewInput) {
            editFormular.setText("0.");
            isNewInput = false;
            return;
        }
        if (!current.contains(".")) {
            editFormular.setText(current + ".");
        }
    }

    private void handleMemoryClear() {
        memoryValue = 0.0;
        hasMemory = false;
    }

    private void handleMemoryRecall() {
        if (!hasMemory) {
            return;
        }
        setDisplayValue(memoryValue);
        isNewInput = true;
    }

    private void handleMemoryAdd() {
        memoryValue += getDisplayValue();
        hasMemory = true;
    }

    private void handleMemorySubtract() {
        memoryValue -= getDisplayValue();
        hasMemory = true;
    }

    private void handleMemoryStore() {
        memoryValue = getDisplayValue();
        hasMemory = true;
    }

    private double applyOperator(double left, double right, String operator) {
        if ("+".equals(operator)) {
            return left + right;
        }
        if ("-".equals(operator)) {
            return left - right;
        }
        if ("x".equals(operator)) {
            return left * right;
        }
        if (":".equals(operator)) {
            if (right == 0.0) {
                showError("Cannot divide by zero");
                return 0.0;
            }
            return left / right;
        }
        return right;
    }

    private double getDisplayValue() {
        String text = editFormular.getText().toString();
        if (text.isEmpty() || "-".equals(text)) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private void setDisplayValue(double value) {
        editFormular.setText(formatNumber(value));
    }

    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "NaN";
        }
        long longValue = (long) value;
        if (value == longValue) {
            return String.valueOf(longValue);
        }
        java.math.BigDecimal decimal = java.math.BigDecimal.valueOf(value).stripTrailingZeros();
        return decimal.toPlainString();
    }

    private void showError(String message) {
        editFormular.setText(message);
        hasError = true;
        isNewInput = true;
    }
}