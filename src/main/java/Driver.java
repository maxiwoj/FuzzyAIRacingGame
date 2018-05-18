import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class Driver implements Runnable{
    private final FIS fis;
    private final FuzzyRuleSet fuzzyRuleSet;
    private Robot robot;
    private Game game;
    private boolean gameIsOn = true;
    private final int look_ahead_threshold = -20;


    public Driver(Game game) throws AWTException {
        this.game = game;
        fis = FIS.load("fuzzy_car.fcl",false);
        fuzzyRuleSet = fis.getFuzzyRuleSet();
        robot = new Robot();
    }

    public synchronized boolean isGameIsOn() {
        return gameIsOn;
    }

    public synchronized void setGameIsOn(boolean gameIsOn) {
        this.gameIsOn = gameIsOn;
    }
    private void startAI() {
        while(this.gameIsOn) {
            int closest_car = 0;
            for (int i = 0; i < game.lx.length ; i++) {
                if (game.lx[i] > look_ahead_threshold){
                    closest_car = i;
                    break;
                }
            }

//            System.out.println(Arrays.toString(game.lx));
//            System.out.println(Arrays.toString(game.ly));
            int opponent_x = game.lx[closest_car];
            int opponent_y = game.ly[closest_car];

            int my_y_loc = game.car_y;
//            System.out.println("My loc y: " + my_y_loc);

            int last_opponent_diff = 300;
            if (closest_car > 0) {
                last_opponent_diff = Math.abs(game.ly[closest_car - 1] - my_y_loc);
            }


            fuzzyRuleSet.setVariable("opponent_x_loc", opponent_x);
            fuzzyRuleSet.setVariable("opponent_y_loc", opponent_y);
            fuzzyRuleSet.setVariable("own_y_loc", my_y_loc);
            fuzzyRuleSet.setVariable("last_opponent_diff", last_opponent_diff);

            fuzzyRuleSet.evaluate();

            Variable out = fuzzyRuleSet.getVariable("zmiana_polozenia");
            try {
                moveCar(out.getLatestDefuzzifiedValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveCar(Double movement) throws InterruptedException {
        robot.setAutoDelay(0);
        int intMovement = movement.intValue();
        System.out.println("Int movement: " + intMovement);
        if(intMovement < 0) {
            robot.keyPress(KeyEvent.VK_LEFT);
            Thread.sleep(Math.abs(intMovement) * 10);
            robot.keyRelease(KeyEvent.VK_LEFT);
        } else if(intMovement> 0) {
            robot.keyPress(KeyEvent.VK_RIGHT);
            Thread.sleep(Math.abs(intMovement) * 10);
            robot.keyRelease(KeyEvent.VK_RIGHT);
        } else {
            Thread.sleep(1);
            System.out.println("Waiting!");
        }
//        System.out.println("movement val: " + movement.toString());
    }

    @Override
    public void run() {
        startAI();
    }
}
