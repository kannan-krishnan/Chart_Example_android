package com.blogspot.kannanpvm007.chartexample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(),ScreenshotDetector.BackPressedListener {

    var firstst=90f
    var sec=50f
    var thr=10f
    var foure=70f
    var five=60f
    private lateinit var chart:LineChartView


    private lateinit var screenshotDetector: ScreenshotDetector
    private lateinit var image: ImageView
    private lateinit var ltv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        screenshotDetector = ScreenshotDetector(baseContext)

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        screenshotDetector.backPressedListener=this
        image= findViewById(R.id.image)
        ltv= findViewById(R.id.ltv)
        chart = findViewById<LineChartView>(R.id.chart)
        setData()
        findViewById<Button>(R.id.reset).setOnClickListener {
            firstst = 90f
            sec = 50f
            thr = 10f
            foure = 70f
            five = 60f
            setData()
        }
        findViewById<Button>(R.id.changeValue).setOnClickListener {
            if (firstst > 1) {
                firstst--
            }

            if (sec < 100) {
                sec++
            }

            if (thr < 100) {
                thr++
            }

            if (foure < 100) {
                foure++
            }

            if (five > 1) {
                five--
            }

            setData()
        }

        findViewById<Button>(R.id.next).setOnClickListener {
            startActivity(Intent(this,MainActivity2::class.java))
        }
        val graphView = findViewById<StockGraphView>(R.id.graphView)
        graphView.setValues(arrayListOf(100f, 120f, 110f, 130f, 140f))
    }

    override fun onResume() {
        super.onResume()
        detectScreenshots()

//        screenshotObserver.register()
    }

    override fun onPause() {
        super.onPause()
//        screenshotObserver.unregister()
        screenshotDetector.stop()
    }

    private fun setData(){
        chart.setData(arrayListOf(firstst,sec,thr,foure,five,firstst,sec,thr,foure,five),100f)
        chart.setHorizontalLine(50f,"50%", Color.RED)
    }

    private fun detectScreenshots() {
        if (haveStoragePermission()) {
            screenshotDetector.start()
        } else {
            requestPermission()
        }
    }
    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this,
             Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions,102)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            102 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    detectScreenshots()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onItemClick(path: String) {

        Log.d("TAG", "onItemClick: ------------------------->$path")
        image.setImageBitmap(BitmapFactory.decodeFile(path))
        ltv.text=path

    }
}
/**chart class*/
class LineChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var lineColor: Int = Color.BLACK
    private var lineWidth: Float = 4f
    private var pointRadius: Float = 16f
    private lateinit var paint: Paint
    private var data: List<Float> = emptyList()
    private var maxValue: Float = 0f

    private var pointPaint = Paint().apply {
        color = Color.BLUE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = lineWidth
    }

    fun setData(data: List<Float>, maxValue: Float) {
        this.data = data
        this.maxValue = maxValue
        invalidate()
    }
    fun setPointColor(color: Int) {
        pointPaint.color = color
        invalidate()
    }

    /**level*/
    private var horizontalLineValue = 0f
    private var horizontalLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 3f
        isAntiAlias = true
    }

    /** level text*/

    private var horizontalLineText = ""
    private var horizontalLineTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        isAntiAlias = true
    }

    fun setHorizontalLine(value: Float, color: Int) {
        horizontalLineValue = value
        horizontalLinePaint.color = color
        invalidate()
    }

    /**zoom*/
    private var scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            if (scaleFactor.isFinite()) {
                val previousMaxValue = maxValue
                maxValue /= scaleFactor
                if (maxValue < 1f) maxValue = 1f
                val scale = previousMaxValue / maxValue
                pointRadius *= scale
                paint.strokeWidth *= scale
                horizontalLinePaint.strokeWidth *= scale
                horizontalLineTextPaint.textSize *= scale
                invalidate()
            }
            return true
        }
    }


    fun setHorizontalLine(value: Float, text: String, color: Int) {
        horizontalLineValue = value
        horizontalLineText = text
        horizontalLinePaint.color = color
        invalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)

        // level
        val horizontalLineY = height - horizontalLineValue / maxValue * height
        canvas.drawLine(0f, horizontalLineY, width.toFloat(), horizontalLineY, horizontalLinePaint)

        // Draw horizontal line text
        if (horizontalLineText.isNotEmpty()) {
            val textWidth = horizontalLineTextPaint.measureText(horizontalLineText)
            val textX = width - textWidth - 10f
            val textY = horizontalLineY - 10f
            canvas.drawText(horizontalLineText, textX, textY, horizontalLineTextPaint)
        }

        // Draw the data points and lines
        for (i in 0 until data.size - 1) {

            Log.e("TAG", "onDraw: i value=$i and data.size-1 ="+(data.size-2))
            val startX = if (i == 0) {
                ( i.toFloat() / (data.size - 2) * width)+40// Add start margin for first point
            }
            else {
                i.toFloat() / (data.size - 1) * width
            }

            /** without margin */
//            val startX = i.toFloat() / (data.size - 1) * width

            val startY = height - data[i] / maxValue * height

            val endX = (i + 1).toFloat() / (data.size - 1) * width
            val endY = height - data[i + 1] / maxValue * height
            canvas.drawLine(startX, startY, endX, endY, paint)
            pointPaint.color=  if (data[i]>30){
                Color.GREEN
            }else{
                Color.RED
            }
            canvas.drawCircle(startX, startY, pointRadius, pointPaint)
            canvas.drawText(data[i].toString(), startX, startY - pointRadius,horizontalLineTextPaint ) // point text
            canvas.drawCircle(endX, endY, pointRadius, pointPaint)


        }


    }
    init {

        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.LineChartView, 0, 0)
        lineColor = ta.getColor(R.styleable.LineChartView_lineColor, Color.BLACK)
        lineWidth = ta.getDimension(R.styleable.LineChartView_lineWidth, 4f)
        pointRadius = ta.getDimension(R.styleable.LineChartView_pointRadius, 16f)
        ta.recycle()

        // Initialize the Paint object
        paint = Paint()
        paint.color = lineColor
        paint.strokeWidth = lineWidth
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val zoom = false // if you need zoom
        if (zoom) {
            scaleDetector.onTouchEvent(event)
            return true
        } else {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Check if the touch event is inside a data point
                    val x = event.x
                    val y = event.y
                    for (i in data.indices) {
                        val startX = i.toFloat() / (data.size - 1) * width
                        val startY = height - data[i] / maxValue * height
                        val rectF = RectF(
                            startX - pointRadius,
                            startY - pointRadius,
                            startX + pointRadius,
                            startY + pointRadius
                        )
                        if (rectF.contains(x, y)) {

                            // Display additional information about the selected point
                            //                        invalidate() //

                            val detailsPopup = PopupWindow(context)
                            val contentView =
                                LayoutInflater.from(context).inflate(R.layout.pop_up, null)
                            val selectedPoint = data[i]
                            val textView =
                                contentView.findViewById<TextView>(R.id.details_text_view)
                            textView.text = "Value: $selectedPoint"
                            detailsPopup.contentView = contentView
                            detailsPopup.showAtLocation(this, Gravity.CENTER, 0, 0)
                            return true
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    // Clear the selection when the touch event is released
                    invalidate()
                    return true
                }
            }
            return super.onTouchEvent(event)
        }
    }

}

