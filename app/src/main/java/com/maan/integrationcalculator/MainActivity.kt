package com.maan.integrationcalculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.*


class Calculus3DGraphView(context: Context, attrs: android.util.AttributeSet? = null) : View(context, attrs) {
    private val gridPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(65, 125, 211, 252)
        strokeWidth = 1.2f
        style = android.graphics.Paint.Style.STROKE
    }
    private val curvePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(34, 211, 238)
        strokeWidth = 5f
        style = android.graphics.Paint.Style.STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeJoin = android.graphics.Paint.Join.ROUND
        setShadowLayer(10f, 0f, 3f, android.graphics.Color.CYAN)
    }
    private val glowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(30, 34, 211, 238)
        style = android.graphics.Paint.Style.FILL
    }
    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val path = android.graphics.Path()
        // Perspective grid gives the panel a lightweight 3D graph-paper appearance.
        for (i in 0..10) {
            val x = w * i / 10f
            canvas.drawLine(x, h * .22f, w * .5f + (x - w * .5f) * .78f, h * .94f, gridPaint)
            val y = h * (.22f + i * .065f)
            canvas.drawLine(w * .08f, y, w * .92f, y, gridPaint)
        }
        val curve = android.graphics.Path()
        val fill = android.graphics.Path()
        for (i in 0..180) {
            val t = i / 180f
            val x = w * (.08f + .84f * t)
            val y = h * (.58f - .25f * kotlin.math.sin(t * Math.PI * 2.0).toFloat() - .07f * kotlin.math.cos(t * Math.PI * 4.0).toFloat())
            if (i == 0) { curve.moveTo(x, y); fill.moveTo(x, h * .91f); fill.lineTo(x, y) }
            else { curve.lineTo(x, y); fill.lineTo(x, y) }
        }
        fill.lineTo(w * .92f, h * .91f); fill.close()
        canvas.drawPath(fill, glowPaint)
        canvas.drawPath(curve, curvePaint)
    }
}

class MainActivity : AppCompatActivity() {

    private var selectedFuncIndex = 0
    private var derivativeMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toggleMode = findViewById<MaterialButtonToggleGroup>(R.id.toggleMode)
        val btnModePoly = findViewById<MaterialButton>(R.id.btnModePoly)
        val layoutPoly = findViewById<LinearLayout>(R.id.layoutPoly)
        val layoutFunc = findViewById<LinearLayout>(R.id.layoutFunc)

        val etPolyCoef = findViewById<TextInputEditText>(R.id.etPolyCoef)
        val actvFunction = findViewById<AutoCompleteTextView>(R.id.actvFunction)
        val etLowerA = findViewById<TextInputEditText>(R.id.etLowerA)
        val etUpperB = findViewById<TextInputEditText>(R.id.etUpperB)

        val chipSample1 = findViewById<Chip>(R.id.chipSample1)
        val chipSample2 = findViewById<Chip>(R.id.chipSample2)
        val chipSample3 = findViewById<Chip>(R.id.chipSample3)

        val btnHelp = findViewById<MaterialButton>(R.id.btnHelp)
        val btnIntegrate = findViewById<MaterialButton>(R.id.btnIntegrate)
        val btnClear = findViewById<MaterialButton>(R.id.btnClear)
        val btnCopy = findViewById<MaterialButton>(R.id.btnCopy)

        val tvFunctionPreview = findViewById<TextView>(R.id.tvFunctionPreview)
        val tvIndefiniteResult = findViewById<TextView>(R.id.tvIndefiniteResult)
        val tvDefiniteResult = findViewById<TextView>(R.id.tvDefiniteResult)

