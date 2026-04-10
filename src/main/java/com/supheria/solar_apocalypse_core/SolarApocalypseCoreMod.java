package com.supheria.solar_apocalypse_core;

import com.supheria.solar_apocalypse_core.config.solar.SolarStageConfig;
import com.supheria.solar_apocalypse_core.config.solar.StageHeightConfig;
import com.supheria.solar_apocalypse_core.init.*;
import com.supheria.solar_apocalypse_core.integration.weather2.Weather2StageStormHandler;
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
import net.minecraftforge.fml.ModList;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

/**
 * 模组主入口。
 *
 * <p>除完成基础注册外，这里还承担两类核心运行时职责：
 * <ul>
 *     <li>注册模组自定义网络消息；</li>
 *     <li>作为方块灾变规则的统一分派入口，把不同方块映射到对应的转换过程。</li>
 * </ul>
 *
 * <p>因此本类既是 Forge 生命周期入口，也是方块转换系统与网络同步系统的总装配点。
 */
@Mod(SolarApocalypseCoreMod.MOD_ID)
public class SolarApocalypseCoreMod {
    public static final Logger LOGGER = LogManager.getLogger(SolarApocalypseCoreMod.class);
    public static final String MOD_ID = "solar_apocalypse_core";
    private static final Map<BlockState, BlockTransform> BLOCK_TRANSFORM_CACHE = new ConcurrentHashMap<>();
    private static final Map<BlockState, BlockTransform> ON_PLACE_TRANSFORM_CACHE = new ConcurrentHashMap<>();

    public SolarApocalypseCoreMod() {
        MinecraftForge.EVENT_BUS.register(this);
        if (ModList.get().isLoaded("weather2")) {
            MinecraftForge.EVENT_BUS.register(new Weather2StageStormHandler());
        }
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        SolarModBlocks.REGISTRY.register(bus);

        SolarModItems.REGISTRY.register(bus);

        SolarModTabs.REGISTRY.register(bus);

    }

    private static final String PROTOCOL_VERSION = "1";
    /**
     * 模组级网络通道。
     * 所有自定义同步消息都通过同一个协议版本进行注册与分发。
     */
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, MOD_ID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    /**
     * 递增消息 ID，确保网络消息按稳定顺序注册到同一通道中。
     */
    private static int messageID = 0;

