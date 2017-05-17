package command;

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

/**
 * Created by Christian on 02-05-2017.
 */
public class UnitCommand {

    private Game game;
    private Player self;

    public UnitCommand(Game game, Player self){
        this.game = game;
        this.self = self;
    }


    public void SendWorkerToMine(Unit unit){
        Unit closestMineral = null;
        //if it's a worker and it's idle, send it to the closest mineral patch
        if (unit.isIdle()) {
            //find the closest mineral
            for (Unit neutralUnit : game.neutral().getUnits()) {
                if (neutralUnit.getType().isMineralField()) {
                    if (closestMineral == null || unit.getDistance(neutralUnit) < unit.getDistance(closestMineral)) {
                        closestMineral = neutralUnit;
                    }
                }
            }
        }
        //if a mineral patch was found, send the worker to gather it
        if (closestMineral != null) {
            unit.gather(closestMineral, false);
        }
    }

    public boolean hasSendScout = false;
    private int scout = -1;
    private int visitedBases = 0;


    public static BaseLocation enemyBase = null;
    public boolean foundEnemy = false;

    public void SendScout(Unit unit){
        if(!foundEnemy) {
            for (BaseLocation b : BWTA.getBaseLocations().subList(visitedBases, BWTA.getBaseLocations().size() - 1)) {
                // If this is a possible start location,
                if (b.isStartLocation() && !hasSendScout && game.enemy().getUnits().isEmpty()) {
                    game.drawTextScreen(200, 10, "SCOUT STUFF " + b.getPosition());
                    if (scout == -1) {
                        scout = unit.getID();
                    }
                    game.getUnit(scout).move(b.getPosition());
                    hasSendScout = true;
                }
                if (!foundEnemy && game.getUnit(scout).isGatheringMinerals() && game.enemy().getUnits().isEmpty()) {
                    visitedBases++;
                    enemyBase = b;
                    hasSendScout = false;
                }
                if (!foundEnemy && !game.enemy().getUnits().isEmpty()) {
                    enemyBase = b;
                    foundEnemy = true;
                }
            }
        }
    }

    public void SendAttack(){
        for (Unit unit : self.getUnits()) {
            if(unit.getType() == UnitType.Terran_Marine) {
                if (unit.isIdle()) {
                    unit.attack(enemyBase.getPosition());
                }
                //if enemy units appear start attacking
                for (Unit eunit: game.enemy().getUnits())
                {
                    if(!unit.isAttacking()) {
                        unit.attack(eunit, true);
                    }
                }
            }
        }
    }

    //TODO simple defence method, make it smarter
    public void SendDefend(){
        if(!game.enemy().getUnits().isEmpty()) {
            for (Unit unit : self.getUnits()) {
                for (Unit eunit : game.enemy().getUnits()) {
                    if (!unit.isAttacking() && unit.getDistance(eunit) <= 400 && unit.getID() != scout) {
                        unit.attack(eunit, true);
                    }
                }
            }
        }
    }

    //Change this to something smarter
    public int CountAttackUnit(){
        int count = 0;
        for (Unit unit: self.getUnits()) {
            if(unit.getType() == UnitType.Terran_Marine){
                count++;
            }
        }
        return count;
    }
}
