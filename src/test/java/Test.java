import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

public class Test {
    private final FIS fis = FIS.load("fuzzy_car.fcl",false);;
    private final FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();

    public static void main(String[] args) {
        Test test = new Test();
    }

    public Test() {
        fuzzyRuleSet.chart();
        testMovement();
    }

    public void testMovement() {
        fuzzyRuleSet.setVariable("opponent_x_loc",322);
        fuzzyRuleSet.setVariable("opponent_y_loc", 300);
        fuzzyRuleSet.setVariable("own_y_loc", 138);
        fuzzyRuleSet.setVariable("last_opponent_diff", 185 - 138);

        fuzzyRuleSet.evaluate();

        final Double zmiana_polozenia = fuzzyRuleSet.getVariable("zmiana_polozenia").getLatestDefuzzifiedValue();
        System.out.println("int: " + zmiana_polozenia.intValue());
        fuzzyRuleSet.getVariable("zmiana_polozenia").chartDefuzzifier(true);
    }
}
