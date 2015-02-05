package kelp.guessnumber;

public class GuessManager {
	private int number_size;
	private boolean isMatched;
	private int[] answer;
	private String verify_response, guess_response, ans;
	private int times, count;
	
	public GuessManager(int size, int times) {
		this.number_size = size;
		this.times = times;
		generateNumber();
	}
	// generate answer
	private void generateNumber() {
		answer = new int[this.number_size];
		int[] random = new int[10];
		// generate ten numbers, from 0 to 9
		for(int index = 0; index < random.length; index++) {
			random[index] = index;
		}
		//  shuffle
		for(int index = 0; index < random.length; index++) {
			int change = (int) (Math.random()*10);
			if (change == index) continue;
			random[index] = random[index] + random[change];
			random[change] = random[index] - random[change];
			random[index] = random[index] - random[change];
		}
		//  get answer
		for(int index = 0; index < this.number_size; index++) {
			answer[index] = random[index];
		}
		ans = "";
		for (int n : answer) {
			ans += n;
		}
	}
	// start to guess and return result
	public String guessNumber(String guess_number) {
		int A = 0; int B = 0;
		int[] guess = new int[number_size];
		//  convert string to number
		for (int index = 0; index < guess_number.length(); index++) {
			guess[index] = Integer.parseInt(guess_number.substring(index, index+1));
		}
		//  compare
		for (int x = 0; x < number_size; x++) {
			for (int y = 0; y < number_size; y++) {
				if (guess[x] == answer[y]) {
					if (x == y) A++;
					else B++;
				}
			}
		}
		count++;
		//  determine the guessing result
		if (A == number_size) {
			isMatched = true;
			guess_response = "正確答案!!!";
		} else if (times == count) {
			isMatched = true;
			guess_response = "用光了猜測次數.正確答案是" + ans;
		} else {
			guess_response = A + "A" + B + "B";
		}
		
		return guess_response;
	}
	// verify the given number
	public boolean verify(String guess_number) {
		boolean isCorrect = false;
		
		if (guess_number.equals("") || guess_number.equals(" ")) {    // nothing or space be given
			verify_response = "請輸入數字";
		} else if (guess_number.length() != this.number_size) {         //  incorrect size
			verify_response = "請輸入正確字數";
		} else {
			isCorrect = true;
		}
		return isCorrect;
	}
	// return the verify result for shown in toast
	public String getVerifyResponse() {
		return this.verify_response;
	}
	// return isMatehed
	public boolean isMatched() {
		return this.isMatched;
	}
	//  return the remainder number of times
	public int getRemanderTimes() {
		return times - count;
	}
}
