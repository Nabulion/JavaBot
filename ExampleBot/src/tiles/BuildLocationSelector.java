package tiles;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * Created by Christian on 02-05-2017.
 */
public class BuildLocationSelector {


    private static BuildLocationSelector instance = null;

    private BuildLocationSelector() {
    }
    public static BuildLocationSelector GetTile() {
        if(instance == null) {
            instance = new BuildLocationSelector();
        }
        return instance;
    }
    // Returns a suitable TilePosition to build a given building type near
// specified TilePosition aroundTile, or null if not found. (builder parameter is our worker)

    public TilePosition getBuildTile(Game game, Unit builder, UnitType buildingType, TilePosition aroundTile) {
        TilePosition ret = null;
        int maxDist = 3;
        int stopDist = 40;

        // Refinery, Assimilator, Extractor
        if (buildingType.isRefinery()) {
            for (Unit n : game.neutral().getUnits()) {
                if ((n.getType() == UnitType.Resource_Vespene_Geyser) &&
                        ( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
                        ( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
                        ) return n.getTilePosition();
            }
        }

        while ((maxDist < stopDist) && (ret == null)) {
            for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
                for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
                    if (game.canBuildHere(new TilePosition(i,j), buildingType, builder, false)) {
                        // units that are blocking the tile
                        boolean unitsInWay = false;
                        for (Unit u : game.getAllUnits()) {
                            if (u.getID() == builder.getID()) continue;
                            if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
                        }
                        if (!unitsInWay) {
                            return new TilePosition(i, j);
                        }
                    }
                }
            }
            maxDist += 2;
        }

        if (ret == null) game.printf("Unable to find suitable build position for "+buildingType.toString());
        return ret;
    }
}
