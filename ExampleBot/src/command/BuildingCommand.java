package command;

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * Created by Christian on 04-05-2017.
 */
public class BuildingCommand {
    private Game game;
    private Player self;

    public BuildingCommand(Game game, Player self){
        this.game = game;
        this.self = self;
    }

    public void TrainUnit(Unit myUnit){

        if (myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50 && myUnit.getTrainingQueue().isEmpty()) {
            myUnit.train(UnitType.Terran_SCV);
        }
        else if (myUnit.getType() == UnitType.Terran_Barracks && self.minerals() >= 50 && myUnit.getTrainingQueue().isEmpty()) {
            myUnit.train(UnitType.Terran_Marine);
        }
    }
}
