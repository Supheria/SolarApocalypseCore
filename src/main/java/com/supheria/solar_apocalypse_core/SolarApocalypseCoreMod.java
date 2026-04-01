package com.supheria.solar_apocalypse_core;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.config.solar.SolarHudConfig;
import com.supheria.solar_apocalypse_core.init.*;
import com.supheria.solar_apocalypse_core.procedures.*;
import com.supheria.solar_apocalypse_core.procedures.stones.*;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod(SolarApocalypseCoreMod.MOD_ID)
public class SolarApocalypseCoreMod {
    public static final Logger LOGGER = LogManager.getLogger(SolarApocalypseCoreMod.class);
    public static final String MOD_ID = "solar_apocalypse_core";

    public SolarApocalypseCoreMod() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Solar Stage Configuration (COMMON)
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, SolarStageConfig.SPEC);

        // Register HUD Configuration (CLIENT)
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, SolarHudConfig.SPEC);

        SapModBlocks.REGISTRY.register(bus);

        SapModItems.REGISTRY.register(bus);

        SapModMobEffects.REGISTRY.register(bus);

        SapModTabs.REGISTRY.register(bus);

    }

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueue.forEach(work -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() == 0)
                    actions.add(work);
            });
            actions.forEach(e -> e.getKey().run());
            workQueue.removeAll(actions);
        }
    }

    public static Procedure getProcedure(BlockState blockState) {
        FluidState fluidState = blockState.getFluidState();
        Block block = blockState.getBlock();
        Fluid fluid = fluidState.getType();
        Reference<Block> builtInRegistryHolder = block.builtInRegistryHolder();
        Reference<Fluid> fluidReference = fluid.builtInRegistryHolder();
        //사소한 오브
        if (builtInRegistryHolder.is(SapModTags.Blocks.SIMPLE_DELETE)
                || builtInRegistryHolder.is(BlockTags.REPLACEABLE_BY_TREES)
                || builtInRegistryHolder.is(BlockTags.SWORD_EFFICIENT)
                || builtInRegistryHolder.is(BlockTags.MINEABLE_WITH_HOE)
                || builtInRegistryHolder.is(BlockTags.FLOWERS)
                || builtInRegistryHolder.is(BlockTags.BANNERS)
                || builtInRegistryHolder.is(BlockTags.CANDLES)
                || builtInRegistryHolder.is(BlockTags.CANDLE_CAKES)
                || builtInRegistryHolder.is(BlockTags.BEDS)
                || builtInRegistryHolder.is(BlockTags.SNOW)
                || block instanceof BushBlock
                || block instanceof WaterlilyBlock
                || block instanceof CoralBlock
                || block instanceof CoralPlantBlock
                || block instanceof BaseCoralPlantBlock
                || block instanceof BaseCoralFanBlock
                || block instanceof BaseCoralWallFanBlock
                || block instanceof CoralFanBlock
                || block instanceof CoralWallFanBlock
                || block instanceof CactusBlock
                || block instanceof FrogspawnBlock
                || block instanceof WebBlock
                || block instanceof HoneyBlock
                || block instanceof SlimeBlock) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)
                    && !builtInRegistryHolder.is(BlockTags.LEAVES)
                    && !builtInRegistryHolder.is(FluidTags.WATER.location())
                    && !(block instanceof SpongeBlock)
                    && !(block instanceof WetSpongeBlock)
                    && !(block instanceof SculkBlock)
                    && !(block instanceof SculkSensorBlock)
                    && !(block instanceof SculkVeinBlock)
                    && !(block instanceof SculkCatalystBlock)
                    && !(block instanceof SculkShriekerBlock)
                    && !(block instanceof LeavesBlock)) {
                return SimpleDeleteProcedure::execute;
            }
        }
        //작은 식물
        if (builtInRegistryHolder.is(BlockTags.SAPLINGS)
                || block instanceof BambooSaplingBlock
                || block instanceof BambooStalkBlock) {
            if (!builtInRegistryHolder.is(BlockTags.LEAVES)
                    && !builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return SmallPlantDeleteProcedure::execute;
            }
        }
        //불타는계열
        if (builtInRegistryHolder.is(BlockTags.LOGS)
                || builtInRegistryHolder.is(BlockTags.PLANKS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_BUTTONS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_DOORS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_STAIRS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_SLABS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_FENCES)
                || builtInRegistryHolder.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || builtInRegistryHolder.is(BlockTags.WOODEN_TRAPDOORS)
                || builtInRegistryHolder.is(BlockTags.BAMBOO_BLOCKS)
                || builtInRegistryHolder.is(BlockTags.MINEABLE_WITH_AXE)
                || builtInRegistryHolder.is(BlockTags.WOOL)
                || builtInRegistryHolder.is(BlockTags.WOOL_CARPETS)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)
                    && !builtInRegistryHolder.is(BlockTags.LEAVES)) {
                return WoodBlockFProcedure::execute;
            }
        }
        //나뭇잎
        if (builtInRegistryHolder.is(BlockTags.LEAVES)
                || block instanceof LeavesBlock
                || block instanceof CherryLeavesBlock
                || block instanceof MangroveLeavesBlock) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return WitheredLeavesBlockFProcedure::execute;
            }
        }
        //이끼 낀 돌
        if (builtInRegistryHolder.is(SapModTags.Blocks.MOSSY)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return MossyDeleteProcedure::execute;
            }
        }
        //잔디
        if (builtInRegistryHolder.is(SapModTags.Blocks.MOIST_DIRT)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return GrassBlockTCDirtProcedure::execute;
            }
        }
        //흙
        if (builtInRegistryHolder.is(SapModTags.Blocks.DIRT)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return DirtTCCoarseDirtProcedure::execute;
            }
        }
        //거친흙
        if (builtInRegistryHolder.is(SapModTags.Blocks.HARD_DIRT)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return CoarseDirtTCCrushedDirtBlockProcedure::execute;
            }
        }
        //모래
        if (builtInRegistryHolder.is(BlockTags.SAND)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return SandTCDustProcedure::execute;
            }
        }
        //가루
        if (builtInRegistryHolder.is(SapModTags.Blocks.POWDER)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return DustLostProcedure::execute;
            }
        }
        //얼음
        if (builtInRegistryHolder.is(BlockTags.ICE)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return IceMeltProcedure::execute;
            }
        }
        //물
        if (builtInRegistryHolder.is(FluidTags.WATER.location())) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return WaterEvaporateProcedure::execute;
            }
        }
        //침수될 수 있으며, 현재 침수됨
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                && blockState.getValue(BlockStateProperties.WATERLOGGED) == true) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return WaterTagDeleteProcedure::execute;
            }
        }
        //공기방울
        if (block instanceof BubbleColumnBlock) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return BubbleEvaporateProcedure::execute;
            }
        }
        //스폰지
        if (block instanceof SpongeBlock
                || block instanceof WetSpongeBlock) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return SpongeDeleteProcedure::execute;
            }
        }
        //TNT
        if (builtInRegistryHolder.is(SapModTags.Blocks.TNT)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return TNTFProcedure::execute;
            }
        }
        //화분
        if (builtInRegistryHolder.is(BlockTags.FLOWER_POTS)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return FlowerPotFProcedure::execute;
            }
        }
        //돌계열
        if (builtInRegistryHolder.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(SapModTags.Blocks.SIMPLE_DELETE)
                    && !builtInRegistryHolder.is(SapModTags.Blocks.SANDSTONE)
                    && !builtInRegistryHolder.is(SapModTags.Blocks.COBBLESTONE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)
                    && !builtInRegistryHolder.is(BlockTags.IRON_ORES)
                    && !builtInRegistryHolder.is(Tags.Blocks.STORAGE_BLOCKS_IRON)
                    && !builtInRegistryHolder.is(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON)
                    && !builtInRegistryHolder.is(BlockTags.REDSTONE_ORES)
                    && !builtInRegistryHolder.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)
                    && !builtInRegistryHolder.is(BlockTags.DIAMOND_ORES)
                    && !builtInRegistryHolder.is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND)
                    && !builtInRegistryHolder.is(BlockTags.LAPIS_ORES)
                    && !builtInRegistryHolder.is(Tags.Blocks.STORAGE_BLOCKS_LAPIS)) {
                //심층암계열
                if (builtInRegistryHolder.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                        || builtInRegistryHolder.is(Tags.Blocks.COBBLESTONE_DEEPSLATE)
                        || builtInRegistryHolder.is(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE)
                        || builtInRegistryHolder.is(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                        || builtInRegistryHolder.is(SapModTags.Blocks.DEEPSLATE)) {
                    if (builtInRegistryHolder.is(BlockTags.STAIRS)) {
                        return StoneTCCobbledDeepslateStairsProcedure::execute;
                    } else if (builtInRegistryHolder.is(BlockTags.SLABS)) {
                        return StoneTCCobbledDeepslateSlabProcedure::execute;
                    } else if (builtInRegistryHolder.is(BlockTags.WALLS)) {
                        return StoneTCCobbledDeepslateWallProcedure::execute;
                    } else {
                        return StoneTCCobbledDeepslateProcedure::execute;
                    }
                    //일반돌계열
                } else {
                    //계단
                    if (builtInRegistryHolder.is(BlockTags.STAIRS)) {
                        return StoneTCCobblestoneStairsProcedure::execute;
                        //반블록
                    } else if (builtInRegistryHolder.is(BlockTags.SLABS)) {
                        return StoneTCCobblestoneSlabProcedure::execute;
                        //담장
                    } else if (builtInRegistryHolder.is(BlockTags.WALLS)) {
                        return StoneTCCobblestoneWallProcedure::execute;
                        //일반블록
                    } else {
                        return StoneTCCobblestoneProcedure::execute;
                    }
                }
            }
        }
        //조약돌
        if (builtInRegistryHolder.is(SapModTags.Blocks.COBBLESTONE)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return CobblestoneTCGravelProcedure::execute;
            }
        }
        //자갈
        if (builtInRegistryHolder.is(Tags.Blocks.GRAVEL)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return GravelTCLavaProcedure::execute;
            }
        }
        //사암
        if (builtInRegistryHolder.is(SapModTags.Blocks.SANDSTONE)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return CrushedDirtTCSandProcedure::execute;
            }
        }
        //점토
        if (builtInRegistryHolder.is(SapModTags.Blocks.CLAY)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return ClayTCTerracottaProcedure::execute;
            }
        }
        //포탈관련
        if (builtInRegistryHolder.is(BlockTags.PORTALS)) {
            return DeleteUnconditionallyProcedure::execute;
        }
        //엔드프레임
        if (block instanceof EndPortalFrameBlock) {
            return EndFrameTagDeleteProcedure::execute;
        }
        return null;
    }
    public static Procedure getNProcedure(BlockState blockState) {
        FluidState fluidState = blockState.getFluidState();
        Block block = blockState.getBlock();
        Fluid fluid = fluidState.getType();
        Reference<Block> builtInRegistryHolder = block.builtInRegistryHolder();
        Reference<Fluid> fluidReference = fluid.builtInRegistryHolder();
        //포탈관련
        if (builtInRegistryHolder.is(BlockTags.PORTALS)) {
            return DeleteUnconditionallyProcedure::execute;
        }
        //엔드프레임
        if (block instanceof EndPortalFrameBlock) {
            return EndFrameTagDeleteProcedure::execute;
        }
        //사소한 오브
        if (builtInRegistryHolder.is(SapModTags.Blocks.SIMPLE_DELETE)
                || builtInRegistryHolder.is(BlockTags.REPLACEABLE_BY_TREES)
                || builtInRegistryHolder.is(BlockTags.SWORD_EFFICIENT)
                || builtInRegistryHolder.is(BlockTags.MINEABLE_WITH_HOE)
                || builtInRegistryHolder.is(BlockTags.FLOWERS)
                || builtInRegistryHolder.is(BlockTags.BANNERS)
                || builtInRegistryHolder.is(BlockTags.CANDLES)
                || builtInRegistryHolder.is(BlockTags.CANDLE_CAKES)
                || builtInRegistryHolder.is(BlockTags.BEDS)
                || builtInRegistryHolder.is(BlockTags.SNOW)
                || block instanceof BushBlock
                || block instanceof WaterlilyBlock
                || block instanceof CoralBlock
                || block instanceof CoralPlantBlock
                || block instanceof BaseCoralPlantBlock
                || block instanceof BaseCoralFanBlock
                || block instanceof BaseCoralWallFanBlock
                || block instanceof CoralFanBlock
                || block instanceof CoralWallFanBlock
                || block instanceof CactusBlock
                || block instanceof FrogspawnBlock
                || block instanceof WebBlock
                || block instanceof HoneyBlock
                || block instanceof SlimeBlock) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)
                    && !builtInRegistryHolder.is(BlockTags.LEAVES)
                    && !builtInRegistryHolder.is(FluidTags.WATER.location())
                    && !(block instanceof SpongeBlock)
                    && !(block instanceof WetSpongeBlock)
                    && !(block instanceof SculkBlock)
                    && !(block instanceof SculkSensorBlock)
                    && !(block instanceof SculkVeinBlock)
                    && !(block instanceof SculkCatalystBlock)
                    && !(block instanceof SculkShriekerBlock)
                    && !(block instanceof LeavesBlock)) {
                return SimpleDeleteProcedure::execute;
            }
        }
        //작은 식물
        if (builtInRegistryHolder.is(BlockTags.SAPLINGS)
                || block instanceof BambooSaplingBlock
                || block instanceof BambooStalkBlock) {
            if (!builtInRegistryHolder.is(BlockTags.LEAVES)
                    && !builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return SmallPlantDeleteProcedure::execute;
            }
        }
        //불타는계열
        if (builtInRegistryHolder.is(BlockTags.LOGS)
                || builtInRegistryHolder.is(BlockTags.PLANKS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_BUTTONS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_DOORS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_STAIRS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_SLABS)
                || builtInRegistryHolder.is(BlockTags.WOODEN_FENCES)
                || builtInRegistryHolder.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || builtInRegistryHolder.is(BlockTags.WOODEN_TRAPDOORS)
                || builtInRegistryHolder.is(BlockTags.BAMBOO_BLOCKS)
                || builtInRegistryHolder.is(BlockTags.MINEABLE_WITH_AXE)
                || builtInRegistryHolder.is(BlockTags.WOOL)
                || builtInRegistryHolder.is(BlockTags.WOOL_CARPETS)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)
                    && !builtInRegistryHolder.is(BlockTags.LEAVES)) {
                return WoodBlockFProcedure::execute;
            }
        }
        //나뭇잎
        if (builtInRegistryHolder.is(BlockTags.LEAVES)
                || block instanceof LeavesBlock
                || block instanceof CherryLeavesBlock
                || block instanceof MangroveLeavesBlock) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return WitheredLeavesBlockFProcedure::execute;
            }
        }
        //이끼 낀 돌
        if (builtInRegistryHolder.is(SapModTags.Blocks.MOSSY)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return MossyDeleteProcedure::execute;
            }
        }
        //잔디
        if (builtInRegistryHolder.is(SapModTags.Blocks.MOIST_DIRT)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return GrassBlockTCDirtProcedure::execute;
            }
        }
        //흙
        if (builtInRegistryHolder.is(SapModTags.Blocks.DIRT)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return DirtTCCoarseDirtProcedure::execute;
            }
        }
        //거친흙
        if (builtInRegistryHolder.is(SapModTags.Blocks.HARD_DIRT)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return CoarseDirtTCCrushedDirtBlockProcedure::execute;
            }
        }
        //모래
        if (builtInRegistryHolder.is(BlockTags.SAND)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return SandTCDustProcedure::execute;
            }
        }
        //가루
        if (builtInRegistryHolder.is(SapModTags.Blocks.POWDER)) {
            if (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return DustLostProcedure::execute;
            }
        }
        //얼음
        if (builtInRegistryHolder.is(BlockTags.ICE)) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return IceMeltProcedure::execute;
            }
        }
        //물
        if (builtInRegistryHolder.is(FluidTags.WATER.location())) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return WaterEvaporateProcedure::execute;
            }
        }
        //침수될 수 있으며, 현재 침수됨
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                && blockState.getValue(BlockStateProperties.WATERLOGGED) == true) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return WaterTagDeleteProcedure::execute;
            }
        }
        //공기방울
        if (block instanceof BubbleColumnBlock) {
            if  (!builtInRegistryHolder.is(SapModTags.Blocks.FIRE_RESISTANCE)
                    && !builtInRegistryHolder.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return BubbleEvaporateProcedure::execute;
            }
        }
        return null;
    }

    @FunctionalInterface
    public static interface Procedure {
        void call(LevelAccessor world, double x, double y, double z);
    }
}