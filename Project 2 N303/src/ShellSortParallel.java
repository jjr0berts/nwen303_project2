import java.util.Random;

import mpi.MPI;
import mpi.MPIException;


public class ShellSortParallel {

	/*
	 * These values are used for testing on the grid
	 */
	private static int max = 16384;
	private static int min = 1;
	private static int size = max;


	public static void main(String[] args, int[] nums) throws MPIException {
//		public static void main(String[] args) throws MPIException {
//			int[] nums = makeList();

		//MPI.Init(args) ;

        int myrank = MPI.COMM_WORLD.getRank() ;
        int size = MPI.COMM_WORLD.getSize() ;

        int partition = nums.length/size;


        if (0 == myrank) {
        	partition = nums.length/size;
        	System.out.println("Num Processes: " + size);
        }

    	long startTime = System.currentTimeMillis();
        int[] newarray = new int[partition];

        MPI.COMM_WORLD.scatter(nums, partition, MPI.INT,newarray, partition,MPI.INT,0);
//      ShellSort s = new ShellSort();
//      s.sort(newarray);
        sort(newarray);
        MPI.COMM_WORLD.gather(nums, 0, MPI.INT,newarray,0,MPI.INT,0);

        if (myrank == 0) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("Sorted Array..");
			//printnums(newarray);
			System.out.println("Shell Sort (Parallel) Time: " + elapsedTime + "ms\n");
        }

        MPI.Finalize();

        //Main.printnums(newarray);

	}

	public static <T extends Comparable<? super T>> void  sort(int[] nums) {
		//loop over gaps, decreasing size
		for(int gap = nums.length/2; gap > 0; gap /=2){
			//insertion sort for gap size
			for(int i = gap; i < nums.length; i++){
				int temp = nums[i];
				int j;
				for(j = i; j >= gap && larger(nums[j-gap],temp); j -= gap){
					nums[j] = nums[j-gap];
				}
				nums[j] = temp;
			}
		}
	}

	private static boolean larger(int a, int b){
		if(a > b)return true;
		return false;
	}

	/*
	 * The following are used for testing parallel on grid
	 */

	//makes array of random nums of specified size "size"
		public static int[] makeList(){
			int [] nums = new int[size];
			Random ran = new Random();
			for(int i = 0; i < nums.length; i++){
				//add random number from given range into array
				nums[i] = ran.nextInt((max - min) + 1) + min;
			}
			return nums;
		}


		//prints out array
		public static void printnums(int[] nums){
			for(int i = 0; i < 1000; i++){
//				if(i < nums.length-1){
//					if(nums[i] > nums[i+1]){
//						System.out.println("Array Unsorted!");
//						break;
//					}
//				}
				System.out.print(nums[i] + ",");
			}
			System.out.println();
		}

}
