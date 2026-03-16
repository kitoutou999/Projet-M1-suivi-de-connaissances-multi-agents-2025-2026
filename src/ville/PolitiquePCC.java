package ville;

import core.*;
import java.util.*;

public class PolitiquePCC implements Politique{

	private LinkedList<String> chemin;
	private String but;
	private Ville villeConnue;

	public PolitiquePCC(String but, Ville villeConnue){
		this.chemin = new LinkedList<String>(); // Plus court chemin actuel
		this.but = but; // Lieu de RDV
		this.villeConnue = villeConnue; //Ce que l'agent connait de la ville
	}
	
	public LinkedList<String> getChemin(){
		return this.chemin;
	}
	
	public String getBut(){
		return this.but;
	}
	
	public void setBut(String newBut){
		this.but = newBut;
	}
	
	public Ville getVilleConnue(){
		return this.villeConnue;
	}
	
	public void setVilleConnue(Ville newVille){
		this.villeConnue = newVille;
	} 

	public Action chooseAction(Agent agent, Historique historique, List<Action> actionsPossibles){
		if(agent instanceof AgentVille agentVille){
			System.out.println("Position de " + agentVille + " : " + agentVille.getPosition());
			if(agentVille.getPosition() == this.but){
				return new ActionRester();
			}
			if(this.chemin.size() == 0){
				this.pcc(agentVille);
			}
			String destination = this.chemin.poll();
			if (destination != null){
				return new ActionDeplacement(destination);
			}
			return new ActionRester();
		}
		return null;
	}
	
	// Algorithme de Dijkstra
	public void pcc(AgentVille agentVille){
		HashMap<String, Integer> distance = new HashMap<String, Integer>();
		HashMap<String, String> predecesseurs = new HashMap<String, String>();
		LinkedList<String> noeuds = new LinkedList<String>();
		HashSet<String> visites = new HashSet<String>();
		String depart = agentVille.getPosition();
		distance.put(depart, 0);
		noeuds.add(depart);
		while(noeuds.size() != 0){
			String n = noeuds.poll();
			if(n.equals(this.but)){
				this.reconstruireChemin(predecesseurs, depart);
			}
			visites.add(n);
			for(String voisin : this.villeConnue.getVoisins(n)){
				if(!visites.contains(voisin)){
					distance.put(voisin, distance.get(n) + 1);
					predecesseurs.put(voisin, n);
					noeuds.add(voisin);
				}
			}
		}
	}
	
	
	//Construit le chemin utilisé par Dijkstra
	private void reconstruireChemin(HashMap<String, String> predecesseurs, String depart) {
    		String courant = this.but;

    		while (courant != depart) {
       			this.chemin.addFirst(courant);
        		courant = predecesseurs.get(courant);
    		}
	}
	

}