class StockGraphView(context: Context?, attrs: AttributeSet?) : View(context,attrs) {
//    private val values = floatArrayOf(100f, 120f, 110f, 130f, 140f)
private val values = mutableListOf<Float>()
    
    private val paint = Paint()

    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = canvas.width
        val height = canvas.height

        // Draw horizontal and vertical axis lines
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)

        // Calculate x and y scale factors
        val xScale = width / (values.size - 1).toFloat()
        val yScale = height / 140f

        canvas?.let {
            // Draw the chart
            // ...

            // Draw the title
            val title = "Stock Price History"
            paint.textSize = 24f
            val titleWidth = paint.measureText(title)
            val titleX = (width - titleWidth) / 2f
            val titleY = 48f
            canvas.drawText(title, titleX, titleY, paint)
        }

        canvas?.let {
            // Draw the chart
            // ...

            // Draw the x-axis label
            paint.textSize = 18f
            val xAxisLabel = "Time"
            val xAxisLabelWidth = paint.measureText(xAxisLabel)
            val xAxisLabelX = (width - xAxisLabelWidth) / 2f
            val xAxisLabelY = height - 16f
            canvas.drawText(xAxisLabel, xAxisLabelX, xAxisLabelY, paint)

            // Draw the y-axis label
            val yAxisLabel = "Price"
            paint.textSize = 18f
            val yAxisLabelWidth = paint.measureText(yAxisLabel)
            val yAxisLabelX = 8f
            val yAxisLabelY = (height + yAxisLabelWidth) / 2f
            canvas.save()
            canvas.rotate(-90f, yAxisLabelX, yAxisLabelY)
            canvas.drawText(yAxisLabel, yAxisLabelX, yAxisLabelY, paint)
            canvas.restore()
        }


        // Draw the data points and lines
        for (i in values.indices) {
            val x = i * xScale
            val y = height - values[i] * yScale
            canvas.drawCircle(x, y, 4f, paint)
            if (i > 0) {
                val prevX = (i - 1) * xScale
                val prevY = height - values[i - 1] * yScale
                canvas.drawLine(prevX, prevY, x, y, paint)
            }
        }



    }
    fun setValues(values: List<Float>) {
        this.values.clear()
        this.values.addAll(values)
        invalidate()
    }

    fun clear() {
        this.values.clear()
        invalidate()
    }
}