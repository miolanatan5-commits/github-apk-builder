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
import java.io.InputStream
import java.util.zip.ZipInputStream

data class UiState(val token:String="",val user:String="",val message:String="",val busy:Boolean=false,val repoUrl:String?=null,val repoName:String="")
class MainViewModel(private val store:TokenStore):ViewModel(){ private val _state=MutableStateFlow(UiState(token=store.get())); val state=_state.asStateFlow(); private var api:GitHubApi?=null
 private fun client(token:String)=OkHttpClient.Builder().addInterceptor{ chain -> chain.proceed(chain.request().newBuilder().addHeader("Authorization","Bearer $token").addHeader("Accept","application/vnd.github+json").build()) }.build()
 private fun api(token:String)=Retrofit.Builder().baseUrl("https://api.github.com/").client(client(token)).addConverterFactory(MoshiConverterFactory.create()).build().create(GitHubApi::class.java)
 fun validate(){ val t=state.value.token.trim(); if(t.isBlank()){_state.value=state.value.copy(message="Informe um token.");return}; viewModelScope.launch{_state.value=state.value.copy(busy=true,message="Validando token...");runCatching{api(t).user()}.onSuccess{store.save(t);this@MainViewModel.api=api(t);_state.value=state.value.copy(user=it.login,message="Token válido.",busy=false)}.onFailure{_state.value=state.value.copy(message="Token inválido ou sem acesso.",busy=false)}} }
 fun create(name:String,privateRepo:Boolean){ val a=api?:run{_state.value=state.value.copy(message="Valide o token primeiro.");return}; viewModelScope.launch{_state.value=state.value.copy(busy=true,message="Criando repositório...");runCatching{val r=a.createRepo(RepoRequest(name,"Repository created by GitHub APK Builder",privateRepo));r}.onSuccess{_state.value=state.value.copy(repoUrl=it.html_url,repoName=name,message="Repositório criado. Agora selecione o projeto ZIP.",busy=false)}.onFailure{_state.value=state.value.copy(message="Não foi possível criar o repositório.",busy=false)}} }
 fun uploadZip(owner:String, repo:String, input:InputStream)=viewModelScope.launch{val a=api?:return@launch;_state.value=state.value.copy(busy=true,message="Enviando arquivos...");runCatching{ZipInputStream(input).use{z->var e=z.nextEntry;while(e!=null){if(!e.isDirectory&&!e.name.startsWith("__MACOSX")){val bytes=z.readBytes();a.putContent(owner,repo,e.name,ContentRequest("Add ${e.name}",Base64.encodeToString(bytes,Base64.NO_WRAP)))};e=z.nextEntry}};a.dispatch(owner,repo,"android.yml",DispatchRequest())}.onSuccess{_state.value=state.value.copy(message="Projeto enviado e compilação iniciada no GitHub Actions.",busy=false)}.onFailure{_state.value=state.value.copy(message="Falha ao enviar o projeto. Verifique o ZIP e as permissões do token.",busy=false)}}
 fun tokenChanged(v:String){_state.value=state.value.copy(token=v)}; fun clearToken(){store.clear();api=null;_state.value=UiState(message="Token removido do dispositivo.")}
}
