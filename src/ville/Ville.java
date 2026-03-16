package ville;

import java.util.*;

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

    public boolean estOuvert(String lieu) {
        return !lieuxFermes.contains(lieu);
    }

    public List<String> getVoisins(String lieu) {
        return adjacences.getOrDefault(lieu, Collections.emptyList());
    }

    public Set<String> getLieux() {
        return adjacences.keySet();
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
