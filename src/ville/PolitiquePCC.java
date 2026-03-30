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

	private Map<Agent, List<Set<String>>> historiquePositions;
	private Map<Agent, List<StepContrainte>> contraintes;
	private int nbObsTraitees;

	private static class StepContrainte {
		String positionSur;
		Set<String> positionsExclues;

		StepContrainte() {
			this.positionSur = null;
			this.positionsExclues = new HashSet<>();
		}
	}

	public PolitiquePCC(String but, Ville villeConnue){
		this.chemin = new LinkedList<>();
		this.but = but;
		this.villeConnue = villeConnue;
		this.lieuxFermesConnus = new HashSet<>();
		this.doitPoserMessage = false;
		this.messageAPropager = null;
		this.lieuDepotMessage = null;
		this.historiquePositions = new HashMap<>();
		this.contraintes = new HashMap<>();
		this.nbObsTraitees = 0;
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


	public Map<Agent, Set<String>> getPositionsPossibles(){
		Map<Agent, Set<String>> result = new HashMap<>();
		for(Map.Entry<Agent, List<Set<String>>> entry : historiquePositions.entrySet()){
			List<Set<String>> hist = entry.getValue();
			if(!hist.isEmpty()){
				result.put(entry.getKey(), hist.get(hist.size() - 1));
			}
		}
		return result;
	}

	// Calcule le set de positions possibles pour un agent à un step donné
	private Set<String> calculerStep(Agent autreAgent, int stepIndex){
		List<Set<String>> hist = historiquePositions.get(autreAgent);
		Set<String> prev = (stepIndex == 0 || hist.get(stepIndex - 1).isEmpty())
				? new HashSet<>(villeConnue.getLieux())
				: hist.get(stepIndex - 1);

		StepContrainte c = contraintes.get(autreAgent).get(stepIndex);

		if(c.positionSur != null) return new HashSet<>(Set.of(c.positionSur));

		Set<String> predicted = new HashSet<>();
		for(String pos : prev) predicted.add(nextHop(pos));

		Set<String> apresElimination = new HashSet<>(predicted);
		apresElimination.removeAll(c.positionsExclues);
		return apresElimination.isEmpty() ? predicted : apresElimination;
	}

	private void repropagarDepuis(Agent autreAgent, int stepDebut){
		List<Set<String>> hist = historiquePositions.get(autreAgent);
		for(int s = stepDebut; s < hist.size(); s++) hist.set(s, calculerStep(autreAgent, s));
	}

	private void reviserRouteBloquee(String lieuBloque){
		for(Agent autreAgent : historiquePositions.keySet()){
			List<Set<String>> hist = historiquePositions.get(autreAgent);
			for(int s = 0; s < hist.size(); s++){
				if(hist.get(s).contains(lieuBloque)){
					repropagarDepuis(autreAgent, s);
					break;
				}
			}
		}
	}

	private void mettreAJourSuivi(AgentVille agentVille, Historique historique){
		List<Map.Entry<Action, Observation>> liste = historique.getListe();
		String maPosition = agentVille.getPosition();

		for(int i = nbObsTraitees; i < liste.size(); i++){
			ObservationVille obs = (ObservationVille) liste.get(i).getValue();

			Map<Agent, String> agentsVus = new HashMap<>();
			for(Message m : obs.getMessage())
				if(m instanceof MessageAgentPresent map)
					agentsVus.put(map.getAgent(), map.getName());

			Set<Agent> tousAgents = new HashSet<>(historiquePositions.keySet());
			tousAgents.addAll(agentsVus.keySet());

			for(Agent autreAgent : tousAgents){
				historiquePositions.computeIfAbsent(autreAgent, k -> new ArrayList<>());
				contraintes.computeIfAbsent(autreAgent, k -> new ArrayList<>());

				StepContrainte c = new StepContrainte();
				if(agentsVus.containsKey(autreAgent)) c.positionSur = agentsVus.get(autreAgent);
				else c.positionsExclues.add(maPosition);

				contraintes.get(autreAgent).add(c);
				historiquePositions.get(autreAgent).add(calculerStep(autreAgent, contraintes.get(autreAgent).size() - 1));
			}
		}

		nbObsTraitees = liste.size();

		Map<Agent, Set<String>> current = getPositionsPossibles();
		if(!current.isEmpty()){
			System.out.println("  [Suivi " + agentVille + "] Positions possibles des autres agents :");
			for(Map.Entry<Agent, Set<String>> entry : current.entrySet())
				System.out.println("    " + entry.getKey() + " -> " + entry.getValue());
		}
	}
	//drop messaege sur chemin des autres agents
	private String calculerMeilleurDepot(String maPosition){
		Set<String> monChemin = new HashSet<>(this.chemin);
		monChemin.add(maPosition);

		String meilleurDepot = null;
		int meilleurRang = Integer.MAX_VALUE;

		for(Set<String> possPossibles : getPositionsPossibles().values()){
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

			mettreAJourSuivi(agentVille, historique);

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
						reviserRouteBloquee(routeFerme.getName());

						String depot = calculerMeilleurDepot(agentVille.getPosition());
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
