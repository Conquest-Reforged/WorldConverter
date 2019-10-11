package me.dags.scraper.v1_12;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class NeighbourReader implements IBlockAccess {

    private final BlockPos centerPos;
    private final BlockPos neighbourPos;
    private final IBlockState state;
    private final IBlockState neighbour;

    public NeighbourReader(IBlockState state, IBlockState neighbour, EnumFacing direction) {
        this.state = state;
        this.neighbour = neighbour;
        this.centerPos = BlockPos.ORIGIN;
        this.neighbourPos = centerPos.offset(direction);
    }

    public IBlockState getActualState() {
        try {
            return state.getActualState(this, BlockPos.ORIGIN);
        } catch (Throwable t) {
            return state;
        }
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (pos.equals(centerPos)) {
            return state;
        }
        if (pos.equals(neighbourPos)) {
            return neighbour;
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return getBlockState(pos).getBlock() == Blocks.AIR;
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return Biomes.DEFAULT;
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0;
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.DEFAULT;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return false;
    }
}
