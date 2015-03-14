package evolutionaryAlgorithm;

import utils.ListSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import utils.IntegerArrayWrapper;

public class Island extends Thread {

    int[] initialSequence;
    ArrayList<int[]> productions;
    ArrayList<Individual> population;
    Individual bestIndividual;
    //these two are used to insert a migrant only in the beginning of an iteration
    Individual migrant;
    AtomicBoolean addMigrant = new AtomicBoolean();

    public Island() {
    }

    public Island(int[] initialSequence, ArrayList<int[]> productions) {
        this.productions = productions;
        this.initialSequence = initialSequence;

    }

    public void run() {
        createFirstGeneration();

        try {
            bestIndividual = new Individual(productions);
            while (true) {
                select();
                //performCrossover();
                try {
                    performMutation();
                } catch (Exception e) {
                    System.err.println(e.toString());//e.printStackTrace();
                    break;
                }
                //int bestLength = Integer.MAX_VALUE;

                for (Individual i : population) {
                    if (i.length < bestIndividual.length) {
                        bestIndividual = (Individual) i.clone();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void select() {
        if (addMigrant.get()) {
            population.add(migrant);
            addMigrant.set(false);
        }

        Collections.sort(population);
        population = new ArrayList<>(population.subList(0, 40));
    }

    void performMutation() throws Exception {
        for (int i = 0; i < 20; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(population.size());
            population.get(randomIndex).mutate();
        }
    }

    void performCrossover() {
        int amountParents = population.size();
        for (int i = 0; i < amountParents / 2; i++) {
            // pick randomly two individuals as parents
            int p1 = ThreadLocalRandom.current().nextInt(amountParents);
            int p2 = ThreadLocalRandom.current().nextInt(amountParents);
            Individual parent1 = population.get(p1);
            Individual parent2 = population.get(p2);
            Individual child = crossover(parent1, parent2);
            population.add(child);
        }
    }

    private Individual crossover(Individual parent1, Individual parent2) {

        Individual mainParent, secondParent;
        if (parent1.length < parent2.length) {
            mainParent = parent1;
            secondParent = parent2;
        } else {
            mainParent = parent2;
            secondParent = parent1;
        }
        ListSet<int[]> init_productions = new ListSet<int[]>();
        init_productions.add(initialSequence);
        Individual child = new Individual(init_productions);

        ListSet<IntegerArrayWrapper> mainParent_productions = mainParent.getProductions();
        ListSet<IntegerArrayWrapper> secondParent_productions = secondParent.getProductions();

        while (child.length > secondParent.length) {
            int index = ThreadLocalRandom.current().nextInt(mainParent_productions.size());
            if (index != 0 && mainParent_productions.get(index) != null) {
                ProductionHierarchy hierarchy = mainParent.getHierarchyOfProduction(index);
                int oldLength = child.length;
                child.addProductionHierarchy(hierarchy);
                if (child.length >= oldLength) {
                    child.removeProductionHierarchy(hierarchy);
                }
            }
            index = ThreadLocalRandom.current().nextInt(mainParent_productions.size());
            if (index != 0 && mainParent_productions.get(index) != null) {
                ProductionHierarchy hierarchy = mainParent.getHierarchyOfProduction(index);
                int oldLength = child.length;
                child.addProductionHierarchy(hierarchy);
                if (child.length >= oldLength) {
                    child.removeProductionHierarchy(hierarchy);
                }
            }
            index = ThreadLocalRandom.current().nextInt(secondParent_productions.size());
            if (index != 0 && secondParent_productions.get(index) != null) {
                ProductionHierarchy hierarchy = secondParent.getHierarchyOfProduction(index);
                int oldLength = child.length;
                child.addProductionHierarchy(hierarchy);
                if (child.length >= oldLength) {
                    child.removeProductionHierarchy(hierarchy);
                }
            }
        }
        return child;
    }

    void createFirstGeneration() {
        population = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Individual indi = new Individual(productions);
            population.add(indi);
        }
    }

    void addIndivdual(Individual migrant) {
        this.migrant = migrant;
        addMigrant.set(true);
    }
}
