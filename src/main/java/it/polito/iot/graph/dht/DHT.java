package it.polito.iot.graph.dht;

import java.sql.Timestamp;

public class DHT {
	private double temperature;
	private double humidity;
	private java.sql.Timestamp timestamp;
	public DHT() {}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getHumidity() {
		return humidity;
	}
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}
	public java.sql.Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(java.sql.Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public DHT(double temperature, double humidity, Timestamp timestamp) {
		super();
		this.temperature = temperature;
		this.humidity = humidity;
		this.timestamp = timestamp;
	}
	
	
}
