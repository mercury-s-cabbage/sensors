package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SensorAdapter

    private val environmentSensors = mutableListOf<Sensor>()
    private val positionSensors = mutableListOf<Sensor>()
    private val humanSensors = mutableListOf<Sensor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

        for (sensor in allSensors) {
            when (sensor.type) {
                Sensor.TYPE_AMBIENT_TEMPERATURE,
                Sensor.TYPE_PRESSURE,
                Sensor.TYPE_RELATIVE_HUMIDITY,
                Sensor.TYPE_LIGHT,
                Sensor.TYPE_PROXIMITY -> environmentSensors.add(sensor)

                Sensor.TYPE_ACCELEROMETER,
                Sensor.TYPE_GYROSCOPE,
                Sensor.TYPE_MAGNETIC_FIELD,
                Sensor.TYPE_ROTATION_VECTOR,
                Sensor.TYPE_GRAVITY,
                Sensor.TYPE_LINEAR_ACCELERATION -> positionSensors.add(sensor)

                Sensor.TYPE_HEART_BEAT,
                Sensor.TYPE_STEP_COUNTER,
                Sensor.TYPE_STEP_DETECTOR,
                Sensor.TYPE_HEART_RATE -> humanSensors.add(sensor)
            }
        }

        spinner = findViewById(R.id.spinner)
        recyclerView = findViewById(R.id.recyclerView)
        adapter = SensorAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categories = listOf(
            "Датчики окружающей среды",
            "Датчики положения устройства",
            "Датчики состояния человека"
        )
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val sensorsToShow = when (position) {
                    0 -> environmentSensors
                    1 -> positionSensors
                    2 -> humanSensors
                    else -> emptyList()
                }
                adapter.submitList(sensorsToShow)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private class SensorAdapter : RecyclerView.Adapter<SensorAdapter.SensorViewHolder>() {

        private val sensors = mutableListOf<Sensor>()

        fun submitList(newSensors: List<Sensor>) {
            sensors.clear()
            sensors.addAll(newSensors)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return SensorViewHolder(view)
        }

        override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
            val sensor = sensors[position]
            holder.bind(sensor)
        }

        override fun getItemCount(): Int = sensors.size

        class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text1: TextView = itemView.findViewById(android.R.id.text1)
            private val text2: TextView = itemView.findViewById(android.R.id.text2)

            fun bind(sensor: Sensor) {
                text1.text = sensor.name
                text2.text = "Тип: ${sensor.type}"
            }
        }
    }
}
