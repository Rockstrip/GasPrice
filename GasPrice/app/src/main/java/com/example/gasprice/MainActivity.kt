package com.example.gasprice

//import kotlinx.serialization.*
//import kotlinx.serialization.json.JSON
//import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.util.Half.toFloat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors.toList

class MainActivity : AppCompatActivity() {
    private var html = ""
    private var gasStations: MutableList<GasStation> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            val url = URL("https://index.minfin.com.ua/markets/fuel/detail/")
            val urlConnection = url.openConnection() as HttpURLConnection

            try {
                html = urlConnection.inputStream.bufferedReader().readText()
            } finally {
                urlConnection.disconnect()
            }

            val regRegionSplit = ">(?=[А-Я][а-я]+ обл\\.)".toRegex()
            val regStationSplit = "class='r[0-1]'>(?=.*<\\/td><td align='center')".toRegex()
            val regPriceSplit = "<td align='right' class='r[0-1]'>(.*?)<\\/td>".toRegex()

            val regRegionFind = "([А-Я][а-я]+) обл".toRegex()
            val regStationFind = "(.*)<\\/td><td align='center'".toRegex()
            val regPriceFind = "<td align='right' class='r[0-1]'>(.*?)<\\/td>".toRegex()

            val text = regRegionSplit.split(html)
            var first = true
            for (regionSplit in text) {
                if (first) {
                    first = false
                    continue
                }

                val region = regRegionFind.find(regionSplit)!!.groups[1]!!.value;
                val text2 = regStationSplit.split(regionSplit);

                var firstRegion = true
                for (stationSplit in text2.toList()) {
                    if (firstRegion) {
                        firstRegion = false
                        continue
                    }

                    val station = regStationFind.find(stationSplit)!!.groups[1]!!.value;
                    val text3 = regPriceFind.findAll(stationSplit.replace(',','.'))
                    val prices = text3.map { it.groupValues[1] }

                    var prices2: MutableList<Float?> = mutableListOf()
                    for (price in prices) {
                        prices2.add(price.toFloatOrNull())
                    }



                    gasStations.add(GasStation(station, region, prices2))
                }
            }

            runOnUiThread {
                var spin = findViewById<Spinner>(R.id.spinner)

                spin?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        WritePrices()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        WritePrices()
                    }

                }
                WritePrices()

                Log.d("RegPrice1", gasStations.toString());
                Log.d("TEST", "1.2".toFloat().toString())
            }
        }.start()


    }
    fun WritePrices(){
        var spin = findViewById<Spinner>(R.id.spinner)

        findViewById<TextView>(R.id.A95Plus).text = gasStations.first {it.Region==spin.selectedItem}.A95Plus.toString()
        findViewById<TextView>(R.id.A95).text = gasStations.first {it.Region==spin.selectedItem}.A95.toString()
        findViewById<TextView>(R.id.A92).text = gasStations.first {it.Region==spin.selectedItem}.A92.toString()
        findViewById<TextView>(R.id.DT).text = gasStations.first {it.Region==spin.selectedItem}.DT.toString()
        findViewById<TextView>(R.id.Gas).text = gasStations.first {it.Region==spin.selectedItem}.Gas.toString()
    }
}