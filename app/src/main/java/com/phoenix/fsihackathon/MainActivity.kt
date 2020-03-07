package com.phoenix.fsihackathon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.phoenix.fsihackathon.data.QuestionDataSource.getQuestions
import com.phoenix.fsihackathon.data.User
import com.phoenix.fsihackathon.data.UserDataSource.getData
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val allUserData = getData()
        val allQuestions = getQuestions()
        val invalidCodeErrorMessage = "The R code you entered is invalid"
        val cancelTransactionErrorMessage = "You cancelled the transaction"
        val insufficientFundErrorMessage = "Insufficient funds. Select another account"
        val invalidInput = "You entered an invalid input"
        val invalidTransferAmount = "Minimum transfer amount is N50"
        val invalidTransferAmount2 = "Maximum transfer amount is N150,000"
        val invalidOTPErrorMessage = "Invalid OTP."

        var currentUser: User? = User()
        var receivingUser: User = User()
        var currentUserBankIndex = 0
        var recipientBankIndex = 0
        var transferAmount: Double = 0.0
        val OTP = 123456

        tv_question.text = allQuestions[0]
        hideText()
        floatingActionButton.setOnClickListener {
            if (editText.text.isNullOrEmpty()) {
                return@setOnClickListener
            }
            hideText()
            when (tv_question.text) {
                allQuestions[0] -> {
                    currentUser = getCurrentUser(editText.toInt())
                    if (currentUser != null) {
                        tv_question.text = allQuestions[1]
                        clearText()
                    } else {
                        showToast(invalidCodeErrorMessage)
                        //tv_question.text = invalidCodeErrorMessage
                        clearText()
                    }
                }
                allQuestions[1] -> {
                    if (editText.toInt() == 1) {
                        tv_question.text = allQuestions[2]
                        clearText()
                    } else if (editText.toInt() == 2) {
                        tv_question.text = allQuestions[8]
                        val formattedBanks: StringBuilder = java.lang.StringBuilder()
                        for ((index, banks) in currentUser!!.bankDetails.bankName.withIndex()) {
                            formattedBanks.append("${index + 1}. $banks\n")
                        }
                        showText()
                        tv_user_details.text = formattedBanks
                        clearText()
                    } else {
                        showToast(invalidInput)
                        clearText()
                    }
                }
                allQuestions[2] -> {
                    // Inter-bank transfer to yourself
                    if (editText.toInt() == currentUser!!.rCode) {
                        receivingUser = currentUser as User
                        tv_question.text = allQuestions[3]
                    } else {
                        for (user in allUserData) {
                            if (editText.toInt() == user.rCode) {
                                receivingUser = user
                                break
                            } else {
                                showToast(invalidCodeErrorMessage)
                                clearText()
                                return@setOnClickListener
                            }
                        }
                        tv_question.text = allQuestions[3]
                    }
                    clearText()
                }
                allQuestions[3] -> {
                    transferAmount = editText.toInt().toDouble()
                    if (transferAmount < 50.0) {
                        showToast(invalidTransferAmount)
                        return@setOnClickListener
                    } else if (transferAmount <= 150000.0) {
                        tv_question.text = allQuestions[4]
                        val formattedBanks: StringBuilder = java.lang.StringBuilder()
                        for ((index, banks) in currentUser!!.bankDetails.bankName.withIndex()) {
                            formattedBanks.append("${index + 1}. $banks\n")
                        }
                        showText()
                        tv_user_details.text = formattedBanks
                        clearText()
                    } else {
                        showToast(invalidTransferAmount2)
                        return@setOnClickListener
                    }
                }
                allQuestions[4] -> {
                    val formattedBanks: StringBuilder = java.lang.StringBuilder()
                    for ((index, banks) in receivingUser.bankDetails.bankName.withIndex()) {
                        formattedBanks.append("${index + 1}. $banks\n")
                    }
                    showText()
                    tv_user_details.text = formattedBanks
                    if (editText.toInt() > currentUser!!.bankDetails.bankName.size) {
                        showToast(invalidInput)
                        clearText()
                        return@setOnClickListener
                    } else {
                        currentUserBankIndex = editText.toInt()
                        tv_question.text = allQuestions[5]
                        clearText()
                    }
                }
                allQuestions[5] -> {
                    if (editText.toInt() > receivingUser.bankDetails.bankName.size) {
                        showToast(invalidInput)
                        clearText()
                    } else {
                        showText()
                        recipientBankIndex = editText.toInt()
                        tv_question.text = allQuestions[6]
                        val message =
                            "You are about to transfer $transferAmount to ${receivingUser.name}"
                        tv_user_details.text = message
                        clearText()
                    }
                }
                allQuestions[6] -> {
                    if (editText.toInt() == OTP) {
                        showText()
                        tv_question.text = allQuestions[7]
                        val message =
                            "You have successfully transferred $transferAmount to ${receivingUser.name}\n1. Perform another transaction\n2. Exit"
                        tv_user_details.text = message
                        clearText()
                    } else {
                        showToast(invalidOTPErrorMessage)
                        clearText()
                        hideText()
                        return@setOnClickListener
                    }
                }
                allQuestions[7] -> {
                    if (editText.toInt() == 1) {
                        // Perform another transaction
                        tv_question.text = allQuestions[0]
                    } else if (editText.toInt() == 2) {
                        // Exit app
                        exitProcess(0)
                    } else {
                        showToast(invalidInput)
                        return@setOnClickListener
                    }
                }
                allQuestions[8] -> {
                    recipientBankIndex = editText.toInt()
                    tv_question.text =
                        currentUser!!.bankDetails.bankBalance[recipientBankIndex - 1].toString()
                    val message = "1. Perform another transaction\n2. Exit"
                    tv_user_details.text = message
                    if (editText.toInt() == 1) {
                        // Perform another transaction
                        tv_question.text = allQuestions[0]
                    } else if (editText.toInt() == 2) {
                        // Exit app
                        exitProcess(0)
                    }
                }
            }
        }
    }

    private fun getCurrentUser(rCode: Int): User? {
        for (user in getData()) {
            if (user.rCode == rCode) {
                return user
            }
        }
        return null
    }

    private fun EditText.toInt(): Int {
        return Integer.parseInt(this.text.toString())
    }

    private fun hideText() {
        tv_user_details.visibility = View.GONE
    }

    private fun showText() {
        tv_user_details.visibility = View.VISIBLE
    }

    private fun clearText() {
        editText.setText("")
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
