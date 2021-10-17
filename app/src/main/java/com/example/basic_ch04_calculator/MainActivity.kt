package com.example.basic_ch04_calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Room
import com.example.basic_ch04_calculator.databinding.ActivityMainBinding
import com.example.basic_ch04_calculator.databinding.HistoryRowBinding
import com.example.basic_ch04_calculator.model.History
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var historyRowBinding: HistoryRowBinding

    private var isOperator = false    // 연산자를 추가하고 숫자를 추가하는 경우 띄어쓰기를 넣어줘야하기 때문에 판단을 위한 변수 선언
    private var hasOperator = false    // 연산자를 하나만 사용해야하기 때문에

    lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        historyRowBinding = HistoryRowBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()

    }

    // 일반 버튼들을 클릭할 경우
    fun buttonClicked(v:View) {
        when(v.id) {
            R.id.btn0 -> numberFunClicked("0")
            R.id.btn1 -> numberFunClicked("1")
            R.id.btn2 -> numberFunClicked("2")
            R.id.btn3 -> numberFunClicked("3")
            R.id.btn4 -> numberFunClicked("4")
            R.id.btn5 -> numberFunClicked("5")
            R.id.btn6 -> numberFunClicked("6")
            R.id.btn7 -> numberFunClicked("7")
            R.id.btn8 -> numberFunClicked("8")
            R.id.btn9 -> numberFunClicked("9")
            R.id.btnPlus -> operatorBtnClicked("+")
            R.id.btnMinus -> operatorBtnClicked("-")
            R.id.btnModulo -> operatorBtnClicked("%")
            R.id.btnMulti -> operatorBtnClicked("*")
            R.id.btnDiv -> operatorBtnClicked("/")

        }

    }

    // 숫자 버튼을 클릭하는 경우
    private fun numberFunClicked(number : String) {

        if (isOperator) {
            mainBinding.expressionTextView.append(" ")

        }

        isOperator = false

        // 숫자 연산자 숫자 형태로 입력하기 때문에 띄어쓰기로 구분
        var expressionText = mainBinding.expressionTextView.text.split(" ")

        if ( expressionText.isNotEmpty() && expressionText.last().length >= 15 ) {
            Toast.makeText(this,"15자리 까지만 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
            return  // 토스트 메세지 호출 후 리턴으로 함수 종료
        } else if ( number == "0" && expressionText.last().isEmpty() ) {
            Toast.makeText(this,"0은 제일 앞에 올 수 없습니다.",Toast.LENGTH_SHORT).show()
            return
        }

        mainBinding.expressionTextView.append(number)
        // TODO resultTextView 실시간으로 계산 결과를 넣어야 하는 기능
        mainBinding.resultTextView.text = calculateExpression()

    }

    private fun operatorBtnClicked(operator : String) {

        if (mainBinding.expressionTextView.text.isEmpty()) {
            return
        }

        // 연산자가 입력된 상황에서 다른 연산자 입력 시 해당 연산자로 그냥 교체해주는 코드와 이미 연산자가 있는 경우에 따른 코드
        when {
            isOperator -> {
                val text = mainBinding.expressionTextView.text.toString()
                mainBinding.expressionTextView.text = text.dropLast(1) + operator    // dropLast : 제일 뒤에서 한자리 지우고 반환
            }

            hasOperator -> {
                Toast.makeText(this,"연산자는 한 번만 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                return
            }

            else -> {
                mainBinding.expressionTextView.append(" $operator")    // 숫자가 입력된 다음 상황이고 연산자가 처음 들어가는 상황이니깐 띄어쓰기와 연산자를 포함해서 추가
            }
        }

        // 연산자를 입력할 경우 연산자의 text Color를 초록색으로 변경 후 다시 적용
        val ssb = SpannableStringBuilder(mainBinding.expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            mainBinding.expressionTextView.text.length - 1,
            mainBinding.expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        mainBinding.expressionTextView.text = ssb

        // 연산자를 추가했으므로 둘 다 true로 변경
        isOperator = true
        hasOperator = true

    }


    // 모두 초기화
    fun clearButtonClicked(v: View) {

        mainBinding.expressionTextView.text = ""
        mainBinding.resultTextView.text = ""
        isOperator = false
        hasOperator = false

    }

    fun resultButtonClicked(v: View) {

        val expressionTexts = mainBinding.expressionTextView.text.split(" ")

        if (mainBinding.expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        if (expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this,"아직 완성되지 않은 수식입니다.",Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this,"오류가 발생했습니다..",Toast.LENGTH_SHORT).show()
            return
        }



        val expressionText = mainBinding.expressionTextView.text.toString()
        val resultText = calculateExpression()

        // TODO 디비에 넣어주는 부분 , 디비와 관련된 과정은 새로운 스레드에서 진행
        Thread(Runnable {
            db.historyDao().insertHistory(History(null, expressionText, resultText))
        }).start()

        // 계산된 결과는 비워주고 해당 결과를 다음 계산을 위한 계산칸으로 이동
        mainBinding.resultTextView.text = ""
        mainBinding.expressionTextView.text = resultText

        isOperator = false
        hasOperator = false


    }

    private fun calculateExpression() : String {

        val expressionText = mainBinding.expressionTextView.text.split(" ")

        // 연산자가 없거나 입력 값들이 3묶음이 안나올 경우
        if (hasOperator.not() || expressionText.size != 3) {
            return ""
        } else if (expressionText[0].isNumber().not() || expressionText[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionText[0].toBigInteger()
        val exp2 = expressionText[2].toBigInteger()
        val op = expressionText[1]

        return when(op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""

        }
    }

    fun historyButtonClicked(v: View) {

        mainBinding.historyLayout.isVisible = true
        mainBinding.historyLinearLayout.removeAllViews()    // 레이아웃 아래 있는 모든 뷰 삭제

        // TODO 디비에서 모든 기록 가져오기
        // TODO 뷰에 모든 기록 할당하기
        Thread(Runnable {

            db.historyDao().getAll().reversed().forEach {

                runOnUiThread {
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null, false)

                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = "=${it.result}"

                    mainBinding.historyLinearLayout.addView(historyView)

                }

            }

        }).start()


    }

    fun historyClearClicked(v: View) {
        // TODO 디비에서 모든 기록 삭제
        // TODO 뷰에서 모든 기록 삭제

        mainBinding.historyLinearLayout.removeAllViews()    // 뷰에서 모든 기록 삭제
        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()

    }

    fun closeHistoryClicked(v: View) {
        mainBinding.historyLayout.isVisible = false
    }




    // 숫자인지를 판단하는 String 확장 함수
    fun String.isNumber() : Boolean {
        return try {
            this.toBigInteger()
            true
        } catch (e:NumberFormatException) {
            false
        }
    }
}