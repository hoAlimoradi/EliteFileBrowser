package com.alimoradi.elitefilebrowser.settings_authentication

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.authentication.AuthenticationModule
import com.alimoradi.elitefilebrowser.main.ApplicationGraph

class SettingsAuthenticationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr),
    SettingsAuthenticationContract.Screen {

    private val view =
        LayoutInflater.from(context).inflate(R.layout.view_settings_authentication, this)

    private val authenticationRow: View = view.findViewById(R.id.view_settings_authentication_row)
    private val authenticationSection: CardView =
        view.findViewById(R.id.view_settings_authentication_section)
    private val authenticationCheckBox: CheckBox =
        view.findViewById(R.id.view_settings_authentication)

    private val authenticationSectionLabel: TextView =
        view.findViewById(R.id.view_settings_authentication_section_label)
    private val authenticationLabel: TextView =
        view.findViewById(R.id.view_settings_authentication_label)
    private val authenticationSubLabel: TextView =
        view.findViewById(R.id.view_settings_authentication_sublabel)

    private val userAction = createUserAction()

    init {
        orientation = LinearLayout.VERTICAL
        authenticationCheckBox.isChecked =
            AuthenticationModule(context).createAuthenticationManager().isAuthenticationEnable()
        authenticationRow.setOnClickListener {
            val isChecked = !authenticationCheckBox.isChecked
            authenticationCheckBox.isChecked = isChecked
            userAction.onAuthenticationCheckBoxCheckedChanged(isChecked)
        }
        authenticationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            userAction.onAuthenticationCheckBoxCheckedChanged(isChecked)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        userAction.onAttached()
    }

    override fun onDetachedFromWindow() {
        userAction.onDetached()
        super.onDetachedFromWindow()
    }


    private fun createUserAction(): SettingsAuthenticationContract.UserAction = if (isInEditMode) {
        object : SettingsAuthenticationContract.UserAction {
            override fun onAttached() {}
            override fun onDetached() {}
            override fun onAuthenticationCheckBoxCheckedChanged(isChecked: Boolean) {}
        }
    } else {
        val authenticationManager = ApplicationGraph.getAuthenticationManager()

        val themeManager = ApplicationGraph.getThemeManager()
        SettingsAuthenticationPresenter(
            this,
            authenticationManager,
            themeManager
        )
    }

    override fun setAuthenticationCheckBox(checked: Boolean) {
        authenticationCheckBox.isChecked = checked
    }

    override fun setSectionColor(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        authenticationSection.setCardBackgroundColor(color)
    }

    override fun setTextPrimaryColorRes(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        authenticationLabel.setTextColor(color)
        authenticationCheckBox.setTextColor(color)
    }

    override fun setTextSecondaryColorRes(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        authenticationSectionLabel.setTextColor(color)
        authenticationSubLabel.setTextColor(color)
    }
}
