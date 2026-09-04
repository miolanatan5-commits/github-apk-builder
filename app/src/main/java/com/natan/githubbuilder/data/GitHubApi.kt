package com.natan.githubbuilder.data
import retrofit2.http.*
data class DispatchRequest(val ref:String="main")
data class User(val login:String)
data class RepoRequest(val name:String,val description:String,val private:Boolean)
data class Repo(val full_name:String,val html_url:String)
data class ContentRequest(val message:String,val content:String,val branch:String="main")
data class WorkflowRun(val id:Long,val status:String,val conclusion:String?)
interface GitHubApi { @GET("user") suspend fun user():User; @POST("user/repos") suspend fun createRepo(@Body request:RepoRequest):Repo; @PUT("repos/{owner}/{repo}/contents/{path}") suspend fun putContent(@Path("owner") owner:String,@Path("repo") repo:String,@Path("path") path:String,@Body body:ContentRequest):Any; @GET("repos/{owner}/{repo}/actions/runs") suspend fun runs(@Path("owner") owner:String,@Path("repo") repo:String):RunResponse; @POST("repos/{owner}/{repo}/actions/workflows/{workflow}/dispatches") suspend fun dispatch(@Path("owner") owner:String,@Path("repo") repo:String,@Path("workflow") workflow:String,@Body body:DispatchRequest):retrofit2.Response<Unit> }
data class RunResponse(val workflow_runs:List<WorkflowRun>)
