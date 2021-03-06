package com.uqcs.mobile.common

import com.uqcs.mobile.features.memberslist.MemberX
import com.uqcs.mobile.features.eventscalendar.EventX
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface Webserver {

    @GET("/members")
    fun fetchMembers() : Call<List<MemberX>>

    @GET("/events")
    fun fetchEvents() : Call<List<EventX>>

    @GET("/docs")
    fun fetchDocumentation() : Call<ResponseBody>

    @GET("sign_in")
    fun signIn() : Call<ResponseBody>

    @POST("/docs")
    fun updateDocumentation(
        @Field("file_name") fileName : String,
        @Field("file_data") fileData : String,
        @Field("commit_message") commitMessage : String
    )

    @POST("/signup")
    fun createMember(
        @Field("image") image : String
    ) : Call<ResponseBody>
}