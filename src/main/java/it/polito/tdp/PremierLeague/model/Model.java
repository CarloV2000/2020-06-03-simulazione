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
		
		private List<Player> migliore;
		private double maxTitolarita;
		
		
		public Model() {
			this.allPlayers = new ArrayList<>();
			this.dao = new PremierLeagueDAO();
			this.nMaxPlayersBattuti = 0;
			
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
		
		/**
		 * Metodo che calcola il Dream Team
		 */
		public void  calcolaDreamTeam(Integer dimensioneTeam) {
			this.maxTitolarita = 0.0;
			this.migliore = new ArrayList<Player>();
			List<Player>rimanenti = new ArrayList<>(this.grafo.vertexSet());
			List<Player>parziale = new ArrayList<>();
			
			ricorsione(0, parziale, rimanenti, dimensioneTeam, 0);
		}
		
		
		
		/**
		 * La ricorsione vera e propria
		 * @param parziale
		 * @param rimanenti
		 */
		private void ricorsione(Integer livello, List<Player> parziale, List<Player> rimanenti, Integer dimensioneTeam, Integer titolaritaParziale){
			// Condizione Terminale
			if (rimanenti.isEmpty()) {
				
				if (titolaritaParziale > this.maxTitolarita && livello == dimensioneTeam) {
					this.maxTitolarita = titolaritaParziale;
					this.migliore = new ArrayList<>(parziale);
				}
				return;
			}
			
	       	for (Player p : rimanenti) {
	 			List<Player> currentRimanenti = new ArrayList<>(rimanenti);
	 			parziale.add(p);
	 			if(parziale.size() >= dimensioneTeam) {//se mi fa uscire dal vincolo lo scarto
	 				parziale.remove(parziale.size()-1);
	 				break;
	 			}
	 			List<Player>battuti = new ArrayList<>();
	 			for(PlayerDelta x : this.getPlayersBattuti(p)) {
	 				battuti.add(x.getP());
	 			}
	 			currentRimanenti.removeAll(battuti);
	 			currentRimanenti.remove(p);
	 			ricorsione(livello+1, parziale, currentRimanenti, dimensioneTeam, (int)(titolaritaParziale+this.getTitolarita(p)));
	 			parziale.remove(parziale.size()-1);
	 			
	       	}
						
		}
		
	
		private double getTitolarita(Player a) {
			double titolarita = 0.0;
			List<DefaultWeightedEdge>entranti = new ArrayList<>(this.grafo.incomingEdgesOf(a));
			List<DefaultWeightedEdge>uscenti = new ArrayList<>(this.grafo.outgoingEdgesOf(a));
			
			for(DefaultWeightedEdge x : entranti) {
				titolarita += this.grafo.getEdgeWeight(x);
			}
			for(DefaultWeightedEdge x : uscenti) {
				titolarita -= this.grafo.getEdgeWeight(x);
			}
			return titolarita;
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

		public List<Player> getMigliore() {
			return migliore;
		}

		public double getMaxTitolarita() {
			return maxTitolarita;
		}
		
}
