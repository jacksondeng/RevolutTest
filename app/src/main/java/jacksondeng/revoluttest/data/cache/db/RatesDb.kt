package jacksondeng.revoluttest.data.cache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import jacksondeng.revoluttest.data.cache.dao.RatesDao
import jacksondeng.revoluttest.model.entity.TempModel
import jacksondeng.revoluttest.util.DB_VERSION

@Database(entities = [TempModel::class], exportSchema = false, version = DB_VERSION)
abstract class RatesDb : RoomDatabase() {

    abstract fun ratesDao(): RatesDao
}