package com.supheria.solar_apocalypse_core.network;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.function.Supplier;

import net.minecraftforge.items.ItemHandlerHelper;

import com.supheria.solar_apocalypse_core.SolarApocalypseCoreMod;
import com.supheria.solar_apocalypse_core.world.SolarStage;
import org.jetbrains.annotations.NotNull;

/**
 * 模组关键运行时状态的持久化与同步入口。
 *
 * <p>这里统一管理两类 SavedData：
 * <ul>
 *     <li>维度级 {@link WorldVariables}；</li>
 *     <li>固定挂在主世界数据存储中的全局阶段状态 {@link MapVariables}。</li>
 * </ul>
 *
 * <p>服务端持有权威状态，客户端只维护网络同步后的镜像副本。
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SolarModVariables {
	/**
	 * 在通用初始化阶段注册 SavedData 同步消息。
	 */
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		SolarApocalypseCoreMod.addNetworkMessage(SavedDataSyncMessage.class, SavedDataSyncMessage::buffer, SavedDataSyncMessage::new, SavedDataSyncMessage::handler);
	}

	/**
	 * 玩家进入服务器或切换维度时补发状态镜像。
	 *
	 * <p>登录事件用于初始化客户端本地缓存；切维度事件用于刷新维度级数据，避免客户端保留旧维度状态。
	 */
	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		private static final String STARTER_DIARY_FLAG = "received_starter_diary";

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata instanceof MapVariables mapVariables)
					mapVariables.syncToPlayer((ServerPlayer) event.getEntity());
				if (worlddata != null)
					SolarApocalypseCoreMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
				grantStarterDiaryIfNeeded((ServerPlayer) event.getEntity());
			}
		}

		@SubscribeEvent
		public static void onPlayerClone(PlayerEvent.Clone event) {
			CompoundTag originalData = event.getOriginal().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
			if (!originalData.isEmpty()) {
				event.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG, originalData.copy());
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					SolarApocalypseCoreMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}

		private static void grantStarterDiaryIfNeeded(ServerPlayer player) {
			CompoundTag persistedData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
			if (persistedData.getBoolean(STARTER_DIARY_FLAG)) {
				return;
			}
			ItemHandlerHelper.giveItemToPlayer(player, createStarterDiary());
			persistedData.putBoolean(STARTER_DIARY_FLAG, true);
			player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistedData);
		}

		private static ItemStack createStarterDiary() {
			ItemStack diary = new ItemStack(Items.WRITABLE_BOOK);
			diary.setHoverName(Component.literal("一本从天而降的日记"));
			CompoundTag tag = diary.getOrCreateTag();
			ListTag pages = new ListTag();
			pages.add(StringTag.valueOf("第%天：\n\n不知道从什么时候开始世界变得不一样了，以前我并没有记日记的习惯，因此我也搞不清问题到底出在哪一天，就更不可能知道问题出在哪里了。"));
			pages.add(StringTag.valueOf("村庄的房屋有时会莫名其妙的着火，周围的牲畜也越来越少，农夫们抱怨地里的庄稼长得越来越慢，渔夫们则常年打不出几条鱼，那些掠夺者似乎对我们失去了兴趣。"));
			pages.add(StringTag.valueOf("许多人开始主动去找僵尸被感染，然后被铁傀儡杀掉，这些家伙可真奇怪，变成那群没有感觉的僵尸有什么好的？"));
			pages.add(StringTag.valueOf("现在大家只需要担心那些蠢笨的僵尸了，那群家伙根本不可能赢过铁傀儡呀，除了天气越来越热之外，日子反而更好过了才对。"));
			pages.add(StringTag.valueOf("第@天：\n\n附近的矿洞又塌方了，但这片人工矿道相当牢固，从我记事起他就已经存在了，为什么突然塌掉了呢？据说还压死了下矿的玩家，不过他们总会复活的吧。"));
			pages.add(StringTag.valueOf("玩家比我们村民幸运太多了，他们拥有破坏和创造的能力，简直就像是被神明赐福了一般，我们却只能在村庄里苟延残喘。"));
			pages.add(StringTag.valueOf("我在图书管理员的家中翻到过一本古籍，据说很久以前我们村民也拥有和玩家一样的创造能力，这些古人在地下建立村庄，只为躲避来自地上的灾祸。"));
			pages.add(StringTag.valueOf("那里没有玩家会把我们的粮食洗劫一空，也不会有劫掠者和僵尸残害我们的生命……但每当我把这些事情跟其他人分享，他们都嘲笑我是个傻子，连这种传说都信，可他们自己不也相信神明是存在的吗？"));
			pages.add(StringTag.valueOf("第#天：\n\n井水似乎在枯竭？是因为太久都没有下雨了吗？好在天无绝人之路，旅行的商人告诉我们他来时远方正雷电交加，或许要不了多久村里就又有水源了吧。"));
			pages.add(StringTag.valueOf("但牧师老村长却大惊小怪，不仅把全村的盔甲、武器商们都叫过来，还说要离开这里。"));
			pages.add(StringTag.valueOf("我肯定不像他们那么傻，现在外边僵尸那么多，铁傀儡又不会跟他们走，到了晚上都会被吃掉脑子的。"));
			pages.add(StringTag.valueOf("幸好他们也没拉着我一块走，有铁傀儡保护至少在村子里我是绝对安全的！"));
			tag.put("pages", pages);
			return diary;
		}
	}

	/**
	 * 维度级 SavedData。
	 *
	 * <p>当前实现几乎没有承载额外字段，但仍保留完整的持久化与同步路径，便于后续挂载维度范围状态。
	 * 服务端按维度存储，客户端通过 {@link #clientSide} 保存最近一次收到的镜像副本。
	 */
	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "solar_world_data";

		public static WorldVariables load(CompoundTag tag) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		/**
		 * 将当前维度数据标记为脏，并只向同维度玩家广播镜像更新。
		 */
		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level level && !level.isClientSide())
				SolarApocalypseCoreMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(e -> WorldVariables.load(e), WorldVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	/**
	 * 全局阶段与天数进度的权威存储。
	 *
	 * <p>当前实现固定把该数据挂在主世界 {@code DataStorage} 上，因此即使从其他维度访问，
	 * 读取到的仍然是同一份全局阶段状态。客户端通过 {@link #clientSide} 持有最近一次同步到本地的镜像副本。
	 */
	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "solar_mapvars";
		/** 当前太阳阶段。 */
		public SolarStage solarStage = SolarStage.NONE;
		/** 当前日内时间快照。 */
		public double currentTimeOfDay = 0;
		/** 当前天数/日推进快照。 */
		public double currentDay = 0;
		/** 是否已经为该存档初始化过永久禁作弊配置。 */
		public boolean cheatLockConfigured = false;
		/** 该存档是否被永久锁定为不可开启作弊。 */
		public boolean cheatsPermanentlyLocked = false;

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
			// 从 NBT 读取阶段值
			solarStage = SolarStage.getByOrdinal(nbt.getInt("solarStage"));
			currentTimeOfDay = nbt.getDouble("currentTimeOfDay");
			currentDay = nbt.getDouble("currentDay");
			cheatLockConfigured = nbt.getBoolean("cheatLockConfigured");
			cheatsPermanentlyLocked = nbt.getBoolean("cheatsPermanentlyLocked");
		}

		@Override
		public @NotNull CompoundTag save(CompoundTag nbt) {
			// 这里按 ordinal 持久化阶段；如果未来调整枚举顺序，需要同步考虑旧存档兼容性。
			nbt.putInt("solarStage", solarStage.ordinal());
			nbt.putDouble("currentTimeOfDay", currentTimeOfDay);
			nbt.putDouble("currentDay", currentDay);
			nbt.putBoolean("cheatLockConfigured", cheatLockConfigured);
			nbt.putBoolean("cheatsPermanentlyLocked", cheatsPermanentlyLocked);
			return nbt;
		}

		/**
		 * 将全局阶段状态标记为脏，并向所有客户端广播最新镜像。
		 *
		 * <p>由于该数据固定代表整张存档共享的太阳阶段进度，而不是单维度状态，
		 * 因此这里使用全服广播而不是按维度分发。
		 */
		public void markDirty() {
			this.setDirty();
		}

		public void syncData(LevelAccessor world) {
			markDirty();
			if (world instanceof Level level && !level.isClientSide()) {
				SolarApocalypseCoreMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
			}
		}

		public void syncToPlayer(ServerPlayer player) {
			SolarApocalypseCoreMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public SolarStage getSolarStage() {
			return this.solarStage;
		}

		public void setSolarStage(SolarStage stage) {
			this.solarStage = stage;
		}

		public boolean isCheatLockConfigured() {
			return this.cheatLockConfigured;
		}

		public void setCheatLockConfigured(boolean cheatLockConfigured) {
			this.cheatLockConfigured = cheatLockConfigured;
		}

		public boolean isCheatsPermanentlyLocked() {
			return this.cheatLockConfigured && this.cheatsPermanentlyLocked;
		}

		public void setCheatsPermanentlyLocked(boolean cheatsPermanentlyLocked) {
			this.cheatsPermanentlyLocked = cheatsPermanentlyLocked;
		}

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(e -> MapVariables.load(e), MapVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	/**
	 * SavedData 的统一同步消息。
	 *
	 * <p>{@code type == 0} 表示全局 {@link MapVariables}，{@code type == 1}
	 * 表示当前维度的 {@link WorldVariables}。消息体始终携带对应数据的完整 NBT 快照。
	 */
	public static class SavedDataSyncMessage {
		/** 0 = {@link MapVariables}，1 = {@link WorldVariables}。 */
		public int type;
		public SavedData data;

		/**
		 * 从网络缓冲区恢复对应的 SavedData 镜像。
		 * 先根据 type 选择具体容器，再把完整 NBT 快照反序列化进去。
		 */
		public SavedDataSyncMessage(FriendlyByteBuf buffer) {
			this.type = buffer.readInt();
			this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
			if (this.data instanceof MapVariables _mapvars)
				_mapvars.read(buffer.readNbt());
			else if (this.data instanceof WorldVariables _worldvars)
				_worldvars.read(buffer.readNbt());
		}

		public SavedDataSyncMessage(int type, SavedData data) {
			this.type = type;
			this.data = data;
		}

		public static void buffer(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeInt(message.type);
			buffer.writeNbt(message.data.save(new CompoundTag()));
		}

		/**
		 * 客户端收到镜像后，用最新快照直接替换本地缓存。
		 * 服务端不会消费这条消息；权威状态始终只在服务端维护。
		 */
		public static void handler(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					if (message.type == 0)
						MapVariables.clientSide = (MapVariables) message.data;
					else
						WorldVariables.clientSide = (WorldVariables) message.data;
				}
			});
			context.setPacketHandled(true);
		}
	}
}
