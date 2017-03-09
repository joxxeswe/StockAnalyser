package com.joxxe.analyser.gui.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.joxxe.analyser.gui.chart.indicators.AbstractIndicator;
import com.joxxe.analyser.gui.chart.indicators.AbstractInstrument;
import com.joxxe.analyser.gui.chart.indicators.AbstractOnGraphInstrument;
import com.joxxe.analyser.gui.chart.indicators.Volume;
import com.joxxe.analyser.model.Util;
import com.joxxe.analyser.model.stock.Stock;
import com.joxxe.analyser.model.stock.OHLC;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Draws a graph for a selected stock. Draws either a line graph or a
 * candlestick graph depening on zoom level.
 * 
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class StockChart extends Pane {

	/**
	 * Private class for returning a set of values.
	 * 
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
	 * Private class for handling the zoom levels. joakim hagberg
	 * joakimhagberg87@gmail.com
	 *
	 */
	private class Zoom {
		private ArrayList<Integer> start;
		private ArrayList<Integer> end;

		public Zoom() {
			start = new ArrayList<Integer>();
			end = new ArrayList<Integer>();
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

		public void pan(int panAmount) {
			int index = end.size() - 1;
			if (index >= 0) {
				int startValue = start.get(index) + panAmount;
				int endValue = end.get(index) + panAmount;
				if (startValue < 0) {
					int diff = 0 - startValue;
					start.set(index, 0);
					end.set(index, endValue + diff);
				} else if (endValue > end.get(0)) {
					int diff = endValue - end.get(0);
					start.set(index, startValue - diff);
					end.set(index, end.get(0));
				} else {
					start.set(index, startValue);
					end.set(index, endValue);
				}

			}

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
		 * 
		 * @param startZoomIndex
		 *            Index to start show data for, Must be smaller than
		 *            endZoomIndex.
		 * @param endZoomIndex
		 *            End index for data to be shown.
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

		public void clear() {
			start.clear();
			end.clear();
		}

	}

	// variables to change
	public static Color CHART_LINE_COLOR = Color.CORNFLOWERBLUE;
	public static Color CHART_BACKGROUND_COLOR = Color.WHITE;
	public static Color CHART_LABEL_COLOR = Color.BLACK;
	public static Color CHART_GRID_COLOR = Color.rgb(0, 0, 0, 0.5); // light grey;
	public static Color CHART_TODAY_CIRCLE_COLOR = Color.rgb(255, 0, 0);
	public static Color CANDLESTICK_FOCUSED_COLOR = Color.rgb(0, 0, 0, 0.3);
	public static Color CANDLESTICK_MIDDLE_BAR_COLOR = Color.rgb(0, 0, 0, 0.75);
	public static Color CANDLESTICK_POSITIVE_COLOR = Color.rgb(100, 255, 100);
	public static Color CANDLESTICK_NEGATIVE_COLOR = Color.rgb(255, 100, 100);
	public static Color VOLUME_COLOR = Color.rgb(100, 100, 255, 0.8);
	public static int USE_CANDLESTICK_WHEN_LESSER_THAN = 500;
	public static int NUMBER_OF_Y_LINES = 10;
	public static double CHART_TODAY_CIRCLE_SIZE = 5;
	public static double HEIGHT_OF_INSTRUMENT = 80;
	public static double HEIGHT_OF_DATE = 20;
	// true = mousover, false = values on top.
	//
	private ArrayList<AbstractIndicator> instrument;
	private ArrayList<OHLC> stockdata;
	private Canvas overlayCanvas;
	private Canvas graphCanvas;
	private double minValue;
	private double maxValue;
	private boolean pressed;
	private double prevPressedX;
	private String stockName;
	private Zoom zoom;
	private double stockWidth;
	private double stockHeight;
	private double totalWidth;
	private double totalHeight;
	private double scrollValue;
	private Volume volume;
	private ArrayList<RemoveIndicatorButton> removeButtons;

	/**
	 * Constructor, creates the graph.
	 */
	public StockChart() {
		this.removeButtons = new ArrayList<>();
		this.instrument = new ArrayList<>();
		volume = new Volume();
		this.totalWidth = this.getWidth();
		this.totalHeight = this.getHeight();
		graphCanvas = new Canvas(totalWidth, totalHeight);
		overlayCanvas = new Canvas(totalWidth, totalHeight);
		getChildren().add(graphCanvas);
		getChildren().add(overlayCanvas);
		pressed = false;
		this.zoom = new Zoom();
		scrollValue = 0;
	/*	addIndicator(new MA(50, Color.GREENYELLOW));
		addIndicator(new MA(200, Color.INDIANRED));
		addIndicator(new RSI(14, Color.INDIANRED));
		addIndicator(new EMA(10, Color.CHOCOLATE));*/
	}

	public void addIndicator(AbstractIndicator i) {
		instrument.add(i);
		//
		int index = instrument.size() - 1;
		RemoveIndicatorButton b = new RemoveIndicatorButton(index);
		removeButtons.add(b);
		b.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				instrument.remove(b.getIndex());
				getChildren().remove(b);
				removeButtons.remove(b);
				for (int i = 0; i < removeButtons.size(); i++) {
					if (i >= b.getIndex()) {
						removeButtons.get(i).subIndex();
					}
				}
			}

		});
		getChildren().add(b);
	}

	/**
	 * Draws a candlestick graph
	 * 
	 * @param gc
	 *            the GraphicsContext to draw on.
	 * @param values
	 *            Valus to use when drawing.
	 * @param yScale
	 *            The y-axis scale.
	 */
	private void drawCandlestickChart(GraphicsContext gc, ArrayList<OHLC> values, double yScale) {
		double barWidth = (stockWidth) / (values.size());
		for (int i = 0; i < values.size(); i++) {
			double barHeight;
			double x;
			double y;
			Color color;
			// gc.strokeLine(x1, y1, x2, y2);
			if (values.get(i).getClose() > values.get(i).getOpen()) {
				// accending
				barHeight = (values.get(i).getClose() - values.get(i).getOpen()) * yScale;
				y = ((maxValue - values.get(i).getClose()) * yScale);
				x = (i * barWidth);
				color = CANDLESTICK_POSITIVE_COLOR;
			} else {
				// descending
				barHeight = (values.get(i).getOpen() - values.get(i).getClose()) * yScale;
				y = ((maxValue - values.get(i).getOpen()) * yScale);
				x = (i * barWidth);
				color = CANDLESTICK_NEGATIVE_COLOR;
			}
			// candle middle bar
			double yLow = ((maxValue - values.get(i).getLow()) * yScale);
			double yHigh = ((maxValue - values.get(i).getHigh()) * yScale);
			gc.setLineWidth(1);
			gc.setStroke(CANDLESTICK_MIDDLE_BAR_COLOR);
			gc.strokeLine(x + (barWidth / 2), yLow, x + (barWidth / 2), yHigh);
			// candle
			gc.setFill(color);
			gc.fillRect(x, y, barWidth, barHeight);
		}
	}

	/**
	 * Draws a line graph
	 * 
	 * @param gc
	 *            the GraphicsContext to draw on.
	 * @param values
	 *            Valus to use when drawing.
	 * @param xScale
	 *            The x-axis scale.
	 * @param yScale
	 *            The y-axis scale.
	 */
	private void drawLineChart(GraphicsContext gc, ArrayList<OHLC> values, double xScale, double yScale) {
		gc.setStroke(CHART_LINE_COLOR);
		gc.setLineWidth(1);
		for (int i = 0; i < values.size() - 1; i++) {
			double x1 = (i * xScale);
			double y1 = ((maxValue - values.get(i).getClose()) * yScale);
			double x2 = ((i + 1) * xScale);
			double y2 = ((maxValue - values.get(i + 1).getClose()) * yScale);
			gc.strokeLine(x1, y1, x2, y2);
		}
	}

	/**
	 * Draws info about the hovering data on top of the graph.
	 * 
	 * @param gc2
	 *            GraphicsContext to draw on.
	 * @param s
	 *            The stock to draw info about.
	 */
	private void drawStockInfoTop(GraphicsContext gc2, OHLC s, int xPos) {
		// skip showing volume

		double labelWidth = 70;
		double x = stockWidth - (labelWidth * 6) - 10;
		double y = 20;
		double open = 0;
		double close = 0;
		double high = 0;
		double low = 0;
		double vol = 0;
		String date = "";
		if (s != null) {
			date = s.getDate();
			open = s.getOpen();
			close = s.getClose();
			high = s.getHigh();
			low = s.getLow();
			vol = s.getVolume();
		}

		gc2.setFont(new Font(10));
		gc2.setFill(CHART_LABEL_COLOR);
		gc2.fillText(date, x + 5, y, labelWidth);
		gc2.fillText("Open:" + Util.round2Decimal(open), x + 5 + labelWidth, y, labelWidth);
		gc2.fillText("Close:" + Util.round2Decimal(close), x + 5 + labelWidth * 2, y, labelWidth);
		gc2.fillText("High:" + Util.round2Decimal(high), x + 5 + labelWidth * 3, y, labelWidth);
		gc2.fillText("Low:" + Util.round2Decimal(low), x + 5 + labelWidth * 4, y, labelWidth);
		gc2.fillText("Volume:" + Util.bigNumber(vol), 5, 35, 200);
		int index = 0;
		for (AbstractIndicator i : instrument) {
			double val = 0;
			if (s != null) {
				val = i.getValueAtPos(xPos);
			}

			String str = i.getLabel() + " " + Util.round1Decimal(val);
			double ypos = 50 + (index * 15);
			gc2.fillText(str, 5, ypos, 100);
			index++;
		}
	}

	/**
	 * Draws the x-axis for a graph (candlestick/linegraph).
	 * 
	 * @param gc
	 *            GraphicsContext to draw on.
	 * @param values
	 *            Values to base x-axis on.
	 */
	private void drawXAxis(GraphicsContext gc, ArrayList<OHLC> values) {

		for (int i = 0; i < values.size(); i++) {
			if (values.size() > 1) {
				double x0 = i * (stockWidth) / (values.size());
				double x1 = x0;
				double y0 = stockHeight;
				// double y1 = y0 - pointWidth;
				if ((i % ((int) ((values.size() / 10.0)) + 1)) == 0) {
					gc.setStroke(CHART_GRID_COLOR);
					gc.strokeLine(x0, 0, x1, y0);
					gc.setFill(CHART_LABEL_COLOR);
					SimpleDateFormat f;
					if (values.size() <= USE_CANDLESTICK_WHEN_LESSER_THAN) {
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
	 * 
	 * @param gc
	 *            GraphicsContext to draw on.
	 * @param values
	 *            Values to base y-axis on.
	 */
	private void drawYAxis(GraphicsContext gc, ArrayList<OHLC> values, double yScale) {
		double diff = maxValue - minValue;
		double temp = diff / NUMBER_OF_Y_LINES;
		double mag = Math.floor(Math.log10(temp));
		double magPow = Math.pow(10, mag);
		int magMsd = (int) (temp / magPow + 0.5);
		double stepSize = magMsd * magPow;

		// build Y label array. // Lower and upper bounds calculations double
		double lb = stepSize * Math.floor(minValue / stepSize);
		double ub = stepSize * Math.ceil((maxValue / stepSize));
		double val = lb;
		while (true) {

			double x0 = stockWidth;
			double y0 = ((maxValue - val) * yScale);
			double y1 = y0;
			if (y0 <= stockHeight) {
				gc.setStroke(CHART_GRID_COLOR);
				gc.strokeLine(0, y0, stockWidth, y1);
				gc.setFill(CHART_LABEL_COLOR);
				String yLabel = Util.round1Decimal(val) + "";
				int labelWidth = 50;
				gc.setFont(new Font(10));
				gc.fillText(yLabel, x0 - labelWidth, y0, labelWidth);
			}
			val += stepSize;
			if (val > ub)
				break;
		}
	}

	/**
	 * 
	 * @param values
	 * @param lowest
	 *            True if getting low/high, false if getting close/open
	 * @return
	 */
	private Dataset getMinAndMax(ArrayList<OHLC> values) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (OHLC i : values) {
			if (i.getLow() < min) {
				min = i.getLow();
			}
			if (i.getHigh() > max) {
				max = i.getHigh();
			}

		}
		return new Dataset(min * 0.9, max * 1.1);

	}

	/**
	 * Method that makes the candlestick that the mouse is currently hovering
	 * focused.
	 * 
	 * @param gc2
	 *            GraphicsContext to draw on
	 * @param pos
	 *            Position on the x-axis.
	 * @param s
	 *            Stock to get info from
	 * @param yScale
	 *            The y-axis scale.
	 * @param barWidth
	 *            The width of a candlestick bar.
	 */
	private void makeCandlestickFocused(GraphicsContext gc2, int pos, OHLC s, double yScale, double barWidth) {
		double yval;
		double xval = ((pos) * barWidth);
		double barHeight;
		if (s.getClose() > s.getOpen()) {
			barHeight = (s.getClose() - s.getOpen()) * yScale;
			yval = ((maxValue - s.getClose()) * yScale);
		} else {
			barHeight = (s.getOpen() - s.getClose()) * yScale;
			yval = ((maxValue - s.getOpen()) * yScale);
		}
		gc2.setFill(CANDLESTICK_FOCUSED_COLOR);
		gc2.fillRect(xval, yval, barWidth, barHeight);
	}

	/**
	 * Method that makes the point where the mouse is currently hovering
	 * focused.
	 * 
	 * @param gc2
	 * @param pos
	 * @param s
	 * @param yScale
	 * @param barWidth
	 */
	private void makeLineFocused(GraphicsContext gc2, int pos, OHLC s, double yScale, double barWidth) {
		double yval;
		double xval = ((pos) * barWidth) + (barWidth / 2);
		if (s.getClose() > s.getOpen()) {
			yval = ((maxValue - s.getClose()) * yScale);
		} else {
			yval = ((maxValue - s.getOpen()) * yScale);
		}
		gc2.setFill(CHART_TODAY_CIRCLE_COLOR);
		gc2.fillOval(xval - (CHART_TODAY_CIRCLE_SIZE / 2), yval - (CHART_TODAY_CIRCLE_SIZE / 2), CHART_TODAY_CIRCLE_SIZE, CHART_TODAY_CIRCLE_SIZE);
	}

	/**
	 * Method that redraws the whole graph. Called from constructor and when
	 * resize() is called.
	 */
	public void redraw() {

		totalWidth = this.getWidth();
		totalHeight = this.getHeight();
		graphCanvas.setWidth(totalWidth);
		graphCanvas.setHeight(totalHeight);
		overlayCanvas.setWidth(totalWidth);
		overlayCanvas.setHeight(totalHeight);
		GraphicsContext gc = graphCanvas.getGraphicsContext2D();
		GraphicsContext gc2 = overlayCanvas.getGraphicsContext2D();
		setInstrumentWidth();
		// do we have data to show?
		if (stockdata != null && stockdata.size() > 0) {
			showIndicatorButtons(true);
			drawStockInfoTop(gc2, null, -1);
			for (AbstractIndicator gi : instrument) {
				if (gi instanceof AbstractInstrument) {
					AbstractInstrument i = ((AbstractInstrument) gi);
					i.setData(stockdata);

				} else if (gi instanceof AbstractOnGraphInstrument) {
					AbstractOnGraphInstrument i = ((AbstractOnGraphInstrument) gi);
					i.calculate(stockdata);
				}
			}
			// set pos for remove buttons
			// recalculate height for each instrument
			int startIndex = zoom.getLatestZoomLevelStart();
			int endIndex = zoom.getLatestZoomLevelEnd();
			int sDiff = 0;
			int eDiff = 0;
			if (startIndex < 0) {
				sDiff = 0 - startIndex;
				startIndex = 0;
			}
			if (endIndex > stockdata.size() - 1) {
				eDiff = endIndex - stockdata.size() - 1;
				endIndex = stockdata.size() - 1;
			}
			startIndex += eDiff;
			endIndex += sDiff;
			
			ArrayList<OHLC> zoomData = new ArrayList<>(stockdata.subList(startIndex, endIndex+1));
			boolean candleStickChart = zoomData.size() < USE_CANDLESTICK_WHEN_LESSER_THAN;
			boolean hasVolume = !(zoomData.get(0).getVolume() == -1);
			double adjust = 0;
			stockWidth = totalWidth;
			stockHeight = totalHeight - HEIGHT_OF_DATE - (getInstrumentCount() * HEIGHT_OF_INSTRUMENT);
			if (hasVolume) {
				stockHeight -= HEIGHT_OF_INSTRUMENT;
				adjust = HEIGHT_OF_INSTRUMENT;
			}
			Dataset minAndMax = getMinAndMax(zoomData);
			maxValue = minAndMax.getMax();
			minValue = minAndMax.getMin();
			double xScale = stockWidth / (zoomData.size());
			double yScale = stockHeight / (maxValue - minValue);
			gc.setFill(CHART_BACKGROUND_COLOR);
			gc.fillRect(0, 0, stockWidth, stockHeight + HEIGHT_OF_DATE);
			gc.setLineWidth(0.5);

			if (hasVolume) {
				// draw volume
				volume.draw(gc, zoomData, stockHeight + HEIGHT_OF_DATE);
			}
			drawYAxis(gc, zoomData, yScale);
			drawXAxis(gc, zoomData);
			if (candleStickChart) {
				drawCandlestickChart(gc, zoomData, yScale);
			} else {
				drawLineChart(gc, zoomData, xScale, yScale);
			}
			int nr = 0;
			for (AbstractIndicator gi : instrument) {
				if (gi instanceof AbstractInstrument) {
					double startY = stockHeight + HEIGHT_OF_DATE + adjust + (nr * StockChart.HEIGHT_OF_INSTRUMENT);
					AbstractInstrument i = ((AbstractInstrument) gi);
					i.draw(gc, startY, startIndex, endIndex);
					nr++;
				} else if (gi instanceof AbstractOnGraphInstrument) {
					AbstractOnGraphInstrument i = ((AbstractOnGraphInstrument) gi);
					i.drawGraphInstrument(gc, startIndex, endIndex, stockWidth, stockHeight, minValue, maxValue);
				}
			}
			gc.setLineWidth(1);
			gc.setStroke(CHART_LABEL_COLOR);
			gc.strokeRect(0, 0, totalWidth, totalHeight);
			// add pan possibilities
			overlayCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (!pressed) {
						prevPressedX = event.getX();
						pressed = true;
					}
				}
			});
			//handling panning left/right.
			overlayCanvas.setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					if (pressed) {
						double x = event.getX();
						int start = zoom.getLatestZoomLevelStart();
						int end = zoom.getLatestZoomLevelEnd();
						int diff = (end - start) / 10;
						if (prevPressedX > x) {
							zoom.pan(diff);
						} else {
							zoom.pan(-diff);
						}
						redraw();
						pressed = false;
					}
				}
			});
			//allow the user to zoom in/out.
			overlayCanvas.setOnScroll(new EventHandler<ScrollEvent>() {

				@Override
				public void handle(ScrollEvent event) {
					double scroll = event.getDeltaY();
					scrollValue += event.getDeltaY();
					if (Math.abs(scrollValue) > 50) {
						scrollValue = 0;
						if (scroll < 0) {
							// zoom out
							zoom.zoomOut();
							redraw();
						} else {
							// zoom in
							int start = zoom.getLatestZoomLevelStart();
							int end = zoom.getLatestZoomLevelEnd();
							int diff = (end - start);
							double xn = event.getX();
							int xPosition = (int) (xn * (zoomData.size() / totalWidth));
							start = xPosition - (diff / 5);
							end = xPosition + (diff / 5);
							if (start < 0) {
								start = 0;
							}
							if (end > stockdata.size() - 1) {
								end = stockdata.size() - 1;
							}
							zoom.zoomIn(start, end);
							redraw();

						}

					}

				}
			});

			//show hovering info about OHLC and indicators.
			overlayCanvas.setOnMouseMoved(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {
					double x = event.getX();
					if (x > 0 && x < stockWidth) {
						gc2.clearRect(0, 0, totalWidth, totalHeight);
						gc2.strokeLine(x, 0, x, totalHeight);

						double totWidth = stockWidth;
						int pos = (int) (x * (zoomData.size() / totWidth));
						OHLC s = zoomData.get(pos);
						if (s != null) {
							double yScale = stockHeight / (maxValue - minValue);
							double barWidth = (stockWidth) / (zoomData.size());
							if (candleStickChart) {
								makeCandlestickFocused(gc2, pos, s, yScale, barWidth);
							} else {
								makeLineFocused(gc2, pos, s, yScale, barWidth);

							}
							drawStockInfoTop(gc2, s, pos);

						}
					}

				}

			});

			gc.setFill(CHART_LABEL_COLOR);
			gc.setFont(Font.getDefault());
			gc.fillText(stockName, 5, 20, 200);

		} else {
			// no data to show.
			gc2.clearRect(0, 0, totalWidth, totalHeight);
			overlayCanvas.setOnMousePressed(null);
			overlayCanvas.setOnMouseMoved(null);
			gc.setFill(CHART_BACKGROUND_COLOR);
			gc.fillRect(0, 0, totalWidth, totalHeight);
			gc.setFill(CHART_LABEL_COLOR);
			gc.fillText(stockName, 5, 20, 200);
			gc.fillText("No data to show.", 5, 50, 200);
			showIndicatorButtons(false);
		}
	}

	/**
	 * Hide/show the indicator buttons
	 * @param show True if to be shown, otherwise false.
	 */
	private void showIndicatorButtons(boolean show) {
		for(RemoveIndicatorButton r : removeButtons){
			r.setVisible(show);
		}
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
		volume.setWidth(width);
		redraw();
	}

	private void setInstrumentWidth() {
		for (AbstractIndicator i : instrument) {
			i.setWidth(totalWidth);
		}
	}

	/**
	 * Set stock to draw graph for.
	 * 
	 * @param s
	 *            Stock to show graph for.
	 */
	public void setData(Stock s) {
		this.stockdata = s.getQuoteDays();
		this.stockName = s.getName();
		this.zoom.clear();
		this.zoom.zoomIn(0, stockdata.size()-1);
		redraw();
	}

	/**
	 * Method that zooms out (if possible).
	 */
	public void zoomOut() {
		zoom.zoomOut();
		redraw();
	}

	public int getInstrumentCount() {
		int nr = 0;
		for (AbstractIndicator i : instrument) {
			if (i instanceof AbstractInstrument) {
				nr++;
			}
		}
		return nr;
	}

}
