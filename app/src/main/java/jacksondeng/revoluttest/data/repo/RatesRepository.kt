package jacksondeng.revoluttest.data.repo

import android.content.SharedPreferences
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.dao.RatesDao
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.BASE_THUMBNAIL_URL
import jacksondeng.revoluttest.util.TAG_LAST_CACHED_TIME
import org.joda.time.Interval
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface RatesRepository {
    fun pollRates(base: String, multiplier: Double = 1.0): Flowable<Rates>
    fun getCachedRates(base: String, multiplier: Double = 1.0): Single<Rates>
}

class RatesRepositoryImpl @Inject constructor(
    private val api: RatesApi,
    private val ratesDao: RatesDao,
    private val sharePref: SharedPreferences
) :
    RatesRepository {

    override fun pollRates(
        base: String,
        multiplier: Double
    ): Flowable<Rates> {
        // Prevent overlapping requests
        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
        return (
                Flowable.interval(1, TimeUnit.SECONDS)
                    .flatMap {
                        api.pollRates(base).retry(2)
                            // Keep the stream alive by returning cache when api failed
                            .onErrorResumeNext(
                                ratesDao.getRates(base)
                            )
                    }
                    .subscribeOn(scheduler)
                    .doOnNext {
                        cacheRate(it)
                    }
                    .distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        Flowable.just(mapToModel(it, multiplier))
                    })
    }

    override fun getCachedRates(base: String, multiplier: Double): Single<Rates> {
        return ratesDao.getRates(base)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .firstOrError()
            .flatMap {
                Single.just(mapToModel(it, multiplier))
            }
    }

    private fun mapToModel(dto: RatesDTO, multiplier: Double): Rates {
        return Rates(dto.base, generateCurrencies(dto.base, multiplier, dto.rates))
    }

    private fun getImageUrl(countryCode: String): String {
        return "$BASE_THUMBNAIL_URL${countryCode.toLowerCase(Locale.US)}.png"
    }

    fun generateCurrencies(
        base: String,
        multiplier: Double,
        rates: Map<String, Double>
    ): List<CurrencyModel> {
        return mutableListOf<CurrencyModel>().apply {
            // Append an item as the queried item
            this.add(
                CurrencyModel(
                    currency = Currency.getInstance(base),
                    rate = multiplier,
                    imageUrl = getImageUrl(base)
                )
            )

            rates.map {
                // Only consider the rate is valid if it is >= 0
                if (it.value >= 0) {
                    try {
                        this.add(
                            CurrencyModel(
                                currency = Currency.getInstance(it.key),
                                rate = getCalculatedExchangeRate(it.value, multiplier),
                                imageUrl = getImageUrl(it.key)
                            )
                        )
                    } catch (exception: IllegalArgumentException) {
                        // Handle unknown country code
                    }
                }
            }
        }
    }

    // Set the exchange rate to POSITIVE_INFINITY indicate overflow happened
    fun getCalculatedExchangeRate(rate: Double, multiplier: Double): Double {
        return if (checkForOverflow(rate, multiplier)) {
            Double.POSITIVE_INFINITY
        } else {
            rate * multiplier
        }
    }

    fun checkForOverflow(rate: Double, multiplier: Double): Boolean {
        return (rate * multiplier == Double.POSITIVE_INFINITY || rate * multiplier == Double.NEGATIVE_INFINITY)
    }

    private fun cacheRate(ratesDTO: RatesDTO) {
        val lastCachedTime = sharePref.getLong(TAG_LAST_CACHED_TIME, -1L)
        if (lastCachedTime == -1L) {
            updateCache(ratesDTO)
        } else {
            val interval = Interval(lastCachedTime, System.currentTimeMillis())
            if (interval.toDuration().standardMinutes > 5) {
                updateCache(ratesDTO)
            }
        }
    }

    private fun updateCache(ratesDTO: RatesDTO) {
        ratesDao.updateCache(ratesDTO)
        sharePref.edit()
            .putLong(TAG_LAST_CACHED_TIME, System.currentTimeMillis())
            .apply()
    }
}