package tcb.adventurousdungeons.api.dungeon.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Lists;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ComponentDataManager {
	private static final Map<Class<? extends IDungeonComponent>, Integer> NEXT_ID_MAP = new HashMap<>();
	private final IDungeonComponent component;
	private final Map<Integer, ComponentDataManager.DataEntry<?>> entries = new HashMap<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean empty = true;
	private boolean dirty;

	public ComponentDataManager(IDungeonComponent component) {
		this.component = component;
	}

	public static <T> DataParameter<T> createKey(Class<? extends IDungeonComponent> clazz, DataSerializer<T> serializer) {
		int dataId = 0;

		if (NEXT_ID_MAP.containsKey(clazz)) {
			dataId = ((Integer)NEXT_ID_MAP.get(clazz)).intValue() + 1;
		} else {
			Class<?> rootClass = clazz;

			while (rootClass != IDungeonComponent.class && rootClass != null) {
				rootClass = rootClass.getSuperclass();

				if (NEXT_ID_MAP.containsKey(rootClass)) {
					dataId = ((Integer)NEXT_ID_MAP.get(rootClass)).intValue() + 1;
					break;
				}
			}
		}

		if (dataId > 254) {
			throw new IllegalArgumentException("IDungeonComoponent data value id is too big with " + dataId + "! (Max is " + 254 + ")");
		} else {
			NEXT_ID_MAP.put(clazz, Integer.valueOf(dataId));
			return serializer.createKey(dataId);
		}
	}

	public <T> void register(DataParameter<T> key, T value) {
		int i = key.getId();

		if (i > 254) {
			throw new IllegalArgumentException("IDungeonComoponent data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else if (this.entries.containsKey(Integer.valueOf(i))) {
			throw new IllegalArgumentException("IDungeonComoponent has duplicate id value for " + i + "!");
		} else if (DataSerializers.getSerializerId(key.getSerializer()) < 0) {
			throw new IllegalArgumentException("IDungeonComoponent has unregistered serializer " + key.getSerializer() + " for " + i + "!");
		} else {
			this.setEntry(key, value);
		}
	}

	private <T> void setEntry(DataParameter<T> key, T value) {
		ComponentDataManager.DataEntry<T> entry = new ComponentDataManager.DataEntry<>(key, value);
		this.lock.writeLock().lock();
		this.entries.put(Integer.valueOf(key.getId()), entry);
		this.empty = false;
		this.lock.writeLock().unlock();
	}

	@SuppressWarnings("unchecked")
	private <T> ComponentDataManager.DataEntry<T> getEntry(DataParameter<T> key) {
		this.lock.readLock().lock();
		ComponentDataManager.DataEntry<T> entry;

		try {
			entry = (ComponentDataManager.DataEntry<T>)this.entries.get(Integer.valueOf(key.getId()));
		} catch (Throwable throwable) {
			CrashReport crash = CrashReport.makeCrashReport(throwable, "Getting synced IDungeonComponent data");
			CrashReportCategory category = crash.makeCategory("Synced IDungeonComponent data");
			category.addCrashSection("Data ID", key);
			throw new ReportedException(crash);
		}

		this.lock.readLock().unlock();
		return entry;
	}

	public <T> T get(DataParameter<T> key) {
		return this.getEntry(key).getValue();
	}

	public <T> void set(DataParameter<T> key, T value) {
		ComponentDataManager.DataEntry<T> entry = this.<T>getEntry(key);

		if (ObjectUtils.notEqual(value, entry.getValue())) {
			entry.setValue(value);
			entry.setDirty(true);
			this.dirty = true;
		}
	}

	public <T> void setDirty(DataParameter<T> key) {
		this.getEntry(key).dirty = true;
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public static void writeEntries(List<ComponentDataManager.DataEntry<?>> entries, PacketBuffer buf) throws IOException {
		if (entries != null) {
			int i = 0;

			for (int j = entries.size(); i < j; ++i) {
				ComponentDataManager.DataEntry<?> dataentry = (ComponentDataManager.DataEntry<?>)entries.get(i);
				writeEntry(buf, dataentry);
			}
		}

		buf.writeByte(255);
	}

	@Nullable
	public List<ComponentDataManager.DataEntry<?>> getDirty() {
		List<ComponentDataManager.DataEntry<?>> list = null;

		if (this.dirty) {
			this.lock.readLock().lock();

			for (ComponentDataManager.DataEntry<?> dataentry : this.entries.values()) {
				if (dataentry.isDirty()) {
					dataentry.setDirty(false);

					if (list == null) {
						list = Lists. < ComponentDataManager.DataEntry<? >> newArrayList();
					}

					list.add(dataentry);
				}
			}

			this.lock.readLock().unlock();
		}

		this.dirty = false;
		return list;
	}

	public void writeEntries(PacketBuffer buf) throws IOException {
		this.lock.readLock().lock();

		for (ComponentDataManager.DataEntry<?> dataentry : this.entries.values()) {
			writeEntry(buf, dataentry);
		}

		this.lock.readLock().unlock();
		buf.writeByte(255);
	}

	@Nullable
	public List<ComponentDataManager.DataEntry<?>> getAll() {
		List<ComponentDataManager.DataEntry<?>> list = null;
		this.lock.readLock().lock();

		for(ComponentDataManager.DataEntry<?> dataentry : this.entries.values()) {
			if(list == null) {
				list = new ArrayList<>();
			}

			list.add(dataentry);
		}

		this.lock.readLock().unlock();
		return list;
	}

	private static <T> void writeEntry(PacketBuffer buf, ComponentDataManager.DataEntry<T> entry) throws IOException {
		DataParameter<T> key = entry.getKey();
		int serializerId = DataSerializers.getSerializerId(key.getSerializer());

		if (serializerId < 0) {
			throw new EncoderException("Unknown IDungeonComonent serializer type " + key.getSerializer());
		} else {
			buf.writeByte(key.getId());
			buf.writeVarInt(serializerId);
			key.getSerializer().write(buf, entry.getValue());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Nullable
	public static List<ComponentDataManager.DataEntry<?>> readEntries(PacketBuffer buf) throws IOException {
		List<ComponentDataManager.DataEntry<?>> list = null;
		int keyId;

		while ((keyId = buf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = new ArrayList<>();
			}

			int serializerId = buf.readVarInt();
			DataSerializer<?> serializer = DataSerializers.getSerializer(serializerId);

			if (serializer == null) {
				throw new DecoderException("Unknown serializer type " + serializerId);
			}

			list.add(new ComponentDataManager.DataEntry(serializer.createKey(keyId), serializer.read(buf)));
		}

		return list;
	}

	@SideOnly(Side.CLIENT)
	public void setEntryValues(List<ComponentDataManager.DataEntry<?>> entries) {
		this.lock.writeLock().lock();

		for (ComponentDataManager.DataEntry<?> entry : entries) {
			ComponentDataManager.DataEntry<?> entry2 = (ComponentDataManager.DataEntry<?>)this.entries.get(Integer.valueOf(entry.getKey().getId()));

			if (entry2 != null) {
				this.setEntryValue(entry2, entry);
				this.component.notifyDataManagerChange(entry.getKey());
			}
		}

		this.lock.writeLock().unlock();
		this.dirty = true;
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	protected <T> void setEntryValue(ComponentDataManager.DataEntry<T> target, ComponentDataManager.DataEntry<?> source) {
		target.setValue((T)source.getValue());
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public void setClean() {
		this.dirty = false;
		this.lock.readLock().lock();

		for (ComponentDataManager.DataEntry<?> entry : this.entries.values()) {
			entry.setDirty(false);
		}

		this.lock.readLock().unlock();
	}

	public static class DataEntry<T> {
		private final DataParameter<T> key;
		private T value;
		private boolean dirty;

		public DataEntry(DataParameter<T> key, T value) {
			this.key = key;
			this.value = value;
			this.dirty = true;
		}

		public DataParameter<T> getKey() {
			return this.key;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return this.value;
		}

		public boolean isDirty() {
			return this.dirty;
		}

		public void setDirty(boolean dirty) {
			this.dirty = dirty;
		}
	}
}