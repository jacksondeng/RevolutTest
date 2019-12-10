package jacksondeng.revoluttest.data.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import jacksondeng.revoluttest.data.cache.dao.RatesDao
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.TempModel
import jacksondeng.revoluttest.util.DB_VERSION
import jacksondeng.revoluttest.util.MapConverter

@TypeConverters(MapConverter::class)
@Database(
    entities = [TempModel::class, RatesDTO::class],
    exportSchema = false,
    version = DB_VERSION
)
abstract class RatesDb : RoomDatabase() {

    abstract fun ratesDao(): RatesDao
}