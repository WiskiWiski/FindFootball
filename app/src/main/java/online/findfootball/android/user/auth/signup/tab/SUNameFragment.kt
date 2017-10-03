package online.findfootball.android.user.auth.signup.tab

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import online.findfootball.android.R
import online.findfootball.android.app.App
import online.findfootball.android.user.auth.AuthUserObj

/**
 * Created by WiskiW on 13.09.2017.
 */
class SUNameFragment : BaseSUFragment() {

    companion object {
        private val TAG = App.G_TAG + ":SUNameFrg"

        private val MINIMAL_NAME_LENGTH = 3
    }

    private var nameEditText: EditText? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.su_fragment_name, container, false)
        nameEditText = rootView.findViewById(R.id.edit_text_name)
        if (nameEditText != null) {
            nameEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    getParent()?.onDataStateChange(verifyData(false))
                }

                override fun afterTextChanged(editable: Editable) {

                }
            })


        }
        return rootView
    }

    override fun saveResult(o: Any) {
        if (nameEditText != null) {
            o as AuthUserObj
            val nameStr = nameEditText!!.text.toString()
            if (!nameStr.isEmpty()) {
                o.displayName = nameStr
            }
        }

    }

    override fun updateView(o: Any) {
        if (nameEditText != null) {
            o as AuthUserObj
            nameEditText!!.setText(o.displayName)
        }
    }

    override fun verifyData(notifyUser: Boolean): Boolean {
        if (nameEditText == null) {
            return false
        }
        val nameStr = nameEditText?.text.toString()
        if (nameStr.length < MINIMAL_NAME_LENGTH) {
            if (notifyUser) {
                Toast.makeText(context, getString(R.string.su_name_frg_too_short),
                        Toast.LENGTH_SHORT).show()
                vibrate()
            }
        } else if (nameStr.isEmpty()) {
            if (notifyUser) {
                Toast.makeText(context, getString(R.string.su_name_frg_empty_name),
                        Toast.LENGTH_SHORT).show()
                vibrate()
            }
        } else {
            return true
        }
        return false
    }

}