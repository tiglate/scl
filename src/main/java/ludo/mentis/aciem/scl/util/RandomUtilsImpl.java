package ludo.mentis.aciem.scl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomUtilsImpl implements RandomUtils {
	
	private final Random random;
	
	public RandomUtilsImpl() {
		this.random = new Random();
	}
	
	@Override
	public <T> List<T> createRandomSublist(List<T> list, int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Number of elements to pick (n) cannot be negative.");
		}
		if (n > list.size()) {
			throw new IllegalArgumentException("Number of elements to pick (n) cannot be greater than the list size.");
		}
		if (n == 0) {
			return new ArrayList<>();
		}
		if (list.isEmpty()) {
			throw new IllegalArgumentException("Cannot pick elements from an empty list.");
		}
		List<T> tempList = new ArrayList<>(list);
		Collections.shuffle(tempList, random);
		return tempList.subList(0, n);
	}
	
    @Override
	public <T extends Enum<?>> T pickRandomEnumValue(Class<T> enumClass) {
        var enumValues = enumClass.getEnumConstants();
        if (enumValues == null || enumValues.length == 0) {
            throw new IllegalArgumentException("Enum class has no values.");
        }
        int randomIndex = random.nextInt(enumValues.length);
        return enumValues[randomIndex];
    }
    
    @Override
    public boolean pickRandomBoolean() {
    	return random.nextInt(10000) % 2 == 0;
    }
}
