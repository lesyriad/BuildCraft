/* Copyright (c) 2016 AlexIIL and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.builders.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import buildcraft.api.enums.EnumBlueprintType;
import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.builders.tile.TileBuilder_Neptune;
import buildcraft.lib.block.BlockBCTile_Neptune;
import buildcraft.lib.block.IBlockWithFacing;

public class BlockBuilder_Neptune extends BlockBCTile_Neptune implements IBlockWithFacing {
    public static final IProperty<EnumBlueprintType> PROP_BPT = BuildCraftProperties.BLUEPRINT_TYPE;

    public BlockBuilder_Neptune(Material material, String id) {
        super(material, id);
        IBlockState state = getDefaultState();
        state = state.withProperty(PROP_FACING, EnumFacing.NORTH);
        state = state.withProperty(PROP_BPT, EnumBlueprintType.NONE);
        setDefaultState(state);
    }

    // IBlockState

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROP_FACING, PROP_BPT);
    }

    // Others

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing orientation = placer.getHorizontalFacing();
        world.setBlockState(pos, state.withProperty(PROP_FACING, orientation.getOpposite()));
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBuilder_Neptune();
    }
}