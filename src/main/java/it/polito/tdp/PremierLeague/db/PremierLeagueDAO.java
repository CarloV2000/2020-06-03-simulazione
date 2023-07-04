package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Player> readAllPlayers(Double nMinGoals) {
		String sql = "SELECT  p.*, COUNT(DISTINCT a.MatchID)AS partiteGiocate, SUM(a.Goals)AS goals \n"
				+ "FROM players p, actions a "
				+ "WHERE a.PlayerID = p.PlayerID "
				+ "GROUP BY a.PlayerID "
				+ "HAVING goals/partiteGiocate > ? "
				+ " ";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, nMinGoals);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Integer getNumeroScontriTitolari(Player x, Player y) {
		String sql = "SELECT COUNT(a1.MatchID)AS n "
				+ "FROM actions a1, actions a2, matches m1, matches m2 "
				+ "WHERE a1.PlayerID = ? AND a2.PlayerID = ? "
				+ "AND a1.TeamID != a2.TeamID AND a1.starts = 1 AND a2.starts = 1 "
				+ "AND m1.MatchID = a1.MatchID AND m2.MatchID = a2.MatchID AND m1.MatchID = m2.MatchID ";
		Integer n = 0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, x.getPlayerID());
			st.setInt(2, y.getPlayerID());
			ResultSet res = st.executeQuery();
			
			if (res.first()) {
				n = res.getInt("n");				
			}
			conn.close();
			return n;
			
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Integer getWeight(Player x, Player y) {
		String sql = "SELECT SUM(a1.TimePlayed- a2.TimePlayed)AS peso "
				+ "FROM actions a1, actions a2, matches m1, matches m2 "
				+ "WHERE a1.PlayerID = ? AND a2.PlayerID = ? "
				+ "AND a1.TeamID != a2.TeamID AND a1.starts = 1 AND a2.starts = 1 "
				+ "AND m1.MatchID = a1.MatchID AND m2.MatchID = a2.MatchID AND m1.MatchID = m2.MatchID ";
		Integer n = 0;
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, x.getPlayerID());
			st.setInt(2, y.getPlayerID());
			ResultSet res = st.executeQuery();
			if (res.first()) {
				n = res.getInt("peso");				
			}
			conn.close();
			return n;
			
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
