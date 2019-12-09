package jacksondeng.revoluttest.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jacksondeng.revoluttest.data.api.RatesApi
import jacksondeng.revoluttest.data.cache.CachedRates
import jacksondeng.revoluttest.model.dto.RatesDTO
import jacksondeng.revoluttest.model.entity.CurrencyModel
import jacksondeng.revoluttest.model.entity.Rates
import jacksondeng.revoluttest.util.BASE_THUMBNAIL_URL
import jacksondeng.revoluttest.util.Result
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface RatesRepository {
    suspend fun getRates(base: String): Rates?
    fun getRatesToObserve(): LiveData<Result<Rates>>
    fun pollRates(base: String, multiplier: Double = 1.0)
    fun stopPolling()
    fun pausePolling()
}

class RatesRepositoryImpl @Inject constructor(
    private val api: RatesApi,
    private val cachedRates: CachedRates
) :
    RatesRepository {

    private var _rates = MutableLiveData<Result<Rates>>()
    private var rates: LiveData<Result<Rates>> = _rates

    private var compositeDisposable = CompositeDisposable()

    override fun pollRates(base: String, multiplier: Double) {
        // Prevent overlapping requests
        val scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
        compositeDisposable.add(
            Observable.interval(2, TimeUnit.SECONDS)
                .flatMap {
                    api.pollRates(base)
                        .retry(3)
                }
                .subscribeOn(scheduler)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ rates ->
                    rates?.let {
                        _rates.value = Result.Success(mapToModel(it, multiplier))
                    } ?: run {
                        _rates.value = Result.Failure(Throwable("Empty result"))
                    }
                }, {
                    _rates.value = Result.Failure(it)
                })
        )
    }

    override suspend fun getRates(base: String): Rates? {
        return try {
            val dto = api.getRates(base)
            mapToModel(dto, 1.0)
        } catch (exception: Exception) {
            cachedRates.getCachedRates(base)
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

    override fun stopPolling() = compositeDisposable.dispose()

    override fun pausePolling() = compositeDisposable.clear()

    override fun getRatesToObserve() = rates

    // Set the exchange rate to
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
}