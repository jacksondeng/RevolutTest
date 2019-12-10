package jacksondeng.revoluttest.util

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class MapConverter {
    @TypeConverter
    fun fromString(value: String): Map<String, Double> {
        val jsonAdapter = Moshi.Builder().build().adapter<Map<String, Double>>(Any::class.java)
        return jsonAdapter.fromJson(value)!!
    }

    @TypeConverter
    fun fromStringMap(map: Map<String, Double>): String {
        val type = Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            Double::class.javaObjectType
        )
        val adapter: JsonAdapter<Map<String, Double>> = Moshi.Builder().build().adapter(type)
        return adapter.toJson(map).toString()
    }
}