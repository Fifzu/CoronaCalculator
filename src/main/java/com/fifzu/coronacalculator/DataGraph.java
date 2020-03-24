package com.fifzu.coronacalculator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataGraph extends ApplicationFrame {
    public DataGraph(String title, List<String> records) throws ParseException {
        super("Corona Calculator");
        XYDataset dataset = createDataset(records);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                "Date",     //domain
                "Infections",
                dataset
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd.MM"));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    private XYDataset createDataset(List<String> records) throws ParseException {
        TimeSeries infections = new TimeSeries("Infections");
        TimeSeries predictions = new TimeSeries("Predictions");

        for(String record : records) {

            String sDate=record.split(";")[0];
            Date date=new SimpleDateFormat("MM-dd-yyyy").parse(sDate);

            int infected = Integer.parseInt(record.split(";")[1]);
            boolean calculated = Boolean.parseBoolean(record.split(";")[3]);

            if (calculated){
                predictions.add(new Day(date),infected);
            } else {
                infections.add(new Day(date),infected);
            }
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(infections);
        dataset.addSeries(predictions);
        return dataset;
    }
}

