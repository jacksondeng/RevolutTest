package jacksondeng.revoluttest.data.repo

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.dao.RatesDao
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.BASE_THUMBNAIL_URL
import jacksondeng.revoluttest.util.TAG_LAST_CACHED_TIME
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface RatesRepository {
    fun pollRates(base: String, multiplier: Double = 1.0): Observable<Rates>
}

class RatesRepositoryImpl @Inject constructor(
    private val api: RatesApi,
    private val ratesDao: RatesDao,
    private val sharePref: SharedPreferences
) :
    RatesRepository {

    override fun pollRates(base: String, multiplier: Double): Observable<Rates> {
        // Prevent overlapping requests
        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
        return (
                Observable.interval(2, TimeUnit.SECONDS)
                    .flatMap {
                        //api.pollRates(base).retry(3)
                        ratesDao.getRates("EUR")
                    }
                    .subscribeOn(scheduler)
                    .doOnNext {
                        if (sharePref.getLong(TAG_LAST_CACHED_TIME, -1L) == -1L) {
                            ratesDao.cache(it)
                            sharePref.edit()
                                .putLong(TAG_LAST_CACHED_TIME, System.currentTimeMillis())
                                .apply()
                        }
                    }
                    .distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        Observable.just(mapToModel(it, multiplier))
                    }
                )
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
                    rate = 0.0,
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

    fun cacheRate() {
        // TODO: Implement caching mechanism
    }
}