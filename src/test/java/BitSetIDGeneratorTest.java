import com.jblur.idgenerator.*;
import javafx.util.Pair;
import org.junit.Test;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BitSetIDGeneratorTest {
    private BitSet bitSetFromString(String bits){
        BitSet bitSet = new BitSet(bits.length());
        for(int i=0; i<bits.length();i++){
            bitSet.set(i, bits.charAt(i)=='1');
        }
        return bitSet;
    }

    private long longFromBitsString(String bits){
        BitSet bitSet = new BitSet(64);
        for(int i=0, j=bits.length()-1; j>=0;i++,j--){
            bitSet.set(i, bits.charAt(j)=='1');
        }
        long value = 0L;
        for (int i = 0; i < bitSet.length(); ++i) {
            value += bitSet.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    private void testUid(String uidBits, List<Pair<Integer, Integer>> timeAndSequenceBitsCountList) throws Exception{
        BitSet uidBitSet = bitSetFromString(uidBits);
        long uid = longFromBitsString(uidBits);
        BitSetIDGenerator idGenerator;
        for(Pair<Integer, Integer> timeAndSequenceBitsCount:timeAndSequenceBitsCountList){
            idGenerator = new BitSetIDGenerator(0,
                    uid,
                    timeAndSequenceBitsCount.getKey(), //time bits count
                    uidBits.length(),
                    timeAndSequenceBitsCount.getValue(), //sequence bits count
                    IDMode.TIME_UID_SEQUENCE);
            BitSet resultBitSet = idGenerator.generateBitSetId();
            for(int i=0;i<uidBits.length();i++){
                assertThat(uidBitSet.get(i), is(equalTo(resultBitSet.get(i+timeAndSequenceBitsCount.getKey()))));
            }

            idGenerator = new BitSetIDGenerator(0,
                    uid,
                    timeAndSequenceBitsCount.getKey(), //time bits count
                    uidBits.length(),
                    timeAndSequenceBitsCount.getValue(), //sequence bits count
                    IDMode.TIME_SEQUENCE_UID);
            resultBitSet = idGenerator.generateBitSetId();
            for(int i=0;i<uidBits.length();i++){
                assertThat(uidBitSet.get(i), equalTo(resultBitSet.get(i+timeAndSequenceBitsCount.getKey()+timeAndSequenceBitsCount.getValue())));
            }

            idGenerator = new BitSetIDGenerator(0,
                    uid,
                    timeAndSequenceBitsCount.getKey(), //time bits count
                    uidBits.length(),
                    timeAndSequenceBitsCount.getValue(), //sequence bits count
                    IDMode.SEQUENCE_TIME_UID);
            resultBitSet = idGenerator.generateBitSetId();
            for(int i=0;i<uidBits.length();i++){
                assertThat(uidBitSet.get(i), equalTo(resultBitSet.get(i+timeAndSequenceBitsCount.getKey()+timeAndSequenceBitsCount.getValue())));
            }

            idGenerator = new BitSetIDGenerator(0,
                    uid,
                    timeAndSequenceBitsCount.getKey(), //time bits count
                    uidBits.length(),
                    timeAndSequenceBitsCount.getValue(), //sequence bits count
                    IDMode.SEQUENCE_UID_TIME);
            resultBitSet = idGenerator.generateBitSetId();
            for(int i=0;i<uidBits.length();i++){
                assertThat(uidBitSet.get(i), equalTo(resultBitSet.get(i+timeAndSequenceBitsCount.getValue())));
            }

            idGenerator = new BitSetIDGenerator(0,
                    uid,
                    timeAndSequenceBitsCount.getKey(), //time bits count
                    uidBits.length(),
                    timeAndSequenceBitsCount.getValue(), //sequence bits count
                    IDMode.UID_SEQUENCE_TIME);
            resultBitSet = idGenerator.generateBitSetId();
            for(int i=0;i<uidBits.length();i++){
                assertThat(uidBitSet.get(i), equalTo(resultBitSet.get(i)));
            }

            idGenerator = new BitSetIDGenerator(0,
                    uid,
                    timeAndSequenceBitsCount.getKey(), //time bits count
                    uidBits.length(),
                    timeAndSequenceBitsCount.getValue(), //sequence bits count
                    IDMode.UID_TIME_SEQUENCE);
            resultBitSet = idGenerator.generateBitSetId();
            for(int i=0;i<uidBits.length();i++){
                assertThat(uidBitSet.get(i), equalTo(resultBitSet.get(i)));
            }
        }
    }

    @Test
    public void testUIDPosition() throws Exception {
        List timeAndSequenceBitsCountList = new LinkedList();

        timeAndSequenceBitsCountList.add(new Pair(41, 10));
        timeAndSequenceBitsCountList.add(new Pair(0, 50));
        timeAndSequenceBitsCountList.add(new Pair(50, 0));
        timeAndSequenceBitsCountList.add(new Pair(0, 0));
        timeAndSequenceBitsCountList.add(new Pair(1, 0));
        timeAndSequenceBitsCountList.add(new Pair(1, 1));
        timeAndSequenceBitsCountList.add(new Pair(300, 10));
        timeAndSequenceBitsCountList.add(new Pair(50, 300));
        timeAndSequenceBitsCountList.add(new Pair(300, 300));

        testUid("0000000000010", timeAndSequenceBitsCountList);
        testUid("0", timeAndSequenceBitsCountList);
        testUid("1", timeAndSequenceBitsCountList);
        testUid("0000000000000", timeAndSequenceBitsCountList);
        testUid("1111111111111", timeAndSequenceBitsCountList);
        testUid("1111111111111000000000000011111111111110000000000000", timeAndSequenceBitsCountList);
        testUid("1010101010101010101010101010101010", timeAndSequenceBitsCountList);
        testUid("0101011111111111110000000000000111111111111100000000000000101010", timeAndSequenceBitsCountList); //64bits max
    }

    @Test(expected = NegativeBitsCountException.class)
    public void testNegativeBitsCount() throws Exception {
        testUid("0000000000010",
                new LinkedList<Pair<Integer, Integer>>() {{add(new Pair<Integer, Integer>(-1, 1));}});
    }

    @Test(expected = ZeroBitsExceptions.class)
    public void testZeroBitsCount() throws Exception {
        testUid("", new LinkedList<Pair<Integer, Integer>>() {{add(new Pair<Integer, Integer>(0, 0));}});
    }
}
