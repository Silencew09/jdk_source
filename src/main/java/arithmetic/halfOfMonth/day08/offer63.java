package arithmetic.halfOfMonth.day08;

/**
 * @author Silence_w
 */
public class offer63 {
    public int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < min){
                min = prices[i];
            }else if(prices[i]-min>maxProfit){
                maxProfit = prices[i]-min;
            }
        }
        return maxProfit;
    }
}
