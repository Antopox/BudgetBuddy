
import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class DecimalKeyListener : View.OnKeyListener {
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        val editText = v as EditText
        if (event?.action == KeyEvent.ACTION_DOWN && keyCode != KeyEvent.KEYCODE_DEL) {
            val cursorPosition = editText.selectionStart
            val text = editText.text.toString()
            if (text.contains(".")) {
                val decimalIndex = text.indexOf(".")
                val charactersAfterDecimal = text.substring(decimalIndex + 1).length
                if (charactersAfterDecimal >= 2 && cursorPosition > decimalIndex) {
                    return true // Evitar que se agregue más caracteres
                }
            }
        }
        return false
    }
}

fun EditText.setupDecimalKeyListener() {
    this.setOnKeyListener(DecimalKeyListener())
}