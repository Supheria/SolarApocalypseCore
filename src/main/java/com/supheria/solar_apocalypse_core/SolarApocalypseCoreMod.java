package com.supheria.solar_apocalypse_core;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.init.*;
import com.supheria.solar_apocalypse_core.transforms.dirt.DirtChain;
import com.supheria.solar_apocalypse_core.transforms.fluid.*;
import com.supheria.solar_apocalypse_core.transforms.misc.*;
import com.supheria.solar_apocalypse_core.transforms.plant.*;
import com.supheria.solar_apocalypse_core.transforms.stone.*;
import com.supheria.solar_apocalypse_core.transforms.wood.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

        SapModBlocks.REGISTRY.register(bus);

        SapModItems.REGISTRY.register(bus);

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

    /** 未设置耐火标签且无需钻石工具 */
    private static boolean notExcluded(Reference<Block> ref) {
        return !ref.is(SapModTags.Blocks.FIRE_RESISTANCE)
                && !ref.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    public static BlockTransform getBlockTransform(BlockState blockState) {
        Block block = blockState.getBlock();
        Reference<Block> ref = block.builtInRegistryHolder();

        // 简单删除类（杂草、花朵、旗帜、蜡烛、床、珊瑚等）
        if ((ref.is(SapModTags.Blocks.SIMPLE_DELETE)
                || ref.is(BlockTags.REPLACEABLE_BY_TREES)
                || ref.is(BlockTags.SWORD_EFFICIENT)
                || ref.is(BlockTags.MINEABLE_WITH_HOE)
                || ref.is(BlockTags.FLOWERS)
                || ref.is(BlockTags.BANNERS)
                || ref.is(BlockTags.CANDLES)
                || ref.is(BlockTags.CANDLE_CAKES)
                || ref.is(BlockTags.BEDS)
                || ref.is(BlockTags.SNOW)
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
                || block instanceof SlimeBlock)
                && notExcluded(ref)
                && !ref.is(BlockTags.LEAVES)
                && !ref.is(FluidTags.WATER.location())
                && !(block instanceof SpongeBlock)
                && !(block instanceof WetSpongeBlock)
                && !(block instanceof SculkBlock)
                && !(block instanceof SculkSensorBlock)
                && !(block instanceof SculkVeinBlock)
                && !(block instanceof SculkCatalystBlock)
                && !(block instanceof SculkShriekerBlock)
                && !(block instanceof LeavesBlock)) {
            return SimpleDecay.TRANSFORM;
        }
        // 小型植物（树苗、竹子）
        if ((ref.is(BlockTags.SAPLINGS)
                || block instanceof BambooSaplingBlock
                || block instanceof BambooStalkBlock)
                && !ref.is(BlockTags.LEAVES) && notExcluded(ref)) {
            return SmallPlantDecay.TRANSFORM;
        }
        // 木制可燃类（原木、木板、竹块、羊毛等）
        if ((ref.is(BlockTags.LOGS)
                || ref.is(BlockTags.PLANKS)
                || ref.is(BlockTags.WOODEN_BUTTONS)
                || ref.is(BlockTags.WOODEN_DOORS)
                || ref.is(BlockTags.WOODEN_STAIRS)
                || ref.is(BlockTags.WOODEN_SLABS)
                || ref.is(BlockTags.WOODEN_FENCES)
                || ref.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || ref.is(BlockTags.WOODEN_TRAPDOORS)
                || ref.is(BlockTags.BAMBOO_BLOCKS)
                || ref.is(BlockTags.MINEABLE_WITH_AXE)
                || ref.is(BlockTags.WOOL)
                || ref.is(BlockTags.WOOL_CARPETS))
                && notExcluded(ref) && !ref.is(BlockTags.LEAVES)) {
            return WoodBurn.TRANSFORM;
        }
        // 树叶
        if ((ref.is(BlockTags.LEAVES)
                || block instanceof LeavesBlock
                || block instanceof CherryLeavesBlock
                || block instanceof MangroveLeavesBlock)
                && notExcluded(ref)) {
            return LeavesWither.TRANSFORM;
        }
        // 苔藓石
        if (ref.is(SapModTags.Blocks.MOSSY) && notExcluded(ref)) return MossyDecay.TRANSFORM;
        // 草方块
        if (ref.is(SapModTags.Blocks.MOIST_DIRT) && notExcluded(ref)) return DirtChain.GRASS_BLOCK;
        // 泥土
        if (ref.is(SapModTags.Blocks.DIRT) && notExcluded(ref)) return DirtChain.DIRT;
        // 粗泥土
        if (ref.is(SapModTags.Blocks.HARD_DIRT) && notExcluded(ref)) return DirtChain.COARSE_DIRT;
        // 沙子
        if (ref.is(BlockTags.SAND) && notExcluded(ref)) return DirtChain.SAND;
        // 粉尘
        if (ref.is(SapModTags.Blocks.POWDER) && notExcluded(ref)) return DirtChain.DUST;
        // 冰类
        if (ref.is(BlockTags.ICE) && notExcluded(ref)) return IceMelt.TRANSFORM;
        // 水
        if (ref.is(FluidTags.WATER.location()) && notExcluded(ref)) return WaterEvaporate.TRANSFORM;
        // 含水方块（当前处于含水状态）
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                && blockState.getValue(BlockStateProperties.WATERLOGGED)
                && notExcluded(ref)) {
            return WaterloggedDry.TRANSFORM;
        }
        // 气泡柱
        if (block instanceof BubbleColumnBlock && notExcluded(ref)) return BubbleEvaporate.TRANSFORM;
        // 海绵
        if ((block instanceof SpongeBlock || block instanceof WetSpongeBlock)
                && notExcluded(ref)) {
            return SpongeDry.TRANSFORM;
        }
        // TNT
        if (ref.is(SapModTags.Blocks.TNT) && notExcluded(ref)) return TntIgnite.TRANSFORM;
        // 花盆
        if (ref.is(BlockTags.FLOWER_POTS) && notExcluded(ref)) return FlowerPotDecay.TRANSFORM;
        // 石头系（镐可挖掘，排除特殊矿石与存储块）
        if (ref.is(BlockTags.MINEABLE_WITH_PICKAXE) && notExcluded(ref)
                && !ref.is(SapModTags.Blocks.SIMPLE_DELETE)
                && !ref.is(SapModTags.Blocks.SANDSTONE)
                && !ref.is(SapModTags.Blocks.COBBLESTONE)
                && !ref.is(BlockTags.IRON_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_IRON)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON)
                && !ref.is(BlockTags.REDSTONE_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)
                && !ref.is(BlockTags.DIAMOND_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND)
                && !ref.is(BlockTags.LAPIS_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_LAPIS)) {
            // 深板岩系列
            if (ref.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                    || ref.is(Tags.Blocks.COBBLESTONE_DEEPSLATE)
                    || ref.is(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE)
                    || ref.is(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                    || ref.is(SapModTags.Blocks.DEEPSLATE)) {
                if (ref.is(BlockTags.STAIRS)) return StoneTo.of(Blocks.COBBLED_DEEPSLATE_STAIRS);
                if (ref.is(BlockTags.SLABS))  return StoneTo.of(Blocks.COBBLED_DEEPSLATE_SLAB);
                if (ref.is(BlockTags.WALLS))  return StoneTo.of(Blocks.COBBLED_DEEPSLATE_WALL);
                return StoneTo.of(Blocks.COBBLED_DEEPSLATE);
            }
            // 普通石头系列
            if (ref.is(BlockTags.STAIRS)) return StoneTo.of(Blocks.COBBLESTONE_STAIRS);
            if (ref.is(BlockTags.SLABS))  return StoneTo.of(Blocks.COBBLESTONE_SLAB);
            if (ref.is(BlockTags.WALLS))  return StoneTo.of(Blocks.COBBLESTONE_WALL);
            return StoneTo.of(Blocks.COBBLESTONE);
        }
        // 卵石
        if (ref.is(SapModTags.Blocks.COBBLESTONE) && notExcluded(ref)) return StoneChain.COBBLESTONE;
        // 砾石
        if (ref.is(Tags.Blocks.GRAVEL) && notExcluded(ref)) return StoneChain.GRAVEL;
        // 熔岩（第六阶段转化为黑曜石）
        if (block == Blocks.LAVA && !ref.is(SapModTags.Blocks.FIRE_RESISTANCE)) {
            return LavaToObsidian.TRANSFORM;
        }
        // 砂岩
        if (ref.is(SapModTags.Blocks.SANDSTONE) && notExcluded(ref)) return DirtChain.CRUSHED_DIRT;
        // 黏土
        if (ref.is(SapModTags.Blocks.CLAY) && notExcluded(ref)) return StoneChain.CLAY;
        // 传送门
        if (ref.is(BlockTags.PORTALS)) return ForceDelete.TRANSFORM;
        // 末地传送门框架
        if (block instanceof EndPortalFrameBlock) return EndFrameClear.TRANSFORM;
        return null;
    }

    public static BlockTransform getNBlockTransform(BlockState blockState) {
        Block block = blockState.getBlock();
        Reference<Block> ref = block.builtInRegistryHolder();

        // 传送门（优先处理，避免被其他规则捕获）
        if (ref.is(BlockTags.PORTALS)) return ForceDelete.TRANSFORM;
        // 末地传送门框架
        if (block instanceof EndPortalFrameBlock) return EndFrameClear.TRANSFORM;
        // 简单删除类（杂草、花朵、旗帜、蜡烛、床、珊瑚等）
        if ((ref.is(SapModTags.Blocks.SIMPLE_DELETE)
                || ref.is(BlockTags.REPLACEABLE_BY_TREES)
                || ref.is(BlockTags.SWORD_EFFICIENT)
                || ref.is(BlockTags.MINEABLE_WITH_HOE)
                || ref.is(BlockTags.FLOWERS)
                || ref.is(BlockTags.BANNERS)
                || ref.is(BlockTags.CANDLES)
                || ref.is(BlockTags.CANDLE_CAKES)
                || ref.is(BlockTags.BEDS)
                || ref.is(BlockTags.SNOW)
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
                || block instanceof SlimeBlock)
                && notExcluded(ref)
                && !ref.is(BlockTags.LEAVES)
                && !ref.is(FluidTags.WATER.location())
                && !(block instanceof SpongeBlock)
                && !(block instanceof WetSpongeBlock)
                && !(block instanceof SculkBlock)
                && !(block instanceof SculkSensorBlock)
                && !(block instanceof SculkVeinBlock)
                && !(block instanceof SculkCatalystBlock)
                && !(block instanceof SculkShriekerBlock)
                && !(block instanceof LeavesBlock)) {
            return SimpleDecay.TRANSFORM;
        }
        // 小型植物（树苗、竹子）
        if ((ref.is(BlockTags.SAPLINGS)
                || block instanceof BambooSaplingBlock
                || block instanceof BambooStalkBlock)
                && !ref.is(BlockTags.LEAVES) && notExcluded(ref)) {
            return SmallPlantDecay.TRANSFORM;
        }
        // 木制可燃类（原木、木板、竹块、羊毛等）
        if ((ref.is(BlockTags.LOGS)
                || ref.is(BlockTags.PLANKS)
                || ref.is(BlockTags.WOODEN_BUTTONS)
                || ref.is(BlockTags.WOODEN_DOORS)
                || ref.is(BlockTags.WOODEN_STAIRS)
                || ref.is(BlockTags.WOODEN_SLABS)
                || ref.is(BlockTags.WOODEN_FENCES)
                || ref.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || ref.is(BlockTags.WOODEN_TRAPDOORS)
                || ref.is(BlockTags.BAMBOO_BLOCKS)
                || ref.is(BlockTags.MINEABLE_WITH_AXE)
                || ref.is(BlockTags.WOOL)
                || ref.is(BlockTags.WOOL_CARPETS))
                && notExcluded(ref) && !ref.is(BlockTags.LEAVES)) {
            return WoodBurn.TRANSFORM;
        }
        // 树叶
        if ((ref.is(BlockTags.LEAVES)
                || block instanceof LeavesBlock
                || block instanceof CherryLeavesBlock
                || block instanceof MangroveLeavesBlock)
                && notExcluded(ref)) {
            return LeavesWither.TRANSFORM;
        }
        // 苔藓石
        if (ref.is(SapModTags.Blocks.MOSSY) && notExcluded(ref)) return MossyDecay.TRANSFORM;
        // 草方块
        if (ref.is(SapModTags.Blocks.MOIST_DIRT) && notExcluded(ref)) return DirtChain.GRASS_BLOCK;
        // 泥土
        if (ref.is(SapModTags.Blocks.DIRT) && notExcluded(ref)) return DirtChain.DIRT;
        // 粗泥土
        if (ref.is(SapModTags.Blocks.HARD_DIRT) && notExcluded(ref)) return DirtChain.COARSE_DIRT;
        // 沙子
        if (ref.is(BlockTags.SAND) && notExcluded(ref)) return DirtChain.SAND;
        // 粉尘
        if (ref.is(SapModTags.Blocks.POWDER) && notExcluded(ref)) return DirtChain.DUST;
        // 冰类
        if (ref.is(BlockTags.ICE) && notExcluded(ref)) return IceMelt.TRANSFORM;
        // 水
        if (ref.is(FluidTags.WATER.location()) && notExcluded(ref)) return WaterEvaporate.TRANSFORM;
        // 含水方块（当前处于含水状态）
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                && blockState.getValue(BlockStateProperties.WATERLOGGED)
                && notExcluded(ref)) {
            return WaterloggedDry.TRANSFORM;
        }
        // 气泡柱
        if (block instanceof BubbleColumnBlock && notExcluded(ref)) return BubbleEvaporate.TRANSFORM;
        return null;
    }

}
