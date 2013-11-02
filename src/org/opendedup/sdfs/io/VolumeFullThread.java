package org.opendedup.sdfs.io;

import org.opendedup.logging.SDFSLogger;

public class VolumeFullThread implements Runnable {
	private final Volume vol;
	private Thread th = null;
	private long duration = 15 * 1000;
	boolean closed = false;

	public VolumeFullThread(Volume vol) {
		this.vol = vol;
		th = new Thread(this);
		th.start();
	}

	@Override
	public void run() {
		while (!closed) {

			try {
				Thread.sleep(duration);
				vol.volumeFull = this.isFull();
			} catch (Exception e) {
				SDFSLogger.getLog().debug("Unable to check if full.", e);
				this.closed = true;
			}
		}

	}
	
	public synchronized boolean isFull() throws Exception {
		long avail = vol.pathF.getUsableSpace();
		if(avail < Volume.minFree) {
			SDFSLogger.getLog().warn("Drive is almost full space left is [" + avail + "]");
			return true;
			
		}
		if (vol.fullPercentage < 0 || vol.currentSize == 0)
			return false;
		else {
			return (vol.currentSize > vol.absoluteLength);
		}
	}

	public void stop() {
		th.interrupt();
		this.closed = true;
	}

}