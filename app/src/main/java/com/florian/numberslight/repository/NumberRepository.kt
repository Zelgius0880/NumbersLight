package com.florian.numberslight.repository

/*import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query*/
import com.florian.numberslight.model.INumber
import com.florian.numberslight.model.Number
import com.florian.numberslight.model.NumberSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


open class NumberRepository(private val logEnabled: Boolean = false) {
    companion object {
        const val SERVER_URL = "http://dev.tapptic.com/test/"
    }
   /* private var retrofit = Retrofit.Builder()
        .baseUrl(SERVER_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .apply {
                if (logEnabled) {
                    val logger = HttpLoggingInterceptor()
                    logger.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(logger)
                }
            }
            .build()
        )
        .build()*/

    // change your base URL
    private val adapter = RestAdapter.Builder()
        .setEndpoint(SERVER_URL) //Set the Root URL
        .build() //Finally building the adapter


    //Creating object for our interface
    private val serviceLower21 = adapter.create(NumberServiceLower21::class.java)
    //private val service: NumberService = retrofit.create(NumberService::class.java)

    suspend fun getList() =
        //if(BuildConfig.VERSION_CODE < 21) {
            suspendCoroutine<List<NumberSummary>?> { continuation ->
                serviceLower21.getList(object : Callback<List<NumberSummary>>{
                    override fun success(t: List<NumberSummary>?, response: Response?) {
                        continuation.resume(t)
                    }

                    override fun failure(error: RetrofitError?) {
                        continuation.resumeWithException(IOException(error?.message))
                    }

                })

        /*} else {

            service.getList().execute().body()
        }*/
    }

    suspend fun getNumber(number: INumber) = withContext(Dispatchers.IO) {
        //if(BuildConfig.VERSION_CODE < 21) {
            suspendCoroutine<Number?> { continuation ->
                serviceLower21.getNumber(number.name, object : Callback<Number>{
                    override fun success(t: Number?, response: Response?) {
                        continuation.resume(t)
                    }

                    override fun failure(error: RetrofitError?) {
                        continuation.resumeWithException(IOException(error?.message))
                    }

                })
            }
        /*} else {
            service.getNumber(number.name).execute().body()
        }*/
    }


/*    interface NumberService {
        @GET("json.php")
        fun getList(): Call<List<NumberSummary>>

        @GET("json.php")
        fun getNumber(
            @Query("name") name: String
        ): Call<Number>
    }*/


    interface NumberServiceLower21 {
        @retrofit.http.GET("/json.php")
        fun getList(callback: retrofit.Callback<List<NumberSummary>>)

        @retrofit.http.GET("/json.php")
        fun getNumber(
            @retrofit.http.Query("name") name: String,
            callback: retrofit.Callback<Number>
        )
    }
}