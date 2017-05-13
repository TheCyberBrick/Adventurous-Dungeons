package tcb.adventurousdungeons.api.script;

public interface ISerializableScriptComponent<T> extends IScriptComponent {
	public T serialize(T serializer);

	public void deserialize(T serializer);
	
	public Class<T> getSerializer();
}
