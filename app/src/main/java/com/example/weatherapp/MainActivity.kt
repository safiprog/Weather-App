package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.myWeatherApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fatechWeatherData("mumbai")
       SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fatechWeatherData(p0)
                }
                searchView.setQuery("", false);
                searchView.clearFocus();

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                return true
            }
        })
    }


    private fun fatechWeatherData(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val resonse=retrofit.getWeatherData(cityName,"6c78c2561e16084e93808c1902c24a71","metric"
        )
        resonse.enqueue(object : Callback<myWeatherApi?> {
            override fun onResponse(call: Call<myWeatherApi?>, response: Response<myWeatherApi?>) {

//

                if (response.isSuccessful && response.body() !=null) {
                    val resbody=response.body()!!
                    val tem=resbody.main.temp.toString()
                       binding.temprature.text="$tem °C"
                    binding.maxTemp.text="Max ${resbody.main.temp_max} °C"
                    binding.minTemp.text="Min ${resbody.main.temp_min} °C"


                    val co=resbody.weather.firstOrNull()?.main?:"unknown"


                    binding.weather.text=co
                    binding.HumidityId.text=resbody.main.humidity.toString()
                    binding.windSpeedId.text=resbody.wind.speed.toString()

                    val sunraise=time(resbody.sys.sunrise.toLong())
                    val sunset=time(resbody.sys.sunset.toLong())
                    binding.sunriseId.text=sunraise

                    binding.sunsetId.text=sunset
                    binding.sealevelId.text=resbody.main.pressure.toString()
                    binding.cityName.text=cityName
                    binding.dayS.text=getDate(System.currentTimeMillis())
                    binding.dateS.text=dayName()

                    changeBGbaseonW(co)



                }else{
                    Toast.makeText(this@MainActivity, "this city is not found", Toast.LENGTH_SHORT).show()
                }

            }



            override fun onFailure(call: Call<myWeatherApi?>, t: Throwable) {
                Log.d("safif",t.message.toString())
            }
        })




    }

    private fun changeBGbaseonW(co: String) {

        when(co){
            "Haze","Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Sunny","Clear Sky","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

        }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun getDate(timestamp:Long): String {
        val k=SimpleDateFormat("EEEE", Locale.getDefault())
        return k.format((Date()))
    }
    private fun time(timestamp:Long): String {
        val k=SimpleDateFormat("HH:mm", Locale.getDefault())
        return k.format((Date(timestamp*1000)))
    }

    fun dayName():String{
        val k=SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return k.format(Date())
    }
}