        btnHelp.setOnClickListener { showHelpBottomSheet() }
        findViewById<MaterialButton>(R.id.btnMenu).setOnClickListener { anchor ->
            val menu = android.widget.PopupMenu(this, anchor)
            menu.menu.add("Derivative").setOnMenuItemClickListener {
                derivativeMode = true
                btnIntegrate.text = "ƒ′ Calculate"
                findViewById<TextView>(R.id.tvOperationLabel).text = "DERIVATIVE MODE"
                true
            }
            menu.menu.add("Integration").setOnMenuItemClickListener {
                derivativeMode = false
                btnIntegrate.text = "∫ Calculate"
                findViewById<TextView>(R.id.tvOperationLabel).text = "INTEGRATION MODE"
                true
            }
            menu.menu.add("About Maan Softwares").setOnMenuItemClickListener {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Maan Softwares")
                    .setMessage("Owner: Abdul Rehman\\nProduct: Calculus Studio 3D\\n© 2026 Abdul Rehman. All rights reserved.")
                    .setPositiveButton("Close", null).show()
                true
            }
            menu.show()
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("MAAN SOFTWARES")
            .setMessage("Calculus Studio 3D\\nBy Abdul Rehman")
            .setCancelable(true)
            .setPositiveButton("Get Started", null)
            .show()

        val functions = listOf(
            "sin(x)", "cos(x)", "tan(x)", "cot(x)", "cosec(x)", "sec(x)",
            "sec²(x)", "eˣ", "1/x", "1/√x"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, functions)
        actvFunction.setAdapter(adapter)
        actvFunction.setText(functions[0], false)
        actvFunction.setOnItemClickListener { _, _, position, _ ->
            selectedFuncIndex = position
        }

        toggleMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            if (checkedId == R.id.btnModePoly) {
                layoutPoly.visibility = View.VISIBLE
                layoutFunc.visibility = View.GONE
            } else {
                layoutPoly.visibility = View.GONE
                layoutFunc.visibility = View.VISIBLE
            }
        }

        chipSample1.setOnClickListener {
            etPolyCoef.setText("2, 3, 1")
            etLowerA.setText("0")
            etUpperB.setText("2")
        }
        chipSample2.setOnClickListener {
            etPolyCoef.setText("4, -2, 6")
            etLowerA.setText("1")
            etUpperB.setText("3")
        }
        chipSample3.setOnClickListener {
            etPolyCoef.setText("1, 0, 0, 4")
            etLowerA.setText("")
            etUpperB.setText("")
        }

        btnClear.setOnClickListener {
            etPolyCoef.setText("")
            etLowerA.setText("")
            etUpperB.setText("")
            tvFunctionPreview.text = "f(x) = —"
            tvIndefiniteResult.text = "∫ f(x) dx = Result yahan show hoga"
            tvDefiniteResult.text = "Definite [a, b]: Limits enter karein"
            tvDefiniteResult.setTextColor(0xFF94A3B8.toInt())
        }

        btnCopy.setOnClickListener {
            val combined = "${tvFunctionPreview.text}\n${tvIndefiniteResult.text}\n${tvDefiniteResult.text}"
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Integral Result", combined))
            Toast.makeText(this, "Result copied!", Toast.LENGTH_SHORT).show()
        }

