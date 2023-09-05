package com.example.sunnyday.entity

data class ComplexWeather (
    val success: String? = "true",
    val request: RequestParameters,
    val location: Location,
    val current: CurrentWeather
)

data class RequestParameters (
    val type: String?,
    val query: String?,
)

data class Location (
    val name: String?,
    val country: String?,
    val region: String?,
    val lat: Double?,
    val lon: Double?,
    val localtime: String?,
    val localtime_epoch: Long?,
)
data class CurrentWeather (
    val temperature: Int?,
    val weather_icons: List<String?>,
    val wind_speed: Int?,
    val wind_dir: String?,
    val humidity: Int?,
    val cloudcover: Int?,
)

/*
@JsonClass(generateAdapter = true)
data class ComplexWeather (
    @field:Json(name ="request") val requestParameters: RequestParameters,
    @field:Json(name ="location") val location: Location,
    @field:Json(name ="current") val currentWeather: CurrentWeather
        )

@JsonClass(generateAdapter = true)
data class RequestParameters (
    @field:Json(name ="type") val type: String,
    @field:Json(name ="query") val city: String,
)

@JsonClass(generateAdapter = true)
data class Location (
    @field:Json(name ="name") val nameCity: String,
    @field:Json(name ="country") val country: String,
    @field:Json(name ="region") val region: String,
    @field:Json(name ="lat") val latitude: Double,
    @field:Json(name ="lon") val longitude: Double,
    @field:Json(name ="localtime") val localTime: String,
    @field:Json(name ="localtime_epoch") val localTimeInMillis: Long,
    )
@JsonClass(generateAdapter = true)
data class CurrentWeather (
    @field:Json(name ="temperature") val temperature: Int,
    @field:Json(name ="weather_icons") val weatherIconUrl: String,
    @field:Json(name ="wind_speed") val windSpeed: Int,
    @field:Json(name ="wind_dir") val windDir: String,
    @field:Json(name ="humidity") val humidity: Int,
    @field:Json(name ="cloudcover") val cloudCover: Int,
)
{
    "request": {
        "type": "City",
        "query": "Новосибирск, Россия",
        "language": "en",
        "unit": "m"
    },
    "location": {
        "name": "Новосибирск",
        "country": "Россия",
        "region": "Novosibirsk",
        "lat": "55.041",
        "lon": "82.934",
        "timezone_id": "Asia/Novosibirsk",
        "localtime": "2023-05-31 16:09",
        "localtime_epoch": 1685549340,
        "utc_offset": "7.0"
    },
    "current": {
        "observation_time": "09:09 AM",
        "temperature": 25,
        "weather_code": 113,
        "weather_icons": [
            "https://cdn.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0001_sunny.png"
        ],
        "weather_descriptions": [
            "Sunny"
        ],
        "wind_speed": 15,
        "wind_degree": 30,
        "wind_dir": "NNE",
        "pressure": 1021,
        "precip": 0,
        "humidity": 17,
        "cloudcover": 0,
        "feelslike": 26,
        "uv_index": 5,
        "visibility": 10,
        "is_day": "yes"
    }
}
*/