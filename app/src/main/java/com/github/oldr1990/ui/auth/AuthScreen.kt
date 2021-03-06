package com.github.oldr1990.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.oldr1990.data.Constants
import com.github.oldr1990.data.Constants.ERROR_INVALID_EMAIL
import com.github.oldr1990.data.Constants.ERROR_INVALID_PASSWORD
import com.github.oldr1990.data.Constants.LOG_TAG
import com.github.oldr1990.model.UserEntries
import com.github.oldr1990.ui.composes.CircleProgressBar
import com.github.oldr1990.util.isValidEmail
import com.github.oldr1990.util.isValidPassword


@Composable
fun AuthScreen(navigateToHome: ()->Unit, authViewModel: AuthViewModel ) {
    Log.i(Constants.LOG_TAG, "auth recomposition")
    val eventHandler = authViewModel.authEvent.collectAsState()
    val email: String by authViewModel.savedEmail.collectAsState()
    val password: String by authViewModel.savedPassword.collectAsState()
    val loading = rememberSaveable { mutableStateOf(false) } //loading circle state

    val emailLambda: (String) -> Unit = { it -> authViewModel.onEmailChanged(it) }
    val passwordLambda: (String) -> Unit = { it -> authViewModel.onPasswordChanged(it) }

    val registerClickListener: () -> Unit = {
        if (email.isValidEmail()) {
            if (password.isValidPassword()) {
                authViewModel.register(
                    UserEntries(
                        email = email.trim(),
                        password = password.trim(),
                    )
                )
            }
        }
    }
    val loginClickListener: () -> Unit = {
        if (email.isValidEmail()) {
            if (password.isValidPassword()) {
                Log.e("!@#", "here call viewModel.login")
                authViewModel.login(
                    UserEntries(
                        email = email,
                        password = password,
                    )
                )
            } else Log.e("!@#", ERROR_INVALID_PASSWORD)
        } else {
            Log.e("!@#", ERROR_INVALID_EMAIL)
        }
    }

    eventHandler.value.let { response ->
        if (!authViewModel.isEventHandled) {
            authViewModel.isEventHandled = true
            when (response) {
                AuthViewModel.AuthEvent.Empty -> {
                    Log.i(LOG_TAG, "Empty")
                    loading.value = false
                }
                is AuthViewModel.AuthEvent.Error -> {
                    loading.value = false
                    Toast.makeText(LocalContext.current, response.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.i(LOG_TAG, response.message)
                }
                is AuthViewModel.AuthEvent.Success -> {//navigate to list of sensors
                    Log.i(LOG_TAG, "Auth Success")
                    Toast.makeText(LocalContext.current, "Success!", Toast.LENGTH_SHORT).show()
                    loading.value = false
                    navigateToHome()
                }
                AuthViewModel.AuthEvent.WrongEmail -> {
                    loading.value = false
                    Toast.makeText(LocalContext.current, ERROR_INVALID_EMAIL, Toast.LENGTH_SHORT)
                        .show()
                    Log.i(LOG_TAG, ERROR_INVALID_EMAIL)
                }
                AuthViewModel.AuthEvent.WrongPassword -> {
                    loading.value = false
                    Toast.makeText(LocalContext.current, ERROR_INVALID_PASSWORD, Toast.LENGTH_SHORT)
                        .show()
                    Log.i(LOG_TAG, ERROR_INVALID_PASSWORD)
                }
                AuthViewModel.AuthEvent.Loading -> {
                    loading.value = true
                }
            }
        }
    }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(1f)
        ) {
            Text(text = "Place for Icon", fontSize = 48.sp, color = Color.Red)
            //  Image(
            //    painter = painterResource(id = R.drawable.wealthy_notepad_icon),
            //  contentDescription = "Icon"
            // )
            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(15.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = 5.dp,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(15.dp)
                ) {
                    TopLabel(Constants.LOGIN_REGISTER_LABEL)
                    RegisterData(Constants.EMAIL_LABEL, text = email, emailLambda)
                    RegisterData(Constants.PASSWORD_LABEL, text = password, passwordLambda)
                    Row(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ButtonLogReg(label = Constants.LOGIN_LABEL, loginClickListener)
                        Spacer(modifier = Modifier.padding(15.dp))
                        ButtonLogReg(label = Constants.REGISTRATION_LABEL, registerClickListener)
                    }
                }

            }
        }
        CircleProgressBar(state = loading)
    }
}

@Composable
fun TopLabel(text: String) {
    Text(text, fontSize = 24.sp)
}

@Composable
fun RegisterData(label: String, text: String, typeObserver: (String) -> Unit) {
    val transformation: VisualTransformation =
        if (label == Constants.PASSWORD_LABEL) PasswordVisualTransformation()
        else VisualTransformation.None
    OutlinedTextField(
        value = text,
        onValueChange = typeObserver,
        singleLine = true,
        label = { Text(text = label.trim()) },
        visualTransformation = transformation,
    )
}

@Composable
fun ButtonLogReg(label: String, onClickHandler: () -> Unit) {
    Button(onClick = onClickHandler) {
        Text(text = label)
    }
}
