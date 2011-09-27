package ASP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import centurio.GamePlayer;

final class MemoryCheckThread extends Thread {

	private final ASPOracle oracle;

	MemoryCheckThread(final ASPOracle oracle) {
		this.oracle = oracle;
	}

	public final void run() {
		final ProcessBuilder processBuilder = new ProcessBuilder("./freemem.sh");
		final int initialFreeMemory;

		try {
			final BufferedReader stdInput = new BufferedReader(new InputStreamReader(processBuilder.start().getInputStream()));

			initialFreeMemory = Integer.parseInt(stdInput.readLine());
			stdInput.close();
			while (oracle.isAlive()) {
				final BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(processBuilder.start().getInputStream()));

				if (initialFreeMemory - Integer.parseInt(stdInput2.readLine()) > GamePlayer.getConfigMap("ASPMemoryInKB").intValue()) { // kilobyte
					oracle.destroyASP();
				}
				stdInput2.close();
				try {
					sleep(1000);
				} catch (final InterruptedException e) {
					interrupt();
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
