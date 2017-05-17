import command.BuildingCommand;
import orderAction.*;
import command.UnitCommand;
import bwapi.*;
import bwta.BWTA;


public class TerranBotRun extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    private Game game;

    private Player self;

    private BuildOrder order;

    private UnitCommand unitCommand;

    private BuildingCommand buildingCommand;

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }


    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        BWTA.readMap();
        BWTA.analyze();

        order = new BuildOrder(game, self);
        unitCommand = new UnitCommand(game, self);
        buildingCommand = new BuildingCommand(game, self);
    }

    @Override
    public void onFrame() {
        game.drawTextScreen(10, 10, "Build 23 " + order.IsOrderStarted() +" SupplyDepot count: " + order.BuildingCount(UnitType.Terran_Supply_Depot) + " SupplyDepot inc: "+ order.getSupplyDepotCount());

        for (Unit myUnit : self.getUnits()) {
            buildingCommand.TrainUnit(myUnit);

            if (myUnit.getType().isWorker()) {

                unitCommand.SendScout(myUnit);
                unitCommand.SendWorkerToMine(myUnit);
                order.CreateSupplyDepot(myUnit);
                order.CreateBuilding(myUnit, UnitType.Terran_Barracks, 3);

                unitCommand.SendDefend();

                if(unitCommand.CountAttackUnit() >= 25) {
                    unitCommand.SendAttack();
                    game.drawTextScreen(350, 10, "has send attack");
                }
            }
        }

    }

    public static void main(String[] args) {
        new TerranBotRun().run();
    }
}