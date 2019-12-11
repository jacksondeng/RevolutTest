package jacksondeng.revoluttest.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Observable
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.util.TABLE_NAME_RATES

@Dao
interface RatesDao {
    @Query("SELECT * FROM $TABLE_NAME_RATES WHERE base = :base")
    fun getRates(base: String): Flowable<RatesDTO>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCache(rates: RatesDTO): Long
}