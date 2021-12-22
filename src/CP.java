import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;


/**
 * Les données utilisées sont ceux du donné dans l'énoncé du problème qui a été envoyer
 */

public class CP {
    public static void main(String[] args) {
        int[] maxCarsPerWindow = {2, 3, 3, 5, 5};
        int[] windowSize = {1, 2, 1, 2, 1};
        int[] numberOfCarPerClass = {3, 1, 2, 4, 3, 3, 2, 1, 1, 2, 2, 1};
        int numberOfClass = 12;
        int numberOfOption = 5;
        int numberOfCar = 25;
        int[][] optionsPerContrainte = {
                {0, 1, 0, 0, 0},
                {1, 0, 1, 0, 1},
                {1, 1, 0, 0, 0},
                {0, 1, 0, 1, 0},
                {0, 1, 0, 0, 1},
                {0, 0, 0, 1, 0},
                {1, 1, 1, 0, 0},
                {1, 0, 0, 1, 0},
                {1, 0, 1, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 1, 1, 0, 0},
                {1, 1, 0, 1, 0},
        };
        Model model = new Model();
        IntVar[] posOfClass = model.intVarArray("posOfClass", numberOfCar, 0, numberOfClass - 1);
        IntVar[][] posOfOption = model.intVarMatrix("posOfOption", numberOfOption, numberOfCar,0, 1);

        for (int k = 0; k < numberOfOption; k++) {
            for (int i = 0; i < numberOfCar - windowSize[k]; i++) {
                IntVar[] tampon = new IntVar[windowSize[k]];
                System.arraycopy(posOfOption[k], k, tampon, 0, windowSize[k]);
                model.sum(tampon, "<=", maxCarsPerWindow[k]).post();
            }
        }
        int[] cl = new int[numberOfClass];
        IntVar[] demandePerClass = new IntVar[numberOfClass];
        for (int i = 0; i < numberOfClass; i++) {
            cl[i] = i;
            demandePerClass[i] = model.intVar(numberOfCarPerClass[i]);
        }
        model.globalCardinality(posOfClass, cl, demandePerClass, true).post();

        Constraint[] constraints;
        for(int i = 0; i < numberOfClass; i++){
            for (int j = 0; j < numberOfCar; j++){
                constraints = new Constraint[numberOfOption];
                for(int k = 0; k < numberOfOption; k++){
                    constraints[k] = model.arithm(posOfOption[k][j],"=", optionsPerContrainte[i][k]);
                }
                model.ifThen(model.arithm(posOfClass[j],"=", i), model.and(constraints));
            }
        }

        model.getSolver().solve();

        System.out.println("\t\t\tOptions require");
        System.out.println("------------------------------");
        System.out.print("Classe\t\t");
        for (int i = 0; i < numberOfOption; i++) {
            System.out.print(i + 1 + "\t");
        }
        System.out.println("");
        System.out.println("------------------------------");
        for (int i = 0; i < numberOfCar; i++) {
            System.out.print(posOfClass[i].getValue() + 1 + "\t\t\t");
            for (int j = 0; j < numberOfOption; j++) {
                System.out.print(posOfOption[j][i].getValue() + "\t");
            }
            System.out.println("");
        }
    }
}
