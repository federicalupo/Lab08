package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.CoppiaAeroporti;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public double getPeso(Airport a1, Airport a2) {

		String sql = "select avg(distance) as media " + "from  flights "
				+ "where (flights.`ORIGIN_AIRPORT_ID` = ? AND flights.`DESTINATION_AIRPORT_ID`=?) or "
				+ "(flights.`ORIGIN_AIRPORT_ID` = ? and flights.`DESTINATION_AIRPORT_ID`=?)";

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a1.getId());
			st.setInt(2, a2.getId());
			st.setInt(3, a2.getId());
			st.setInt(4, a1.getId());

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				double media = rs.getDouble("media"); // se non ci sono => avg = null, ma viene interpretato come 0

				conn.close();
				return media;

			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");

		}

		return -1;

	}

	public List<CoppiaAeroporti> connesse(Airport a1, Map<Integer, Airport> idMap) {

		String sql = "select flights.`ORIGIN_AIRPORT_ID`, flights.`DESTINATION_AIRPORT_ID`,  avg(distance) as media "
				+ "from  flights "
				+ "where (flights.`ORIGIN_AIRPORT_ID` = ? ) or ( flights.`DESTINATION_AIRPORT_ID`=?) "
				+ "group by flights.`ORIGIN_AIRPORT_ID`, flights.`DESTINATION_AIRPORT_ID`"; 
		// non posso fare come approccio 3 che ordino perchè => se metto 0 allora sarà il primo che vedo
		//se metto 93, prima vedrò gli id di partenza <
		//devo fissare vertice partenza = vertice passato 
		
		List<CoppiaAeroporti> lista = new LinkedList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a1.getId());
			st.setInt(2, a1.getId());

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				int idP = rs.getInt("ORIGIN_AIRPORT_ID");
				int idA = rs.getInt("DESTINATION_AIRPORT_ID");
				double media = rs.getDouble("media");

				if (idP == a1.getId()) {
					CoppiaAeroporti c = new CoppiaAeroporti(idMap.get(idP), idMap.get(idA), media);
					if (!lista.contains(c)) {
						lista.add(c);
					} else {
						CoppiaAeroporti temp = lista.get(lista.indexOf(c));
						temp.aggiornaMedia(media);
					}

				} else {
					CoppiaAeroporti c = new CoppiaAeroporti(idMap.get(idA),idMap.get(idP), media);

					if (!lista.contains(c)) {
						lista.add(c);
					} else {
						CoppiaAeroporti temp = lista.get(lista.indexOf(c));
						temp.aggiornaMedia(media);
					}

				}

			}

			conn.close();
			return lista;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");

		}

	}
	
	
	
	
	public List<CoppiaAeroporti> coppie(Map<Integer, Airport> idMap){
		
		String sql = "select flights.`ORIGIN_AIRPORT_ID`, flights.`DESTINATION_AIRPORT_ID`,  avg(distance) as media " + 
				"from  flights " + 
				"group by flights.`ORIGIN_AIRPORT_ID`, flights.`DESTINATION_AIRPORT_ID` " + 
				"order by flights.origin_airport_id asc";
		
		
		List<CoppiaAeroporti> lista = new LinkedList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				int idP = rs.getInt("ORIGIN_AIRPORT_ID");
				int idA = rs.getInt("DESTINATION_AIRPORT_ID");
				double media = rs.getDouble("media");

				if(idP<idA) {
					
					CoppiaAeroporti c = new CoppiaAeroporti(idMap.get(idP),idMap.get(idA), media);
					lista.add(c);
				
				}else {
					//1. quando idP > idA, si trova un oggetto (idA, idP) in lista??? Non è detto!!! => penso a caso critico => 
					// caso in cui idP > idA e oggetto non in lista: 
					// c'è una sola andata, 40 => 3 quindi idP> idA, non è salvato in lista perchè 3=> 40 non esiste
					//Può capitare nel nostro caso? si => quindi controllo
					
					CoppiaAeroporti c= new CoppiaAeroporti(idMap.get(idA), idMap.get(idP), media);
					if(lista.contains(c)) {
						
						lista.get(lista.indexOf(c)).aggiornaMedia(media);
					}else {
		
						lista.add(c);
						
					}
		
		
					
				}


			}

			
			conn.close();
			return lista;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");

		}

	
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
