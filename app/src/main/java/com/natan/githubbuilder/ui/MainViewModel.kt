package com.natan.githubbuilder.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.natan.githubbuilder.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import android.util.Base64

data class UiState(val token:String="",val user:String="",val message:String="",val busy:Boolean=false,val repoUrl:String?=null)
class MainViewModel(private val store:TokenStore):ViewModel(){ private val _state=MutableStateFlow(UiState(token=store.get())); val state=_state.asStateFlow(); private var api:GitHubApi?=null
 private fun client(token:String)=OkHttpClient.Builder().addInterceptor{ chain -> chain.proceed(chain.request().newBuilder().addHeader("Authorization","Bearer $token").addHeader("Accept","application/vnd.github+json").build()) }.build()
 private fun api(token:String)=Retrofit.Builder().baseUrl("https://api.github.com/").client(client(token)).addConverterFactory(MoshiConverterFactory.create()).build().create(GitHubApi::class.java)
 fun validate(){ val t=state.value.token.trim(); if(t.isBlank()){_state.value=state.value.copy(message="Informe um token.");return}; viewModelScope.launch{_state.value=state.value.copy(busy=true,message="Validando token...");runCatching{api(t).user()}.onSuccess{store.save(t);this@MainViewModel.api=api(t);_state.value=state.value.copy(user=it.login,message="Token válido.",busy=false)}.onFailure{_state.value=state.value.copy(message="Token inválido ou sem acesso.",busy=false)}} }
 fun create(name:String,privateRepo:Boolean){ val a=api?:run{_state.value=state.value.copy(message="Valide o token primeiro.");return}; viewModelScope.launch{_state.value=state.value.copy(busy=true,message="Criando repositório...");runCatching{val r=a.createRepo(RepoRequest(name,"Repository created by GitHub APK Builder",privateRepo));r}.onSuccess{_state.value=state.value.copy(repoUrl=it.html_url,message="Repositório criado. Agora envie o projeto e inicie o workflow no GitHub.",busy=false)}.onFailure{_state.value=state.value.copy(message="Não foi possível criar o repositório.",busy=false)}} }
 fun tokenChanged(v:String){_state.value=state.value.copy(token=v)}; fun clearToken(){store.clear();api=null;_state.value=UiState(message="Token removido do dispositivo.")}
}
