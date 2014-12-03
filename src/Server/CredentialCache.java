package Server;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;


public class CredentialCache
{
	public HashMap<Integer, long[]> cache;
	
	public CredentialCache(){
		cache = new HashMap<Integer, long[]>();
		cache.put(1993, new long []{1181102456L, 756799586749L, 9858691039L, 90872452345L});
		cache.put(1001, new long []{65693769037L, 5837561838590L, 448607821645L, 5632451399983L});
	}
	
	public Boolean checkID(Integer ID){
		return cache.containsKey(ID);
	}
	
	public long[] getEncryptionKey(Integer ID){
		return cache.get(ID);
		
	}
	
	public Set<Integer> getIterableIDs(){
		return cache.keySet();
	}
	
	public long[] getRandomKey(){
		Random rand = new Random();
		return new long [] {rand.nextLong(), rand.nextLong(),
				rand.nextLong(), rand.nextLong()};
	}
	
}
