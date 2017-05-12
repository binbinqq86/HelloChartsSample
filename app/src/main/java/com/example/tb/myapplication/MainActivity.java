package com.example.tb.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lecho.lib.hellocharts.gesture.ChartScroller;
import lecho.lib.hellocharts.gesture.ChartTouchHandler;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final float chartWidth=4f;

    @BindView(R.id.chart)
    LineChartView chart;
    String[] date = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10","11","12","13","14","15","16","17","18","19","20"};//X轴的标注
    int[] score = {25, 22, 18, 16, 15, 30, 22, 35, 37, 10,15,18,20,24,25,26,27,28,29,30};//图表的数据点
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    private float mFirstX,mLastX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getAxisXLables();
        getAxisPoints();

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(mPointValues).setColor(Color.BLUE).setCubic(true).setHasLabels(true).setStrokeWidth(5).setShape(ValueShape.CIRCLE);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        //三个同时设置，才能设置标签背景色
        data.setValueLabelBackgroundEnabled(true);
        data.setValueLabelBackgroundAuto(false);
        data.setValueLabelBackgroundColor(Color.GREEN);



        //坐标轴
        Axis axisX = new Axis(); //X轴
//        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(20);//设置字体大小
//        axisX.setMaxLabelChars(8); //
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线
        axisX.setHasSeparationLine(false);//设置标签跟图表之间的轴线
        axisX.setLineColor(Color.YELLOW);
//        axisX.generateAxisFromRange(0,0,1);//从已知当中截取
        data.setAxisXBottom(axisX); //x 轴在底部

        //设置上下两个轴线，为了防止绘制的曲线被遮挡，可以留出空隙==========================================
        Axis axisXTop = new Axis(); //X轴
        axisXTop.setTextColor(Color.TRANSPARENT);  //设置字体颜色
//        axisXTop.setTextSize(20);//设置字体大小
//        axisXTop.setValues(mAxisXValues);  //填充X轴的坐标名称
//        axisXTop.setHasLines(true); //x 轴分割线
//        axisXTop.setHasSeparationLine(false);//设置标签跟图表之间的轴线
//        axisXTop.setLineColor(Color.RED);
        data.setAxisXTop(axisXTop); //x 轴在底部


        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(20);//设置字体大小
//        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边

//        chart.setValueTouchEnabled(false);
        chart.setZoomEnabled(false);
//        chart.setScrollEnabled(false);
//        chart.setInteractive(false);
//        chart.setZoomType(ZoomType.HORIZONTAL);
//        chart.setMaxZoom((float) 2);//最大方法比例
//        chart.setZoomLevel(0,0,2);
//        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
//
        chart.setLineChartData(data);

        //viewport必须设置在setLineChartData后面，设置一个当前viewport，再设置一个maxviewport，就可以实现滚动，高度要设置数据的上下限
        chart.setViewportCalculationEnabled(false);
        final Viewport v = new Viewport(chart.getMaximumViewport());
//        Log.e(TAG, "onCreate: "+v.left+"#"+v.top+"#"+v.right+"$"+v.bottom );
        v.left = 0;
        v.right= chartWidth;
        v.top=37;
        v.bottom=10;
        chart.setCurrentViewport(v);

//        Log.e(TAG, "onCreate: "+v.left+"#"+v.top+"#"+v.right+"$"+v.bottom );

        final Viewport maxV=new Viewport(chart.getMaximumViewport());
        maxV.left=0;
        maxV.right=score.length-1;
        maxV.top=37;
        maxV.bottom=10;
        chart.setMaximumViewport(maxV);

        final Rect rect=chart.getChartComputator().getContentRectMinusAllMargins();

//        chart.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                float fx=chart.getChartComputator().computeRawDistanceX(5);
//                float fy=chart.getChartComputator().computeRawDistanceY(100);
//                Rect r=chart.getChartComputator().getContentRectMinusAllMargins();
//                float x=motionEvent.getX()/(r.width()/v.width());
//                float y=motionEvent.getY()/(r.height()/v.height());
//                chart.moveTo(x,y);
//
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        mFirstX=motionEvent.getX();
//                        mLastX=motionEvent.getX();
////                        Log.e(TAG, "onTouch: "+mFirstX );
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float totalDeltaX=motionEvent.getX()-mLastX;
//                        mLastX=motionEvent.getX();
//                        float realX=v.width()*totalDeltaX/rect.width();
//
//                        Log.e(TAG, "onTouch: "+realX +"#"+totalDeltaX+"$$$"+rect.width()+"$"+v.width()+"###");
//                        Log.e(TAG, "onTouch:before... "+v.left+"#"+v.top+"$"+v.right+"$"+v.bottom );
//                        Viewport vTemp=new Viewport(v);
//                        vTemp.left += -realX;
//                        vTemp.right = vTemp.left+chartWidth;
//                        if(vTemp.left<0){
//                            vTemp.left=0;
//                            vTemp.right=chartWidth;
//                        }
//                        if(vTemp.left>score.length-1-chartWidth){
//                            vTemp.left=score.length-1-chartWidth;
//                            vTemp.right=score.length-1;
//                        }
//                        if(vTemp.right>score.length-1){
//                            vTemp.right=score.length-1;
//                            vTemp.left=score.length-1-chartWidth;
//                        }
//                        if(vTemp.right-vTemp.left!=chartWidth){
//                            break;
//                        }
//                        v.set(vTemp);
//                        chart.setMaximumViewport(v);
//                        chart.setCurrentViewport(v);
//                        Log.e(TAG, "onTouch:after... "+v.left+"#"+v.top+"$"+v.right+"$"+v.bottom );
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
    }

    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables() {
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints() {
        for (int i = 0; i < score.length; i++) {
            mPointValues.add(new PointValue(i, score[i]));
        }
    }

}
