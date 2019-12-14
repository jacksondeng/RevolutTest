package jacksondeng.revoluttest.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.util.TABLE_NAME_RATES

@Dao
interface RatesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCache(rates: RatesDTO): Completable

    @Query("SELECT * FROM $TABLE_NAME_RATES WHERE base = :base")
    fun getCachedRates(base: String): Single<RatesDTO>
}