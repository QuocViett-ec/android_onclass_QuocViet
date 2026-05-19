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

/**
 * CalculatorActivity
 *
 * Kỹ năng thể hiện:
 *  1. Tách addView() / addEvents() (clean code pattern)
 *  2. Xử lý sự kiện theo 3 cách: XML onClick / Anonymous Inner Class / Shared Listener
 *  3. Hiển thị phép tính (formula bar) và kết quả riêng biệt
 *  4. Đầy đủ Memory: MC, MR, M+, M-, MS, M (hiển thị tên nếu có giá trị)
 *  5. Đầy đủ: %, CE, C, Del, 1/x, x^2, sqrt(x), +/-, .
 *  6. Xử lý lỗi: chia 0, căn số âm, input không hợp lệ
 */
public class CalculatorActivity extends AppCompatActivity {

    // ── Views ──────────────────────────────────────────────────────────────
    EditText editFormular;          // hiển thị số hiện tại / kết quả
    TextView textFormula;           // hiển thị biểu thức đang tính (e.g. "12 + 5 =")

    Button buttonPercent, buttonCE, buttonC, buttonDel;
    Button button_1dividex, button_xx, button_sqrtx;
    Button button_plus_minus, button_dot, button_equal;

    TextView textViewMC, textViewMR, textViewMPlus, textViewMMinus, textViewMS, textViewM;

    // ── State ──────────────────────────────────────────────────────────────
    /** Giá trị tích lũy (toán hạng bên trái của phép tính đang chờ) */
    private double accumulator = 0.0;

    /** Toán tử đang chờ: "+", "-", "x", ":" — null nếu chưa có */
    private String pendingOperator = null;

    /** Chuỗi biểu thức hiển thị ở formula bar (ví dụ "12 + ") */
    private String formulaDisplay = "";

    /** Đánh dấu người dùng vừa nhấn toán tử / equals / hàm đặc biệt — lần nhấn số tiếp theo bắt đầu nhập mới */
    private boolean isNewInput = true;

    /** Đang hiển thị lỗi */
    private boolean hasError = false;

    // ── Memory ─────────────────────────────────────────────────────────────
    private double memoryValue = 0.0;
    private boolean hasMemory = false;

    // ── Shared listener dùng chung cho 6 ô nhớ (Cách 3) ──────────────────
    private View.OnClickListener m_onclick;

    // ══════════════════════════════════════════════════════════════════════
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

    // ══════════════════════════════════════════════════════════════════════
    /** Ánh xạ view — Cách dùng: khai báo field, gọi findViewById một lần */
    private void addView() {
        editFormular      = findViewById(R.id.editFormular);
        textFormula       = findViewById(R.id.textFormula);

        buttonPercent     = findViewById(R.id.buttonPercent);
        buttonCE          = findViewById(R.id.buttonCE);
        buttonC           = findViewById(R.id.buttonC);
        buttonDel         = findViewById(R.id.buttonDel);
        button_1dividex   = findViewById(R.id.button_1dividex);
        button_xx         = findViewById(R.id.button_xx);
        button_sqrtx      = findViewById(R.id.button_sqrtx);
        button_plus_minus = findViewById(R.id.button_plus_minus);
        button_dot        = findViewById(R.id.button_dot);
        button_equal      = findViewById(R.id.button_equal);

        textViewMC        = findViewById(R.id.textViewMC);
        textViewMR        = findViewById(R.id.textViewMR);
        textViewMPlus     = findViewById(R.id.textViewMPlus);
        textViewMMinus    = findViewById(R.id.textViewMMinus);
        textViewMS        = findViewById(R.id.textViewMS);
        textViewM         = findViewById(R.id.textViewM);
    }

