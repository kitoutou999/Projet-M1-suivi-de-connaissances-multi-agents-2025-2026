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
	private String lieuDepotMessage;

	public PolitiquePCC(String but, Ville villeConnue){
		this.chemin = new LinkedList<>();
		this.but = but;
		this.villeConnue = villeConnue;
		this.lieuxFermesConnus = new HashSet<>();
		this.doitPoserMessage = false;
		this.messageAPropager = null;
		this.lieuDepotMessage = null;
	}

	// BFS
	private Map<String, String> bfs(String from){
		if(from.equals(this.but)) return new HashMap<>();
		Map<String, String> predecesseurs = new HashMap<>();
		LinkedList<String> noeuds = new LinkedList<>();
		Set<String> visites = new HashSet<>();
		noeuds.add(from);
		visites.add(from);
		while(!noeuds.isEmpty()){
			String n = noeuds.poll();
			for(String voisin : villeConnue.getVoisins(n)){
				if(!visites.contains(voisin) && villeConnue.estOuvert(voisin)){
					predecesseurs.put(voisin, n);
					if(voisin.equals(this.but)) return predecesseurs;
					visites.add(voisin);
					noeuds.add(voisin);
				}
			}
		}
		return null; // pas de chemin
	}

	// chemin complet
	private LinkedList<String> cheminDepuis(String from){
		Map<String, String> pred = bfs(from);
		LinkedList<String> path = new LinkedList<>();
		if(pred == null) return path;
		if(pred.isEmpty()) return path;
		String courant = this.but;
		while(!courant.equals(from)){
			path.addFirst(courant);
			courant = pred.get(courant);
		}
		return path;
	}

	private String nextHop(String from){
		LinkedList<String> path = cheminDepuis(from);
		return path.isEmpty() ? from : path.getFirst();
	}


	private Map<Agent, Set<String>> getPositionsPossibles(AgentVille agent){
		core.BeliefState<?> bs = agent.getBeliefState();
		if(bs == null) return Map.of();
		Map<Agent, Set<String>> result = new HashMap<>();
		for(Object o : bs.getEtatsCompatibles()){
			EtatRDV e = (EtatRDV) o;
			e.getPositions().forEach((a, pos) -> {
				if(!a.equals(agent))
					result.computeIfAbsent(a, k -> new HashSet<>()).add(pos);
			});
		}
		return result;
	}
	//drop messaege sur chemin des autres agents
	private String calculerMeilleurDepot(AgentVille agent, String maPosition){
		Set<String> monChemin = new HashSet<>(this.chemin);
		monChemin.add(maPosition);

		String meilleurDepot = null;
		int meilleurRang = Integer.MAX_VALUE;

		for(Set<String> possPossibles : getPositionsPossibles(agent).values()){
			for(String pos : possPossibles){
				LinkedList<String> cheminAgent = cheminDepuis(pos);
				for(int i = 0; i < cheminAgent.size(); i++){
					if(monChemin.contains(cheminAgent.get(i)) && i < meilleurRang){
						meilleurRang = i;
						meilleurDepot = cheminAgent.get(i);
						break;
					}
				}
			}
		}
		return meilleurDepot;
	}

	public Action chooseAction(Agent agent, Historique historique, List<Action> actionsPossibles){
		if(agent instanceof AgentVille agentVille){

			System.out.println("Position de " + agentVille + " : " + agentVille.getPosition());

			// Afficher les positions possibles issues du BeliefState
			Map<Agent, Set<String>> posPossibles = getPositionsPossibles(agentVille);
			if(!posPossibles.isEmpty()){
				System.out.println("  [BeliefState " + agentVille + "] Positions possibles des autres agents :");

				for (Map.Entry<Agent, Set<String>> entry : posPossibles.entrySet())
						System.out.println("    " + entry.getKey() + " -> " + entry.getValue());
			}

			if(agentVille.getPosition().equals(this.but)) return new ActionRester();

			List<Map.Entry<Action, Observation>> liste = historique.getListe();
			boolean recalculer = false;
			if(!liste.isEmpty()){
				Map.Entry<Action, Observation> entree = liste.get(liste.size() - 1);
				for(Message m : ((ObservationVille) entree.getValue()).getMessage()){
					if(m instanceof MessageRouteFerme routeFerme && !lieuxFermesConnus.contains(routeFerme.getName())){
						lieuxFermesConnus.add(routeFerme.getName());
						villeConnue.fermerLieu(routeFerme.getName());
						messageAPropager = routeFerme;
						recalculer = true;

						String depot = calculerMeilleurDepot(agentVille, agentVille.getPosition());
						lieuDepotMessage = depot != null ? depot : agentVille.getPosition();
						doitPoserMessage = true;

						if (depot != null ){
							System.out.println("  [Dépôt stratégique] " + agentVille + " déposera le message à " + depot);
						}else{
							System.out.println("  [Dépôt immédiat] " + agentVille + " dépose le message ici");
						}

					}
				}
			}
			if(recalculer) chemin.clear();

			// Dépot ici
			if(doitPoserMessage && agentVille.getPosition().equals(lieuDepotMessage)){
				doitPoserMessage = false;
				lieuDepotMessage = null;
				return new ActionMessage(messageAPropager);
			}

			if(chemin.isEmpty()) chemin = cheminDepuis(agentVille.getPosition());

			String destination = chemin.peek();
			for(Action a : actionsPossibles){
				if(a instanceof ActionDeplacement ad && ad.destination().equals(destination)){
					chemin.poll();
					return new ActionDeplacement(destination);
				}
			}
			return new ActionRester();
		}
		return null;
	}
}
