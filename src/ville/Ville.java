package ville;

import java.util.*;

/**
 * Graphe non orienté représentant la carte de la ville.
 * Stocke les adjacences entre lieux et l'état ouvert/fermé de chacun.
 */
public class Ville {
    private final Map<String, List<String>> adjacences = new HashMap<>();
    private final Set<String> lieuxFermes = new HashSet<>();

    public void ajouterRoute(String lieu1, String lieu2) {
        adjacences.computeIfAbsent(lieu1, k -> new ArrayList<>()).add(lieu2);
        adjacences.computeIfAbsent(lieu2, k -> new ArrayList<>()).add(lieu1);
    }

    public void fermerLieu(String lieu) {
        lieuxFermes.add(lieu);
    }

    public void ouvrirLieu(String lieu) {
        lieuxFermes.remove(lieu);
    }

    public boolean estOuvert(String lieu) {
        return !lieuxFermes.contains(lieu);
    }

    public List<String> getVoisins(String lieu) {
        return adjacences.getOrDefault(lieu, Collections.emptyList());
    }

    public Set<String> getLieux() {
        return adjacences.keySet();
    }


    /** Génère une ville composée de deux arbres binaires reliés par des ponts entre leurs feuilles. */
    public static Ville genererDeuxArbres(int profondeur, int nbPonts) {
        Ville ville = new Ville();
        int nbNoeuds = (int) Math.pow(2, profondeur) - 1;

        //deux arbres
        for(int i = 0; i < nbNoeuds; i++){
            int gauche = 2 * i + 1;
            int droite = 2 * i + 2;
            if(gauche < nbNoeuds) ville.ajouterRoute("A" + i, "A" + gauche);
            if(droite < nbNoeuds) ville.ajouterRoute("A" + i, "A" + droite);
            if(gauche < nbNoeuds) ville.ajouterRoute("B" + i, "B" + gauche);
            if(droite < nbNoeuds) ville.ajouterRoute("B" + i, "B" + droite);
        }
        int premiereFeuilleA = (int) Math.pow(2, profondeur - 1) - 1;
        for(int i = 0; i < nbPonts; i++){
            ville.ajouterRoute("A" + (premiereFeuilleA + i), "B" + (premiereFeuilleA + i));
        }

        return ville;
    }

    public Ville copierVille(){
    	Ville copie = new Ville();
    	for(String lieu : this.getLieux()){
    		for(String voisin : adjacences.get(lieu)){
    			copie.ajouterRoute(lieu, voisin);
    		}
    	}
    	return copie; 
    }
}
