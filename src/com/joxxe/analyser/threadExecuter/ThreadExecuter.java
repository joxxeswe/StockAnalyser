package com.joxxe.analyser.threadExecuter;

import java.util.ArrayList;


public class ThreadExecuter implements ThreadCompleteListener {
	public ArrayList<Thread> threads;
	
	public ThreadExecuter(){
		threads = new ArrayList<>();
	}

	public void exit() {
		for (Thread t : threads) {
			t.interrupt();
		}
	}
	
	public void execute(NotifyingThread t){
		threads.add(t);
		t.addListener(this);
		t.start();
	}

	@Override
	public void notifyOfThreadComplete(Thread t) {
		threads.remove(t);
	}
	
}
