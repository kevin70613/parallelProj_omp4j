import java.util.Arrays;

public class Algorithm {

    /* GA parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.01;
    private static final int tournamentSize = 5;
    //private static final boolean elitism = true;

    /* Public methods */
    
    // Evolve a population
    public static Population evolvePopulation(Population pop) {
    	
        Population newPopulation = new Population(pop.size(), false);
        
    	double [] sortArray=new double[100];
    	for(int i=0;i<pop.size();i++){
    		sortArray[i]=pop.getIndividual(i).getFitness();
    	}
    	// sorted array
    	Arrays.sort(sortArray);
    	
    	double value = sortArray[40];
    	
        for (int i = 0; i < pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            
            do{
                 indiv1 = tournamentSelection(pop);
                 indiv2 = tournamentSelection(pop);
            	
            }while(indiv1.getFitness()>value && indiv2.getFitness()>value);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.saveIndividual(i, newIndiv);
        }

        System.out.println("Threshold="+ value);
        
        // Mutate population
        for (int i = 0; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    // Crossover individuals
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }
    
    /*public static double getThreshold(Population pop,int topPercent){

    	double [] sortArray=new double[100];
    	for(int i=0;i<pop.size();i++){
    		sortArray[i]=pop.getIndividual(i).getFitness();
    		//System.out.println("a "+sortArray[i]);
    		//Arrays.sort(sortArray);
    		
    	}
    	Arrays.sort(sortArray);;
    	
    	
    	return sortArray[topPercent];
    }*/

    // Mutate an individual
    private static void mutate(Individual indiv) {
        // Loop through genes
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
                byte gene = (byte) Math.round(Math.random());
                indiv.setGene(i, gene);
            }
        }
    }

    // Select individuals for crossover
    private static Individual tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        Individual fittest = tournament.getFittest();
        return fittest;
    }
}
