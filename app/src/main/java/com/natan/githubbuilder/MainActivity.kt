package com.natan.githubbuilder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.natan.githubbuilder.data.TokenStore
import com.natan.githubbuilder.ui.MainViewModel

class MainActivity:ComponentActivity(){
 private val picker=registerForActivityResult(ActivityResultContracts.OpenDocument()){uri->uri?.let{selected=it; selectedName=it.lastPathSegment ?: "projeto.zip"}}
 private var selected:android.net.Uri?=null; private var selectedName=""
 override fun onCreate(b:Bundle){super.onCreate(b);setContent{val vm:MainViewModel=viewModel(factory=object:androidx.lifecycle.ViewModelProvider.Factory{@Suppress("UNCHECKED_CAST")override fun<T:androidx.lifecycle.ViewModel>create(c:Class<T>)=MainViewModel(TokenStore(applicationContext)) as T});BuilderScreen(vm){picker.launch(arrayOf("application/zip","application/octet-stream"))}{selected?.let{contentResolver.openInputStream(it)?.let{stream->vm.uploadZip(vm.state.value.user,vm.state.value.repoName,stream)}}}}}
}

@Composable fun BuilderScreen(vm:MainViewModel,onPick:()->Unit,onUpload:()->Unit){val s by vm.state.collectAsStateWithLifecycle();var name by remember{mutableStateOf("meu-app-android")};var privateRepo by remember{mutableStateOf(true)};Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){Text("GitHub APK Builder",style=MaterialTheme.typography.headlineSmall);Text("Crie um repositório, envie um projeto ZIP e compile com GitHub Actions.");OutlinedTextField(s.token,{vm.tokenChanged(it)},label={Text("Token do GitHub")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth());Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick=vm::validate,enabled=!s.busy){Text("Validar token")};OutlinedButton(onClick=vm::clearToken){Text("Apagar")}};s.user.takeIf{it.isNotBlank()}?.let{Text("Conectado como @$it")};HorizontalDivider();OutlinedTextField(name,{name=it},label={Text("Nome do repositório")},modifier=Modifier.fillMaxWidth());Row{Checkbox(privateRepo,{privateRepo=it});Text("Repositório privado",Modifier.padding(top=12.dp))};Button(onClick={vm.create(name,privateRepo)},enabled=!s.busy&&s.user.isNotBlank(),modifier=Modifier.fillMaxWidth()){Text("Criar repositório")};if(s.repoUrl!=null){Button(onClick=onPick,enabled=!s.busy,modifier=Modifier.fillMaxWidth()){Text("Selecionar projeto ZIP")};Button(onClick=onUpload,enabled=!s.busy,modifier=Modifier.fillMaxWidth()){Text("Enviar e compilar")}};if(s.busy)LinearProgressIndicator(Modifier.fillMaxWidth());Text(s.message);s.repoUrl?.let{Text("Repositório criado: $it")};Text("O token fica protegido pelo Android Keystore e nunca é gravado no código.",style=MaterialTheme.typography.bodySmall)}}
