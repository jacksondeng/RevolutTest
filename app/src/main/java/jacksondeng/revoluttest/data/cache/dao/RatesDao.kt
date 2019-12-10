package jacksondeng.revoluttest.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jacksondeng.revoluttest.model.entity.TempModel

@Dao
interface RatesDao {
    @Query("SELECT * FROM TempTable")
    fun all(): List<TempModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(data: TempModel): Long
}