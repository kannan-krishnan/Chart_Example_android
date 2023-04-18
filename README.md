# Chart_Example_android
Sample Chart Graph Without 3rd Party Libraries Android Kotlin

Create layout file

``` 
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

 
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)

       
        // Draw the data points and lines
        for (i in 0 until data.size - 1) {
 
            val startX = i.toFloat() / (data.size - 1) * width
            val startY = height - data[i] / maxValue * height

            val endX = (i + 1).toFloat() / (data.size - 1) * width
            val endY = height - data[i + 1] / maxValue * height
            canvas.drawLine(startX, startY, endX, endY, paint)
      
            canvas.drawCircle(startX, startY, pointRadius, pointPaint)
  
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

     
    }

}
```

Add xml View 

```
 <youer_package.LineChartView
        android:id="@+id/chart"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_marginStart="10dp"
        android:layout_marginTop="50dp"
        android:layout_marginEnd="10dp"
        android:pointerIcon="hand"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/reset"
        app:lineColor="@color/design_default_color_error"
        app:lineWidth="1dp"

        app:pointRadius="8dp" />
```

Add attrs file
```
    <declare-styleable name="LineChartView">
        <attr name="lineColor" format="color" />
        <attr name="lineWidth" format="dimension" />
        <attr name="pointRadius" format="dimension" />
    </declare-styleable>
    
```
    
    
 Set data from Activity to Fragment 
 ```
 private lateinit var chart:LineChartView
 ...
 
 chart = findViewById<LineChartView>(R.id.chart_name)
 
 chart.setData(arrayListOf(firstst,sec,thr,foure,five,firstst,sec,thr,foure,five),100f) 
 ```