    // ══════════════════════════════════════════════════════════════════════
    /**
     * Gán sự kiện.
     *
     * • Các nút số, toán tử cơ bản, CE, C, Del, %, +/-, . và = đều dùng
     *   android:onClick="processInputData" trong XML (Cách 1).
     *   → Tất cả các nút trên đã có android:onClick trong layout,
     *     nên ở đây chỉ cần đăng ký thêm cho các ô nhớ.
     *
     * • 6 ô nhớ dùng Shared Listener (Cách 3) vì chúng là TextView
     *   (không dùng được android:onClick trực tiếp từ XML một cách thuận tiện)
     *   và chia sẻ cùng một logic phân nhánh.
     */
    private void addEvents() {
        // ── Shared listener cho 6 ô nhớ (Cách 3) ─────────────────────────
        m_onclick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dùng .equals() thay vì == vì đang so sánh object reference
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
                } else if (view.equals(textViewM)) {
                    handleMemoryRecall();   // M cũng recall giống MR
                }
            }
        };

        textViewMC.setOnClickListener(m_onclick);
        textViewMR.setOnClickListener(m_onclick);
        textViewMPlus.setOnClickListener(m_onclick);
        textViewMMinus.setOnClickListener(m_onclick);
        textViewMS.setOnClickListener(m_onclick);
        textViewM.setOnClickListener(m_onclick);
    }

    // ══════════════════════════════════════════════════════════════════════
    /**
     * Cách 1 — XML android:onClick="processInputData"
     * Xử lý TẤT CẢ các nút trên bàn phím.
     * Phân loại theo text của nút rồi gọi handler tương ứng.
     */
    public void processInputData(View view) {
        if (hasError) {
            // Nếu đang hiển thị lỗi, mọi phím đều reset (trừ Del/C sẽ xử lý riêng bên dưới)
            handleClearAll();
        }

        Button btn = (Button) view;
        String input = btn.getText().toString();

        switch (input) {
            // ── Chữ số ──────────────────────────────────────────────────
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                handleDigit(input);
                break;

            // ── Toán tử nhị phân ────────────────────────────────────────
            case "+":
            case "-":
            case "x":
            case ":":
                handleOperator(input);
                break;

            // ── Dấu chấm thập phân ──────────────────────────────────────
            case ".":
                handleDecimal();
                break;

            // ── Bằng ────────────────────────────────────────────────────
            case "=":
                handleEquals();
                break;

            // ── Xóa / Reset ─────────────────────────────────────────────
            case "CE":
                handleClearEntry();
                break;
            case "C":
                handleClearAll();
                break;
            case "Del":
                handleDelete();
                break;

            // ── Hàm đặc biệt ────────────────────────────────────────────
            case "%":
                handlePercent();
                break;
            case "+/-":
                handleToggleSign();
                break;
            case "1/x":
                handleReciprocal();
                break;
            case "x^2":
                handleSquare();
                break;
            case "sqrt(x)":
                handleSqrt();
                break;
        }
    }

    //  Digit & Decimal

    private void handleDigit(String digit) {
        String current = editFormular.getText().toString();
        if (isNewInput || "0".equals(current)) {
            setDisplay(digit);
        } else {
            setDisplay(current + digit);
        }
        isNewInput = false;
    }

    private void handleDecimal() {
        String current = editFormular.getText().toString();
        if (isNewInput) {
            setDisplay("0.");
            isNewInput = false;
            return;
        }
        if (!current.contains(".")) {
            setDisplay(current + ".");
        }
        isNewInput = false;
    }

    //  Operators

    private void handleOperator(String operator) {
        double currentValue = getDisplayValue();

        if (pendingOperator != null && !isNewInput) {
            // Tính kết quả trung gian (ví dụ: 3 + 5 x → tính 3+5 trước)
            double result = applyOperator(accumulator, currentValue, pendingOperator);
            if (hasError) return;
            accumulator = result;
            setDisplayValue(result);
        } else {
            accumulator = currentValue;
        }

        pendingOperator = operator;
        isNewInput = true;

        // Cập nhật formula bar: "12 + "
        formulaDisplay = formatNumber(accumulator) + " " + operatorSymbol(operator) + " ";
        textFormula.setText(formulaDisplay);
    }

    private void handleEquals() {
        if (pendingOperator == null) return;

        double currentValue = getDisplayValue();

        // Hiển thị biểu thức đầy đủ: "12 + 5 ="
        formulaDisplay = formatNumber(accumulator) + " "
                + operatorSymbol(pendingOperator) + " "
                + formatNumber(currentValue) + " =";
        textFormula.setText(formulaDisplay);

        double result = applyOperator(accumulator, currentValue, pendingOperator);
        if (hasError) return;

        setDisplayValue(result);
        accumulator = result;
        pendingOperator = null;
        isNewInput = true;
    }

    //  Special functions

    private void handlePercent() {
        double currentValue = getDisplayValue();
        double result;
        if (pendingOperator != null) {
            // 200 + 50% → 200 + (200 * 50 / 100) = 300
            result = accumulator * currentValue / 100.0;
            formulaDisplay = formatNumber(accumulator) + " "
                    + operatorSymbol(pendingOperator) + " "
                    + formatNumber(currentValue) + "%";
        } else {
            result = currentValue / 100.0;
            formulaDisplay = formatNumber(currentValue) + "%";
        }
        textFormula.setText(formulaDisplay);
        setDisplayValue(result);
        isNewInput = false;
    }

    private void handleToggleSign() {
        String current = editFormular.getText().toString();
        if ("0".equals(current) || hasError) return;
        if (current.startsWith("-")) {
            setDisplay(current.substring(1));
        } else {
            setDisplay("-" + current);
        }
        isNewInput = false;
    }

    private void handleReciprocal() {
        double value = getDisplayValue();
        formulaDisplay = "1/(" + formatNumber(value) + ")";
        textFormula.setText(formulaDisplay);
        if (value == 0.0) {
            showError("Không thể chia cho 0");
            return;
        }
        setDisplayValue(1.0 / value);
        isNewInput = true;
    }

    private void handleSquare() {
        double value = getDisplayValue();
        formulaDisplay = "sqr(" + formatNumber(value) + ")";
        textFormula.setText(formulaDisplay);
        setDisplayValue(value * value);
        isNewInput = true;
    }

    private void handleSqrt() {
        double value = getDisplayValue();
        formulaDisplay = "√(" + formatNumber(value) + ")";
        textFormula.setText(formulaDisplay);
        if (value < 0.0) {
            showError("Đầu vào không hợp lệ");
            return;
        }
        setDisplayValue(Math.sqrt(value));
        isNewInput = true;
    }

    //  Clear / Delete

    /** CE — chỉ xóa số đang nhập, giữ nguyên phép tính */
    private void handleClearEntry() {
        setDisplay("0");
        isNewInput = true;
        hasError = false;
    }

    /** C — xóa toàn bộ trạng thái */
    private void handleClearAll() {
        setDisplay("0");
        formulaDisplay = "";
        textFormula.setText("");
        accumulator = 0.0;
        pendingOperator = null;
        isNewInput = true;
        hasError = false;
    }

    /** Del — xóa ký tự cuối cùng */
    private void handleDelete() {
        if (hasError) {
            handleClearAll();
            return;
        }
        String current = editFormular.getText().toString();
        if (current.length() <= 1
                || "-".equals(current)
                || (current.startsWith("-") && current.length() <= 2)) {
            setDisplay("0");
            isNewInput = true;
            return;
        }
        setDisplay(current.substring(0, current.length() - 1));
        isNewInput = false;
    }

    //  Memory functions

    private void handleMemoryClear() {
        memoryValue = 0.0;
        hasMemory = false;
        updateMemoryLabel();
    }

    private void handleMemoryRecall() {
        if (!hasMemory) return;
        setDisplayValue(memoryValue);
        isNewInput = true;
    }

    private void handleMemoryAdd() {
        memoryValue += getDisplayValue();
        hasMemory = true;
        updateMemoryLabel();
        isNewInput = true;
    }

    private void handleMemorySubtract() {
        memoryValue -= getDisplayValue();
        hasMemory = true;
        updateMemoryLabel();
        isNewInput = true;
    }

    private void handleMemoryStore() {
        memoryValue = getDisplayValue();
        hasMemory = true;
        updateMemoryLabel();
        isNewInput = true;
    }

    /**
     * Cập nhật label ô textViewM để người dùng biết có giá trị trong bộ nhớ hay không.
     * Nếu có, hiển thị giá trị nhỏ bên cạnh chữ M.
     */
    private void updateMemoryLabel() {
        if (hasMemory) {
            textViewM.setText("M: " + formatNumber(memoryValue));
        } else {
            textViewM.setText(getString(R.string.str_m));
        }
    }

    //  Core arithmetic

    private double applyOperator(double left, double right, String operator) {
        switch (operator) {
            case "+": return left + right;
            case "-": return left - right;
            case "x": return left * right;
            case ":":
                if (right == 0.0) {
                    showError("Không thể chia cho 0");
                    return 0.0;
                }
                return left / right;
            default:
                return right;
        }
    }

    //  Display helpers

    private void setDisplay(String text) {
        editFormular.setText(text);
    }

    private void setDisplayValue(double value) {
        editFormular.setText(formatNumber(value));
    }

    private double getDisplayValue() {
        String text = editFormular.getText().toString();
        if (text.isEmpty() || "-".equals(text)) return 0.0;
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void showError(String message) {
        formulaDisplay = message;
        textFormula.setText(formulaDisplay);
        setDisplay("Lỗi");
        hasError = true;
        isNewInput = true;
    }

    //  Formatting helpers

    /**
     * Format số: không có phần thập phân thì bỏ .0,
     * có phần thập phân thì bỏ số 0 thừa ở cuối.
     */
    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "Lỗi";
        long longValue = (long) value;
        if (value == longValue) return String.valueOf(longValue);
        java.math.BigDecimal bd = java.math.BigDecimal.valueOf(value).stripTrailingZeros();
        return bd.toPlainString();
    }

    /** Trả về ký hiệu hiển thị đẹp cho toán tử */
    private String operatorSymbol(String operator) {
        switch (operator) {
            case "+": return "+";
            case "-": return "-";
            case "x": return "×";
            case ":": return "÷";
            default:  return operator;
        }
    }
}