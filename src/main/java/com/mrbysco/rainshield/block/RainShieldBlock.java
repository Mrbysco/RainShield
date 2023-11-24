package com.mrbysco.rainshield.block;

import com.mrbysco.rainshield.util.RainShieldData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class RainShieldBlock extends RodBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public RainShieldBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);

		if (level.isRaining() && !state.getValue(POWERED)) {
			for (double mod = 0; mod < 1; mod += 0.25f) {
				for (double a = 0; a <= Math.PI * 2D; a += (Math.PI * 2D) / 20) {
					double x = pos.getX() + 0.5 + (1 - mod) * Math.cos(a);
					double z = pos.getZ() + 0.5 + (1 - mod) * Math.sin(a);

					level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, pos.getY() + 1.7f + mod, z, 0, 0.02, 0);
					level.addParticle(ParticleTypes.SMOKE, x, pos.getY() + 1.6f + mod, z, 0, 0, 0);
				}
			}
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		RainShieldData.addRainShieldPos(pos, level);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			RainShieldData.removeRainShieldPos(pos, level);

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.isClientSide) {
			boolean flag = state.getValue(POWERED);
			if (flag != level.hasNeighborSignal(pos)) {
				if (flag) {
					level.scheduleTick(pos, this, 4);
				} else {
					level.setBlock(pos, state.cycle(POWERED), 2);
				}
			}
		}
	}

	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.cycle(POWERED), 2);
		}
	}

	public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
		FluidState fluidstate = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
		boolean flag = fluidstate.getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(FACING, placeContext.getClickedFace()).setValue(WATERLOGGED, Boolean.valueOf(flag));
	}

	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
		blockStateBuilder.add(FACING, POWERED, WATERLOGGED);
	}
}
