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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

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
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata instanceof MapVariables mapVariables)
					mapVariables.syncToPlayer((ServerPlayer) event.getEntity());
				if (worlddata != null)
					SolarApocalypseCoreMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
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
		/** 与月相或独立昼夜推进关联的计数快照。 */
		public double currentLunarDay = 0;

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
			currentLunarDay = nbt.getDouble("currentLunarDay");
		}

		@Override
		public @NotNull CompoundTag save(CompoundTag nbt) {
			// 这里按 ordinal 持久化阶段；如果未来调整枚举顺序，需要同步考虑旧存档兼容性。
			nbt.putInt("solarStage", solarStage.ordinal());
			nbt.putDouble("currentTimeOfDay", currentTimeOfDay);
			nbt.putDouble("currentDay", currentDay);
			nbt.putDouble("currentLunarDay", currentLunarDay);
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

		public SolarStage getCurrentStage() {
			return this.solarStage;
		}

		public SolarStage getSolarStage() {
			return this.solarStage;
		}

		public void setCurrentStage(SolarStage stage) {
			this.solarStage = stage;
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