        btnIntegrate.setOnClickListener {
            val aStr = etLowerA.text?.toString()?.trim().orEmpty()
            val bStr = etUpperB.text?.toString()?.trim().orEmpty()

            if (!derivativeMode && aStr.isNotEmpty() != bStr.isNotEmpty()) {
                tvDefiniteResult.text = "Definite integral ke liye 'a' aur 'b' dono enter karein."
                tvDefiniteResult.setTextColor(0xFFFCA5A5.toInt())
                return@setOnClickListener
            }

            val a = if (aStr.isNotEmpty()) aStr.toDoubleOrNull() else null
            val b = if (bStr.isNotEmpty()) bStr.toDoubleOrNull() else null

            if (!derivativeMode && aStr.isNotEmpty() && (a == null || b == null)) {
                tvDefiniteResult.text = "Ghalat limits! Valid numbers likhein."
                tvDefiniteResult.setTextColor(0xFFFCA5A5.toInt())
                return@setOnClickListener
            }

            val result = if (derivativeMode) {
                if (btnModePoly.isChecked) differentiatePolynomial(etPolyCoef.text?.toString().orEmpty())
                else differentiateFunction(selectedFuncIndex)
            } else if (btnModePoly.isChecked) {
                integratePolynomial(etPolyCoef.text?.toString().orEmpty(), a, b)
            } else {
                integrateFunction(selectedFuncIndex, a, b)
            }

            tvFunctionPreview.text = result.fxLine
            tvIndefiniteResult.text = result.indefiniteLine
            tvDefiniteResult.text = result.definiteLine
            tvDefiniteResult.setTextColor(
                if (result.isError) 0xFFFCA5A5.toInt()
                else if (result.hasDefinite) 0xFF34D399.toInt()
                else 0xFF94A3B8.toInt()
            )
        }
    }


    private fun differentiatePolynomial(rawInput: String): CalcResult {
        val parts = rawInput.split(",", " ", "\n").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.isEmpty() || parts.size > 51) return CalcResult("f(x) = Invalid input", "Enter 1 to 51 coefficients", "Example: 2, 3, 1", isError = true)
        val coef = parts.map { it.toDoubleOrNull() ?: return CalcResult("f(x) = Error", "Invalid coefficient: $it", "Use numbers only", isError = true) }
        val terms = (1 until coef.size).reversed().filter { coef[it] != 0.0 }
        val derivative = if (terms.isEmpty()) "0" else terms.mapIndexed { idx, power ->
            formatTerm(coef[power] * power, power - 1, idx == 0)
        }.joinToString("")
        val fx = (coef.indices.reversed().filter { coef[it] != 0.0 }.mapIndexed { idx, power ->
            formatTerm(coef[power], power, idx == 0)
        }.joinToString("").ifEmpty { "0" })
        return CalcResult("f(x) = $fx", "f′(x) = $derivative", "Power rule: d/dx (a·xⁿ) = n·a·xⁿ⁻¹")
    }

    private fun differentiateFunction(index: Int): CalcResult {
        val names = listOf("sin(x)", "cos(x)", "tan(x)", "cot(x)", "cosec(x)", "sec(x)", "sec²(x)", "eˣ", "1/x", "1/√x")
        val derivatives = listOf("cos(x)", "-sin(x)", "sec²(x)", "-cosec²(x)", "-cosec(x)cot(x)", "sec(x)tan(x)", "2sec²(x)tan(x)", "eˣ", "-1/x²", "-1/(2x^(3/2))")
        val idx = index.coerceIn(0, names.lastIndex)
        return CalcResult("f(x) = ${names[idx]}", "f′(x) = ${derivatives[idx]}", "Standard differentiation rule")
    }

    private fun showHelpBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_help, null)
        view.findViewById<MaterialButton>(R.id.btnCloseHelp).setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private data class CalcResult(
        val fxLine: String,
        val indefiniteLine: String,
        val definiteLine: String,
        val hasDefinite: Boolean = false,
        val isError: Boolean = false
    )

    private fun toSuperscript(n: Int): String {
        val map = mapOf(
            '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
            '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹', '-' to '⁻'
        )
        return n.toString().map { map[it] ?: it }.joinToString("")
    }

    private fun formatTerm(coef: Double, power: Int, isFirst: Boolean): String {
        val sign = when {
            isFirst && coef < 0 -> "-"
            isFirst -> ""
            coef < 0 -> " - "
            else -> " + "
        }
        val absVal = abs(coef)
        val numStr = if (absVal % 1.0 == 0.0) absVal.toLong().toString() else "%.4g".format(absVal)
        val xPart = when (power) {
            0 -> ""
            1 -> "x"
            else -> "x${toSuperscript(power)}"
        }
        val coefDisplay = if (power > 0 && absVal == 1.0) "" else numStr
        return "$sign$coefDisplay$xPart"
    }

    private fun integratePolynomial(rawInput: String, a: Double?, b: Double?): CalcResult {
        val parts = rawInput.split(",", " ", "\n").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.isEmpty() || parts.size > 51) {
            return CalcResult(
                fxLine = "f(x) = Invalid input",
                indefiniteLine = "1 se 51 tak coefficients likhein",
                definiteLine = "Example: 2, 3, 1",
                isError = true
            )
        }

        val coef = DoubleArray(parts.size)
        for (i in parts.indices) {
            coef[i] = parts[i].toDoubleOrNull() ?: return CalcResult(
                fxLine = "f(x) = Error",
                indefiniteLine = "Ghalat coefficient: ${parts[i]}",
                definiteLine = "Sirf numbers enter karein",
                isError = true
            )
        }

        val nonZeroFx = coef.indices.reversed().filter { coef[it] != 0.0 }
        val fxFormatted = if (nonZeroFx.isEmpty()) "0" else {
            nonZeroFx.mapIndexed { idx, power ->
                formatTerm(coef[power], power, idx == 0)
            }.joinToString("")
        }

        val intFormatted = if (nonZeroFx.isEmpty()) "C" else {
            nonZeroFx.mapIndexed { idx, power ->
                formatTerm(coef[power] / (power + 1), power + 1, idx == 0)
            }.joinToString("") + " + C"
        }

        if (a != null && b != null) {
            var sumA = 0.0
            var sumB = 0.0
            for (i in coef.indices) {
                if (coef[i] != 0.0) {
                    val c = coef[i] / (i + 1)
                    sumA += c * a.pow(i + 1)
                    sumB += c * b.pow(i + 1)
                }
            }
            return CalcResult(
                fxLine = "f(x) = $fxFormatted",
                indefiniteLine = "∫ f(x) dx = $intFormatted",
                definiteLine = "Definite [%g, %g] = %.6f".format(a, b, sumB - sumA),
                hasDefinite = true
            )
        }

        return CalcResult(
            fxLine = "f(x) = $fxFormatted",
            indefiniteLine = "∫ f(x) dx = $intFormatted",
            definiteLine = "Definite [a, b]: Optional limits enter karein"
        )
    }

    private fun integrateFunction(index: Int, a: Double?, b: Double?): CalcResult {
        data class FuncSpec(
            val name: String,
            val formula: String,
            val anti: (Double) -> Double?,
            val err: (Double) -> String?
        )

        val spec = when (index) {
            0 -> FuncSpec("sin(x)", "-cos(x) + C", { -cos(it) }, { null })
            1 -> FuncSpec("cos(x)", "sin(x) + C", { sin(it) }, { null })
            2 -> FuncSpec("tan(x)", "-ln|cos(x)| + C",
                { if (abs(cos(it)) < 1e-12) null else -ln(abs(cos(it))) },
                { if (abs(cos(it)) < 1e-12) "tan(x) undefined at x = %g".format(it) else null })
            3 -> FuncSpec("cot(x)", "ln|sin(x)| + C",
                { if (abs(sin(it)) < 1e-12) null else ln(abs(sin(it))) },
                { if (abs(sin(it)) < 1e-12) "cot(x) undefined at x = %g".format(it) else null })
            4 -> FuncSpec("cosec(x)", "ln|cosec(x) - cot(x)| + C",
                { if (abs(sin(it)) < 1e-12) null else ln(abs(1.0 / sin(it) - cos(it) / sin(it))) },
                { if (abs(sin(it)) < 1e-12) "cosec(x) undefined at x = %g".format(it) else null })
            5 -> FuncSpec("sec(x)", "ln|sec(x) + tan(x)| + C",
                { if (abs(cos(it)) < 1e-12) null else ln(abs(1.0 / cos(it) + sin(it) / cos(it))) },
                { if (abs(cos(it)) < 1e-12) "sec(x) undefined at x = %g".format(it) else null })
            6 -> FuncSpec(
                "sec²(x)",
                "tan(x) + C",
                { if (abs(cos(it)) < 1e-12) null else tan(it) },
                { if (abs(cos(it)) < 1e-12) "tan(x) undefined at x = %g".format(it) else null }
            )
            7 -> FuncSpec("eˣ", "eˣ + C", { exp(it) }, { null })
            8 -> FuncSpec(
                "1/x",
                "ln|x| + C",
                { if (it == 0.0) null else ln(abs(it)) },
                { if (it == 0.0) "1/x undefined at x = 0" else null }
            )
            9 -> FuncSpec(
                "1/√x",
                "2√x + C",
                { if (it < 0.0) null else 2.0 * sqrt(it) },
                { if (it < 0.0) "x >= 0 hona chahiye" else null }
            )
            else -> return CalcResult("f(x) = —", "Invalid selection", "", isError = true)
        }

        if (a != null && b != null) {
            val errMsg = spec.err(a) ?: spec.err(b)
            if (errMsg != null) {
                return CalcResult(
                    fxLine = "f(x) = ${spec.name}",
                    indefiniteLine = "∫ ${spec.name} dx = ${spec.formula}",
                    definiteLine = errMsg,
                    isError = true
                )
            }
            val fa = spec.anti(a)!!
            val fb = spec.anti(b)!!
            return CalcResult(
                fxLine = "f(x) = ${spec.name}",
                indefiniteLine = "∫ ${spec.name} dx = ${spec.formula}",
                definiteLine = "Definite [%g, %g] = %.6f".format(a, b, fb - fa),
                hasDefinite = true
            )
        }

        return CalcResult(
            fxLine = "f(x) = ${spec.name}",
            indefiniteLine = "∫ ${spec.name} dx = ${spec.formula}",
            definiteLine = "Definite [a, b]: Optional limits enter karein"
        )
    }
}
