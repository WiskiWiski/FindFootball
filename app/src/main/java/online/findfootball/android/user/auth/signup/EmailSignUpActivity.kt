package online.findfootball.android.user.auth.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import online.findfootball.android.R
import online.findfootball.android.app.App
import online.findfootball.android.app.BaseActivity
import online.findfootball.android.app.view.verify.view.pager.VerifyTabViewPager
import online.findfootball.android.app.view.verify.view.pager.VerifyTabsParent
import online.findfootball.android.app.view.verify.view.pager.VerifycapableTab
import online.findfootball.android.user.auth.AuthUserObj
import online.findfootball.android.user.auth.signup.tab.SUEmailFragment
import online.findfootball.android.user.auth.signup.tab.SUNameFragment
import online.findfootball.android.user.auth.signup.tab.SUPasswordFragment
import online.findfootball.android.user.auth.signup.tab.SUSexFragment


class EmailSignUpActivity : BaseActivity(), VerifyTabsParent {

    companion object {
        private val TAG = App.G_TAG + ":EmailSignUpAct"

        @JvmField
        val EXTRA_KEY = "intent_sign_up_user_key"
        @JvmField
        val REQUEST_CODE = 3188
        private val ENABLE_BUTTON_VIEW_ALPHA = 1F
        private val DISABLE_BUTTON_VIEW_ALPHA = 0.2F
    }


    private var btnLeft: FloatingActionButton? = null
    private var btnRight: FloatingActionButton? = null
    private var viewPager: VerifyTabViewPager? = null

    private var authUser: AuthUserObj = AuthUserObj()

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_sign_up)
        initToolbar()

        authUser = intent.getParcelableExtra(EXTRA_KEY)

        viewPager = findViewById(R.id.pager) as VerifyTabViewPager
        if (viewPager != null) {
            val adapter = SUPageAdapter(supportFragmentManager)
            // add tabs here
            adapter.addNext(SUEmailFragment())
            adapter.addNext(SUPasswordFragment())
            adapter.addNext(SUNameFragment())
            adapter.addNext(SUSexFragment())
            viewPager!!.adapter = adapter
            viewPager!!.setParent(this)
            adapter.getItem(0)?.updateView(authUser)
        }

        btnLeft = findViewById(R.id.fab_left) as FloatingActionButton
        btnRight = findViewById(R.id.fab_right) as FloatingActionButton

        btnLeft!!.setOnClickListener { tryLeftSwipe() }
        btnRight!!.setOnClickListener { tryRightSwipe() }

    }

    private fun onFinish() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_progress, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(getString(R.string.su_fragment_dialog_title))
        dialogBuilder.setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(authUser.email, authUser.password)
                .addOnCompleteListener { task ->
                    dialog.hide()
                    if (task.isSuccessful) {
                        authUser.uid = task.result.user.uid
                        task.result.user.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    Log.d(TAG, "Successful sent verification: " + task.isSuccessful)
                                    Toast.makeText(this, getString(R.string.su_fragment_check_email),
                                            Toast.LENGTH_SHORT).show()
                                    val resultIntent = Intent()
                                    resultIntent.putExtra(EXTRA_KEY, authUser)
                                    setResult(REQUEST_CODE, resultIntent)
                                    finish()
                                }
                    } else {
                        val errMsg = getString(R.string.su_fragment_registration_failed)
                        val extMsg: String? = task.exception?.message
                        if (extMsg != null) {
                            errMsg.replace("@error@", extMsg, true)
                        }
                        Log.d(TAG, errMsg)
                        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        updateButtonViews()
    }

    private fun updateButtonViews() {
        viewPager ?: return
        val correct = viewPager?.currentTab?.verifyData(false) ?: true
        setBtnEnable(btnRight, correct)
        if (!viewPager!!.hasNext()) {
            // последний таб
            setBtnEnable(btnLeft, true)
            btnRight?.setImageResource(R.drawable.ic_check)
        } else if (!viewPager!!.hasPreview()) {
            // первый таб
            setBtnEnable(btnLeft, false)
            btnRight!!.setImageResource(R.drawable.ic_arrow_right)
        } else {
            setBtnEnable(btnLeft, true)
            btnRight!!.setImageResource(R.drawable.ic_arrow_right)
        }
    }

    private fun setBtnEnable(btn: FloatingActionButton?, value: Boolean) {
        if (btn == null) return
        btn.alpha = if (value) ENABLE_BUTTON_VIEW_ALPHA else DISABLE_BUTTON_VIEW_ALPHA
        btn.isEnabled = value
    }

    override fun onBackPressed() {
        if (!tryLeftSwipe()) {
            super.onBackPressed()
        }
    }

    override fun onRightSwipe() {
        if (viewPager!!.hasPreview()) {
            setBtnEnable(btnLeft, true)
        }
        setBtnEnable(btnRight, viewPager!!.currentTab.verifyData(false))
        if (!viewPager!!.hasNext()) {
            // последний таб
            btnRight!!.setImageResource(R.drawable.ic_check)
        } else {
            // не устанавливаем ic_arrow_right, т к она стои по умолчанию
            // btnRight.setImageResource(R.drawable.ic_arrow_right);
        }
    }

    override fun onLeftSwipe() {
        if (!viewPager!!.hasPreview()) {
            // первый таб
            setBtnEnable(btnLeft, false)
        }
        var correct = true
        if (viewPager != null) {
            correct = viewPager!!.currentTab.verifyData(false)
        }
        setBtnEnable(btnRight, correct)
        btnRight!!.setImageResource(R.drawable.ic_arrow_right)
    }

    override fun saveTabData(tab: VerifycapableTab) = tab.saveResult(authUser)

    override fun onDataStateChange(correct: Boolean) = setBtnEnable(btnRight, correct)

    override fun tryLeftSwipe(): Boolean {
        if (viewPager?.hasPreview() != true) {
            return false
        }
        // сохраняем данных с предыдущего таба
        saveTabData(viewPager!!.currentTab)

        viewPager!!.goBack()
        // обновляем данные в следующем табе
        viewPager!!.currentTab.updateView(authUser)
        //onPreviewTab();
        return true
    }

    override fun tryRightSwipe(): Boolean {
        // Возвращает был ли сменен таб
        if (viewPager?.currentTab?.verifyData(true) != false) {
            // сохраняем данных с таба
            viewPager?.currentTab?.let { saveTabData(it) }
            if (viewPager?.hasNext() == true) {
                viewPager?.goNext()
                viewPager?.currentTab?.updateView(authUser)
                return true
            } else {
                onFinish()
                return false
            }
        }
        return false
    }

}
