package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {

		private Graph<Player, DefaultWeightedEdge>grafo;
		private List<Player>allPlayers;
		private PremierLeagueDAO dao;
		private Integer nMaxPlayersBattuti;
		//private Map<String, People>idMap;
		
		
		public Model() {
			this.allPlayers = new ArrayList<>();
			this.dao = new PremierLeagueDAO();
			this.nMaxPlayersBattuti = 0;
//			this.idMap = new HashMap<>();	
			
		}
		
		public String creaGrafo(Double nMinGoals) {
			
			this.grafo = new SimpleDirectedWeightedGraph<Player, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			
			this.allPlayers = dao.readAllPlayers(nMinGoals);
			Graphs.addAllVertices(grafo, this.allPlayers);
			
			for(Player x : allPlayers) {
				for(Player y : allPlayers) {
					if(dao.getNumeroScontriTitolari(x, y) > 0) {
						Integer peso = dao.getWeight(x, y);
						if(peso > 0) {
							Graphs.addEdge(grafo, x, y, peso);
						}else if(peso < 0) {
							Graphs.addEdge(grafo, y, x, -peso);
						}
					}
				}
			}
			
			return ("Grafo creato con "+grafo.vertexSet().size()+" vertici e "+grafo.edgeSet().size()+" archi");
		}
		
		public Player topPlayer() {
			Player p = null;
			for(Player x : grafo.vertexSet()) {
				Integer n = this.getPlayersBattuti(x).size();
				if(n > this.nMaxPlayersBattuti) {
					this.nMaxPlayersBattuti = n;
					p = x;
				}
			}
			
			return p;
		}
		
		public List<PlayerDelta> getPlayersBattuti(Player a){
			List<PlayerDelta>playersBattuti = new ArrayList<>();
			List<DefaultWeightedEdge>battuti = new ArrayList<>(this.grafo.outgoingEdgesOf(a));
			
			for(DefaultWeightedEdge x : battuti) {
				Player p = grafo.getEdgeTarget(x);
				Integer delta = (int) grafo.getEdgeWeight(x);
				PlayerDelta d = new PlayerDelta(p, delta); 
				playersBattuti.add(d);
			}
			Collections.sort(playersBattuti);
			return playersBattuti;
		}

		public Graph<Player, DefaultWeightedEdge> getGrafo() {
			return grafo;
		}

		public List<Player> getAllPlayers() {
			return allPlayers;
		}

		public Integer getnMaxPlayersBattuti() {
			return nMaxPlayersBattuti;
		}
		
}
