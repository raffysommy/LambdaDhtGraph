package it.polito.iot.graph.dht;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;


import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

/**
* AthenaClientFactory
* -------------------------------------
* This code shows how to create and configure an Amazon Athena client.
*/
public class AthenaClient
{

  private Connection connection;
  private AWSCredentials cred=new AWSCredentialsProviderChain( DefaultAWSCredentialsProviderChain.getInstance()).getCredentials();
  public AthenaClient()
  {
	  Properties info = new Properties();
      info.put("s3_staging_dir", "s3://aws-athena-query-results-08761428278-eu-west-1/Unsaved/");
      info.put("aws_credentials_provider_class","com.amazonaws.auth.DefaultAWSCredentialsProviderChain");
      info.put("user",cred.getAWSAccessKeyId());
      info.put("password", cred.getAWSSecretKey());
      try {
    	Class.forName("com.amazonaws.athena.jdbc.AthenaDriver");
		this.connection = DriverManager.getConnection("jdbc:awsathena://athena.eu-west-1.amazonaws.com:443/", info);
  	} catch (ClassNotFoundException | SQLException e) {
		throw new RuntimeException(e);
	}
  }
  public ArrayList<DHT> search(Long start,Long end){
	  try {
		  java.sql.Timestamp startd = new java.sql.Timestamp(start);
		  java.sql.Timestamp endd = new java.sql.Timestamp(end);
		  String sql = "SELECT temperature,humidity,timestamp FROM sampledb.dht where timestamp between timestamp'"+startd.toString()+"'  and timestamp'"+endd.toString()+"'";
		  Statement prepStat = this.connection.createStatement();
		  ResultSet resultSet=prepStat.executeQuery(sql);
		  ArrayList<DHT> readings=new ArrayList<DHT>();
	      while (resultSet.next()) {
	          readings.add(new DHT(resultSet.getDouble(1),resultSet.getDouble(2),resultSet.getTimestamp(3)));
	      }
	      return readings;
	  } catch (SQLException e) {
		 this.dispose();
		 throw new RuntimeException(e);
	
	  }
	  
  }
  public void dispose() {
	  try {
		this.connection.close();
	} catch (SQLException e) {
		throw new RuntimeException(e);

	}
  }


}