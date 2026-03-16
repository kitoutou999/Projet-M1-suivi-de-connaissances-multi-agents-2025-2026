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


    public static Ville genererAleatoire(int nbLieux, int nbRoutesSupp, Random rand) {
        Ville ville = new Ville();
        List<String> lieux = new ArrayList<>();
        for (int i = 0; i < nbLieux; i++) {
            lieux.add("Lieu" + i);
        }

        // Arbre
        List<String> connectes = new ArrayList<>();
        connectes.add(lieux.get(0));
        List<String> restants = new ArrayList<>(lieux.subList(1, lieux.size()));
        Collections.shuffle(restants, rand);
        for (String lieu : restants) {
            String parent = connectes.get(rand.nextInt(connectes.size()));
            ville.ajouterRoute(parent, lieu);
            connectes.add(lieu);
        }

        // Routes en plus aleatoire
        for (int i = 0; i < nbRoutesSupp; i++) {
            String a = lieux.get(rand.nextInt(nbLieux));
            String b = lieux.get(rand.nextInt(nbLieux));
            if (!a.equals(b)) {
                ville.ajouterRoute(a, b);
            }
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
