package ville;

import core.*;
import java.util.*;

/**
 * Politique du plus court chemin (BFS) vers le lieu de rendez-vous.
 * Gère également la propagation de messages sur les routes fermées
 * et le raisonnement de second ordre pour décider si un message est utile aux autres agents.
 */
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

	// Second ordre : vérifie si au moins un autre agent a besoin de l'info et ne la connait pas déjà.
	private boolean autreAgentABesoinInfo(AgentVille moi, String routeFermee) {
		BeliefState<EtatRDV> bs = moi.getBeliefState();
		if (bs == null || bs.getEtatsCompatibles().isEmpty()) return true;

		villeConnue.ouvrirLieu(routeFermee);
		Map<Agent<?>, Boolean> besoinParAgent = new LinkedHashMap<>();
		for (EtatRDV etat : bs.getEtatsCompatibles()) {
			for (Map.Entry<Agent<?>, String> entry : etat.getPositions().entrySet()) {
				Agent<?> autreAgent = entry.getKey();
				if (autreAgent.equals(moi)) continue;
				boolean cheminPassePar = cheminDepuis(entry.getValue()).contains(routeFermee);
				boolean dejaInforme = etat.getRoutesConnues(autreAgent).contains(routeFermee);
				if (cheminPassePar && !dejaInforme)
					besoinParAgent.put(autreAgent, true);
				else
					besoinParAgent.putIfAbsent(autreAgent, false);
			}
		}
		villeConnue.fermerLieu(routeFermee);

		boolean besoinGlobal = false;
		for (Map.Entry<Agent<?>, Boolean> e : besoinParAgent.entrySet()) {
			boolean besoin = e.getValue();
			if (besoin) besoinGlobal = true;
			System.out.println("  [2nd ordre] " + moi.getId() + " : "
				+ e.getKey().getId()
				+ (besoin ? " a besoin" : " n'a PAS besoin (déjà informé)")
				+ " du message sur " + routeFermee);
		}
		return besoinGlobal;
	}

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

	private Map<Agent<?>, Set<String>> getPositionsPossibles(AgentVille agent){
		core.BeliefState<EtatRDV> bs = agent.getBeliefState();
		if(bs == null) return Map.of();
		Map<Agent<?>, Set<String>> result = new HashMap<>();
		for(EtatRDV e : bs.getEtatsCompatibles()){
			e.getPositions().forEach((a, pos) -> {
				if(!a.equals(agent))
					result.computeIfAbsent(a, k -> new HashSet<>()).add(pos);
			});
		}
		return result;
	}
	private String calculerMeilleurDepot(AgentVille agent, String maPosition){
		// Si agent sur ma position -> poster ici immédiatement
		for(Set<String> possPossibles : getPositionsPossibles(agent).values()){
			if(possPossibles.contains(maPosition)) return maPosition;
		}

		// Sinon chercher l'intersection d'un autres agents
		Set<String> monChemin = new HashSet<>(cheminDepuis(maPosition));
		monChemin.add(maPosition);
		monChemin.remove(this.but); // Le RDV n'est pas un bon dépôt

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

	public Action chooseAction(Agent<?> agent, Historique historique, List<Action> actionsPossibles){
		if(!(agent instanceof AgentVille agentVille)) return null;

		// Mise a jour des routes fermees
		List<Map.Entry<Action, Observation>> liste = historique.getListe();
		boolean recalculer = false;
		if(!liste.isEmpty()){
			Map.Entry<Action, Observation> entree = liste.get(liste.size() - 1);
			Action derniereAction = entree.getKey();

			for(Message m : ((ObservationVille) entree.getValue()).getMessage()){
				if(m instanceof MessageAgentBloque mab && !lieuxFermesConnus.contains(mab.getDestination())){
					lieuxFermesConnus.add(mab.getDestination());
					villeConnue.fermerLieu(mab.getDestination());
					recalculer = true;
					// Observé chez un colocataire -> on sait, mais pas besoin de relayer
				}
				if(m instanceof MessageRouteFerme routeFerme && !lieuxFermesConnus.contains(routeFerme.getName())){
					lieuxFermesConnus.add(routeFerme.getName());
					villeConnue.fermerLieu(routeFerme.getName());
					recalculer = true;

					// Ne met le msg que si l'agent a lui meme essayé d'aller sur cette route
					boolean aEssayeRoute = derniereAction instanceof ActionDeplacement ad && ad.destination().equals(routeFerme.getName());

					if (aEssayeRoute) {
						messageAPropager = routeFerme;
						if (autreAgentABesoinInfo(agentVille, routeFerme.getName())) {
							String depot = calculerMeilleurDepot(agentVille, agentVille.getPosition());
							lieuDepotMessage = depot != null ? depot : agentVille.getPosition();
							doitPoserMessage = true;
						} else {
							System.out.println("  " + agentVille.getId() + " : les autres agents ont pas besoin du message sur " + routeFerme.getName() + " (second ordre)");
						}
					}
				}
			}
		}
		if(recalculer) chemin.clear();

		// Choix de l'action
		Action action;
		if(agentVille.getPosition().equals(this.but)){
			action = new ActionRester();
		} else if(doitPoserMessage && agentVille.getPosition().equals(lieuDepotMessage)){
			doitPoserMessage = false;
			lieuDepotMessage = null;
			action = new ActionMessage(messageAPropager);
		} else {
			if(chemin.isEmpty()) chemin = cheminDepuis(agentVille.getPosition());
			String destination = chemin.peek();
			action = new ActionRester();
			for(Action a : actionsPossibles){
				if(a instanceof ActionDeplacement ad && ad.destination().equals(destination)){
					chemin.poll();
					action = new ActionDeplacement(destination);
					break;
				}
			}
		}

		// Affichage
		String beliefsStr = "";
		Map<Agent<?>, Set<String>> posPossibles = getPositionsPossibles(agentVille);
		for(Map.Entry<Agent<?>, Set<String>> e : posPossibles.entrySet()){
			beliefsStr += " " + e.getKey().getId() + "->" + e.getValue();
		}
		String ligne = "  " + agentVille.getId() + " (" + agentVille.getPosition() + ")";
		if(!beliefsStr.isEmpty()){
			ligne += "  \033[1mbeliefs:\033[0m" + beliefsStr;
		}
		ligne += "  => " + action;
		System.out.println(ligne);

		return action;
	}
}
