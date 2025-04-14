package eu.tutorials.weather

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import eu.tutorials.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

//e59cf72081da9c78e0e10a7472913cd1
class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetweatherdata("Delhi")
        SearchCity()

    }

    private fun SearchCity() {
        val SearchView=binding.searchView
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetweatherdata(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true

            }

        })
    }

    private fun fetweatherdata(cityName:String) {
        val retrofit= Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(WeatherApi::class.java)
        val response=retrofit.getWeatherData(cityName,"e59cf72081da9c78e0e10a7472913cd1","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call:Call<WeatherApp>, response: Response<WeatherApp>){

                val responseBody = response.body()
                if (response.isSuccessful && (responseBody != null)) {

                    val temperature = responseBody.main.temp.toString()
                    binding.Temp.text="${temperature}°C"

                    val humidity = responseBody.main.humidity.toString()
                    binding.humidity.text = "${humidity}%"
                    val windSpeed = responseBody.wind.speed.toString()
                    binding.WindSpeed.text = "${windSpeed}m/s"
                    val sunRise = responseBody.sys.sunrise.toLong()
                    binding.SunRise.text = "${time(sunRise)}"
                    val sunSet = responseBody.sys.sunset.toLong()
                    binding.Sunset.text = "${time(sunSet)}"
                    val SeaLevel = responseBody.main.pressure.toString()
                    binding.sea.text = "${SeaLevel}hPa"
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    binding.Weather.text = condition
                    val MaxTemp = responseBody.main.temp_max.toString()
                    binding.maxTemp.text="${MaxTemp}°C"
                    val MinTemp = responseBody.main.temp_min.toString()
                    binding.minTemp.text="${MinTemp}°C"
                    val condition1 = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    Log.d("WeatherCondition", "Condition from API: $condition1")
                    binding.Weather.text = condition1
                    binding.Date.text =date()
                    binding.Day.text = dayName(System.currentTimeMillis())
                    binding.city.text = "${cityName}"
                    changeImageAccording(condition1)
                }
            }

            private fun changeImageAccording(condition1: String) {
                binding.main.background = null
                binding.lottieAnimationView.cancelAnimation()

                when (condition1.toLowerCase(Locale.ROOT)) {
                    "Clear Sky", "Sunny", "Clear" -> {
                        binding.main.setBackgroundResource(R.drawable.sunny)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }

                    "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                        binding.main.setBackgroundResource(R.drawable.cloudscreen)
                        binding.lottieAnimationView.setAnimation(R.raw.cloudsanimation)
                    }

                    "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                        binding.main.setBackgroundResource(R.drawable.rainscreen)
                        binding.lottieAnimationView.setAnimation(R.raw.rainanimantion)
                    }

                    "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                        binding.main.setBackgroundResource(R.drawable.snowscreen)
                        binding.lottieAnimationView.setAnimation(R.raw.snowanimation)
                    }
                    else -> {
                        binding.main.setBackgroundResource(R.drawable.sunny)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }
                }
                binding.main.invalidate()
                binding.lottieAnimationView.playAnimation()
            }




            override fun onFailure( call:Call<WeatherApp>,t: Throwable){
                Toast.makeText(this@MainActivity, "Failed to fetch data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })




    }
    fun dayName(timestamp:Long):String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    fun date():String{
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}