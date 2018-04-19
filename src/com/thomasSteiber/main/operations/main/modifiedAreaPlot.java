package com.thomasSteiber.main.operations.main;

import java.util.*;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

public class modifiedAreaPlot<X,Y> extends AreaChart<X,Y> {

    XYChart.Series vShaleSeries;

    public modifiedAreaPlot(Axis<X> xAxis, Axis<Y> yAxis, XYChart.Series<X,Y> series) {
        super(xAxis, yAxis);
        vShaleSeries = series;
    }

    @Override protected void layoutPlotChildren() {
        List<LineTo> constructedPath = new ArrayList<>(1);
            double lastY = 0;
            final ObservableList<Node> children = ((Group) vShaleSeries.getNode()).getChildren();
            ObservableList<PathElement> seriesLine = ((Path) children.get(1)).getElements();
            ObservableList<PathElement> fillPath = ((Path) children.get(0)).getElements();
            seriesLine.clear();
            fillPath.clear();
            constructedPath.clear();
            for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(vShaleSeries); it.hasNext(); ) {
                Data<X, Y> item = it.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                constructedPath.add(new LineTo(x, y));
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                lastY = y;
                Node symbol = item.getNode();
                if (symbol != null) {
                    final double w = symbol.prefWidth(-1);
                    final double h = symbol.prefHeight(-1);
                    symbol.resizeRelocate(x-(w/2), y-(h/2),w,h);
                }
            }

            if (!constructedPath.isEmpty()) {
                Collections.sort(constructedPath, Comparator.comparingDouble(LineTo::getY));
                LineTo first = constructedPath.get(0);

                final double displayXPos = first.getX();
                final double numericXPos = getXAxis().toNumericValue(getXAxis().getValueForDisplay(displayXPos));

                // RT-34626: We can't always use getZeroPosition(), as it may be the case
                // that the zero position of the y-axis is not visible on the chart. In these
                // cases, we need to use the height between the point and the y-axis line.
                final double xAxisZeroPos = getXAxis().getZeroPosition();
                final boolean isXAxisZeroPosVisible = !Double.isNaN(xAxisZeroPos);
                final double xAxisHeight = getXAxis().getHeight();
                final double xFillPos = isXAxisZeroPosVisible ? xAxisZeroPos :
                        numericXPos < 0 ? numericXPos - xAxisHeight : xAxisHeight;

                seriesLine.add(new MoveTo(displayXPos, first.getY()));
                fillPath.add(new MoveTo(xFillPos, first.getY()));

                seriesLine.addAll(constructedPath);
                fillPath.addAll(constructedPath);
                fillPath.add(new LineTo(xFillPos, lastY));
                fillPath.add(new ClosePath());
            }
    }

}
