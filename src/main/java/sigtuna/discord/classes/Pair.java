package sigtuna.discord.classes;

public class Pair<T1,T2>{
	public T1 key;
	public T2 value;
	public Pair(T1 key, T2 value){
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object object){
		if(object instanceof Pair){
			Pair<T1,T2> pair = (Pair<T1,T2>) object;
			return (pair.key == key && pair.value == value);
 		}
		return false;
	}
}