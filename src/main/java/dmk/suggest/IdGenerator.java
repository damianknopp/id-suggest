package dmk.suggest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Generate a bunch of UUIDs for test data.
 *
 */
public class IdGenerator {
	protected File base = null;
	protected int numIds = -1;
	protected int poolSize = -1;
	protected final ExecutorService pool;

	public final static void main(final String args[]) throws Exception {
		IdGenerator idGen = new IdGenerator(5000000);
		idGen.go();
	}

	public IdGenerator(final int numIds) {
		this.numIds = numIds;
		this.base = new File(Constants.baseDir);
		if (!base.exists()) {
			System.out.println("creating base dir " + base.getName() + "...");
			base.mkdirs();
		}
		this.poolSize = 5;
//		this.pool = Executors.newFixedThreadPool(poolSize);
//		final long startTime = System.currentTimeMillis();
		pool = new ThreadPoolExecutor(5, 5, 120,
				TimeUnit.SECONDS, new LinkedBlockingQueue());
	}

	public void go() throws IOException, Exception {
		int workLoad = this.numIds / this.poolSize;
		final long startTime = System.currentTimeMillis();
		for(int i = 0; i < poolSize; i++){
			final File f = File.createTempFile("uuids", ".txt", base);
			System.out.println("starting thread, writing " + workLoad + " ids to " + f.getName());
			final IdWriter writer = new IdWriter(f, workLoad);
			this.pool.execute(writer);
		}
		this.pool.shutdown();
		this.pool.awaitTermination(90, TimeUnit.SECONDS);
		final long endTime = System.currentTimeMillis();
		System.out.println("total time took " + (endTime - startTime) + " millis");
	}

	private class IdWriter implements Runnable {
		protected final File f1;
		protected final int workLoad;
		public IdWriter(final File f, final int numIds) {
			this.f1 = f;
//			if (this.f1.exists()) {
//				throw new RuntimeException(f.getName()
//						+ " already exists I cannot use this file!");
//			}
			this.workLoad = numIds;
		}

		public void run() {
			FileWriter fw = null;
			BufferedWriter bw = null;

			try {
				fw = new FileWriter(this.f1);
				bw = new BufferedWriter(fw);
				int i = 0;
				while(i < this.workLoad){
					bw.write(UUID.randomUUID().toString());
					bw.write('\n');
					i++;
					if(i % 100000 == 0){
						System.out.println(Thread.currentThread().getName() + " wrote " + i + " ids.");
					}
				}
				bw.flush();
				
				System.out.println(Thread.currentThread().getName() + " done writing..");
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fw != null) {
						fw.close();
					}
					if (bw != null) {
						bw.close();
					}
				} catch (Exception e) {
				}
			}
		}
	}
}
