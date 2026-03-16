package ville;

import core.*;
import java.util.*;

public class PolitiquePCC implements Politique{

	private LinkedList<String> chemin;
	private String but;
	private Ville villeConnue;
	private Set<String> lieuxFermesConnus;
	private boolean doitPoserMessage;
	private MessageRouteFerme messageAPropager;

	public PolitiquePCC(String but, Ville villeConnue){
		this.chemin = new LinkedList<String>();
		this.but = but;
		this.villeConnue = villeConnue;
		this.lieuxFermesConnus = new HashSet<String>();
		this.doitPoserMessage = false;
		this.messageAPropager = null;
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

			// Si agent au but alors rien faire et attendre
			if(agentVille.getPosition().equals(this.but)){
				return new ActionRester();
			}

			// Si on doit poser un message alors on le pose et on attend le tour suivant pour recalculer le chemin
			if(this.doitPoserMessage && this.messageAPropager != null){
				this.doitPoserMessage = false;
				return new ActionMessage(this.messageAPropager);
			}

			// Parcourir l'historique pour trouver les routes fermer
			boolean recalculer = false;
			for(Map.Entry<Action, Observation> entree : historique.getListe()){
				ObservationVille obs = (ObservationVille) entree.getValue();
				for(Message m : obs.getMessage()){
					if(m instanceof MessageRouteFerme routeFerme){
						if(!this.lieuxFermesConnus.contains(routeFerme.getName())){
							this.lieuxFermesConnus.add(routeFerme.getName());
							this.villeConnue.fermerLieu(routeFerme.getName());
							this.doitPoserMessage = true;
							this.messageAPropager = routeFerme;
							recalculer = true;
						}
					}
				}

			}
			if(recalculer){
				this.chemin.clear();
			}

			if(this.chemin.isEmpty()){
				this.pcc(agentVille);

			}
			String destination = this.chemin.peek();
			boolean accessible = false;
			for(Action a : actionsPossibles){
				if(a instanceof ActionDeplacement ad && ad.destination().equals(destination)){
					accessible = true;
					break;
				}
			}
			if(destination != null && accessible){
				this.chemin.poll();
				return new ActionDeplacement(destination);
			}
			// Destination pas accessible ce tour → on attend
			return new ActionRester();
		}
		return null;
	}
	
	// Algorithme BFS
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
				if(!visites.contains(voisin) && this.villeConnue.estOuvert(voisin)){
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

    		while (!courant.equals(depart)) {
       			this.chemin.addFirst(courant);
        		courant = predecesseurs.get(courant);
    		}
	}
	

}
