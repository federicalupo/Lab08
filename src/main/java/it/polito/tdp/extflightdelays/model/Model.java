package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;

	public Model() {
		dao = new ExtFlightDelaysDAO();
	}

	public void creaGrafo(Integer distanzaMin) {

		this.idMap = new HashMap<>();
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		Graphs.addAllVertices(this.grafo, dao.loadAllAirports());

		for (Airport a : this.grafo.vertexSet()) {
			
			idMap.put(a.getId(), a);
		}
		
		
	//ERRORE : DEVO AGGIUNGERE SOLO ARCHI LA CUI MEDIA  (PESO) > DISTANZA PASSATA!!!!
		
		/*3 approccio*///	 ===> stampa : primo vertice < secondo vertice, archi ordinati
		
		List<CoppiaAeroporti> coppie = dao.coppie(idMap);
		for(CoppiaAeroporti c : coppie) {
			if(c.getMedia() > distanzaMin)
				Graphs.addEdge(this.grafo, c.getAeroportoP(), c.getAeroportoA(), c.getMedia());
			
		}
		
		

		/* 2 approccio   => stampa a caso

		for (Airport a : this.grafo.vertexSet()) {

			List<CoppiaAeroporti> connesse = dao.connesse(a, idMap);

			for (CoppiaAeroporti c : connesse) {

				if (!this.grafo.containsEdge(c.getAeroportoA(), c.getAeroportoP()) && c.getMedia() > distanzaMin) { //CONDIZIONE
					Graphs.addEdge(this.grafo, c.getAeroportoA(), c.getAeroportoP(), c.getMedia());
				}

			}

		}*/
		

		/* 1 approccio    ===> stampa : primo vertice < secondo vertice, archi ordinati
		  for (Airport a1 : this.grafo.vertexSet())  { 
			  for (Airport a2 : this.grafo.vertexSet()) {
		  
				  if (a1.getId() < a2.getId()) {
					  //if(!this.grafo.containsEdge(a1, a2)) { uguale
					  double peso = this.dao.getPeso(a1, a2);
		  
					  if (peso > 0 && peso> distanzaMin) { // aggiungi arco
						  	Graphs.addEdge(this.grafo, a1, a2, peso);
					  		}
		          	} 
		      }
		   } */
	   

	}

	public int nVertici() {
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public String stampaArchiDistanza(){
		
		String s="";
		
		for( DefaultWeightedEdge d: this.grafo.edgeSet()) {
			
			s+= String.format("Partenza: %s   -->   Arrivo: %s    Distanza: %g\n", this.grafo.getEdgeSource(d), grafo.getEdgeTarget(d), this.grafo.getEdgeWeight(d));	
		}
	
		
		return s;
		
	}
	
}
