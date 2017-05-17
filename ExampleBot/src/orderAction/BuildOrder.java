package orderAction;

import bwapi.*;
import tiles.BuildLocationSelector;

/**
 * Created by Christian on 02-05-2017.
 */
public class BuildOrder {
    private Game game;
    private Player self;
    private static BuildLocationSelector _tileSelector = BuildLocationSelector.GetTile();
    private boolean orderStarted = false;
    private boolean orderStarted1 = false;
    private int currentUnitId;
    private int supplyDepotCount = 0;
    private int barrackCount = 0;

    public BuildOrder(Game game, Player self)
    {
        this.game = game;
        this.self = self;
    }

    public void CreateBuilding(Unit unit, UnitType type, int count){
        if((self.supplyTotal() - self.supplyUsed() <= 4) && self.supplyTotal() != 400 && barrackCount <= count) {
            if (!orderStarted1) {
                 if (type.equals(UnitType.Terran_Barracks) && (self.minerals() >= 150)) {
                        //get a nice place to build a supply depot
                        TilePosition buildTile =
                                _tileSelector.getBuildTile(game, unit, UnitType.Terran_Barracks, self.getStartLocation());
                        //and, if found, send the worker to build it
                        orderStarted1 = unit.build(UnitType.Terran_Barracks, buildTile);
                        barrackCount = BuildingCount(UnitType.Terran_Barracks) + 1;
                    }
            }
        }
        if(orderStarted1 && game.getUnit(getCurrentUnitId()).isGatheringMinerals() && barrackCount != BuildingCount(UnitType.Terran_Barracks)){
            barrackCount--;
            orderStarted1 = false;
        }
        if(barrackCount == BuildingCount(UnitType.Terran_Barracks)){
            orderStarted1 = false;
        }

    }



    public void CreateSupplyDepot(Unit unit){
        //supplydepots are a special case since supply building is needed all game until we reach max supply (400)
        if ((self.supplyTotal() - self.supplyUsed() <= 4 || (self.supplyUsed() > 70 && self.supplyTotal() - self.supplyUsed() <= 10)) && self.supplyTotal() != 400) {
            game.drawTextScreen(100, 10, "BUILD STUFF");

            if (!orderStarted && (self.minerals() >= 100)) {
                setCurrentUnitId(unit.getID());
                TilePosition buildTile =
                        _tileSelector.getBuildTile(game, unit, UnitType.Terran_Supply_Depot, self.getStartLocation());

                    orderStarted = unit.build(UnitType.Terran_Supply_Depot, buildTile);
                    supplyDepotCount = BuildingCount(UnitType.Terran_Supply_Depot) + 1;
            }

        }
        buildFailedReset();

        IsOrderFinished();

    }

    public boolean IsOrderStarted(){
        return orderStarted;
    }

    private void buildFailedReset(){
        if(orderStarted && game.getUnit(getCurrentUnitId()).isGatheringMinerals() && supplyDepotCount != BuildingCount(UnitType.Terran_Supply_Depot)){
            orderStarted = false;
            supplyDepotCount--;
        }
    }

    private boolean IsOrderFinished() {
        if (BuildingCount(UnitType.Terran_Supply_Depot) == supplyDepotCount) {
            orderStarted = false;
            return false;
        }
        return true;
    }

    public int BuildingCount(UnitType type){
        return self.completedUnitCount(type);
    }

    public void setCurrentUnitId(int currentUnitId) {
        this.currentUnitId = currentUnitId;
    }

    public int getSupplyDepotCount(){
        return supplyDepotCount;
    }

    public int getCurrentUnitId() {
        return currentUnitId;
    }
}
