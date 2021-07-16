package com.github.oldr1990.ui.auth

import androidx.lifecycle.ViewModel
import com.github.oldr1990.repository.AuthRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private  val authRepository: AuthRepositoryInterface
): ViewModel() {


}