package com.joxxe.analyser.model.stock;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.joxxe.analyser.gui.MainWindow;
import com.joxxe.analyser.model.NordnetCrawler;
import com.joxxe.analyser.model.SearchResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Keeps track of all added stocks and search results.
 * 
 * @author joakim hagberg joakimhagberg87@gmail.com
 *
 */
public class StockHandler {
	private ObservableList<SearchResult> searchResult;
	private ObservableList<Stock> stocks;

	public StockHandler() {
		this.searchResult = FXCollections.observableList(new ArrayList<SearchResult>());
		this.stocks = FXCollections.observableList(new ArrayList<Stock>());
		this.load();

	}

	public void addSearchResult(SearchResult s) {
		searchResult.add(s);
	}

	public ObservableList<SearchResult> getSearchResults() {
		return searchResult;
	}

	public void addStock(Stock s) {
		if (!stocks.contains(s)) {
			MainWindow.output("Adding " + s.getName());
			stocks.add(s);
		} else {
			MainWindow.output(s.getName() + " already added");
		}

	}

	public ObservableList<Stock> getStocks() {
		return stocks;
	}

	public void clearSearchResults() {
		this.searchResult.clear();
	}

	public void save() {
		try {
			MainWindow.output("Saving data");
			OutputStream file = new FileOutputStream("marketdata.ser");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			ArrayList<Stock> stocksCopy = new ArrayList<>();
			for (Stock s : stocks) {
				stocksCopy.add(s);
			}
			output.writeObject(stocksCopy);
			output.close();
			file.close();
			MainWindow.output("data saved");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void load() {
		try {
			MainWindow.output("Loading data");
			FileInputStream file = new FileInputStream("marketdata.ser");
			BufferedInputStream buffer = new BufferedInputStream(file);
			ObjectInputStream input = new ObjectInputStream(buffer);
			ArrayList<Stock> loaded = (ArrayList<Stock>) input.readObject();
			this.stocks = FXCollections.observableList(loaded);
			input.close();
			file.close();
			// addStock(new Stock("OMXS30", "SSE", "OMXS30", null));
			MainWindow.output("Data loaded");
		} catch (FileNotFoundException e) {
			MainWindow.output("No data to load.");
		} catch (IOException e) {
			MainWindow.output("Error when loading saved data:" + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			MainWindow.output("Error when loading saved data:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean removeStock(Stock item) {
		if (stocks.remove(item)) {
			MainWindow.output(item.getName() + " removed.");
			return true;
		}
		MainWindow.output(item.getName() + " not found.");
		return false;
	}

	public void updateStocks(Date startDate) {
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = dt.format(startDate.getTime());
		MainWindow.output(strDate);
		for (Stock s : stocks) {
			Date d = s.getLatestUpdate();
			Date sDt = null;
			if (d == null) {
				// today startdate!!
				try {
					sDt = dt.parse("2000-01-01");
					MainWindow.output("Update " + s.getName() + " from date: " + sDt);
					ArrayList<Values> result = NordnetCrawler.getStockData(s.getIdentifier(), s.getMarketId(),
							sDt);
					for (Values v : result) {
						s.addQouteDay(v, false);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				// d - 14;
				sDt = d;
				sDt.setTime(sDt.getTime() - (14 * 24 * 60 * 60 * 1000));
				MainWindow.output("Update " + s.getName() + " from date: " + sDt);
				ArrayList<Values> result = NordnetCrawler.getStockData(s.getIdentifier(), s.getMarketId(), sDt);
				for (Values v : result) {
					s.addQouteDay(v, true);
				}
				MainWindow.output("Finished updating " + s.getName());
			}

		}

	}
}
