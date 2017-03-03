package com.joxxe.analyser.gui.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.joxxe.analyser.gui.MainWindow;
import com.joxxe.analyser.model.Util;
import com.joxxe.analyser.model.stock.Stock;
import com.joxxe.analyser.model.stock.Values;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Draws a graph for a selected stock.
 * Draws either a line graph or a candlestick graph depening on zoom level.
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class StockChart extends Pane {

	/**
	 * Private class for returning a set of values.
	 * @author joakim hagberg joakimhagberg87@gmail.com
	 *
	 */
	private class Dataset {
		private double min;
		private double max;

		public Dataset(double x, double y) {
			this.min = x;
			this.max = y;
		}

		public double getMax() {
			return max;
		}

		public double getMin() {
			return min;
		}
	}

	/**
	 * Private class for handling the zoom levels.
	 * joakim hagberg joakimhagberg87@gmail.com
	 *
	 */
	private class Zoom {
		private ArrayList<Integer> start;
		private ArrayList<Integer> end;

		public Zoom() {
			start = new ArrayList<Integer>();
			end = new ArrayList<Integer>();
		}

		public void clear() {
			start.clear();
			end.clear();
		}

		/**
		 *
		 * @return the latest zoom levels end index.
		 */
		public int getLatestZoomLevelEnd() {
			int last = end.size() - 1;
			if (last >= 0) {
				return end.get(last);
			}
			return -1;
		}

		/**
		 * 
		 * @return the latest zoom levels start index.
		 */
		public int getLatestZoomLevelStart() {

			int last = start.size() - 1;
			if (last >= 0) {
				return start.get(last);
			}

			return -1;
		}

		/**
		 * Zoom in to a specific level.
		 * @param startZoomIndex Index to start show data for, Must be smaller than endZoomIndex.
		 * @param endZoomIndex End index for data to be shown.
		 */
		public void zoomIn(int startZoomIndex, int endZoomIndex) {
			if (startZoomIndex < endZoomIndex && (endZoomIndex - startZoomIndex > 10)) {
				// only allow zoom in when start is smaller than end.
				int latestStart = 0;
				if (start.size() > 1) {
					latestStart = start.get(start.size() - 1);
				}
				start.add(startZoomIndex + latestStart);
				end.add(endZoomIndex + latestStart);
			}
		}

		/**
		 * Zoom out one level.
		 */
		public void zoomOut() {
			int index = start.size() - 1;
			if (index > 0) {
				start.remove(index);
				end.remove(index);
			}

		}

	}

	// variables to change
	private static double padding = 15;
	private static double labelPadding = 35;
	private static double volumeHeight = 80;
	private static double dateHeight = 20;
	private static Color graphColor = Color.CORNFLOWERBLUE;
	private static Color graphBackgroundColor = Color.WHITE;
	private static Color labelColor = Color.BLACK;
	private static Color gridColor = Color.rgb(0, 0, 0, 0.5); // light grey;
	private static Color todayColor = Color.rgb(255, 0, 0);
	private static Color markedCandleStick = Color.rgb(0, 0, 0, 0.3);
	private static Color zoomColor = Color.rgb(200, 230, 255, 0.5);
	private static Color candleMiddleBarColor = Color.rgb(0, 0, 0, 0.75);
	private static Color candlePositiveColor = Color.rgb(100, 255, 100);
	private static Color candleNegativeColor = Color.rgb(255, 100, 100);
	private static Color volumeColor = Color.rgb(100, 100, 255, 0.8);
	private static int CandleSticksWhenLesserThan = 500;
	private static int numberYDivisions = 10;
	private static double todaySize = 5;
	private static double tooltipWidth = 100;
	private static double toolTipHeight = 130;
	private static boolean toolTipEnabled = false; 
	// true = mousover, false = values on top.
	//
	private ArrayList<Values> stockdata;
	private Canvas overlayCanvas;
	private Canvas graphCanvas;
	private double minValue;
	private double maxValue;
	private boolean pressed;
	protected double prevPressedX;
	protected int prevPressedXPosition;

	private String stockName;
	private Zoom zoom;
	private double stockWidth;
	private double stockHeight;
	private double totalWidth;
	private double totalHeight;

	/**
	 * Constructor, creates the graph.
	 */
	public StockChart() {
		this.totalWidth = this.getWidth();
		this.totalHeight = this.getHeight();
		graphCanvas = new Canvas(totalWidth, totalHeight);
		overlayCanvas = new Canvas(totalWidth, totalHeight);
		getChildren().add(graphCanvas);
		getChildren().add(overlayCanvas);
		pressed = false;
		this.zoom = new Zoom();
	}

	/**
	 * Draws a candlestick graph
	 * @param gc the GraphicsContext to draw on.
	 * @param values Valus to use when drawing.
	 * @param yScale The y-axis scale.
	 */
	private void drawCandlestickChart(GraphicsContext gc, ArrayList<Values> values, double yScale) {
		double barWidth = (stockWidth - padding * 2 - labelPadding) / (values.size());
		for (int i = 0; i < values.size(); i++) {
			double barHeight;
			double x;
			double y;
			Color color;
			// gc.strokeLine(x1, y1, x2, y2);
			if (values.get(i).getClose() > values.get(i).getOpen()) {
				// accending
				barHeight = (values.get(i).getClose() - values.get(i).getOpen()) * yScale;
				y = ((maxValue - values.get(i).getClose()) * yScale + padding);
				x = padding + labelPadding + (i * barWidth);
				color = candlePositiveColor;
			} else {
				// descending
				barHeight = (values.get(i).getOpen() - values.get(i).getClose()) * yScale;
				y = ((maxValue - values.get(i).getOpen()) * yScale + padding);
				x = padding + labelPadding + (i * barWidth);
				color = candleNegativeColor;
			}
			// candle middle bar
			double yLow = ((maxValue - values.get(i).getLow()) * yScale + padding);
			double yHigh = ((maxValue - values.get(i).getHigh()) * yScale + padding);
			gc.setLineWidth(1);
			gc.setStroke(candleMiddleBarColor);
			gc.strokeLine(x + (barWidth / 2), yLow, x + (barWidth / 2), yHigh);
			// candle
			gc.setFill(color);
			gc.fillRect(x, y, barWidth, barHeight);
		}
	}

	/**
	 * Draws a line graph
	 * @param gc the GraphicsContext to draw on.
	 * @param values Valus to use when drawing.
	 * @param xScale The x-axis scale.
	 * @param yScale The y-axis scale.
	 */
	private void drawLineChart(GraphicsContext gc, ArrayList<Values> values, double xScale, double yScale) {
		gc.setStroke(graphColor);
		gc.setLineWidth(1);
		for (int i = 0; i < values.size() - 1; i++) {
			double x1 = (i * xScale + padding + labelPadding);
			double y1 = ((maxValue - values.get(i).getClose()) * yScale + padding);
			double x2 = ((i + 1) * xScale + padding + labelPadding);
			double y2 = ((maxValue - values.get(i + 1).getClose()) * yScale + padding);
			gc.strokeLine(x1, y1, x2, y2);
		}
	}

	/**
	 * Draws info about the hovering data on top of the graph.
	 * @param gc2 GraphicsContext to draw on.
	 * @param s The stock to draw info about.
	 */
	private void drawStockInfoTop(GraphicsContext gc2, Values s) {
		double labelWidth = 70;
		double x = stockWidth - padding - (labelWidth * 6);
		double y = padding + 20;
		gc2.setFont(new Font(10));
		gc2.setFill(labelColor);
		gc2.fillText(s.getDate(), x + 5, y, labelWidth);
		gc2.fillText("Open:" + Util.round2Decimal(s.getOpen()), x + 5 + labelWidth, y, labelWidth);
		gc2.fillText("Close:" + Util.round2Decimal(s.getClose()), x + 5 + labelWidth * 2, y, labelWidth);
		gc2.fillText("High:" + Util.round2Decimal(s.getHigh()), x + 5 + labelWidth * 3, y, labelWidth);
		gc2.fillText("Low:" + Util.round2Decimal(s.getLow()), x + 5 + labelWidth * 4, y, labelWidth);
		gc2.fillText("Volume:" + Util.bigNumber(s.getVolume()), x + 5 + labelWidth * 5, y, labelWidth);
	}
	/**
	 * Draws a tooltip for the hovering data values.
	 * @param gc2 GraphicsContext to draw on.
	 * @param x X-coord to draw tooltip on.
	 * @param y Y-coord to draw tooltip on.
	 * @param s The stock to draw info about.
	 */
	private void drawTooltip(GraphicsContext gc2, double x, double y, Values s) {
		double xc = x;
		double yc = y;
		gc2.setFill(labelColor);
		gc2.fillRect(xc, yc, tooltipWidth + 2, toolTipHeight + 7);
		gc2.setFill(graphBackgroundColor);
		gc2.fillRect(xc + 1, yc + 1, tooltipWidth, toolTipHeight + 5);
		gc2.setFill(labelColor);
		gc2.fillText(s.getDate(), xc + 5, yc + 20, 90);
		gc2.setFont(new Font(10));
		gc2.fillText("Open:" + Util.round2Decimal(s.getOpen()), xc + 5, yc + 40, 90);
		gc2.fillText("Close:" + Util.round2Decimal(s.getClose()), xc + 5, yc + 60, 90);
		gc2.fillText("High:" + Util.round2Decimal(s.getHigh()), xc + 5, yc + 80, 90);
		gc2.fillText("Low:" + Util.round2Decimal(s.getLow()), xc + 5, yc + 100, 90);
		gc2.fillText("Volume:" + Util.bigNumber(s.getVolume()), xc + 5, yc + 120, 90);
	}

	/**
	 * Draws a volume graph.
	 * @param gc GraphicsContext to draw on
	 * @param values Values to use when drawing the graph
	 */
	private void drawVolume(GraphicsContext gc, ArrayList<Values> values) {
		double minVol = Double.MAX_VALUE;
		double maxVol = Double.MIN_VALUE;
		boolean noVolume = false;
		for (Values v : values) {
			if (v.getVolume() == -1) {
				noVolume = true;
				break;
			}
			if (v.getVolume() < minVol) {
				minVol = v.getVolume();
			}
			if (v.getVolume() > maxVol) {
				maxVol = v.getVolume();
			}

		}
		if (noVolume) {
			//draw no volume graph, the stock didnt have any volume values.
			gc.clearRect(0, stockHeight + dateHeight, stockWidth, volumeHeight);
			gc.setFill(graphBackgroundColor);
			gc.fillRect(0, stockHeight + dateHeight, stockWidth, volumeHeight);
			gc.setFill(labelColor);
			gc.fillText("No volume to show", padding + labelPadding + 10, padding + stockHeight + dateHeight, 100);
		} else {
			gc.setStroke(graphColor);
			gc.clearRect(0, stockHeight + dateHeight, stockWidth, volumeHeight);
			gc.setFill(graphBackgroundColor);
			gc.fillRect(0, stockHeight + dateHeight, stockWidth, volumeHeight);
			gc.setFill(labelColor);
			gc.fillText("Volume", padding + labelPadding + 10, padding + stockHeight + dateHeight, 100);
			gc.setFill(volumeColor);
			double scale = (volumeHeight - padding) / (maxVol - minVol);
			for (int i = 0; i < values.size(); i++) {
				double barWidth = (stockWidth - padding * 2 - labelPadding) / (values.size());
				double barHeight = values.get(i).getVolume() * scale;
				double x = padding + labelPadding + (i * barWidth);
				double y = stockHeight + volumeHeight + dateHeight - barHeight;
				gc.fillRect(x, y, barWidth, barHeight);
			}
		}
	}

	/**
	 * Draws the x-axis for a graph (candlestick/linegraph).
	 * @param gc GraphicsContext to draw on.
	 * @param values Values to base x-axis on.
	 */
	private void drawXAxis(GraphicsContext gc, ArrayList<Values> values) {

		for (int i = 0; i < values.size(); i++) {
			if (values.size() > 1) {
				double x0 = i * (stockWidth - padding * 2 - labelPadding) / (values.size() - 1) + padding
						+ labelPadding;
				double x1 = x0;
				double y0 = stockHeight + padding;
				// double y1 = y0 - pointWidth;
				if ((i % ((int) ((values.size() / 10.0)) + 1)) == 0) {
					gc.setStroke(gridColor);
					gc.strokeLine(x0, padding, x1, y0);
					gc.setFill(labelColor);
					SimpleDateFormat f;
					if (values.size() <= CandleSticksWhenLesserThan) {
						// month
						f = new SimpleDateFormat("MMM dd");
					} else {
						// year
						f = new SimpleDateFormat("yyyy MMM");
					}

					String xLabel = f.format(values.get(i).getDateAsDate());
					int labelWidth = 50;
					gc.setFont(new Font(10));
					gc.fillText(xLabel, x0, y0, labelWidth);

				}
			}
		}
	}
	/**
	 * Draws the y-axis for a graph (candlestick/linegraph).
	 * @param gc GraphicsContext to draw on.
	 * @param values Values to base y-axis on.
	 */
	private void drawYAxis(GraphicsContext gc, ArrayList<Values> values) {
		for (int i = 0; i < numberYDivisions + 1; i++) {
			double x0 = padding + labelPadding;
			// double x1 = pointWidth + padding + labelPadding;
			double y0 = stockHeight + padding - ((i * (stockHeight + padding)) / numberYDivisions) + (10);
			double y1 = y0;
			if (values.size() > 0) {
				gc.setStroke(gridColor);
				gc.strokeLine(padding + labelPadding, y0, stockWidth - padding, y1);
				gc.setFill(labelColor);
				String yLabel = ((int) ((minValue + (maxValue - minValue) * ((i * 1.0) / numberYDivisions)) * 100))
						/ 100.0 + "";
				int labelWidth = 50;
				gc.setFont(new Font(10));
				gc.fillText(yLabel, x0 - labelWidth + 5, y0, labelWidth);
			}
			gc.setStroke(gridColor);
			// gc.strokeLine(x0, y0, x1, y1);
		}
	}

	/**
	 * 
	 * @param values
	 * @param lowest
	 *            True if getting low/high, false if getting close/open
	 * @return
	 */
	private Dataset getMinAndMax(ArrayList<Values> values) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (Values i : values) {
			if (i.getLow() < min) {
				min = i.getLow();
			}
			if (i.getHigh() > max) {
				max = i.getHigh();
			}

		}
		return new Dataset(min * 0.9, max * 1.1); // add some whitespace

	}

	/**
	 * Method that makes the candlestick that the mouse is currently hovering focused.
	 * @param gc2 GraphicsContext to draw on
	 * @param pos Position on the x-axis.
	 * @param s Stock to get info from
	 * @param yScale The y-axis scale.
	 * @param barWidth The width of a candlestick bar.
	 */
	private void makeCandlestickFocused(GraphicsContext gc2, int pos, Values s, double yScale, double barWidth) {
		double yval;
		double xval = ((pos) * barWidth) + (padding + labelPadding);
		double barHeight;
		if (s.getClose() > s.getOpen()) {
			barHeight = (s.getClose() - s.getOpen()) * yScale;
			yval = ((maxValue - s.getClose()) * yScale + padding);
		} else {
			barHeight = (s.getOpen() - s.getClose()) * yScale;
			yval = ((maxValue - s.getOpen()) * yScale + padding);
		}
		gc2.setFill(markedCandleStick);
		gc2.fillRect(xval, yval, barWidth, barHeight);
	}

	/**
	 *  Method that makes the point where the mouse is currently hovering focused.
	 * @param gc2
	 * @param pos
	 * @param s
	 * @param yScale
	 * @param barWidth
	 */
	private void makeLineFocused(GraphicsContext gc2, int pos, Values s, double yScale, double barWidth) {
		double yval;
		double xval = ((pos) * barWidth) + (padding + labelPadding) + (barWidth / 2);
		if (s.getClose() > s.getOpen()) {
			yval = ((maxValue - s.getClose()) * yScale + padding);
		} else {
			yval = ((maxValue - s.getOpen()) * yScale + padding);
		}
		gc2.setFill(todayColor);
		gc2.fillOval(xval - (todaySize / 2), yval - (todaySize / 2), todaySize, todaySize);
	}

	/**
	 * Method that redraws the whole graph. Called from constructor and when resize() is called.
	 */
	private void redraw() {

		totalWidth = this.getWidth();
		totalHeight = this.getHeight();
		stockWidth = totalWidth;
		stockHeight = totalHeight - volumeHeight - dateHeight;
		graphCanvas.setWidth(totalWidth);
		graphCanvas.setHeight(totalHeight);
		overlayCanvas.setWidth(totalWidth);
		overlayCanvas.setHeight(totalHeight);
		GraphicsContext gc = graphCanvas.getGraphicsContext2D();
		GraphicsContext gc2 = overlayCanvas.getGraphicsContext2D();
		int start = zoom.getLatestZoomLevelStart();
		int end = zoom.getLatestZoomLevelEnd();
		if (end > stockdata.size() - 1) {
			end = stockdata.size() - 1;
		}
		//do we have data to show?
		if (start != -1 && end != -1) {
			ArrayList<Values> data = new ArrayList<Values>(stockdata.subList(start, end));
			boolean candleStickChart = data.size() < CandleSticksWhenLesserThan;
			if (data != null) {
				Dataset minAndMax = getMinAndMax(data);
				maxValue = minAndMax.getMax();
				minValue = minAndMax.getMin();
				double xScale = (stockWidth - (2 * padding) - labelPadding) / (data.size() - 1);
				double yScale = (stockHeight - padding) / (maxValue - minValue);
				gc.setFill(graphBackgroundColor);
				gc.fillRect(0, 0, stockWidth, stockHeight + dateHeight);
				gc.setLineWidth(0.5);
				drawVolume(gc, data);
				drawYAxis(gc, data);
				drawXAxis(gc, data);
				if (candleStickChart) {
					drawCandlestickChart(gc, data, yScale);
				} else {
					drawLineChart(gc, data, xScale, yScale);
				}

				gc.setLineWidth(1);
				gc.setStroke(labelColor);
				gc.strokeRect(0, 0, totalWidth, totalHeight);

			}
			gc.setFill(labelColor);
			gc.setFont(Font.getDefault());
			gc.fillText(stockName, padding + labelPadding + 5, padding + 20, 200);
			//add zoom possibilities
			overlayCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {

				private int fixZoom(int pos) {
					if (pos < 0) {
						pos = 0;
					}
					if (pos > stockdata.size() - 1) {
						pos = stockdata.size() - 1;
					}
					return pos;
				}

				@Override
				public void handle(MouseEvent event) {
					if (event.getClickCount() == 1) {
						double x = event.getX();
						double xn = x - padding - labelPadding;
						double totWidth = stockWidth - padding * 2 - labelPadding;
						int xPosition = (int) (xn * (data.size() / totWidth));
						if (!pressed) {
							pressed = true;
							prevPressedX = x;
							prevPressedXPosition = xPosition;
						} else {
							pressed = false;
							if (prevPressedXPosition < xPosition) {
								xPosition = fixZoom(xPosition);
								prevPressedXPosition = fixZoom(prevPressedXPosition);
								MainWindow.output("Zoom between" + prevPressedXPosition + " and " + xPosition);
								zoom.zoomIn(prevPressedXPosition, xPosition);
							} else {
								xPosition = fixZoom(xPosition);
								prevPressedXPosition = fixZoom(prevPressedXPosition);
								MainWindow.output("Zoom between " + xPosition + " and " + prevPressedXPosition);
								zoom.zoomIn(xPosition, prevPressedXPosition);
							}
							redraw();

						}
					}

				}
			});

			overlayCanvas.setOnMouseMoved(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					double x = event.getX();
					double y = event.getY();
					if (x > padding + labelPadding && x < stockWidth - padding) {
						gc2.clearRect(0, 0, totalWidth, totalHeight);
						if (pressed) {
							gc2.setFill(zoomColor);
							if (x > prevPressedX) {
								gc2.fillRect(prevPressedX, 0 + padding, x - prevPressedX, stockHeight);
							} else {
								gc2.fillRect(x, 0 + padding, prevPressedX - x, stockHeight);
							}
						}

						gc2.strokeLine(x, 0 + padding, x, totalHeight);

						double xn = x - padding - labelPadding;
						if (xn > 0 && xn < stockWidth) {
							double totWidth = stockWidth - padding * 2 - labelPadding;
							int pos = (int) (xn * (data.size() / totWidth));
							Values s = data.get(pos);
							if (s != null) {
								double yScale = (stockHeight - padding) / (maxValue - minValue);
								double barWidth = (stockWidth - padding * 2 - labelPadding) / (data.size());
								if (candleStickChart) {
									makeCandlestickFocused(gc2, pos, s, yScale, barWidth);
								} else {
									makeLineFocused(gc2, pos, s, yScale, barWidth);

								}
								if (x > stockWidth - tooltipWidth) {
									x = x - tooltipWidth;
								}
								if (y + padding > stockHeight - toolTipHeight) {
									y = y - toolTipHeight;
								}
								if (toolTipEnabled) {
									drawTooltip(gc2, x, y, s);
								} else {
									drawStockInfoTop(gc2, s);
								}

							}

						}
					}

				}

			});
		} else {
			// no data to show.
			gc.setFill(graphBackgroundColor);
			gc.fillRect(0, 0, stockWidth, stockHeight + dateHeight + volumeHeight);
			gc.setFill(labelColor);
			gc.fillText(stockName, padding + labelPadding + 5, padding + 20, 200);
			gc.fillText("No data to show.", padding + labelPadding + 5, padding + 50, 200);
		}
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		redraw();
	}

	/**
	 * Set stock to draw graph for.
	 * @param s Stock to show graph for.
	 */
	public void setData(Stock s) {
		this.stockdata = s.getQuoteDays();
		this.stockName = s.getName();
		this.zoom.clear();
		if (stockdata.size() > 0) {
			this.zoom.zoomIn(0, stockdata.size() - 1);
		} else {
			this.zoom.zoomIn(0, 0);
		}
		redraw();
	}

	/**
	 * Method that zooms out (if possible).
	 */
	public void zoomOut() {
		zoom.zoomOut();
		redraw();
	}

}
