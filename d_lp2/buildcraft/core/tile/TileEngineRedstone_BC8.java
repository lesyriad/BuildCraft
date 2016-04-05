package buildcraft.core.tile;

import buildcraft.api.enums.EnumEnergyStage;
import buildcraft.api.mj.EnumMjPowerType;
import buildcraft.core.lib.utils.AverageDouble;
import buildcraft.lib.engine.TileEngineBase_BC8;
import buildcraft.lib.mj.helpers.MjSimpleProducer;

public class TileEngineRedstone_BC8 extends TileEngineBase_BC8 {
    // TODO: Fix these numbers as they are probably completely wrong
    public static final int[] MILLIWATTS_PROVIDED = { 35, 50, 75, 100, 0 };

    private EnumEnergyStage stage = EnumEnergyStage.BLUE;
    private AverageDouble powerAvg = new AverageDouble(10);
    private long lastChange = 0;

    @Override
    protected MjSimpleProducer createProducer() {
        return new EngineProducer(EnumMjPowerType.REDSTONE);
    }

    @Override
    public EnumEnergyStage getEnergyStage() {
        return stage;
    }

    @Override
    public boolean hasMoreFuel() {
        return true;// We always have more fuel
    }

    @Override
    public int getMaxCurrentlySuppliable() {
        return MILLIWATTS_PROVIDED[stage.ordinal()];
    }

    @Override
    public void setCurrentUsed(int milliwatts) {
        double beingUsed = milliwatts / (double) getMaxCurrentlySuppliable();
        powerAvg.push(beingUsed * 2);
    }

    @Override
    public int getMaxEngineCarryDist() {
        return 1;
    }

    @Override
    protected boolean canCarryOver(TileEngineBase_BC8 engine) {
        return engine instanceof TileEngineRedstone_BC8;
    }

    @Override
    public void update() {
        super.update();
        if (cannotUpdate()) return;
        powerAvg.tick();
        double average = powerAvg.getAverage();
        if (average > 1) {
            if (worldObj.getTotalWorldTime() > lastChange + 100) {
                if (stage != EnumEnergyStage.OVERHEAT) {
                    stage = EnumEnergyStage.VALUES[stage.ordinal() + 1];
                    lastChange = worldObj.getTotalWorldTime();
                    redrawBlock();
                }
            }
        } else if (average < 0.5) {
            if (worldObj.getTotalWorldTime() > lastChange + 20) {
                if (stage != EnumEnergyStage.BLUE) {
                    stage = EnumEnergyStage.VALUES[stage.ordinal() - 1];
                    lastChange = worldObj.getTotalWorldTime();
                    redrawBlock();
                }
            }
        }
    }
}