    /**
     * 注册一条模组网络消息。
     *
     * <p>这里只负责把编解码器和处理器挂到统一通道上，不承载具体业务语义。
     */
    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    /**
     * 服务端延迟任务队列。
     *
     * <p>这里存放的是“若干 tick 后再执行”的主线程任务，而不是异步后台任务。
     */
    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    /**
     * 把一个动作排入服务端 tick 队列，在指定 tick 数后执行。
     */
    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry(action, tick));
    }

    /**
     * 在服务端 tick 结束阶段消费延迟任务，避免与世界本轮更新流程交叉执行。
     */
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

    /**
     * 统一排除不应被常规灾变规则处理的高保护方块。
     *
     * <p>当前主要排除两类对象：显式标记为耐火的方块，以及需要钻石工具的高价值硬质方块。
     */
    private static boolean notExcluded(Reference<Block> ref) {
        return !ref.is(SolarModTags.Blocks.FIRE_RESISTANCE)
                && !ref.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    private static boolean isExcluded(Reference<Block> ref) {
        return !notExcluded(ref);
    }

    public static BlockTransform getCachedBlockTransform(BlockState blockState) {
        return BLOCK_TRANSFORM_CACHE.computeIfAbsent(blockState, SolarApocalypseCoreMod::getBlockTransform);
    }

    public static BlockTransform getCachedOnPlaceTransform(BlockState blockState) {
        return ON_PLACE_TRANSFORM_CACHE.computeIfAbsent(blockState, SolarApocalypseCoreMod::getNBlockTransform);
    }

    /**
     * 为可参与太阳环境调度的方块状态选择一条灾变转换规则。
     *
     * <p>这里的判断顺序本身就是优先级：一旦前面的分类命中，后续更具体或更宽泛的分类都不会再参与。
     * 因此该方法既是规则入口，也是规则冲突的裁决点。
     */
    public static BlockTransform getBlockTransform(BlockState blockState) {
        Block block = blockState.getBlock();
        Reference<Block> ref = block.builtInRegistryHolder();

        if (isExcluded(ref)) {
            return null;
        }

        BlockTransform structureTransform = getSpecialStructureTransform(block, ref);
        if (structureTransform != null) {
            return structureTransform;
        }

        BlockTransform meltingTransform = getMeltingEvaporationTransform(blockState, block, ref);
        if (meltingTransform != null) {
            return meltingTransform;
        }

        BlockTransform flammableTransform = getFlammableTransform(block, ref);
        if (flammableTransform != null) {
            return flammableTransform;
        }

        BlockTransform plantObjectTransform = getPlantObjectTransform(block, ref);
        if (plantObjectTransform != null) {
            return plantObjectTransform;
        }

        BlockTransform geologyTransform = getGeologyTransform(blockState, block, ref);
        if (geologyTransform != null) {
            return geologyTransform;
        }

        if (isFunctionalStructure(block, ref)) {
            return FunctionalStructureDecay.TRANSFORM;
        }

        return null;
    }

    /**
     * 为需要在方块放置后立即修正的状态选择一条 onPlace 转换规则。
     *
     * <p>这条链只覆盖“不能等到随机刻再处理”的情形，因此规则范围比
     * {@link #getBlockTransform(BlockState)} 更窄，优先处理会在落地瞬间造成异常状态的对象。
     */
    public static BlockTransform getNBlockTransform(BlockState blockState) {
        Block block = blockState.getBlock();
        Reference<Block> ref = block.builtInRegistryHolder();

        if (isExcluded(ref)) {
            return null;
        }

        BlockTransform structureTransform = getSpecialStructureTransform(block, ref);
        if (structureTransform != null) {
            return structureTransform;
        }

        BlockTransform meltingTransform = getMeltingEvaporationTransform(blockState, block, ref);
        if (meltingTransform != null) {
            return meltingTransform;
        }

        BlockTransform flammableTransform = getFlammableTransform(block, ref);
        if (flammableTransform != null) {
            return flammableTransform;
        }

        return getPlantObjectTransform(block, ref);
    }

    private static BlockTransform getSpecialStructureTransform(Block block, Reference<Block> ref) {
        if (ref.is(BlockTags.PORTALS)) {
            return ForceDelete.TRANSFORM;
        }
        if (block instanceof EndPortalFrameBlock) {
            return EndFrameClear.TRANSFORM;
        }
        if (ref.is(SolarModTags.Blocks.TNT)) {
            return TntIgnite.TRANSFORM;
        }
        return null;
    }

    private static BlockTransform getMeltingEvaporationTransform(BlockState blockState, Block block, Reference<Block> ref) {
        if (block == Blocks.SNOW_BLOCK || block == Blocks.SNOW) {
            return SnowMelt.FALL_CHECK_TRANSFORM;
        }
        if (ref.is(BlockTags.ICE)) {
            return IceMelt.TRANSFORM;
        }
        if (ref.is(FluidTags.WATER.location()) && blockState.getFluidState().isSource()) {
            return WaterEvaporate.TRANSFORM;
        }
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
            return WaterloggedDry.TRANSFORM;
        }
        if (block instanceof BubbleColumnBlock) {
            return BubbleEvaporate.TRANSFORM;
        }
        if (block instanceof SpongeBlock || block instanceof WetSpongeBlock) {
            return SpongeDry.TRANSFORM;
        }
        return null;
    }

    private static BlockTransform getFlammableTransform(Block block, Reference<Block> ref) {
        if (isLeaves(block, ref)) {
            return LeavesWither.TRANSFORM;
        }
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
                || ref.is(BlockTags.WOOL)
                || ref.is(BlockTags.WOOL_CARPETS)
                || block == Blocks.HAY_BLOCK)
                && !isLeaves(block, ref)) {
            return WoodBurn.TRANSFORM;
        }
        return null;
    }

    private static BlockTransform getPlantObjectTransform(Block block, Reference<Block> ref) {
        if (block instanceof BambooSaplingBlock || block instanceof BambooStalkBlock) {
            return BambooDecay.TRANSFORM;
        }
        if (ref.is(BlockTags.SAPLINGS)) {
            return SmallPlantDecay.TRANSFORM;
        }
        if (ref.is(BlockTags.FLOWER_POTS)) {
            return FlowerPotDecay.TRANSFORM;
        }
        if (isPlantObject(block, ref)) {
            return PlantObjectDecay.TRANSFORM;
        }
        return null;
    }

    private static BlockTransform getGeologyTransform(BlockState blockState, Block block, Reference<Block> ref) {
        if (ref.is(SolarModTags.Blocks.MOIST_DIRT)) return DirtChain.GRASS_BLOCK;
        if (ref.is(SolarModTags.Blocks.DIRT)) return DirtChain.DIRT;
        if (ref.is(SolarModTags.Blocks.HARD_DIRT)) return DirtChain.COARSE_DIRT;
        if (ref.is(BlockTags.SAND) || block == Blocks.SUSPICIOUS_SAND) return DirtChain.SAND;
        if (ref.is(SolarModTags.Blocks.POWDER)) return DirtChain.DUST;
        if (ref.is(SolarModTags.Blocks.SANDSTONE)) return DirtChain.SANDSTONE;
        if (ref.is(SolarModTags.Blocks.MOSSY)) return MossyDecay.TRANSFORM;
        if (ref.is(SolarModTags.Blocks.COBBLESTONE) || block == Blocks.INFESTED_COBBLESTONE) return StoneChain.COBBLESTONE;
        if (ref.is(Tags.Blocks.GRAVEL) || block == Blocks.SUSPICIOUS_GRAVEL) return StoneChain.GRAVEL;
        if (ref.is(SolarModTags.Blocks.CLAY)) return StoneChain.CLAY;
        if (block == Blocks.LAVA) return LavaToObsidian.TRANSFORM;
        if (block == Blocks.INFESTED_DEEPSLATE) return StoneTo.of(Blocks.COBBLED_DEEPSLATE);
        if (block == Blocks.INFESTED_STONE || block == Blocks.INFESTED_STONE_BRICKS
                || block == Blocks.INFESTED_CRACKED_STONE_BRICKS || block == Blocks.INFESTED_CHISELED_STONE_BRICKS
                || block == Blocks.INFESTED_MOSSY_STONE_BRICKS) {
            return StoneTo.of(Blocks.COBBLESTONE);
        }
        if (ref.is(BlockTags.MINEABLE_WITH_PICKAXE)
                && !isFunctionalStructure(block, ref)
                && !ref.is(SolarModTags.Blocks.SANDSTONE)
                && !ref.is(SolarModTags.Blocks.COBBLESTONE)
                && !ref.is(BlockTags.IRON_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_IRON)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_RAW_IRON)
                && !ref.is(BlockTags.REDSTONE_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)
                && !ref.is(BlockTags.DIAMOND_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_DIAMOND)
                && !ref.is(BlockTags.LAPIS_ORES)
                && !ref.is(Tags.Blocks.STORAGE_BLOCKS_LAPIS)) {
            if (ref.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                    || ref.is(Tags.Blocks.COBBLESTONE_DEEPSLATE)
                    || ref.is(Tags.Blocks.ORE_BEARING_GROUND_DEEPSLATE)
                    || ref.is(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                    || ref.is(SolarModTags.Blocks.DEEPSLATE)) {
                if (ref.is(BlockTags.STAIRS)) return StoneTo.of(Blocks.COBBLED_DEEPSLATE_STAIRS);
                if (ref.is(BlockTags.SLABS)) return StoneTo.of(Blocks.COBBLED_DEEPSLATE_SLAB);
                if (ref.is(BlockTags.WALLS)) return StoneTo.of(Blocks.COBBLED_DEEPSLATE_WALL);
                return StoneTo.of(Blocks.COBBLED_DEEPSLATE);
            }
            if (ref.is(BlockTags.STAIRS)) return StoneTo.of(Blocks.COBBLESTONE_STAIRS);
            if (ref.is(BlockTags.SLABS)) return StoneTo.of(Blocks.COBBLESTONE_SLAB);
            if (ref.is(BlockTags.WALLS)) return StoneTo.of(Blocks.COBBLESTONE_WALL);
            return StoneTo.of(Blocks.COBBLESTONE);
        }
        return null;
    }

    private static boolean isPlantObject(Block block, Reference<Block> ref) {
        return ref.is(BlockTags.FLOWERS)
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
                || block instanceof FrogspawnBlock;
    }

    private static boolean isFunctionalStructure(Block block, Reference<Block> ref) {
        return ref.is(BlockTags.BANNERS)
                || ref.is(BlockTags.CANDLES)
                || ref.is(BlockTags.CANDLE_CAKES)
                || ref.is(BlockTags.BEDS)
                || block == Blocks.TORCH
                || block == Blocks.WALL_TORCH
                || block == Blocks.REDSTONE_TORCH
                || block == Blocks.REDSTONE_WALL_TORCH
                || block == Blocks.SOUL_TORCH
                || block == Blocks.SOUL_WALL_TORCH
                || block == Blocks.LEVER
                || block == Blocks.TRIPWIRE
                || block == Blocks.TRIPWIRE_HOOK
                || block == Blocks.RAIL
                || block == Blocks.POWERED_RAIL
                || block == Blocks.DETECTOR_RAIL
                || block == Blocks.ACTIVATOR_RAIL
                || block == Blocks.BELL
                || block == Blocks.REPEATER
                || block == Blocks.COMPARATOR
                || block == Blocks.PISTON
                || block == Blocks.STICKY_PISTON
                || block == Blocks.PISTON_HEAD
                || block == Blocks.SMOKER
                || block == Blocks.HONEYCOMB_BLOCK
                || block == Blocks.COAL_BLOCK
                || block instanceof WebBlock
                || block instanceof HoneyBlock
                || block instanceof SlimeBlock;
    }

    private static boolean isLeaves(Block block, Reference<Block> ref) {
        return ref.is(BlockTags.LEAVES)
                || block instanceof LeavesBlock
                || block instanceof CherryLeavesBlock
                || block instanceof MangroveLeavesBlock;
    }

}
